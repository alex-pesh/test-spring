package com.example.test.unit;


import com.example.Application;
import com.example.entity.User;
import com.example.repository.UserRepository;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigurationPackage;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.hasSize;


/**
 * Тестирование конечных точек REST API с использованием RestAssured и Testcontainers
 * <p>
 * https://habr.com/ru/articles/700286/
 */

@Testcontainers
@SpringBootTest
//@AutoConfigureMockMvc
@AutoConfigurationPackage(basePackageClasses = Application.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class)
@TestPropertySource(locations="classpath:application-test.yml")
public class UserControllerContainerTest {

	@LocalServerPort
	private Integer port;

	@Autowired
	UserRepository userRepository;


	@Container
	static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(UnitTestContextConfig.CONTAINER_IMG_POSTGRES);

	@DynamicPropertySource
	static void configureProperties(DynamicPropertyRegistry registry) {
		registry.add("spring.datasource.url", postgres::getJdbcUrl);
		registry.add("spring.datasource.username", postgres::getUsername);
		registry.add("spring.datasource.password", postgres::getPassword);
	}

	@BeforeAll
	static void beforeAll() {
		postgres.start();
	}

	@AfterAll
	static void afterAll() {
		postgres.stop();
	}

	@BeforeEach
	void setUp() {
		userRepository.deleteAll();
		RestAssured.baseURI = "http://localhost";
		RestAssured.port = port;
		RestAssured.basePath = "/api/v1";
	}



	@Test
	void shouldGetAllUsers() {
		List<User> users = List.of(
				User.builder().id(1L).name("TestUser_1").username("testuser_1").email("user_1@email.com").build(),
				User.builder().id(2L).name("TestUser_2").username("testuser_2").email("user_2@email.com").build(),
				User.builder().id(3L).name("TestUser_3").username("testuser_3").email("user_3@email.com").build()
		);

		userRepository.saveAll(users);

		given()
				.contentType(ContentType.JSON)
				.when()
				.get("/user")
				.then()
				.statusCode(200)
				.body(".", hasSize(3));
	}

}
