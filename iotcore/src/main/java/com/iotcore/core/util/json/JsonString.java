/**
 * 
 */
package com.iotcore.core.util.json;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;


/**
 * Representation of a JSON string that provides a similar interface to
 * Map<String, Object<
 * 
 * @author <a href="mailto:garciadjx@gmail.com">J.M. Garcia</a>
 *
 */
public class JsonString implements Serializable {

	private static final long serialVersionUID = -8087334884679178533L;

	public static final String DATE_FORMAT_ISO = "YYYY-MM-DD'T'HH:mm:ss.SSSX";
	public static final String DATE_FORMAT_Z = "yyyy-MM-dd'T'HH:mm:ss.S'Z'";
	
	private static final ObjectMapper MAPPER = new ObjectMapper();
	private static final DateFormat defaultDateFormat = new SimpleDateFormat(DATE_FORMAT_ISO);
	private static final String pathSeparator = File.separator;

	
	/**
	 * @param json
	 * @return
	 */
	public static JsonString fromString(String json) {
		return new JsonString(jsonToMap(json));
	}

	/**
	 * @param map
	 * @return
	 */
	public static JsonString fromMap(final Map<String, Object> map) {
		return new JsonString(map);
	}

	private DateFormat dateFormat = null;
	private Map<String, Object> map = null;
	private String cachedJson = null;
	private boolean isModified = false;

	/**
	 * 
	 */
	public JsonString() {
		map = new HashMap<String, Object>();
	}

	/**
	 * @param map
	 */
	public JsonString(final Map<String, Object> map) {
		this.map = map;
	}

	/**
	 * @param json
	 */
	public JsonString(final byte[] json) {
		this(new String(json));
	}

	/**
	 * @param json
	 */
	public JsonString(final String json) {
		this.map = jsonToMap(json);
		this.cachedJson = json;
	}

	/**
	 * @param pattern
	 */
	public void setDateFormat(String pattern) {
		dateFormat = new SimpleDateFormat(pattern);
	}

	/**
	 * @param pattern
	 */
	public DateFormat getDateFormat() {
		if (dateFormat == null) {
			return defaultDateFormat;
		} else {
			return dateFormat;
		}
	}

