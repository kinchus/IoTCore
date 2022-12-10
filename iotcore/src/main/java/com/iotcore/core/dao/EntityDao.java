package com.iotcore.core.dao;

import java.util.List;

import com.iotcore.core.model.exception.ServiceException;



/**
 * @author jmgd6647
 *
 * @param <T>
 * @param <K>
 * 
 */
public interface EntityDao<T extends IdEntity<K>, K> extends Dao<T> {
	
	/** FLD_ID */
	static final String ENTITY_ID = "_id";
	static final String FLD_CREATED = "createdAt";
	static final String FLD_UPDATED = "updatedAt";
	
	
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
	@Override
	T save(T entity);
	
	/**
	 * @param object
	 * @return
	 */
	default List<T> save(List<T> objects)  {
		objects.forEach(o -> save(o));
		return objects;
	}

	/**
	 * @param id
	 * @return
	 */
	void delete(K id);

	/**
	 * @param entity
	 * @return number of deleted entities
	 */
	@Override
	void delete(T entity);
	
	/**
	 * @param entities
	 * @return
	 */
	default void delete(List<T> objects){
		objects.forEach(o -> delete(o));
	}

	

}
