import { Twilio } from "twilio";

const {
  TWILIO_ACCOUNT_SID,
  TWILIO_AUTH_TOKEN,
  PHONE_NUMBER_FROM,
  DOMAIN: rawDomain,
} = process.env;

const DOMAIN = rawDomain?.replace(/(^\w+:|^)\/\//, '').replace(/\/+$/, '') ?? ''; // Clean protocols and slashes
const outboundTwiML = `<?xml version="1.0" encoding="UTF-8"?><Response><Connect><Stream url="wss://${DOMAIN}/api/call-stream"><Parameter name="mode" value="duplex" /></Stream></Connect></Response>`;

async function isNumberAllowed(client: Twilio, to: string) {
    // Allow all phone numbers
    return true;
}

async function makeCall(client: Twilio, to: string) {
    try {
        const call = await client.calls.create({
            from: PHONE_NUMBER_FROM!,
            to,
            twiml: outboundTwiML,
            statusCallback: `https://${DOMAIN}/api/call-status`,
            statusCallbackEvent: ['initiated', 'ringing', 'answered', 'completed'],
            statusCallbackMethod: 'POST',
        });
        
        console.log(`Call started with SID: ${call.sid}`);
        return call;
    } catch (error) {
        console.error("Error making call:", error);
        throw error;
    }
}

export async function POST(request: Request) {
    if (!TWILIO_ACCOUNT_SID || !TWILIO_AUTH_TOKEN || !PHONE_NUMBER_FROM || !rawDomain) {
        return Response.json({ error: "Server is not configured" }, { status: 500 });
    }

    try {
        const client = new Twilio(TWILIO_ACCOUNT_SID, TWILIO_AUTH_TOKEN);
        const { to } = await request.json();
        
        if (!to) {
            return Response.json({ error: "Phone number is required" }, { status: 400 });
        }

        const call = await makeCall(client, to);
        return Response.json({ message: "Call started", callSid: call.sid });
    } catch (error: any) {
        console.error("Error in POST handler:", error);
        return Response.json({ 
            error: error.message || "Failed to start call" 
        }, { 
            status: error.status || 500 
        });
    }
}
