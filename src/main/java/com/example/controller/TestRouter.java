package com.example.controller;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.RouterFunctions;
import org.springframework.web.servlet.function.ServerResponse;

import java.util.Map;

import static org.springframework.web.servlet.function.RequestPredicates.GET;
import static org.springframework.web.servlet.function.RequestPredicates.accept;


@Configuration
public class TestRouter {

	@Bean
	public RouterFunction<ServerResponse> route() {
		return RouterFunctions
				.route(GET("/api/v2/whoami")
						.and(accept(MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON)),
						request -> {
							User user_2 = request.principal()
									.map(Authentication.class::cast)
									.map(Authentication::getPrincipal)
									.map(User.class::cast)
									.orElseThrow();

							return ServerResponse.ok()
									.contentType(MediaType.APPLICATION_JSON)
									.body(Map.of("result", user_2.getUsername()));

						});
	}
}
