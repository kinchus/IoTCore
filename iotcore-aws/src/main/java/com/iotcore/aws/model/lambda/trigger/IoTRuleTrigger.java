package com.iotcore.aws.model.lambda.trigger;

import com.iotcore.aws.util.AwsUtil;
import com.iotcore.aws.util.AwsUtil.AwsResource;

/**
 * @author <a href="mailto:garciadjx@gmail.com">J.M. Garcia</a>
 *
 */
public class IoTRuleTrigger extends Trigger {

	private String topic = null;
	private String sql = null;

	/**
	 * @param name
	 */
	public IoTRuleTrigger(String name) {
		super(TriggerType.IoTRule, name);
	}

	/**
	 * @param name
	 * @param sql
	 */
	public IoTRuleTrigger(String name, String sql) {
		super(TriggerType.IoTRule, name);
		this.setName(name);
		this.setSql(sql);
	}

	/**
	 * @param name
	 * @param topic
	 * @param sql
	 */
	public IoTRuleTrigger(String name, String topic, String sql) {
		super(TriggerType.IoTRule, name);
		this.setSql(sql);
		this.setTopic(topic);
	}

	/**
	 * @return the sourceArn
	 */
	@Override
	public String getSourceArn() {
		return AwsUtil.getArn(AwsResource.AwsIoTRULE, getName());
	}

	/**
	 * @return the sql
	 */
	public String getSql() {
		return sql;
	}

	/**
	 * @return the topic
	 */
	public String getTopic() {
		return topic;
	}

	/**
	 * @param sql the sql to set
	 */
	public void setSql(String sql) {
		this.sql = sql;
	}

	/**
	 * @param topic the topic to set
	 */
	public void setTopic(String topic) {
		this.topic = topic;
	}

}
