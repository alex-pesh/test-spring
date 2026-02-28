package com.example.controller.support.upload;

import jakarta.servlet.AsyncContext;
import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


/**
 * <b>Zero-Copy Streaming FileChannel → FileChannel</b>
 * <li>Данные читаются non-blocking.
 * <li>Пишутся в FileChannel через NIO.
 * <li>Подходит для отправки больших файлов в хранилище через zero-copy (transferTo / transferFrom) —
 * не происходит копирование данных из буфера kernel space в буфер user space.
 * <li>Используется прямой off-heap буфер, не грузит GC
 * <p>
 * <li>{@code FileChannel} работает эффективно для больших файлов, используя системные вызовы для прямого копирования файла в сокет
 * в пространстве ядра без дополнительных операций чтения/запсиси между буферами устройств в kernel- и user-space.
 */
@Slf4j
public class NioChannelUploadHandler implements UploadHandler, ReadListener {

	private final AsyncContext async;
	private final FileChannel outChannel;
	private final ByteBuffer buffer = ByteBuffer.allocateDirect(1024);
	private final ExecutorService fileWriteExecutor = Executors.newSingleThreadExecutor();


	public NioChannelUploadHandler(String fileName, AsyncContext async) throws IOException {
		this.async = async;

		Path target = Path.of("/tmp/uploads/" + fileName);
		Files.createDirectories(target.getParent());

		outChannel = FileChannel.open(target,
				StandardOpenOption.CREATE,
				StandardOpenOption.WRITE,
				StandardOpenOption.TRUNCATE_EXISTING);
	}


	@Override
	public void onDataAvailable() throws IOException {
		long start = System.nanoTime();

		ServletInputStream inputStream = async.getRequest().getInputStream();

		ReadableByteChannel inputChannel = Channels.newChannel(inputStream);

		while (inputStream.isReady()) {
			// NIO метод read() пишет данные напрямую в Direct ByteBuffer
			int bytesRead = inputChannel.read(buffer);

			if (bytesRead == -1) {
				break;
			}

			buffer.flip(); // Подготавливаем буфер к чтению/записи: position -> 0, limit -> текущая позиция

			final ByteBuffer finalBuffer = buffer.duplicate();

			while (finalBuffer.hasRemaining()) {
				outChannel.write(finalBuffer);
			}

/*
			fileWriteExecutor.submit(() -> {
				try {
					while (finalBuffer.hasRemaining()) {
						outChannel.write(finalBuffer);
					}
				} catch (IOException e) {
					onError(e); // Обработка ошибок записи
				}
			});*/

			buffer.clear();
		}

		long end = System.nanoTime();
		System.out.println("Taken time (ms): " + (end - start)/1000000);
	}


	@Override
	public void onAllDataRead() throws IOException {
		fileWriteExecutor.shutdown();
		outChannel.force(true);
		outChannel.close();

		async.getResponse().getWriter().write("UPLOAD OK");
		async.complete();
	}


	@Override
	public void onError(Throwable t) {
		try {
			outChannel.close();
		} catch (Exception ignored) {
		}

		async.complete();

		log.error("Failed to receive file", t);

	}

}
