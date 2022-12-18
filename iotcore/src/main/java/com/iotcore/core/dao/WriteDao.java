package com.iotcore.core.dao;

import java.io.Serializable;
import java.util.List;


/**
 * @author jmgd6647
 *
 * @param <T>
 * @param <K>
 * 
 */
public interface WriteDao<T extends IdEntity<K>, K extends Serializable> extends Serializable {
	
	/** FLD_ID */
	static final String FLD_ID = "_id";
	

	/**
	 * @param entity
	 * @return updated object
	 */
	T save(T entity);

	/**
	 * @param entity
	 * @return
	 */
	List<T> save(List<T> entity);

	/**
	 * @param _id
	 * @return
	 */
	void delete(K id);

	/**
	 * @param entity
	 * @return number of deleted entities
	 */
	void delete(T entity);
	
	/**
	 * @param entities
	 * @return
	 */
	void delete(List<T> entities);

}
