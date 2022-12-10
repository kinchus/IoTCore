package com.iotcore.core.util.lru;
import java.util.AbstractMap;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
 



/**
 * Implementation of a lru cache
 * @param <K>
 * @param <V>
 *
* @author <a href="mailto:garciadjx@gmail.com">J.M. Garcia</a>
 */
public class LruMap<K, V> extends AbstractMap<K, V>{
 
	private static final Logger LOG = LoggerFactory.getLogger(LruMap.class);
	
	private static final int MAX_ITEMS = 1000;
	private static final int CACHE_TIMER_MILLIS = 60*1000;
	
	public static boolean eagerRenew = false;
	
	private long cacheTimer;
	private long ttlMillis;
	private int maxItems = MAX_ITEMS;
    private Map<K,TtlObject> lruMap;
    private List<K> deleteKeys;
    
    
    /**
     * Wrapper class for cache objects
     */
    protected class TtlObject {
        private long lastAccess = System.currentTimeMillis();
        private boolean dirty = false;
        private V object;
 
        protected TtlObject(V value) {
            this.object = value;
        }
        
        /**
         * Retrieve the timed object and update the last access time
         * @return
         */
        protected V get() {
        	lastAccess = System.currentTimeMillis();
        	LOG.trace("New access to object {}:  {}", object);
        	return object;
        }
        
        /**
         * Retrieve the timed object without updating the last access time
         * @return
         */
        protected V peek() {
        	return object;
        }
        
        protected boolean isDirty() {
        	return dirty;
        }
        
        protected void setDirty() {
        	dirty = true;
        }
    }
 
    
    /**
     * LRU Cache with no defined ttl for items
     * @param maxItems Constrained maixmum
     */
    public LruMap(int maxItems) {
    	this(-1, maxItems);
    }
        
    
    /**
     * @param ttl Time To Live (seconds) of the objects in the cache
     * @param maxItems
     */
    public LruMap(long ttl,  int maxItems) {
    	this(ttl, maxItems, CACHE_TIMER_MILLIS);
    }
    
    /**
     * @param ttl Time To Live (seconds) of the objects in the cache
     * @param maxItems
     * @param cacheTimerMillis 
     */
    public LruMap(long ttl,  int maxItems, long cacheTimerMillis) {
    	
    	this.maxItems = maxItems;
    	this.ttlMillis = ttl * 1000;
    	this.cacheTimer = cacheTimerMillis;
    	this.lruMap = Collections.synchronizedMap(new HashMap<K,TtlObject>(maxItems));
                
    }
 
 
    /**
	 * @return the cacheTimerMillis
	 */
	public long getCacheTimerMillis() {
		return cacheTimer;
	}


	/**
	 * @param cacheTimerMillis the cacheTimerMillis to set
	 */
	public void setCacheTimerMillis(long cacheTimerMillis) {
		this.cacheTimer = cacheTimerMillis;
	}


	/**
     *
     */
    @Override
	public V put(K key, V value) {
    	
    	// cleanOldests(lruyMap.size() - maxItems);
    	
    	if (lruMap.size() >= maxItems) {
    		cleanOldest();
        }
    	
    	if (LOG.isTraceEnabled()) {
    		LOG.trace("Caching object {}", key);
		}
        lruMap.put(key, new TtlObject(value));
        return value;
    }
    
 
    /**
     * @param key
     * @return
     */
    @Override
	public V get(Object key) {
        TtlObject c = lruMap.get(key);
        if (c == null) {
        	return null;
        }
        return c.peek();
        
    }
 
    /**
     * @param key
     */
    @Override
	public V remove(Object key) {
    	TtlObject ret = lruMap.remove(key);
    	if (ret != null) {
    		return ret.get();
    	}
    	return null;
    }
    
   /**
     * @return
     * @see java.util.AbstractMap#size()
     */
    @Override
	public int size() {
        return lruMap.size();
    }
 

	/**
	 * @return
	 * @see java.util.AbstractMap#entrySet()
	 */
	@Override
	public Set<Entry<K, V>> entrySet() {
		Set<Entry<K, V>> ret = new LinkedHashSet<Entry<K, V>>();
		lruMap.entrySet().forEach(e -> ret.add(
				new AbstractMap.SimpleImmutableEntry<K, V>(e.getKey(), e.getValue().peek()))
			);
		return ret;
	}
	
	public void setMaxSize(int maxSize) {
		this.maxItems = maxSize;
	}
	
	
	protected void refresh() {
		
        long now = System.currentTimeMillis();
 
        for (Entry<K,TtlObject> v: lruMap.entrySet()) {
    		K key = v.getKey();
    		TtlObject c = v.getValue();
    		if (c != null && (now > (ttlMillis + c.lastAccess))) {
            	LOG.trace("Key to remove: {}", key);
                deleteKeys.add(key);
            }
        }
        deleteKeys.forEach(key -> lruMap.remove(key));
        deleteKeys.clear();
        	
        Thread.yield();
        
    }
	
	public void cleanOldest() {
		
		K deleteKey = null;
        long oldestAccess = Long.MAX_VALUE;
        
    	for (Entry<K,TtlObject> v: lruMap.entrySet()) {
    		K key = v.getKey();
    		TtlObject c = v.getValue();
    		if (c != null && (c.lastAccess < oldestAccess)) {
            	LOG.trace("Key to remove: ", key);
                deleteKey = (key);
            }
    	}
    	
    	 if (deleteKey != null) {
    		 lruMap.remove(deleteKey);
    	 }
    }
	
	
	public void cleanOldests(int num) {
		
		if (num < 1) {
			return;
		}
		
		Object[] deleteKey = new Object[num];
		long[] keyAccessTime = new long[num];
		
		for (int i = 0; i < num; i++) {
			deleteKey[i] =  null;
			keyAccessTime[i] = Long.MAX_VALUE;
		}
        
        
        
        for (Entry<K,TtlObject> v: lruMap.entrySet()) {
        	K key = v.getKey();
        	TtlObject c = v.getValue();
        	int n = -1;
        	long maxDiff = 0;
        	for (int i = 0; i < num; i++) {
        		long diff = keyAccessTime[i] - c.lastAccess;
        		if (diff > maxDiff) {
        			maxDiff = diff;
        			n = i;
        		}
    		}
        	if (n >= 0) {
        		deleteKey[n] = key;
        		keyAccessTime[n] = c.lastAccess;
        	}
        }
        	
        for (int i = 0; i < num; i++) {
			if (deleteKey[i] != null) {
				lruMap.remove(deleteKey[i]);
			}
		}
    }


}