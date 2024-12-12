package com.tripactions.infra.kafka.producer;

import com.tripactions.avro.message.transactiondata.TransactionProcessedEvent;
import com.tripactions.infra.kafka.config.KafkaConfiguration;
import com.tripactions.kafka.avro.autoconfigure.KafkaWriter;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.generic.IndexedRecord;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@Slf4j
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.NONE,
        classes = {KafkaConfiguration.class})
@EnableAutoConfiguration
@ActiveProfiles("test")
@TestPropertySource(properties = {
        "kafka.enabled=true",
        "kafka.groupId=ta-call-bot"
})
@Disabled // enable for local use
/**
 * This tool is used to send messages to kafka topics.
 * It is useful for testing kafka consumers.
 * To use this tool, you need to:
 * 1. Uncomment the @Disabled annotation
 * 2. Write a test that sends the event you need to the topic you need
 * 3. Run Kafka locally, listening to port 9092
 */
class KafkaMessageSenderTool {
    @Autowired
    private KafkaWriter<String, IndexedRecord> kafkaWriter;

    @Test
    // This is just an example
    void sendTransactionProcessedEvent() {
        TransactionProcessedEvent transactionProcessedEvent = new TransactionProcessedEvent();
        transactionProcessedEvent.setBookingUuid("test");
        kafkaWriter.write(transactionProcessedEvent);
    }
}
