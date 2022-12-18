/**
 * 
 */
package com.iotcore.mongo.dao;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.lang.reflect.ParameterizedType;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iotcore.core.dao.EntityDao;
import com.iotcore.core.util.StringUtil;
import com.iotcore.mongo.MongoConfig;
import com.iotcore.mongo.MongoConfig.ConfigKey;
import com.iotcore.mongo.MongoManager;
import com.iotcore.mongo.dao.factory.MongoDaoFactory;
import com.iotcore.mongo.util.BsonMapper;

import de.flapdoodle.embed.mongo.commands.ServerAddress;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.mongo.transitions.Mongod;
import de.flapdoodle.embed.mongo.transitions.RunningMongodProcess;
import de.flapdoodle.reverse.TransitionWalker;
import dev.morphia.Datastore;

/**
 * Base class  for DAO Unit Tests
 * 
 * @author <a href="mailto:garciadjx@gmail.com">J.M. Garcia</a>
 * @param <E> 
 * @param <D> 
 */
public abstract class DaoTestBase<E extends DomainEntity<?>, D extends EntityDao<E,?>> {
	
	private static final Logger LOG = LoggerFactory.getLogger(DaoTestBase.class);
	
	private static final String URI_PATTERN = "mongodb://%s:%d/%s";
	private static final Version MONGO_VERSION =Version.V4_0_12;
	
	private static RunningMongodProcess runningMongo;
	private static MongoManager mongoManager;
	private static Datastore datastore;
	/**
	 * @throws Exception
	 */
	@BeforeAll
	public static void setUpClass() throws Exception {
		
		TransitionWalker.ReachedState<RunningMongodProcess> running = Mongod.instance().start(MONGO_VERSION);
		
				runningMongo = running.current();
				
				ServerAddress srvAddress = runningMongo.getServerAddress();
				MongoConfig config = new MongoConfig();
				String dbUri = String.format(URI_PATTERN, srvAddress.getHost(), srvAddress.getPort(), "test");
				config.setProperty(ConfigKey.DBHOST, srvAddress.getHost());
				config.setProperty(ConfigKey.DBPORT, srvAddress.getPort());
				config.setProperty(ConfigKey.DBNAME, "test");
				config.setProperty(ConfigKey.URI_PROPERTY, dbUri);
				MongoManager.setConfig(config);
				
				mongoManager = MongoManager.getInstance();
				
				
		
	}
	
	/**
	 * @throws Exception
	 */
	@AfterAll
	public static void tearDownClass() throws Exception {
		runningMongo.stop();
	}
	
	protected static Datastore getDatastore() {
		if (datastore == null) {
			datastore = getMongoManager().getDatastore();
		}
		return datastore;
	}
	
		
	/**
	 * @return
	 */
	protected static MongoManager getMongoManager() {
		return mongoManager;
	}
	
		
	/**
	 * @param filename 
	 * @param clazz
	 * @param <T> 
	 * @return
	 * @throws FileNotFoundException 
	 */
	protected static <T> List<T> loadDbDataFromJSONFile(String collectionName, Class<T> clazz, String filename) {
		
		LOG.debug("Loading {} model from file:{}", clazz.getSimpleName(), filename);
		InputStream is =  Thread.currentThread().getContextClassLoader().getResourceAsStream(filename);
		if (is == null) {
			LOG.error("Couldn't open file: {}", filename);
			return null;
		}
		String json = StringUtil.readFromFile(is);
		if (json == null)  {
			LOG.error("Couldn't parse json from file {}", filename);
			return null;
		}
		
		List<T> ret = BsonMapper.getInstance().mapList(clazz, json);
		if (ret == null) {
			return null;
		}
		
		return getDatastore().save(ret);
	}

		


	private Class<D> daoClass;
	private D dao;
	
	/**
	 * Constructor 
	 */
	@SuppressWarnings("unchecked")
	public DaoTestBase() {
		daoClass =  (Class<D>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[1];
		MongoDaoFactory.getInstance().registerDao(daoClass);
	}
		
	/**
	 * @param daoClazz
	 * @param entityClazz
	 */
	public DaoTestBase(Class<D> daoClazz, Class<E> entityClazz) {
		daoClass =  daoClazz;
		MongoDaoFactory.getInstance().registerDao(daoClass);
	}
		

	/**
	 * @return the dao
	 */
	public D getDao() {
		if (dao == null) {
			dao = MongoDaoFactory.getDao(daoClass);
		}
		return dao;
	}

		

}
