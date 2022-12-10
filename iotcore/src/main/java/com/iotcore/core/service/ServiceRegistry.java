/**
 * 
 */
package com.iotcore.core.service;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iotcore.core.model.exception.ServiceException;

/**
 * @author jmgarcia
 *
 */
public class ServiceRegistry implements Serializable {

	private static final long serialVersionUID = -7865598825039407786L;
	private static final Logger LOG  = LoggerFactory.getLogger(ServiceRegistry.class);
	
	private static final Map<ObjectIndex, Object> serviceMap = new HashMap<ObjectIndex, Object>();

	
	/**
	 * @param name
	 * @param service
	 */
	public static void registerService(String name, Object service) {
		ObjectIndex idx = ObjectIndex.from(name);
		if (LOG.isTraceEnabled()) {
			LOG.trace("Registering service {}: {}", name, idx.hashCode());
		}
		serviceMap.put(ObjectIndex.from(name), service);
	}

	/**
	 * @param clazz
	 * @param service
	 */
	public static void registerService(Class<?> clazz, Object service) {
		ObjectIndex idx = ObjectIndex.from(clazz);
		if (LOG.isTraceEnabled()) {
			LOG.trace("Registering service {}: {}", clazz.getSimpleName(), idx.hashCode());
		}
		serviceMap.put(idx, service);
	}
	
	/**
	 * @param name
	 * @param service
	 */
	public static void registerService(Object service) {
		ObjectIndex idx = ObjectIndex.from(service.getClass());
		if (LOG.isTraceEnabled()) {
			LOG.trace("Registering service {}: {}", service.getClass().getSimpleName(), idx.hashCode());
		}
		serviceMap.put(idx, service);
	}
	
	/**
	 * @param name
	 * @return
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public static <T> T  getService(String name) throws ServiceException {
		ObjectIndex idx = ObjectIndex.from(name);
		if (LOG.isTraceEnabled()) {
			LOG.trace("Requesting service {} (IDX:{})", name, idx.hashCode());
		}
	
		Object ret = serviceMap.get(idx);
		if (ret == null) {
			throw new ServiceException("Service " + name + " not found");
		}
		return (T) ret;
		
	}
	
	/**
	 * @param name
	 * @return
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getService(Class<T> clazz) throws ServiceException {
		ObjectIndex idx = ObjectIndex.from(clazz);
		if (LOG.isTraceEnabled()) {
			LOG.trace("Requesting service {} (IDX:{})", clazz.getSimpleName(), idx.hashCode());
		}
	
		Object ret = serviceMap.get(ObjectIndex.from(clazz));
		if (ret == null) {
			throw new ServiceException("Service for " + clazz.getName() + " not found");
		}
		return (T) ret;
	}
	
	
	/**
	 *
	 */
	public static class ObjectIndex {
		
		public static ObjectIndex from(String key) {
			return new ObjectIndex(key);
		}
		
		public static ObjectIndex from(Class<?> clazz) {
			return new ObjectIndex(clazz);
		}
		
		private String name;
		
		/**
		 * @param name
		 */
		private ObjectIndex(String name) {
			this.name = name;
		}

		/**
		 * @param clazz
		 */
		private ObjectIndex(Class<?> clazz) {
			this.name = clazz.getName();
		}

		@Override
		public int hashCode() {
			return name.hashCode();
		}

		@Override
		public String toString() {
			return name;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof ObjectIndex) {
				ObjectIndex other = (ObjectIndex)obj;
				return name.equals(other.name);
			}
			return false;
		}
		
	}

}
