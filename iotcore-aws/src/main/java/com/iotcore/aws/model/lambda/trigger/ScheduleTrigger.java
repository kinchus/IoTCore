package com.iotcore.aws.model.lambda.trigger;

/**
 * 
 * @author <a href="mailto:garciadjx@gmail.com">J.M. Garcia</a>
 */
public class ScheduleTrigger extends EventRuleTrigger {

	/**
	 * Constructor
	 * 
	 * @param name
	 */
	public ScheduleTrigger(String name) {
		super(name);
		setBusName(BUSNAME_DEFAULT);
	}

	/**
	 * Constructor
	 * 
	 * @param name
	 * @param scheduleExpression
	 */
	public ScheduleTrigger(String name, String scheduleExpression) {
		super(name, scheduleExpression);
		setBusName(BUSNAME_DEFAULT);
	}

}
