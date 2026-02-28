package com.example.validation;

import jakarta.validation.Payload;

public class Severity {
	public interface Info extends Payload {};
	public interface Warning extends Payload {};
	public interface Error extends Payload {};
}
