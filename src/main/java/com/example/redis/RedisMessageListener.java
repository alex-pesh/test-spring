package com.example.redis;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Service;

import java.net.InetAddress;


/**
 *
 * Redis' Pub/Sub exhibits at-most-once message delivery semantics. As the name suggests, it means that a message will be
 * delivered once if at all. Once the message is sent by the Redis server, there's no chance of it being sent again.
 * If the subscriber is unable to handle the message (for example, due to an error or a network disconnect) the message is forever lost.
 * <p>
 * If your application requires stronger delivery guarantees, you may want to learn about Redis Streams.
 * Messages in streams are persisted, and support both at-most-once as well as at-least-once delivery semantics.
 * <p>
 * https://redis.io/docs/latest/develop/pubsub/
 * <p>
 * https://www.baeldung.com/spring-data-redis-pub-sub
 */

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisMessageListener implements MessageListener {


	@Override
	@SneakyThrows
	public void onMessage(Message message, byte[] pattern) {

		String channel = new String(message.getChannel());
		log.info("{} - Message received from channel '{}': {}",
				InetAddress.getLocalHost().getHostName(), channel, message.getBody().length);
	}
	
}
