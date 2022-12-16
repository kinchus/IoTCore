/**
 * 
 */
package com.iotcore.mongo.dao;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.iotcore.core.dao.EntityDao;
import com.iotcore.core.dao.FieldValuePair;
import com.iotcore.core.dao.IdEntity;
import com.iotcore.core.dao.ResultsPage;
import com.iotcore.mongo.MongoManager;
import com.iotcore.mongo.id.ObjectIdUtils;
import com.mongodb.DBCollection;

import dev.morphia.Datastore;
import dev.morphia.query.CountOptions;
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
public class GenericDao<T extends IdEntity<K>, K extends Serializable>  implements EntityDao<T, K> {

	private static final long serialVersionUID = 6314714981317571793L;
	private static final int OFFSET_DEFAULT = 0;
	
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
		this.getClass();
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
	 */
	@Override
	public List<T> findAll(Integer start, Integer count) {
		MorphiaCursor<T> cur = getQuery().iterator(findOptions(start, count));
		return cur.toList();
	}
	

	/**
	 * @param id
	 * @return
	 */
	@Override
	public T findById(K id) {
		return getQuery().filter(Filters.eq(ENTITY_ID, id)).first();
		// return getQuery().field("_id").equal(id).first();
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
		
		return object;
	}

	
	/**
	 * @param id
	 * @return
	 */
	@Override
	public void delete(K id) {
		getQuery().filter(Filters.eq(ENTITY_ID, id)).findAndDelete();
	}

		
	/**
	 * @param ids
	 * @return
	 */
	protected List<T> findAllByIds(List<K> ids) {
		MorphiaCursor<T> cur =  getQuery().filter(Filters.eq(ENTITY_ID, ids)).iterator(findOptions());
		return cur.toList();
		//return (getQuery().field("_id").in(ids)).find().toList();
	}
	
	
	/**
	 * @param object
	 * @return
	 */
	protected List<T> save(List<T> object) {
		
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
	 * @param object
	 * @return
	 * @see com.northstar.domain.dao.EntityDao#delete(java.lang.Object)
	 */
	protected void delete(T object) {
		getDaoDatastore().delete(object);
	}
	
	
	/**
	 * @param objects
	 */
	protected void delete(List<T> objects) {
		
		List<K> ids = new ArrayList<K>();
		for (T ent:objects) {
			ids.add(ent.getId());
		}
		getQuery().filter(Filters.eq(ENTITY_ID, ids)).findAndDelete();
		
//		Query<T> qry = getQuery().field("_id").in(ids);
//		getDaoDatastore().delete(qry);

	}
		
		
	protected T findByField(String field, Object value) {
		return getQuery().filter(Filters.eq(field, value)).first();
	}
	
	protected T findByFields(FieldValuePair ... fieldValues) {
		Query<T> qry = getQuery();
		for (FieldValuePair fv:fieldValues) {
			if (!fv.isNullValue()) {
				qry.filter(Filters.eq(fv.getField(), fv.getValue()));
			}
			else if (fv.isArrayValue()) {
				qry.filter(Filters.in(fv.getField(), fv.getValues()));
			}
		}
		return qry.first();
	}
	
	
	protected ResultsPage<T> findAllByField(String field, Object value, Integer start, Integer count) {
		FindOptions fOpts = findOptions(start, count);
		Query<T> qry = getQuery().filter(Filters.eq(field, value));
		MorphiaCursor<T> cur = qry.iterator(fOpts);
		CountOptions options = new CountOptions().skip(fOpts.getSkip());
		return new ResultsPage<T>(cur.toList(), qry.count(options));
	}
	
	protected ResultsPage<T> findAllByFields(Integer start, Integer count, FieldValuePair ... fieldValues ) {
		FindOptions fOpts = findOptions(start, count);
		Query<T> qry = getQuery();
		for (FieldValuePair fv:fieldValues) {
			if (!fv.isArrayValue()) {
				qry.filter(Filters.eq(fv.getField(), fv.getValue()));
			}
			else {
				qry.filter(Filters.in(fv.getField(), fv.getValues()));
			}
		}
		MorphiaCursor<T> cur = qry.iterator(fOpts);
		CountOptions options = new CountOptions().skip(fOpts.getSkip());
		return new ResultsPage<T>(cur.toList(), qry.count(options));
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
	 * @param sorting
	 */
	protected void setDefaultSorting(String ... sortFields) {
		List<Sort> sorting = new ArrayList<Sort> ();
		for (String sort:sortFields) {
			String field = null;
			Sort s = null;
			Character c = sort.charAt(0);
			switch (c) {
			case '-':
				field = sort.substring(1);
				s = Sort.descending(field);
				break;
			case '+':
				field = sort.substring(1);
			default:
				if (field == null) {
					field = sort.substring(0);
				}
				s = Sort.ascending(field);
				break;
			}
			sorting.add(s);
		}
		
		this.sorting = sorting.toArray(new Sort[sorting.size()]);
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
				.skip(start != null?start:OFFSET_DEFAULT);
		
		if (count != null) {
				f.limit(count);
		}
		
		if (sorting != null) {
			f.sort(sorting);
		}
		return f;
	}
	
	
	protected FindOptions findOptions(Integer start, Integer count, Sort[] sorting) {
		FindOptions f = new FindOptions()
				.skip(start != null?start:OFFSET_DEFAULT);
		
		if (count != null) {
			f.limit(count);
		}
	
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
