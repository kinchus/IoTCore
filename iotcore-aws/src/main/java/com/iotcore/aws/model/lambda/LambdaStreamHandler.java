package com.iotcore.aws.model.lambda;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.services.lambda.runtime.ClientContext;
import com.amazonaws.services.lambda.runtime.CognitoIdentity;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;


/**
 * 
 * @author <a href="mailto:garciadjx@gmail.com">J.M. Garcia</a>
 */
public abstract class LambdaStreamHandler implements RequestStreamHandler {

	private static final Logger LOG = LoggerFactory.getLogger(LambdaStreamHandler.class);

	protected static final String OUTPUT_OK = "OK ";
	protected static final String OUTPUT_ERROR = "ERROR ";

	private static Context createLocalContext() {
		return new Context() {

			@Override
			public String getAwsRequestId() {
				return null;
			}

			@Override
			public ClientContext getClientContext() {
				return null;
			}

			@Override
			public String getFunctionName() {
				return null;
			}

			@Override
			public String getFunctionVersion() {
				return null;
			}

			@Override
			public CognitoIdentity getIdentity() {
				return null;
			}

			@Override
			public String getInvokedFunctionArn() {
				return null;
			}

			@Override
			public LambdaLogger getLogger() {
				return null;
			}

			@Override
			public String getLogGroupName() {
				return null;
			}

			@Override
			public String getLogStreamName() {
				return null;
			}

			@Override
			public int getMemoryLimitInMB() {
				return 0;
			}

			@Override
			public int getRemainingTimeInMillis() {
				return 0;
			}

		};
	}

	private Context context;

	/**
	 */
	private void clearContext() {
		this.context = null;
	}

	/**
	 * @return the context
	 */
	protected Context getContext() {
		if (context == null) {
			context = createLocalContext();
		}
		return context;
	}

	/**
	 * Handles a Lambda Function request
	 * 
	 * @param input
	 * @param output
	 * @throws IOException
	 */
	public abstract void handleRequest(InputStream input, OutputStream output) throws IOException;

	/**
	 * @param input
	 * @param output
	 * @param context
	 * @throws IOException
	 * @see com.amazonaws.services.lambda.runtime.RequestStreamHandler#handleRequest(java.io.InputStream,
	 *      java.io.OutputStream, com.amazonaws.services.lambda.runtime.Context)
	 */
	@Override
	public void handleRequest(InputStream input, OutputStream output, Context context) throws IOException {
		setContext(context);
		handleRequest(input, output);
		clearContext();

	}

	/**
	 * Utility method for simple logging and returning of messages from error
	 * situations
	 * 
	 * @param ostream
	 * @param errInfo
	 */
	protected String outputError(OutputStream ostream, String errInfo) {

		String message = OUTPUT_ERROR;

		if (errInfo == null) {
			message += errInfo;
		}

		message += "\n";
		try {
			ostream.write(message.getBytes());
		} catch (final IOException e) {
		}
		return message;
	}

	/**
	 * Utility method for simple logging and returning of messages from error
	 * situations
	 * 
	 * @param errInfo
	 */
	protected String outputError(String errInfo) {
		final String message = OUTPUT_ERROR + errInfo;
		LOG.error(message);
		return message;
	}

	/**
	 * Utility method for simple logging and returning of messages from error
	 * situations
	 * 
	 * @param ostream
	 * @param msgInfo
	 */
	protected String outputMsg(OutputStream ostream, String msgInfo) {
		String message = OUTPUT_OK;

		if (msgInfo == null) {
			message += msgInfo;
		}

		message += "\n";
		try {
			ostream.write(message.getBytes());
		} catch (final IOException e) {
		}
		return message;
	}

	/**
	 * @param context the context to set
	 */
	private void setContext(Context context) {
		this.context = context;
	}

}
