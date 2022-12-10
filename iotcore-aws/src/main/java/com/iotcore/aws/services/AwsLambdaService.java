/**
 * 
 */
package com.iotcore.aws.services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iotcore.aws.exception.AwsException;
import com.iotcore.aws.model.lambda.LambdaFunction;
import com.iotcore.aws.model.lambda.LambdaRuntimeConfig;
import com.iotcore.aws.model.lambda.trigger.Trigger;

import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.services.lambda.LambdaClient;
import software.amazon.awssdk.services.lambda.model.AddPermissionRequest;
import software.amazon.awssdk.services.lambda.model.AddPermissionResponse;
import software.amazon.awssdk.services.lambda.model.CreateEventSourceMappingRequest;
import software.amazon.awssdk.services.lambda.model.CreateEventSourceMappingResponse;
import software.amazon.awssdk.services.lambda.model.CreateFunctionRequest;
import software.amazon.awssdk.services.lambda.model.CreateFunctionResponse;
import software.amazon.awssdk.services.lambda.model.DeleteEventSourceMappingRequest;
import software.amazon.awssdk.services.lambda.model.DeleteFunctionRequest;
import software.amazon.awssdk.services.lambda.model.DeleteFunctionResponse;
import software.amazon.awssdk.services.lambda.model.Environment;
import software.amazon.awssdk.services.lambda.model.EventSourceMappingConfiguration;
import software.amazon.awssdk.services.lambda.model.Filter;
import software.amazon.awssdk.services.lambda.model.FilterCriteria;
import software.amazon.awssdk.services.lambda.model.FunctionCode;
import software.amazon.awssdk.services.lambda.model.FunctionConfiguration;
import software.amazon.awssdk.services.lambda.model.GetFunctionRequest;
import software.amazon.awssdk.services.lambda.model.GetFunctionResponse;
import software.amazon.awssdk.services.lambda.model.Layer;
import software.amazon.awssdk.services.lambda.model.LayersListItem;
import software.amazon.awssdk.services.lambda.model.ListEventSourceMappingsRequest;
import software.amazon.awssdk.services.lambda.model.ListEventSourceMappingsResponse;
import software.amazon.awssdk.services.lambda.model.ListFunctionsRequest;
import software.amazon.awssdk.services.lambda.model.ListFunctionsResponse;
import software.amazon.awssdk.services.lambda.model.ListLayersRequest;
import software.amazon.awssdk.services.lambda.model.ListLayersResponse;
import software.amazon.awssdk.services.lambda.model.ResourceNotFoundException;
import software.amazon.awssdk.services.lambda.model.UpdateFunctionCodeRequest;
import software.amazon.awssdk.services.lambda.model.UpdateFunctionCodeResponse;

/**
 * @author <a href="mailto:garciadjx@gmail.com">J.M. Garcia</a>
 *
 */
@Named
@SessionScoped
public class AwsLambdaService extends AwsBase<LambdaClient> {

	private static final long serialVersionUID = -6256940865937661071L;
	private static Logger LOG = LoggerFactory.getLogger(AwsLambdaService.class);

	private static final String ACTION_INVOKE = "lambda:InvokeFunction";
	private static final String JAVA_RT = LambdaRuntimeConfig.RUNTIME_JAVA11;
	
	
	@Override
	public LambdaClient create() {
		return LambdaClient.builder().build();
	}
	

