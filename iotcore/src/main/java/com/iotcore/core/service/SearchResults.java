/**
 * 
 */
package com.iotcore.core.service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author jmgarcia
 *
 */
public class SearchResults<T> {
	
	private List<T> items;
	private int expected;
	
	/**
	 * 
	 */
	public SearchResults(int expectedItemsCount) {
		expected = expectedItemsCount;
		items = new ArrayList<T>(expectedItemsCount);
	}
	

	/**
	 * @return the items
	 */
	public int getItemsCount() {
		return items.size();
	}
	/**
	 * @return the items
	 */
	public List<T> getItems() {
		return items;
	}

	/**
	 * @param items the items to set
	 */
	public void addItems(List<T> items) {
		this.items.addAll(items);
	}

	/**
	 * @return
	 */
	public boolean moreItemsFollow() {
		return expected <= getItemsCount(); 
	}
}
