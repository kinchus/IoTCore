package com.iotcore.aws.model.cognito;

/**
 * 
 * @author <a href="mailto:garciadjx@gmail.com">J.M. Garcia</a>
 */
public enum CognitoValidationResponse {

	/** LOGIN_OK */
	LOGIN_OK,
	/** USER_MUST_CHANGE_PASSWORD */
	USER_MUST_CHANGE_PASSWORD,
	/** USER_NOT_CONFIRMED */
	USER_NOT_CONFIRMED,
	/** USER_NOT_FOUND */
	USER_NOT_FOUND,
	/** SUBMITTED_INVALID_PASSWORD */
	SUBMITTED_INVALID_PASSWORD,
	/** NOT_AUTHORIZED */
	NOT_AUTHORIZED;

	private String authSesssion = null;
	private String token = null;

	/**
	 * Constructor
	 * 
	 * @param res
	 */
	private CognitoValidationResponse() {
	}

	/**
	 * @return the authSesssion
	 */
	public String getAuthSesssion() {
		return authSesssion;
	}

	/**
	 * @return the token
	 */
	public String getToken() {
		return token;
	}

	/**
	 * @param authSesssion the authSesssion to set
	 */
	public void setAuthSesssion(String authSesssion) {
		this.authSesssion = authSesssion;
	}

	/**
	 * @param token the token to set
	 */
	public void setToken(String token) {
		this.token = token;
	}

	/**
	 * @param authSesssion the authSesssion to set
	 * @return
	 */
	public CognitoValidationResponse withAuthSession(String authSesssion) {
		this.authSesssion = authSesssion;
		return this;
	}

	/**
	 * @param token the token to set
	 * @return
	 */
	public CognitoValidationResponse withSesssionToken(String token) {
		this.token = token;
		return this;
	}

}
