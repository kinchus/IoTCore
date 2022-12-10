/**
 * 
 */
package com.laiwa.iotcloud.aws.model.message;

import static org.junit.Assert.fail;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iotcore.aws.AwsConfig;
import com.iotcore.aws.model.timestream.TimestreamQuery;
import com.iotcore.aws.services.AwsTimestreamQueryService;
import com.laiwa.iotcloud.core.exception.ServiceException;
import com.laiwa.iotcloud.core.metrics.ResultRow;

/**
 * @author jmgarcia
 *
 */
public class TestAwsTimestreamQuery {
	
	private static final Logger LOG = LoggerFactory.getLogger(TestAwsTimestreamQuery.class);

	private static final String SRC_DATABASE = "IoTCloud";
	private static final String SRC_TABLE = "000000000000000000000000.MsgMetrics";
	private static final String TARGET_DB = "IoTCloud";
	private static final String TARGET_TABLE = "000000000000000000000000.MsgAggregateMetrics";
	
	private static final String HOLDERID = "000000000000000000000000";
	private static final String DEV_EUI = "0101010101010101";
	
	
	private static AwsTimestreamQueryService timestreamQuery;

	

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		AwsConfig cfg = AwsConfig.getInstance();
		cfg.setRegion("eu-west-1");
		timestreamQuery = new AwsTimestreamQueryService();
	}

	
	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}


	/**
	 * Test method for {@link com.iotcore.aws.services.AwsTimestreamQueryService#executeQuery(java.lang.String)}.
	 */
	//@Test
	public void testExecuteQuery() {
		Collection<ResultRow> rows;
		String euiArray = "('" + DEV_EUI + "')";
		String query = String.format(MESSAGESTATS_QUERY_TS, TARGET_TABLE, 5, euiArray);
		
		try {
			rows = timestreamQuery.executeQuery(query);
			int c = 0;
			for (ResultRow row:rows) {
				String devEui = row.stringColumn(0);
				System.out.println(c++ + "\t" + devEui);
				
//				TimeSeries sumRes = row.columnValueTimeSeries(1);
//				TimeSeries avgRes = row.columnValueTimeSeries(2);
//				TimeSeries cntRes = row.columnValueTimeSeries(3);
				
				
			}
		} catch (ServiceException e) {
			LOG.error("Exception thrown: {}", e.getMessage());
			e.printStackTrace();
			fail(e.getMessage());
		}
		
	}

	
	/**
	 * Test method for {@link com.iotcore.aws.services.AwsTimestreamQueryService#createScheduledQuery(java.lang.String, java.lang.String, java.lang.String, com.iotcore.aws.model.timestream.TimestreamQuery)}.
	 */
	@Test
	public void testCreateScheduledQuery() {
		
		String queryName = "AggregateMetrics";
		String schedule = "cron(0 * * * ? *)";
		String role = "arn:aws:iam::235303819696:role/IoTCloud.ScheduledQueryExecutor";
		String sql =  String.format(HOLDER_AGGREGATE_METRICS_SQL, HOLDERID, 1);
		
		Map<String,String> dimMappings = new HashMap<String,String>();
		dimMappings.put("deviceEui", "VARCHAR");
		
		TimestreamQuery devStatsQuery = new TimestreamQuery()
				.withName( queryName )
				.withMultiMeasureName( "msg_metrics" )
				.withSql( sql )
				.withTimeColumn("binned_time")
				.withDimensionMappings( dimMappings )
				.withMultiMeasureMapping("rx_data", "BIGINT")
				.withMultiMeasureMapping("avg_data", "DOUBLE")
				.withMultiMeasureMapping("num_msgs", "BIGINT")
				.withMultiMeasureMapping("meter_reading", "BIGINT")
				.withMultiMeasureMapping("image_data", "BIGINT");
				// .withMeasureMapping("sum_size", "MULTI")
				// .withMeasureMapping("avg_size", "MULTI")
				// .withMeasureMapping("msg_count", "MULTI");
		
		
		try {
			String queryArn = timestreamQuery.createScheduledQuery(TARGET_DB, TARGET_TABLE, schedule, role, devStatsQuery);
			LOG.info("Created query {}: {}", queryName, queryArn);
		} catch (ServiceException e) {
			LOG.error("Exception: {}", e.getMessage());
			e.printStackTrace();
		}
		
	}
	
	
	public static final String HOLDER_AGGREGATE_METRICS_SQL = "SELECT \n"
			+ "		deviceEui,\n"
			+ "		BIN(time, 1h) AS binned_time, \n"
			+ "		SUM(size) AS rx_data,\n"
			+ "		AVG(size) as avg_data,\n"
			+ "		COUNT(*) AS num_msgs,\n"
			+ "		COUNT_IF(type = 'meter_reading') AS meter_reading, \n"
			+ "		COUNT_IF(type = 'image_data') AS image_data\n"
			+ "FROM \"" + SRC_DATABASE + "\".\"" +  SRC_TABLE + "\"\n"
			+ "WHERE \n"
			+ "		holderId = '%s'\n"
			+ "		AND measure_name = 'message'\n"
			+ "		AND time between ago(%dh) and now() \n"
			+ "GROUP BY deviceEui, BIN(time, 1h)\n"
			+ "ORDER BY deviceEui, binned_time ";


	
	public static final String MESSAGESTATS_QUERY_TS = "WITH \n"
			+ "	binned_timeseries AS (\n"
			+ "		SELECT \n"
			+ "        	deviceEui,\n"
			+ "        	time as binned_time, \n"
			+ "      	num_msgs AS binned_num_msgs,\n"
			+ "      	rx_data AS binned_rx_data,\n"
			+ "      	avg_data AS binned_avg_data,\n"
			+ "      	meter_reading AS binned_meter_reading,\n"
			+ "      	image_data AS binned_image_data\n"
			+ "		FROM \""  + TARGET_DB + "\".\"%s\" \n"
			+ "		WHERE  \n"
			+ "			time between ago(%dd) and now() \n"
			+ "			AND deviceEui IN %s \n"
			+ "		GROUP BY deviceEui, time, rx_data, avg_data, num_msgs, meter_reading, image_data \n"
			+ "     ORDER BY time DESC\n"
			+ "	),\n"
			+ "	interpolated_data AS (\n"
			+ "		SELECT \n"
			+ "			deviceEui,\n"
			+ "			INTERPOLATE_FILL(\n"
			+ "              CREATE_TIME_SERIES(binned_time, binned_rx_data),\n"
			+ "              SEQUENCE(min(binned_time), max(binned_time), 1h), \n"
			+ "              0) AS rx_data,\n"
			+ "         INTERPOLATE_FILL(\n"
			+ "              CREATE_TIME_SERIES(binned_time, binned_avg_data),\n"
			+ "              SEQUENCE(min(binned_time), max(binned_time), 1h), \n"
			+ "              0) AS avg_data,\n"
			+ "      	INTERPOLATE_FILL(\n"
			+ "              CREATE_TIME_SERIES(binned_time, binned_num_msgs),\n"
			+ "              SEQUENCE(min(binned_time), max(binned_time), 1h), \n"
			+ "              0) AS num_msgs,\n"
			+ "			INTERPOLATE_FILL(\n"
			+ "              CREATE_TIME_SERIES(binned_time, binned_meter_reading),\n"
			+ "              SEQUENCE(min(binned_time), max(binned_time), 1h), \n"
			+ "              0) AS meter_reading,\n"
			+ "			INTERPOLATE_FILL(\n"
			+ "              CREATE_TIME_SERIES(binned_time, binned_image_data),\n"
			+ "              SEQUENCE(min(binned_time), max(binned_time), 1h), \n"
			+ "              0) AS image_data\n"
			+ "		FROM binned_timeseries\n"
			+ "      	GROUP BY deviceEui\n"
			+ "		ORDER BY deviceEui\n"
			+ "	)\n"
			+ "SELECT *\n"
			+ "FROM interpolated_data";
	
	public static final String DATAUSAGE_ALARM_QUERY = "WITH \n"
			+ "	binned_timeseries AS (\n"
			+ "		SELECT \n"
			+ "        	deviceEui,\n"
			+ "        	time as binned_time,\n"
			+ "      	rx_data AS binned_rx_data\n"
			+ "		FROM \""  + TARGET_DB + "\".\"%s\" \n"
			+ "		WHERE  \n"
			+ "			time between ago(%dh) and now() \n"
			+ "			AND deviceEui IN %s \n"
			+ "		GROUP BY deviceEui, time, rx_data\n"
			+ "     ORDER BY time DESC\n"
			+ "	),\n"
			+ "	interpolated_data AS (\n"
			+ "		SELECT \n"
			+ "			deviceEui,\n"
			+ "			INTERPOLATE_FILL(\n"
			+ "              CREATE_TIME_SERIES(binned_time, binned_rx_data),\n"
			+ "              SEQUENCE(min(binned_time), max(binned_time), 1h), \n"
			+ "              0) AS rx_data,\n"
			+ "		FROM binned_timeseries\n"
			+ "     GROUP BY deviceEui\n"
			+ "		ORDER BY deviceEui\n"
			+ "	)\n"
			+ "SELECT *\n"
			+ "FROM interpolated_data";
	

	
}
