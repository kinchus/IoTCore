/**
 * 
 */
package com.iotcore.mongo.dao.factory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iotcore.core.dao.EntityDao;
import com.iotcore.core.dao.factory.DaoFactory;
import com.iotcore.mongo.MongoManager;

import dev.morphia.Datastore;

/**
@author <a href="mailto:garciadjx@gmail.com">J.M. Garcia</a>
 *
 */
public class MongoDaoFactory extends DaoFactory {
	
	private static final long serialVersionUID = 4812816954008326969L;
	private static final Logger LOG = LoggerFactory.getLogger(MongoDaoFactory.class);
	public static final String IMPL_PACKAGE = ".mongo.";
	
	
	private static  MongoDaoFactory instance = null;

	/**
	 * @return the instance
	 */
	public static MongoDaoFactory getInstance() {
		if (instance == null) {
			instance = new MongoDaoFactory();
			DaoFactory.registerFactory(instance);
		}
		return instance;
	}
	
	private final Datastore ds = MongoManager.getInstance().getDatastore();
	
	/**
	 * 
	 */
	private MongoDaoFactory() {
		super();
	}
	
	
	protected Class<? extends EntityDao<?,?>>  getDaoImplementationClass(Class<?> daoClass) {
		Class<? extends EntityDao<?,?>> implClass = super.getDaoImplementationClass(daoClass);
		if (implClass == null) {
			StringBuffer implName = new StringBuffer();
			implName.append(daoClass.getPackage().getName());
			implName.append(IMPL_PACKAGE);
			implName.append(daoClass.getSimpleName());
			implName.append(IMPL_SUFFIX);
			if (LOG.isTraceEnabled()) {
				LOG.trace("Loading DAO implementation: {}", implName);
			}
			implClass = loadDaoClass(implName.toString());
		}
		
		
		return implClass;
	}
	

	protected <D extends EntityDao<?, ?>> D createInstance(Class<D> daoClazz) {
		@SuppressWarnings("unchecked")
		Class<? extends D> implClass = (Class<? extends D>) getDaoImplementationClass(daoClazz);
		if (implClass != null) {
			
			if (LOG.isTraceEnabled()) {
				LOG.trace("Instantiating DAO implementation for {}", daoClazz.getSimpleName());
			}
			Constructor<? extends D> cons = null;            
	    	try {
				cons = implClass.getConstructor(Datastore.class);   
				return cons.newInstance(ds);
		    } catch (IllegalArgumentException | NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | InvocationTargetException  e) {
		    	
		    }
	    	
	    	try {
    			cons = implClass.getConstructor();
    			return cons.newInstance();
    		} catch (IllegalArgumentException | NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | InvocationTargetException  e) {
    			
		    }
		}
		
		return null;
	}

}
