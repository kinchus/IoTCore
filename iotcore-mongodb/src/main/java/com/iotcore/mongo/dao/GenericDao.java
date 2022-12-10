/**
 * 
 */
package com.iotcore.mongo.dao;


import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.iotcore.core.dao.EntityDao;
import com.iotcore.mongo.MongoManager;
import com.iotcore.mongo.id.ObjectIdUtils;
import com.mongodb.DBCollection;

import dev.morphia.Datastore;
import dev.morphia.query.FindOptions;
import dev.morphia.query.MorphiaCursor;
import dev.morphia.query.Query;
import dev.morphia.query.Sort;
import dev.morphia.query.experimental.filters.Filter;
import dev.morphia.query.experimental.filters.Filters;

/**
 * 
 *
 * @param <T>
 * @param <K>
* @author <a href="mailto:garciadjx@gmail.com">J.M. Garcia</a>
 */
public class GenericDao<T extends DomainEntity<?,K>, K>  implements EntityDao<T, K> {

	private static final long serialVersionUID = 6314714981317571793L;
	
	static final int LIMIT_DEFAULT = 1000;
	static final int OFFSET_DEFAULT = 0;
	

	private static Datastore datastore;
	
	
	/**
     * Returns a new query bound to the collection (a specific {@link DBCollection})
     *
     * @param collectionClazz The collection to query
     * @param <T> the type of the query
     * @return the query
     */
	protected static <E> Query<E> getQuery(Class<E> collectionClazz) {
		return getDatastore().find(collectionClazz)
				.disableValidation();
//				.filter(DomainEntity.CLASSNAME_FIELD, collectionClazz.getName());
	}
	
	/**
     * Returns a new query bound to the collection (a specific {@link DBCollection})
     * including those entities whose class name matches any in the dicriminatorClassName array
     *
     * @param collectionClazz The collection to query
     * @param <T> the type of the query
     * @return the query
     */
	protected static <E> Query<E>  getQuery(Class<E> collectionClazz, Filter queryFilter) {
		Query<E> qry = getDatastore().find(collectionClazz)
				.disableValidation();
		
		if (queryFilter != null ) {
			qry = qry.filter(queryFilter);
		}
		
		return qry;
	}
		
	protected static Datastore getDatastore() {
		if (datastore == null) {
			datastore = MongoManager.getInstance().getDatastore();
		}
		return datastore;
	}
	
	
	protected static boolean checkLocks = false;
	private Class<T> persistentClass = null;
	private Filter subclassesFilter = null;
	private Datastore daoDatastore;
	private Sort[] sorting = null;
	
	
	/**
	 * 
	 */
	@SuppressWarnings("unchecked")
	public GenericDao() {
		persistentClass = (Class<T>)((ParameterizedType)(getClass().getGenericSuperclass())).getActualTypeArguments()[0];
	}

	/**
	 * @param entityClass
	 */
	public GenericDao(Class<T> entityClass) {
		persistentClass = entityClass;
	}
	
	/**
	 * Constructor  for polymorphic DAO classes. You must specify the entity subclasses allowed in the collection. 
	 * @param entityClass
	 * @param subclasses
	 */
	public GenericDao(Class<T> entityClass, Class<?> ...subclasses) {
		this(entityClass, MongoManager.getInstance().getDatastore(), subclasses);
	}
	/**
	 * @param entityClass
	 * @param datastore
	 */
	public GenericDao(Class<T> entityClass, Datastore datastore) {
		this.persistentClass = entityClass;
		setDaoDatastore(datastore);
	}
	
	/**
	 * Constructor  for polymorphic DAO classes. You must specify the entity subclasses allowed in the collection.
	 * @param entityClass
	 * @param datastore
	 * @param subclasses 
	 */
	public GenericDao(Class<T> entityClass, Datastore datastore, Class<?> ...subclasses) {
		this.persistentClass = entityClass;
		if (subclasses != null) {
			List<String> discrClassNames = new ArrayList<String>(subclasses.length + 1);
			discrClassNames.add(persistentClass.getCanonicalName());
			for (Class<?> subclass:subclasses) {
				discrClassNames.add(subclass.getCanonicalName());
			}
			subclassesFilter = Filters.in("className", getQuery());
			
		}
		setDaoDatastore(datastore);
	}
	
	/**
	 * @return
	 * @see com.northstar.domain.dao.EntityDao#findAll()
	 */
	@Override
	public List<T> findAll(Integer start, Integer count) {
		MorphiaCursor<T> cur = getQuery().iterator(findOptions(start, count));
		return cur.toList();
	}
	

