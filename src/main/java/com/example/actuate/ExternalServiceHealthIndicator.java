package com.example.actuate;

import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health;
import org.springframework.stereotype.Component;

@Component
public class ExternalServiceHealthIndicator extends AbstractHealthIndicator {
	@Override
	protected void doHealthCheck(Health.Builder builder) {
		builder.up();
	}

/*
	private final ExternalServiceClient client;

	public ExternalServiceHealthIndicator(ExternalServiceClient client) {
		this.client = client;
	}

	@Override
	protected void doHealthCheck(Health.Builder builder) {
		try {
			ServiceStatus status = client.checkStatus();
			if (status.isOperational()) {
				builder.up()
						.withDetail("version", status.getVersion())
						.withDetail("latency", status.getResponseTime());
			} else {
				builder.down()
						.withDetail("reason", status.getErrorMessage());
			}
		} catch (Exception e) {
			builder.down()
					.withDetail("error", e.getMessage());
		}
	}
	*/
}