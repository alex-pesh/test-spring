package ru.test.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.test.entity.UserDetails;
import ru.test.repository.UserDetailsRepository;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class TestServiceImpl implements TestService {

	private final UserDetailsRepository userDetailsRepository;

	@Override
	@Transactional(readOnly = true)
	public void test() {
		log.info("test invoked");

		Stream<UserDetails> details = userDetailsRepository.findAllStreamed();
		List<String> names = details.map(UserDetails::getName).collect(Collectors.toList());
	}


	@Async("asyncTaskExecutor")
	@Override
	public CompletableFuture testAsync() {
		return CompletableFuture.completedFuture("testAsync result");
	}

}
