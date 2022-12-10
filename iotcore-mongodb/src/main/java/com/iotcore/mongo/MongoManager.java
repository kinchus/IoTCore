/**
 * 
 */
package com.iotcore.mongo;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

import java.io.Serializable;
import java.util.Objects;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.ClassModel;
import org.bson.codecs.pojo.Conventions;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bson.codecs.pojo.PojoCodecProvider.Builder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iotcore.mongo.dao.DomainEntity;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

// import com.github.mongobee.Mongobee;
// import com.github.mongobee.exception.MongobeeException;
import dev.morphia.Datastore;
import dev.morphia.Morphia;


/**
 * @author <a href="mailto:garciadjx@gmail.com">J.M. Garcia</a>
 *
 */
@SessionScoped
public class MongoManager implements Serializable {

	private static final long serialVersionUID = 3097309955217740271L;

	private static final Logger LOG = LoggerFactory.getLogger(MongoManager.class);
	
	/** DBCONFIG_FILE */
	private static final String DBCONFIG_FILE 	= "mongo.properties";
	private static final String DOMAIN_PACKAGE 	= "com.laiwa.iotcloud.domain.model";
	
	private static CodecProvider pojoCodecProvider = null;
	private static MongoConfig	config = null;
	private static MongoManager instance = null;
	
	
	
	
	/**
	 * @return the instance
	 */
	public static MongoManager getInstance() {
		if (instance != null) {
			return instance;
		}
		instance = new MongoManager();
		instance.init();
		return instance;
	}
	
		

	
	/**
	 *  @param configFile 
	 * @return the initialized MongoConfig instance
	 */
	public static MongoConfig getConfig(String configFile) {
		if (config == null) {
			config = new MongoConfig(configFile);
			config.parseConfiguration();
		}
		return config;
	}

	/**
	 * @return the config
	 */
	public static  MongoConfig getConfig() {
		if (config == null) {
			config = new MongoConfig(DBCONFIG_FILE);
			config.parseConfiguration();
		}
		return config;
	}

	
	/**
	 * @param cfg
	 */
	public static void setConfig(MongoConfig cfg) {
		config = cfg;
		config.parseConfiguration();
	}
	

	
	private MongoClient mongo = null;
	private Datastore 	datastore = null;
	
	/**
	 * 
	 */
	public MongoManager() {
		instance = this;
	}
	
	
	/**
	 * Bean initialization for CDI enabled environment
	 */
	@PostConstruct
	public synchronized void init() {
		
		if (config == null) {
			
			MongoConfig config = new MongoConfig();
			setConfig(config);
		}
		
		getDatastore();
		
	}

	/**
	
	/**
	 * @return
	 * @throws RuntimeException
	 */
	public synchronized MongoClient getClient() throws RuntimeException {
		
		if (mongo == null) {
			try {
				mongo = getConfig().buildClient();
				LOG.trace("MongoDB client initialized successfully");
			} catch (Exception e) {
				LOG.error("Exception thrown: {}", e.getMessage());
				throw new RuntimeException("Couldn't connect to database server", e);
			}
		}
		
		return mongo;
	}
	
	/**
	 * @param jndi 
	 * @return
	 * @throws NamingException 
	 */
	public synchronized MongoClient getClient(String jndi) throws NamingException {
		Context ctx = new InitialContext(); 
		mongo = (MongoClient) ctx.lookup(jndi);
		return mongo;
	}
	
	/**
	 * @param client
	 */
	public void setClient(MongoClient client) {
		this.mongo = client;
	}

