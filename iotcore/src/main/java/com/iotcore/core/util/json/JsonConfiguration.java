/**
 * 
 */
package com.iotcore.core.util.json;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;

/**
 * 
 * @author <a href="mailto:garciadjx@gmail.com">J.M. Garcia</a>
 */
public class JsonConfiguration implements Serializable {
	
	private static final long serialVersionUID = -70627294050059919L;

	private static String lineSeparator = System.getProperty("line.separator");
	
	private JsonString config = null;

	/**
	 * 
	 */
	public JsonConfiguration() {
		config = new JsonString();
	}
	
	/**
	 * @param in 
	 * @throws IOException 
	 * 
	 */
	public JsonConfiguration(InputStream in) throws IOException {
		load(in);
	}

	
	/**
	 * @param in
	 * @throws IOException
	 */
	public void load(InputStream in) throws IOException {
		String fileContents = null;
		BufferedReader buffReader = new BufferedReader(new InputStreamReader(in));
		try {
			String	line = null;
			StringBuilder  stringBuilder = new StringBuilder();
			while((line = buffReader.readLine()) != null) {
				stringBuilder.append(line);
		        stringBuilder.append(lineSeparator);
			}
			fileContents = stringBuilder.toString();
		    buffReader.close();
		} catch (IOException e) {} 
		
		config = new JsonString(fileContents);
	}

	/**
	 * @param <T>
	 * @param field
	 * @return
	 */
	public <T> T get(String field) {
		return config.get(field);
	}
	
	/**
	 * @param <T>
	 * @param field
	 * @param defVal 
	 * @return
	 */
	public <T> T get(String field, T defVal) {
		return config.get(field, defVal);
	}
	
	/**
	 * @param field
	 * @param value
	 * @return 
	 */
	public JsonConfiguration put(String field, Object value) {
		config.put(field, value);
		return this;
	}
	
	/**
	 * @param field
	 * @return 
	 */
	public JsonConfiguration delete(String field) {
		config.delete(field);
		return this;
	}
	
	/**
	 * @return
	 */
	public InputStream getInputStream() {
		return new ByteArrayInputStream(config.prettyPrint().getBytes());
	}
	
	/**
	 * 
	 */
	public void clear() {
		config.clear();
	}


	/**
	 * @return
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return config.toString();
	}

}
