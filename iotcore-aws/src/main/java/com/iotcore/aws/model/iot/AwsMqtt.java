package com.iotcore.aws.model.iot;

/**
 * 
 * @author <a href="mailto:garciadjx@gmail.com">J.M. Garcia</a>
 */
public class AwsMqtt {

	/** TOPIC_IoTAPP_PATTERN */
	public static final String TOPIC_IoTAPP_PATTERN = "%s/application/%s/device/%s/data";
	/** AWS_TOPIC_PREFIX */
	public static final String AWS_TOPIC_PREFIX = "$aws/things/";
	/** AWS_TOPIC_UPDSHADOW_SUFFIX */
	public static final String AWS_TOPIC_UPDSHADOW_SUFFIX = "/shadow/update";
	/** AWS_TOPIC_GETSHADOW_SUFFIX */
	public static final String AWS_TOPIC_GETSHADOW_SUFFIX = "/shadow/update";

	/** MSG_FIELD_MESSAGE */
	public static final String MSG_FIELD_MESSAGE = "message";
	/** MSG_FIELD_DEVEUI */
	public static final String MSG_FIELD_DEVEUI = "deviceEUI";
	/** MSG_FIELD_MSGTYPE */
	public static final String MSG_FIELD_MSGTYPE = "msgType";
	/** MSG_FIELD_APPID */
	public static final String MSG_FIELD_APPID = "appID";
	/** MSG_FIELD_DATA */
	public static final String MSG_FIELD_DATA = "data";
	/** MSG_FIELD_SEQNUM */
	public static final String MSG_FIELD_SEQNUM = "seqNumber";
	/** MSG_FIELD_SENSOR */
	public static final String MSG_FIELD_SENSOR = "sensorID";
	/** MSG_FIELD_RECVDATE */
	public static final String MSG_FIELD_RECVDATE = "recvDate";
	/** MSG_FIELD_TIMESTAMP */
	public static final String MSG_FIELD_TIMESTAMP = "timestamp";
	/** MSG_FIELD_VALUE */
	public static final String MSG_FIELD_VALUE = "value";
	/** MSG_FIELD_PAYLOAD */
	public static final String MSG_FIELD_PAYLOAD = "payload";
	/** MSG_FIELD_CUSTOMERID */
	public static final String MSG_FIELD_CUSTOMERID = "customerID";
	/** MSG_FIELD_MSGID */
	public static final String MSG_FIELD_MSGID = "N";

}
