package com.example.test.integration;

import com.example.controller.UserController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;

import static org.junit.jupiter.api.Assertions.*;


/**
 * Test class for the {@link com.example.controller.UserController}
 * <p>
 * Тестирование конечных точек REST API с использованием MockMvc
 * <p>
 * <a href="https://hamcrest.org/JavaHamcrest/tutorial">Hamcrest Tutorial</a>
 * <p>
 * <a href="https://www.baeldung.com/integration-testing-in-spring">Integration Testing in Spring</a>
 */
@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerMockMvcTest {

	@Autowired
	private WebApplicationContext context;

	@Autowired
	private MockMvc mockMvc;

	@BeforeEach
	public void setup() {
/*		mockMvc = MockMvcBuilders
				.webAppContextSetup(context)
//				.standaloneSetup(UserController.class)
				.defaultRequest(get("/").with(user("user").roles("USER")))
				.apply(SecurityMockMvcConfigurers.springSecurity())
				.build();*/
	}

	@Test
//	@WithMockUser(username = "user", password = "password", roles = "USER")
	public void createUser() throws Exception {
		String dto = """
						{
						    "username": "UserName__test",
						    "name": "Name__test",
						    "email": "Email__test"
						}
				""";

		var requestBuilder = post("/api/v1/user/create/", "")
				.with(user("user").password("password").roles("USER"))
				.with(csrf()) // any POST request must include a valid CSRF token
				.content(dto)
				.contentType(MediaType.APPLICATION_JSON);


		MvcResult result = mockMvc.perform(requestBuilder)
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.username").value("UserName__test"))
				.andDo(print())
				.andReturn();

		assertEquals("application/json;charset=UTF-8", result.getResponse().getContentType());
	}



	public void findUser() throws Exception {

		mockMvc.perform(get("/${api.endpoint.user}/{id}", 1))
				.andDo(print())
				.andExpect(content().contentType("application/json;charset=UTF-8"))
				.andExpect(jsonPath("$.username").value("UserName__test"));
	}

}
