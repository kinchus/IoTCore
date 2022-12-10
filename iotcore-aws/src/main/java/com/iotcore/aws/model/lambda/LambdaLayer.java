package com.iotcore.aws.model.lambda;

import java.net.URL;

import com.iotcore.aws.model.AwsEntity;

/**
 * The Class LambdaLayer.
 */
public class LambdaLayer extends AwsEntity {

	private Long version;
	private URL codeUrl;

	/**
	 * Instantiates a new lambda layer.
	 */
	public LambdaLayer() {
		super();
	}

	/**
	 * Gets the code url.
	 *
	 * @return the code url
	 */
	public URL getCodeUrl() {
		return codeUrl;
	}

	/**
	 * Gets the version.
	 *
	 * @return the version
	 */
	public Long getVersion() {
		return version;
	}

	/**
	 * Sets the code url.
	 *
	 * @param codeUrl the new code url
	 */
	public void setCodeUrl(URL codeUrl) {
		this.codeUrl = codeUrl;
	}

	/**
	 * Sets the version.
	 *
	 * @param version the new version
	 */
	public void setVersion(Long version) {
		this.version = version;
	}

	/**
	 * With code url.
	 *
	 * @param codeUrl the code url
	 * @return the lambda layer
	 */
	public LambdaLayer withCodeUrl(URL codeUrl) {
		this.codeUrl = codeUrl;
		return this;
	}

	/**
	 * With version.
	 *
	 * @param version the version
	 * @return the lambda layer
	 */
	public LambdaLayer withVersion(Long version) {
		this.version = version;
		return this;
	}

}
