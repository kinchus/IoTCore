/**
 * 
 */
package com.iotcore.mongo;

/**
* @author <a href="mailto:garciadjx@gmail.com">J.M. Garcia</a>d
 *
 */
public class DbConstants {

	/** TEST_DATABASE */
	public static final String TEST_DATABASE = "test";
	
	/** TEST_CONFIG */
	public static final String TEST_CONFIG = "mongotest.properties";
	
	
	/** BASE_DOMAIN_PACKAGE */
	public static final String BASE_DOMAIN_PACKAGE = "com.northstar.domain.";
	/** JSON_PATH */
	public static final String JSON_PATH = "db/test/changesets/";
	/** ROLES */
	public static final String ROLES = "Role";
	/** PROFILES */
	public static final String USER_PROFILES = "UserProfile";
	/** ORGANIZATIONS */
	public static final String ORGANIZATIONS = "Organization";
	/** CUSTOMERS */
	public static final String CUSTOMERS = "IHolder";
	/** USERS */
	public static final String USERS = "User";
	/** APP_HANDLERS */
	public static final String APP_HANDLERS = "ApplicationHandler";
	/** APPLICATIONS */
	public static final String APPLICATIONS = "Application";
	/** APP_SUBSCRIPTIONS */
	public static final String SUBSCRIPTIONS = "ApplicationSubscription";
	/** DEVICES */
	public static final String DEVICES = "Device";
	/** DEVICE_PROFILES */
	public static final String DEVICE_PROFILES = "DeviceProfile";
	/** METER_CONFIGS */
	public static final String METER_PROFILES = "MeterProfile";
	/** ALARM_DEFINITION */
	public static final String ALARM_DEFINITIONS = "AlarmDefinition";
	/** ALARM_ACTIVATIONS */
	public static final String ALARM_ACTIVATIONS = "AlarmActivation";
	
	public static final String PLATFORM = "Platform";
	
	
	
	/** LORA_GATEWAYS */
	public static final String LORA_GATEWAYS = null;
	/** LORA_DEVICES */
	public static final String LORA_DEVICES = null;
	
	
	/** ALL_COLLECTIONS */
	public static final String [] ALL_COLLECTIONS = {
			ROLES,
			ORGANIZATIONS,
			CUSTOMERS,
			USER_PROFILES,
			USERS,
			APP_HANDLERS,
			APPLICATIONS,
			SUBSCRIPTIONS,
			METER_PROFILES,
			ALARM_DEFINITIONS
	};




	

}
