package com.iotcore.core.dao.factory;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iotcore.core.dao.EntityDao;
import com.iotcore.core.util.Reflection;


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
	
		LOG.error("No DAO implementation found for {}", daoClazz.getSimpleName());
		return null;
	}
		
	
	public static Set<DaoFactory> getFactories() {
		return Collections.unmodifiableSet(registeredFactories);
	}
	
	

	/**
	 * @param factory
	 * @param daoClass
	 * @param implClass
	 */
	public static void registerFactory(DaoFactory factory) {
		Objects.requireNonNull(factory);
		
		registeredFactories.add(factory);
		factory.registered = true;
	}
	

	/**
	 * @param factory
	 * @param daoClass
	 * @param implClass
	 */

	public static synchronized void registerFactory(DaoFactory factory, Collection<Class<? extends EntityDao<?, ?>>> daoClasses ) {
		Objects.requireNonNull(factory);
		Objects.requireNonNull(daoClasses);
		
		registeredFactories.add(factory);
		factory.registered = true;
		
		for (Class<? extends EntityDao<?, ?>> clazz : daoClasses) {
			if (clazz.isInterface()) {
				if (LOG.isTraceEnabled()) {
					LOG.trace("Registering DAO interface {}", clazz.getName());
				}
				daoFactoryMap.put(clazz, factory);
				factory.registerDao(clazz);
			}
			else {
				@SuppressWarnings("unchecked")
				Class<? extends EntityDao<?, ?>>[] ifaces = (Class<? extends EntityDao<?, ?>>[]) clazz.getInterfaces();
				if (ifaces.length > 0) {
					Class<? extends EntityDao<?, ?>> daoIface = ifaces[0];
					if (LOG.isTraceEnabled()) {
						LOG.trace("Registering DAO interface {}", clazz.getName());
					}
					daoFactoryMap.put(daoIface, factory);
					factory.registerDao(daoIface, clazz);
				}
				else {
					LOG.error("Class {} is not a DAO interface", clazz.getName());
				}
			}
		}
	}


	private Map<Class<? extends EntityDao<?, ?>>, EntityDao<?, ?>> daoInstances = null;
	private Map<Class<? extends EntityDao<?, ?>>, Class<? extends EntityDao<?, ?>>> daoImplementationsMap = null;
	private ClassLoader classLoader;
	private boolean registered = false;
	
	/**
	 * 
	 */
	protected  DaoFactory() {	
		daoImplementationsMap = new HashMap<Class<? extends EntityDao<?, ?>>, Class<? extends EntityDao<?, ?>>>();
		daoInstances = new HashMap<>();
	}
	
	
	/**
	 * @param daoClass
	 * @return
	 */
	public void registerDao(Class<? extends EntityDao<?, ?>> daoClass) {
		
		if (!registered) {
			registerFactory(this);
		}
		
		Class<? extends EntityDao<?, ?>> implClass = getDaoImplementationClass(daoClass);
		if (implClass != null) {
			daoImplementationsMap.put(daoClass, implClass);
		}
		else {
			LOG.warn("Couldn't find an implementation DAO for {}", daoClass.getSimpleName());
		}
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
	 * @param implClass
	 */
	protected void registerDao(Class<? extends EntityDao<?, ?>> daoClass, Class<? extends EntityDao<?, ?>> implClass) {
		
		if (!registered) {
			registerFactory(this);
		}
		
		daoImplementationsMap.put(daoClass, implClass);
	}


	
	protected Class<? extends EntityDao<?,?>>  getDaoImplementationClass(Class<?> daoClass) {
		String implName = daoClass.getName() + IMPL_SUFFIX;
		if (LOG.isTraceEnabled()) {
			LOG.trace("Loading DAO implementation: {}", implName);
		}
		return loadDaoClass(implName);
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
	
	protected <D extends EntityDao<?, ?>> D createInstance(Class<D> daoClazz) {
		@SuppressWarnings("unchecked")
		Class<? extends D> implClass = (Class<? extends D>) daoImplementationsMap.get(daoClazz);
		if (implClass != null) {
			try {
				return Reflection.newInstance(implClass);
			} catch (InstantiationException e) {
				LOG.error("{}", e.getMessage());
			}
		}
		
		return null;
	}



}
