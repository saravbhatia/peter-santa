package com.tripactions.infra.redis.queue;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class RedisMessageListenerTest {

	private MessagePublisher redisMessagePublisher;
	private MessageListener redisMessageSubscriber;
	@Mock
	private RedisTemplate redisTemplate;
	@Mock
	private ChannelTopic channelTopic;

	@Test
	void testOnMessage() throws Exception {
		redisMessagePublisher = new RedisMessagePublisher(redisTemplate, channelTopic);
		redisMessageSubscriber = new RedisMessageSubscriber();
		String message = "Message " + UUID.randomUUID();
		doNothing().when(redisTemplate).convertAndSend(null, message);
		redisMessagePublisher.publish(message);
		Thread.sleep(1000);
		verify(redisTemplate, times(1)).convertAndSend(null, message);

		Message m = new Message() {
			@Override
			public byte[] getBody() {
				return message.getBytes();
			}

			@Override
			public byte[] getChannel() {
				return new byte[0];
			}
		};
		redisMessageSubscriber.onMessage(m, null);
		assertEquals(m.toString(), RedisMessageSubscriber.messageList.getFirst());
	}
}