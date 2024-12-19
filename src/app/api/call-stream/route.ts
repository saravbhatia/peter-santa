import { WebSocket } from "ws";
import fs from "fs";
import { addClient } from "@/utils/websocket";

const OPENAI_API_KEY = process.env.OPENAI_API_KEY;
const VOICE = "ash";

const SYSTEM_MESSAGE = fs
    .readFileSync("./src/prompts/system-prompt.txt", "utf8")
    .replace(
        "${booking_info}",
        JSON.stringify({
            name: "Santa Claus",
            phone_number: "+14088180452",
            email: "sclaus@navan.com",
            check_in_date: "2025-12-25",
            check_out_date: "2025-12-26",
        })
    );

const LOG_EVENT_TYPES = [
    "error",
    "response.content.done",
    "rate_limits.updated",
    "response.done",
    "response.audio_transcript.done",
    "input_audio_buffer.committed",
    "input_audio_buffer.speech_stopped",
    "input_audio_buffer.speech_started",
    "conversation.item.created",
    "conversation.item.input_audio_transcription.completed",
    "session.created",
    "mark",
];

// Add HTTP method handlers to satisfy Next.js
export async function GET() {
    return new Response("WebSocket endpoint", { status: 200 });
}

export async function POST() {
    return new Response("WebSocket endpoint", { status: 200 });
}

