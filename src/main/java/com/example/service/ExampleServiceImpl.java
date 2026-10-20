package com.example.service;

import com.example.aspect.CustomAspect;
import com.example.config.EmailProperties;
import com.example.config.MeasureProperties;
import com.example.entity.User;
import com.example.entity.projection.JobPositionProjection;
import com.example.entity.projection.UserProjection;
import com.example.repository.JobPositionRepository;
import com.example.repository.UserDetailsRepository;
import com.example.repository.UserRepository;
import io.micrometer.observation.annotation.Observed;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

@Service
@Scope(value = "custom", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class ExampleServiceImpl implements ExampleService {

	private static final Logger log = org.slf4j.LoggerFactory.getLogger(ExampleServiceImpl.class);
	private final UserRepository userRepository;
	private final UserDetailsRepository userDetailsRepository;
	private final JobPositionRepository jobPositionRepository;
	private final EmailProperties emailProperties;
	private final MeasureProperties measureProperties;

	private final ApplicationContext ctx;
	private final RedisTemplate<String, String> redisTemplate;
	private final RedisTemplate<String, byte[]> redisTemplateRaw;

	private final ProductService productService;

	@PersistenceContext
	private EntityManager em;

	@Autowired
	private SessionFactory sessionFactory;

	private Object lock = new Object();

	public ExampleServiceImpl(UserRepository userRepository, UserDetailsRepository userDetailsRepository, JobPositionRepository jobPositionRepository, EmailProperties emailProperties, MeasureProperties measureProperties, ApplicationContext ctx, RedisTemplate<String, String> redisTemplate, RedisTemplate<String, byte[]> redisTemplateRaw, ProductService productService) {
		this.userRepository = userRepository;
		this.userDetailsRepository = userDetailsRepository;
		this.jobPositionRepository = jobPositionRepository;
		this.emailProperties = emailProperties;
		this.measureProperties = measureProperties;
		this.ctx = ctx;
		this.redisTemplate = redisTemplate;
		this.redisTemplateRaw = redisTemplateRaw;
		this.productService = productService;
	}


	@PostConstruct
	public void init() {
		log.info("EntityManager: {}", em);
	}


	@CustomAspect
	@Observed(name = "TestService:test")
	@Override
	@Transactional(readOnly = false)
	public void test() {
		log.info("test invoked");

		userRepository.findAll();

//		productService.updatePrice(1L, 255.50);


//		Stream.iterate(0, i -> i +1).collect(Collectors.toList());
/*
		synchronized (lock) {
			try {
				log.info("Processing in thread " + Thread.currentThread().getId());
				doTest();
				log.info("Processing finished in thread " + Thread.currentThread().getId());
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}
*/

//		Stream<UserDetails> detailsStream = userDetailsRepository.findAllStreamed();
//		List<String> names = detailsStream.map(UserDetails::getUsername).collect(Collectors.toList());


		List<UserProjection.UserSummary> usersStream = userRepository.findProjection(UserProjection.UserSummary.class);
		usersStream.stream().forEach(user -> System.out.println(user.getInfo()));

//		List<String> names = usersStream.map(User::getUsername).toList();
//		log.info("Found users: {}", names.size());

//		usersStream.forEach(user -> {
//			redisTemplate.opsForValue().set("user_" + user.getId(), user.getName());
//		});
//		log.info("Cached users: {}", redisTemplate.opsForValue().get("user_1"));


		Stream<User> users = userRepository.findProjection();
		users.forEach(u -> log.info("{} - {}", u.getId(), u.getName()));


/*
		Page<User> users = userRepository.findAll(
//				new PageRequest_(0, 10, JpaSort.sort(User.class).by(User::getName))
				PageRequest.of(2, 10, Sort.by("id").ascending())
		);
//		users.stream().findFirst().get().setName("Alibaba");
		log.info("Found users: {}", users.getTotalElements());
*/


//		UserProjection.UserSummary userSummary = userRepository.findByEmail("user@mail.test.ru", UserProjection.UserSummary.class);
//		log.info("Found user summary: {}", userSummary.getInfo());


//		List<JobPosition> positions = jobPositionRepository.findAll();
//		log.info("Found job positions: {}", positions.size());

//		Stream<JobPosition> jobPositionStream = jobPositionRepository.findAllStreamed();
//		jobPositionStream.forEach(jp -> System.out.println(jp.getName()));


		List<JobPositionProjection.Summary> jobPositions =
				jobPositionRepository.findProjection(JobPositionProjection.Summary.class);

		jobPositions.forEach(position -> log.info("Job position «{}» of department «{}» has salary {}",
				position.getName(), position.getDepartment().getName(), position.getSalary()));
	}


	@Override
	public void testRedis() {
		Path path = Path.of("/home/alexander/workspace/dictionary.zip");
		try {
			byte[] data = Files.readAllBytes(path);
//			redisTemplateRaw.opsForStream().add(ObjectRecord.create("events-stream", data));
//			redisTemplateRaw.opsForStream().add("events-stream", Map.of("dictionary", data));
			redisTemplateRaw.convertAndSend("dictionary", data);
		} catch (IOException e) {
			throw new RuntimeException("Failed to load file", e);
		}
	}


	static class PageRequest_ extends PageRequest {

		public PageRequest_(int pageNumber, int pageSize, Sort sort) {
			super(pageNumber, pageSize, sort);
		}
	}


	private void doTest() throws InterruptedException {
		Thread.sleep(1000);
	}

	@Async("asyncTaskExecutor")
	@Override
	public CompletableFuture<String> testAsync() {
		return CompletableFuture.completedFuture("testAsync result");
	}

}
