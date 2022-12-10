/**
 * 
 */
package com.iotcore.aws.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iotcore.aws.AwsConfig;
import com.iotcore.aws.exception.AwsException;
import com.iotcore.aws.model.iot.Thing;
import com.iotcore.aws.model.lambda.trigger.IoTRuleTrigger;
import com.iotcore.core.model.exception.ObjectExistsException;
import com.iotcore.core.model.exception.ObjectNotFoundException;
import com.iotcore.core.model.exception.ServiceException;

import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.iot.IotClient;
import software.amazon.awssdk.services.iot.IotClientBuilder;
import software.amazon.awssdk.services.iot.model.Action;
import software.amazon.awssdk.services.iot.model.AddThingToThingGroupRequest;
import software.amazon.awssdk.services.iot.model.AttachPolicyRequest;
import software.amazon.awssdk.services.iot.model.AttachThingPrincipalRequest;
import software.amazon.awssdk.services.iot.model.AttributePayload;
import software.amazon.awssdk.services.iot.model.CertificateStatus;
import software.amazon.awssdk.services.iot.model.CreateKeysAndCertificateRequest;
import software.amazon.awssdk.services.iot.model.CreateKeysAndCertificateResponse;
import software.amazon.awssdk.services.iot.model.CreateThingGroupRequest;
import software.amazon.awssdk.services.iot.model.CreateThingGroupResponse;
import software.amazon.awssdk.services.iot.model.CreateThingRequest;
import software.amazon.awssdk.services.iot.model.CreateThingResponse;
import software.amazon.awssdk.services.iot.model.CreateThingTypeRequest;
import software.amazon.awssdk.services.iot.model.CreateThingTypeResponse;
import software.amazon.awssdk.services.iot.model.CreateTopicRuleDestinationRequest;
import software.amazon.awssdk.services.iot.model.CreateTopicRuleRequest;
import software.amazon.awssdk.services.iot.model.CreateTopicRuleResponse;
import software.amazon.awssdk.services.iot.model.DeleteCertificateRequest;
import software.amazon.awssdk.services.iot.model.DeleteThingGroupRequest;
import software.amazon.awssdk.services.iot.model.DeleteThingGroupResponse;
import software.amazon.awssdk.services.iot.model.DeleteThingRequest;
import software.amazon.awssdk.services.iot.model.DeleteThingResponse;
import software.amazon.awssdk.services.iot.model.DeleteThingTypeRequest;
import software.amazon.awssdk.services.iot.model.DeleteThingTypeResponse;
import software.amazon.awssdk.services.iot.model.DeleteTopicRuleDestinationRequest;
import software.amazon.awssdk.services.iot.model.DeleteTopicRuleRequest;
import software.amazon.awssdk.services.iot.model.DeleteTopicRuleResponse;
import software.amazon.awssdk.services.iot.model.DescribeThingRequest;
import software.amazon.awssdk.services.iot.model.DescribeThingResponse;
import software.amazon.awssdk.services.iot.model.DetachPolicyRequest;
import software.amazon.awssdk.services.iot.model.DetachThingPrincipalRequest;
import software.amazon.awssdk.services.iot.model.GetTopicRuleRequest;
import software.amazon.awssdk.services.iot.model.GetTopicRuleResponse;
import software.amazon.awssdk.services.iot.model.KeyPair;
import software.amazon.awssdk.services.iot.model.ListThingPrincipalsRequest;
import software.amazon.awssdk.services.iot.model.ListThingPrincipalsResponse;
import software.amazon.awssdk.services.iot.model.ListThingTypesRequest;
import software.amazon.awssdk.services.iot.model.ListThingTypesResponse;
import software.amazon.awssdk.services.iot.model.ListTopicRulesRequest;
import software.amazon.awssdk.services.iot.model.ListTopicRulesResponse;
import software.amazon.awssdk.services.iot.model.ResourceAlreadyExistsException;
import software.amazon.awssdk.services.iot.model.ResourceNotFoundException;
import software.amazon.awssdk.services.iot.model.ThingTypeDefinition;
import software.amazon.awssdk.services.iot.model.TopicRule;
import software.amazon.awssdk.services.iot.model.TopicRuleDestinationConfiguration;
import software.amazon.awssdk.services.iot.model.TopicRuleListItem;
import software.amazon.awssdk.services.iot.model.TopicRulePayload;
import software.amazon.awssdk.services.iot.model.UpdateCertificateRequest;
import software.amazon.awssdk.services.iot.model.UpdateThingRequest;
import software.amazon.awssdk.services.iot.model.UpdateThingResponse;

