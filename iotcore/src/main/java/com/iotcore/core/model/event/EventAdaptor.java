/**
 * 
 */
package com.iotcore.core.model.event;

import java.io.Serializable;

/**
 * @author jmgarcia
 *
 */
public abstract class EventAdaptor<E extends Event<?>> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2434695490544344126L;
	private E event;
	
	/**
	 * 
	 */
	public EventAdaptor() {
		
	}

	/**
	 * @return the event
	 */
	public E getEvent() {
		return event;
	}

	/**
	 * @param event the event to set
	 */
	public void setEvent(E event) {
		this.event = event;
	}

}
