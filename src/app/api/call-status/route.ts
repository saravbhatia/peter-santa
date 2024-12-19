import { WebSocket } from 'ws';
import { addClient, broadcastMessage } from '@/utils/websocket';

// WebSocket handler
export async function SOCKET(client: WebSocket) {
    console.log('Status WebSocket connection opened');
    addClient(client);

    client.on('close', () => {
        console.log('Status WebSocket connection closed');
    });
}

// HTTP POST handler for Twilio callbacks
export async function POST(request: Request) {
    const data = await request.formData();
    const callStatus = data.get('CallStatus');
    const callSid = data.get('CallSid');

    console.log(`Received call status update - SID: ${callSid}, Status: ${callStatus}`);

    // Broadcast status to all connected clients
    const message = JSON.stringify({
        type: 'callStatus',
        callSid,
        status: callStatus
    });

    const sentCount = broadcastMessage(message);
    console.log(`Broadcasted status to ${sentCount} clients`);
    return new Response('OK');
}

// Add HTTP GET handler to satisfy Next.js
export async function GET() {
    return new Response('WebSocket endpoint', { status: 200 });
} 