	/**
	 * @param id
	 * @return
	 * @see com.northstar.domain.dao.EntityDao#findById(java.lang.Object)
	 */
	@Override
	public T findById(K id) {
		return getQuery().filter(Filters.eq(ENTITY_ID, id)).first();
		// return getQuery().field("_id").equal(id).first();
	}
	
	
	/**
	 * @param ids
	 * @return
	 */
	public List<T> findByIds(List<K> ids) {
		MorphiaCursor<T> cur =  getQuery().filter(Filters.eq(ENTITY_ID, ids)).iterator(findOptions());
		return cur.toList();
		//return (getQuery().field("_id").in(ids)).find().toList();
	}
	


	/**
	 * @param object
	 * @return
	 */
	@Override
	public T save(T object) {
		
		if (object.getId() == null) {
			object.setId(ObjectIdUtils.newId(object.keyClass()));
			object.setCreatedAt(Calendar.getInstance().getTime());
			getDaoDatastore().save(object);
		}
		else {
			object.setUpdatedAt(Calendar.getInstance().getTime());
			getDaoDatastore().save(object);
		}
		
		
		return object;
	}
	
	/**
	 * @param object
	 * @return
	 */
	@Override
	public List<T> save(List<T> object) {
		
		for (T t:object) {
			if (t.getId() == null) { 
				Date now  = Calendar.getInstance().getTime();
				t.setId(ObjectIdUtils.newId(t.keyClass()));
				t.setCreatedAt(now);
			}
		}
		
		getDaoDatastore().save(object);
		return object;
	}
	
	
	/**
	 * @param id
	 * @return
	 * @see com.northstar.domain.dao.EntityDao#delete(java.lang.Object)
	 */
	@Override
	public void delete(K id) {
		getQuery().filter(Filters.eq(ENTITY_ID, id)).findAndDelete();
		//Query<T> qry = getQuery().field("_id").equal(id);
		//getDaoDatastore().delete(qry);
	}
	
	/**
	 * @param object
	 * @return
	 * @see com.northstar.domain.dao.EntityDao#delete(java.lang.Object)
	 */
	@Override
	public void delete(T object) {
		getDaoDatastore().delete(object);
	}
	
	/**
	 * @param objects
	 * @return
	 * @see com.northstar.domain.dao.EntityDao#delete(java.lang.Object)
	 */
	@Override
	public void delete(List<T> objects) {
		
		List<K> ids = new ArrayList<K>();
		for (T ent:objects) {
			ids.add(ent.getId());
		}
		getQuery().filter(Filters.eq(ENTITY_ID, ids)).findAndDelete();
		
//		Query<T> qry = getQuery().field("_id").in(ids);
//		getDaoDatastore().delete(qry);

	}
		
		

	/**
	 * @return
	 */
	protected Query<T> getQuery() {
		Query<T> ret = null;
		if (subclassesFilter == null) {
			ret = getQuery(persistentClass);
		}
		else {
			ret = getQuery(persistentClass, subclassesFilter);
		}
		
		// ret.or(ret.criteria(DomainEntity.DELETED_FIELD).doesNotExist(), ret.criteria(DomainEntity.DELETED_FIELD).equal(false));
		return ret;
	}
	
	
	/**
	 * @return
	 */
	protected Query<T> getQueryForDeletedItems() {
		Query<T> ret = null;
		if (subclassesFilter == null) {
			ret = getQuery(persistentClass);
		}
		else {
			ret = getQuery(persistentClass, subclassesFilter);
		}
		
		return ret.filter( Filters.eq(DomainEntity.DELETED_FIELD, true));	
	}
	
		
	/**
	 * @param sorting
	 */
	protected void setDefaultSorting(Sort ... sorts) {
		this.sorting = sorts;
	}
	
	protected  FindOptions findOptions() {
		FindOptions f =  new FindOptions();
		if (sorting != null) {
			f.sort(sorting);
		}
		return f;
	}
	
	
	protected  FindOptions findOptions(Integer start, Integer count) {
		FindOptions f = new FindOptions()
				.skip(start != null?start:OFFSET_DEFAULT)
				.limit(count != null?count:LIMIT_DEFAULT);
		if (sorting != null) {
			f.sort(sorting);
		}
		return f;
	}
	
	
	protected FindOptions findOptions(Integer start, Integer count, Sort[] sorting) {
		FindOptions f = new FindOptions()
				.skip(start != null?start:OFFSET_DEFAULT)
				.limit(count != null?count:LIMIT_DEFAULT);
		if (sorting != null) {
			f.sort(sorting);
		}
		return f;
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