/**
 * @author <a href="mailto:garciadjx@gmail.com">J.M. Garcia</a>
 *
 */
@Named
@SessionScoped
public class AwsIoTService extends AwsBase<IotClient> {

	private static final long serialVersionUID = -4801862517096831734L;
	private static final Logger LOG = LoggerFactory.getLogger(AwsIoTService.class);

	private static final String IOT_POLICY = AwsConfig.getInstance().getPlatformName() + "-MQTTDevicePolicy";

	/** */
	protected static final String IOTRULE_ACTION_DYNAMODB_INS = "DynamoDB:insert:";
	/** */
	protected static final String IOTRULE_ACTION_LAMBDA_CALL = "Lambda:";

	@Override
	public IotClient create() {
		
		// final String endpoint = getConfig().getProperty(AwsConfig.AWS_IoT_ENDPOINT);
		
		IotClientBuilder builder = IotClient.builder()
		//		.endpointOverride(URI.create(endpoint))
				.region(Region.of(getConfig().getRegion()));
		
		if (!getConfig().isLambdaFunction()) {
			builder.credentialsProvider(getCredentialsProvider());
		}
		
		return builder.build();
	}
	
	
	/**
	 * 
	 */
	public AwsIoTService() {
		super();
	}
	


	/**
	 * 
	 *
	 * @param name
	 * @param sql
	 * @param actions
	 * @throws AwsException
	 * @throws AwsResourceAlreadyExistsException
	 */
	public void createIoTRule(String name, String sql, List<Action> actions)
			throws ObjectExistsException, ServiceException {

		final List<IoTRuleTrigger> rules = listIoTRules(null);
		for (final IoTRuleTrigger r : rules) {
			if (r.getName().equals(name)) {
				LOG.trace("Rule \"{}\" exist. Deleting it.", name);
				
				
				deleteIoTRule(name);
			}
		}

		final TopicRulePayload payload = TopicRulePayload.builder()
				.sql(sql)
				.description("")
				.actions(actions)
				.build();

		final CreateTopicRuleRequest crRuleReq = CreateTopicRuleRequest.builder()
				.ruleName(name)
				.topicRulePayload(payload)
				.build();

		if (LOG.isTraceEnabled()) {
			final StringBuffer detStr = new StringBuffer();
			detStr.append(String.format("\n\tName:		%s", crRuleReq.ruleName()));
			detStr.append(String.format("\n\tRule filter:	%s", crRuleReq.topicRulePayload().sql()));
			if (crRuleReq.topicRulePayload().actions() != null) {
				for (final Action act : crRuleReq.topicRulePayload().actions()) {
					if (act.lambda() != null) {
						detStr.append(String.format("\n\tRule Action:	%s", act.lambda().toString()));
					}
				}
			}
			LOG.trace("IoT Rule creation request:{}", detStr.toString());
		}

		CreateTopicRuleResponse res = null;
		try {
			res = client().createTopicRule(crRuleReq);
		} catch (final ResourceAlreadyExistsException e) {
			throw new ObjectExistsException(name);
		} catch (final AwsServiceException e) {
			throw new ServiceException("createIoTRule", e);
		}

		if (!responseOk(res)) {
			LOG.error("IoT rule was not registered");
		}

	}

