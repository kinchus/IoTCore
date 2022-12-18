/*
 * Copyright 2016 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package com.iotcore.core.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * Helper class for simplifying reading and processing configuration files.
 */
public class BaseConfig {
	
	/**
	 * 
	 */
	public interface ConfigKey {
		
		/**
		 * @return
		 */
		String getKey();
		
		/**
		 * @return
		 */
		Class<?> getType();
	}
	
	
	private static final Logger LOG = LoggerFactory.getLogger(BaseConfig.class);
	
	private Path path = null;
	protected Properties properties = new Properties(); 
    protected boolean fileParsed = false;
    
    /**
     * 
     */
    public BaseConfig() {
    }
    
    
    /**
     * @param filename Name of the configuration file to set (Forced to lower case)
     */
    public BaseConfig(String filename) {
    	try {
			setPropertiesFile(filename.toLowerCase());
		} catch (IOException e) {
			LOG.warn("Couldn't read configuration from file {}", filename);
		}
    }
    
   
	

	public  Properties getProperties() {
    	return properties;
    }
    
    public void setProperties(Map<String, Object> map)  {
    	properties.clear();
    	properties.putAll(map);
    }
    
    /**
     * Looks for the requested properties file and loads it.
     * Search order:
     * 	1. User directory
     *  2. Local resource file
     *  3. Classpath file
     *  
     * @param filename
     * @throws IOException
     * @throws FileNotFoundException
     */
    public void setPropertiesFile(String filename) throws IOException, FileNotFoundException {
    	InputStream stream = null;
    	String currentPath = System.getProperty("user.dir");
    	File file = Paths.get(currentPath, filename).toFile();
		if (file.exists())  {
			stream = new FileInputStream(file);
		}
        else {
        	stream = getClass().getClassLoader().getResourceAsStream(filename);
        	if (stream == null) {
    			String defaults = filename + ".default";
    			stream = getClass().getClassLoader().getResourceAsStream(defaults);
    		}
        }
		
        if (stream != null) {
        	LOG.trace("Reading configuration properties");
        	properties.load(stream);
        	// path = file.toPath();
        	fileParsed = true;
			stream.close();
		}
        else {
        	LOG.trace("Configuration file not found: {}", filename);
        	throw new FileNotFoundException();
        }
    }
    
	public void loadProperties(Map<String, Object> map) {
		for (String k:map.keySet()) {
			Object val = map.get(k);
			if (val != null) {
				properties.put(k, val);
			}
		}
		
		if (LOG.isTraceEnabled()) {
			StringBuffer buff = new StringBuffer();
			for (Object prop:properties.keySet()) {
				buff.append(StringUtil.indent(30, (String)prop, (String)properties.get(prop)));
			}
			LOG.trace("AWS Configuration parameters:\n{}", buff.toString());
		}

	}
    
    /**
     * @param key
     * @return
     */
    public boolean hasProperty(ConfigKey key) {
		if (getProperty(key) != null) {
			return true;
		}
		return false;
	}
    
      
    /**
     * @param <T>
     * @param key
     * @return
     */
   @SuppressWarnings("unchecked")
   public <T> T getProperty(ConfigKey key) {
		return (T)getProperty(key, null);
	}
    
   	/**
   	 * @param <T>
   	 * @param key
   	 * @param defVal
   	 * @return
   	 */
   	@SuppressWarnings("unchecked")
	public <T> T getProperty(ConfigKey key, Object defVal) {
   		
   		
   		String strVal = System.getenv(getSystemVarName(key.getKey()));
   		
   		if (strVal == null) {
   			strVal = getProperty(key.getKey());
   		}
   		
   		if ((strVal != null) && LOG.isTraceEnabled()) {
			LOG.trace("Read property {}: {}", key.getKey(), strVal);
		}
   		
   		
   		if (StringUtil.isBlank(strVal)) {
			return (T)defVal;
		}
		else if (key.getType() == String.class) {
			return (T)strVal;
		}
		else if (key.getType() == Boolean.class) {
			return (T)((Boolean)Boolean.parseBoolean(strVal));
		}
		else if (key.getType() == Integer.class) {
			return (T)((Integer)Integer.parseInt(strVal));
		}
		else if (key.getType() == Long.class) {
			return (T)((Long)Long.parseLong(strVal));
		}
		else if (key.getType() == Float.class) {
			return (T)((Float)Float.parseFloat(strVal));
		}
		else if (key.getType() == Double.class) {
			return (T)((Double)Double.parseDouble(strVal));
		}
		
		return null;
	}
   	
   	/**
   	 * @param <T>
   	 * @param key
   	 * @return
   	 * @throws Exception
   	 */
   	@SuppressWarnings("unchecked")
	public <T> T requireProperty(ConfigKey key) throws Exception {
		
		String strVal = getProperty(key.getKey());
		try {
			if (strVal == null) {
				throw new Exception("Configuration property not found: " + key.getKey());
			}
			else if (key.getType() == String.class) {
				return (T)strVal;
			}
			else if (key.getType() == Boolean.class) {
				return (T)((Boolean)Boolean.parseBoolean(strVal));
			}
			else if (key.getType() == Integer.class) {
				return (T)((Integer)Integer.parseInt(strVal));
			}
			else if (key.getType() == Long.class) {
				return (T)((Long)Long.parseLong(strVal));
			}
			else if (key.getType() == Float.class) {
				return (T)((Float)Float.parseFloat(strVal));
			}
			else if (key.getType() == Double.class) {
				return (T)((Double)Double.parseDouble(strVal));
			}
		}
		catch(ParseException e) {
			throw new Exception("Wrong value found for property " + key.getKey() + ": " + strVal);
		}
		catch(NumberFormatException e) {
			throw new Exception("Wrong value found for property " + key.getKey() + ": " + strVal);
		}
		
		throw new Exception("Unmanaged property value type: " + key.getType());
	}
    
    /**
     * @param property
     * @param defaultValue 
     * @return
     */
    public String getProperty(String property, String defaultValue) {
    	String val = getProperty(property);
    	if (StringUtil.isBlank(val)) {
    		val = defaultValue;
    	}
    	return val;
    }
 
    /**
     * 
     * @param property
     * @return
     */
    public String getProperty(String property) {
    	if (properties == null) {
    		return null;
    	}

    	return properties.getProperty(property); 
    }
    
    
    public void setProperty(ConfigKey configKey, Object value) {
    	if (configKey != null) {
    		properties.setProperty(configKey.getKey(), value.toString());
    	}
    }
    
    /**
     * @param property
     * @param value 
     */
    public void setProperty(String property, Object value) {
    	if (value != null) {
    		properties.setProperty(property, value.toString());
    	}
    }
    
    /**
     * @param key
     * @return
     */
    public boolean validateProperty(ConfigKey key) {
    	if (!properties.containsKey(key.getKey())) {
    		return false;
    	}
    	try {
			requireProperty(key);
		} catch (Exception e) {
			LOG.error(e.getMessage());;
			return false;
		}
    	return true;
    }
    
    /**
     * 
     */
    public boolean persist() {
    	if (path == null) {
    		return false;
    	}
    	FileOutputStream fos;
		try {
			fos = new FileOutputStream(path.toFile());
			properties.store(fos, null);
		} catch (IOException e) {
			LOG.error(e.getMessage());;
			return false;
		}
    	return true;
    }
    
    
    /**
     * @param property
     * @return
     */
    public static String getSystemVarName(String property) {
    	return property.replace('.', '_').toUpperCase();
    }

}
