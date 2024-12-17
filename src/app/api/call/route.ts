import { Twilio } from "twilio";

const {
  TWILIO_ACCOUNT_SID,
  TWILIO_AUTH_TOKEN,
  PHONE_NUMBER_FROM,
  DOMAIN: rawDomain,
} = process.env;

const DOMAIN = rawDomain?.replace(/(^\w+:|^)\/\//, '').replace(/\/+$/, '') ?? ''; // Clean protocols and slashes
const outboundTwiML = `<?xml version="1.0" encoding="UTF-8"?><Response><Connect><Stream url="wss://${DOMAIN}/api/call-stream" /></Connect></Response>`;

async function isNumberAllowed(client: Twilio, to: string) {
    try {
        // Uncomment these lines to test numbers. Only add numbers you have permission to call
        const consentMap = {
            "+14088180452": true,
        } as Record<string, boolean>;
        if (consentMap[to]) return true;

        // Check if the number is a Twilio phone number in the account, for example, when making a call to the Twilio Dev Phone
        const incomingNumbers = await client.incomingPhoneNumbers.list({
            phoneNumber: to,
        });
        if (incomingNumbers.length > 0) {
            return true;
        }

        // Check if the number is a verified outgoing caller ID. https://www.twilio.com/docs/voice/api/outgoing-caller-ids
        const outgoingCallerIds = await client.outgoingCallerIds.list({
            phoneNumber: to,
        });
        if (outgoingCallerIds.length > 0) {
            return true;
        }

        return false;
    } catch (error) {
        console.error("Error checking phone number:", error);
        return false;
    }
}

async function makeCall(client: Twilio, to: string) {
    try {
        const isAllowed = await isNumberAllowed(client, to);
        if (!isAllowed) {
            console.warn(
                `The number ${to} is not recognized as a valid outgoing number or caller ID.`
            );
            process.exit(1);
        }

        const call = await client.calls.create({
            from: PHONE_NUMBER_FROM!,
            to,
            twiml: outboundTwiML,
        });
        console.log(`Call started with SID: ${call.sid}`);
    } catch (error) {
        console.error("Error making call:", error);
    }
}

export async function POST(request: Request) {
    if (!TWILIO_ACCOUNT_SID || !TWILIO_AUTH_TOKEN || !PHONE_NUMBER_FROM || !rawDomain) {
        return Response.json({ error: "Server is not configured" }, { status: 500 });
    }
    const client = new Twilio(TWILIO_ACCOUNT_SID, TWILIO_AUTH_TOKEN);
    const { to } = await request.json();
    await makeCall(client, to);
    return Response.json({ message: "Call started" });
}
