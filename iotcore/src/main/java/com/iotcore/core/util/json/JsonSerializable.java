/**
 * 
 */
package com.iotcore.core.util.json;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author jmgarcia
 *
 */
public interface JsonSerializable<T> {
	
	static final ObjectMapper mapper = new ObjectMapper();
	
	

    @SuppressWarnings("unchecked")
    default T deserialize(String rawJson) throws IOException {
        return mapper.readValue(rawJson, (Class<T>) this.getClass());
    }
    
    
    default String toJson() {
    	try {
			return mapper.writeValueAsString(this);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
    	return null;
    }
    
}
