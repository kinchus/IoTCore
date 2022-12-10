/**
 * 
 */
package com.iotcore.core.model.exception;

/**
@author <a href="mailto:garciadjx@gmail.com">J.M. Garcia</a>
 *
 */
public class ObjectValidationException extends ServiceException {

	private static final long serialVersionUID = 698206422280846932L;

	private static final String MSG_DEFAULT = "Invalid or missing required object data";
	private static final String MISSING_FIELD = "Missing required field \"%s\" of type \"%s\"";
	
	/**
	 * 
	 */
	public ObjectValidationException() {
		super(MSG_DEFAULT);
	}
	
	/**
	 * @param msg
	 */
	public ObjectValidationException(String msg) {
		super(msg);
	}
	
	/**
	 * @param fieldName
	 * @param fieldType
	 */
	public ObjectValidationException(String fieldName, Class<?> fieldType) {
		super(String.format(MISSING_FIELD, fieldName, fieldType.getSimpleName()));
	}


}
