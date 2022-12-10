/**
 * 
 */
package com.iotcore.mongo.dao;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;

import com.iotcore.core.dao.IdEntity;
import com.iotcore.mongo.MongoManager;
import com.mongodb.DBCollection;

import dev.morphia.Datastore;
import dev.morphia.query.Query;

/**
 * 
 *
 * @param <T>
 * @param <K>
* @author <a href="mailto:garciadjx@gmail.com">J.M. Garcia</a>
 */
public abstract class BaseDao<T extends DomainEntity<?,K>, K> implements Serializable {

	private static final long serialVersionUID = 1507683779896231488L;
	
		
	private static Datastore datastore;
	private static boolean deletionMark = true;
	
	private static String idField = IdEntity.getIdField();
	private static String deletedField = DomainEntity.DELETED_FIELD;
	
	/**
	 * @return the idField
	 */
	protected static boolean isDeletionMark() {
		return deletionMark;
	}

	
	/**
     * Returns a new query bound to the collection (a specific {@link DBCollection})
     *
     * @param collectionClazz The collection to query
     * @param <T> the type of the query
     * @return the query
     */
	protected static <E> Query<E> getQuery(Class<E> collectionClazz) {
		Query<E> qry = getDatastore().createQuery(collectionClazz)
				.disableValidation()
				.filter("className", collectionClazz.getName());
		
		if (isDeletionMark()) {
				qry.field(deletedField).equal(false);
		}
		
		return qry;
	}
	
	/**
     * Returns a new query bound to the collection (a specific {@link DBCollection})
     * including those entities whose class name matches any in the dicriminatorClassName array
     *
     * @param collectionClazz The collection to query
     * @param <T> the type of the query
     * @return the query
     */
	protected static <E> Query<E>  getQuery(Class<E> collectionClazz, String [] discrClassNames ) {
		Query<E> qry = getDatastore().createQuery(collectionClazz).disableValidation();
		
		
		if (isDeletionMark()) {
			qry.field(deletedField).equal(false);
		}
		
		if (discrClassNames != null ) {
			qry.filter("className in", discrClassNames);
		}
		
		return qry;
	}
	
	
	
	/**
	 * @return
	 */
	protected static <E> Query<E> getQuery(Class<E> collectionClazz, Class<?> ...subclasses) {
		
		String[] dClassNames = null;
		if (subclasses != null) {
			int i = 1;
			dClassNames = new String[subclasses.length + 1];
			dClassNames[0] = collectionClazz.getCanonicalName();
			for (Class<?> subclass:subclasses) {
				dClassNames[i++] = subclass.getCanonicalName();
			}
		}
		return getQuery(collectionClazz, dClassNames);
	}
	
	
	

	
	protected static Datastore getDatastore() {
		if (datastore == null) {
			datastore = MongoManager.getInstance().getDatastore();
		}
		return datastore;
	}
	
	
	//
	// INSTANCE FIELDS
	//
	
	private Datastore daoDatastore;
	private Class<T> persistentClass = null;
	private String[] discrClassNames = null;
	

	/**
	 * 
	 */
	@SuppressWarnings("unchecked")
	public BaseDao() {
		setDaoDatastore(MongoManager.getInstance().getDatastore());
		persistentClass = ((Class<T>)((ParameterizedType)(getClass().getGenericSuperclass())).getActualTypeArguments()[0]);
	}
		
	/**
	 * @param entityClass
	 */
	public BaseDao(Class<T> entityClass) {
		this(MongoManager.getInstance().getDatastore(), entityClass);
	}
	

	/**
	 * Constructor  for polymorphic DAO classes. You must specify the entity subclasses allowed in the collection. 
	 * @param entityClass
	 * @param subclasses
	 */
	public BaseDao(Class<T> entityClass, Class<?> ...subclasses) {
		this(MongoManager.getInstance().getDatastore(), entityClass,subclasses);
	}
	
	/**
	 * @param entityClass
	 * @param datastore
	 */
	public BaseDao(Datastore datastore) {
		setDaoDatastore(datastore);
	}
	

	/**
	 * @param entityClass
	 * @param datastore
	 */
	public BaseDao(Datastore datastore, Class<T> entityClass) {
		setDaoDatastore(datastore);
		this.persistentClass = entityClass;
	}
	
	/**
	 * Constructor  for polymorphic DAO classes. You must specify the entity subclasses allowed in the collection.
	 * @param entityClass
	 * @param datastore
	 * @param subclasses 
	 */
	public BaseDao(Datastore datastore, Class<T> entityClass,Class<?> ...subclasses) {
		setDaoDatastore(datastore);
		this.persistentClass = entityClass;
		if (subclasses != null) {
			int i = 1;
			this.discrClassNames = new String[subclasses.length + 1];
			discrClassNames[0] = persistentClass.getCanonicalName();
			for (Class<?> subclass:subclasses) {
				discrClassNames[i++] = subclass.getCanonicalName();
			}
		}
	}
	


	
	
	/**
	 * @param ids
	 * @return
	 */
	public List<T> findByIds(List<K> ids) {
		return (getQuery().field(idField).in(ids)).find().toList();
	}
	
	/**
	 * @param id
	 * @return
	 * @see com.northstar.domain.dao.EntityDao#delete(java.lang.Object)
	 */
	public void delete(K id) {
		// ObjectId oid = ObjectIdUtils.getObjectId(id.toString());
		// MetricsQuery<T> qry = getQuery().field("_id").equal(oid);
		Query<T> qry = getQuery().field(idField).equal(id);
		if (deletionMark) {
			markAsDeleted(true, qry.find().toList());
		}
		else {
			getDaoDatastore().delete(qry);
		}
	}
	
	/**
	 * @param entity
	 * @return
	 * @see com.northstar.domain.dao.EntityDao#delete(java.lang.Object)
	 */
	public void delete(T entity) {
		if (deletionMark) {
			markAsDeleted(true, entity);
		}
		else {
			getDaoDatastore().delete(entity);
		}
	}
	
	/**
	 * @param entities
	 * @return
	 * @see com.northstar.domain.dao.EntityDao#delete(java.lang.Object)
	 */
	public void delete(List<T> entities) {
		
		List<K> ids = new ArrayList<K>(entities.size());
		entities.forEach(e->ids.add(e.getId()));
		Query<T> qry = getQuery().field(idField).in(ids);
		if (deletionMark) {
			markAsDeleted(true, qry.find().toList());
		}
		else {
			getDaoDatastore().delete(qry);
		}
	}
		
	
	protected void markAsDeleted(boolean deleted, T entity) {
		entity.setDeleted(deleted);
		getDaoDatastore().save(entity);
	}
	
	protected void markAsDeleted(boolean deleted, List<T> entities) {
		for (T ent:entities) {
			ent.setDeleted(deleted);
		}
		getDaoDatastore().save(entities);
	}
	

	/**
	 * @return
	 */
	protected Query<T> getQuery() {
		return getQuery(persistentClass, discrClassNames);
	}


	
		
	protected Datastore getDaoDatastore() {
		if (daoDatastore == null) {
			daoDatastore = getDatastore();
		}
		return daoDatastore;
	}

	private void setDaoDatastore(Datastore daoDatastore) {
		this.daoDatastore = daoDatastore;
	}


}
