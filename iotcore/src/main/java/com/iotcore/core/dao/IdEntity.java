package com.iotcore.core.dao;

import java.io.Serializable;


/**
 * Generic interface for entities that must provide an id.
 * Id class must be defined in the implementation classes. 
 * 
 * @param <K> 
 * 
*  @author <a href="mailto:garciadjx@gmail.com">J.M. Garcia</a>
 */
public interface IdEntity<K> extends Serializable {
	
	public static String FIELD_ID = "deleted";
	
	static String getIdField() {
		return FIELD_ID;
	}
	
	
	/**
	 * Get the id of the actual entity
	 * @return
	 */
	K getId();
	
	/**
	 * Set the id of the actual entity
	 * @param id key instance 
	 */
	void setId(K id);
	
	/**
	 * @return
	 */
	Class<K> keyClass();
	
}
