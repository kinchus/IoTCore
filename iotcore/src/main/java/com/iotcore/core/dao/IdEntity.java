package com.iotcore.core.dao;

import java.io.Serializable;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;


/**
 * Generic interface for entities that must provide an id.
 * Id class must be defined in the implementation classes. 
 * 
 * @param <K> 
 * 
*  @author <a href="mailto:garciadjx@gmail.com">J.M. Garcia</a>
 */
public interface IdEntity<K extends Serializable> extends Serializable {
	
	static final Logger LOG = LoggerFactory.getLogger(IdEntity.class);
	static final ObjectMapper MAPPER = new ObjectMapper();
	
	static final String ID = "_id";
	static final Class<?> DEFAULT_KEY_CLASS = String.class;
	
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
	Date getCreatedAt();

	/**
	 * @param createdAt
	 */
	void setCreatedAt(Date createdAt);


	/**
	 * @return
	 */
	@SuppressWarnings("unchecked")
	default Class<K> keyClass() {
		return (Class<K>) DEFAULT_KEY_CLASS;
	}
	
	
	
	/**
	 * @return
	 */
	default String asJson() {
		String json = null;
		try {
			json = MAPPER.writeValueAsString(this);
		} catch (JsonProcessingException e) {
			LOG.error("Exception while mapping entity: {}", e.getMessage());
		}
		return json;
	}
	
	
	
}
