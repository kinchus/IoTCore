package com.iotcore.core.model.message;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.iotcore.core.util.json.JsonString;

/**
* @author <a href="mailto:garciadjx@gmail.com">J.M. Garcia</a>
 *
 */
public interface IoTMessage extends Serializable, Comparable<IoTMessage> {
	
	public static final String DATE_FORMAT_PATTERN = "YY-MM-DD'T'HH:mm:ss.SSS'Zs'";
	
	public static final String MSG_TYPE_HEARTBEAT = "heartbeat";
	public static final String MSG_TYPE_IMAGEDATA = "image_data";
	public static final String MSG_TYPE_APPEVENT = "app_event";
	public static final String MSG_TYPE_CHANGESCONFIRMED = "changes_confirmed";
	
	
	public enum MessageType {
		
		HEARTBEAT(MSG_TYPE_HEARTBEAT),
		APP_EVENT(MSG_TYPE_APPEVENT),
		IMAGEDATA(MSG_TYPE_IMAGEDATA),
		CHANGESCONFIRMED(MSG_TYPE_CHANGESCONFIRMED);
		
		
		private String msgTypeName;
		
		private MessageType(String name) {
			msgTypeName = name;
		}

		/**
		 * @return the msgTypeName
		 */
		public String messageTypeName() {
			return msgTypeName;
		}
		
	}

	/**
	 * @return
	 */
	public static MessageType getMessageType(String msgType) {
		try {
			return MessageType.valueOf(msgType);
		}
		catch (Exception e) {
			return null;
		}
	}
	
	
	public static  SimpleDateFormat dateFormat  = new SimpleDateFormat(DATE_FORMAT_PATTERN);
		
	// Message fields common to all messages
	static final String MSG_DEVICEID = "deviceID";
	static final String MSG_TIMESTAMP = "timestamp";
	static final String MSG_COUNTER = "counter";
	static final String MSG_APPLICATION = "application";
	static final String MSG_TYPE = "msg_type";
	static final String MSG_SENSORID = "sensorID";
	static final String MSG_DATA = "data";
	static final String MSG_EVENT_NAME = "event_name";
	static final String MSG_EVENT_DATA = "event_data";
	
	
	/**
	 * @return
	 */
	String getDeviceID();
	
	/**
	 * @return
	 */
	String getApplication();
	
	/**
	 * @return
	 */
	Integer getSensorID();
	
	/**
	 * @return
	 */
	String getMsgType();
	
	/**
	 * @return
	 */
	Long getCounter();
	
	/**
	 * @return
	 */
	Long getTimestamp();
	
	/**
	 * @return
	 */
	Object getData();
	
	/**
	 * @return
	 */
	String getEventName();
		
	/**
	 * @return
	 */
	Object getEventData();
	
	/**
	 * @return
	 */
	default Integer getSize() {
		return toJson().toString().length();
	}
	
	
	/**
	 * Get the full JSON representation of this message 
	 * @return
	 */
	default JsonString toJson() {
		JsonString ret = new JsonString();
		ret.put(MSG_TIMESTAMP, getTimestamp());
		ret.put(MSG_COUNTER, getCounter());
		ret.put(MSG_APPLICATION, getApplication());
		ret.put(MSG_DEVICEID, getDeviceID());
		ret.put(MSG_TYPE, getMsgType());
		ret.put(MSG_SENSORID, getSensorID());
		ret.put(MSG_DATA, getData());
		return ret;
	}
	
	default Map<String, Object> toMap() {
		Map<String, Object> ret = new HashMap<String, Object>();
		ret.put(MSG_TIMESTAMP, getTimestamp());
		ret.put(MSG_COUNTER, getCounter());
		ret.put(MSG_APPLICATION, getApplication());
		ret.put(MSG_DEVICEID, getDeviceID());
		ret.put(MSG_TYPE, getMsgType());
		ret.put(MSG_SENSORID, getSensorID());
		ret.put(MSG_DATA, getData());
		return ret;
	}
	

	/**
	 * @return the time
	 */
	default Date getTimestampDate() {
		return new Date(getTimestamp());
	}

	@Override
	default int compareTo(IoTMessage o) {
		if (getDeviceID().equals(o.getDeviceID())) {
			return getTimestamp().compareTo(o.getTimestamp());
		}
		else {
			return getDeviceID().compareTo(o.getDeviceID());
		}
	}
	
}
