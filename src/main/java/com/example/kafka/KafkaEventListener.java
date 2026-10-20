package com.example.kafka;

import com.example.dto.KafkaEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.retrytopic.DltStrategy;
import org.springframework.kafka.retrytopic.TopicSuffixingStrategy;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class KafkaEventListener {

	@KafkaListener(topics = "app-events")
	@RetryableTopic(	// будут создаваться топики для попыток app-events_0, app-events_1, ...
			attempts = "3",
			backoff = @Backoff(delay = 1000, multiplier = 2.0),
			autoCreateTopics = "true",
			topicSuffixingStrategy = TopicSuffixingStrategy.SUFFIX_WITH_INDEX_VALUE,
			dltStrategy = DltStrategy.FAIL_ON_ERROR )
	public void processEvent(KafkaEvent event, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
		log.info("Processing event {} from topic {}", event.objectId(), topic);

		try {
//			externalServiceClient.process(event);
		} catch (Exception e) {
			log.error("External service error for event {}: {}", event.objectId(), e.getMessage());
			throw e; // Trigger retry mechanism
		}
	}


	@DltHandler
	public void handleFailedEvent(KafkaEvent event, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
		log.error("Event {} failed all retries, sending to manual review from topic {}", event.objectId(), topic);

		// Implement dead letter handling logic
//		deadLetterService.handleFailedEvent(event);

	}
}