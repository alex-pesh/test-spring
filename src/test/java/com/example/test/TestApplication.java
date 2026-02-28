package com.example.test;

import com.example.Application;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.PostgreSQLContainer;

public class TestApplication {

	public static void main(String[] args) {
		SpringApplication
				.from(Application::main)
				.with(ContainersConfig.class)
				.run(args);
	}



	/**
	 * https://howtodoinjava.com/spring-boot/spring-boot-service-connection-example/
	 */

	@TestConfiguration(proxyBeanMethods = false)
	static class ContainersConfig {

		@Bean
	//	@RestartScope
		@ServiceConnection("postgreSQLContainer")
		PostgreSQLContainer<?> postgreSQLContainer() {
			return new PostgreSQLContainer<>("postgres:17");
		}


	/*
		@Container
		private static final RedisContainer REDIS_CONTAINER =
				new RedisContainer(DockerImageName.parse("7.4.1-alpine")).withExposedPorts(6379);

		@DynamicPropertySource
		private static void registerRedisProperties(DynamicPropertyRegistry registry) {
			registry.add("spring.redis.host", REDIS_CONTAINER::getHost);
			registry.add("spring.redis.port", () -> REDIS_CONTAINER.getMappedPort(6379).toString());
		}
	*/

/*
//	 * Не получилось поднять RedisContainer. По-видимому какая-то внутренняя бага с установкой
//	 * connectionDetails для RedisContainer из-под @ServiceConnection
		@Bean
	//	@RestartScope
		@ServiceConnection("redisContainer")
		public RedisContainer redisContainer() {
			var redis = new RedisContainer(DockerImageName.parse("7.4.1-alpine"));
			redis.addExposedPort(6379);
			redis.start();

			return redis;
		}*/

	}
}
