/**
 * 
 */
package com.iotcore.core.util;

import java.math.BigDecimal;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author jmgarcia
 *
 */

public class MapUtil {
	
	private static final Logger LOG  = LoggerFactory.getLogger(MapUtil.class);
	

	@SuppressWarnings("unchecked")
	public static <T> T get(Map<String, Object> input, String field, Class<T> clazz) throws Exception {
		Object aux = input.get(field);
		if (aux == null) {
			String logMsg = String.format("\"%s\" not found in map", field);
			LOG.error(logMsg);
			throw new Exception(logMsg);
		}
		else if (clazz.isAssignableFrom(aux.getClass())) {
			return (T)aux;
		}
		else if ((aux instanceof Number)) {
			Class<? extends Number> nClazz = (Class<? extends Number>)clazz;
			return (T)toNumber((Number)aux, nClazz);
		}
		else {
			String logMsg = String.format("\"%s\" is not of type %s", field, clazz.getSimpleName());
			LOG.error(logMsg);
			throw new Exception(logMsg);
		}
	}
	
	/**
	 * @param <T>
	 * @param input
	 * @param field
	 * @param clazz
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getOrNull(Map<String, Object> input, String field, Class<T> clazz) {
		Object aux = input.get(field);
		if ((aux == null) || !clazz.isAssignableFrom(aux.getClass())) {
			return null;
		}
		else if ((aux instanceof Number)) {
			Class<? extends Number> nClazz = (Class<? extends Number>)clazz;
			return (T)toNumber((Number)aux, nClazz);
		}
		return (T)aux;
	}
	
	@SuppressWarnings("unchecked")
	public static <T extends Number> T toNumber(Number in, Class<T> clazz) {
		
		if (clazz.equals(Byte.class)) {
			Byte val = in.byteValue();
			return (T)val;
		}
		else if (clazz.equals(Short.class)) {
			Short val = in.shortValue();
			return (T)val;
		}
		// Integer
		else if (clazz.equals(Integer.class)) {
			Integer val = in.intValue();
			return (T)val;
		}
		// Long
		else if (clazz.equals(Long.class)) {
			Long val = in.longValue();
			return (T)val;
		}
		// Float
		else if (in instanceof Float) {
			Float val = in.floatValue();
			return (T)val;
		}
		// Double
		else if (in instanceof Double) {
			Double val = in.doubleValue();
			return (T)val;
		}
		// Conversion from String representation
		else if (in instanceof BigDecimal) {
			return (T)new BigDecimal(in.toString());
		}
		
		return null;
	}
	
	
}
