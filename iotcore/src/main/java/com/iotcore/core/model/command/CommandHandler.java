/**
 * 
 */
package com.iotcore.core.model.command;

import java.io.Serializable;

import com.iotcore.core.model.event.Event;
import com.iotcore.core.model.event.IEventBus;

/**
 * @author jmgarcia
 *
 */
public interface CommandHandler<C extends Command> extends Serializable {

	IEventBus getEventBus();
	
	
	String handle(C event) throws Exception;

	default String publish(String busName, Event<?> event) {
		getEventBus().publish(busName, event);
		return event.getId();
	}
	
}
