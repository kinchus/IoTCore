/**
 * 
 */
package com.iotcore.aws.model.iot;

import java.util.Map;

import com.iotcore.aws.model.AwsEntity;

/**
 * The Class Thing.
 */
public class Thing extends AwsEntity {

	private String id;
	private String type;
	private String certificateArn;
	private String certificateUrl;
	private String privateKeyUrl;
	private String publicKeyUrl;
	private String certificatePem;
	private String privateKeyPem;
	private String publicKeyPem;
	private String policyName;
	private Map<String, String> attributes;
	private String certificateId;

	/**
	 * Instantiates a new thing.
	 */
	public Thing() {

	}

	/**
	 * Instantiates a new thing.
	 *
	 * @param _id         the _id
	 * @param arn        the arn
	 * @param name       the name
	 * @param type       the type
	 * @param certPem    the cert pem
	 * @param privKey    the priv key
	 * @param pubKey     the pub key
	 * @param attributes the attributes
	 */
	public Thing(String id, String arn, String name, String type, String certPem, String privKey, String pubKey,
			Map<String, String> attributes) {
		this.id = id;
		this.arn = arn;
		this.name = name;
		this.type = type;
		this.certificatePem = certPem;
		this.privateKeyPem = privKey;
		this.publicKeyPem = pubKey;
		this.attributes = attributes;
	}

	/**
	 * @param obj The other object
	 * @return true if both objects represent the same Thing instance
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Thing)) {
			return false;
		}
		final Thing other = (Thing) obj;
		return id.equals(other.getId());
	}

	/**
	 * Gets the attributes.
	 *
	 * @return the attributes
	 */
	public Map<String, String> getAttributes() {
		return attributes;
	}

	/**
	 * Gets the certificate arn.
	 *
	 * @return the certificate arn
	 */
	public String getCertificateArn() {
		return certificateArn;
	}

	/**
	 * Gets the certificate _id.
	 *
	 * @return the certificate _id
	 */
	public String getCertificateId() {
		return certificateId;
	}

	/**
	 * Gets the certificate pem.
	 *
	 * @return the certificate pem
	 */
	public String getCertificatePem() {
		return certificatePem;
	}

	/**
	 * Gets the certificate url.
	 *
	 * @return the certificate url
	 */
	protected String getCertificateUrl() {
		return certificateUrl;
	}

	/**
	 * Gets the _id.
	 *
	 * @return the _id
	 */
	public String getId() {
		return id;
	}

	/**
	 * Gets the policy name.
	 *
	 * @return the policy name
	 */
	public String getPolicyName() {
		return policyName;
	}

	/**
	 * Gets the private key pem.
	 *
	 * @return the private key pem
	 */
	public String getPrivateKeyPem() {
		return privateKeyPem;
	}

	/**
	 * Gets the private key url.
	 *
	 * @return the private key url
	 */
	protected String getPrivateKeyUrl() {
		return privateKeyUrl;
	}

	/**
	 * Gets the public key pem.
	 *
	 * @return the public key pem
	 */
	public String getPublicKeyPem() {
		return publicKeyPem;
	}

	/**
	 * Gets the public key url.
	 *
	 * @return the public key url
	 */
	protected String getPublicKeyUrl() {
		return publicKeyUrl;
	}

	/**
	 * Gets the type.
	 *
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * Sets the attributes.
	 *
	 * @param attributes the attributes
	 */
	public void setAttributes(Map<String, String> attributes) {
		this.attributes = attributes;
	}

	/**
	 * Sets the certificate arn.
	 *
	 * @param certArn the new certificate arn
	 */
	public void setCertificateArn(String certArn) {
		this.certificateArn = certArn;
	}

	/**
	 * Sets the certificate _id.
	 *
	 * @param certificateId the new certificate _id
	 */
	public void setCertificateId(String certificateId) {
		this.certificateId = certificateId;
	}

	/**
	 * Sets the certificate pem.
	 *
	 * @param certPem the new certificate pem
	 */
	public void setCertificatePem(String certPem) {
		this.certificatePem = certPem;
	}

	/**
	 * Sets the certificate url.
	 *
	 * @param certificateUrl the new certificate url
	 */
	protected void setCertificateUrl(String certificateUrl) {
		this.certificateUrl = certificateUrl;
	}

	/**
	 * Sets the _id.
	 *
	 * @param _id the new _id
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * Sets the policy name.
	 *
	 * @param policy the new policy name
	 */
	public void setPolicyName(String policy) {
		this.policyName = policy;
	}

	/**
	 * Sets the private key pem.
	 *
	 * @param privKey the new private key pem
	 */
	public void setPrivateKeyPem(String privKey) {
		this.privateKeyPem = privKey;
	}

	/**
	 * Sets the private key url.
	 *
	 * @param privateKeyUrl the new private key url
	 */
	protected void setPrivateKeyUrl(String privateKeyUrl) {
		this.privateKeyUrl = privateKeyUrl;
	}

	/**
	 * Sets the public key pem.
	 *
	 * @param pubKey the new public key pem
	 */
	public void setPublicKeyPem(String pubKey) {
		this.publicKeyPem = pubKey;
	}

	/**
	 * Sets the public key url.
	 *
	 * @param publicKeyUrl the new public key url
	 */
	protected void setPublicKeyUrl(String publicKeyUrl) {
		this.publicKeyUrl = publicKeyUrl;
	}

	/**
	 * Sets the type.
	 *
	 * @param type the new type
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @return the String representatin if this Thing
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		String ret = "";
		String separator = "";
		if (id != null) {
			ret = id;
			separator = ":";
		}
		if (name != null) {
			ret = ret + separator + name;
		}
		return ret;

	}

}
