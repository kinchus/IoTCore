package com.iotcore.mongo.dao;

import java.util.Date;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.iotcore.core.dao.IdEntity;

import dev.morphia.annotations.Id;
import dev.morphia.annotations.Indexed;
import dev.morphia.annotations.PrePersist;
import dev.morphia.annotations.Property;
import dev.morphia.annotations.Transient;


/**
 * @author jmgarcia
 *
 * @param <E>
 * @param <K>
 */
@JsonInclude( Include.NON_NULL )
@JsonIgnoreProperties( ignoreUnknown = true )
public abstract class DomainEntity<E extends DomainEntity<E,K>, K> implements IdEntity<K> {

	
	private static final long serialVersionUID = 5121857647174425393L;
	private static Logger LOG = LoggerFactory.getLogger(DomainEntity.class);
	private static ObjectMapper mapper = null;
	
	public static final String DELETED_FIELD = "deleted";
	public static final String CLASSNAME_FIELD = "className";
	
	
	protected static ObjectMapper getMapper() {
		if (mapper == null) {
			mapper = new ObjectMapper();
		}
		return mapper;
	}

	
	@Id
	private K id;
	@Property
	private Date createdAt;
	@Property
	private Date updatedAt;
	@Property
	private Map<String, String> attributesMap;
	@Indexed
	@Property
	private boolean deleted = false;
	
	@Transient
	private Class<E> entityClass;
	@Transient
	private Class<K> keyClass;
	
	/**
	 * 
	 */
	public  DomainEntity() {
	}
	
	/**
	 * @param entityClass
	 * @param keyClass
	 */
	public  DomainEntity(Class<E> entityClass, Class<K> keyClass) {
		this.entityClass = entityClass;
		this.keyClass = keyClass;
	}
	
	/**
	 * 
	 */
	@PrePersist
    protected void prePersist() {
		Date now = new Date();
        if (createdAt == null) {
        	createdAt = now;
        }
        this.updatedAt = now;
    }
	
	/**
	 *
	 */
	@Override
	public K getId() {
		return id;
	}

	/**
	 *
	 */
	@Override
	public void setId(K id) {
		this.id = id;
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
	public Class<E> getEntityClass() {
		return entityClass;
	}
	
	/**
	 * @param clazz
	 */
	public void setEntityClass(Class<E>  clazz) {
		entityClass = clazz;
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

	/**
	 * @return
	 */
	public Date getUpdatedAt() {
		return updatedAt;
	}

	/**
	 * @param updatedAt
	 */
	public void setUpdatedAt(Date updatedAt) {
		this.updatedAt = updatedAt;
	}

	/**
	 * @return
	 */
	public boolean isDeleted() {
		return deleted;
	}

	/**
	 * @param deleted
	 */
	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	
	/**
	 * @return
	 */
	public String asJson() {
		String json = null;
		try {
			json = getMapper().writeValueAsString(this);
		} catch (JsonProcessingException e) {
			LOG.error("Exception while mapping entity: {}", e.getMessage());
			e.printStackTrace();
		}
		return json;
	}
	
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof DomainEntity<?,?>)) {
			return false;
		}
		
		DomainEntity<?,?> ent = (DomainEntity<?,?>)obj;
		
		if (id == null) {
			return false;
		}
		
		return getId().equals(ent.getId());
		
		
	}

	/**
	 * @author jmgarcia
	 *
	 * @param <B>
	 */
	public static class DomainEntityBuilder<E extends DomainEntity<?,?>> {
		
		private Class<E> entityClass;
		private String json = null;
		
		public DomainEntityBuilder(Class<E> entityClass) {
			this.entityClass = entityClass;
		}
	
		/**
		 * @return the entityClass
		 */
		public Class<E> getEntityClass() {
			return entityClass;
		}

		/**
		 * @return the json
		 */
		public String getJson() {
			return json;
		}

		public DomainEntityBuilder<E> withJson(String json) {
			this.json = json;
			return this;
		}
	
		public E build() {
			try {
				return getMapper().readValue(json, entityClass);
			} catch (JsonMappingException e) {
				LOG.error("Deserialization error: {}", e.getMessage());
				e.printStackTrace();
			} catch (JsonProcessingException e) {
				LOG.error("Deserialization error: {}", e.getMessage());
				e.printStackTrace();
			}
			
			return null;
		}
	}

	

}
