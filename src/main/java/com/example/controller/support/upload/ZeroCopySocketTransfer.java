package com.example.controller.support.upload;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.StandardProtocolFamily;
import java.net.UnixDomainSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.locks.LockSupport;


@Slf4j
public class ZeroCopySocketTransfer {

	private static final String SOCKET_PATH = "/tmp/java.sock";
	private static final int BUFF_SIZE = 8192;


	/* 	Для проверки нативной памяти:
	 *	Запустить любое  приложение с флагом -XX:NativeMemoryTracking=detail
	 * и выполнить jcmd <pid> VM.native_memory
	 * Прямые буферы, созданные через ByteBuffer.allocateDirect(), внутри вызывают Unsafe.allocateMemory().
	 * NMT относит такие аллокации к категории Internal (в некоторых версиях JDK — к Other)
	 */

	public static void transfer(ReadableByteChannel source, WritableByteChannel destination) throws IOException {

		ByteBuffer buffer = ByteBuffer.allocateDirect(BUFF_SIZE);
		int bytesRead;

		while ((bytesRead = source.read(buffer)) != -1) {
			buffer.flip(); // Prepare buffer for writing
			while (buffer.hasRemaining()) {
				destination.write(buffer);
			}
			buffer.clear(); // Prepare buffer for reading
		}

		LockSupport.parkNanos(120_000_000_000L);
	}


	/*
	 * Создать серверный unix-сокет:
	 * socket -s /tmp/java.sock
	 * или
	 * nc -lU /tmp/java.sock
	*/
	private static void runServer() throws IOException {
		var address = UnixDomainSocketAddress.of(SOCKET_PATH);
		try (var server = ServerSocketChannel.open(StandardProtocolFamily.UNIX)) {
			server.bind(address);
			log.info("Server is started on {}", address);
			try (var channel = server.accept()) {
				var buffer = ByteBuffer.allocateDirect(BUFF_SIZE);
				channel.read(buffer);
				buffer.flip();
				log.info("Received {} bytes", buffer.limit());
			}
		}
	}


	private static void writeText(String text) throws IOException {
		var address = UnixDomainSocketAddress.of(SOCKET_PATH);
		try (var channel = SocketChannel.open(StandardProtocolFamily.UNIX)) {
			channel.connect(address);
			channel.write(ByteBuffer.wrap(text.getBytes()));
		}
	}


	private static void writeFile(String path) throws IOException {
		var address = UnixDomainSocketAddress.of(SOCKET_PATH);
		var fileChannel = FileChannel.open(Path.of(path), StandardOpenOption.READ);

		try (var socketChannel = SocketChannel.open(StandardProtocolFamily.UNIX)) {
			socketChannel.connect(address);
			transfer(fileChannel, socketChannel);
		}
	}



	public static void main(String[] args) throws IOException {

		writeFile("/home/alexander/workspace/project/test-spring/dep.txt");
		writeText("Hello!\n");

	}

}