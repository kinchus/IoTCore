package com.iotcore.core.dao;

import java.io.Serializable;
import java.util.List;

import com.iotcore.core.model.exception.ServiceException;



/**
 * @author jmgd6647
 *
 * @param <T>
 * @param <K>
 * 
 */
public interface EntityDao<T extends IdEntity<K>, K extends Serializable> extends Serializable {
	
	/** FLD_ID */
	static final String ENTITY_ID = "_id";
	static final String FLD_CREATED = "createdAt";
	
	/**
	 * Find all entities in the collection
	 * @return List containing the found Object entities
	 */
	List<T> findAll(Integer start, Integer count);
	

	/**
	 * @param id
	 * @return Object entity or NULL if none found
	 */
	T findById(K id);

	/**
	 * @param entity
	 * @return updated object
	 * @throws ServiceException 
	 */
	T save(T entity);
	

	/**
	 * @param id
	 * @return
	 */
	void delete(K id);
	
	

}
