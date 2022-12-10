package com.iotcore.aws.exception;

import software.amazon.awssdk.awscore.exception.AwsServiceException;

/**
 * @author <a href="mailto:garciadjx@gmail.com">J.M. Garcia</a>
 *
 */
public class AwsException extends Exception {

	private static final long serialVersionUID = 1L;

	private String serviceName = "unspecified";
	private String serviceCall = "unspecified";
	private String origin = "unspecified";

	
	/**
	 * @param awsError
	 * 
	 */
	public AwsException(AwsServiceException awsError) {
		super(awsError.awsErrorDetails().errorMessage(), awsError);
		setServiceName(awsError.awsErrorDetails().serviceName());
		setOrigin(awsError.awsErrorDetails().errorCode());
	}

	/**
	 * @param msg
	 */
	public AwsException(String msg) {
		super(msg);
	}

	/**
	 * @param message
	 * @param awsError
	 */
	public AwsException(String message, AwsServiceException awsError) {
		super(awsError.getMessage(), awsError);
		setServiceName(awsError.awsErrorDetails().serviceName());
		setServiceCall(serviceCall);
	}

	/**
	 * 
	 * @param message
	 * @param serviceCall
	 * @param awsError
	 */
	public AwsException(String message, String serviceCall, AwsServiceException awsError) {
		super(message, awsError);
		setServiceName(awsError.awsErrorDetails().serviceName());
		setServiceCall(serviceCall);
	}

	/**
	 * @return the origin
	 */
	public String getOrigin() {
		return origin;
	}

	/**
	 * @return the serviceCall
	 */
	public String getServiceCall() {
		return serviceCall;
	}

	/**
	 * @return the serviceName
	 */
	public String getServiceName() {
		return serviceName;
	}

	/**
	 * @param origin the origin to set
	 */
	public void setOrigin(String origin) {
		this.origin = origin;
	}

	/**
	 * @param serviceCall the serviceCall to set
	 */
	public void setServiceCall(String serviceCall) {
		this.serviceCall = serviceCall;
	}

	/**
	 * @param serviceName the serviceName to set
	 */
	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

}
