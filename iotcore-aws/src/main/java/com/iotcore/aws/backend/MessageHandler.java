/**
 * 
 */
package com.iotcore.aws.backend;

import java.util.Collection;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

/**
 * @author jmgarcia
 *
 * @param <M>
 * @param <E>
 */
public interface MessageHandler<E, M> extends RequestHandler<E, Void> {

	/**
	 * Implement the business logic specific for the actual message
	 * @param input
	 * @throws Exception
	 */
	void handleMessage(M input);
	
	/**
	 * @param request
	 * @return
	 */
	Collection<M> getMessagesFromRequest(E request);


	@Override
	default Void handleRequest(E event, Context context) {
		Collection<M> msgs = getMessagesFromRequest(event);
		msgs.forEach(this::handleMessage);
		return null;
	}


}