package com.example.redis;

import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.stream.StreamRecords;
import org.springframework.data.redis.connection.stream.StringRecord;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;


@Slf4j
@Service
@RequiredArgsConstructor
//@EnableScheduling
public class RedisEventPublisher {

	private final RedisTemplate<String, String> redisTemplate;

	private final AtomicInteger atomicInteger = new AtomicInteger(0);

//	@Value("${stream.key}")
	private String streamKey = "events-stream";



	@Scheduled(fixedRateString = "5000")
	public void publishEvent() {

/*
		ObjectRecord<String, MovieDetails> record = StreamRecords.newRecord()
				.ofObject(movieDetails)
				.withStreamKey(streamKey);
*/

		StringRecord record = StreamRecords
				.string(Map.of("test_key", "FOOO"))
				.withStreamKey(streamKey);

		this.redisTemplate.opsForStream().add(record);

		atomicInteger.incrementAndGet();
	}


	@Scheduled(fixedRate = 10000)
	public void showPublishedEventsSoFar() {
		log.info("Total events sent: {}", atomicInteger.get());
	}

}
