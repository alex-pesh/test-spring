package com.example.reactive;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;


@Configuration
public class TestRouterReactive {

	@Bean
	public RouterFunction<ServerResponse> routeReactive(TestHandler handler) {
		return RouterFunctions
				.route(GET("/flux")
						.and(accept(MediaType.APPLICATION_JSON)), handler::flux);

	}
}
