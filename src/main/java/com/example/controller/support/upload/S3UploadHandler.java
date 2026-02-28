package com.example.controller.support.upload;

import jakarta.servlet.AsyncContext;
import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import org.springframework.http.ETag;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CompleteMultipartUploadRequest;
import software.amazon.awssdk.services.s3.model.CreateMultipartUploadResponse;
import software.amazon.awssdk.services.s3.model.NoSuchBucketException;
import software.amazon.awssdk.services.s3.model.UploadPartRequest;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

public class S3UploadHandler implements UploadHandler, ReadListener {

	private final AsyncContext async;


	public S3UploadHandler(AsyncContext async) {
		this.async = async;
	}


	@Override
	public void onDataAvailable() throws IOException {

		ServletInputStream input = async.getRequest().getInputStream();

		String regionName = "<AWS Region>";
		String accessKey = "<AWS Access Key>";
		String secretKey = "<AWS Secret Key>";
		String bucketName = "bucket";

		AwsCredentials credentials = AwsBasicCredentials.create(accessKey, secretKey);

		S3Client s3 = S3Client
				.builder()
				.region(Region.of(regionName))
				.credentialsProvider(StaticCredentialsProvider.create(credentials))
				.build();


//		s3.createBucket(request -> request.bucket(bucket));

		s3.headBucket(request -> request.bucket(bucketName));


		String uploadId = s3.createMultipartUpload(request -> request.bucket(bucketName)).uploadId();
		List<String> parts = new ArrayList<>();

		int buffSize = 8 * 1024 * 1024;  // 8MB chunk
		byte[] buffer = new byte[buffSize];
		int partNumber = 1;
		long lenght;

		ByteBuffer byteBuffer = ByteBuffer.allocateDirect(buffSize);


		while ((lenght = input.read(buffer)) != -1) {
			UploadPartRequest request = UploadPartRequest.builder()
					.bucket(bucketName)
					.key(accessKey)
					.uploadId(uploadId)
					.partNumber(partNumber++)
					.contentLength(lenght)
					.build();

			ByteArrayInputStream bais = new ByteArrayInputStream(buffer, 0, buffer.length);
			RequestBody body = RequestBody.fromInputStream(bais, lenght);

			parts.add(s3.uploadPart(request, body).eTag());
		}

		s3.completeMultipartUpload(request -> request
				.bucket(bucketName)
				.key(accessKey)
				.uploadId(uploadId)
		);

	}


	@Override
	public void onAllDataRead() throws IOException {

		async.getResponse().getWriter().write("OK");
		async.complete();
	}


	@Override
	public void onError(Throwable t) {
		async.complete();
	}


}