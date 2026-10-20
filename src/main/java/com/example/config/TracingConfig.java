package com.example.config;

import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationHandler;
import io.micrometer.observation.ObservationTextPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TracingConfig {

	@Bean
	public ObservationHandler<Observation.Context> observationTextHandler() {
		return new ObservationTextPublisher();
	}

}