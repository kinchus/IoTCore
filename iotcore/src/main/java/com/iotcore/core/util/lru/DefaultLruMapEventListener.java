package com.iotcore.core.util.lru;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultLruMapEventListener implements LruMapEventListener {
	
	private static final Logger LOG = LoggerFactory.getLogger(DefaultLruMapEventListener.class);
	
	@Override
	public void onLruMapIsEmpty() {
		if (LOG.isTraceEnabled()) {
			LOG.trace("LRU map is empty");
		}
	}

	@Override
	public void onLruMapIsFull() {
		if (LOG.isTraceEnabled()) {
			LOG.trace("LRU map is full");
		}
	}

	@Override
	public void onLruMapRefresh() {
		if (LOG.isTraceEnabled()) {
			LOG.trace("LRU map refreshed");
		}
	}

	@Override
	public void onItemCached(Object key) {
		if (LOG.isTraceEnabled()) {
			LOG.trace("New item added: {}", key);
		}
	}

	@Override
	public void onItemUpdated(Object key) {
		if (LOG.isTraceEnabled()) {
			LOG.trace("Empty cache");
		}
	}

	@Override
	public void onItemExpired(Object key) {
		if (LOG.isTraceEnabled()) {
			LOG.trace("Empty cache");
		}
	}

	@Override
	public void onSettinsChanged() {
		if (LOG.isTraceEnabled()) {
			LOG.trace("Empty cache");
		}
	}

}
