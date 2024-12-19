import { Twilio } from "twilio";

const {
    TWILIO_ACCOUNT_SID,
    TWILIO_AUTH_TOKEN,
} = process.env;

export async function POST(request: Request) {
    if (!TWILIO_ACCOUNT_SID || !TWILIO_AUTH_TOKEN) {
        return Response.json({ error: "Server is not configured" }, { status: 500 });
    }

    try {
        const client = new Twilio(TWILIO_ACCOUNT_SID, TWILIO_AUTH_TOKEN);
        const { callSid } = await request.json();
        
        if (!callSid) {
            return Response.json({ error: "Call SID is required" }, { status: 400 });
        }

        // End the call by updating its status to 'completed'
        await client.calls(callSid)
            .update({ status: 'completed' });

        return Response.json({ message: "Call ended successfully" });
    } catch (error: any) {
        console.error("Error ending call:", error);
        return Response.json({ 
            error: error.message || "Failed to end call" 
        }, { 
            status: error.status || 500 
        });
    }
} 