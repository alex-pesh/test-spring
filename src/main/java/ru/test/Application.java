package ru.test;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@SpringBootApplication
@EnableAsync
@EnableConfigurationProperties
public class Application {

	public static void main(String[] args) {
		Thread.currentThread().setName("test-spring-thread");
		SpringApplication.run(Application.class, args);
	}


	@Bean(name = "asyncTaskExecutor")
	public Executor asyncTaskExecutor() {

		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(2);
		executor.setMaxPoolSize(2);
		executor.setQueueCapacity(4);
		executor.setThreadNamePrefix("ApplicationTask-");
		executor.initialize();

		return executor;
	}

}
