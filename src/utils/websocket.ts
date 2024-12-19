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

export function getConnectedClients(): WebSocket[] {
    return connectedClients;
}

export function broadcastMessage(message: string): number {
    let sentCount = 0;
    connectedClients.forEach(client => {
        if (client.readyState === WebSocket.OPEN) {
            client.send(message);
            sentCount++;
        }
    });
    return sentCount;
} 