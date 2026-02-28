package com.example.dto;

import jakarta.persistence.Entity;

public record UserDTO (
		String username,
		String name,
		String email) {}
