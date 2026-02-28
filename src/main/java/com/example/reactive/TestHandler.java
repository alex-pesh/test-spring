package com.example.reactive;

import lombok.SneakyThrows;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.io.UnsupportedEncodingException;

import static org.springframework.web.reactive.function.server.ServerResponse.ok;

@Component
public class TestHandler {

	public Mono<ServerResponse> flux(ServerRequest request) {
			return ok()
				.contentType(MediaType.APPLICATION_JSON)
				.body("Flux", String.class);
	}

	public class SneakyThrowsExample implements Runnable {
		@SneakyThrows(UnsupportedEncodingException.class)
		public String utf8ToString(byte[] bytes) {
			return new String(bytes, "UTF-8");
		}

		@SneakyThrows
		public void run() {
			throw new Throwable();
		}
	}

}
