/**
 * 
 */
package com.iotcore.mongo.monitor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iotcore.core.dao.IdEntity;
import com.iotcore.mongo.MongoManager;
import com.mongodb.client.ChangeStreamIterable;
import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.changestream.ChangeStreamDocument;
import com.mongodb.client.model.changestream.OperationType;

/**
 * @author jmgarcia
 *
 */
public class DbCollectionMonitor<C extends IdEntity<?>> {
	
	private static final Logger LOG  = LoggerFactory.getLogger(DbCollectionMonitor.class);
	
	private CollectionChangeListener<C> changeListener;
	private MongoCollection<C> collection;
	private DbCollectionWatcher watcher;
	private ChangeStreamIterable<C> watchStream;
	
	
	/**
	 * 
	 */
	public DbCollectionMonitor() {
		
	}
	
	/**
	 * @param jobManager
	 * @param colName
	 * @param colClass
	 * @param colClasses
	 */
	public DbCollectionMonitor(CollectionChangeListener<C> jobManager, String colName, Class<C> colClass, Class<?> [] colClasses) {
		this.changeListener = jobManager;
		this.collection = MongoManager.getInstance().getCollection(colName, colClass, colClasses);
	}
	
	/**
	 * @return
	 */
	public boolean start() {
		
		if (collection == null) {
			LOG.error("MongoCollection not initialized");
			return false;
		}
		
		LOG.debug("Starting watcher thread");
		watcher = new DbCollectionWatcher(collection);
		new Thread(watcher).start();
		return true;
	}
	

	/**
	 * 
	 */
	public void stop() {
		
		LOG.debug("Closing MongoDB watch stream cursor");
		try {
			watchStream.cursor().close();
		}
		catch (Exception e) {
			LOG.error("{}", e.getMessage());
		}
		
	}

		
	/**
	 * @return the jobManager
	 */
	public CollectionChangeListener<C> getCollectionChangeListener() {
		return changeListener;
	}


	/**
	 * @param listener the CollectionChangeListener to set
	 */
	public void setCollectionChangeListener(CollectionChangeListener<C> listener) {
		this.changeListener = listener;
	}
	

	/**
	 * @param listener the CollectionChangeListener to set
	 */
	public DbCollectionMonitor<C> withCollectionChangeListener(CollectionChangeListener<C> listener) {
		this.changeListener = listener;
		return this;
	}


	/**
	 * @return the collection
	 */
	public MongoCollection<C> getCollection() {
		return collection;
	}

	/**
	 * @param collection the collection to set
	 */
	public void setCollection(MongoCollection<C> collection) {
		this.collection = collection;
	}

	
	/**
	 * @param collectionName the collectionName to set
	 */
	public DbCollectionMonitor<C> withCollection(MongoCollection<C> collection) {
		this.collection = collection;
		return this;
	}
	

	/**
	 * @param chgStreamDoc
	 */
	private void manage(ChangeStreamDocument<C> chgStreamDoc) {
		C doc = chgStreamDoc.getFullDocument();
		OperationType type = chgStreamDoc.getOperationType();
		
		LOG.trace("DB Collection Modified - {} : Doc.ID={}", type.getValue(), doc.getId());

		switch (type) {
			case DROP :
			case DROP_DATABASE :
			case INVALIDATE :
			case DELETE :
				// Document Removed
				changeListener.onRemoveDocument(doc);
				break;
			case INSERT :
				// Document Inserted
				changeListener.onInsertDocument(doc);
				break;
			case UPDATE :
			case RENAME :
			case REPLACE :
				// Document Updated
				changeListener.onUpdateDocument(doc);
				break;
			case OTHER :
			default :
				break;
			
		}
	}
	
	
	/**
	 * @author jmgarcia
	 *
	 */
	public class DbCollectionWatcher implements Runnable {
		
		private ClientSession mongoSession;

		
		/**
		 * @param collection
		 */
		public DbCollectionWatcher(MongoCollection<C> collection) {
			setMongoSession(MongoManager.getInstance().getClient().startSession());
			if (getMongoSession() != null) {
				watchStream = collection.watch(getMongoSession());
			}
			else {
				watchStream = collection.watch();
			}
		}

		@Override
		public void run() {
			try {
				watchStream.forEach(d -> manage(d));
			}
			catch (Exception e) {
				LOG.error("{}", e.getMessage());
			}
		}
		
		/**
		 * @return the mongoSession
		 */
		public ClientSession getMongoSession() {
			return mongoSession;
		}

		/**
		 * @param mongoSession the mongoSession to set
		 */
		public void setMongoSession(ClientSession mongoSession) {
			this.mongoSession = mongoSession;
		}

		
		
	}


}
