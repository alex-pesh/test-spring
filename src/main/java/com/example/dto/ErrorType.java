package com.example.dto;

import jakarta.validation.Payload;

public enum ErrorType implements Payload {

	INFO,
	WARNING,
	ERROR

}
