/**
 * 
 */
package com.iotcore.core.dao.factory;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.iotcore.core.dao.DomainEntity;
import com.iotcore.core.dao.EntityDao;

/**
 * @author jmgarcia
 *
 */
class DaoFactoryTest {
	
	private DaoFactory factory;

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeEach
	void setUp() throws Exception {
		factory = new DaoFactory();
	}

	/**
	 * Test method for {@link com.iotcore.core.dao.factory.DaoFactory#registerFactory(com.iotcore.core.dao.factory.DaoFactory)}.
	 */
	@Test
	void testRegisterFactoryDaoFactory() {
		MyTestDaoFactory myFactory = new MyTestDaoFactory();
		DaoFactory.registerFactory( myFactory );
		Set<DaoFactory> factories = DaoFactory.getFactories();
		assertNotNull(factories);
		assertTrue(factories.contains(myFactory));
		
	}

	/**
	 * Test method for {@link com.iotcore.core.dao.factory.DaoFactory#registerFactory(com.iotcore.core.dao.factory.DaoFactory, java.lang.Class)}.
	 */
	@Test
	void testRegisterFactoryDaoFactoryClassOfQextendsEntityDaoOfQQ() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link com.iotcore.core.dao.factory.DaoFactory#getDao(java.lang.Class)}.
	 */
	@Test
	void testGetDao() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link com.iotcore.core.dao.factory.DaoFactory#DaoFactory()}.
	 */
	@Test
	void testDaoFactory() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link com.iotcore.core.dao.factory.DaoFactory#registerDao(java.lang.Class)}.
	 */
	@Test
	void testRegisterDao() {
		DaoFactory.registerFactory(factory);
	}

	/**
	 * Test method for {@link com.iotcore.core.dao.factory.DaoFactory#getDaoImplementationClass(java.lang.Class)}.
	 */
	@Test
	void testGetDaoImplementationClass() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link com.iotcore.core.dao.factory.DaoFactory#createInstance(java.lang.Class)}.
	 */
	@Test
	void testCreateInstance() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link com.iotcore.core.dao.factory.DaoFactory#getClassLoader()}.
	 */
	@Test
	void testGetClassLoader() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link com.iotcore.core.dao.factory.DaoFactory#setClassLoader(java.lang.ClassLoader)}.
	 */
	@Test
	void testSetClassLoader() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link com.iotcore.core.dao.factory.DaoFactory#loadDaoClass(java.lang.String)}.
	 */
	@Test
	void testLoadDaoClass() {
		fail("Not yet implemented");
	}

	
	private class MyTestDaoFactory extends DaoFactory {

		@Override
		protected Class<? extends EntityDao<?, ?>> getDaoImplementationClass(Class<?> daoClass) {
			// TODO Auto-generated method stub
			return super.getDaoImplementationClass(daoClass);
		}
		
	}
	
	private class TestEntity extends DomainEntity<String> {
		
	}
	
	private interface TestEntityDao extends EntityDao<TestEntity, String> {
		
	}
	
	private class TestEntityDaoImpl implements TestEntityDao {

		@Override
		public List<TestEntity> findAll(Integer start, Integer count) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public TestEntity findById(String id) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public TestEntity save(TestEntity entity) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void delete(String id) {
			// TODO Auto-generated method stub
			
		}

		
	}
}
