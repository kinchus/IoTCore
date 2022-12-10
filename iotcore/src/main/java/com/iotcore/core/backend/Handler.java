package com.iotcore.core.backend;

import java.io.Serializable;

public interface Handler<I,O> extends Serializable {

	static final String OUTPUT_OK = "OK";
	static final String OUTPUT_ERROR = "ERROR ";


	O handle(I input) throws Exception;
		
}
