/**
 * 
 */
package com.iotcore.mongo;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bson.BsonArray;
import org.bson.BsonValue;
import org.bson.codecs.BsonArrayCodec;
import org.bson.codecs.BsonValueCodecProvider;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.DocumentCodecProvider;
import org.bson.codecs.ValueCodecProvider;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.json.JsonReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iotcore.core.util.StringUtil;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;

import dev.morphia.Datastore;

/**
@author <a href="mailto:garciadjx@gmail.com">J.M. Garcia</a>
 *
 */
public class DbManager {

	private static final Logger LOG = LoggerFactory.getLogger(DbManager.class);
	
	private static MongoClient mongoClient = null;
	private static Datastore datastore = null;
	
	
	/**
	 * @param dbName 
	 * @param changelog 
	 * 
	 */
	public static void initDatabase(String dbName, String changelog) {
		
		// mongoInitialized = true;
		
		LOG.debug("Initializing MongoDB Database: {}", dbName);
	
		// mongoInitialized = true;
	}
	
	
	/**
	 * Initialize MongoDB instance with default test schemas
	 */
	public static void clearDatabase() {
		MongoDatabase db = getMongoClient().getDatabase(DbConstants.TEST_DATABASE);
		db.drop();
	}
	
	
	/**
	 * @return the mongoClient
	 */
	public static MongoClient getMongoClient() {
		
		//localDatabase = MongoManager.getConfig().isFileParsed();
		
		if (mongoClient == null) {
			mongoClient = MongoManager.getInstance().getClient();
		}
		return mongoClient;
	}

	

	/**
	 * @param client 
	 */
	public static void setMongoClient(MongoClient client) {
		mongoClient = client;
	}

	/**
	 * @return
	 */
	public static Datastore getDatastore() {
		if (datastore == null) {
			// datastore = getMorphia().createDatastore(getMongoClient(), DbConstants.TEST_DATABASE);
			datastore = MongoManager.getInstance().getDatastore(getMongoClient());
		}
		return datastore;
	}

		
	/**
	 * @param filename 
	 * @param clazz
	 * @param <T> 
	 * @return
	 * @throws FileNotFoundException 
	 */
	public static <T> List<T> getDataModelFromJSONFile(String filename, Class<T> clazz) throws FileNotFoundException {
		final CodecRegistry codecRegistry = CodecRegistries.fromProviders(Arrays.asList(new ValueCodecProvider(),
				new BsonValueCodecProvider(),
				new DocumentCodecProvider()));
		List<T> ret = new ArrayList<T>();
		
		ObjectMapper mapper = new ObjectMapper();
		
		LOG.debug("Loading {} model from file:{}", clazz.getSimpleName(), filename);
		InputStream is =  Thread.currentThread().getContextClassLoader().getResourceAsStream(filename);
		if (is == null) {
			LOG.error("Couldn't open file: {}", filename);
			throw new FileNotFoundException(filename);
		}
		String json = StringUtil.readFromFile(is);
		JsonReader reader = new JsonReader(json);
		BsonArrayCodec arrayReader = new BsonArrayCodec(codecRegistry);
		BsonArray docArray = arrayReader.decode(reader, DecoderContext.builder().build());

		for (BsonValue doc : docArray.getValues()) {
			T aux = null;
			try {
				aux = mapper.readValue(doc.toString(), clazz);
				ret.add(aux);
			} catch (IOException e) {
				e.printStackTrace();
			}
		    
		}
		return ret;
	}

}
