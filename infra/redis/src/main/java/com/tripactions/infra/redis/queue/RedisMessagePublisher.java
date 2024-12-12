package com.tripactions.infra.redis.queue;

import lombok.AllArgsConstructor;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class RedisMessagePublisher implements MessagePublisher {

	private RedisTemplate<String, Object> redisTemplate;
	private ChannelTopic topic;

	public void publish(final String message) {
		redisTemplate.convertAndSend(topic.getTopic(), message);
	}
}
