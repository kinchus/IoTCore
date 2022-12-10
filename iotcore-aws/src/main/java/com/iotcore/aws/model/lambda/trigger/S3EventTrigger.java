package com.iotcore.aws.model.lambda.trigger;

import com.iotcore.aws.util.AwsUtil;

/**
 * @author <a href="mailto:garciadjx@gmail.com">J.M. Garcia</a>
 *
 */
public class S3EventTrigger extends Trigger {

	/** S3_OBJECT_CREATED */
	public static final String S3_OBJECT_CREATED = "s3:ObjectCreated:*";
	/** S3_OBJECT_REMOVED */
	public static final String S3_OBJECT_REMOVED = "s3:ObjectRemoved:*";

	/**
	 * iotcloud.backend
	 * 
	 * @param str
	 * @return
	 */
	public static String getFilterPart(String str) {
		String ret = str;
		final int indx = str.indexOf('/');
		if (indx > 0) {
			ret = str.substring(indx + 1);
		}
		return ret;
	}

	/**
	 * @param str
	 * @return
	 */
	public static String getS3BucketPart(String str) {
		String ret = str;
		final int indx = str.indexOf('/');
		if (indx > 0) {
			ret = str.substring(0, indx);
		}
		return ret;
	}

	private String s3Bucket = null;
	private String operation = null;
	private String filterPrefix = null;
	private String filterSuffix = null;

	/**
	 * @param name
	 * @param operation
	 * @param s3Bucket
	 * @param filter
	 */
	public S3EventTrigger(String name, String s3Bucket, String operation, String filter) {
		super(TriggerType.S3Event, name);
		setOperation(operation);
		setS3Bucket(s3Bucket);
		setFilter(filter);
	}

	/**
	 * @return the filterPrefix
	 */
	public String getFilterPrefix() {
		return filterPrefix;
	}

	/**
	 * @return the filterSuffix
	 */
	public String getFilterSuffix() {
		return filterSuffix;
	}

	/**
	 * @return the operation
	 */
	public String getOperation() {
		return operation;
	}

	/**
	 * @return the s3BucketArn
	 */
	public String getS3Bucket() {
		return s3Bucket;
	}

	/**
	 * @return the sourceArn
	 */
	@Override
	public String getSourceArn() {
		return AwsUtil.S3_BUCKET + getS3Bucket();
	}

	/**
	 * @param filter
	 */
	public void setFilter(String filter) {

		if ((filter == null) || (filter.isEmpty()) || filter.equals("*")) {
			setFilterPrefix(null);
			setFilterSuffix(null);
			return;
		}

		int i = filter.indexOf("*");
		if (i == -1) {
			setFilterPrefix(filter);
			setFilterSuffix(filter);
		} else if (filter.startsWith("*")) {
			setFilterPrefix(null);
			setFilterSuffix(filter.substring(1));
		} else if (filter.endsWith("*")) {
			setFilterPrefix(filter.substring(0, filter.length() - 2));
			setFilterSuffix(null);

		} else if ((i = filter.indexOf('*')) > 0 && (i == filter.lastIndexOf('*'))) {
			setFilterPrefix(filter.substring(0, i));
			setFilterSuffix(filter.substring(i + 1));
		}
	}

	/**
	 * @param filterPrefix the filterPrefix to set
	 */
	public void setFilterPrefix(String filterPrefix) {
		this.filterPrefix = filterPrefix;
	}

	/**
	 * @param filterSuffix the filterSuffix to set
	 */
	public void setFilterSuffix(String filterSuffix) {
		this.filterSuffix = filterSuffix;
	}

	/**
	 * @param operation the operation to set
	 */
	public void setOperation(String operation) {
		this.operation = operation;
	}

	/**
	 * @param s3BucketArn the s3BucketArn to set
	 */
	public void setS3Bucket(String s3BucketArn) {
		this.s3Bucket = s3BucketArn;
	}

}