	/**
	 * 
	 */
	public AwsLambdaService() {
		super();
	}

	
	/**
	 * Add invocation permissions to an existing Lambda function
	 * 
	 * @param functionName
	 * @param trigger
	 * @throws AwsException
	 */
	public void addLambdaInvokePermission(String functionName, Trigger trigger) throws AwsException {

		// LOG.trace("Adding invocation permission from {}", trigger.getSourceArn());
		final Long l = System.currentTimeMillis();
		
		final AddPermissionRequest req = AddPermissionRequest.builder()
				.action(ACTION_INVOKE)
				.functionName(functionName)
				.principal(trigger.getIdentity())
				.sourceAccount(getConfig().getAccountId())
				.sourceArn(trigger.getSourceArn())
				.statementId(l.toString())
				.build();
				
		try {
			
			final AddPermissionResponse res = client().addPermission(req);
			if (!responseOk(res)) {
				LOG.error("Couldn't add invokation permissions from requested source");
			}
		} catch (final AwsServiceException e) {
			throw new AwsException("AddPermission", e);
		}

	}

	/**
	 * @param function
	 * @return
	 * @throws Exception
	 */
	public LambdaFunction createFunction(final LambdaFunction function) throws Exception {
		
		
		final FunctionCode code = FunctionCode.builder()
				.s3Bucket(function.getConfiguration().getS3Bucket())
				.s3Key(function.getConfiguration().getS3Key())
				.build();
				
		final Environment env = Environment.builder()
				.variables(function.getEnvVars())
				.build();
		
		final CreateFunctionRequest crFnReq = CreateFunctionRequest.builder()
				.functionName(function.getName())
				.description(function.getDescription())
				.role(function.getRoleArn())
				.handler(function.getHandler())
				.layers(function.getLayers())
				.tags(function.getTags())
				.runtime(function.getConfiguration().getRuntime())
				.memorySize(function.getConfiguration().getMemSize())
				.timeout(function.getConfiguration().getTimeout())
				.publish(function.getConfiguration().getPublish())
				.environment(env)
				.code(code)
				.build();

//		if (LOG.isTraceEnabled()) {
//			final StringBuffer detStr = new StringBuffer();
//			detStr.append(String.format("\n\tName:    %s", crFnReq.functionName()));
//			detStr.append(String.format("\n\tHandler: %s", crFnReq.handler()));
//			detStr.append(String.format("\n\tRole:    %s", crFnReq.role()));
//			detStr.append(String.format("\n\tLayers:  %s", crFnReq.layers()));
//			for (final String envVar : crFnReq.environment().variables().keySet()) {
//				detStr.append(String.format("\n\tEnvironment.%s:\t%s", envVar,
//						crFnReq.environment().variables().get(envVar)));
//			}
//			LOG.trace("CreateFunctionRequest:{}", detStr);
//		}
		final CreateFunctionResponse res = client().createFunction(crFnReq);

		if (!responseOk(res)) {
			LOG.error("Unable to create function (Status = {})", res.sdkHttpResponse().statusCode());
			return null;
		}

		LOG.debug("Function \"{}\" was created successfully", function.getName());

		function.setVersion(res.version());
		function.setLastModified(parseISODate(res.lastModified()));
		function.setArn(res.functionArn());

		return function;
	}
	
	
	/**
	 * @param sourceArn
	 * @param functionArn
	 * @param filterExprList
	 * @return
	 */
	public String createMapping(String sourceArn, String functionArn, List<String> filterExprList) {
		
		final List<Filter> filters = new ArrayList<Filter>(); 
		
		if ((filterExprList != null) && ! filterExprList.isEmpty()) {
			filterExprList.forEach(expr -> filters.add(Filter.builder().pattern(expr).build()));
		}
				
		FilterCriteria filterCrit = FilterCriteria.builder()
				.filters(filters)
				.build();
		
		CreateEventSourceMappingRequest req = CreateEventSourceMappingRequest.builder()
				.eventSourceArn(sourceArn)
				.batchSize(10)
				.functionName(functionArn)
				.filterCriteria(filterCrit)
				.build();
		
		CreateEventSourceMappingResponse res = client().createEventSourceMapping(req);
		return res.uuid();
	}


