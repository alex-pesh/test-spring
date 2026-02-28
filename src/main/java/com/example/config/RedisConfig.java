package com.example.config;

import com.example.redis.RedisStreamListenerNamed;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.connection.stream.*;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.stream.StreamListener;
import org.springframework.data.redis.stream.StreamMessageListenerContainer;
import org.springframework.data.redis.stream.Subscription;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Set;

import static org.springframework.data.redis.stream.StreamMessageListenerContainer.StreamMessageListenerContainerOptions;

@Slf4j
@Configuration
@EnableRedisRepositories
public class RedisConfig {

	/**
	 * https://redis.io/docs/latest/commands/xgroup-create/
	 * redis-cli
	 * XGROUP CREATE mystream mygroup $ MKSTREAM
	 */
//	@Value("${stream.key}")
//	private String streamKey = "events-stream";
//	private String consumerGroup = "events-group";


	@Bean
	JedisConnectionFactory jedisConnectionFactory() {
		return new JedisConnectionFactory();
	}

	@Bean
	public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
		RedisTemplate<String, Object> template = new RedisTemplate<>();
		template.setConnectionFactory(connectionFactory);
		return template;
	}


	@Bean
	public RedisTemplate<String, byte[]> rawRedisTemplate(RedisConnectionFactory connectionFactory) {
		RedisTemplate<String, byte[]> template = new RedisTemplate<>();
		template.setKeySerializer(RedisSerializer.string());
		template.setValueSerializer(RedisSerializer.byteArray());
		template.setHashKeySerializer(RedisSerializer.string());
		template.setHashValueSerializer(RedisSerializer.byteArray());

		template.setConnectionFactory(connectionFactory);
		template.afterPropertiesSet();

		return template;
	}


	@Bean
	public Subscription subscription(
			RedisConnectionFactory connectionFactory,
			Set<RedisStreamListenerNamed<String, MapRecord<String, String, Object>>> streamListeners) throws UnknownHostException {

/*
		StreamMessageListenerContainerOptions<String, Record<String, String>> options =
				StreamMessageListenerContainerOptions
				.builder()
				.pollTimeout(Duration.ofSeconds(1))
				.build();
*/

		StreamMessageListenerContainerOptions<String, MapRecord<String, String, Object>> options2 =
				StreamMessageListenerContainerOptions.builder()
//						.keySerializer(RedisSerializer.string())
						.hashKeySerializer(RedisSerializer.string())
				.build();

		StreamMessageListenerContainer<String, MapRecord<String, String, Object>>  listenerContainer =
				StreamMessageListenerContainer.create(connectionFactory, options2);

		RedisStreamListenerNamed<String, MapRecord<String, String, Object>> listener = streamListeners.iterator().next();
		Subscription subscription = listenerContainer.receive(
				Consumer.from(listener.getConsumerGroup(), "test-spring"),
				StreamOffset.create(listener.getStreamKey(), ReadOffset.lastConsumed()),
				streamListeners.iterator().next());

		listenerContainer.start();

		return subscription;
	}


	@Bean
	RedisMessageListenerContainer redisMessageContainer(RedisConnectionFactory connectionFactory, MessageListener messageListener) {
		RedisMessageListenerContainer container = new RedisMessageListenerContainer();
		container.setConnectionFactory(connectionFactory);
		container.addMessageListener(messageListener, new ChannelTopic("dictionary"));
		return container;
	}

/*
	@Bean
	public MessageListenerAdapter messageListener() {
		return new MessageListenerAdapter(new RedisMessageListener());
	}

	@Bean
	ChannelTopic topic() {
		return new ChannelTopic("messageQueue");
	}
*/


/*
	@Bean
	public KeyspaceEventMessageListener redisEventMessageListener(RedisConnectionFactory redisConnectionFactory) {

		RedisMessageListenerContainer listenerContainer = new RedisMessageListenerContainer();
		listenerContainer.setConnectionFactory(redisConnectionFactory);
		listenerContainer.afterPropertiesSet();
		listenerContainer.start();

		return new KeyspaceEventMessageListener(listenerContainer) {
			@Override
			protected void doHandleMessage(Message message) {
				log.info("Redis KeyspaceEvent: " + message);
			}
		};
	}
*/

}
