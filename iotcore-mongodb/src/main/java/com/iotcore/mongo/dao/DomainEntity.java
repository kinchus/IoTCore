package com.iotcore.mongo.dao;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.iotcore.core.dao.IdEntity;

import dev.morphia.annotations.Id;


/**
 * @author jmgarcia
 *
 * @param <E>
 * @param <K>
 */
@JsonInclude( Include.NON_NULL )
@JsonIgnoreProperties( ignoreUnknown = true )
public abstract class DomainEntity<K extends Serializable> implements IdEntity<K> {
	
	private static final long serialVersionUID = 5121857647174425393L;
	
	@Id
	public K _id;
	private Class<K> keyClass;
	private Date createdAt;
	
	/**
	 * 
	 */
	public  DomainEntity() {
	}
	
	/**
	 * @param entityClass
	 * @param keyClass
	 */
	public  DomainEntity(Class<K> keyClass) {
		this.keyClass = keyClass;
	}
	

	/**
	 *
	 */
	@Override
	public K getId() {
		return _id;
	}

	/**
	 *
	 */
	@Override
	public void setId(K id) {
		this._id = id;
	}
	

	/**
	 *
	 */
	@Override
	public Class<K> keyClass() {
		return keyClass;
	}

		
	/**
	 * @return
	 */
	public Date getCreatedAt() {
		return createdAt;
	}

	/**
	 * @param createdAt
	 */
	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}


	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof IdEntity<?>)) {
			return false;
		}
		
		IdEntity<?> other = (IdEntity<?>)obj;
		
		if ((getId() == null) || (other.getId() == null)) {
			return false;
		}
		
		return getId().equals(other.getId());
		
		
	}

}
