import { WebSocket } from 'ws';

let connectedClients: WebSocket[] = [];

export function addClient(ws: WebSocket) {
    connectedClients.push(ws);
    console.log(`New client connected. Total clients: ${connectedClients.length}`);
    
    ws.on('close', () => {
        connectedClients = connectedClients.filter(client => client !== ws);
        console.log(`Client disconnected. Remaining clients: ${connectedClients.length}`);
    });
}

// WebSocket handler
export async function SOCKET(client: WebSocket) {
    console.log('Status WebSocket connection opened');
    addClient(client);

    client.on('close', () => {
        console.log('Status WebSocket connection closed');
        connectedClients = connectedClients.filter(c => c !== client);
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

    let sentCount = 0;
    connectedClients.forEach(client => {
        if (client.readyState === WebSocket.OPEN) {
            client.send(message);
            sentCount++;
        }
    });

    console.log(`Broadcasted status to ${sentCount} clients`);
    return new Response('OK');
}

// Add HTTP GET handler to satisfy Next.js
export async function GET() {
    return new Response('WebSocket endpoint', { status: 200 });
} 