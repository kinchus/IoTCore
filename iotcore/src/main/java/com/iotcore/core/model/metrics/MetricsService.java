/**
 * 
 */
package com.iotcore.core.model.metrics;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.List;

import com.iotcore.core.model.exception.ServiceException;
/**
 * @author jmgarcia
 *
 */
public interface MetricsService extends Serializable {
	
	static final String DATE_PATTERN = "yyyy-MM-dd HH:mm:ss.S";
	static final DateFormat DATE_FORMAT = new SimpleDateFormat(DATE_PATTERN);

	/**
	 * @param query
	 * @param resultColumns
	 * @return
	 * @throws ServiceException
	 */
	List<ResultRow> executeQuery(String query) throws ServiceException;
	
	/**
	 * @param query
	 * @param resultColumns
	 * @return
	 * @throws ServiceException
	 */
	List<ResultRow> executeQuery(MetricsQuery query) throws ServiceException;
	
	
	
	static String queryParamIn(String ... values) {
		StringBuffer buff = new StringBuffer(" ('");
		String sep1 = "";
		String sep2 = "', '";
		String sep = sep1;
		for(String val:values) {
			buff.append(sep);
			buff.append(val);
			sep = sep2;
				
		}
		return buff.append("')").toString();
	}
	
	static String queryParamIn(Collection<String> values) {
		StringBuffer buff = new StringBuffer(" ('");
		String sep1 = "";
		String sep2 = "', '";
		String sep = sep1;
		for(String val:values) {
			buff.append(sep);
			buff.append(val);
			sep = sep2;
				
		}
		return buff.append("')").toString();
	}
	
	

}