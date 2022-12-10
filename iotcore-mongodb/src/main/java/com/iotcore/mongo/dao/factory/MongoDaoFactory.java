/**
 * 
 */
package com.iotcore.mongo.dao.factory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iotcore.core.dao.Dao;
import com.iotcore.core.dao.EntityDao;
import com.iotcore.core.dao.IdEntity;
import com.iotcore.core.dao.factory.DaoFactory;
import com.iotcore.mongo.MongoManager;
import com.iotcore.mongo.dao.BaseDao;
import com.iotcore.mongo.dao.DomainEntity;
import com.iotcore.mongo.dao.GenericDao;

import dev.morphia.Datastore;

/**
@author <a href="mailto:garciadjx@gmail.com">J.M. Garcia</a>
 *
 */
public class MongoDaoFactory extends DaoFactory {
	
	private static final long serialVersionUID = 4812816954008326969L;
	private static final Logger LOG = LoggerFactory.getLogger(MongoDaoFactory.class);
	private static final String DAO_IMPLEMENTATION_PACKAGE = BaseDao.class.getPackageName() + ".";
	private static final MongoDaoFactory instance = new MongoDaoFactory();
	
	
	
	private static boolean initialized = false;
	private static Datastore ds = null;
	
	/**
	 * 
	 */
	public static synchronized void init(Class<?> [] daoClasses) {
		if (initialized) {
			return;
		}
		
		
		registerFactory(instance, daoClasses, null);
		
		MongoManager.getInstance().init();
		initialized = true;
	}

	
	
	/**
	 * @param daoClass
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static  <E extends IdEntity<?>, D extends Dao<E>> D getDaoImplementation(Class<D> daoIface) {
		StringBuffer  daoImplName = new StringBuffer(DAO_IMPLEMENTATION_PACKAGE);
    	daoImplName.append(daoIface.getSimpleName());
    	daoImplName.append("Impl");
    	
	    try  {
	    	if (LOG.isTraceEnabled())
	    		LOG.trace("Creating instance of \"{}\"", daoImplName.toString());
	    	Class<D> implDao = (Class<D>)Class.forName(daoImplName.toString());
	    	Constructor<D> cons = implDao.getConstructor(Datastore.class);
	    	return cons.newInstance(getDatastore());  
	    } catch (Exception e) {
	    	throw new RuntimeException("Can not instantiate DAO: " + daoImplName.toString(), e);
		}
	}
	


	/**
	 * @param <E>
	 * @param entityClass
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <E extends DomainEntity<?,?>> EntityDao<E,?> getDaoForEntity(Class<E> entityClass) {
	    String entityName = entityClass.getSimpleName();
	    try {
	    	StringBuffer  daoClassName = new StringBuffer(DAO_IMPLEMENTATION_PACKAGE);
	    	daoClassName.append(entityName);
	    	daoClassName.append("DaoImpl");
	    	if (LOG.isTraceEnabled())
	    		LOG.trace("Building instance of DAO class \"{}\"", daoClassName.toString());
	    	Class<EntityDao<E,?>> eDao = (Class<EntityDao<E,?>>)(Class<?>)Class.forName(daoClassName.toString());
	    	Constructor<EntityDao<E,?>> cons = eDao.getConstructor();        
	    	return cons.newInstance();
	    } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException  e) {
	    	LOG.error(e.getMessage());
	    	e.printStackTrace();
	      return null;
	    }
	}
	
	
	/**
	 * @param <E>
	 * @param entityClass
	 * @param ds
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <E extends DomainEntity<?,K>, K> GenericDao<E,K> getDaoForEntity(Class<E> entityClass, Datastore ds) {
		String entityName = entityClass.getSimpleName();
	    try {
	    	StringBuffer  daoClassName = new StringBuffer(DAO_IMPLEMENTATION_PACKAGE);
	    	daoClassName.append(entityName);
	    	daoClassName.append("DaoImpl");
	    	if (LOG.isTraceEnabled())
	    		LOG.trace("Building instance of DAO class \"{}\"", daoClassName.toString());
	    	Class<GenericDao<E,K>> eDao = (Class<GenericDao<E,K>>)(Class<?>)Class.forName(daoClassName.toString());
	    	Constructor<GenericDao<E,K>> cons = eDao.getConstructor(Datastore.class);            
	    	return cons.newInstance(ds);
	    } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException  e) {
	    	LOG.error(e.getMessage());
	    	e.printStackTrace();
	      return new GenericDao<E,K>(entityClass, ds);
	    }
	}
	
	
	/**
	 * @return
	 */
	private static Datastore getDatastore() {
		if (ds == null) {
			 ds = MongoManager.getInstance().getDatastore();
		}
		return ds;
	}
	
	
	/**
	 * 
	 */
	private MongoDaoFactory() {
		super(DAO_IMPLEMENTATION_PACKAGE);
	}


}
