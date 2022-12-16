/**
 * 
 */
package com.iotcore.core.dao.factory;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iotcore.core.dao.EntityDao;


/**
 * @author jmgarcia
 *
 */
/**
 * @author jmgarcia
 *
 */
public class DaoFactory implements Serializable {
	
	private static final long serialVersionUID = -4904108751565871869L;
	private static final Logger LOG = LoggerFactory.getLogger(DaoFactory.class);
	
	public static final String IMPL_SUFFIX = "Impl";
	
	private static Map<Class<?>, DaoFactory> daoFactoryMap = new HashMap<>();
	private static Set<DaoFactory> registeredFactories = new HashSet<DaoFactory>();
	
	
	/**
	 * @param <E>
	 * @param <D>
	 * @param daoClazz
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <D extends EntityDao<?, ?>> D getDao(Class<D> daoClazz) {
		
		Objects.requireNonNull(daoClazz);
		
		D dao = null;
		
		DaoFactory factory =  daoFactoryMap.get(daoClazz);
		if (factory != null) {
			dao = (D) factory.getDaoInstance(daoClazz);
			return dao;
		}
		
		LOG.warn("No DAO Factory registered for DAO {}", daoClazz.getSimpleName());
		if (registeredFactories.isEmpty()) {
			LOG.error("No DAO Factories found");
			return null;
		}
		
		for (DaoFactory fact : registeredFactories) {
			dao = (D) fact.getDaoInstance(daoClazz);
			if (dao != null) {
				return dao;
			}
		}
	
		LOG.error("No implementation found for {}", daoClazz.getSimpleName());
		return null;
	}
	

	/**
	 * @param factory
	 * @param daoClass
	 * @param implClass
	 */
	protected static void registerFactory(DaoFactory factory) {
		Objects.requireNonNull(factory);
		
		if (LOG.isTraceEnabled()) {
			LOG.trace("Registering DAO factory: {}", factory.getClass().getSimpleName());
		}
		registeredFactories.add(factory);
	}
	
	
	
	
	/**
	 * @param factory
	 * @param daoClass
	 * @param implClass
	 */
	private static void registerFactory(DaoFactory factory, Class<? extends EntityDao<?, ?>> daoClass) {
		Objects.requireNonNull(factory);
		Objects.requireNonNull(daoClass);
		
		if (LOG.isTraceEnabled()) {
			LOG.trace("Registering {} DAO implementation: {}", factory.getClass().getSimpleName());
		}
		
		if (!registeredFactories.contains(factory)) {
			registeredFactories.add(factory);
		}
		
		daoFactoryMap.put(daoClass, factory);	
		factory.registerDao(daoClass);
	}
	

	
	public static Set<DaoFactory> getFactories() {
		return Collections.unmodifiableSet(registeredFactories);
	}
	

	private Map<Class<? extends EntityDao<?, ?>>, EntityDao<?, ?>> daoInstances = new HashMap<>();
	private Map<Class<? extends EntityDao<?, ?>>, Class<? extends EntityDao<?, ?>>> daoImplementationsMap = null;
	private ClassLoader classLoader;

	
	/**
	 * 
	 */
	protected  DaoFactory() {	
		this.daoImplementationsMap = new HashMap<Class<? extends EntityDao<?, ?>>, Class<? extends EntityDao<?, ?>>>();
	}

	
	protected EntityDao<?, ?> getDaoInstance(Class<? extends EntityDao<?, ?>> daoClazz) {
		EntityDao<?, ?> dao =  daoInstances.get(daoClazz);
		if (dao != null) {
			return dao;
		}
		
		Class<? extends EntityDao<?, ?>> implClass = daoImplementationsMap.get(daoClazz);
		if (implClass != null) {
			dao = createInstance(daoClazz);
		}
		
		return dao;
	}
	
	/**
	 * @param daoClass
	 * @return
	 */
	protected void registerDao(Class<? extends EntityDao<?, ?>> daoClass) {
		registerFactory(this, daoClass);
		
		Class<? extends EntityDao<?, ?>> implClass = getDaoImplementationClass(daoClass);
		if (implClass != null) {
			daoImplementationsMap.put(daoClass, implClass);
		}
		else {
			LOG.warn("Couldn't find an implementation DAO for {}", daoClass.getSimpleName());
		}
	}
	
	/**
	 * @param daoClass
	 * @param implClass
	 */
	protected void registerDao(Class<? extends EntityDao<?, ?>> daoClass, Class<? extends EntityDao<?, ?>> implClass) {
		registerFactory(this, daoClass);
		daoImplementationsMap.put(daoClass, implClass);
	}


	
	protected Class<? extends EntityDao<?,?>>  getDaoImplementationClass(Class<?> daoClass) {
		String implName = daoClass.getName() + IMPL_SUFFIX;
		if (LOG.isTraceEnabled()) {
			LOG.trace("Loading DAO implementation: {}", implName);
		}
		return loadDaoClass(implName);
	}
	
	
	protected <D extends EntityDao<?, ?>> D createInstance(Class<D> daoClazz) {
		@SuppressWarnings("unchecked")
		Class<? extends D> implClass = (Class<? extends D>) daoImplementationsMap.get(daoClazz);
		D dao = null;
		if (implClass != null) {
			
			if (LOG.isTraceEnabled()) {
				LOG.trace("Instantiating DAO implementation for {}", daoClazz.getSimpleName());
			}
			try {
				Constructor<? extends D> cons = implClass.getConstructor();            
		    	dao = cons.newInstance();
		    } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException  e) {
		    	LOG.error("{}", e.getMessage());
		    	e.printStackTrace();
		    }

			return dao;
		}
		
		return null;
	}


	/**
	 * @return the classLoader
	 */
	protected ClassLoader getClassLoader() {
		if (classLoader == null) {
			classLoader = Thread.currentThread().getContextClassLoader();
		}
		return classLoader;
	}


	/**
	 * @param classLoader the classLoader to set
	 */
	protected void setClassLoader(ClassLoader classLoader) {
		this.classLoader = classLoader;
	}

	
	
	@SuppressWarnings("unchecked")
	protected Class<? extends EntityDao<?, ?>> loadDaoClass(String className) {
		Class<? extends EntityDao<?, ?>> clazz = null;
		try {
			clazz =  (Class<? extends EntityDao<?, ?>>) Class.forName(className, true, getClassLoader());
		} catch (ClassNotFoundException e) {
			if (LOG.isTraceEnabled()) {
				LOG.trace("DAO implementation does not exist: {}", className);
			}
		}
		return clazz;
	}
	



}
