/**
 * 
 */
package com.iotcore.core.model.metrics;

import java.text.ParseException;
import java.util.Date;

/**
 * @author jmgarcia
 *
 */
public class ResultRow {
	
	
	private Object values[] = {null};
	
	

	/**
	 * 
	 */
	public ResultRow() {
		
	}
	
	public ResultRow(Object[] values) {
		this.values  = values;
	}

	/**
	 * @return the values
	 */
	public Object[] getValues() {
		return values;
	}

	/**
	 * @param values the values to set
	 */
	public void setValues(Object[] values) {
		this.values = values;
	}
	
	public Object column(int col) {
		return (values[col]);
	}
	
	public String stringColumn(int col) {
		if (values[col] instanceof String) {
			return (String) values[col];
		}
		else {
			return values[col].toString();
		}
	}

	
	public long intColumn(int col) {
		if (values[col] instanceof String) {
			return Long.parseLong((String)values[col]);
		}
		else {
			return (Long)values[col];
		}
	}

	public double doubleColumn(int col) {
		if (values[col] instanceof String) {
			return Double.parseDouble((String)values[col]);
		}
		else {
			return (Double)values[col];
		}
	}
	
	public Date timeColumn(int col) {
		if (values[col] instanceof Date) {
			return (Date)values[col];
		}
		else if (values[col] instanceof Long) {
			Date ret = new Date();
			ret.setTime((Long)values[col]);
		}
		else if (values[col] instanceof String) {
			try {
				return Metrics.DATE_FORMAT.parse((String)values[col]);
			} catch (ParseException e) {
				System.err.println(e.getMessage());
			}
		}
		return null;
	}
	
	public TimeSeries columnValueTimeSeries(int col) {
		return (TimeSeries) values[col];
	}
	
}
