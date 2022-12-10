/*
 * Version 1.0 3/3/2015
 * 
 */
package com.iotcore.mongo.id;

import org.bson.types.ObjectId;

/**
* @author <a href="mailto:garciadjx@gmail.com">J.M. Garcia</a>
 * @version 1.0
 */
public class ObjectIdUtils {
	
	/** OID_LENGTH */
	public static final int OID_LENGTH = 24;
	/** SID_LENGTH */
	public static final int SID_LENGTH = 11;

	private static final int TIMESTR_LENGTH = 8;
	private static final String OID_BASE = "000000000000000000000000";

	
	/**
	 * @return
	 */
	public static String newId() {
		return new ObjectId().toString().substring(OID_LENGTH - SID_LENGTH);
	}
	
	/**
	 * @param <K>
	 * @param clazz
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <K> K newId(Class<K> clazz) {
		if (clazz.equals(ObjectId.class)) {
			return  (K) new ObjectId();
		}
		String id = new ObjectId().toString().substring(OID_LENGTH - SID_LENGTH);
		if (clazz.equals(Long.class)) {
			return (K) Long.valueOf(id, 16);
		}
		else {
			return (K)id;
		}
		
		
	}
	
	/**
	 * Gets and concatenate the time and counter sections of an ObjectId string representation
	 * @param str
	 * @param numDigits
	 * @return
	 */
	public static String trimObjectIdString(String str, int numDigits) {
		int d = numDigits - TIMESTR_LENGTH;
		String hexStr = null;
		
		if ((str == null)) {
			return null;
		}
		
		int sz = str.length();
		
		if (str.length() >= numDigits) {
			hexStr = str.substring(0, TIMESTR_LENGTH) + str.substring(sz - d);
		}
		else {
			hexStr = str;
			
		}
		
		return hexStr;
	}
	
	
	
	/**
	 * Builds a valid ObjectID instance from any hex string 
	 * @param str
	 * @return
	 */
	public static ObjectId getObjectId(String str) {
		String aux = OID_BASE + str;
		return new ObjectId(aux.substring(aux.length() - OID_LENGTH));
	}
	
	public static ObjectId getObjectId(Object id) {
		String aux = OID_BASE + id.toString();
		return new ObjectId(aux.substring(aux.length() - OID_LENGTH));
	}

	
}

