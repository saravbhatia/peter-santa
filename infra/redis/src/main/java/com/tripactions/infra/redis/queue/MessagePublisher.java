package com.tripactions.infra.redis.queue;

public interface MessagePublisher {
	void publish(final String message);
}
