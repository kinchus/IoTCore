/**
 * 
 */
package com.iotcore.core.model.metrics;

import java.text.ParseException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @author jmgarcia
 *
 */
public class TimeSeries {
	
	Date [] times;
	Object [] values;
	
	/**
	 * @param rows
	 */
	public TimeSeries(int rows) {
		values = new Object[rows];
		times = new Date[rows];
	}
	
	/**
	 * @return
	 */
	public List<Date> times() {
		return Arrays.asList(times);
	}

	public void setTimes(Date[] times) {
		this.times = times;
	}

	
	public Object[] values() {
		return values;
	}
	
	@SuppressWarnings("unchecked")
	public <T> List<T> toList() {
		return Arrays.asList((T[])values);
	}
	
	public Long sum() {
		Long ret = 0L;
		for (Object v:values) {
			long t = 0;
			if (v.getClass().isAssignableFrom(Long.class)) {
				t = (Long)v;
			}
			else if (v.getClass().isAssignableFrom(Integer.class)) {
				t = ((Integer)v).longValue();
			}
			else if (v.getClass().isAssignableFrom(Double.class)) {
				t = ((Double)v).longValue();
			}
			else if (v.getClass().isAssignableFrom(Float.class)) {
				t = ((Double)v).longValue();
			}
			ret += t;
		}
		return ret;
	}


	public void setValues(Object[] values) {
		this.values = values;
	}

	public void setValues(Date[] times, Object[] values) {
		setTimes(times);
		setValues(values); 
	}

	
	public boolean setValue(int row, String ts, Object value) {
		values[row] = value;
		try {
			times[row] = Metrics.DATE_FORMAT.parse(ts);
			return true;
		} catch (ParseException e) {
			return false;
		}
		
	}
	
	@SuppressWarnings("unchecked")
	public <T> List<T> asList() {
		return Arrays.asList((T[])values);
		
	}

	/**
	 * @return
	 */
	public int size() {
		
		return times.length;
	}

	
}
