package com.iotcore.core.model.command;

import java.io.Serializable;
import java.util.Date;

public interface Command extends Serializable {

	/**
	 * @return the _id
	 */
	String getId();

	/**
	 * @return the createdOn
	 */
	Date getCreatedOn();

}