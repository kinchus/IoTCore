/**
 * 
 */
package com.iotcore.aws.services;

import java.util.HashMap;
import java.util.Map;

import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

import com.iotcore.aws.util.AwsUtil;
import com.iotcore.aws.util.AwsUtil.AwsResource;

import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.CreateTopicRequest;
import software.amazon.awssdk.services.sns.model.CreateTopicResponse;
import software.amazon.awssdk.services.sns.model.MessageAttributeValue;
import software.amazon.awssdk.services.sns.model.PublishRequest;
import software.amazon.awssdk.services.sns.model.PublishResponse;
import software.amazon.awssdk.services.sns.model.SetSubscriptionAttributesRequest;
import software.amazon.awssdk.services.sns.model.SubscribeRequest;
import software.amazon.awssdk.services.sns.model.SubscribeResponse;

/**
 * @author jmgarcia
 *
 */
@Named
@SessionScoped
public class AwsSnsService extends AwsBase<SnsClient> {
	private static final long serialVersionUID = 0L;
	
	private static org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(AwsSnsService.class);
	
	public static final String PROTOCOL_LAMBDA = "lambda";
	public static final String PROTOCOL_SMS = "sms";
	
	
	public static final String TOPIC_FILTER = "FilterPolicy";
	

	/**
	 * @param topic
	 * @return
	 */
	public static String getTopicArn(String topic) {
		return AwsUtil.getArn(AwsResource.AwsSNS, topic);
	}
	
	@Override
	public SnsClient create() {
		return SnsClient.builder().build();
	}
	
	
	/**
	 * @param topic
	 * @return
	 */
	public String createTopic(String topic) {
		
		CreateTopicRequest req = CreateTopicRequest.builder()
				.name(topic)
				.build();
		
		CreateTopicResponse res = client().createTopic(req);
		
		if (LOG.isTraceEnabled())
			LOG.trace("Topic created successfully with ARN = {}", res.topicArn());
		
		return res.topicArn();
	}
	
	
	/**
	 * @param topicArn
	 * @param lambdaArn
	 * @return
	 */
	public String subscribeToTopic(String topicArn, String lambdaArn) {
		
		SubscribeRequest req = SubscribeRequest.builder()
				.topicArn(topicArn)
				.protocol(PROTOCOL_LAMBDA)
				.endpoint(lambdaArn)
				.returnSubscriptionArn(true)
				.build();
		
		SubscribeResponse res = client().subscribe(req);
	
		if (LOG.isTraceEnabled())
			LOG.trace("Subscription created successfully with ARN = {}", res.subscriptionArn());
		
		return res.subscriptionArn();
	}
	
	/**
	 * @param topicArn
	 * @param lambdaArn
	 * @return
	 */
	public String subscribeToTopic(String topicArn, String lambdaArn, String filterExpr) {
		
		SubscribeRequest req = SubscribeRequest.builder()
				.topicArn(topicArn)
				.protocol(PROTOCOL_LAMBDA)
				.endpoint(lambdaArn)
				.returnSubscriptionArn(true)
				.build();
		
		
		String arn = null;
		if (LOG.isDebugEnabled())
			LOG.debug("Creating subscription for topic {}", topicArn);
		
		try {
			SubscribeResponse res = client().subscribe(req);
			arn = res.subscriptionArn();
		}
		catch (AwsServiceException e) {
			LOG.error("Exception thrown: {}", e.getMessage());
			return null;
		}
		
		if (LOG.isDebugEnabled())
			LOG.debug("Applying subscription filter: {}", filterExpr);
		
		SetSubscriptionAttributesRequest setAttrReq = SetSubscriptionAttributesRequest.builder()
				.attributeName(TOPIC_FILTER)
				.attributeValue(filterExpr)
				.subscriptionArn(arn)
				.build();
		
		try {
			client().setSubscriptionAttributes(setAttrReq);
		}
		catch (AwsServiceException e) {
			LOG.error("Exception thrown: {}", e.getMessage());
			return null;
		}
		
		return arn;
	}
	
	
	/**
	 * @param topicArn
	 * @param message
	 * @return
	 */
	public String  publish(String topicArn, String message) {
		
		
		PublishRequest req = PublishRequest.builder()
				.message(message)
				.topicArn(topicArn)
				.build();
		
		try {
			PublishResponse res = client().publish(req);
			return res.messageId();
		}
		catch (AwsServiceException e) {
			LOG.error("Exception thrown: {}", e.getMessage());
			e.printStackTrace();
			return null;
		}
		
	}
	
	public String  publish(String topicArn, String message, Map<String, Object> attributes) {
		
		
		final Map<String, MessageAttributeValue> msgAttrs = new HashMap<String, MessageAttributeValue>();
		for (String attrName:attributes.keySet()) {
			String val = attributes.get(attrName).toString();
			msgAttrs.put(attrName, MessageAttributeValue.builder()
					.dataType("String")
					.stringValue(val)
					.build());
		}
		
		
		PublishRequest req = PublishRequest.builder()
				.message(message)
				.topicArn(topicArn)
				.messageAttributes(msgAttrs)
				.build();
		
		try {
			PublishResponse res = client().publish(req);
			return res.messageId();
		}
		catch (AwsServiceException e) {
			LOG.error("Exception thrown: {}", e.getMessage());
			e.printStackTrace();
			return null;
		}
		
	}
	

}
