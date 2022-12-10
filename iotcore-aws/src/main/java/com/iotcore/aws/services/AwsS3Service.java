/**
 * 
 */
package com.iotcore.aws.services;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iotcore.aws.AwsConfig;
import com.iotcore.aws.exception.AwsException;
import com.iotcore.aws.model.AwsEntity;
import com.iotcore.aws.model.lambda.trigger.S3EventTrigger;

import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3ClientBuilder;
import software.amazon.awssdk.services.s3.model.Delete;
import software.amazon.awssdk.services.s3.model.DeleteObjectsRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectsResponse;
import software.amazon.awssdk.services.s3.model.Event;
import software.amazon.awssdk.services.s3.model.FilterRule;
import software.amazon.awssdk.services.s3.model.GetBucketNotificationConfigurationRequest;
import software.amazon.awssdk.services.s3.model.GetBucketNotificationConfigurationResponse;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.LambdaFunctionConfiguration;
import software.amazon.awssdk.services.s3.model.ListObjectsRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsResponse;
import software.amazon.awssdk.services.s3.model.NotificationConfiguration;
import software.amazon.awssdk.services.s3.model.NotificationConfigurationFilter;
import software.amazon.awssdk.services.s3.model.ObjectIdentifier;
import software.amazon.awssdk.services.s3.model.PutBucketNotificationConfigurationRequest;
import software.amazon.awssdk.services.s3.model.PutBucketNotificationConfigurationResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.services.s3.model.S3KeyFilter;
import software.amazon.awssdk.services.s3.model.S3Object;

/**
 * 
 * @author <a href="mailto:garciadjx@gmail.com">J.M. Garcia</a>
 */
@Named
@SessionScoped
public class AwsS3Service extends AwsBase<S3Client> {

	private static final long serialVersionUID = 1520642074390617513L;
	private static final Logger LOG = LoggerFactory.getLogger(AwsS3Service.class);

	private static AwsS3Service instance = null;
	
	/**
	 * @return the instance
	 */
	public static AwsS3Service getInstance() {
		if (instance == null) {
			instance = new AwsS3Service();
		}
		return instance;
	}

	private String s3Bucket = null;
	
	/**
	 * 
	 */
	public AwsS3Service() {
		super();
	}

	/**
	 * @param credentials
	 */
	public AwsS3Service(AwsCredentialsProvider credentials) {
		super(credentials);
	}
	
	@Override
	public S3Client create() {
		S3ClientBuilder builder = S3Client.builder();
		if (getCredentialsProvider()!=null) {
			builder.credentialsProvider(getCredentialsProvider());
		}
		return builder.build();
	}



	/**
	 * @param trigger
	 * @param function
	 * @throws AwsException
	 * 
	 */
	public void createNotificationEvent(S3EventTrigger trigger, AwsEntity function) throws AwsException {

		GetBucketNotificationConfigurationResponse getCfgRes = null;
		try {
			getCfgRes = client().getBucketNotificationConfiguration(
					GetBucketNotificationConfigurationRequest.builder().bucket(getS3Bucket()).build()); 
		} catch (final AwsServiceException e) {
			LOG.error("Error: {}", e.getMessage());
			throw new AwsException(e);
		}
		
		final S3KeyFilter s3KeyFilter =  S3KeyFilter.builder().filterRules(
				FilterRule.builder().name("prefix").value(trigger.getFilterPrefix()).build(),
				FilterRule.builder().name("suffix").value(trigger.getFilterSuffix()).build()).build();
		
		// Create the list of LambdaConfigurations for this bucket
		List<LambdaFunctionConfiguration> lambdaConfigs = new ArrayList<LambdaFunctionConfiguration>();
		lambdaConfigs.add(LambdaFunctionConfiguration.builder()
				.filter(NotificationConfigurationFilter.builder().key(s3KeyFilter).build())
				.eventsWithStrings(trigger.getOperation())
				.lambdaFunctionArn(function.getArn())
				.build());
		
		// Add the existing configurations for other events
		for (LambdaFunctionConfiguration lbdCfg:getCfgRes.lambdaFunctionConfigurations()) {
			boolean addThis = true;
			if (lbdCfg.hasEvents()) {
				for (Event event:lbdCfg.events()) {
					if (event.toString().equals(trigger.getOperation())) {
						addThis = false;
					}
				}
			}
			if (addThis) {
				lambdaConfigs.add(lbdCfg);
			}
		}
	
		// Replace the existing configuration
		NotificationConfiguration config = NotificationConfiguration.builder()
			.lambdaFunctionConfigurations(lambdaConfigs)
			.topicConfigurations(getCfgRes.topicConfigurations())
			.queueConfigurations(getCfgRes.queueConfigurations())
			.build();
		
		PutBucketNotificationConfigurationResponse putCfgRes = client().putBucketNotificationConfiguration(
				PutBucketNotificationConfigurationRequest.builder()
					.bucket(getS3Bucket())
					.notificationConfiguration(config)
					.build());
		
		if (!responseOk(putCfgRes)) {
			throw new AwsException("");
		}

	}

	/**
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public InputStream getObject(String key) throws IOException {
		return getObject(s3Bucket, key);
	}


	/**
	 * Gets an S3 object from the specified bucket
	 * 
	 * @param bucket
	 * @param key
	 * @param progressListener
	 * @return
	 * @throws IOException
	 */
	public InputStream getObject(String bucket, String key) throws IOException {
		final GetObjectRequest req = GetObjectRequest.builder()
				.bucket(bucket)
				.key(key)
				.build();


		LOG.trace("Retrieving object {}/{}", bucket, key);

		ResponseInputStream<GetObjectResponse> res = client().getObject(req);

		if (res == null) {
			LOG.debug("Object {} not found in bucket {}", key, bucket);
			return null;
		}
		return res;
	}


