package com.tripactions.infra.kafka.producer;

import java.util.UUID;

import org.apache.avro.generic.IndexedRecord;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;

import com.tripactions.avro.message.transactiondata.TransactionProcessedEvent;
import com.tripactions.kafka.avro.autoconfigure.KafkaWriter;
import com.tripactions.kafka.avro.autoconfigure.KafkaWriterImpl;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class SampleKafkaProducerTest {
	private final String uuId = UUID.randomUUID().toString();
	@Autowired
	KafkaProducer<String, IndexedRecord> kafkaProducers;

	KafkaWriter<String, IndexedRecord> kafkaWriter = new KafkaWriterImpl<>(kafkaProducers, "");;
	private final SampleKafkaProducer sampleKafkaProducer = new SampleKafkaProducer(kafkaWriter);
	@Test
	public void null_check_for_handleTransactionProcessedEventEvent() {
		assertThrows(NullPointerException.class, () -> sampleKafkaProducer.handleTransactionProcessedEvent(null));
	}

//	@Test
//	public void handleTransactionProcessedEvent() {
//		TransactionProcessedEvent transactionProcessedEvent = new TransactionProcessedEvent();
//		transactionProcessedEvent.setBookingUuid(uuId);
//		sampleKafkaProducer.handleTransactionProcessedEvent(transactionProcessedEvent);
//		verify(kafkaWriter, times(1)).write(transactionProcessedEvent);
//	}

	@Test
	void handle_exception_thrown_during_sync() {
		TransactionProcessedEvent transactionProcessedEvent = new TransactionProcessedEvent();
		transactionProcessedEvent.setBookingUuid("");
		assertThrows(StringIndexOutOfBoundsException.class, () -> {
			sampleKafkaProducer.handleTransactionProcessedEvent(transactionProcessedEvent);
		});
	}
}
