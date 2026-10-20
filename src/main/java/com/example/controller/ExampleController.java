package com.example.controller;

import com.example.service.AuthService;
import com.example.service.ExampleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.concurrent.TimeUnit;


@Slf4j
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ExampleController {

	@Autowired
	private ExampleService exampleService;
	private final AuthService authService;


	@ResponseBody
	@GetMapping(value = "/version")
	public ResponseEntity<String> getVersion() {
		return ResponseEntity.ok("0.0.1");
	}


	@GetMapping(value = "/service")
	public ResponseEntity<String> test() throws Exception {
		exampleService.test();
		return ResponseEntity.ok().body("Test successful");
	}


	@GetMapping(value = "/service/redis")
	public ResponseEntity<String> testRedis() throws Exception {
		exampleService.testRedis();
		return ResponseEntity.ok()
				.cacheControl(CacheControl.maxAge(30, TimeUnit.SECONDS))
				.body("Test successful");
	}


	/**
	 * Asynchronous Requests
	 * https://docs.spring.io/spring-framework/reference/web/webmvc/mvc-ann-async.html#mvc-ann-async-deferredresult
	 * @return
	 */
	@GetMapping(value = "/async")
	public DeferredResult<String> async() {
		authService.auth();

		DeferredResult<String> result = new DeferredResult<>();
		exampleService.testAsync()
				.thenAcceptAsync((String svcResult) -> {
					System.out.println(svcResult);
					result.setResult(svcResult);
				})
				.thenRun(() -> {
					System.out.println("thenRunAsync");
				});

		return result;
	}

}
