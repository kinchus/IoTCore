/**
 * 
 */
package com.iotcore.mongo.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bson.BsonArray;
import org.bson.BsonValue;
import org.bson.codecs.BsonArrayCodec;
import org.bson.codecs.BsonDocumentCodec;
import org.bson.codecs.BsonValueCodecProvider;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.DocumentCodecProvider;
import org.bson.codecs.ValueCodecProvider;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.json.JsonReader;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author jmgarcia
 *
 */
public class BsonMapper {
	

	private static BsonMapper instance;
	
	private static ObjectMapper objectMapper;
	private static CodecRegistry codecRegistry;
	private static DecoderContext decoderContext;
	private static BsonDocumentCodec documentCodec; 
	private static BsonArrayCodec arrayCodec;
	

	
	/**
	 * @return the instance
	 */
	public static BsonMapper getInstance() {
		if (instance == null) {
			instance = new BsonMapper();
		}
		return instance;
	}


	
	/**
	 * @return the documentCodec
	 */
	public static BsonDocumentCodec getDocumentCodec() {
		if (documentCodec == null) {
			documentCodec = new BsonDocumentCodec(getCodecRegistry());
		}
		return documentCodec;
	}


	/**
	 * @return the arrayCodec
	 */
	private static BsonArrayCodec getArrayCodec() {
		if (arrayCodec == null) {
			arrayCodec = new BsonArrayCodec(getCodecRegistry());
		}
		return arrayCodec;
	}

	private static ObjectMapper getObjectMapper() {
		if (objectMapper == null) {
			objectMapper = new ObjectMapper();
		}
		return objectMapper;
	}

		
	/**
	 * @return the codecRegistry
	 */
	private static CodecRegistry getCodecRegistry() {
		if (codecRegistry == null) {
			codecRegistry = CodecRegistries.fromProviders(Arrays.asList(new ValueCodecProvider(),
					new BsonValueCodecProvider(),
					new DocumentCodecProvider()));
		}
		return codecRegistry;
	}


	/**
	 * @return the decoderContext
	 */
	private static DecoderContext getDecoderContext() {
		if (decoderContext == null) {
			decoderContext = DecoderContext.builder().build();
		}
		return decoderContext;
	}
	



	/**
	 * 
	 */
	public BsonMapper() {
		
	}
	
	public  <T> T map(Class<T> clazz, String json) {
		T ret = null;
		JsonReader reader = new JsonReader(json);
		BsonValue doc = getDocumentCodec().decode(reader, getDecoderContext());
		
		try {
			ret = getObjectMapper().readValue(doc.toString(), clazz);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return ret;
	}
		
	
	/**
	 * @param <T>
	 * @param clazz
	 * @param json
	 * @return
	 */
	public  <T> List<T> mapList(Class<T> clazz, String json) {
		List<T> ret = new ArrayList<T>();
		
		JsonReader reader = new JsonReader(json);
		BsonArray docArray = getArrayCodec().decode(reader, getDecoderContext());

		for (BsonValue doc : docArray.getValues()) {
			T aux = null;
			try {
				aux = getObjectMapper().readValue(doc.toString(), clazz);
				ret.add(aux);
			} catch (IOException e) {
				e.printStackTrace();
			}
		    
		}
		return ret;
	}
	

}
