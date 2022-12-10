/**
 * 
 */
package com.iotcore.aws.model.lambda.trigger;

import com.iotcore.aws.util.AwsUtil;
import com.iotcore.aws.util.AwsUtil.AwsResource;

/**
 * @author jmgarcia
 *
 */
public class SnsNotificationTrigger extends Trigger {

	private String topic;
	private String filter;
	
	/**
	 * @param name
	 * @param topic
	 */
	public SnsNotificationTrigger(String name, String topic) {
		super(TriggerType.SNSNotification, name);
		this.topic = topic;
	}

	/**
	 * @param name
	 * @param topic
	 * @param filter
	 */
	public SnsNotificationTrigger(String name, String topic, String filter) {
		super(TriggerType.SNSNotification, name);
		this.topic = topic;
		this.filter = filter;
	}
	
	@Override
	public String getSourceArn() {
		return AwsUtil.getArn(AwsResource.AwsSNS, topic);
	}


	/**
	 * @return the filter
	 */
	public String getFilter() {
		return filter;
	}
	
}
