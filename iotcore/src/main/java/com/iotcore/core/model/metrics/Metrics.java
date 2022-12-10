/**
 * 
 */
package com.iotcore.core.model.metrics;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * @author jmgarcia
 *
 */
public interface Metrics extends Serializable {
	
	static final String SCHEDULE_EXPRESSION = "cron(0 * * * ? *)";
	static final String DATE_PATTERN = "yyyy-MM-dd HH:mm:ss.S";
	
	
	static final DateFormat DATE_FORMAT = new SimpleDateFormat(DATE_PATTERN);
	
	

	
}
