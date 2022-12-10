package com.iotcore.core.service;

import java.io.Serializable;

import com.iotcore.core.dao.IdEntity;

/**
 * 
 * @author <a href="mailto:garciadjx@gmail.com">J.M. Garcia</a>
 * @param <T>
 * @param <K>
 */
public interface DomainService<T extends IdEntity<K>, K extends Serializable>
		extends
			DomainReadService<T, K>,
			DomainWriteService<T, K> {


}
