package com.example.dto;

import com.example.entity.User;
import com.example.validation.CheckCase;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * DTO for {@link User}
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record UserDto(
		Long id,
		@NotBlank(message = "Поле 'username' не может быть пустым")
		@CheckCase(value = CheckCase.CaseMode.LOWER, message = "Значение поля 'username' должно быть в нижнем регистре")
		String username,
		String name,
		@Email(message = "Некорректный формат 'email'")
		String email) {
}