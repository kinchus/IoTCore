package com.iotcore.aws.model.lambda;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iotcore.core.backend.Handler;

import software.amazon.awssdk.core.interceptor.Context;

/**
 * 
 * @author <a href="mailto:garciadjx@gmail.com">J.M. Garcia</a>
 * @param <E>
 */
public interface AwsLambda<E> extends Handler<E, String> {

	static final Logger LOG = LoggerFactory.getLogger(AwsLambda.class);

	/**
	 * @param event
	 * @param context
	 * @return
	 * @see com.amazonaws.services.lambda.runtime.RequestHandler#handleRequest(java.lang.Object,
	 *      com.amazonaws.services.lambda.runtime.Context)
	 */
	default String handleRequest(E event, Context context) {

		try {
			return handle(event);
		} catch (final Exception e) {
			LOG.error("Exception thrown: {}", e.getMessage());
			e.printStackTrace();
			return OUTPUT_ERROR + e.getMessage();
		}
	}

}
