package com.example.test.unit;


import com.example.entity.User;
import com.example.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;


import static org.assertj.core.api.Assertions.*;

/**
 * Тестирование репозитория с помощью Assertj и Testcontainers
 * <p>
 * https://habr.com/ru/articles/700286/
 */

@Testcontainers
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class)
@TestPropertySource(locations="classpath:application-test.yml")
public class UserRepositoryContainerTest {

	@Autowired
	private UserRepository repository;

	static PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>(UnitTestContextConfig.CONTAINER_IMG_POSTGRES);

	@DynamicPropertySource
	static void configureProperties(DynamicPropertyRegistry registry) {
		registry.add("spring.datasource.url", postgresContainer::getJdbcUrl);
		registry.add("spring.datasource.username", postgresContainer::getUsername);
		registry.add("spring.datasource.password", postgresContainer::getPassword);
	}


	@BeforeAll
	static void beforeAll() {
		postgresContainer.start();
	}

	@AfterAll
	static void afterAll() {
		postgresContainer.stop();
	}


	@BeforeEach
	void setUp() {
		repository.deleteAll();
	}


	@Test
	@DisplayName("UserRepository_save_load")
	void shouldGetAllUsers() {

		List<User> users = List.of(
				User.builder().id(1L).name("TestUser_1").username("testuser_1").email("user_1@email.com").build(),
				User.builder().id(2L).name("TestUser_2").username("testuser_2").email("user_2@email.com").build(),
				User.builder().id(3L).name("TestUser_3").username("testuser_3").email("user_3@email.com").build()
		);

		repository.saveAll(users);

/*
		given()
				.contentType(ContentType.JSON)
				.when()
				.get("/todos")
				.then()
				.statusCode(200)
				.body(".", hasSize(2));
*/

		assertThat(repository.findAll()).hasSize(3);

	}

}