	/**
	 * @param function
	 * @throws Exception
	 */
	public void deleteFunction(final LambdaFunction function) throws Exception {

		final ListEventSourceMappingsRequest listEvReq = ListEventSourceMappingsRequest.builder()
				.functionName(function.getName())
				.build();
		
		final List<EventSourceMappingConfiguration> events = new ArrayList<EventSourceMappingConfiguration>();
		final ListEventSourceMappingsResponse listEvRes = client().listEventSourceMappings(listEvReq);
		events.addAll(listEvRes.eventSourceMappings());
		
		for (final EventSourceMappingConfiguration evt : events) {
			LOG.trace("Delete \"{}\" source event {}", function.getName(), evt.eventSourceArn());
			final DeleteEventSourceMappingRequest delEvReq = DeleteEventSourceMappingRequest.builder()
					.uuid(evt.uuid())
					.build();
			
			client().deleteEventSourceMapping(delEvReq);
		}

		
		final DeleteFunctionRequest delFuncReq = DeleteFunctionRequest.builder()
				.functionName(function.getName())
				.build();

		final DeleteFunctionResponse res = client().deleteFunction(delFuncReq);

		if (!responseOk(res)) {
			LOG.error("Unable to delete function. Status code is {}", res.sdkHttpResponse().statusCode());
			throw new Exception("Unable to delete function " + function.getName());
		}
		
		LOG.trace("Function \"{}\" was removed from system", function.getName());

	}

	/**
	 * @param functionName
	 * @return
	 */
	public boolean deleteFunction(final String functionName) {

		final ListEventSourceMappingsRequest listEvReq = ListEventSourceMappingsRequest.builder()
				.functionName(functionName)
				.build();
		
		final List<EventSourceMappingConfiguration> events = new ArrayList<EventSourceMappingConfiguration>();
		String pgTkn = null;
		do {
			final ListEventSourceMappingsResponse listEvRes = client()
					.listEventSourceMappings(listEvReq);
			
			pgTkn = listEvRes.nextMarker();
			events.addAll(listEvRes.eventSourceMappings());
		} while ((pgTkn != null));

		for (final EventSourceMappingConfiguration evt : events) {
			LOG.trace("Delete \"{}\" source event {}", functionName, evt.eventSourceArn());
			final DeleteEventSourceMappingRequest delEvReq = DeleteEventSourceMappingRequest.builder()
					.uuid(evt.uuid())
					.build();
			
			client().deleteEventSourceMapping(delEvReq);
		}

		LOG.trace("Delete \"{}\"", functionName);

		final DeleteFunctionRequest delFuncReq =  DeleteFunctionRequest.builder()
				.functionName(functionName)
				.build();

		final DeleteFunctionResponse res = client().deleteFunction(delFuncReq);

		if (!responseOk(res)) {
			LOG.error("Unable to delete function. Status code is {}", res.sdkHttpResponse().statusCode());
			return false;
		}
		return true;
	}


	/**
	 * Retrieve the lambda function with the given name or null if there is not a
	 * matching function
	 * 
	 * @param name
	 * @return
	 */
	public LambdaFunction getFunction(final String name) {
		LambdaFunction ret = null;
		try {
			final GetFunctionRequest getFReq = GetFunctionRequest.builder()
					.functionName(name)
					.build();
			
			final GetFunctionResponse res = client().getFunction(getFReq);
			if (responseOk(res)) {
				ret = new LambdaFunction();
				ret.setName(res.configuration().functionName());
				ret.setArn(res.configuration().functionArn());
				ret.setRoleName(res.configuration().role());
				ret.setDescription(res.configuration().description());
				ret.setHandler(res.configuration().handler());
				ret.setVersion(res.configuration().version());
				ret.setTags(res.tags());
			}
		} catch (final ResourceNotFoundException e) {
		}
		return ret;
	}

