/**
 * 
 */
package com.iotcore.aws.model.lambda;

/**
 * The Class LambdaRuntimeConfig.
 */
public class LambdaRuntimeConfig {

	public static final String RUNTIME_JAVA8 = "java8";
	public static final String RUNTIME_JAVA11 = "java11";
	
	private static String defaultRuntime = RUNTIME_JAVA11;
	
	private static LambdaRuntimeConfig defaultConfig = new LambdaRuntimeConfig();

	/**
	 * Gets the default.
	 *
	 * @return the default
	 */
	public static LambdaRuntimeConfig getDefault() {
		return defaultConfig;
	}

	private String s3Bucket = null;
	private String s3Key = null;
	private String S3ObjectVersion = null;
	private Integer memSize = 512;
	private Integer timeout = 60;
	private String role = null;

	private Boolean publish = true;

	private String runtime = defaultRuntime;

	/**
	 * Instantiates a new lambda runtime config.
	 */
	public LambdaRuntimeConfig() {
	}

	/**
	 * Gets the mem size.
	 *
	 * @return the mem size
	 */
	public Integer getMemSize() {
		return memSize;
	}

	/**
	 * Gets the publish.
	 *
	 * @return the publish
	 */
	public Boolean getPublish() {
		return publish;
	}

	/**
	 * Gets the role.
	 *
	 * @return the role
	 */
	public String getRole() {
		return role;
	}

	/**
	 * Gets the runtime.
	 *
	 * @return the runtime
	 */
	public String getRuntime() {
		return runtime;
	}

	/**
	 * Gets the s 3 bucket.
	 *
	 * @return the s 3 bucket
	 */
	public String getS3Bucket() {
		return s3Bucket;
	}

	/**
	 * Gets the s 3 key.
	 *
	 * @return the s 3 key
	 */
	public String getS3Key() {
		return s3Key;
	}

	/**
	 * Gets the s 3 object version.
	 *
	 * @return the s 3 object version
	 */
	public String getS3ObjectVersion() {
		return S3ObjectVersion;
	}

	/**
	 * Gets the timeout.
	 *
	 * @return the timeout
	 */
	public Integer getTimeout() {
		return timeout;
	}

	/**
	 * Sets the mem size.
	 *
	 * @param memSize the new mem size
	 */
	public void setMemSize(Integer memSize) {
		this.memSize = memSize;
	}

	/**
	 * Sets the publish.
	 *
	 * @param publish the new publish
	 */
	public void setPublish(Boolean publish) {
		this.publish = publish;
	}

	/**
	 * Sets the role.
	 *
	 * @param role the new role
	 */
	public void setRole(String role) {
		this.role = role;
	}

	/**
	 * Sets the runtime.
	 *
	 * @param runtime the new runtime
	 */
	public void setRuntime(String runtime) {
		this.runtime = runtime;
	}

	/**
	 * Sets the s 3 bucket.
	 *
	 * @param s3Bucket the new s 3 bucket
	 */
	public void setS3Bucket(String s3Bucket) {
		this.s3Bucket = s3Bucket;
	}

	/**
	 * Sets the s 3 key.
	 *
	 * @param s3Key the new s 3 key
	 */
	public void setS3Key(String s3Key) {
		this.s3Key = s3Key;
	}

	/**
	 * Sets the s 3 object version.
	 *
	 * @param s3ObjectVersion the new s 3 object version
	 */
	public void setS3ObjectVersion(String s3ObjectVersion) {
		S3ObjectVersion = s3ObjectVersion;
	}

	/**
	 * Sets the timeout.
	 *
	 * @param timeout the new timeout
	 */
	public void setTimeout(Integer timeout) {
		this.timeout = timeout;
	}

}
