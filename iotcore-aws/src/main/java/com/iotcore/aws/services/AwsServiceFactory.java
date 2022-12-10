/**
 * 
 */
package com.iotcore.aws.services;

import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Produces;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iotcore.aws.backend.AwsBackendConfiguration;
import com.iotcore.aws.protocols.s3.S3UrlStreamHandlerFactory;

import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;

/**
 * @author <a href="mailto:garciadjx@gmail.com">J.M. Garcia</a>
 *
 */
public class AwsServiceFactory {
	
	private static Logger LOG = LoggerFactory.getLogger(AwsBackendConfiguration.class);

	private static AwsCognito cognitoService = null;
	private static AwsIoTService iotService = null;
	private static AwsIoTDataService iotDataService = null; 
	private static AwsLambdaService lambdaService = null;
	private static AwsS3Service s3Service = null;
	private static AwsEventBridgeService eventBridgeService = null;
	private static AwsSnsService awsSnsService = null;
	
	public static void init() {
		if (S3UrlStreamHandlerFactory.isRegistered()) {
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
		AwsBackendConfiguration cfg = AwsBackendConfiguration.getInstance();
		cfg.waitForInitialization();
	}
	
	
	/**
	 * @return the AWS Cognito service
	 */
	@Produces
	@SessionScoped
	public static synchronized AwsCognito getCognitoService() {
		if (cognitoService == null) {
			cognitoService = new AwsCognito();
		}
		return cognitoService;
	}

	/**
	 * @return
	 */
	@Produces
	@SessionScoped
	public static AwsEventBridgeService getEventBridgeService() {
		if (eventBridgeService == null) {
			eventBridgeService = AwsEventBridgeService.getInstance(); // AwsIoTService.getInstance();
		}
		return eventBridgeService;
	}

	/**
	 * @return the iotService
	 */
	@Produces
	@SessionScoped
	public static synchronized AwsIoTService getIotService() {
		if (iotService == null) {
			iotService = new AwsIoTService(); // AwsIoTService.getInstance();
		}
		return iotService;
	}
	
	
	@Produces
	@SessionScoped
	public static synchronized AwsIoTDataService getIotDataService() {
		if (iotDataService == null) {
			iotDataService = new AwsIoTDataService(); // AwsIoTService.getInstance();
		}
		return iotDataService;
	}

	/**
	 * @return the lambdaService
	 */
	@Produces
	@SessionScoped
	public static synchronized AwsLambdaService getLambdaService() {
		if (lambdaService == null) {
			lambdaService = new AwsLambdaService();
		}
		return lambdaService;
	}

	/**
	 * @return the s3Service
	 */
	@Produces
	@SessionScoped
	public static synchronized AwsS3Service getS3Service() {
		if (s3Service == null) {
			s3Service = new AwsS3Service();
		}
		return s3Service;
	}

	/**
	 * @param credentials
	 * @return the s3Service
	 */
	public static synchronized AwsS3Service getS3Service(AwsCredentialsProvider credentials) {
		if (s3Service == null) {
			s3Service = new AwsS3Service(credentials);
		}
		return s3Service;
	}

	/**
	 * @return
	 */
	@Produces
	@SessionScoped
	public static AwsSnsService getSnsService() {
		if (awsSnsService == null) {
			awsSnsService = new AwsSnsService();
		}
		return awsSnsService;
	}
	

}