	/**
	 * @return
	 */
	public synchronized MongoDatabase getDatabase() {
		String dbName = config.getDbName();
		
		CodecRegistry pojoCodecRegistry = fromRegistries(
				MongoClientSettings.getDefaultCodecRegistry(), 
				fromProviders(getCodecProvider()));
		
		return getClient().getDatabase(dbName).withCodecRegistry(pojoCodecRegistry);
	}
	
	
	public synchronized <D> MongoCollection<D> getCollection(String collectionName, Class<D> collectionClazz, Class<?> [] classes) {
		
		Objects.requireNonNull(collectionName, "collectionName cannot be null");
		Objects.requireNonNull(collectionClazz, "collectionClazz cannot be null");
		
		String dbName = config.getDbName();
		
		CodecProvider pojoCodecProvider = null;
		
		if ((classes != null) && (classes.length > 1)) {
			pojoCodecProvider = getCodecProvider(classes);
			int n = 1 + classes.length;
			Class<?> [] classesToRegister = new Class<?>[n];
			classesToRegister[0] = collectionClazz;
			if (n > 1) {
				int i = 1;
				for (Class<?> clazz:classes) {
					classesToRegister[i++] = clazz;
				}
			}
		}
		else {
			pojoCodecProvider = getCodecProvider();
		}
		
		
		CodecRegistry pojoCodecRegistry = fromRegistries(MongoClientSettings.getDefaultCodecRegistry(), fromProviders(pojoCodecProvider));
		return getClient().getDatabase(dbName).getCollection(collectionName, collectionClazz).withCodecRegistry(pojoCodecRegistry);
	}
	
	/**
	 * @param <T>
	 * @param colName
	 * @param colClass
	 * @return
	 */
	public <T> MongoCollection<T> getDbCollection(String colName, Class<T> colClass) {
		return getCollection(colName, colClass, null);
	}

	
	/**
	 * @return
	 */
	public Datastore getDatastore() {
		synchronized (this) {
			if (datastore == null) {
				datastore = Morphia.createDatastore(getClient(), config.getDbName());
				datastore.ensureIndexes();
			}
		}

		return datastore;
	}
	
	/**
	 * @param mongoClient
	 * @return
	 */
	public Datastore getDatastore(MongoClient mongoClient) {
		synchronized (this) {
			if (datastore == null) {
				datastore = getDatastore(mongoClient);
				datastore.ensureIndexes();
			}
		}
		return datastore;
	}
	
	
	/**
	 * @return
	 */
//	public Morphia getMorphia() {
//		synchronized (this) {
//			if (morphia == null) {
//				morphia = new Morphia();
//				if (LOG.isTraceEnabled()) LOG.trace("Mapping entities from {}", DOMAIN_PACKAGE);
//				morphia.mapPackage(DOMAIN_PACKAGE, true);
//				String[] packages = config.getDomainPackages();
//				if (packages != null) {
//					for (String p:packages) {
//						if (LOG.isTraceEnabled()) LOG.trace("Mapping entities from {}", p);
//						morphia.mapPackage(p, true);
//					}
//				}
//			}
//		}
//		return morphia;
//	}

	
	
	/**
	 * 
	 */
	public synchronized void close() {
		if (mongo != null) {
			try {
				LOG.debug("Releasing MongoDB client resources");
				mongo.close();
				mongo = null;
				datastore = null;
			} catch (Exception e) {
				LOG.error("An error occurred when closing the MongoDB connection: {}", e.getMessage());
			}
		}
	}
	
	/**
	 * @param classes
	 * @return
	 */
	private CodecProvider getCodecProvider(Class<?> [] classes) {
		Builder builder = PojoCodecProvider.builder();
		for (Class<?> clazz:classes) {
			ClassModel<?> model = ClassModel.builder(clazz)
					.enableDiscriminator(true)
					.discriminatorKey(DomainEntity.CLASSNAME_FIELD)
					.discriminator(clazz.getName())
					.build();
			builder.register(model);
		}
		
		return builder.automatic(true).build(); 

	}

	private static CodecProvider getCodecProvider() {
		if (pojoCodecProvider  == null) {
			pojoCodecProvider = PojoCodecProvider.builder()
				.register(DOMAIN_PACKAGE)
				.conventions(Conventions.DEFAULT_CONVENTIONS)
				.automatic(true)
				.build();
		}
		return pojoCodecProvider;
	}
}