	/**
	 * 
	 *
	 * @param thingGroup
	 * @param thingName
	 * @param thingType
	 * @param attributes
	 * @return
	 * @throws AwsResourceAlreadyExistsException
	 * @throws AwsException
	 */
	public Thing createIotThing(final String thingGroup, final String thingName, final String thingType,
			final Map<String, String> attributes) throws ObjectExistsException, ServiceException {

		// 0. Set thing attributes
		final AttributePayload attributePayload = AttributePayload.builder()
				.attributes(attributes)
				.build();
		

		// 1. Request for Thing creation
		CreateThingResponse ctRes = null;
		try {
			final CreateThingRequest ctReq = CreateThingRequest.builder()
					.thingName(thingName)
					.attributePayload(attributePayload)
					.build();
			
			ctRes = client().createThing(ctReq);
		} catch (final ResourceAlreadyExistsException e) {
			throw new ObjectExistsException(thingName);
		} catch (final AwsServiceException e) {
			throw new ServiceException("CreateThing", e);
		}

		if (!responseOk(ctRes)) {
			LOG.error("Couldn't complete CreateThingRequest for {}", ctRes.thingName());
			return null;
		}

		// 2. Return the created thing
		final Thing ret = new Thing();
		ret.setId(ctRes.thingId());
		ret.setName(ctRes.thingName());
		ret.setArn(ctRes.thingArn());

		if (thingGroup == null) {
			return ret;
		}

		LOG.debug("Adding thing to group {}", thingGroup);
		try {
			final AddThingToThingGroupRequest addGrReq = AddThingToThingGroupRequest.builder()
					.thingName(thingName)
					.thingGroupName(thingGroup)
					.build();
			
			client().addThingToThingGroup(addGrReq);
		} catch (final AwsServiceException e) {
			throw new ServiceException("AddThingToThingGroup", e);
		}
		return ret;
	}
	
	/**
	 * 
	 *
	 * @param thing
	 * @return
	 * @throws AwsResourceNotFoundException
	 * @throws AwsException
	 */
	public Thing updateIotThing(final Thing thing) throws ObjectNotFoundException, ServiceException {

		final String thingName = thing.getName();

		// 0. Set thing attributes
		final AttributePayload attributePayload = AttributePayload.builder()
				.attributes(thing.getAttributes())
				.build();

		// 1. Request Thing update
		UpdateThingResponse res = null;
		try {
			final UpdateThingRequest utReq = UpdateThingRequest.builder()
					.thingName(thing.getName())
					.thingTypeName(thing.getType())
					.attributePayload(attributePayload)
					.build();
						
			res = client().updateThing(utReq);
		} catch (final ResourceNotFoundException e) {
			throw new ObjectNotFoundException(thingName);
		} catch (final AwsServiceException e) {
			throw new ServiceException("GetThingShadow", e);
		}

		if (!responseOk(res)) {
			LOG.error("Couldn't update IoT Thing {}", thing.getName());
			return null;
		}

		LOG.debug("Updated IoT thing {}", thing.getName());
		return thing;
	}




	/**
	 * @param type
	 * @return
	 */
	public boolean createIotThingType(String type) {
		final CreateThingTypeRequest req = CreateThingTypeRequest.builder()
				.thingTypeName(type)
				.build();
		
		final CreateThingTypeResponse res = client().createThingType(req);
		return responseOk(res);
	}

