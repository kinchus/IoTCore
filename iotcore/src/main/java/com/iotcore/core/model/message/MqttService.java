package com.iotcore.core.model.message;

import java.io.Serializable;

import com.iotcore.core.model.exception.ServiceException;

public interface MqttService extends Serializable {
	

	/**
	 * @param topic
	 * @param message
	 * @return
	 * @throws Exception
	 */
	Boolean publish(String topic, String message) throws ServiceException;


}