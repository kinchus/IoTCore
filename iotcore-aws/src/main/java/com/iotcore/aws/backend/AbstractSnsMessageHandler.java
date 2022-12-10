/**
 * 
 */
package com.iotcore.aws.backend;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.amazonaws.services.lambda.runtime.events.SNSEvent;
import com.amazonaws.services.lambda.runtime.events.SNSEvent.SNS;
import com.amazonaws.services.lambda.runtime.events.SNSEvent.SNSRecord;
import com.iotcore.core.model.message.IoTMessage;

/**
 * @author jmgarcia
 *
 */
public abstract class AbstractSnsMessageHandler<M extends IoTMessage> extends  AbstractMessageHandler<M, SNSEvent> {

	
	/**
	 * @param messageClass
	 */
	protected AbstractSnsMessageHandler(Class<M> messageClass) {
		super(messageClass);
	}
	
	
	/**
	 *
	 */
	@Override
	public Collection<M> getMessagesFromRequest(SNSEvent request) {
		List<M> ret = new ArrayList<M>();
		for (SNSRecord input:request.getRecords()) {
			SNS sns = input.getSNS();
			M msg = getMessageFromInput(sns.getMessage());
			if (msg!= null) {
				ret.add(msg);
			}
		}
		return ret;
	}
	

}
