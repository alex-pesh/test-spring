package com.example.controller;

import com.example.mapper.UserMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import com.example.dto.UserDto;
import com.example.repository.UserRepository;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(value = "/api/v1/user")
@RequiredArgsConstructor
public class UserController {

	private final UserMapper userMapper;

	private final UserRepository userRepository;

	@Autowired
	private Validator validator;


	@GetMapping(value = "/whoami")
	public ResponseEntity<String> whoami(HttpServletRequest request,
										 @AuthenticationPrincipal User user) {
		User user_1 = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		User user_2 = (User) ((Authentication) request.getUserPrincipal()).getPrincipal();

//		testService.test();

		return ResponseEntity.ok().body("Hello " + user.getUsername());
	}


	@GetMapping(value = "/")
	public ResponseEntity<List<UserDto>> getAllUsers() {
		List<com.example.entity.User> users = userRepository.findAll();
		return ResponseEntity.ok(userMapper.toUsersDto(users));
	}


	@GetMapping(value = "/{id}")
	public ResponseEntity<UserDto> getUser(WebRequest request, @PathVariable Long id) {
		request.checkNotModified("XxXxXx", Instant.now().minus(3, ChronoUnit.DAYS).toEpochMilli());

		com.example.entity.User user = userRepository.findById(id).orElseThrow();
		return ResponseEntity.ok(userMapper.toUserDto(user));
	}


	@PostMapping(value = "/create", consumes = MediaType.APPLICATION_JSON_VALUE)
	public UserDto create(@RequestBody @Valid UserDto dto) {
		com.example.entity.User user = userMapper.toEntity(dto);
		user = userRepository.save(user);
		return userMapper.toUserDto(user);
	}

}
