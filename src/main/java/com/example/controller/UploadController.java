package com.example.controller;


import com.example.controller.support.upload.NioChannelUploadHandler;
import com.example.controller.support.upload.UploadHandler;
import com.example.dto.UploadedFileStream;
import com.example.service.StreamingUploadService;
import jakarta.servlet.AsyncContext;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import javax.naming.ServiceUnavailableException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.CompletableFuture;

import static java.nio.file.StandardOpenOption.*;

@Slf4j
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class UploadController {

	private final StreamingUploadService uploadService;


	/*
		curl -X POST http://localhost:8080/upload-stream \
				-H "Content-Type: application/octet-stream" \
				-H "X-Filename: test.zip" \
				--data-binary @test.zip
	*/

	@PostMapping(value = "/upload-stream", consumes = MediaType.APPLICATION_OCTET_STREAM_VALUE)
	public ResponseEntity<Void> upload(
			HttpServletRequest request,
			@RequestHeader("X-Filename") String filename,
			@RequestHeader(value = "Content-Type", required = false) String contentType ) throws IOException {

		InputStream inputStream = request.getInputStream();

		UploadedFileStream file = new UploadedFileStream(
				filename,
				contentType == null ? MediaType.APPLICATION_OCTET_STREAM_VALUE : contentType,
				inputStream
		);

		uploadService.handle(file);

		return ResponseEntity.ok().build();
	}


	@PostMapping("/upload-nio")
	public void upload(HttpServletRequest req, HttpServletResponse resp,
					   @RequestHeader("X-Filename") String filename) throws IOException {

		req.setAttribute("org.apache.catalina.ASYNC_SUPPORTED", true);

		AsyncContext asyncContext = req.startAsync();
		ServletInputStream input = req.getInputStream();

		UploadHandler handler = new NioChannelUploadHandler(filename, asyncContext);

		input.setReadListener(handler);
	}


	@PostMapping("/upload/{filename:.+}")
	public CompletableFuture<ResponseEntity<?>> upload(
			HttpServletRequest request,
			@PathVariable("filename") String filename) throws ServiceUnavailableException {

		final int MAX_BUFFER_SIZE = 1024 * 128;

		// TODO: validate 'filename' to ensure it's legal and will be written where you want it
		// to be within the file system. Watch out for the many security gotchas.


		return CompletableFuture.supplyAsync(() -> {

			try {

				// TODO: Change this to where you want the file to be written
				Path file = Paths.get(filename);

				try (ReadableByteChannel inChannel = Channels.newChannel(request.getInputStream())) {
					try (WritableByteChannel outChannel = Files.newByteChannel(file, CREATE, TRUNCATE_EXISTING, WRITE)) {

						ByteBuffer buffer = ByteBuffer.allocateDirect(MAX_BUFFER_SIZE);

						while (inChannel.read(buffer) != -1) {
							buffer.flip();
							outChannel.write(buffer);
							buffer.compact();
						}

						// EOF will leave buffer in fill state, flip it and write anything remaining

						buffer.flip();

						while (buffer.hasRemaining()) {
							outChannel.write(buffer);
						}
					}
				}
			} catch (IOException ex) {
				throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to upload the file", ex);
			}

			return ResponseEntity.ok().build();
		});
	}





	@GetMapping("/download")
	public StreamingResponseBody handle() {
		return new StreamingResponseBody() {
			@Override
			public void writeTo(OutputStream outputStream) throws IOException {
				outputStream.write("DATA".getBytes(StandardCharsets.UTF_8));
			}
		};
	}

}
