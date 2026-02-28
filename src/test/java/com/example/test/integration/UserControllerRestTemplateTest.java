package com.example.test.integration;


import com.example.controller.UserController;
import com.example.repository.UserRepository;
import com.example.test.TestApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;


@SpringBootTest(classes = {IntTestContextConfig.class},
		webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:application-test.yml")
@AutoConfigureMockMvc
@ContextConfiguration(/*loader = AnnotationConfigContextLoader.class*/)
public class UserControllerRestTemplateTest {

	@LocalServerPort
	private int port;

	@Autowired
	private TestRestTemplate restTemplate;

	@MockBean
	private UserRepository userRepository;

	@Autowired
	UserController userController;

	@Autowired
	private WebApplicationContext context;

	@Test
	@WithUserDetails("user")
	void greetingShouldReturnDefaultMessage() throws Exception {
		assertThat(
				restTemplate
//						.withBasicAuth("user", "password")
						.getForObject("http://localhost:" + port + "/api/v1/user/", String.class)
		).isInstanceOf(List.class);
	}

}