	/**
	 * 
	 *
	 * @param thing
	 * @return
	 * @throws AwsResourceAlreadyExistsException
	 * @throws AwsException
	 */
	public Thing createThingCertificate(final Thing thing) throws ObjectExistsException, ServiceException {

		LOG.trace("Creating Keys and Certificate for {}", thing.getName());
		CreateKeysAndCertificateResponse ckcRes = null;
		try {
			final CreateKeysAndCertificateRequest createReq = CreateKeysAndCertificateRequest.builder()
					.setAsActive(true)
					.build();
					
			ckcRes = client().createKeysAndCertificate(createReq);
			if (!responseOk(ckcRes)) {
				LOG.error("Couldn't complete CreateKeysAndCertificate request");
				return null;
			}
		} catch (final ResourceAlreadyExistsException e) {
			throw new ObjectExistsException(thing.getName());
		} catch (final AwsServiceException e) {
			throw new ServiceException("CreateKeysAndCertificate", e);
		}

		final String certId = ckcRes.certificateId();
		final String certPem = ckcRes.certificatePem();
		final String certificateArn = ckcRes.certificateArn();

		final KeyPair keyPem = ckcRes.keyPair();
		final String privKeyPem = keyPem.privateKey();
		final String pubKeyPem = keyPem.publicKey();

		thing.setCertificateId(certId);
		thing.setCertificateArn(certificateArn);
		thing.setCertificatePem(certPem);
		thing.setPrivateKeyPem(privKeyPem);
		thing.setPublicKeyPem(pubKeyPem);

		LOG.trace("Attaching policy {} to certificate", IOT_POLICY);
		try {
			final AttachPolicyRequest apReq = AttachPolicyRequest.builder()
					.target(certificateArn)
					.policyName(IOT_POLICY)
					.build();
			
			if (!responseOk(client().attachPolicy(apReq))) {
				LOG.error("Couldn't attach policy for [certificateArn={}]", IOT_POLICY, certificateArn);
				return null;
			}
			thing.setPolicyName(IOT_POLICY);
		} catch (final AwsServiceException e) {
			throw new ServiceException("AttachPolicy", e);
		}

		LOG.trace("Attaching thing to certificate");
		try {
			final AttachThingPrincipalRequest atpReq = AttachThingPrincipalRequest.builder()
					.thingName(thing.getName())
					.principal(certificateArn)
					.build();
					
			if (!responseOk(client().attachThingPrincipal(atpReq))) {
				LOG.error("Couldn't attach principal to Thing {}", atpReq.thingName());
				return null;
			}
		} catch (final AwsServiceException e) {
			throw new ServiceException("AttachThingPrincipal", e);
		}

		return thing;
	}

	/**
	 * 
	 *
	 * @param groupName
	 * @return
	 * @throws AwsResourceAlreadyExistsException
	 * @throws AwsException
	 */
	public String createThingsGroup(String groupName) throws ObjectExistsException, ServiceException {

		try {
			final CreateThingGroupRequest req = CreateThingGroupRequest.builder()
					.thingGroupName(groupName)
					.build();
			
			final CreateThingGroupResponse res = client().createThingGroup(req);
			if (!responseOk(res)) {
				LOG.error("Couldn't create group {}", groupName);
				return null;
			}
		} catch (final ResourceAlreadyExistsException e) {
			throw new ObjectExistsException(groupName);
		} catch (final AwsServiceException e) {
			throw new ServiceException("CreateThingGroup", e);
		}

		return groupName;
	}

	/**
	 * 
	 *
	 * @param name
	 * @return
	 */
	public boolean deleteIoTRule(String name) {
		try {
			final DeleteTopicRuleRequest deleteTopicRuleRequest = DeleteTopicRuleRequest.builder()
					.ruleName(name)
					.build();
			
			final DeleteTopicRuleResponse res = client().deleteTopicRule(deleteTopicRuleRequest);
			if (responseOk(res)) {
				return true;
			}
		} catch (final AwsServiceException e) {
			return false;
		}

		return false;
	}

