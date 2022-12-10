/**
 * 
 */
package com.iotcore.core.model.event;

import javax.inject.Inject;

/**
 * @author jmgarcia
 *
 */
public abstract class EventBus implements IEventBus {
	
	private static final long serialVersionUID = 5842063467933720782L;
	
	@Inject
	protected static IEventBus _instance;
		
	/**
	 * @return
	 */
	public static void setInstance(IEventBus instance) {
		_instance = instance;
	}
	
	
	/**
	 * @return
	 */
	public static IEventBus getInstance() {
		return _instance;
	}
	
	
	/**
	 * 
	 */
	protected EventBus() {
		
	}
	
	
	public String send(EventAdaptor<?> message) {
		if (publish(message.getEvent())) {
			return message.getEvent().getId();
		}
		else {
			return null;
		}
	}

}
