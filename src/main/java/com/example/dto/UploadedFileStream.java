package com.example.dto;

import java.io.InputStream;

public record UploadedFileStream(
		String filename,
		String contentType,
		InputStream stream
) {}
