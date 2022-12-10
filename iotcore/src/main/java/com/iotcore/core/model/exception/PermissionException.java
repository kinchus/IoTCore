/**
 * 
 */
package com.iotcore.core.model.exception;

/**
@author <a href="mailto:garciadjx@gmail.com">J.M. Garcia</a>
 *
 */
public class PermissionException extends ServiceException {

	private static final String DEFAULT_MESSAGE = "The requested operation could not be authorized";
	private static final String FULL_MESSAGE = "The operation \"%s\" on object \"%s\" could not be authorized";
	
	
	/**
	 * 
	 */
	public PermissionException() {
		super(DEFAULT_MESSAGE);
	}

	
	/**
	 * @param message
	 */
	public PermissionException(String message) {
		super(message);
	}

	/**
	 * @param operation 
	 * @param obj 
	 */
	public PermissionException(String operation, Object obj) {
		super(String.format(FULL_MESSAGE, operation, obj.toString()));
	}

	private static final long serialVersionUID = -7653202802468525757L;


}
