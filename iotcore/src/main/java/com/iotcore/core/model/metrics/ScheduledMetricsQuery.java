/**
 * 
 */
package com.iotcore.core.model.metrics;

// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;

/**
 * @author jmgarcia
 *
 */
public class ScheduledMetricsQuery extends MetricsQuery {

	private static final long serialVersionUID = -6903179582322664230L;
	// private static final  Logger LOG = LoggerFactory.getLogger(ScheduledMetricsQuery.class);
	
	private String schedule;
	private String targetDb;
	private String targetTable;
	

	/**
	 * 
	 */
	public ScheduledMetricsQuery() {
	}

	/**
	 * @param querySql
	 * @param parameterNames
	 */
	public ScheduledMetricsQuery(String querySql, String[] parameterNames) {
		super(querySql, parameterNames);
	}
	// 

	/**
	 * @return the schedule
	 */
	public String getSchedule() {
		return schedule;
	}

	/**
	 * @param schedule the schedule to set
	 */
	public void setSchedule(String schedule) {
		this.schedule = schedule;
	}

	/**
	 * @return the targetDb
	 */
	public String getTargetDb() {
		return targetDb;
	}

	/**
	 * @param targetDb the targetDb to set
	 */
	public void setTargetDb(String targetDb) {
		this.targetDb = targetDb;
	}

	/**
	 * @return the targetTable
	 */
	public String getTargetTable() {
		return targetTable;
	}

	/**
	 * @param targetTable the targetTable to set
	 */
	public void setTargetTable(String targetTable) {
		this.targetTable = targetTable;
	}

}
