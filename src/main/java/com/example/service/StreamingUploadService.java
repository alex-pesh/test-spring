package com.example.service;

import com.example.dto.UploadedFileStream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

@Slf4j
@Service
public class StreamingUploadService {


	public void handle(UploadedFileStream file) {

		try (InputStream in = file.stream()) {
			Path target = Path.of("/tmp/uploads/" + file.filename());
			Files.createDirectories(target.getParent());
			Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);

			log.info("File is copied to {}", target);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}
