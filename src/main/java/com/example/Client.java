package com.example;

import io.netty.channel.ChannelOption;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.timeout.ReadTimeoutHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;
import reactor.netty.http.client.HttpClient;
import reactor.netty.tcp.TcpClient;
import reactor.netty.transport.logging.AdvancedByteBufFormat;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 */
@Slf4j
public class Client {

	public static void main(String[] args) {
		consumeServerSentEvent();
	}


	static ParameterizedTypeReference<ServerSentEvent<String>> TYPE_SSE = new ParameterizedTypeReference<>() {};

	static final TcpClient tcpClient = TcpClient.create()
//			.bindAddress(() -> new java.net.InetSocketAddress("127.0.0.1", 51734)) // Bind to static client port
			.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000) // 10 seconds connection timeout
			.option(ChannelOption.SO_KEEPALIVE, true) // Enable TCP keep-alive
			.option(ChannelOption.TCP_NODELAY, true) // Disable Nagle's algorithm
			.doOnConnected(connection -> {
				connection.addHandlerLast(new ReadTimeoutHandler(60)); // 60 seconds read timeout
			});

	static  final HttpClient httpClient = HttpClient.from(tcpClient)//create()
			.wiretap(HttpClient.class.getCanonicalName(), LogLevel.DEBUG, AdvancedByteBufFormat.TEXTUAL, StandardCharsets.UTF_8);



	public static void consumeServerSentEvent() {

		ExecutorService executor = Executors.newFixedThreadPool(1);
		Scheduler scheduler = Schedulers.fromExecutor(executor);


		WebClient client_1 = buildClient("http://127.0.0.1:8080");
		WebClient client_2 = buildClient("http://127.0.0.1:8081");

		Flux<ServerSentEvent<String>> eventStream_1 = getStream(client_1, scheduler, 1000);
		Flux<ServerSentEvent<String>> eventStream_2 = getStream(client_2, scheduler, 5000);

		Flux.merge(eventStream_1, eventStream_2)
				.subscribe(
						event -> log.info("event: id [{}], name[{}], conn[{}], content[{}]",
								event.id(), event.event(), event.comment(), event.data()),
						error -> System.out.println("Error receiving SSE: {}" + error),
						() -> System.out.println("Completed!!!")
				);;


	}


	private static WebClient buildClient(String baseUrl) {
		return WebClient.builder()
//				.clientConnector(new ReactorClientHttpConnector(httpClient))
				.baseUrl(baseUrl)
				.filter((request, next) -> {
					System.out.println("Request: " + request.method() + " " + request.url());
					request.headers().forEach((name, values) -> System.out.println(name + ": " + values));
					return next.exchange(request)
							.doOnSuccess(response -> {
								// Log response details
								System.out.println("Response Status: " + response.statusCode());
								response.headers().asHttpHeaders().forEach((name, values) -> System.out.println(name + ": " + values));
							});
				})
				.build();

	}


	private static Flux<ServerSentEvent<String>> getStream(WebClient client, Scheduler scheduler, long delay) {
		Flux<ServerSentEvent<String>> eventStream = client.get()
				.uri("/api/v1/public/events-react?delay="  + delay)
				.accept(MediaType.TEXT_EVENT_STREAM)
				.retrieve()
				.bodyToFlux(TYPE_SSE)
				.publishOn(scheduler);

		eventStream
//				.doOnNext(
//						content -> System.out.println(String.format("Time: %s - event: id [%s], name[%s], content[%s] ",
//								LocalTime.now(), content.id(), content.event(), content.data()))
//				)
//				.subscribe();
//				.blockLast();

/*				.subscribe(
						event -> System.out.printf("Time: %s - event: id [%s], name[%s], server[%s], content[%s] %n",
								LocalTime.now(), event.id(), event.event(), event.comment(), event.data()),
						error -> System.out.println("Error receiving SSE: {}" + error),
						() -> System.out.println("Completed!!!")
				);*/

				.subscribe();
		return eventStream;
	}

}
