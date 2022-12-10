package com.iotcore.aws.backend;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.services.lambda.runtime.ClientContext;
import com.amazonaws.services.lambda.runtime.CognitoIdentity;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.iotcore.aws.services.AwsServiceFactory;

/**
 * 
 * @author <a href="mailto:garciadjx@gmail.com">J.M. Garcia</a>
 */
public abstract class LambdaHandler implements RequestHandler<Map<String,Object>, String> {

	private static final Logger LOG  = LoggerFactory.getLogger(LambdaHandler.class);
	private static final ExecutorService threadPool = Executors.newCachedThreadPool();
	private static final ThreadLocal<Context> localContext = new ThreadLocal<Context>();

	protected static final String OUTPUT_OK = "OK ";
	protected static final String OUTPUT_ERROR = "ERROR ";

	protected static final String  STATUS = "Status";
	protected static final String  REASON = "Reason";
	
	protected static final String  STATUS_SUCCESS = "SUCCESS";
	
	protected static final String  STACK_ID = "StackId";
	protected static final String  RESPONSE_URL  = "ResponseURL";
	protected static final String  REQUEST_TYPE = "RequestType";
	protected static final String  REQUEST_ID = "RequestId";
	protected static final String  RESOURCE_TYPE = "ResourceType";
	protected static final String  LOGICAL_RESOURCE_ID = "LogicalResourceId";
	protected static final String  PHYSICAL_RESOURCE_ID = "PhysicalResourceId";
	protected static final String  RESOURCE_PROPERTIES = "ResourceProperties";
	
	protected static final String  REQ_CREATE = "Create";
	protected static final String  REQ_UPDATE = "Update";
	protected static final String  REQ_DELETE = "Delete";
	
	
	/*
	 * Initialization
	 */
	static {
		LOG.debug("Initializing AWS Services...");
		AwsServiceFactory.init();
	}



	private Context context;
	

	
	/**
	 * @param input
	 * @throws Exception
	 */
	public abstract void handleRequest(Map<String, Object> input) throws Exception;
	
	
	
	/**
	 * @param input
	 * @param context
	 * @return
	 * @see com.amazonaws.services.lambda.runtime.RequestHandler#handleRequest(java.lang.Object, com.amazonaws.services.lambda.runtime.Context)
	 */
	@Override
	public String handleRequest(Map<String, Object> input, Context context) {
		
		localContext().set(context);
		
		 try {
			handleRequest(input);
			return OUTPUT_OK;
		} catch (Exception e) {
			LOG.error("An error occured: {}", e.getMessage());
			e.printStackTrace();
			return OUTPUT_ERROR + e.getMessage();
		}
	}

	/**
	 * @return the localcontext
	 */
	protected ThreadLocal<Context> localContext() {
		return localContext;
	}


