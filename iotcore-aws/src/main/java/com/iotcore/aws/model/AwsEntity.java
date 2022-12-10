package com.iotcore.aws.model;

import java.util.HashMap;
import java.util.Map;

/**
 * The Class Aws.
 */
public abstract class AwsEntity {

	protected String name;
	protected String description;
	protected String arn;
	private Map<String, String> tags = new HashMap<String, String>();

	/**
	 * Instantiates a new aws entity.
	 */
	public AwsEntity() {
		super();
	}

	/**
	 * @param tag
	 * @param value
	 */
	public void addTag(String tag, String value) {
		tags.put(tag, value);
	}

	/**
	 * Gets the arn.
	 *
	 * @return the arn
	 */
	public String getArn() {
		return arn;
	}

	/**
	 * Gets the description.
	 *
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Gets the tags.
	 *
	 * @return the tags
	 */
	public Map<String, String> getTags() {
		return tags;
	}

	/**
	 * Sets the arn.
	 *
	 * @param arn the new arn
	 */
	public void setArn(String arn) {
		this.arn = arn;
	}

	/**
	 * Sets the description.
	 *
	 * @param description the new description
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Sets the name.
	 *
	 * @param name the new name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Sets the tags.
	 *
	 * @param tags the tags
	 */
	public void setTags(Map<String, String> tags) {
		this.tags = tags;
	}

	/**
	 * @return
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		if (arn != null) {
			return arn;
		} else {
			return name;
		}
	}

	/**
	 * With arn.
	 *
	 * @param arn the arn
	 * @return the aws entity
	 */
	public AwsEntity withArn(String arn) {
		this.arn = arn;
		return this;
	}

	/**
	 * With description.
	 *
	 * @param description the description
	 * @return the aws entity
	 */
	public AwsEntity withDescription(String description) {
		this.description = description;
		return this;
	}

	/**
	 * With name.
	 *
	 * @param name the name
	 * @return the aws entity
	 */
	public AwsEntity withName(String name) {
		this.name = name;
		return this;
	}

}