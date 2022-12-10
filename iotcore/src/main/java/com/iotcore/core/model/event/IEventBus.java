package com.iotcore.core.model.event;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;

/**
 * 
 * @author <a href="mailto:garciadjx@gmail.com">J.M. Garcia</a>
 */
public interface IEventBus extends Serializable {
	

	/**
	 * @return
	 */
	static IEventBus getInstance() {
		// TODO Auto-generated method stub
		return null;
	}


	
	/**
	 * 
	 */
	void init();
	
	
    /**
     * @param busName
     * @param event
     * @return
     */
    boolean publish(String busName, Event<?> event);
    
    /**
	 * @param evts
	 * @return
	 */
	<E extends Event<E>> Collection<E> publish(Collection<E> evts);
    
    
    /**
     * @param event
     * @return
     */
    default boolean publish(Event<?> event) {
    	return publish(event.getBusName(), event);
    }
	
	
    /**
     * @param events
     */
    default void publish(String busName, Collection<Event<?>> events) {
        events.stream().forEach(e -> publish(busName, e));
    }
    
    /**
     * @param events
     */
    default void publish(String busName, Event<?> ... events) {
    	Arrays.asList(events).stream().forEach(e -> publish(busName, e));
    }



}