package com.iotcore.core.service;

import java.io.Serializable;
import java.util.List;

import com.iotcore.core.dao.IdEntity;
import com.iotcore.core.model.exception.ObjectNotFoundException;

/**
 * 
 * @author <a href="mailto:garciadjx@gmail.com">J.M. Garcia</a>
 * @param <T> 
 * @param <K> 
 */
public interface DomainReadService<T extends IdEntity<K>, K extends Serializable> extends Serializable {

	
	/**
	 * @param _id
	 * @return
	 * @throws ObjectNotFoundException
	 */
	T getById(K id) throws ObjectNotFoundException;

	/**
	 * @return
	 */
	List<T> getAll();
	
	/**
	 * @param pageToken
	 * @param maxResults
	 * @return
	 */
	List<T> getAll(Integer offset, Integer maxResults);
	
}
