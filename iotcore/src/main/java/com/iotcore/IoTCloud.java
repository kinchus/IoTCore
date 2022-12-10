/**
 * 
 */
package com.iotcore;

import java.text.SimpleDateFormat;

/**
 * 
@author <a href="mailto:garciadjx@gmail.com">J.M. Garcia</a>
 *
 */
public class IoTCloud {
	
		
	public static final String APPLICATION_ROLE = "Application";
	public static final String SYSTEM_ROLE = "System";

	public static final String ALARMS_TABLE_SUFFIX = ".alarms";
	public static final String DEFAULT_EVENT_BUS = "default";
	
	
		
	/** DATE_FORMAT_STD */
	public static final String DATE_FORMAT_STD 	= "yyyy-MM-dd hh:mm:ss";
	/** DATE_FORMAT_ISO */
	public static final String DATE_FORMAT_ISO 	= "yyyy-MM-dd'T'hh:mm:ss.SSSX"; 
	/** DATE_FORMAT_RAW */
	public static final String DATE_FORMAT_RAW 	= "yyyyMMddhhmmssSSS";
	/** DATE_FORMAT_RAW */
	public static final String DATE_FORMAT_MINIMAL 	= "yy/MM/dd hh:mm:ss";
	
	
	private static final SimpleDateFormat stdDateFormat = new SimpleDateFormat(DATE_FORMAT_STD);
	private static final SimpleDateFormat isoDateFormat = new SimpleDateFormat(DATE_FORMAT_ISO);
	private static final SimpleDateFormat rawDateFormat = new SimpleDateFormat(DATE_FORMAT_RAW);
	private static final SimpleDateFormat minDateFormat = new SimpleDateFormat(DATE_FORMAT_MINIMAL);
	
	/**
	 * @return the sdf
	 */
	public static synchronized SimpleDateFormat getStdDateFormat() {
		return stdDateFormat;
	}

	/**
	 * @return the sdf
	 */
	public static synchronized SimpleDateFormat getIsoDateFormat() {
		return isoDateFormat;
	}

	/**
	 * @return
	 */
	public static synchronized SimpleDateFormat getRawDateFormat() {
		return rawDateFormat;
	}
	
	/**
	 * @return
	 */
	public static synchronized SimpleDateFormat getMinimalDateFormat() {
		return minDateFormat;
	}
	
	
	/**
	 * 
	 */
	public static class Feature {
		/** enableUserPermissions */
		public static final boolean enableUserPermissions = false;
		/** enableLora */
		public static final boolean enableLora = false;
		
	}
	
	
	/**
	 * 
	 */
	public static class Implementation {
		
		public static final boolean useAwsTimestream = false;
		public static final boolean useMongoTimestream = !useAwsTimestream;
		
		public static final boolean useAwsDynamoDb_DeviceMessageStore = false;
		public static final boolean useMongoDb_DeviceMessageStore = !useAwsDynamoDb_DeviceMessageStore;
		
		/** DeviceProfile_storeCopy */
		public static boolean DeviceProfile_storeCopy = false;
		/** DeviceProfile_storeReference */
		public static boolean DeviceProfile_storeReference = !DeviceProfile_storeCopy;
		/** deviceProfile_configurationAsJson */
		public static boolean deviceProfile_configurationAsJson = false;
		/** deviceProfile_configurationAsMap */
		public static boolean deviceProfile_configurationAsMap = false; 
		
	}
	
	

		
	/**
	 * Definitions for UDP gateway and device messaging
	 */
	public static class UdpMqtt {
		
		/** BACKEND_HANDLER_NAME */
		public static final String BACKEND_HANDLER_NAME = "UdpDeviceMessage";
		
		/** IoTRULE_DEVMSG_NAME */
		public static final String IoTRULE_DEVMSG_NAME 		= "UDPDeviceMessageTX";
		/** IoTRULE_DEVMSG_TOPIC */
		public static final String IoTRULE_DEVMSG_TOPIC 	= "udpserver/device/+/tx";
		/** IoTRULE_DEVMSG_SQL */
		public static final String IoTRULE_DEVMSG_SQL 		= "SELECT *, topic(3) AS devEUI FROM '" + IoTRULE_DEVMSG_TOPIC +  "'";
		
		/** MSG_FIELD_APPID */
		public static final String MSG_FIELD_APPID		= "applicationID";
		/** MSG_FIELD_DEVEUI */
		public static final String MSG_FIELD_DEVEUI		= "devEUI";
		/** MSG_FIELD_DEVTYPE */
		public static final String MSG_FIELD_DEVTYPE	= "type";
		/** MSG_FIELD_PAYLOAD */
		public static final String MSG_FIELD_PAYLOAD	= "payload";
		/** MSG_FIELD_DATA */
		public static final String MSG_FIELD_DATA 		= "data";
		/** MSG_FIELD_SEQNUM */
		public static final String MSG_FIELD_SEQNUM		= "fCnt";
		/** MSG_FIELD_RECVDATE */
		public static final String MSG_FIELD_RECVDATE 	= "recvDate";
		/** MSG_FIELD_MSGID */
		public static final String MSG_FIELD_MSGID 		= "N";
	}




}
