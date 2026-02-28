package com.example.controller.support.upload;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.io.IOException;


public class ZeroCopySocketTransfer {

	public static void transfer(SocketChannel source, SocketChannel destination) throws IOException {

		ByteBuffer buffer = ByteBuffer.allocateDirect(8192);
		int bytesRead;

		while ((bytesRead = source.read(buffer)) != -1) {
			buffer.flip(); // Prepare buffer for writing
			while (buffer.hasRemaining()) {
				destination.write(buffer);
			}
			buffer.clear(); // Prepare buffer for reading
		}

	}

}