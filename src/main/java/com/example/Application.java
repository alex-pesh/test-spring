package com.example;


import com.example.config.ApplicationConfig;
import com.example.config.RedisConfig;
import com.example.service.ExampleService;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.util.concurrent.Executor;

@SpringBootApplication(proxyBeanMethods = true)
@Import(RedisConfig.class)
@SpringBootConfiguration
@EnableAsync
@EnableConfigurationProperties
@ConfigurationPropertiesScan(basePackageClasses = ApplicationConfig.class)
public class Application {

	public static void main(String[] args) {
		Thread.currentThread().setName("test-spring-thread-main");
		SpringApplication app = new SpringApplication(Application.class);
//		app.setAdditionalProfiles();
		app.run(args);

//		ConfigurableApplicationContext ctx = SpringApplication.run(Application.class, args);
//		new Application().cmdRunner(null);
	}


	@Bean
	public A a() {
		return new A();
	}

	@Bean
	public B b() {
		A a = a();
		return new B(a);
	}


	public static class A {}

	public static class B {
		A a;

		public B(A a) {
			this.a = a;
		}
	}


	@Bean
	public BeanFactoryPostProcessor beanFactoryPostProcessor() {
		return beanFactory -> {
			String[] beanNames = beanFactory.getBeanDefinitionNames();
			System.out.printf("Defined beans: (%d)%n", beanNames.length);
			for (String bean : beanNames) {
				System.out.println(bean);
			}
		};
	}


//	@Bean
	public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
		return args -> {
			ctx.getBean(ExampleService.class).test();
		};
	}


	//	@Bean
	public CommandLineRunner cmdRunner(ApplicationContext ctx) {

		// https://habr.com/ru/articles/811289/

		Scheduler scheduler_pub = Schedulers.newSingle("test-scheduler-pub", false);
		Scheduler scheduler_sub = Schedulers.newSingle("test-scheduler-sub", true);


		Flux.just(1, 2)
				.subscribeOn(scheduler_pub)
				.map(it -> it + 1)
				.filter(item -> {
					System.out.printf("Filtering element: %s;\t%s%n", item, Thread.currentThread());
					return item >= 3;
				})
				.publishOn(scheduler_sub)
				.doOnDiscard(Integer.class, item -> {
					System.out.printf("Discarded element: %s;\t%s%n", item, Thread.currentThread());
				}).subscribe((item) -> {
					System.out.printf("Consumed element:  %s;\t%s%n", item, Thread.currentThread());
				});


/*
		Publisher<Object> publisher =
				Mono.fromCallable(() -> {
					Thread.sleep(1000);
					return 90;
				});

		publisher.subscribe(new Subscriber<>() {
			@Override
			public void onSubscribe(Subscription s) {
				System.out.println("onSubscribe-1");
				s.request(10);
			}

			@Override
			public void onNext(Object o) {
				System.out.println("onNext-1: " + o);
			}

			@Override
			public void onError(Throwable t) {
				System.out.println("onError-1");
			}

			@Override
			public void onComplete() {
				System.out.println("onComplete-1");
			}
		});
*/



		return (args) -> {
			ctx.getBean("asyncTaskExecutor");
		};
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


/*
	@Bean(TaskExecutionAutoConfiguration.APPLICATION_TASK_EXECUTOR_BEAN_NAME)
	public AsyncTaskExecutor asyncTaskExecutor() {
		return new TaskExecutorAdapter(Executors.newVirtualThreadPerTaskExecutor());
	}
*/
/*

	@Bean
	public TomcatProtocolHandlerCustomizer<?> protocolHandlerVirtualThreadExecutorCustomizer() {
		return protocolHandler -> {
			protocolHandler.setExecutor(Executors.newVirtualThreadPerTaskExecutor());
		};
	}
*/

	@Bean
	public BeanPostProcessor beanPostProcessor() {
		return new BeanPostProcessor() {
			@Override
			public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
				return BeanPostProcessor.super.postProcessAfterInitialization(bean, beanName);
			}
		};
	}

}