	/**
	 * @return
	 */
	public List<LambdaFunction> getFunctions() {
		ListFunctionsResponse res = null;
		final ListFunctionsRequest listFunctionsRequest = ListFunctionsRequest.builder().build();

		try {
			res = client().listFunctions(listFunctionsRequest);
		} catch (final Exception e) {
			LOG.error("Unable to request existing Lambda functions: {}", e.getMessage());
			e.printStackTrace();
			return null;
		}

		final List<LambdaFunction> ret = new ArrayList<LambdaFunction>();
		for (final FunctionConfiguration fc : res.functions()) {
			final LambdaFunction function = new LambdaFunction(
					fc.functionName(), 
					fc.description(),
					fc.handler(), 
					fc.version(), 
					fc.role(), 
					parseISODate(fc.lastModified()),
					fc.functionArn());
			if (fc.layers() != null) {
				final List<String> layers = new ArrayList<String>();
				for (final Layer l : fc.layers()) {
					layers.add(l.arn());
				}
				function.setLayers(layers);
			}

			ret.add(function);
			LOG.trace("Found AWS Lambda function: {}", function.getName());
		}

		return ret;
	}

	/**
	 * @param names
	 * @return
	 */
	public List<LambdaFunction> getFunctions(Collection<String> names) {
		List<LambdaFunction> list = null;
		try {
			final ListFunctionsRequest getFReq = ListFunctionsRequest.builder().build();
			
			final ListFunctionsResponse res = client().listFunctions(getFReq);
			
			if (responseOk(res)) {
				list = new ArrayList<LambdaFunction>();
				for (final FunctionConfiguration fc : res.functions()) {
					if (!names.contains(fc.functionName())) {
						continue;
					}
					final LambdaFunction func = new LambdaFunction();
					func.setName(fc.functionName());
					func.setArn(fc.functionArn());
					func.setRoleName(fc.role());
					func.setDescription(fc.description());
					func.setHandler(fc.handler());
					func.setVersion(fc.version());
					list.add(func);
				}
			}
		} catch (final Exception e) {
			LOG.warn("Error occured while checking if function already exists: {}", e.getMessage());
		}
		return list;
	}

	/**
	 * @return
	 */
	public Map<String, String> getLayers() {
		return getLayers(null);
	}

	/**
	 * @param prefix
	 * @return
	 */
	public Map<String, String> getLayers(String prefix) {
		Map<String, String> ret = null;
		final ListLayersRequest req = ListLayersRequest.builder()
				.compatibleRuntime(JAVA_RT)
				.build();
		
		final ListLayersResponse res = client().listLayers(req);
		if (responseOk(res)) {
			ret = new HashMap<String, String>();
			for (final LayersListItem lItem : res.layers()) {
				final String lName = lItem.layerName();
				final String lArn = lItem.latestMatchingVersion().layerVersionArn();
				if ((prefix != null) && lName.startsWith(prefix)) {
					ret.put(lName, lArn);
				}
			}
		}

		return ret;
	}

	

	/**
	 * @param function
	 * @return
	 * @throws Exception
	 */
	public LambdaFunction updateFunction(final LambdaFunction function) throws Exception {
		final FunctionCode code = FunctionCode.builder()
				.s3Bucket(function.getConfiguration().getS3Bucket())
				.s3Key(function.getConfiguration().getS3Key())
				.s3ObjectVersion(function.getConfiguration().getS3ObjectVersion())
				.build();

		final UpdateFunctionCodeRequest updateFunctionRequest = UpdateFunctionCodeRequest.builder()
				.functionName(function.getName())
				.publish(function.getConfiguration().getPublish())
				.s3Bucket(code.s3Bucket())
				.s3Key(code.s3Key())
				.build();

		final UpdateFunctionCodeResponse res = client().updateFunctionCode(updateFunctionRequest);
		if (!responseOk(res)) {
			LOG.error("Couldn't create function");
			return null;
		}

		LOG.trace("Function \"{}\" was updated successfully", function.getName());
		function.setVersion(res.version());
		function.setLastModified(parseISODate(res.lastModified()));
		function.setArn(res.functionArn());
		return function;
	}

}
