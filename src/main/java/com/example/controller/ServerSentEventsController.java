package com.example.controller;


import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.util.MimeType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;

import java.net.InetAddress;
import java.time.Duration;
import java.time.LocalTime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * https://www.baeldung.com/spring-server-sent-events
 * https://medium.com/@rohanreddym/setting-up-server-sent-events-sse-with-spring-boot-on-kubernetes-df906b799dbf
 */

@Slf4j
@RestController
@RequestMapping("/api/v1")
public class ServerSentEventsController {

	private static final Integer DELAY_MILLIS = Integer.valueOf(System.getProperty("DELAY_MILLIS", "1000"));


	@GetMapping(value = "/public/events")
	public SseEmitter eventsStream() {

		SseEmitter emitter = new SseEmitter();

		try (ExecutorService sseMvcExecutor = Executors.newSingleThreadExecutor()) {
			sseMvcExecutor.execute(() -> {
				try {
					for (int i = 0; i < 10; i++) {
						SseEmitter.SseEventBuilder event = SseEmitter.event()
								.id(String.valueOf(i))
								.name("sse event")
								.data("SSE - " + LocalTime.now());
						emitter.send(event);
						Thread.sleep(500);
					}
					emitter.complete();
				} catch (Exception ex) {
					emitter.completeWithError(ex);
				}
			});
		}

		emitter.onCompletion(() -> {
			System.out.println("Emitting complete");
		});

		return emitter;

	}


	@GetMapping(value = "/public/events-react", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public Flux<ServerSentEvent<String>> streamEvents(HttpServletRequest request, @RequestParam Integer delay) {
		if (delay == null) {
			delay = DELAY_MILLIS;
		}
		log.info("Requested event with delay: {}", delay);

		return Flux.interval(Duration.ofMillis(delay))
				.map(sequence -> ServerSentEvent.<String>builder()
						.id(String.valueOf(sequence))
						.event("periodic-event")
						.comment("Connection: " + request.getRemoteAddr() + ":" + request.getRemotePort())
						.data("SSE - " + LocalTime.now())
						.build());
	}
}