	/**
	 * Constructor
	 * 
	 * @param input
	 */
	public JsonString(final InputStream input) {
		
		try {
			String text = new String(input.readAllBytes(), StandardCharsets.UTF_8);
			this.map = jsonToMap(text);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	/**
	 * @return
	 */
	public Map<String, Object> getMap() {
		return this.map;
	}

	/**
	 * @param <T>
	 * @param field
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T> T get(String field) {

		// Check if the given field exists in the first level
		Object val = map.get(field);
		if (val != null) {
			return (T) val;
		}

		Map<String, Object> inner = map;
		String fieldPath = field;
		if (fieldPath.startsWith(pathSeparator)) {
			fieldPath = fieldPath.substring(1);
		}

		int i = fieldPath.indexOf(pathSeparator);
		while (i > -1) {
			String path = fieldPath.substring(0, i);
			fieldPath = fieldPath.substring(i + 1);
			inner = (Map<String, Object>) inner.get(path);
			if (inner == null) {
				return null;
			}
			val = inner.get(fieldPath);
			if (val != null) {
				return (T) val;
			}
			i = fieldPath.indexOf(pathSeparator);
		}

		return null;
	}

	/**
	 * @param <T>
	 * @param field
	 * @param defVal
	 * @return
	 */
	public <T> T get(String field, T defVal) {

		T ret = get(field);
		if (ret == null) {
			ret = defVal;
		}
		return ret;
	}

	/**
	 * @param <T>
	 * @param field
	 * @param clazz
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T> T get(String field, Class<T> clazz) {

		Object val = get(field);

		if (val == null) {
			return null;
		}

		if (clazz.isAssignableFrom(val.getClass())) {
			return (T) val;
		} else if (clazz.equals(String.class)) {
			return (T) val.toString();
		} else if ((val instanceof Number)) {
			Class<? extends Number> nClazz = (Class<? extends Number>) clazz;
			return (T) toNumber((Number) val, nClazz);
		} else if (clazz.equals(Date.class)) {
			return (T) toDate(val);
		} else if (clazz.equals(JsonString.class) && val instanceof Map) {
			return (T) new JsonString((Map<String, Object>) val);
		} else {
			return null;
		}
	}

	/**
	 * @param field
	 * @param value
	 */
	@SuppressWarnings("unchecked")
	public void put(String field, Object value) {

		String lastPath = field;
		Map<String, Object> parent = map;
		Object anterior = parent.get(lastPath);

		if (anterior == null) {
			String[] paths = field.split(pathSeparator);
			for (int j = 0; j < paths.length - 1; j++) {
				String path = paths[j];
				anterior = parent.get(path);
				if (anterior == null) {
					parent.put(path, new HashMap<String, Object>());
				}
				parent = (Map<String, Object>) parent.get(path);
			}
			lastPath = paths[paths.length - 1];
		}

		anterior = parent.get(lastPath);

		// No existe el último elemento: Lo añade como un nuevo hijo
		if (anterior == null) {
			parent.put(lastPath, value);
			anterior = parent.get(lastPath);
		}
		// Anterior Existe y es un mapa: Copia los elementos del hijo en el
		// anterior
		else if (anterior instanceof Map<?, ?>) {
			Map<String, Object> pmap = (Map<String, Object>) anterior;
			for (String k : ((Map<String, Object>) value).keySet()) {
				pmap.put(k, ((Map<String, Object>) value).get(k));
			}

		}
		// Anterior Existe y es una lista:
		else if (anterior instanceof List<?>) {
			List<Object> ant = (List<Object>) anterior;
			// Si el valor es una lista sustituye dicha lista los elementos
			if (value instanceof List<?>) {
				ant.clear();
				ant.addAll((List<?>) value);
			}
			// Si el valor es un mapa, lo añade los elementos
			else {
				ant.add(value);
			}
		}
		else {
			parent.put(lastPath, value);
		}

		isModified = true;
		return;
	}

	/**
	 * @param field
	 * @return
	 */
	public Boolean hasField(String field) {
		return hasField(field, null);

	}

	/**
	 * @param field
	 * @param ofClass
	 * @return
	 */
	public Boolean hasField(String field, Class<?> ofClass) {
		Object val = map.get(field);
		if (val == null) {
			return false;
		}
		if (ofClass == null) {
			return true;
		}
		if (ofClass.isInstance(val)) {
			return true;
		}
		else {
			Class<?> valueClass = val.getClass();
			return valueClass.isAssignableFrom(ofClass);
		}
	}

	/**
	 * @param field
	 */
	@SuppressWarnings("unchecked")
	public void delete(String field) {
		if (map.containsKey(field)) {
			map.remove(field);
		}

		String lastPath = field;
		Map<String, Object> parent = map;
		Object anterior = parent.get(lastPath);

		if (anterior == null) {
			String[] paths = field.split(pathSeparator);
			for (int j = 0; j < paths.length - 1; j++) {
				String path = paths[j];
				anterior = parent.get(path);
				if (anterior == null) {
					return;
				}
				parent = (Map<String, Object>) parent.get(path);
			}
			lastPath = paths[paths.length - 1];
		}

		parent.remove(lastPath);

	}

	/**
	 * @param field
	 * @return
	 */
	public JsonString fromField(String field) {
		if (hasField(field, Map.class)) {
			Map<String, Object> map = get(field);
			return new JsonString(map);
		} else {
			return null;
		}
	}

	/**
	 * @return
	 */
	public byte[] getBytes() {
		return toString().getBytes(Charset.defaultCharset());
	}
	
	public void clear() {
		map.clear();
		isModified = true;
	}

	/**
	 * 
	 * @return
	 */
	@Override
	public String toString() {
		if (isModified || (cachedJson == null)) {
			cachedJson = toJsonString(map);
			isModified = false;
		}
		return cachedJson;
	}

	/**
	 * Compares two JsonString objects and returns true if equals (= same fields
	 * at same deep with equal values
	 */
	@SuppressWarnings("unchecked")
	@Override
	public boolean equals(Object other) {
		JsonString json2 = null;
		boolean equals = true;
		if (other instanceof String) {
			json2 = new JsonString((String) other);
		} else if (other instanceof Map<?, ?>) {
			json2 = new JsonString((Map<String, Object>) other);
		} else if (other instanceof JsonString) {
			json2 = (JsonString) other;
		} else {
			return false;
		}

		Map<String, Object> map2 = json2.map;
		Set<String> keys = map.keySet();
		if (!keys.equals(map2.keySet())) {
			return false;
		}
		for (String k : keys) {
			equals = false;
			Object val = map.get(k);
			Object val2 = map2.get(k);
			if (val2 != null) {
				equals = val.equals(val2);
			}
			if (!equals)
				return false;
		}

		return true;
	}

	/**
	 * @return
	 */
	public String prettyPrint() {
		String unformattedJsonString = toString();
		StringBuilder prettyJSONBuilder = new StringBuilder();
		int indentLevel = 0;
		boolean inQuote = false;
		for (char charFromUnformattedJson : unformattedJsonString.toCharArray()) {
			switch (charFromUnformattedJson) {
				case '"' :
					// switch the quoting status
					inQuote = !inQuote;
					prettyJSONBuilder.append(charFromUnformattedJson);
					break;
				case ' ' :
					// For space: ignore the space if it is not being quoted.
					if (inQuote) {
						prettyJSONBuilder.append(charFromUnformattedJson);
					}
					break;
				case '{' :
				case '[' :
					// Starting a new block: increase the indent level
					prettyJSONBuilder.append(charFromUnformattedJson);
					indentLevel++;
					appendIndentedNewLine(indentLevel, prettyJSONBuilder);
					break;
				case '}' :
				case ']' :
					// Ending a new block; decrese the indent level
					indentLevel--;
					appendIndentedNewLine(indentLevel, prettyJSONBuilder);
					prettyJSONBuilder.append(charFromUnformattedJson);
					break;
				case ',' :
					// Ending a json item; create a new line after
					prettyJSONBuilder.append(charFromUnformattedJson);
					if (!inQuote) {
						appendIndentedNewLine(indentLevel, prettyJSONBuilder);
					}
					break;
				default :
					prettyJSONBuilder.append(charFromUnformattedJson);
			}
		}
		return prettyJSONBuilder.toString().trim();
	}

	/**
	 * Print a new line with indention at the beginning of the new line.
	 * 
	 * @param indentLevel
	 * @param stringBuilder
	 */
	private static void appendIndentedNewLine(int indentLevel,
			StringBuilder stringBuilder) {
		stringBuilder.append("\n");
		for (int i = 0; i < indentLevel; i++) {
			// Assuming indention using 2 spaces
			stringBuilder.append("   ");
		}
	}

	//
	//
	//

	/**
	 * @param obj
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static String toJsonString(Object obj) {
		if (obj == null) {
			return null;
		} else if (obj instanceof String) {
			return "\"" + (String) obj + "\"";
		} else if (obj instanceof Map<?, ?>) {
			return toJsonString((Map<String, ?>) obj);
		} else if (obj instanceof List<?>) {
			return toJsonString((List<?>) obj);
		} else if (obj instanceof Date) {
			return "\"" + defaultDateFormat.format((Date) obj) + "\"";
		} else {
			return obj.toString();
		}
	}

	/**
	 * @param payload
	 * @return
	 */
	private static String toJsonString(Map<String, ?> payload) {
		StringBuilder logStr = new StringBuilder("{");
		String sep = "";
		if (payload != null) {
			for (String field : payload.keySet()) {
				Object val = payload.get(field);
				logStr.append(sep);
				logStr.append(String.format("\"%s\" : ", field));
				logStr.append(toJsonString(val));
				sep = ",";
			}
		}
		logStr.append("}");
		return logStr.toString();
	}

	/**
	 * @param array
	 * @return
	 */
	private static String toJsonString(List<?> array) {
		StringBuilder str = new StringBuilder("[");
		String sep = "";
		for (Object k : array) {
			str.append(sep);
			str.append(toJsonString(k));
			sep = ",";
		}
		str.append("]");
		return str.toString();
	}

	/**
	 * @param date
	 * @return
	 */
	public synchronized String format(Date date) {
		return dateFormat.format(date);
	}

	/**
	 * @param in
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private static <T extends Number> T toNumber(Number in, Class<T> clazz) {

		if (clazz.equals(Byte.class)) {
			Byte val = in.byteValue();
			return (T) val;
		} else if (clazz.equals(Short.class)) {
			Short val = in.shortValue();
			return (T) val;
		}
		// Integer
		else if (clazz.equals(Integer.class)) {
			Integer val = in.intValue();
			return (T) val;
		}
		// Long
		else if (clazz.equals(Long.class)) {
			Long val = in.longValue();
			return (T) val;
		}
		// Float
		else if (in instanceof Float) {
			Float val = in.floatValue();
			return (T) val;
		}
		// Double
		else if (in instanceof Double) {
			Double val = in.doubleValue();
			return (T) val;
		}
		// Conversion from String representation
		else if (in instanceof BigDecimal) {
			return (T) new BigDecimal(in.toString());
		}

		return null;
	}

	/**
	 * Convert this object or its string representation to a Date object
	 * instance
	 * 
	 * @param obj
	 * @return
	 */
	private static Date toDate(Object obj) {
		try {
			return defaultDateFormat.parse((String) obj);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	private static Map<String, Object> jsonToMap(String json) {
		Map<String, Object> ret = null;
		try {
			ret = MAPPER.readValue(json, Map.class);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		
		return ret;
	}



}