	/**
	 * 
	 *
	 * @param thingName
	 * @return
	 * @throws AwsResourceNotFoundException
	 * @throws AwsException
	 */
	public boolean deleteIotThing(String thingName) throws ObjectNotFoundException, ServiceException {

		final ListThingPrincipalsRequest listThingPrincipalsRequest = ListThingPrincipalsRequest.builder()
				.thingName(thingName)
				.build();
		
		// 1. Detach thing principal
		final ListThingPrincipalsResponse result = client().listThingPrincipals(listThingPrincipalsRequest);
		for (final String principal : result.principals()) {
			try {
				final DetachThingPrincipalRequest detachThingPrincipalRequest = DetachThingPrincipalRequest.builder()
						.thingName(thingName)
						.principal(principal)
						.build();
						
				client().detachThingPrincipal(detachThingPrincipalRequest);

				final DetachPolicyRequest detachPolicyRequest = DetachPolicyRequest.builder()
						.policyName(IOT_POLICY)
						.target(principal)
						.build();
						
				client().detachPolicy(detachPolicyRequest);

				final String certId = getIdFromPrincipal(principal);

				final UpdateCertificateRequest updateCertificateRequest = UpdateCertificateRequest.builder()
						.certificateId(certId)
						.newStatus(CertificateStatus.INACTIVE)
						.build();
						
				client().updateCertificate(updateCertificateRequest);

				final DeleteCertificateRequest deleteCertificateRequest = DeleteCertificateRequest.builder()
						.certificateId(certId)
						.build();
						
				client().deleteCertificate(deleteCertificateRequest);
			} catch (final AwsServiceException e) {
				throw new ServiceException(e);
			}
		}

		// 2. Delete Thing
		try {
			final DeleteThingRequest dtReq = DeleteThingRequest.builder().thingName(thingName)
					.build();
			
			final DeleteThingResponse ctRes = client().deleteThing(dtReq);
			if (!responseOk(ctRes)) {
				LOG.error("Couldn't delete IoT thing {}", thingName);
				return false;
			}
		} catch (final ResourceNotFoundException e) {
			throw new ObjectNotFoundException(thingName);
		} catch (final AwsServiceException e) {
			throw new ServiceException("GetThingShadow", e);
		}

		return true;
	}

	/**
	 * @param type
	 * @return
	 */
	public boolean deleteIotThingType(String type) {
		final DeleteThingTypeRequest req = DeleteThingTypeRequest.builder().thingTypeName(type)
				.build();
		
		final DeleteThingTypeResponse res = client().deleteThingType(req);
		return responseOk(res);
	}

	/**
	 * 
	 *
	 * @param thing
	 * @return
	 * @throws AwsResourceNotFoundException
	 * @throws AwsException
	 */
	public Thing deleteThingCertificate(final Thing thing) throws ObjectNotFoundException, ServiceException {

		final String policyName = thing.getPolicyName();
		final String certArn = thing.getCertificateArn();
		final String certId = thing.getCertificateId();
		
		LOG.debug("Delete thing certificate (ID={})",certId);
		LOG.trace("Dettaching certificate policy {}", IOT_POLICY);
		DetachPolicyRequest detachPolicyRequest = DetachPolicyRequest.builder()
				.target(thing.getCertificateArn())
				.policyName(IOT_POLICY)
				.build();
		
		client().detachPolicy(detachPolicyRequest);

		if (policyName != null) {
			LOG.trace("Dettaching certificate policy {}", policyName);
			detachPolicyRequest = DetachPolicyRequest.builder()
					.target(thing.getCertificateArn())
					.policyName(thing.getPolicyName())
					.build();
					
			client().detachPolicy(detachPolicyRequest);
		}

		if (certArn != null) {
			try {
				
				final DetachThingPrincipalRequest detachThingPrincipalRequest = DetachThingPrincipalRequest.builder()
						.principal(certArn)
						.thingName(thing.getName())
						.build();
						
				LOG.trace("Dettaching certificate object {}", thing.getName());
				client().detachThingPrincipal(detachThingPrincipalRequest);

				final UpdateCertificateRequest updCrtReq = UpdateCertificateRequest.builder()
						.certificateId(certId)
						.newStatus(CertificateStatus.INACTIVE)
						.build();
				
				LOG.trace("Deactivating certificate");
				client().updateCertificate(updCrtReq);

				final DeleteCertificateRequest deleteCertificateRequest = DeleteCertificateRequest.builder()
						.certificateId(certId)
						.forceDelete(true)
						.build();
						
				LOG.trace("Deleting certificate");
				client().deleteCertificate(deleteCertificateRequest);
				
			} catch (final ResourceNotFoundException e) {
				throw new ObjectNotFoundException(certArn);
			} catch (final AwsServiceException e) {
				throw new ServiceException("DeleteThingGroup", e);
			}
		}

		// Update thing
		thing.setCertificateArn(null);
		thing.setPrivateKeyPem(null);
		thing.setPublicKeyPem(null);

		return thing;
	}

