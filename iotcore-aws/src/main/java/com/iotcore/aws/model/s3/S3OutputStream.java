package com.iotcore.aws.model.s3;

import java.io.ByteArrayInputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.AbortMultipartUploadRequest;
import software.amazon.awssdk.services.s3.model.CompleteMultipartUploadRequest;
import software.amazon.awssdk.services.s3.model.CreateMultipartUploadRequest;
import software.amazon.awssdk.services.s3.model.CreateMultipartUploadResponse;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.UploadPartRequest;
import software.amazon.awssdk.services.s3.model.UploadPartResponse;


public class S3OutputStream extends OutputStream {

	/** Default chunk size is 10MB */
	protected static final int BUFFER_SIZE = 10000000;

	/** The bucket-name on Amazon S3 */
	private final String bucket;

	/** The path (key) name within the bucket */
	private final String path;

	/** The temporary buffer used for storing the chunks */
	private final byte[] buf;

	/** The position in the buffer */
	private int position;

	/** Amazon S3 client. TODO: support KMS */
	private final S3Client s3Client;

	/** The unique id for this upload */
	private String uploadId;

	/** Collection of the etags for the parts that have been uploaded */
	private final List<String> etags;

	/** indicates whether the stream is still open / valid */
	private boolean open;

	/**
	 * Creates a new S3 OutputStream
	 * 
	 * @param s3Client the AmazonS3 client
	 * @param bucket   name of the bucket
	 * @param path     path within the bucket
	 */
	public S3OutputStream(S3Client s3Client, String bucket, String path) {
		this.s3Client = s3Client;
		this.bucket = bucket;
		this.path = path;
		this.buf = new byte[BUFFER_SIZE];
		this.position = 0;
		this.etags = new ArrayList<String>();
		this.open = true;
	}

	private void assertOpen() {
		if (!this.open) {
			throw new IllegalStateException("Closed");
		}
	}

	public void cancel() {
		this.open = false;
		if (this.uploadId != null) {
			this.s3Client.abortMultipartUpload(AbortMultipartUploadRequest.builder()
					.bucket(bucket)
					.uploadId(uploadId)
					.key(path)
					.build());
		}
	}

	@Override
	public void close() {
		if (this.open) {
			this.open = false;
			if (this.uploadId != null) {
				if (this.position > 0) {
					uploadPart();
				}
				this.s3Client
						.completeMultipartUpload(CompleteMultipartUploadRequest.builder()
								.bucket(bucket)
								.uploadId(uploadId)
								.key(path)
								.build());
			} 
		}
	}

	/**
	 * Flushes the buffer by uploading a part to S3.
	 */
	@Override
	public synchronized void flush() {
		this.assertOpen();
	}

	protected void flushBufferAndRewind() {
		if (uploadId == null) {
			final CreateMultipartUploadRequest request = CreateMultipartUploadRequest.builder()
					.bucket(bucket)
					.key(path)
					.acl( ObjectCannedACL.BUCKET_OWNER_FULL_CONTROL)
					.build();
					
			final CreateMultipartUploadResponse initResponse = s3Client.createMultipartUpload(request);
			this.uploadId = initResponse.uploadId();
		}
		uploadPart();
		this.position = 0;
	}

	protected void uploadPart() {
		
		final UploadPartRequest req = UploadPartRequest.builder()
				.bucket(this.bucket)
				.key(this.path)
				.uploadId(this.uploadId)
				.partNumber(this.etags.size() + 1)
				.build();
		
		RequestBody reqBody = RequestBody.fromInputStream(new ByteArrayInputStream(buf, 0, this.position), this.position+1);
		
		
		final UploadPartResponse uploadResult = this.s3Client.uploadPart(req, reqBody);
		
		this.etags.add(uploadResult.eTag());
	}

	/**
	 * Write an array to the S3 output stream.
	 *
	 * @param b the byte-array to append
	 */
	@Override
	public void write(byte[] b) {
		write(b, 0, b.length);
	}

	/**
	 * Writes an array to the S3 Output Stream
	 *
	 * @param byteArray the array to write
	 * @param o         the offset into the array
	 * @param l         the number of bytes to write
	 */
	@Override
	public void write(final byte[] byteArray, final int o, final int l) {
		this.assertOpen();
		int ofs = o, len = l;
		int size;
		while (len > (size = this.buf.length - position)) {
			System.arraycopy(byteArray, ofs, this.buf, this.position, size);
			this.position += size;
			flushBufferAndRewind();
			ofs += size;
			len -= size;
		}
		System.arraycopy(byteArray, ofs, this.buf, this.position, len);
		this.position += len;
	}

	@Override
	public void write(int b) {
		this.assertOpen();
		if (position >= this.buf.length) {
			flushBufferAndRewind();
		}
		this.buf[position++] = (byte) b;
	}
}