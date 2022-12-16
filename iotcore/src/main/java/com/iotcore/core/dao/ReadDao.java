package com.iotcore.core.dao;

import java.io.Serializable;
import java.util.List;
import java.util.Map;


/**
 * @author jmgd6647
 *
 * @param <T>
 * @param <K>
 * 
 */
public interface ReadDao<T extends IdEntity<K>, K extends Serializable> extends Serializable {
	
	/** FLD_ID */
	static final String FLD_ID = "id";
	
	
	/**
	 * @param id
	 * @return Object entity or NULL if none found
	 */
	T findById(K id);

	/**
	 * @param ids
	 * @return
	 */
	List<T> findByIds(List<K> ids);
	
	
	/**
	 * Find all entities in the collection
	 * @return List containing the found Object entities
	 */
	List<T> findAll();
	
	/**
	 * @param offset
	 * @param count
	 * @param sortAscending 
	 * @param sortField 
	 * @param filters
	 * @return
	 */
	List<T> findAll(int offset, int count, String sortField, boolean sortAscending, Map<String, Object> filters);



}
