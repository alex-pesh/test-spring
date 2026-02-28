package com.example.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

public class CheckCaseValidator implements ConstraintValidator<CheckCase, String> {

	@Autowired
	ApplicationContext  applicationContext;


	private CheckCase.CaseMode caseMode;


	@Override
	public void initialize(CheckCase annotation) {
		ConstraintValidator.super.initialize(annotation);
		this.caseMode = annotation.value();
	}

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		if (value == null) {
			return true;
		}

		return switch (caseMode) {
			case LOWER -> value.equals(value.toLowerCase());
			case UPPER -> value.equals(value.toUpperCase());
		};
	}

}
