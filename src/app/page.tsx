'use client';

import { useState, useEffect } from 'react';

export default function Home() {
    const [phoneNumber, setPhoneNumber] = useState('');
    const [isLoading, setIsLoading] = useState(false);
    const [error, setError] = useState('');
    const [callStatus, setCallStatus] = useState<'idle' | 'calling' | 'inProgress'>('idle');
    const [currentCallSid, setCurrentCallSid] = useState<string | null>(null);
    const [wsConnected, setWsConnected] = useState(false);

    useEffect(() => {
        let websocket: WebSocket | null = null;
        let reconnectTimeout: NodeJS.Timeout | null = null;

        const connectWebSocket = () => {
            if (websocket?.readyState === WebSocket.OPEN) {
                console.log('WebSocket already connected');
                return;
            }

            try {
                const protocol = window.location.protocol === 'https:' ? 'wss:' : 'ws:';
                const wsUrl = `${protocol}//${window.location.host}/api/call-status`;
                console.log('Connecting to WebSocket:', wsUrl);
                
                websocket = new WebSocket(wsUrl);

                websocket.onopen = () => {
                    console.log('WebSocket connected successfully');
                    setWsConnected(true);
                };

                websocket.onmessage = (event) => {
                    try {
                        const data = JSON.parse(event.data);
                        console.log('Received WebSocket message:', data);
                        
                        if (data.type === 'callStatus' && data.callSid === currentCallSid) {
                            console.log('Processing call status:', data.status);
                            switch (data.status) {
                                case 'initiated':
                                case 'ringing':
                                    setCallStatus('calling');
                                    break;
                                case 'in-progress':
                                    setCallStatus('inProgress');
                                    break;
                                case 'completed':
                                case 'failed':
                                case 'busy':
                                case 'no-answer':
                                case 'canceled':
                                    console.log('Call ended, resetting state');
                                    setCallStatus('idle');
                                    setCurrentCallSid(null);
                                    break;
                            }
                        }
                    } catch (error) {
                        console.error('Error processing WebSocket message:', error);
                    }
                };

                websocket.onclose = (event) => {
                    console.log('WebSocket disconnected:', event);
                    setWsConnected(false);
                    // Try to reconnect in 5 seconds if we still have an active call
                    if (currentCallSid && !event.wasClean) {
                        console.log('Scheduling reconnection...');
                        reconnectTimeout = setTimeout(connectWebSocket, 5000);
                    }
                };

                websocket.onerror = (error) => {
                    console.error('WebSocket error:', error);
                    setWsConnected(false);
                };
            } catch (error) {
                console.error('Error creating WebSocket:', error);
                setWsConnected(false);
            }
        };

        connectWebSocket();

        return () => {
            if (reconnectTimeout) {
                clearTimeout(reconnectTimeout);
            }
            if (websocket) {
                websocket.close();
            }
        };
    }, [currentCallSid]);

    const validatePhoneNumber = (number: string) => {
        // Basic phone number validation - can be enhanced based on requirements
        const phoneRegex = /^\+?[1-9]\d{1,14}$/;
        return phoneRegex.test(number);
    };

    const onCallHandler = async () => {
        if (!validatePhoneNumber(phoneNumber)) {
            setError('Please enter a valid phone number (e.g., +14088180452)');
            return;
        }

        if (!wsConnected) {
            setError('WebSocket not connected. Please try again in a moment.');
            return;
        }

        setError('');
        setIsLoading(true);
        setCallStatus('calling');
        
        try {
            const response = await fetch("/api/call", {
                method: "POST",
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({ to: phoneNumber }),
            });

            if (!response.ok) {
                throw new Error('Failed to initiate call');
            }

            const data = await response.json();
            console.log('Call initiated with SID:', data.callSid);
            setCurrentCallSid(data.callSid);
        } catch (err) {
            console.error('Error starting call:', err);
            setError('Failed to start the call. Please try again.');
            setCallStatus('idle');
        } finally {
            setIsLoading(false);
        }
    };

    const endCallHandler = async () => {
        if (!currentCallSid) return;

        if (!wsConnected) {
            setError('WebSocket not connected. Call may not end properly.');
        }

        try {
            console.log('Ending call with SID:', currentCallSid);
            const response = await fetch("/api/call/end", {
                method: "POST",
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({ callSid: currentCallSid }),
            });

            if (!response.ok) {
                throw new Error('Failed to end call');
            }

            // Set a timeout to force reset the state if we don't receive a callback
            setTimeout(() => {
                if (callStatus !== 'idle') {
                    console.log('Forcing state reset after timeout');
                    setCallStatus('idle');
                    setCurrentCallSid(null);
                }
            }, 5000);

            console.log('End call request sent successfully');
        } catch (err) {
            console.error('Error ending call:', err);
            setError('Failed to end the call. Please try again.');
            // Force reset state on error
            setCallStatus('idle');
            setCurrentCallSid(null);
        }
    };

    return (
        <div 
            className="min-h-screen bg-cover bg-center bg-no-repeat"
            style={{ backgroundImage: 'url("/santa_elf_background.png")' }}
        >
            <div className="min-h-screen bg-black/30 backdrop-blur-sm">
                <div className="grid grid-rows-[20px_1fr_20px] items-center justify-items-center min-h-screen p-8 pb-20 gap-16 sm:p-20">
                    <main className="flex flex-col gap-8 row-start-2 items-center w-full max-w-md">
                        <div className="w-full space-y-4 bg-white/90 backdrop-blur-md rounded-xl p-8 shadow-xl">
                            <h1 className="text-2xl font-bold text-center mb-8 text-gray-800">🧝‍♂️ Peter Santa's AI Elf 🧝</h1>
                            
                            {!wsConnected && (
                                <div className="text-center py-2 px-4 bg-yellow-100 text-yellow-800 rounded-md">
                                    Connecting to server...
                                </div>
                            )}
                            
                            <div className="space-y-2">
                                <label htmlFor="phoneNumber" className="block text-sm font-medium text-gray-700">
                                    Enter Phone Number to confirm Santa's visit
                                </label>
                                <input
                                    id="phoneNumber"
                                    type="tel"
                                    placeholder="+14088180452"
                                    value={phoneNumber}
                                    onChange={(e) => {
                                        setPhoneNumber(e.target.value);
                                        setError('');
                                    }}
                                    disabled={callStatus !== 'idle'}
                                    className="w-full px-4 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 disabled:bg-gray-100 disabled:cursor-not-allowed bg-white"
                                />
                                {error && (
                                    <p className="text-red-500 text-sm mt-1">{error}</p>
                                )}
                            </div>

                            <div className="space-y-3">
                                {callStatus === 'idle' ? (
                                    <button
                                        className={`w-full bg-blue-500 text-white px-4 py-2 rounded-md hover:bg-blue-600 transition-colors
                                            ${(!phoneNumber || isLoading || !wsConnected) ? 'opacity-50 cursor-not-allowed' : ''}`}
                                        onClick={onCallHandler}
                                        disabled={!phoneNumber || isLoading || !wsConnected}
                                    >
                                        {isLoading ? 'Starting Call...' : 'Start Call'}
                                    </button>
                                ) : (
                                    <>
                                        <div className="text-center py-2 px-4 bg-green-100 text-green-800 rounded-md">
                                            {callStatus === 'calling' ? 'Calling...' : 'Call in Progress'}
                                        </div>
                                        <button
                                            className="w-full bg-red-500 text-white px-4 py-2 rounded-md hover:bg-red-600 transition-colors"
                                            onClick={endCallHandler}
                                        >
                                            End Call
                                        </button>
                                    </>
                                )}
                            </div>
                        </div>
                    </main>
                </div>
            </div>
        </div>
    );
}
