/**
 * 
 */
package com.iotcore.core.util.json;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.Map;

import org.junit.jupiter.api.Test;

import com.iotcore.core.util.json.JsonString;

/**
 * @author jmgarcia
 *
 */
public class JsonStringTest {
	
	private static final String JSON_TEST_NUMBER = "{\"field\" : 0}";
	private static final String JSON_TEST_STRING = "{\"field\" : \"value\"}";
	private static final String JSON_TEST_MAP = "{\"1\" : 1, \"2\" : 1, \"3\" : 1, \"4\" : 1, \"5\" : 1}";


	/**
	 * Test method for {@link com.iotcore.core.util.json.JsonString#setDateFormat(java.lang.String)}.
	 */
	@Test
	void testSetDateFormat() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link com.iotcore.core.util.json.JsonString#getDateFormat()}.
	 */
	@Test
	void testGetDateFormat() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link com.iotcore.core.util.json.JsonString#JsonString(java.io.InputStream)}.
	 */
	@Test
	void testJsonStringInputStream() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link com.iotcore.core.util.json.JsonString#getMap()}.
	 */
	@Test
	void testGetMap() {
		JsonString json = new JsonString(JSON_TEST_MAP);
		Map<String, Object> map = json.getMap();
		assertNotNull(map);
		for (Integer k=1; k<6; k++) {
			Object v = map.get(k.toString());
			assertNotNull(v);
		}
	}

	/**
	 * Test method for {@link com.iotcore.core.util.json.JsonString#get(java.lang.String)}.
	 */
	@Test
	public void testGetString() {
		JsonString json = new JsonString(JSON_TEST_STRING);
		String v = json.get("field");
		
		assertNotNull(v);
		assertEquals("value", v);
	}

	/**
	 * Test method for {@link com.iotcore.core.util.json.JsonString#get(java.lang.String, java.lang.Object)}.
	 */
	@Test
	public void testGetNumber() {
		JsonString json = new JsonString(JSON_TEST_NUMBER);
		Long l = json.get("field", Long.class);
		assertNotNull(l);
		assertEquals((Long)0L, l);
		
		Integer i = json.get("field", Integer.class);
		assertNotNull(i);
		assertEquals(0, i);
	}

	
	/**
	 * Test method for {@link com.iotcore.core.util.json.JsonString#put(java.lang.String, java.lang.Object)}.
	 */
	@Test
	void testPut() {
		JsonString json = new JsonString(JSON_TEST_NUMBER);
		json.put("new", "test");
		String val = json.get("new");
		assertNotNull(val);
		assertEquals("test", val);
	}

	/**
	 * Test method for {@link com.iotcore.core.util.json.JsonString#hasField(java.lang.String)}.
	 */
	@Test
	void testHasField() {
		JsonString json = new JsonString(JSON_TEST_NUMBER);
		assertTrue(json.hasField("field"));
	}

	/**
	 * Test method for {@link com.iotcore.core.util.json.JsonString#hasField(java.lang.String, java.lang.Class)}.
	 */
	@Test
	void testHasFieldStringClassOfQ() {
		JsonString json = new JsonString(JSON_TEST_NUMBER);
		assertTrue(json.hasField("field", Long.class));
	}

	/**
	 * Test method for {@link com.iotcore.core.util.json.JsonString#delete(java.lang.String)}.
	 */
	@Test
	void testDelete() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link com.iotcore.core.util.json.JsonString#fromField(java.lang.String)}.
	 */
	@Test
	void testFromField() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link com.iotcore.core.util.json.JsonString#getBytes()}.
	 */
	@Test
	void testGetBytes() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link com.iotcore.core.util.json.JsonString#clear()}.
	 */
	@Test
	void testClear() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link com.iotcore.core.util.json.JsonString#toString()}.
	 */
	@Test
	void testToString() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link com.iotcore.core.util.json.JsonString#equals(java.lang.Object)}.
	 */
	@Test
	void testEqualsObject() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link com.iotcore.core.util.json.JsonString#prettyPrint()}.
	 */
	@Test
	void testPrettyPrint() {
		fail("Not yet implemented");
	}


}
