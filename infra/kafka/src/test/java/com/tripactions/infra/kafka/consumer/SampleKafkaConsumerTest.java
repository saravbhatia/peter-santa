package com.tripactions.infra.kafka.consumer;

import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.tripactions.avro.message.transactiondata.TransactionProcessedEvent;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class SampleKafkaConsumerTest {
	private final String uuId = UUID.randomUUID().toString();
	private final SampleKafkaConsumer sampleKafkaConsumer = new SampleKafkaConsumer();
	@Test
	public void null_check_for_onTransactionProcessedEvent() {
		assertThrows(NullPointerException.class, () -> sampleKafkaConsumer.onTransactionProcessedEvent(null));
	}

	@Test
	public void onTransactionProcessedEvent() {
		TransactionProcessedEvent transactionProcessedEvent = new TransactionProcessedEvent();
		transactionProcessedEvent.setBookingUuid(uuId);
		sampleKafkaConsumer.onTransactionProcessedEvent(transactionProcessedEvent);
		assertEquals(sampleKafkaConsumer.number, uuId.length()/uuId.substring(3).length());
	}

	@Test
	void handle_exception_thrown_during_sync() {
		TransactionProcessedEvent transactionProcessedEvent = new TransactionProcessedEvent();
		transactionProcessedEvent.setBookingUuid("");
		assertThrows(StringIndexOutOfBoundsException.class, () -> {
			sampleKafkaConsumer.onTransactionProcessedEvent(transactionProcessedEvent);
		});
	}
}
