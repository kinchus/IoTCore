package com.iotcore.core.dao.cache;

import com.iotcore.core.dao.IdEntity;

/**
 * 
 * @param <E>
 * @author <a href="mailto:garciadjx@gmail.com">J.M. Garcia</a>
 */
public interface Cache<E extends IdEntity<K>,K> {

	/** Time To Live for the items stored in the cache (seconds) */
	static int TTL_ITEMS_DEFAULT = 600;
	/** Maximum number of items in the cache */
	static int MAX_ITEMS_DEFAULT = 1000;
	
	/**
	 * Gets an object from the cache
	 * @param key
	 * @return matching object
	 */
	E get(K key);

	
	/**
	 * @param key
	 * @param obj
	 */
	void put(K key, E obj);
	

}