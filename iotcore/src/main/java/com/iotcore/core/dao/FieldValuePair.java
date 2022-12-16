package com.iotcore.core.dao;

import java.util.Arrays;
import java.util.List;

/**
 * @author jmgarcia
 *
 */
public class FieldValuePair {
	
	
	public static FieldValuePair[] fromFieldValuePairs(Object ... objects) {
		FieldValuePair[]  ret = null;
		if ((objects.length % 2) != 0) {
			return null;
		}
		int len = objects.length / 2;
		ret = new FieldValuePair[len];
		for (int i=0;i<len;i++) {
			int fn = i*2;
			ret[i] = new FieldValuePair(objects[fn].toString(), objects[fn+1]);
		}
		
		return ret;
	}
	
	private String field;
	private Object value;
	private Object[] values;
	
	/**
	 * 
	 */
	public FieldValuePair() {
		super();
	}

	/**
	 * @param field
	 * @param value
	 */
	public FieldValuePair(String field, Object value) {
		super();
		this.field = field;
		this.value = value;
	}

	/**
	 * @param field
	 * @param values
	 */
	public FieldValuePair(String field, Object[] values) {
		super();
		this.field = field;
		this.values = values;
	}
	
	public boolean isArrayValue() {
		return values != null;
	}
	
	public boolean isNullValue() {
		return value != null;
	}
	

	/**
	 * @return the field
	 */
	public String getField() {
		return field;
	}

	/**
	 * @return the value
	 */
	public Object getValue() {
		return value;
	}

	/**
	 * @return the values
	 */
	public List<Object> getValues() {
		return Arrays.asList(values);
	}

	
}