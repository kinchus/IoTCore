/**
 * 
 */
package com.iotcore.core.dao.cache;

import java.io.Serializable;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iotcore.core.dao.IdEntity;
import com.iotcore.core.service.DomainService;
import com.iotcore.core.util.lru.LruMapCache;

/**
 * @param <E> 
 *
@author <a href="mailto:garciadjx@gmail.com">J.M. Garcia</a>
 * 
 */
public abstract class EntityCache<E extends IdEntity<K>, K extends Serializable> implements Cache<E,K>, Serializable {
	
	private static final long serialVersionUID = 8083230605823808983L;
	
	private static final Logger LOG  = LoggerFactory.getLogger(EntityCache.class);
	
	private String name;
	private LruMapCache<K, E> map = null;
	private DomainService<E, K> service = null;
	
	/**
	 * Gets an object from the underlying service
	 * @param key
	 * @return Object instance (if found) or null
	 */
	protected abstract E retrieve(K key);

	
	/**
	 * 
	 */
	public EntityCache() {
		this.map = new LruMapCache<K, E>(TTL_ITEMS_DEFAULT, MAX_ITEMS_DEFAULT);
	}
	
	/**
	 * @param service
	 */
	public EntityCache(DomainService<E, K> service) {
		this.map = new LruMapCache<K, E>(TTL_ITEMS_DEFAULT, MAX_ITEMS_DEFAULT);
		setService(service);
	}
	
	/**
	 * @param itemsTtl
	 * @param maxItems
	 */
	public EntityCache(int itemsTtl, int maxItems) {
		this.map = new LruMapCache<K, E>(itemsTtl, maxItems);
	}

	/**
	 * @param service
	 * @param itemsTtl
	 * @param maxItems
	 */
	public EntityCache(DomainService<E, K> service, int itemsTtl, int maxItems) {
		this.map = new LruMapCache<K, E>(itemsTtl, maxItems);
		setService(service);
	}

	/**
	 * @param service the service to set
	 */
	public void setService(DomainService<E, K> service) {
		this.service = service;
	}

	/**
	 * @param key
	 * @return The entity with the given key
	 */
	@Override
	public synchronized E get(K key) {
		
		E ret = getMap().get(key);
		
		if (ret != null) {
			return ret;
		}
		
		ret = retrieve(key);
		if (ret != null) {
			getMap().put(key, ret);
		}
		
		return ret;
	}
	
	/**
	 * @param key
	 * @param obj
	 * @see com.northstar.domain.cache.Cache#putDetailField(java.lang.String, com.northstar.domain.model.id.StringIdEntity)
	 */
	@Override
	public synchronized void put(K key, E obj) {
		if (LOG.isTraceEnabled()) {
			LOG.trace("{} Caching object {} (current size: {})", name, key, map.size());
		}
		if (getMap().get(key) == null) {
			getMap().put(key, obj);
		}
		else {
			getMap().replace(key, obj);
		}
	}
	
	
	
	/**
	 * @return the service
	 */
	protected DomainService<E, K> getService() {
		return service;
	}
	
	/**
	 * @return the map
	 */
	protected Map<K, E> getMap() {
		return map;
	}


}
