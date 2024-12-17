# ta-call-bot

## Requirement

- NodeJS
- Ngrok (for exposing development port)
- Twilio development credentials
- OpenAI API key

## Setup

1. Clone the repo, create an `.env.development` file with the template from `example.env` file
2. Install all dependencies
   ```
   npm install
   ```
3. Install WebSocket support for Next.js
   ```
   npx next-ws-cli@latest patch
   ```

   Note: You'll have to repeat this step everytime you do `npm install`.
4. Run `ngrok` to expose your development port over internet:
   ```
   ngrok http 3000
   ```
   After this, copy the public URL and put it to the `DOMAIN` environment variable.
5. Run the app:
   ```
   npm run dev
   ```