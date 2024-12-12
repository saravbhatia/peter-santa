package com.tripactions.infra.kafka.producer;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.apache.avro.generic.IndexedRecord;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import com.tripactions.avro.message.transactiondata.TransactionProcessedEvent;
import com.tripactions.infra.kafka.Utils;
import com.tripactions.kafka.avro.autoconfigure.KafkaWriter;

@Service
@Slf4j
@RequiredArgsConstructor
@ConditionalOnProperty(value = "kafka.enabled", havingValue = "true", matchIfMissing = true)
public class SampleKafkaProducer {
	private final KafkaWriter<String, IndexedRecord> kafkaWriter;
	int number = 0;
	@EventListener
	public void handleTransactionProcessedEvent(@NonNull TransactionProcessedEvent event) {
		String uuId = event.getBookingUuid();
		log.info("Started: Sending uuid {} to kafka.", uuId);
		try {
			number = Utils.generateNumber(uuId);
			kafkaWriter.write(event);
			log.info("Completed: Sending uuid {} to kafka", uuId);
		} catch (Exception e) {
			log.error("Failed: Sending uuid {} to kafka", uuId);
			throw e;
		}
	}
}
