/**
 * 
 */
package com.iotcore.core;

import java.text.SimpleDateFormat;

/**
 * 
@author <a href="mailto:garciadjx@gmail.com">J.M. Garcia</a>
 *
 */
public class IoTCloud {
	
	public static final String ROOT_ORGANIZATION_ID = "000000000000";	
	
		
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
	
	
	
	public static void initialize() {
		
	}
	

}
