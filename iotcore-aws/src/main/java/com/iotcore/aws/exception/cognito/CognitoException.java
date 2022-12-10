package com.iotcore.aws.exception.cognito;

import com.iotcore.aws.exception.AwsException;

import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.services.cognitoidentity.model.NotAuthorizedException;
import software.amazon.awssdk.services.cognitoidentity.model.ResourceNotFoundException;
import software.amazon.awssdk.services.cognitoidentityprovider.model.InvalidPasswordException;

/**
 * 
 * @author <a href="mailto:garciadjx@gmail.com">J.M. Garcia</a>
 */
public class CognitoException extends AwsException {

	private enum CognitoResponse {
		UserNotFound, NotAuthorized, InvalidPassword, UnsupportedChallengeException
	}

	/** serialVersionUID */
	private static final long serialVersionUID = 9025360782003271337L;

	private CognitoResponse response;

	/**
	 * Constructor
	 * 
	 * @param msg
	 * @param awsError
	 */
	public CognitoException(String msg, AwsServiceException awsError) {
		super(msg, awsError);
		if (awsError instanceof ResourceNotFoundException) {
			response = CognitoResponse.UserNotFound;
		} else if (awsError instanceof NotAuthorizedException) {
			response = CognitoResponse.NotAuthorized;
		} else if (awsError instanceof InvalidPasswordException) {
			response = CognitoResponse.InvalidPassword;
		}

	}

	/**
	 * Constructor
	 * 
	 * @param msg
	 * @param response
	 */
	public CognitoException(String msg, CognitoResponse response) {
		super(msg);
		this.response = response;
	}

	/**
	 * @return the response
	 */
	public CognitoResponse getResponse() {
		return response;
	}

}
