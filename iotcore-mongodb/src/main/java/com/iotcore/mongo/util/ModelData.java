package com.iotcore.mongo.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

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
import com.iotcore.mongo.dao.DomainEntity;

import dev.morphia.Datastore;



/**
 * 
 * @author <a href="mailto:garciadjx@gmail.com">J.M. Garcia</a>
 */
public class ModelData {

	private static final Logger LOG = LoggerFactory.getLogger(ModelData.class);

	/** BASE_DOMAIN_PACKAGE */
	public static final String BASE_DOMAIN_PACKAGE = "com.laiwa.iotcloud.domain.model.";
	/** JSON_PATH */
	public static final String JSON_PATH = "db/datasets/";

	private static Map<Class<?>, List<?>> modelMap = new HashMap<Class<?>, List<?>>();
	

	public static List<?> getModel(Class<?> entClass) {
		return modelMap.get(entClass);
	}

	/**
	 * @param <T>
	 * @param ds 
	 * @param jsonCollection
	 * @param clazz
	 * @throws IOException
	 */
	public static <T extends DomainEntity<?>> void updateCollection(Datastore ds, String jsonCollection, Class<T> clazz) throws IOException {
		List<T> list = getDataModelFromJSONFile(jsonCollection, clazz);
		if (list == null) {
			LOG.error("Unable to load model data");
		}
		
		
		for (T entity:list) {
			ds.save(entity);
			if (LOG.isTraceEnabled()) {
				LOG.trace("\tInserted/updated {} {}", clazz.getSimpleName(), entity.getId());
			}
		}
		
		modelMap.put(clazz, list);
	}


	/**
	 * @param <T> 
	 * @param filename
	 * @param clazz
	 * @return
	 * @throws FileNotFoundException 
	 */
	public static <T> List<T> getDataModelFromJSONFile(String filename, Class<T> clazz) throws FileNotFoundException {
		final CodecRegistry codecRegistry = CodecRegistries.fromProviders(Arrays.asList(new ValueCodecProvider(),
				new BsonValueCodecProvider(),
				new DocumentCodecProvider()));
		List<T> ret = new ArrayList<T>();

		ObjectMapper mapper = new ObjectMapper();
		mapper.setLocale(Locale.US);
		
		InputStream is = ModelData.class.getClassLoader().getResourceAsStream(filename);
		if (is == null) {
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

	/**
	 * @param ds 
	 * @throws IOException 
	 * 
	 */
	@SuppressWarnings("unchecked")
	public static void initialize(Datastore ds, String jsonFilename, Class<?> clazz) throws IOException {

		LOG.info("Populating database with initial data...");
		updateCollection(ds, jsonFilename, (Class<? extends DomainEntity<?>>)clazz);
		
		/*
		Platform platf = ds.find(Platform.class).first();
		if (platf.getInitialized()) {
			return;
		}
		
		LOG.info("Updating Platform db document...");
		Organization rootOrg = getRootOrganization(ds); 
		if (rootOrg == null) {
			LOG.error("No root organization defined");
			return;
		}
		LOG.debug("Main organization: {}", rootOrg.getName());
		platf.setRootOrganization(rootOrg);
		List<Application> apps = ds.createQuery(Application.class).find().toList();
		for (Application app:apps) {
			LOG.debug("Platform application: {}", app.getName());
		}
		platf.setApplications(apps);
		ds.save(platf);
		LOG.info("Platform saved");
		
		*/
	}


}