	/**
	 * @param key
	 * @param inputStream
	 * @return
	 * @throws IOException
	 */
	public boolean putObject(String key, InputStream inputStream) throws IOException {
		return putObject(getS3Bucket(),key, inputStream);
	}

	/**
	 * @param bucket
	 * @param key
	 * @param content
	 * @return
	 */
	public boolean putObject(String bucket, String key, byte[] content) {
		PutObjectRequest putReq = PutObjectRequest.builder()
				.bucket(bucket)
				.key(key)
				.build();
		
		RequestBody reqBody = RequestBody.fromBytes(content);
		PutObjectResponse res = client().putObject(putReq, reqBody);
		return responseOk(res);
	}

	/**
	 * @param bucket
	 * @param key
	 * @param file
	 * @param progressListener
	 * @return
	 * @throws Exception
	 */
	public boolean putObject(String bucket, String key, File file) throws Exception {
		PutObjectRequest putReq = PutObjectRequest.builder()
				.bucket(bucket)
				.key(key)
				.build();
		
		RequestBody reqBody = RequestBody.fromFile(file);
		PutObjectResponse res = client().putObject(putReq, reqBody);
		return responseOk(res);
	}

	/**
	 * @param bucket
	 * @param key
	 * @param inputStream
	 * @return
	 * @throws IOException
	 */
	public boolean putObject(String bucket, String key, InputStream inputStream) throws IOException {
		PutObjectRequest putReq = PutObjectRequest.builder()
				.bucket(bucket)
				.key(key)
				.build();
		
		RequestBody reqBody = RequestBody.fromInputStream(inputStream, inputStream.available());
		PutObjectResponse res = client().putObject(putReq, reqBody);
		return responseOk(res);
	}

	/**
	 * @param bucket
	 * @param key
	 * @param inputStream
	 * @param size
	 * @param progressListener
	 * @return
	 * @throws IOException
	 */
	public boolean putObject(String bucket, String key, InputStream inputStream, Long size) throws IOException {
		PutObjectRequest putReq = PutObjectRequest.builder()
				.bucket(bucket)
				.key(key)
				.build();
		
		RequestBody reqBody = RequestBody.fromInputStream(inputStream, size);
		PutObjectResponse res = client().putObject(putReq, reqBody);
		return responseOk(res);
	}
	
	

	/**
	 * @param folder
	 */
	public void deleteFolder(String folder) {
		deleteFolder(this.getS3Bucket(), folder);
	}

	/**
	 * Delete a folder and its contained objects from S3 bucket
	 * 
	 * @param bucketName
	 * @param folder
	 * @return
	 */
	public int deleteFolder(String bucketName, String folder) {

		int successfulDeletes = 0;
		try {
			
			ListObjectsResponse listRes = client().listObjects(ListObjectsRequest.builder()
						.bucket(bucketName)
						.prefix(folder).build());
			
			final ArrayList<ObjectIdentifier> keys = new ArrayList<ObjectIdentifier>();
			for (S3Object obj : listRes.contents()) {
				keys.add(ObjectIdentifier.builder()
								.key(obj.key())
								.build());
			}

			if (keys.isEmpty()) {
				LOG.warn("No matching objects found. Nothing to delete");
				return 0;
			}

			// Delete the sample objects.
			final DeleteObjectsRequest multiObjectDeleteRequest = DeleteObjectsRequest.builder()
					.bucket(bucketName)
					.delete(Delete.builder().objects(keys).build())
					.build();

			DeleteObjectsResponse delObjRes = client().deleteObjects(multiObjectDeleteRequest);
			successfulDeletes = delObjRes.deleted().size();
		} catch (final AwsServiceException e) {
			LOG.error("{}", e.getMessage());
			e.printStackTrace();
		} catch (final SdkClientException e) {
			LOG.error("{}", e.getMessage());
			e.printStackTrace();
		}
		return successfulDeletes;
	}

	/**
	 * Delete objects from S3 bucket
	 * 
	 * @param bucket
	 * @param key
	 */
	public void deleteObjects(String bucket, String... keys) {
		
		ArrayList<ObjectIdentifier> objIds = new ArrayList<ObjectIdentifier>();
		for (String k:keys) {
			objIds.add(ObjectIdentifier.builder().key(k).build());
		}
		
		final DeleteObjectsRequest req = DeleteObjectsRequest.builder()
				.bucket(bucket)
				.delete(Delete.builder().objects(objIds).build())
				.build();

		client().deleteObjects(req);
	}


	/**
	 * @param bucket
	 * @return
	 */
	public List<LambdaFunctionConfiguration> getLambdaFunctionConfigurations(String bucket) {
		GetBucketNotificationConfigurationResponse res = client().getBucketNotificationConfiguration(
				GetBucketNotificationConfigurationRequest.builder()
					.bucket(bucket).build());
		
		return res.lambdaFunctionConfigurations();
		
	}


	/**
	 * @return the s3Bucket
	 */
	public String getS3Bucket() {
		if (s3Bucket == null) {
			s3Bucket = AwsConfig.getInstance().getPlatformBucket();
		}
		return s3Bucket;
	}



	/**
	 * @param s3Bucket the s3Bucket to set
	 */
	public void setS3Bucket(String s3Bucket) {
		this.s3Bucket = s3Bucket;
	}

}
