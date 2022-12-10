package com.iotcore.core.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import com.iotcore.core.dao.IdEntity;
import com.iotcore.core.model.exception.ServiceException;

/**
 * 
 * @author <a href="mailto:garciadjx@gmail.com">J.M. Garcia</a>
 * @param <T> 
 * @param <K> 
 */
public interface DomainWriteService<T extends IdEntity<K>, K extends Serializable> extends Serializable {
	
	
	/**
	 * @param object
	 * @return
	 * @throws Exception 
	 */
	T create(T object) throws ServiceException;


	/**
	 * @param objects
	 * @return
	 * @throws Exception 
	 */
	default List<T> create(List<T> objects)  throws ServiceException {

		Objects.nonNull(objects);
		
		List<T> ret = new ArrayList<T>(objects.size());
		objects.stream().forEach(t -> {
			try {
				ret.add(create(t));
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		
		return ret;
		
	}


	/**
	 * @param object
	 * @return
	 * @throws Exception 
	 */
	T update(T object) throws ServiceException;

	/**
	 * @param ent
	 * @throws Exception 
	 */
	void delete(T ent) throws ServiceException;

	/**
	 * @param objects
	 * @return 
	 * @throws Exception 
	 */
	default int delete(Collection<T> objects)  throws ServiceException {
		Objects.nonNull(objects);
		List<T> ret = new ArrayList<T>(objects.size());
		objects.stream().forEach(t -> {
			try {
				delete(t);
				ret.add(t);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		return ret.size();
	}

}
