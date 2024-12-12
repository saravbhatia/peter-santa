package com.tripactions.infra.kafka.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

import org.apache.avro.generic.IndexedRecord;
import org.apache.avro.specific.SpecificRecord;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.LoggingErrorHandler;
import org.springframework.kafka.listener.SeekToCurrentErrorHandler;
import org.springframework.kafka.support.serializer.DeserializationException;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.stereotype.Component;
import org.springframework.util.backoff.FixedBackOff;

import io.confluent.kafka.streams.serdes.avro.SpecificAvroDeserializer;
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerializer;

import com.tripactions.kafka.avro.autoconfigure.KafkaWriter;
import com.tripactions.kafka.avro.autoconfigure.KafkaWriterImpl;

import static io.confluent.kafka.serializers.AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG;

@Configuration
@Slf4j
@EnableKafka
@ConditionalOnProperty(value = "kafka.enabled", havingValue = "true", matchIfMissing = true)
public class KafkaConfiguration {

	@ConfigurationProperties("kafka")
	@Data
	@Component
	public static class KafkaProperties {
		private String schemaRegistry;
		private String bootstrapServers;
		private String groupId;
		private int listenerContainerConcurrency;
		private int maxPollRecords;
	}

	@Bean
	public KafkaProducer<String, IndexedRecord> kafkaProducers(KafkaProperties kafkaProperties) {
		Map<String, Object> props = new HashMap<>();
		props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServers());
		props.put(ProducerConfig.ACKS_CONFIG, "all");
		props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
		props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, SpecificAvroSerializer.class);
		props.put(SCHEMA_REGISTRY_URL_CONFIG, kafkaProperties.getSchemaRegistry());

		log.info("Initializing kafkaProducer with properties: {}", props);
		KafkaProducer<String, IndexedRecord> kafkaProducer = new KafkaProducer<>(props);
		Runtime.getRuntime().addShutdownHook(new Thread(kafkaProducer::close));
		return kafkaProducer;
	}

	@Bean
	public ConcurrentKafkaListenerContainerFactory<byte[], SpecificRecord> kafkaListenerContainerFactory(KafkaProperties kafkaProperties) {
		ConcurrentKafkaListenerContainerFactory<byte[], SpecificRecord> factory = new ConcurrentKafkaListenerContainerFactory<>();
		factory.setConsumerFactory(consumerFactory(kafkaProperties));
		factory.setConcurrency(kafkaProperties.getListenerContainerConcurrency());
		factory.setErrorHandler(createSeekToCurrentErrorHandler());
		return factory;
	}

	@Bean
	public ConsumerFactory<byte[], SpecificRecord> consumerFactory(KafkaProperties kafkaProperties) {
		Map<String, Object> props = new HashMap<>();
		props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServers());
		props.put(ConsumerConfig.GROUP_ID_CONFIG, kafkaProperties.getGroupId());
		props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
		props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, kafkaProperties.getMaxPollRecords());
		props.put(SCHEMA_REGISTRY_URL_CONFIG, kafkaProperties.getSchemaRegistry());
		props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
		props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
		props.put(ErrorHandlingDeserializer.KEY_DESERIALIZER_CLASS, StringDeserializer.class);
		props.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, SpecificAvroDeserializer.class.getName());
		return new DefaultKafkaConsumerFactory<>(props);
	}

	private SeekToCurrentErrorHandler createSeekToCurrentErrorHandler() {
		SeekToCurrentErrorHandler seekToCurrentErrorHandler = new SeekToCurrentErrorHandler((record, exception) -> {
			if (exception.getCause() instanceof DeserializationException) {
				String rawMessage = new String(((DeserializationException) exception.getCause()).getData());
				log.error("Deserialization error for message: {}", rawMessage);
				return;
			}
			log.error("An error occurred: {} with data: {}", exception.getMessage(), record);
		}, new FixedBackOff(1000L, 3L));
		seekToCurrentErrorHandler.setAckAfterHandle(true);
		return seekToCurrentErrorHandler;
	}

	@Bean
	public KafkaWriter<String, IndexedRecord> kafkaWriter(KafkaProducer<String, IndexedRecord> kafkaProducer, KafkaProperties kafkaProperties) {
		return new KafkaWriterImpl<>(kafkaProducer, "");
	}
}