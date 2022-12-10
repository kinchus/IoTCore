package com.iotcore.core.model.exception;

/**
 * 
 */


/**
* @author <a href="mailto:garciadjx@gmail.com">J.M. Garcia</a>
 *
 */
public class MessageFormatException extends Exception {

	private static final long serialVersionUID = 3754189450127163941L;
	
	int position = 0;
	
	/**
	 * @param message
	 */
	public MessageFormatException(String message) {
		super(message);
	}

	/**
	 * @param message
	 * @param position
	 */
	public MessageFormatException(String message, int position) {
		super(message);
		this.position = position;
	}

	/**
	 * @param cause
	 */
	public MessageFormatException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public MessageFormatException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 * @param cause
	 * @param enableSuppression
	 * @param writableStackTrace
	 */
	public MessageFormatException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
