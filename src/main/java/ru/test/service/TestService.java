package ru.test.service;

import java.util.concurrent.CompletableFuture;

public interface TestService {

	void test();

	CompletableFuture testAsync();

}
