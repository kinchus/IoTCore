/**
 * 
 */
package com.iotcore.mongo.monitor;


/**
 * @author jmgarcia
 *
 */
public interface CollectionChangeListener<D> {
	
	void onInsertDocument(D document);
	
	void onUpdateDocument(D document);
	
	void onRemoveDocument(D document);
	
	default void onReplaceDocument(D document) {
		onUpdateDocument(document);
	}
	
	default void onRenameDocument(D document) {
		onUpdateDocument(document);
	}
	
	default void onDropDocument(D document) {
		onRemoveDocument(document);
	}
	
}
