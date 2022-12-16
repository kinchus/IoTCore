/**
 * 
 */
package com.iotcore.core.dao;

import java.io.Serializable;
import java.util.Collection;

/**
 * @author jmgarcia
 *
 */
public class ResultsPage<T> implements Serializable {
	
	private static final long serialVersionUID = -4033358252481798374L;

	private Collection<T> results;
	private long count;
	private long total;
	
	/**
	 * 
	 */
	public ResultsPage(Collection<T> results) {
		this.results = results;
		this.count = results.size();
		this.total = count;
	}
	
	/**
	 * 
	 */
	public ResultsPage(Collection<T> results, long total) {
		this.results = results;
		this.count = results.size();
		this.total = total;
	}

	/**
	 * @return the results
	 */
	public Collection<T> getResults() {
		return results;
	}

	/**
	 * @return the count
	 */
	public long getCount() {
		return count;
	}

	/**
	 * @return the total
	 */
	public long getTotal() {
		return total;
	}

	/**
	 * @param results the results to set
	 */
	public void setResults(Collection<T> results) {
		this.results = results;
	}

	/**
	 * @param count the count to set
	 */
	public void setCount(long count) {
		this.count = count;
	}

	/**
	 * @param total the total to set
	 */
	public void setTotal(long total) {
		this.total = total;
	}

}
