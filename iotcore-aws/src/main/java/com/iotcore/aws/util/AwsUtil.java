/**
 * 
 */
package com.iotcore.aws.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

import com.iotcore.aws.AwsConfig;

/**
 * The Class AwsUtil. At class initialization, set AWS_ID, REGION, and PATH, and
 * substitute them in the ARN patterns
 */
public class AwsUtil {

	/**
	 * 
	 *
	 */
	public enum AwsResource {

		/** */
		AwsIAMROLE(IAM_ROLE, true, false),
		/** */
		AwsIAMPOLICY(IAM_POLICY, true, false),
		/** */
		AwsIoTRULE(IOT_RULE),
		/** */
		AwsS3Bucket(S3_BUCKET, false, false),
		/** AwsEVENT */
		AwsEVENT(EVENT_RULE),
		/** AwsEVENT */
		AwsSNS(SNS_TOPIC),
		/** AwsEVENT */
		AwsSQS(SQS_QUEUE);


		private String pattern;
		private Boolean hasRegion = true;
		private Boolean hasAccountId = true;

		private AwsResource(String pattern) {
			this(pattern, true, true);
		}

		private AwsResource(String pattern, Boolean addAccountId) {
			this(pattern, addAccountId, true);
		}

		private AwsResource(String pattern, Boolean addAccountId, Boolean addRegion) {
			this.hasAccountId = addAccountId;
			this.hasRegion = addRegion;

			if (addRegion && addAccountId) {
				this.pattern = String.format(pattern, getRegion(), getAccountId());
			} else if (addAccountId) {
				this.pattern = String.format(pattern, getAccountId());
			} else if (addRegion) {
				this.pattern = String.format(pattern, getRegion());
			}
			this.pattern += "%s";
		}

		/**
		 * @return the role
		 */
		public String getPattern() {
			return pattern;
		}

		/**
		 * @return the addAccountId
		 */
		public Boolean hasAccountId() {
			return hasAccountId;
		}

		/**
		 * @return the addRegion
		 */
		public Boolean hasRegion() {
			return hasRegion;
		}

	}

	private static final String DATE_FMT = "yyyy-MM-dd'T'HH:mm:ss'Z'";
	/** IAM_ROLE */
	public static final String IAM_ROLE = "arn:aws:iam::%s:role/";
	/** IAM_POLICY */
	public static final String IAM_POLICY = "arn:aws:iam::%s:policy/";
	/** IOT_RULE */
	public static final String IOT_RULE = "arn:aws:iot:%s:%s:rule/";
	/** S3_BUCKET */
	public final static String S3_BUCKET = "arn:aws:s3:::";
	/** EventsBridge Rule */
	public final static String EVENT_RULE = "arn:aws:events:%s:%s:rule/";
	/** SNS Topic */
	public final static String SNS_TOPIC = "arn:aws:sns:%s:%s:";
	/** SQS Queue */
	public final static String SQS_QUEUE = "arn:aws:sqs:%s:%s:";

	private static DateFormat dateFormat = null;

	private static String bucketName = null;

	

	/**
	 * Gets the arn of some feature
	 *
	 * @param feature    the feature
	 * @param objectName the object name
	 * @return the arn
	 */
	public static String getArn(AwsResource feature, String objectName) {
		return String.format(feature.getPattern(), objectName);
	}

	/**
	 * @return
	 */
	public static String getBucketName() {
		if (bucketName == null) {
			bucketName = getPlatformName().toLowerCase() + "." + getRegion();
		}
		return bucketName;
	}

	/**
	 * @return
	 */
	public static DateFormat getDateFormat() {
		if (dateFormat == null) {
			dateFormat = new SimpleDateFormat(DATE_FMT);
			dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
		}
		return dateFormat;
	}

	/**
	 * @return
	 */
	public static String getPlatformName() {
		return AwsConfig.getInstance().getPlatformName();
	}

	/**
	 * @return
	 */
	public static String getRegion() {
		return AwsConfig.getInstance().getRegion();
	}
	
	/**
	 * @return
	 */
	public static String getAccountId() {
		return AwsConfig.getInstance().getAccountId();
	}

	/**


	/**
	 * @param input
	 * @return
	 */
	public static String replaceVariables(String input) {
		if (input != null) {
			return input.replaceAll("%PlatformName%", getPlatformName());
		}
		return input;
	}

}
