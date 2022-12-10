/**
 * 
 */
package com.iotcore.core.dao;

/**
 * @author jmgarcia
 *
 */
public interface SortField<O> {
	
	
	enum Order {ASCENDING, DESCENDING};
	

	SortField<O> ascending();
	
	SortField<O> descending();

	String getField();
	
	void setField(String field);
	
	Order getOrder();
	
	void setOrder(Order order);
	
	O toSortObject();
	
	
}
