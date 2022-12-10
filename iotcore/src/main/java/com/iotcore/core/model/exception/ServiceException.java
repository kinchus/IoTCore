/**
 * 
 */
package com.iotcore.core.model.exception;

/**
 * Base class for errors in the service layer. 
 * Inheritance point is set to Throwable for less overheading.
 * 
 * @author <a href="mailto:garciadjx@gmail.com">J.M. Garcia</a>
 *
 */
public class ServiceException extends Exception {
	
	private static final long serialVersionUID = -5644708987907593906L;


	/**
	 * 
	 */
	public ServiceException() {
	}

	/**
	 * Constructor 
	 * @param message
	 * @param cause
	 */
	public ServiceException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 */
	public ServiceException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public ServiceException(Throwable cause) {
		super(cause);
	}

		@Override
	public String getMessage() {
		StringBuffer msgBuff = new StringBuffer(super.getMessage());
		if (getCause() != null) {
			msgBuff.append(". Caused by:\n" + getCause().getMessage());
		}
		return super.getMessage();
	}


}
