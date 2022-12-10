/**
 * 
 */
package com.iotcore.core.model.exception;

/**
 * @author jmgarcia
 *
 */
public class ParameterValidationException extends ServiceException {

	private static final long serialVersionUID = -3516520382900443579L;

	/**
	 * @param message
	 */
	public ParameterValidationException(String message) {
		super(message);
	}

}
