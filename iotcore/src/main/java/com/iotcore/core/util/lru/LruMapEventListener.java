/**
 * 
 */
package com.iotcore.core.util.lru;

/**
 * @author jmgarcia
 *
 */
public interface LruMapEventListener {
	
	void onLruMapIsEmpty();
	
	void onLruMapIsFull();
	
	void onLruMapRefresh();
	
	void onItemCached(Object key);
	
	void onItemUpdated(Object key);
	
	void onItemExpired(Object key);
	
	void onSettinsChanged();
	
}
