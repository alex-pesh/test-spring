package com.example.redis;

import org.springframework.data.redis.connection.stream.Record;
import org.springframework.data.redis.stream.StreamListener;

public interface RedisStreamListenerNamed<K, V extends Record<K, ?>> extends StreamListener<K, V> {

	String getStreamKey();
	String getConsumerGroup();

}
