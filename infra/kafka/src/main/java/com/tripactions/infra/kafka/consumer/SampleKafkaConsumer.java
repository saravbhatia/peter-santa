package com.tripactions.infra.kafka.consumer;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.tripactions.avro.message.transactiondata.TransactionProcessedEvent;
import com.tripactions.infra.kafka.Utils;

@Service
@Slf4j
@RequiredArgsConstructor
@ConditionalOnProperty(value = "kafka.enabled", havingValue = "true", matchIfMissing = true)
public class SampleKafkaConsumer {
	int number = 0;
	@KafkaListener(topics = "transaction_processed_event")
	public void onTransactionProcessedEvent(@NonNull TransactionProcessedEvent transactionProcessedEvent) {
		String uuId = transactionProcessedEvent.getBookingUuid();
		log.info("Started: Processing uuid {} from kafka", uuId);
		try {
			log.info("Do something useful here.");
			number = Utils.generateNumber(uuId);
			log.info("Completed: Processing uuid {} from kafka", uuId);
		} catch (Exception e) {
			log.error("Failed: Processing uuid {} from kafka", uuId, e);
			throw e;
		}
	}


}