package com.example.service;

import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public interface ExampleService {

	void test();

	void testRedis();

	CompletableFuture<String> testAsync();

}
