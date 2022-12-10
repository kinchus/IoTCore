/**
 * 
 */
package com.iotcore.aws.backend;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iotcore.aws.AwsConfig;
import com.iotcore.aws.protocols.s3.S3UrlStreamHandlerFactory;
import com.iotcore.aws.services.AwsServiceFactory;
import com.iotcore.core.backend.BackendConfiguration;


/**
 * @author <a href="mailto:garciadjx@gmail.com">J.M. Garcia</a>
 *
 */
@ApplicationScoped
public class AwsBackendConfiguration extends BackendConfiguration {

	private static final long serialVersionUID = -3511898267041831206L;
	private static Logger LOG = LoggerFactory.getLogger(AwsBackendConfiguration.class);
	
	/** AWS */
	public static final String KEY_CONFIG_AWS = "config/aws";
	public static final String AWS_REGION = "config/aws/region";
	public static final String AWS_ACCOUNT_ID = "config/aws/account_id";
	public static final String AWS_COGNITO_POOLID = "config/" +  AwsConfig.AWS_COGNITO_USERPOOLID.replace('.', '/');
	public static final String AWS_COGNITO_CLIENTID = "config/" +  AwsConfig.AWS_COGNITO_POOLCLIENTID.replace('.', '/');
	public static final String AWS_IOT_ENDPOINT = "config/" + AwsConfig.AWS_IoT_ENDPOINT.replace('.', '/');
	public static final String AWS_SQS_MESSAGES_QUEUE = "config/aws/sqs_messages_queue";

	
	/**
	 * @return
	 */
	public static AwsBackendConfiguration getInstance() {
		if (_instance == null) {
			LOG.debug("Initializing instance");
			if (!S3UrlStreamHandlerFactory.isRegistered()) {
				try {
					LOG.debug("Registering S3 protocol URL handler");
					S3UrlStreamHandlerFactory.register();	
				} catch (Exception e) {
					LOG.error("Exception thrown registering Handler: {}", e.getMessage());
					e.printStackTrace();
				}
			}
			else {
				LOG.trace("S3 prtocol URL handler already registered");	
			}
			
			_instance = new AwsBackendConfiguration();
			_instance.init();
		}
		return (AwsBackendConfiguration)_instance;
	}

	

	/**
	 * 
	 */
	public AwsBackendConfiguration() {
		super();
	}
	

	public String getSnsMessagesTopicName() {
		return getPlatformName() + "-messages";
	}
	
	public String getSqsMessagesQueueName() {
		return get(AWS_SQS_MESSAGES_QUEUE);
	}
	
	/**
	 * Read the configuration defined ixn the system var
	 * 
	 */
	@Override
	@PostConstruct
	public synchronized void init() {

		AwsBackendConfiguration._instance = this;
		
		if (initialized) {
			return;
		}
		
		super.init();
		
		String region = config.get(AwsBackendConfiguration.AWS_REGION);
		String accountId = config.get(AwsBackendConfiguration.AWS_ACCOUNT_ID);
		String platformName = config.get(AwsBackendConfiguration.PLATFORM_NAME);
		String cognitoPoolId =  config.get(AwsBackendConfiguration.AWS_COGNITO_POOLID);
		String cognitoClientId = config.get(AwsBackendConfiguration.AWS_COGNITO_CLIENTID); 
		String iotEndpoint = config.get(AwsBackendConfiguration.AWS_IOT_ENDPOINT); 
		String sqsMessagesQueue = config.get(AwsBackendConfiguration.AWS_SQS_MESSAGES_QUEUE); 
	
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(AwsConfig.AWS_REGION, region);
		params.put(AwsConfig.AWS_ACCOUNT_ID, accountId);
		params.put(AwsConfig.AWS_PLATFORM_NAME, platformName);
		params.put(AwsConfig.AWS_COGNITO_USERPOOLID, cognitoPoolId);
		params.put(AwsConfig.AWS_COGNITO_POOLCLIENTID, cognitoClientId);
		params.put(AwsConfig.AWS_IoT_ENDPOINT, iotEndpoint );
		params.put(AwsConfig.AWS_SQS_MESSAGES_QUEUE, sqsMessagesQueue );
		
		if(LOG.isTraceEnabled()) {
			LOG.trace("Setting AWS Configuration properties");
		}
		AwsConfig.getInstance().loadProperties(params);
				
		initialized = true;
	}
	
	
	/**
	 * TODO Remove ths method leaving the parent implementation to do the job
	 * using the S3InputStream class (Available after registering Handler ) 
	 */
	@Override
	public void update() throws IOException {

		InputStream istream = config.getInputStream();		
//		byte[] buf = istream.readAllBytes();
//		istream.close();
//		
//		OutputStream ostream = this.getResourceUrl().toURL().openConnection().getOutputStream();
//		ostream.write(buf);
//		LOG.debug("Writing configuration resource {}: OK ({} bytes)", resourceUrl.toString(), buf.length);
//		LOG.trace("File contents:\n{}", new String(buf));
//		ostream.close();
		
		String s3Bucket = getS3Bucket();
		String s3Key = getS3ObjectKey();
		LOG.trace("Updating S3 configuration resource [BUCKET={}, KEY={}]", s3Bucket, s3Key);
		AwsServiceFactory.getS3Service().putObject(s3Bucket, s3Key, istream);
		
	}
	
	public String getPlatformLocation() {
		return getS3Bucket();
	}

	private String getS3Bucket() {
		return getResourceUrl().getHost();
	}

	private String getS3ObjectKey() {
		return getResourceUrl().getPath().substring(1);
	}

}
