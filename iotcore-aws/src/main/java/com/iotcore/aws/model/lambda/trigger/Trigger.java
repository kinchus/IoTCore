/**
 * 
 */
package com.iotcore.aws.model.lambda.trigger;

/**
 * @author <a href="mailto:garciadjx@gmail.com">J.M. Garcia</a>
 *
 */
public abstract class Trigger {

	/**
	 * @author <a href="mailto:garciadjx@gmail.com">J.M. Garcia</a>
	 *
	 */
	public enum TriggerType {

		/** */
		NONE(null, null),
		/** */
		IoTRule(IOTRule_PREFIX, AWS_IoT_IDENTITY),
		/** */
		SNSNotification(SNSTopic_PREFIX, AWS_SNS_IDENTITY),
		/** */
		SQSNotification(SQSTopic_PREFIX, AWS_SQS_IDENTITY),
		/** */
		S3Event(S3Event_PREFIX, AWS_S3_IDENTITY),
		/** EventRule */
		EventRule(EventRule_PREFIX, AWS_EVENTS_IDENTITY),
		/** CloudWatchRule */
		Schedule(Schedule_PREFIX, AWS_EVENTS_IDENTITY);

		private String prefix;
		private String identity;

		private TriggerType(String prefix, String identity) {
			setPrefix(prefix);
			setIdentity(identity);
		}

		/**
		 * @return the identity
		 */
		public String getIdentity() {
			return identity;
		}

		/**
		 * @return the prefix
		 */
		public String getPrefix() {
			return prefix;
		}

		/**
		 * @param identity the identity to set
		 */
		private void setIdentity(String identity) {
			this.identity = identity;
		}

		/**
		 * @param prefix the prefix to set
		 */
		private void setPrefix(String prefix) {
			this.prefix = prefix;
		}
	}

	/** */
	public final static String S3Event_PREFIX = "S3ObjectCreatedEvent://";
	/** */
	public final static String IOTRule_PREFIX = "IoTRule://";
	/** */
	public final static String SNSTopic_PREFIX = "SNSTopic://";
	/** */
	public final static String SQSTopic_PREFIX = "SQSTopic://";
	/** */
	public final static String EventRule_PREFIX = "Event://";
	/** */
	public final static String Schedule_PREFIX = "ScheduleEvent://";

	/** */
	public final static String AWS_S3_IDENTITY = "s3.amazonaws.com";
	/** */
	public final static String AWS_IoT_IDENTITY = "iot.amazonaws.com";
	/** */
	public final static String AWS_SNS_IDENTITY = "sns.amazonaws.com";
	/** */
	public final static String AWS_SQS_IDENTITY = "sqs.amazonaws.com";
	/** AWS_EVENTS_IDENTITY */
	public final static String AWS_EVENTS_IDENTITY = "events.amazonaws.com";

	
	private TriggerType type;
	private String name;
	private String identity;
	private Boolean enabled;


	/**
	 * @param type
	 */
	public Trigger(TriggerType type) {
		setType(type);
		setIdentity(type.getIdentity());
	}

	/**
	 * @param type
	 * @param name
	 */
	public Trigger(TriggerType type, String name) {
		setType(type);
		setIdentity(type.getIdentity());
		setName(name);
	}

	/**
	 * @return the enabled
	 */
	public Boolean getEnabled() {
		return enabled;
	}

	/**
	 * @return the identity
	 */
	public String getIdentity() {
		return identity;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the sourceArn
	 */
	public abstract String getSourceArn();

	/**
	 * @return the type
	 */
	public TriggerType getType() {
		return type;
	}

	/**
	 * @param enabled the enabled to set
	 */
	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}

	/**
	 * @param identity the identity to set
	 */
	public void setIdentity(String identity) {
		this.identity = identity;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(TriggerType type) {
		this.type = type;
	}

}
