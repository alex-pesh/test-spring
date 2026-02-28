package com.example.test.unit;

import com.example.controller.UserController;
import com.example.entity.User;
import com.example.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(UserController.class)
@Import(UserController.class)
public class UserControllerWebMvcTest {

	@Autowired
	private WebApplicationContext context;

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private UserRepository userRepository;

	@Autowired
	@InjectMocks
	private UserController userController;


	@Test
	public void getAllUsers() throws Exception {

		List<User> users = List.of(
				User.builder().id(1L).name("TestUser_1").username("testuser_1").email("user_1@email.com").build(),
				User.builder().id(2L).name("TestUser_2").username("testuser_2").email("user_2@email.com").build(),
				User.builder().id(3L).name("TestUser_3").username("testuser_3").email("user_3@email.com").build()
		);

		Mockito.when(userRepository.findAll()).thenReturn(users);

		var requestBuilder = get("/api/v1/user/")
				.with(user("user").password("password").roles("USER"));


		MvcResult result = mockMvc.perform(requestBuilder)
				.andExpect(status().isOk())
//				.andExpect(jsonPath("$.username").value("UserName__test"))
				.andDo(print())
				.andReturn();

//		Mockito.verify(Mockito.spy(userController)).getAllUsers();

		assertEquals(MediaType.APPLICATION_JSON_VALUE, result.getResponse().getContentType());

	}


}
