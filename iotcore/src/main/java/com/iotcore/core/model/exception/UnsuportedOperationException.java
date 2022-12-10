package com.iotcore.core.model.exception;

/**
 * 
 * @author <a href="mailto:garciadjx@gmail.com">J.M. Garcia</a>
 */
public class UnsuportedOperationException extends Exception {

	/** serialVersionUID */
	private static final long serialVersionUID = 2369542956789334645L;

	/**
	 * Constructor 
	 */
	public UnsuportedOperationException() {
	}

	/**
	 * Constructor 
	 * @param arg0
	 */
	public UnsuportedOperationException(String arg0) {
		super(arg0);

	}

	/**
	 * Constructor 
	 * @param arg0
	 */
	public UnsuportedOperationException(Throwable arg0) {
		super(arg0);

	}

	/**
	 * Constructor 
	 * @param arg0
	 * @param arg1
	 */
	public UnsuportedOperationException(String arg0, Throwable arg1) {
		super(arg0, arg1);

	}

}
