package com.iotcore.core.dao;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public interface IDaoFactory {
	
	static final String IMPLEMENTATION_PACKAGE = "com.iotcore.infrastructure.mongo.dao";
	
	static String getImplementationPackage() {
		return IMPLEMENTATION_PACKAGE;
	}
	
	
	public static <E extends IdEntity<E>> EntityDao<E,?> getDaoFor(Class<E> entityClass) {
	    
	    try {
	    	String daoClassName = getImplementationPackage() + "." + entityClass.getSimpleName() + "DaoImpl";
	    	@SuppressWarnings("unchecked")
			Class<EntityDao<E,?>> eDao = (Class<EntityDao<E,?>>)(Class<?>)Class.forName(daoClassName);
	    	Constructor<EntityDao<E,?>> cons = eDao.getConstructor();            
	    	return cons.newInstance();
	    } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException  ex) {
	      return null;
	    }
	}

}