	/**
	 * 
	 *
	 * @param groupName
	 * @throws AwsResourceNotFoundException
	 * @throws AwsException
	 */
	public void deleteThingsGroup(String groupName) throws ObjectNotFoundException, ServiceException {
		try {
			final DeleteThingGroupRequest req = DeleteThingGroupRequest.builder()
					.thingGroupName(groupName)
					.build();
					
			final DeleteThingGroupResponse res = client().deleteThingGroup(req);
			if (!responseOk(res)) {
				LOG.error("Couldn't delete group {}", groupName);
			}
		} catch (final ResourceNotFoundException e) {
			throw new ObjectNotFoundException(groupName);
		} catch (final AwsServiceException e) {
			throw new ServiceException("DeleteThingGroup", e);
		}
	}

	/**
	 * 
	 *
	 * @param certificateArn
	 * @return
	 */
	private String getIdFromPrincipal(String certificateArn) {
		String id = null;
		final int i = certificateArn.lastIndexOf(":cert/");
		if (i >= 0) {
			id = certificateArn.substring(i + 6);
		}
		return id;
	}

	/**
	 * 
	 *
	 * @param thingName
	 * @return
	 * @throws AwsResourceNotFoundException
	 * @throws AwsException
	 */
	public Thing getIotThing(String thingName) throws ObjectNotFoundException, ServiceException {
		final Thing ret = new Thing();
		final DescribeThingRequest describeThingRequest = DescribeThingRequest.builder()
				.thingName(thingName)
				.build();
		
		DescribeThingResponse dtRes = null;
		try {
			dtRes = client().describeThing(describeThingRequest);
			ret.setName(dtRes.thingName());
			ret.setId(dtRes.thingId());
			ret.setType(dtRes.thingTypeName());
			ret.setAttributes(dtRes.attributes());

			final ListThingPrincipalsRequest listPrincipalsReq = ListThingPrincipalsRequest.builder()
					.thingName(thingName)
					.build();
					
			final ListThingPrincipalsResponse lpRes = client().listThingPrincipals(listPrincipalsReq);
			for (final String arn : lpRes.principals()) {
				final String certId = getIdFromPrincipal(arn);
				if (certId != null) {
					ret.setCertificateId(certId);
					ret.setCertificateArn(arn);
				}
			}
		} catch (final ResourceNotFoundException e) {
			throw new ObjectNotFoundException(thingName);
		} catch (final AwsServiceException e) {
			throw new ServiceException("GetThingShadow", e);
		}
		return ret;
	}



