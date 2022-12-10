/**
 * 
 */
package com.iotcore.core.model.exception;

/**
@author <a href="mailto:garciadjx@gmail.com">J.M. Garcia</a>
 *
 */
public class ServiceUnavailableException extends Exception {

	private static final long serialVersionUID = -4491806588078894481L;

	private static final String MSG_PATTERN = "The target service %s is not available.";
	private static final String MSG_DETAILS = " Service Details:\n%s";
	
	/**
	 * 
	 */
	public ServiceUnavailableException() {
		super(String.format(MSG_PATTERN, ""));
	}

	/**
	 * @param serviceName
	 */
	public ServiceUnavailableException(String serviceName) {
		super(String.format(MSG_PATTERN, serviceName));
	}

	
	/**
	 * @param serviceName
	 * @param serviceDetails
	 */
	public ServiceUnavailableException(String serviceName, String serviceDetails) {
		super(String.format(MSG_PATTERN, serviceName) + 
				String.format(MSG_DETAILS, serviceDetails));
	}

}
