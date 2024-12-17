import { WebSocket } from "ws";
import fs from "fs";

const OPENAI_API_KEY = process.env.OPENAI_API_KEY;
const VOICE = "alloy";

const SYSTEM_MESSAGE = fs
    .readFileSync("./src/prompts/system-prompt.txt", "utf8")
    .replace(
        "${booking_info}",
        JSON.stringify({
            name: "Huy Tran",
            phone_number: "+14088180452",
            email: "htran@navan.com",
            check_in_date: "2025-01-01",
            check_out_date: "2025-01-02",
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
];

export async function SOCKET(client: WebSocket) {
    console.log("WebSocket connection opened");
    const openAiWs = new WebSocket(
        "wss://api.openai.com/v1/realtime?model=gpt-4o-realtime-preview-2024-10-01",
        {
            headers: {
                Authorization: `Bearer ${OPENAI_API_KEY}`,
                "OpenAI-Beta": "realtime=v1",
            },
        }
    );

    let streamSid = "";

    const sendInitialSessionUpdate = () => {
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

        openAiWs.send(JSON.stringify(sessionUpdate));
        console.log("Session started\n");

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

        openAiWs.send(JSON.stringify(initialConversationItem));
        openAiWs.send(JSON.stringify({ type: "response.create" }));
    };

    // Open event for OpenAI WebSocket
    openAiWs.on("open", () => {
        console.log("Connected to the OpenAI Realtime API");
        setTimeout(sendInitialSessionUpdate, 100); // Ensure connection stability, send after .1 second
    });

    // Listen for messages from the OpenAI WebSocket (and send to Twilio if necessary)
    openAiWs.on("message", (data) => {
        try {
            const response = JSON.parse(data);

            if (LOG_EVENT_TYPES.includes(response.type)) {
                if (response.type === "response.audio_transcript.done") {
                    console.log("[ASSISTANT]: ", response.transcript);
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
                // ENABLE THIS TO DEBUG
                // console.log(JSON.stringify(response, null, 2));
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

    client.on("open", () => {
        console.log("WebSocket connection opened");
    });

    client.on("message", (message) => {
        try {
            const data = JSON.parse(message);

            switch (data.event) {
                case "media":
                    if (openAiWs.readyState === WebSocket.OPEN) {
                        const audioAppend = {
                            type: "input_audio_buffer.append",
                            audio: data.media.payload,
                        };
                        openAiWs.send(JSON.stringify(audioAppend));
                    }
                    break;
                case "start":
                    streamSid = data.start.streamSid;
                    console.log("Incoming stream has started", streamSid);
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
        if (openAiWs.readyState === WebSocket.OPEN) {
            openAiWs.close();
        }
        console.log("WebSocket connection closed");
    });

    // Handle WebSocket close and errors
    openAiWs.on("close", () => {
        console.log("Disconnected from the OpenAI Realtime API");
    });

    openAiWs.on("error", (error) => {
        console.error("Error in the OpenAI WebSocket:", error);
    });
}