	/**
	 * 
	 *
	 * @param topic
	 * @return
	 */
	public List<IoTRuleTrigger> listIoTRules(String topic) {

		final List<IoTRuleTrigger> ret = new ArrayList<IoTRuleTrigger>();
		final ListTopicRulesRequest listRulesReq = ListTopicRulesRequest.builder()
				.topic(topic)
				.build();
		
		String tkn = null;


		do {
			try {
				final ListTopicRulesResponse res = client().listTopicRules(listRulesReq);
				if (responseOk(res)) {
					for (final TopicRuleListItem rule : res.rules()) {
						final IoTRuleTrigger ruleTrigger = new IoTRuleTrigger(rule.ruleName(), null);
						ruleTrigger.setEnabled(!rule.ruleDisabled());
						ruleTrigger.setTopic(rule.topicPattern());
						ret.add(ruleTrigger);
					}
				} else {
					tkn = null;
				}
			} catch (final AwsServiceException e) {
				LOG.error("An error occured retrieving IoT Topic rules: {}", e.getMessage());
			}
		} while (tkn != null);

		return ret;
	}
	
	
	
	
	public  IoTRuleTrigger getIoTRule(String ruleName) {

		IoTRuleTrigger ret = null; 
		
		GetTopicRuleRequest req = GetTopicRuleRequest.builder()
				.ruleName(ruleName)
				.build();
		

		try {
			
			GetTopicRuleResponse res = client().getTopicRule(req);
			if (!responseOk(res)) {
				return null;
			}
			
			TopicRule rule = res.rule();
			ret = new IoTRuleTrigger(ruleName, rule.sql());
			
		} catch (final ResourceNotFoundException e) {
		} catch (final AwsServiceException e) {
			LOG.error("An error occured retrieving IoT Topic rules: {}", e.getMessage());
			e.printStackTrace();
		}
		
		
		return ret;
	}
	
	
	public boolean  addIoTRuleAction(String ruleName, Action action) throws ObjectNotFoundException {
	
		GetTopicRuleRequest req = GetTopicRuleRequest.builder()
				.ruleName(ruleName)
				.build();
		try {
			
			GetTopicRuleResponse res = client().getTopicRule(req);
			if (!responseOk(res)) {
				return false;
			}
			
			TopicRule rule = res.rule();
			
			for (Action act : rule.actions()) {
				if (act.equals(action)) {
					LOG.debug("Removing old rule action: Lambda {}", action.lambda().functionArn());
					DeleteTopicRuleDestinationRequest dlReq = DeleteTopicRuleDestinationRequest.builder()
							.arn(action.lambda().functionArn())
							.build();
					client().deleteTopicRuleDestination(dlReq);
				}
			}
			
			CreateTopicRuleDestinationRequest crReq = CreateTopicRuleDestinationRequest.builder()
					.destinationConfiguration(TopicRuleDestinationConfiguration.builder().build())
					.build();
			
			client().createTopicRuleDestination(crReq );

			
		} catch (final ResourceNotFoundException e) {
			throw new ObjectNotFoundException(ruleName);
		} catch (final AwsServiceException e) {
			LOG.error("An error occured retrieving IoT Topic rules: {}", e.getMessage());
			e.printStackTrace();
		}
		return true;
	}
		
	/**
	 * @return
	 */
	public List<String> listThingTypes() {
		final List<String> ret = new ArrayList<String>();
		final ListThingTypesResponse res = client().listThingTypes(ListThingTypesRequest.builder().build());
		for (final ThingTypeDefinition tType : res.thingTypes()) {
			ret.add(tType.thingTypeName());
		}
		return ret;
	}

	/**
	 * 
	 *
	 * @param name
	 * @param sql
	 * @param actions
	 * @throws AwsException
	 * @throws AwsResourceNotFoundException
	 */
	public void replaceIoTRule(String name, String sql, List<Action> actions)
			throws ObjectNotFoundException, ServiceException {

		final TopicRulePayload payload = TopicRulePayload.builder()
				.actions(actions)
				.sql(sql)
				.build();
				

		LOG.debug("Request for IoT topic rule creation");

		final CreateTopicRuleRequest crRuleReq = CreateTopicRuleRequest.builder()
				.ruleName(name)
				.topicRulePayload(payload)
				.build();

		if (LOG.isTraceEnabled()) {
			final StringBuffer detStr = new StringBuffer();
			detStr.append(String.format("\tName:		%s\n", name));
			detStr.append(String.format("\tDescription:	%s\n", crRuleReq.topicRulePayload()));
			detStr.append(String.format("\tRule filter:	%s\n", crRuleReq.topicRulePayload().sql()));
			for (final Action act : crRuleReq.topicRulePayload().actions()) {
				detStr.append(String.format("\tRule Action:	%s\n", act.lambda().toString()));
			}
			LOG.trace("Request details:\n{}", detStr);
		}

		try {
			final CreateTopicRuleResponse res = client().createTopicRule(crRuleReq);
			if (!responseOk(res)) {
				LOG.error("IoT rule was not registered");
			}
		} catch (final ResourceNotFoundException e) {
			throw new ObjectNotFoundException(name);
		} catch (final AwsServiceException e) {
			throw new ServiceException("CreateTopicRule", e);
		}
	}


}
