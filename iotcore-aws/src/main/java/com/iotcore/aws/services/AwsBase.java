/**
 * 
 */
package com.iotcore.aws.services;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iotcore.aws.AwsConfig;
import com.iotcore.aws.backend.AwsBackendConfiguration;
import com.iotcore.aws.protocols.s3.S3UrlStreamHandlerFactory;

import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.awscore.AwsResponse;
import software.amazon.awssdk.core.SdkClient;

/**
 * @author <a href="mailto:garciadjx@gmail.com">J.M. Garcia</a>
 *
 */
public  abstract class AwsBase<C extends SdkClient> implements Serializable {

	private static final long serialVersionUID = 316881563719151362L;

	private static final Logger LOG = LoggerFactory.getLogger(AwsBase.class);
	private static final String ISO_8601 = "YYYY-MM-DD'T'hh:mm:ss.SSSX";
	private static final int HTTP_STATUS_OK = 200;
	private static final SimpleDateFormat iso8601Fmt = new SimpleDateFormat(ISO_8601);
	
	private static final AwsBackendConfiguration backendConfiguration;
	static {
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
		backendConfiguration = AwsBackendConfiguration.getInstance();
		backendConfiguration.waitForInitialization();
	}
	
	private static AwsCredentialsProvider credentialsProvider = null;
	
	
	/**
	 * @return the backendConfiguration
	 */
	protected static AwsBackendConfiguration getBackendConfiguration() {
		return backendConfiguration;
	}

	
	/**
	 * 
	 *
	 * @param dateStr
	 * @return
	 */
	protected synchronized static Date parseISODate(String dateStr) {
		Date ret = null;
		try {
			ret = iso8601Fmt.parse(dateStr);
		} catch (final ParseException e) {
			LOG.error("Couldn't parse the given date: \"{}\" ", dateStr);
		}
		return ret;
	}



	/**
	 * 
	 *
	 * @param credsProvider
	 */
	public static void setCredentialsProvider(AwsCredentialsProvider credsProvider) {
		credentialsProvider = credsProvider;
	}
	
	public static AwsCredentialsProvider getCredentialsProvider() {
		if (credentialsProvider == null) {
			credentialsProvider = DefaultCredentialsProvider.create();
		}
		return credentialsProvider;
	}
	
	
	public abstract C create();
	
	
	private C client;

	
	/**
	 * 
	 */
	public AwsBase() {
		
	}
	

	/**
	 * 
	 *
	 * @param credentials
	 */
	public AwsBase(AwsCredentials credentials) {
		setCredentialsProvider(StaticCredentialsProvider.create(credentials));
	}

	/**
	 * 
	 *
	 * @param credentials
	 */
	public AwsBase(AwsCredentialsProvider credentials) {
		setCredentialsProvider(credentials);
	}

	public C client() {
		if (client == null) {
			client = create();
		}
		return client;
	}

	
	/**
	 * @param result
	 * @return
	 */
	public static boolean responseOk(AwsResponse result) {
		final int code = result.sdkHttpResponse().statusCode();
		if ((AwsBase.HTTP_STATUS_OK <= code) && (code <= 205)) {
			return true;
		}
		return false;
	}

	
	/**
	 * @return
	 */
	protected synchronized AwsConfig getConfig() {
		return AwsConfig.getInstance();
	}


}
