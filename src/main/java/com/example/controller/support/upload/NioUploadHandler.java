package com.example.controller.support.upload;

import jakarta.servlet.AsyncContext;
import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

/**
 * Servlet NIO (ReadListener) — неблокирующий upload
 */
public class NioUploadHandler implements UploadHandler, ReadListener {

	private final AsyncContext async;
	private final OutputStream fileOut;


	public NioUploadHandler(AsyncContext async) {
		this.async = async;

		try {
			Path path = Path.of("/data/uploads/upload.bin");
			this.fileOut = Files.newOutputStream(path, StandardOpenOption.CREATE);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}


	@Override
	public void onDataAvailable() throws IOException {
		ServletInputStream input = async.getRequest().getInputStream();

		byte[] buffer = new byte[8192];

		// Non-blocking read
		while (input.isReady() && input.read(buffer) != -1) {
			int len = input.read(buffer);
			if (len > 0) {
				fileOut.write(buffer, 0, len);
			}
		}
	}


	@Override
	public void onAllDataRead() throws IOException {
		fileOut.flush();
		fileOut.close();

		async.getResponse().getWriter().write("OK");
		async.complete();
	}


	@Override
	public void onError(Throwable t) {
		try { fileOut.close(); } catch (IOException ignored) {}
		async.complete();
	}

}

