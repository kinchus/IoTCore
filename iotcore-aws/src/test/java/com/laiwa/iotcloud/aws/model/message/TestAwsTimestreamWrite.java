/**
 * 
 */
package com.laiwa.iotcloud.aws.model.message;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iotcore.aws.AwsConfig;
import com.iotcore.aws.model.timestream.Records;
import com.iotcore.aws.services.AwsTimestreamWriteService;
import com.laiwa.iotcloud.core.exception.ServiceException;

import software.amazon.awssdk.services.timestreamwrite.model.Dimension;
import software.amazon.awssdk.services.timestreamwrite.model.Record;

/**
 * @author jmgarcia
 *
 */
public class TestAwsTimestreamWrite {
	
	private static final Logger LOG = LoggerFactory.getLogger(TestAwsTimestreamWrite.class);
	
	private static final String TESTDB = "TEST";
	private static final String TESTTABLE = "TEST";
	
	private static final String DEV_EUI = "0000000000000000";
	private static final String HOLDER_ID = "000000000000000";
	
	
	private static AwsTimestreamWriteService service;
	private static Long counter = 0L;
	

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		AwsConfig cfg = AwsConfig.getInstance();
		cfg.setRegion("eu-west-1");
		service = new AwsTimestreamWriteService();
		service.createDatabase(TESTDB);
		service.createTable(TESTDB, TESTTABLE);
		
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		service.deleteTable(TESTDB, TESTTABLE);
		service.deleteDatabase(TESTDB);
	}
	
	

	/**
	 * Test method for {@link com.iotcore.aws.services.AwsTimestreamWriteService#write(java.lang.String, java.lang.String, software.amazon.awssdk.services.timestreamwrite.model.Record, java.util.List)}.
	 */
	@Test
	public void testWriteRecord() {
	
	//	IoTDeviceMessage msg = createDeviceMessage(DEV_EUI, counter++);
		
		List<Dimension> dims = getDimensions();
		
		Record record = Records.multiValueRecord(null, dims);

		LOG.debug("Writing record");
		try {
			service.write(TESTDB, TESTTABLE, Arrays.asList(record));
		} catch (ServiceException e) {
			LOG.error("Exception thrown: {}", e.getMessage());
			e.printStackTrace();
		}
		LOG.info("OK");
	}
	
	
	@Test
	public void testWriteRecords() {
	
		List<Dimension> dims = getDimensions();
		List<Record> records = new ArrayList<Record>();
		
		for (int i=0; i<10; i++) {
		//	IoTMessage msg = createDeviceMessage(DEV_EUI, counter++);
		//	records.add(Records.multiValueRecord(msg, dims));
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {}
		}

		LOG.debug("Writing record");
		try {
			service.write(TESTDB, TESTTABLE, records);
		} catch (ServiceException e) {
			LOG.error("Exception thrown: {}", e.getMessage());
			e.printStackTrace();
		}
		LOG.info("OK");
	}


	
	
	public static List<Dimension> getDimensions() {
		List<Dimension> dimensions = new ArrayList<>();
		
		dimensions.add(Dimension.builder()
							.name("deviceEui")
							.value(DEV_EUI)
							.build());
		dimensions.add(Dimension.builder()
							.name("holderId")
							.value(HOLDER_ID)
							.build());
		
		return dimensions;
	}
	

/*
	private static IoTDeviceMessage createDeviceMessage(String devEui, Long counter) {
		IoTDeviceMessage msg = new IoTDeviceMessage();
		msg.setApplication("TestApplication");
		msg.setDeviceID(devEui);
		msg.setCounter(counter);
		msg.setTimestamp(System.currentTimeMillis());
		msg.setMsgType("TEST");
		msg.setSensorID(0);
		
		double r = Math.random()*20;
		StringBuffer buff = new StringBuffer();
		for (int i=0; i<r;i++) {
			buff.append('0');
		}
		
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("test", Boolean.TRUE);
		data.put("contents", buff.toString());
		msg.setData(data);
		return msg;
	}
	*/
	
}
