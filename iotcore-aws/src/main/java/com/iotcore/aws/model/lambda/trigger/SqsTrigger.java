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
public class SqsTrigger extends Trigger {

	private String queue;
	private String filter;
	
	/**
	 * @param name
	 * @param topic
	 */
	public SqsTrigger(String name, String queue) {
		super(TriggerType.SQSNotification, name);
		this.queue = queue;
	}

	/**
	 * @param name
	 * @param topic
	 * @param filter
	 */
	public SqsTrigger(String name, String queue, String filter) {
		super(TriggerType.SQSNotification, name);
		this.queue = queue;
		this.filter = filter;
	}
	
	
	@Override
	public String getSourceArn() {
		return AwsUtil.getArn(AwsResource.AwsSQS, queue);
	}


	/**
	 * @return the filter
	 */
	public String getFilter() {
		return filter;
	}
	
}
