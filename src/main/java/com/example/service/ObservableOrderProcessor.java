package com.example.service;

import com.example.dto.Order;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import org.springframework.stereotype.Component;

@Component
public class ObservableOrderProcessor {

	private final ObservationRegistry observationRegistry;

	public ObservableOrderProcessor(ObservationRegistry observationRegistry) {
		this.observationRegistry = observationRegistry;
	}

	public void processOrder(Order order) {

		Observation observation = Observation
				.createNotStarted("order-processing", observationRegistry)
				.lowCardinalityKeyValue("customerId", order.customerId().toString())
				.highCardinalityKeyValue("orderId", order.id().toString());

		try (Observation.Scope scope = observation.start().openScope()) {
//				validateOrder(order);
//				reserveInventory(order);
//				processPayment(order);
		} catch (Exception e) {
			observation.error(e);
			throw e;
		} finally {
			observation.stop();
		}
	}
}
