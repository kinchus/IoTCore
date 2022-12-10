/**
 * 
 */
package com.iotcore.core.dao.factory;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iotcore.core.dao.Dao;
import com.iotcore.core.dao.EntityDao;
import com.iotcore.core.dao.IdEntity;


/**
 * @author jmgarcia
 *
 */
public abstract class DaoFactory implements Serializable {
	
	private static final long serialVersionUID = -4904108751565871869L;
	private static final Logger LOG = LoggerFactory.getLogger(DaoFactory.class);

	private static final Map<Class<?>, DaoFactory> factoryMap = new HashMap<>();
	protected static DaoFactory _instance;
	
	
		
	
	/**
	 * @param factory
	 * @param daoClasses
	 */
	@SuppressWarnings("unchecked")
	protected static void registerFactory(DaoFactory factory, Class<?> daoClasses[], Class<?> implClasses[]) {
		Objects.requireNonNull(factory);
		Objects.requireNonNull(daoClasses);
		Objects.requireNonNull(implClasses);
		
		for (int i=0; i<daoClasses.length; i++) {
			registerFactory(factory, (Class<? extends Dao<?>>)daoClasses[i], (Class<? extends Dao<?>>)implClasses[i]);
		}
	}
	
	protected static void registerFactory(DaoFactory factory, Class<? extends Dao<?>> daoClass) {
		Objects.requireNonNull(factory);
		Objects.requireNonNull(daoClass);
		
		if (LOG.isTraceEnabled()) {
			LOG.trace("Registering {} DAO: {}", factory.getClass().getSimpleName(), daoClass.getSimpleName());
		}
		factoryMap.put(daoClass, factory);
		Class<? extends Dao<?>> implClass;
		try {
			implClass = factory.getDaoImplementationClass(daoClass);
			factory.addDaoImplementation(daoClass, implClass);
		} catch (ClassNotFoundException e) {
			LOG.error("Exception: {}", e.getMessage());
			e.printStackTrace();
		}
		
	}
	
	protected static void registerFactory(DaoFactory factory, Class<? extends Dao<?>> daoClass, Class<? extends Dao<?>> implClass) {
		Objects.requireNonNull(factory);
		Objects.requireNonNull(daoClass);
		Objects.requireNonNull(implClass);
		
		if (LOG.isTraceEnabled()) {
			LOG.trace("Registering {} DAO implementation: {}", factory.getClass().getSimpleName(), implClass.getSimpleName());
		}
		factoryMap.put(daoClass, factory);	
		factory.addDaoImplementation(daoClass, implClass);
	}
	
	
	/**
	 * @param daoClazz
	 * @return
	 */
	protected static DaoFactory getDaoFactory(Class<? extends Dao<?>> daoClazz) {
		return  factoryMap.get(daoClazz);
	}
	
	/**
	 * @param <E>
	 * @param <D>
	 * @param daoClazz
	 * @return
	 */
	public static <E extends IdEntity<?>, D extends Dao<E>> D getDaoImplementation(Class<D> daoClazz) {
		
		Objects.requireNonNull(daoClazz);
		
		String ifaceName = daoClazz.getSimpleName();
		
		DaoFactory factory =  factoryMap.get(daoClazz);
		if (factory == null) {
			LOG.error("No DaoFactory found for {}", ifaceName);
			return null;
		}	
		
		D dao = factory.getDao(daoClazz);
		if (dao == null) {
			LOG.error("DAO instance is null!!", daoClazz.getSimpleName());
		}
		
		
		return dao;
	}
	
	
	public static DaoFactory getInstance() {
		return _instance;
	}
	



	private String implementationPackage = null;
	private Map<String, Class<? extends Dao<?>>> daoClassMap = null;
	private ClassLoader classLoader;

	protected DaoFactory() {	
		this.daoClassMap = new HashMap<String, Class<? extends Dao<?>>>();
	}


	/**
	 * @param implPackage
	 */
	protected DaoFactory(String implPackage) {	
		this.implementationPackage = implPackage;
		this.daoClassMap = new HashMap<String, Class<? extends Dao<?>>>();
	}


	protected void setClassLoader(ClassLoader  classLoader) {
		this.classLoader = classLoader ;
	}

	protected void addDaoImplementation(Class<?> ifaceClass, Class<? extends Dao<?>> implClass) {
		daoClassMap.put(ifaceClass.getSimpleName(), implClass);
	}
	
	public Class<? extends Dao<?>>  getDaoImplementationClass(Class<?> ifaceClass) throws ClassNotFoundException {
		String daoClassName = implementationPackage + ifaceClass.getSimpleName() + "Impl";
		@SuppressWarnings("unchecked")
		Class<? extends EntityDao<?,?>> daoClass = (Class<? extends EntityDao<?, ?>>) Class.forName(daoClassName, true, classLoader);
		return daoClass;
	}
	
	@SuppressWarnings("unchecked")
	public <D extends Dao<?>> D getDao(Class<D> clazz) {
		 
		String ifaceName = clazz.getSimpleName();
		String daoClassName = implementationPackage + ifaceName + "Impl";
		
		if (LOG.isTraceEnabled()) {
			LOG.trace("Getting {} implementation: {}", ifaceName,  daoClassName);
		}
		
		Class<D> implClass = (Class<D>) daoClassMap.get(ifaceName);
		if (implClass == null) {
			try {
				implClass = (Class<D>) getDaoImplementationClass(clazz);
			} catch (ClassNotFoundException e) {
				LOG.error("Exception: {}", e.getMessage());
				return null;
			}
			daoClassMap.put(ifaceName, implClass);
		}
		
		D dao = null;
		
		try {
			Constructor<D> cons = implClass.getConstructor();            
	    	dao = cons.newInstance();
	    } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException  e) {
	    	LOG.error("{}", e.getMessage());
	    	e.printStackTrace();
	    }
		
		return dao;
	}



}
