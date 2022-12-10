package com.iotcore.core.model.exception;

/**
 * 
 * @author <a href="mailto:garciadjx@gmail.com">J.M. Garcia</a>
 */
public class PlatformException extends ServiceException {

	/**
	 * Constructor 
	 */
	public PlatformException() {
		super();
		
	}

	/**
	 * Constructor 
	 * @param message
	 * @param cause
	 */
	public PlatformException(String message, Throwable cause) {
		super(message, cause);
		
	}

	/**
	 * Constructor 
	 * @param message
	 */
	public PlatformException(String message) {
		super(message);
		
	}

	/**
	 * Constructor 
	 * @param cause
	 */
	public PlatformException(Throwable cause) {
		super(cause);
		
	}

	/** serialVersionUID */
	private static final long serialVersionUID = 6845210787595128070L;

}
