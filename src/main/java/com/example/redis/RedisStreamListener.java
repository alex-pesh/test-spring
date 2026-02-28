package com.example.redis;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StreamOperations;
import org.springframework.data.redis.stream.StreamListener;
import org.springframework.stereotype.Service;

import java.net.InetAddress;

/**
 * Messages in streams are persisted, and support both at-most-once as well as at-least-once delivery semantics.
 * <p>
 * https://redis.io/docs/latest/develop/data-types/streams/
 */

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisStreamListener implements RedisStreamListenerNamed<String, MapRecord<String, String, Object>> {

	private final RedisTemplate<String, String> redisTemplate;

	private final String streamKey = "events-stream";
	private final String consumerGroup = "events-group";


	@Override
	public String getStreamKey() {
		return streamKey;
	}

	@Override
	public String getConsumerGroup() {
		return consumerGroup;
	}


	@Override
	@SneakyThrows
	public void onMessage(MapRecord<String, String, Object> record) {

		log.info(InetAddress.getLocalHost().getHostName() + " - consumed :" + record.getValue());

//		this.redisTemplate
//				.opsForZSet()
//				.incrementScore(record.getValue(), "Views", 1)
//				.subscribe();

		redisTemplate.opsForStream().acknowledge(streamKey, consumerGroup, record.getId());
	}

}
