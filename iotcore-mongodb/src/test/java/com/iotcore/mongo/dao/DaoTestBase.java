/**
 * 
 */
package com.iotcore.mongo.dao;

import java.lang.reflect.ParameterizedType;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import com.iotcore.core.dao.DomainEntity;
import com.iotcore.core.dao.EntityDao;
import com.iotcore.mongo.DbConstants;
import com.iotcore.mongo.MongoConfig;
import com.iotcore.mongo.MongoManager;
import com.iotcore.mongo.dao.factory.MongoDaoFactory;
import com.iotcore.mongo.util.ModelData;
import com.mongodb.client.MongoDatabase;

import dev.morphia.Datastore;

/**
 * Base class  for DAO Unit Tests
 * 
 * @author <a href="mailto:garciadjx@gmail.com">J.M. Garcia</a>
 * @param <E> 
 * @param <D> 
 */
public abstract class DaoTestBase<E extends DomainEntity<?>, D extends EntityDao<E,?>> {
	
	private static String configFile = DbConstants.TEST_CONFIG;
	private static MongoManager mongoManager;
	private static Datastore datastore = null;
	
	private Class<D> daoClass;
	private Class<E> entityClass;
	private D dao;
	
	
	/**
	 * @return
	 */
	public abstract String getCollectionName();
	
	
	protected static Datastore getDatastore() {
		if (datastore == null) {
			datastore = getMongoManager().getDatastore();
		}
		return datastore;
	}

	/**
	 * @return
	 */
	public static MongoManager getMongoManager() {
		if (mongoManager == null) {
			MongoManager.setConfig(new MongoConfig(configFile));
			mongoManager = MongoManager.getInstance();
		}
		return mongoManager;
	}

	protected static String getJsonFile(String collection) {
		return DbConstants.JSON_PATH + collection + ".json";
	}
	
	
	/**
	 * @throws Exception
	 */
	@BeforeAll
	public static void setUpClass() throws Exception {
		
	
	}
	
	/**
	 * @throws Exception
	 */
	@AfterAll
	public static void tearDownClass() throws Exception {
		MongoDatabase db = mongoManager.getDatabase();
		for (String col:db.listCollectionNames()) {
			db.getCollection(col).drop();
		}
		System.out.println("Database successfully cleared");
		Thread.sleep(1000);
	}
	
	
	/**
	 * Constructor 
	 */
	@SuppressWarnings("unchecked")
	public DaoTestBase() {
		entityClass =  (Class<E>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
		daoClass =  (Class<D>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[1];
	}
	
	
	/**
	 * @throws Exception
	 */
	@BeforeEach
	public void setUp() throws Exception {
		if (dao == null) {
			dao = MongoDaoFactory.getDao(daoClass);
		}
	}
	

	/**
	 * @return the dao
	 */
	public D getDao() {
		return dao;
	}

	/**
	 * @return the dataModel
	 */
	@SuppressWarnings("unchecked")
	public List<E> getDataModel() {
		return (List<E>) ModelData.getModel(entityClass);
	}

	
	/**
	 * @param idx
	 * @return
	 */
	public E getDataModel(int idx) {
		return getDataModel().get(idx);
	}

		
}
