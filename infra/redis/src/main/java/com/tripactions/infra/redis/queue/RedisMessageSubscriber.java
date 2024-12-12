package com.tripactions.infra.redis.queue;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Getter
public class RedisMessageSubscriber implements MessageListener {

	protected static List<String> messageList = new ArrayList<>();

	public void onMessage(final Message message, final byte[] pattern) {
		messageList.add(message.toString());
		log.info("Message received: " + new String(message.getBody()));
	}
}
