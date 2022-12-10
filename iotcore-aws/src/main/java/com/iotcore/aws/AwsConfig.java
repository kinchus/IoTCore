/**
 * 
 */
package com.iotcore.aws;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iotcore.core.util.BaseConfig;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentials;


/**
 * Class for configuration the AWS SDK
 *
 * @author <a href="mailto:garciadjx@gmail.com">J.M. Garcia</a>
 */
public class AwsConfig extends BaseConfig {

	private static final Logger LOG = LoggerFactory.getLogger(AwsConfig.class);

	
	/** Config. property: AWS_PROPS_FILE. */
	public static final String AWS_PROPS_FILE = "aws.properties";
	
	/** Default value for  AWS_REGION. */
	public static final String AWS_REGION_DEFAULT = "eu-west-2";
	
	/** Config. property: AWS_REGION. */
	public static final String AWS_PLATFORM_NAME = "aws.name";
	/** Config. property: AWS_REGION. */
	public static final String AWS_REGION = "aws.region";
	/** Config. property: AWS_PROFILE. */
	public static final String AWS_PROFILE = "aws.profile";
	/** Config. property: AWS_ACCOUNT_ID. */
	public static final String AWS_ACCOUNT_ID = "aws.account_id";
	/** Config. property: AWS_KEY_ID. */
	public static final String AWS_KEY_ID = "aws.key_id";
	/** AWS_KEY_SECRET. */
	public static final String AWS_KEY_SECRET = "aws.key_secret";
	/** AWS_IoT_ENDPOINT. */
	public static final String AWS_IoT_ENDPOINT = "aws.iot.mqtt_endpoint";
	/** AWS_IoT_ENDPOINT. */
	public static final String AWS_SQS_MESSAGES_QUEUE = "aws.sqs_messages_queue";
	/** Config. property: AWS_COGNITO_USERPOOLID. */
	public static final String AWS_COGNITO_USERPOOLID = "aws.cognito.pool_id";
	/** Config. property: AWS_COGNITO_POOLCLIENTID. */
	public static final String AWS_COGNITO_POOLCLIENTID = "aws.cognito.pool_client_id";

	/** Config. property: AWS_S3_BUCKET. */
	public static final String AWS_S3_BUCKET = "aws.s3.backend_bucket";

	/** LAMBDA_FUNCTION_VAR. */
	private static final String LAMBDA_FUNCTION_VAR = "AWS_LAMBDA_FUNCTION_NAME";

	/** instance. */
	private static AwsConfig instance = null;

	private static String region = null;
	private static String accountId = null;
	private static String platformName = null;
	private static String platformBucket = null;

	/**
	 * Gets the single instance of AwsConfig.
	 *
	 * @return single instance of AwsConfig
	 */
	public static AwsConfig getInstance() {
		if (instance == null) {
			instance = new AwsConfig();
		}
		return instance;
	}
	

	/** The properties. */
	private String profile = null;
	private Boolean isEC2Instance = null;
	private Boolean isLambdaFunction = null;
	private String lambdaFunctionName = null;
	private AwsCredentials credentials = null;

	/**
	 * Instantiates a new AWS config.
	 */
	private AwsConfig() {
		super(AWS_PROPS_FILE);
	}
	


	@Override
	public void loadProperties(Map<String, Object> map) {
		region = null;
		accountId = null;
		platformName = null;
		platformBucket = null;
		super.loadProperties(map);
	}
	
	



	/**
	 * Gets the account id.
	 *
	 * @return the account id
	 */
	public String getAccountId() {
		if (accountId == null) {
			// accountId = getProperty(AWS_ACCOUNT_ID, DEFAULT_ACCOUNT_ID);
			accountId = getProperty(AWS_ACCOUNT_ID);
		}
		return accountId;
	}

	/**
	 * Retrieve the AWS account credentials set in configuration.
	 *
	 * @return the credentials
	 */
	public AwsCredentials getCredentials() {

		if (credentials == null) {
			final String keyId = getProperty(AwsConfig.AWS_KEY_ID);
			final String secret = getProperty(AwsConfig.AWS_KEY_SECRET);

			if (keyId != null) {
				LOG.debug("Get credentials from specified key");
				credentials =  AwsBasicCredentials.create(keyId, secret);
			}
		}

		return credentials;

	}

	/**
	 * Gets the lambda function name.
	 *
	 * @return the lambdaFunctionName
	 */
	public String getLambdaFunctionName() {
		return lambdaFunctionName;
	}


	/**
	 * Gets the account id.
	 *
	 * @return the account id
	 */
	public String getPlatformName() {
		if (platformName == null) {
			platformName = getProperty(AWS_PLATFORM_NAME);
		}
		return platformName;
	}
	
	
	/**
	 * Gets the region.
	 *
	 * @return the region
	 */
	public String getRegion() {
		if (region == null) {
			region = getProperty(AWS_REGION, AWS_REGION_DEFAULT);
		}

		return region;
	}
	

	
	/**
	 * @return
	 */
	public String getPlatformBucket() {
		if (platformBucket == null) {
			final StringBuilder bucket = new StringBuilder();
			bucket.append(getPlatformName());
			bucket.append(".");
			bucket.append(getRegion());
			platformBucket = bucket.toString().toLowerCase();
		}
		return platformBucket;
	}


	/**
	 * Gets the profile.
	 *
	 * @return the profile
	 */
	public String getProfile() {
		if (profile == null) {
			profile = getProperty(AWS_PROFILE);
		}
		return profile;
	}
	


	/**
	 * @param awsRegion
	 */
	public void setRegion(String awsRegion) {
		region = awsRegion;
		setProperty(AWS_REGION, region);
	}


	/**
	 * Checks if is EC 2 instance.
	 *
	 * @return the isEC2Instance
	 */
	public Boolean isEC2Instance() {
		return isEC2Instance;
	}

	/**
	 * Checks if is lambda function.
	 *
	 * @return the isLambdaInstance
	 */
	public Boolean isLambdaFunction() {
		if (isLambdaFunction == null) {
			lambdaFunctionName = System.getenv(LAMBDA_FUNCTION_VAR);
			if (lambdaFunctionName != null) {
				LOG.trace("Running from Lambda function \"{}\"", lambdaFunctionName);
				isLambdaFunction = true;
			} else {
				isLambdaFunction = false;
			}
		}
		return isLambdaFunction;
	}




}
