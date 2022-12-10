package com.iotcore.core.model.exception;

/**
 *  Thrown when some action is requested on some object which doesn't exists. 
 * @author <a href="mailto:garciadjx@gmail.com">J.M. Garcia</a>
 *
 */
public class ObjectNotFoundException extends ServiceException {

	private static final long serialVersionUID = -2537328555718406485L;
	
	private static final String MSG_PATTERN = "The requested object %s was not found in the system.";
	
	/**
	 * Creates an exception with default message
	 */
	public ObjectNotFoundException() {
		super(String.format(MSG_PATTERN, ""));
	}
	
	
	/**
	 * Creates an exception specifying the entity id in the message
	 * @param entityId
	 */
	public ObjectNotFoundException(String entityId) {
		super(String.format(MSG_PATTERN, ("ID:" + entityId)));
	}

}
