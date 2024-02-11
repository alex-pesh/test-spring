package ru.test.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.function.ServerResponse;
import reactor.core.publisher.Flux;
import ru.test.service.AuthService;
import ru.test.service.TestService;

@Slf4j
@RestController
@RequestMapping("api")
@RequiredArgsConstructor
public class TestController {

	private final TestService testService;
	private final AuthService authService;


	@ResponseBody
	@GetMapping(value = "/version")
	public ResponseEntity<String> getVersion() {
		return ResponseEntity.ok("0.0.1");
	}


	@GetMapping(value = "/service")
	public ResponseEntity<Void> test() {
		testService.test();

		return ResponseEntity.ok().build();
	}

	@GetMapping(value = "/async")
	public void async() {
		testService.testAsync()
				.thenAccept((Object result) -> {
					System.out.println(result);
				})
				.thenRun(() -> {
					System.out.println("thenRunAsync");
				});
	}


/*	@GetMapping(value = "/flux")
	public Flux<String> fluxTest() {
		Flux<String> flux = Flux.range(1, 10)
				.map(Integer::toString);
		return null;
	}*/

}
