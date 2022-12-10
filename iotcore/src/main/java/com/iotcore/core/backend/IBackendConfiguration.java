package com.iotcore.core.backend;

import java.io.IOException;
import java.io.Serializable;
import java.net.URI;


/**
 * @author jmgarcia
 *
 */
public interface IBackendConfiguration extends Serializable {

	/** ENV_BACKEND_S3BUCKET */
	String ENV_BACKEND_LOCATION = "BACKEND_LOCATION";
	
	/** PLATFORM INFO */
	String KEY_PLATFORM_INFO = "info";
	String PLATFORM_NAME = "info/name";
	String PLATFORM_VERSION = "info/version";
	String PLATFORM_DATE = "info/date";
	String PLATFORM_TIMEZONE = "info/timezone";
	/** DATABASE */
	String KEY_CONFIG_DB = "config/database";
	String DB_URI =  "config/database/uri";
	String DB_HOST = "config/database/host";
	String DB_USER = "config/database/user";
	String DB_PASSWORD = "config/database/password";
	String DB_DBNAME = "config/database/dbname";
	/** KEY_CONFIG_LORA */
	String KEY_CONFIG_LORA = "config/lora";
	String LORA_API_URL = "config/lora/api_url";
	String LORA_API_USER = "config/lora/api_user";
	String LORA_API_SECRET = "config/lora/api_secret";
	String LORA_ORGANIZATIONID_DEFAULT = "config/lora/default_organizationID";
	String LORA_NETWORKSERVERID_DEFAULT = "config/lora/default_networkServerID";
	String LORA_APPLICATIONID_DEFAULT = "config/lora/default_applicationID";
	String LORA_DEVPROFILEID_DEFAULT = "config/lora/default_deviceProfileID";
	/** */
	String KEY_BACKEND_SYSTEM = "backend/system";
	String KEY_BACKEND_APPLICATIONS = "backend/applications";

	
	

	/**
	 * @return the bucketName
	 */
	URI getResourceUrl();

	/**
	 * @param location the bucketName to set
	 * 
	 */
	void setResourceUrl(URI location);

	/**
	 * Read the configuration defined ixn the system var
	 * 
	 */
	void init();
	
	boolean waitForInitialization();
	
	/**
	 * @param <T>
	 * @param field
	 * @return
	 */
	<T> T get(String field);

	/**
	 * @param <T>
	 * @param field
	 * @param defVal
	 * @return
	 */
	<T> T get(String field, T defVal);

	/**
	 * @param field
	 * @param value
	 * 
	 */
	void put(String field, Object value);


	/**
	 * @throws IOException
	 */
	void update() throws IOException;


	/**
	 * @param field
	 * 
	 */
	void delete(String field);
	
	/**
	 * @return
	 */
	default String getPlatformName() {
		return get(IBackendConfiguration.PLATFORM_NAME);
	}
	
	default String getPlatformRegion() {
		return get(IBackendConfiguration.PLATFORM_TIMEZONE);
	}


	/**
	 * @return the platformVersion
	 */
	default String getPlatformVersion() {
		return get(IBackendConfiguration.PLATFORM_VERSION);
	}

	/**
	 * @return the platformDate
	 */
	default String getPlatformDate() {
		return get(IBackendConfiguration.PLATFORM_DATE);
	}
	
	void clear();

}