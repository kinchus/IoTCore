/**
 * 
 */
package com.iotcore.core.dao;

import java.io.Serializable;

/**
 * @author jmgarcia
 *
 */
public interface Dao<T> extends Serializable {

	T save(T entity);
	
	void delete(T entity);
}
