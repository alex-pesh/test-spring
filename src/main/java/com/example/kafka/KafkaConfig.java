package com.example.kafka;

import com.example.dto.Order;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.listener.KafkaConsumerBackoffManager;
import org.springframework.kafka.retrytopic.DeadLetterPublishingRecovererFactory;
import org.springframework.kafka.retrytopic.ListenerContainerFactoryConfigurer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.stereotype.Component;
import org.springframework.util.backoff.BackOff;
import org.springframework.util.backoff.FixedBackOff;

import java.net.SocketTimeoutException;
import java.time.Clock;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Configuration
public class KafkaConfig {


	@Bean
	public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory() {
		ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
		factory.setConsumerFactory(consumerFactory());
		factory.setCommonErrorHandler(errorHandler());
		factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.RECORD);
		return factory;
	}


	@Bean
	public Map<String, Object> consumerProperties() {
		Map<String, Object> props = new HashMap<>();
		props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
		props.put(ConsumerConfig.GROUP_ID_CONFIG, "payment-service");
		props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false); // Manual acknowledgment
		props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
		props.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, 300000); // 5 minutes
		props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, 30000);
		props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
		props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
		props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
		return props;
	}


	@Bean
	public ConsumerFactory<String, Object> consumerFactory() {
		return new DefaultKafkaConsumerFactory<>(consumerProperties());
	}


	/**
	 * Blocking Retry handler. Будет использоваться для всех контейнеров,
	 * если для KafkaListener явно не задан @RetryableTopic (Non-Blocking Retry)
	 * @return
	 */
	@Bean
	public DefaultErrorHandler errorHandler() {
		BackOff fixedBackOff = new FixedBackOff(1000L, 2L);
		DefaultErrorHandler errorHandler = new DefaultErrorHandler((record, exception) -> {
			// logic to execute when all the retry attemps are exhausted
			log.error("Failed to process message after retries: {}", record.value());
		}, fixedBackOff);

		errorHandler.addRetryableExceptions(SocketTimeoutException.class);
		errorHandler.addNotRetryableExceptions(NullPointerException.class);

		return errorHandler;
	}


	/**
	 * Кастомизация recoverer, который юудет вызываться при @RetryableTopic над KafkaListener,
	 * когда при безуспешных попытках recover для отправки записи в топик DeadLetter.
	 *
	 * @param kafkaConsumerBackoffManager
	 * @param deadLetterPublishingRecovererFactory
	 * @return
	 */

	@Bean
	ListenerContainerFactoryConfigurer listenerContainerFactoryConfigurer(
			KafkaTemplate<String, String> template,
			KafkaConsumerBackoffManager kafkaConsumerBackoffManager,
			DeadLetterPublishingRecovererFactory deadLetterPublishingRecovererFactory) {

		return new ListenerContainerFactoryConfigurer(
				kafkaConsumerBackoffManager,
				deadLetterPublishingRecovererFactory,
				Clock.systemUTC()) {
			@Override
			protected DefaultErrorHandler createDefaultErrorHandlerInstance(DeadLetterPublishingRecoverer recoverer) {
				return super.createDefaultErrorHandlerInstance(deadLetterRecoverer(template));
			}
		};
	}

	/**
	 Default Headers Added by Spring:
	 <li> kafka_dlt-original-topic
	 <li> kafka_dlt-original-partition
	 <li> kafka_dlt-original-offset
	 <li> kafka_dlt-original-timestamp
	 <li> kafka_dlt-original-timestamp-type
	 <li> kafka_dlt-exception-fqcn
	 <li> kafka_dlt-exception-message
	 <li> kafka_dlt-exception-stacktrace
	 */
	@Bean
	public DeadLetterPublishingRecoverer deadLetterRecoverer(KafkaTemplate template) {
		return new DeadLetterPublishingRecoverer(template, (record, exception) -> {
			// Default naming: originalTopic + "-dlt"
			return new TopicPartition(record.topic() + "-dlt", record.partition());
		});
	}


}