	/**
	 * @param input
	 * @param status
	 * @return
	 * @throws IOException 
	 * @throws IOException
	 */
	protected String notifyResource(Map<String, Object> input, String status) throws IOException  {
		
		if (LOG.isTraceEnabled()) LOG.trace("Starting CF resource notification");
		
		Context context = localContext().get();
		if (context == null) {
			throw new NullPointerException("Null context");
		}
		
		String requestType = getInputField(input, REQUEST_TYPE, String.class);
		if (LOG.isTraceEnabled()) LOG.trace("Input request [{}='{}']", RESPONSE_URL, requestType);
		String responseURL = getInputField(input, RESPONSE_URL, String.class);
		if (LOG.isTraceEnabled()) LOG.trace("Input request [{}='{}']", RESPONSE_URL, responseURL);
		String stackId = getInputField(input, STACK_ID, String.class);
		if (LOG.isTraceEnabled()) LOG.trace("Input request [{}='{}']", STACK_ID, stackId);
		String requestId = getInputField(input, REQUEST_ID, String.class);
		if (LOG.isTraceEnabled()) LOG.trace("Input request [{}='{}']", REQUEST_ID, requestId);
		String resourceType = getInputField(input, RESOURCE_TYPE, String.class);
		if (resourceType == null) {
			resourceType = "Custom";
		}
		if (LOG.isTraceEnabled()) LOG.trace("Input request [{}='{}']", RESOURCE_TYPE, resourceType);
		String logicalResourceId = getInputField(input, LOGICAL_RESOURCE_ID, String.class);
		if (LOG.isTraceEnabled()) LOG.trace("Input request: {} = {}", LOGICAL_RESOURCE_ID, logicalResourceId);
		
		String physicalResourceId = getInputFieldOrNull(input, PHYSICAL_RESOURCE_ID, String.class);
		if (physicalResourceId == null) {
			physicalResourceId = context.getLogStreamName();
		}
		if (LOG.isTraceEnabled()) LOG.trace("Input request: {} = {}", PHYSICAL_RESOURCE_ID, physicalResourceId);
		
		String reason = "See the details in CloudWatch Log Stream: " + context.getLogStreamName();
		
		if (LOG.isTraceEnabled()) LOG.trace("Building JSON request...");
		
		StringBuilder json = new StringBuilder("{");
        json.append(String.format("\"%s\" : \"%s\", ", STATUS, status));
        json.append(String.format("\"%s\" : \"%s\", ", REASON, reason));
        json.append(String.format("\"%s\" : \"%s\", ", PHYSICAL_RESOURCE_ID, physicalResourceId));
        json.append(String.format("\"%s\" : \"%s\", ", STACK_ID, stackId));
        json.append(String.format("\"%s\" : \"%s\", ", REQUEST_ID, requestId));
        json.append(String.format("\"%s\" : \"%s\", ", LOGICAL_RESOURCE_ID, logicalResourceId));
        json.append("\"Data\" : { } ");
        json.append("}");
        
        if (LOG.isTraceEnabled()) LOG.trace("JSON request contents:\n {}", json.toString());
        
        URL url = new URL(responseURL);
    	HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    	connection.setDoOutput(true);
    	connection.setRequestMethod("PUT");
    	OutputStreamWriter response = new OutputStreamWriter(connection.getOutputStream());
    	response.write(json.toString());
    	response.close();
    	
    	if (LOG.isDebugEnabled()) {
    		LOG.debug("Response: {} {}", connection.getResponseCode(), connection.getResponseMessage());
    	}
    	
    	return OUTPUT_OK;
        
	}
	

	/**
	 * @param <T>
	 * @param input
	 * @param field
	 * @param clazz
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected static <T> T getInputField(Map<String, Object> input, String field, Class<T> clazz) throws IOException {
		Object aux = input.get(field);
		if (aux == null) {
			String logMsg = String.format("\"%s\" not found in payload", field);
			LOG.error(logMsg);
			throw new IOException(logMsg);
		}
		else if (!clazz.isAssignableFrom(aux.getClass())) {
			String logMsg = String.format("\"%s\" is not of type %s", field, clazz.getSimpleName());
			LOG.error(logMsg);
			throw new IOException(logMsg);
		}
		
		return (T)aux;
	}
	
	/**
	 * @param <T>
	 * @param input
	 * @param field
	 * @param clazz
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected static <T> T getInputFieldOrNull(Map<String, Object> input, String field, Class<T> clazz) {
		Object aux = input.get(field);
		if ((aux == null) || !clazz.isAssignableFrom(aux.getClass())) {
			return null;
		}
		
		return (T)aux;
	}
	
	

	/**
	 * @return the localcontext
	 */
	protected ThreadLocal<Context> getLocalContext() {
		return localContext;
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

	protected  Context createLocalContext() {
		return new Context() {

			@Override
			public String getAwsRequestId() {
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
			public String getFunctionName() {
				return null;
			}

			@Override
			public String getFunctionVersion() {
				return null;
			}

			@Override
			public String getInvokedFunctionArn() {
				return null;
			}

			@Override
			public CognitoIdentity getIdentity() {
				return null;
			}

			@Override
			public ClientContext getClientContext() {
				return null;
			}

			@Override
			public int getRemainingTimeInMillis() {
				return 0;
			}

			@Override
			public int getMemoryLimitInMB() {
				return 0;
			}

			@Override
			public LambdaLogger getLogger() {
				return null;
			}
			
		};
	}
	
	/**
	 * @param context the context to set
	 */
	protected void setContext(Context context) {
		this.context = context;
	}
	
	/**
	 */
	protected void clearContext() {
		this.context = null;
	}

	

	/**
	 *
	 *
	 */
	protected static abstract class BackgroundThread implements Callable<String> {
		
		public synchronized Future<String> start() {
			return threadPool.submit(this);
		}
		
	}



}
