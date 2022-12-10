/**
 * 
 */
package com.iotcore.core.model.event;

import com.iotcore.core.backend.Handler;

/**
 * @author jmgarcia
 *
 */
public interface EventHandler<E extends Event<?>> extends Handler<E,String> {

	String handle(E event)  throws Exception;
	
}
