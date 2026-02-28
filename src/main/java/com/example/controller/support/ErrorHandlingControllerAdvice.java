package com.example.controller.support;


import jakarta.validation.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;

import static com.example.controller.support.ValidationErrorResponse.Violation;

@RestControllerAdvice
public class ErrorHandlingControllerAdvice {

	@Autowired
	private ConversionService conversionService;


	/**
	 * ConstraintViolationException выбрасывается при нарушении ограничений на уровне параметров
	 */
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(ConstraintViolationException.class)
	public ValidationErrorResponse onConstraintValidationException(ConstraintViolationException e) {
		final List<Violation> violations =
				e.getConstraintViolations().stream()
				.map(violation -> new Violation(
								violation.getPropertyPath().toString(),
								violation.getMessage() ))
				.collect(Collectors.toList());

		return new ValidationErrorResponse(violations);
	}


	/**
	 * MethodArgumentNotValidException выбрасывается при нарушении ограничений на уровне тела запроса
	 */
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ValidationErrorResponse onMethodArgumentNotValidException(MethodArgumentNotValidException e) {
		final List<Violation> violations = e.getBindingResult().getFieldErrors().stream()
				.map(error -> {
//					Object payload = error.unwrap(ConstraintViolation.class).getConstraintDescriptor().getPayload().stream().findFirst().orElse(null);
					return new Violation(error.getField(), error.getDefaultMessage());
				})
				.collect(Collectors.toList());

		return new ValidationErrorResponse(violations);
	}

}
