package com.iotcore.aws.model.lambda.trigger;

import com.iotcore.aws.util.AwsUtil;
import com.iotcore.aws.util.AwsUtil.AwsResource;

/**
 * 
 * @author <a href="mailto:garciadjx@gmail.com">J.M. Garcia</a>
 */
public class EventRuleTrigger extends Trigger {

	/** Schedule */
	public static final String SCHEDULE = "Schedule";
	/** BUSNAME_DEFAULT */
	public static final String BUSNAME_DEFAULT = "default";

	private String busName = BUSNAME_DEFAULT;
	private String detailType;
	private String eventPattern;

	/**
	 * Constructor
	 * 
	 * @param name
	 */
	public EventRuleTrigger(String name) {
		super(TriggerType.EventRule, name);
	}

	/**
	 * Constructor
	 * 
	 * @param name
	 * @param scheduleExpression
	 */
	public EventRuleTrigger(String name, String scheduleExpression) {
		super(TriggerType.Schedule, name);
		setDetailType(SCHEDULE);
		setEventPattern(scheduleExpression);
	}

	/**
	 * Constructor
	 * 
	 * @param name
	 * @param detailType
	 * @param eventFilter
	 */
	public EventRuleTrigger(String name, String detailType, String eventFilter) {
		super(TriggerType.EventRule, name);
		setDetailType(detailType);
		setEventPattern(eventFilter);
	}

	/**
	 * Constructor
	 * 
	 * @param name
	 * @param busName
	 * @param detailType
	 * @param eventFilter
	 */
	public EventRuleTrigger(String name, String busName, String detailType, String eventFilter) {
		super(TriggerType.EventRule, name);
		setBusName(busName);
		setDetailType(detailType);
		setEventPattern(eventFilter);
	}

	/**
	 * @return the busName
	 */
	public String getBusName() {
		return busName;
	}

	/**
	 * @return the detailType
	 */
	public String getDetailType() {
		return detailType;
	}

	/**
	 * @return the eventFilter
	 */
	public String getEventPattern() {
		return eventPattern;
	}

	/**
	 * @return
	 * @see com.iotcore.aws.model.lambda.trigger.Trigger#getSourceArn()
	 */
	@Override
	public String getSourceArn() {
		String name = getName();
		if (!busName.equals(BUSNAME_DEFAULT)) {
			name = busName + "/" + name;
		}
		return AwsUtil.getArn(AwsResource.AwsEVENT, name);
	}

	/**
	 * @param busName the busName to set
	 */
	public void setBusName(String busName) {
		this.busName = busName;
	}

	/**
	 * @param detailType the detailType to set
	 */
	public void setDetailType(String detailType) {
		this.detailType = detailType;
	}

	/**
	 * @param eventFilter the eventFilter to set
	 */
	public void setEventPattern(String eventFilter) {
		this.eventPattern = eventFilter;
	}

}
