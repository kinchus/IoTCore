/**
 * 
 */
package com.iotcore.aws.backend;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iotcore.core.backend.IBackendConfiguration;
import com.iotcore.core.model.message.IoTMessage;
import com.iotcore.core.util.json.JsonString;


/**
 * @author jmgarcia
 *
 */
public abstract class AbstractMessageHandler<M extends IoTMessage, E> implements MessageHandler<E, M> {

	private static final Logger LOG  = LoggerFactory.getLogger(AbstractMessageHandler.class);
	
	private static IBackendConfiguration backendConfiguration = AwsBackendConfiguration.getInstance();
	

	/**
	 * Get the BackendConfiguration
	 * @return
	 */
	protected static synchronized IBackendConfiguration getBackendConfig() {
		return backendConfiguration;
	}
	
	private Constructor<M> messageConstructor;
	
	
	/**
	 * @param messageClass
	 */
	protected AbstractMessageHandler(Class<M> messageClass) {
		try {
			messageConstructor = messageClass.getConstructor(JsonString.class);
		} catch (NoSuchMethodException | SecurityException e) {
			LOG.error("Exception: {}", e.getMessage());
			e.printStackTrace();
		}
	}


	/**
	 * @param message
	 * @return
	 */
	protected M getMessageFromInput(String message) {
		JsonString json = new JsonString(message);
		if (LOG.isTraceEnabled()) {
			LOG.trace("Processing message: {}", json.toString());
		}
			
		M ret = null;
		try {
			ret = messageConstructor.newInstance(json);
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			LOG.error("Exception: {}", e.getMessage());
		}
		return ret;
	}
	
}
