package com.iotcore.domain;

public interface IDevice {

	String getId();
	
	/**
	 * @return the deviceID
	 */
	String getDeviceID();

	/**
	 * @return the deviceType
	 */
	String getDeviceType();

	/**
	 * @return the networkID
	 */
	String getNetworkID();

	/**
	 * @return the networkName
	 */
	String getNetworkName();

	/**
	 * @return the applicationID
	 */
	String getApplicationID();

	/**
	 * @return the applicationName
	 */
	String getApplicationName();

	/**
	 * @return the holderID
	 */
	String getHolderID();
	
	
	String getCertificateID();

}