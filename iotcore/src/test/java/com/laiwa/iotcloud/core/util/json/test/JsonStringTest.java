/**
 * 
 */
package com.laiwa.iotcloud.core.util.json.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import com.iotcore.core.util.json.JsonString;

/**
 * @author jmgarcia
 *
 */
public class JsonStringTest {
	
	private static final String JSON_TEST_NUMBER = "{\"field\" : 0}";

	/**
	 * Test method for {@link com.iotcore.core.util.json.JsonString#get(java.lang.String)}.
	 */
	@Test
	public void testGetString() {
		JsonString json = new JsonString(JSON_TEST_NUMBER);
		Long t = json.get("field");
		
		assertNotNull(t);
		assertEquals((Long)0L, t);
	}

	/**
	 * Test method for {@link com.iotcore.core.util.json.JsonString#get(java.lang.String, java.lang.Object)}.
	 */
	@Test
	public void testGetStringT() {
		JsonString json = new JsonString(JSON_TEST_NUMBER);
		Long t = json.get("field", Long.class);
		
		assertNotNull(t);
		assertEquals((Long)0L, t);
	}

	/**
	 * Test method for {@link com.iotcore.core.util.json.JsonString#get(java.lang.String, java.lang.Class)}.
	 */
	@Test
	public void testGetStringClassOfT() {
	}

}
