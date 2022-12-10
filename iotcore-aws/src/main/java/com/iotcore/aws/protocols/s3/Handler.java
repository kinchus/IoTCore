package com.iotcore.aws.protocols.s3;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

import com.iotcore.aws.model.s3.S3OutputStream;
import com.iotcore.aws.services.AwsS3Service;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;

/**
 * The Class Handler.
 */
public class Handler extends URLStreamHandler {

	public static final String S3_PROTOCOL = "s3";
	
	private AwsS3Service s3Service = null;

	private AwsCredentialsProvider credentialsProvider = null;

	/**
	 * Instantiates a new s 3 url handler.
	 */
	public Handler() {
		super();
	}

	/**
	 * 
	 *
	 * @param s3Service the s 3 service
	 */
	public Handler(AwsS3Service s3Service) {
		setS3Service(s3Service);
	}

	/**
	 * Gets the s 3 service.
	 *
	 * @return the s 3 service
	 */
	public AwsS3Service getS3Service() {
		if (s3Service == null) {
			if (credentialsProvider != null) {
				s3Service = new AwsS3Service(credentialsProvider);
			} else {
				s3Service = AwsS3Service.getInstance();
			}
		}
		return s3Service;
	}

	/**
	 * @param url
	 * @return
	 * @throws IOException
	 * @see java.net.URLStreamHandler#openConnection(java.net.URL)
	 */
	@Override
	public URLConnection openConnection(URL url) throws IOException {

		return new URLConnection(url) {

			@Override
			public void connect() throws IOException {
			}

			@Override
			public String getContentType() {
				return guessContentTypeFromName(url.getFile());
			}

			@Override
			public InputStream getInputStream() throws IOException {
				initS3Credentials();
				String buket = s3Bucket();
				String objKey = s3ObjectKey();
				return getS3Service().getObject(buket, objKey);
			}

			@Override
			public OutputStream getOutputStream() throws IOException {
				initS3Credentials();
				return new S3OutputStream(getS3Service().client(), s3Bucket(), s3ObjectKey());
			}

			private void initS3Credentials() {

				if (url.getUserInfo() != null) {
					String accessKey = null;
					String secretKey = null;
					final String[] credentials = url.getUserInfo().split("[:]");
					if ((credentials.length > 0)) {
						accessKey = credentials[0];
					}
					if ((credentials.length > 1)) {
						secretKey = credentials[1];
					}
					final AwsCredentials awsCredentials =  AwsBasicCredentials.create(accessKey, secretKey);
					credentialsProvider =  StaticCredentialsProvider.create(awsCredentials);
				} else if (credentialsProvider == null) {
					// Get default credentials provider
					credentialsProvider = DefaultCredentialsProvider.builder().build();
				}
			}

			private String s3Bucket() {
				return url.getHost();
			}

			private String s3ObjectKey() {
				return url.getPath().substring(1);
			}
		};
	}

	/**
	 * Sets the s 3 service.
	 *
	 * @param s3Service the new s 3 service
	 */
	public void setS3Service(AwsS3Service s3Service) {
		this.s3Service = s3Service;
	}

}