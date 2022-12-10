package com.iotcore.core.model.exception;

/**
 * Thrown when a service is requested for creating an object that already exists. 
 *  
 * @author <a href="mailto:garciadjx@gmail.com">J.M. Garcia</a>
 *
 */
public class ObjectExistsException extends ServiceException {

	private static final long serialVersionUID = 3982795970064331499L;
	private static final String MSG_PATTERN = "%s already exists in the system";
	
	/**
	 * Creates an exception with default message
	 */
	public ObjectExistsException() {
		super(String.format(MSG_PATTERN, ""));
	}
	
	
	/**
	 * Creates an exception specifying the entity id in the message
	 * @param entity
	 */
	public ObjectExistsException(String entity) {
		super(String.format(MSG_PATTERN, entity));
	}

}