export async function SOCKET(client: WebSocket) {
    console.log("WebSocket connection opened");
    addClient(client);
    let openAiWs: WebSocket | null = null;
    let streamSid = "";
    let isCallActive = false;
    let reconnectAttempts = 0;
    const MAX_RECONNECT_ATTEMPTS = 3;

    const initializeOpenAIConnection = () => {
        if (openAiWs?.readyState === WebSocket.OPEN) {
            console.log("OpenAI WebSocket already connected");
            return;
        }

        console.log("Initializing OpenAI connection...");
        openAiWs = new WebSocket(
            "wss://api.openai.com/v1/realtime?model=gpt-4o-realtime-preview-2024-10-01",
            {
                headers: {
                    Authorization: `Bearer ${OPENAI_API_KEY}`,
                    "OpenAI-Beta": "realtime=v1",
                },
            }
        );

        // Open event for OpenAI WebSocket
        openAiWs.on("open", () => {
            console.log("Connected to the OpenAI Realtime API");
            reconnectAttempts = 0;
            sendInitialSessionUpdate();
        });

        setupOpenAIEventHandlers();
    };

    const reconnectOpenAI = () => {
        if (reconnectAttempts >= MAX_RECONNECT_ATTEMPTS || !isCallActive) {
            console.log("Max reconnection attempts reached or call is not active");
            return;
        }

        console.log(`Attempting to reconnect (attempt ${reconnectAttempts + 1}/${MAX_RECONNECT_ATTEMPTS})`);
        reconnectAttempts++;
        setTimeout(initializeOpenAIConnection, 1000 * reconnectAttempts);
    };

    const sendInitialSessionUpdate = () => {
        if (!openAiWs || openAiWs.readyState !== WebSocket.OPEN) {
            console.log("OpenAI WebSocket not ready for initial session update");
            return;
        }

        console.log("Sending initial session update...");

        const sessionUpdate = {
            type: "session.update",
            session: {
                turn_detection: { type: "server_vad" },
                input_audio_format: "g711_ulaw",
                output_audio_format: "g711_ulaw",
                input_audio_transcription: {
                    model: "whisper-1",
                },
                voice: VOICE,
                instructions: SYSTEM_MESSAGE,
                modalities: ["text", "audio"],
                temperature: 0.6,
                tools: [
                    {
                        type: "function",
                        name: "save_booking_info",
                        description:
                            "Save the booking information to the database",
                        parameters: {
                            type: "object",
                            properties: {
                                text: { type: "string" },
                            },
                        },
                    },
                ],
            },
        };

        try {
            openAiWs.send(JSON.stringify(sessionUpdate));
            console.log("Session update sent");

            const initialConversationItem = {
                type: "conversation.item.create",
                item: {
                    type: "message",
                    role: "user",
                    content: [
                        {
                            type: "input_text",
                            text: 'Greet the user with "Hello there! I\'m a booking agent from Navan. I would like confirm about the hotel booking for one of my customer. Can you help me with that?"',
                        },
                    ],
                },
            };

            console.log("Sending initial conversation item...");
            openAiWs.send(JSON.stringify(initialConversationItem));
            openAiWs.send(JSON.stringify({ type: "response.create" }));
            console.log("Initial messages sent");
        } catch (error) {
            console.error("Error sending initial session update:", error);
            reconnectOpenAI();
        }
    };

    const setupOpenAIEventHandlers = () => {
        if (!openAiWs) return;

        openAiWs.on("message", (data: Buffer) => {
            try {
                const response = JSON.parse(data.toString());
                console.log("Received from OpenAI:", response.type);

                if (LOG_EVENT_TYPES.includes(response.type)) {
                    if (response.type === "response.audio_transcript.done") {
                        console.log("[ASSISTANT]: ", response.transcript);
                    }
                    if (response.type === "mark") {
                        // Echo back mark events to maintain stream synchronization
                        client.send(JSON.stringify({
                            event: "mark",
                            streamSid: streamSid,
                            mark: response.mark
                        }));
                    }
                    if (
                        response.type ===
                        "conversation.item.input_audio_transcription.completed"
                    ) {
                        console.log(
                            "[USER]: ",
                            response.transcript.replace(/\n/g, " ")
                        );
                    }
                    if (
                        response.type === "conversation.item.created" &&
                        response.item.type === "function_call"
                    ) {
                        console.log(
                            "!!![FUNCTION CALL]: ",
                            JSON.stringify(response.item, null, 2)
                        );
                        const callId = response.item.id;
                        client.send(
                            JSON.stringify({
                                type: "conversation.item.create",
                                item: {
                                    type: "function_call_output",
                                    call_id: callId,
                                    output: "success",
                                },
                            })
                        );
                    }
                }

                if (response.type === "response.audio.delta" && response.delta) {
                    console.log("Sending audio delta to client");
                    const audioDelta = {
                        event: "media",
                        streamSid: streamSid,
                        media: {
                            payload: Buffer.from(response.delta, "base64").toString(
                                "base64"
                            ),
                        },
                    };
                    client.send(JSON.stringify(audioDelta));
                }
            } catch (error) {
                console.error(
                    "Error processing OpenAI message:",
                    error,
                    "Raw message:",
                    data
                );
            }
        });

        openAiWs.on("close", () => {
            console.log("Disconnected from the OpenAI Realtime API");
            if (isCallActive) {
                reconnectOpenAI();
            }
        });

        openAiWs.on("error", (error) => {
            console.error("Error in the OpenAI WebSocket:", error);
            if (isCallActive) {
                reconnectOpenAI();
            }
        });
    };

    client.on("message", (message: Buffer) => {
        console.log("Received message from client:", message.toString());
        try {
            const data = JSON.parse(message.toString());

            switch (data.event) {
                case "connected":
                    console.log("Client connected, initializing OpenAI connection");
                    initializeOpenAIConnection();
                    break;
                case "start":
                    streamSid = data.start.streamSid;
                    isCallActive = true;
                    console.log("Incoming stream started with SID:", streamSid);
                    break;
                case "stop":
                    isCallActive = false;
                    console.log("Call stopped");
                    if (openAiWs?.readyState === WebSocket.OPEN) {
                        openAiWs.close();
                    }
                    break;
                case "media":
                    if (openAiWs?.readyState === WebSocket.OPEN) {
                        const audioAppend = {
                            type: "input_audio_buffer.append",
                            audio: data.media.payload,
                        };
                        openAiWs.send(JSON.stringify(audioAppend));
                    } else {
                        console.log("OpenAI WebSocket not ready for media");
                    }
                    break;
                default:
                    console.log("Received non-media event:", data.event);
                    break;
            }
        } catch (error) {
            console.error("Error parsing message:", error, "Message:", message);
        }
    });

    client.on("close", () => {
        console.log("Client WebSocket connection closed");
        isCallActive = false;
        if (openAiWs?.readyState === WebSocket.OPEN) {
            openAiWs.close();
        }
    });
}
