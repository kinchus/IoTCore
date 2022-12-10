/**
 * 
 */
package com.iotcore.aws.model.lambda;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.iotcore.aws.model.AwsEntity;
import com.iotcore.aws.model.lambda.trigger.Trigger;
import com.iotcore.aws.model.lambda.trigger.Trigger.TriggerType;

import software.amazon.awssdk.services.iot.model.Action;
import software.amazon.awssdk.services.iot.model.LambdaAction;

/**
 * The Class LambdaFunction.
 */
public class LambdaFunction extends AwsEntity {

	// private String applicationVersion;
	private String handler;
	private String application;
	private String version;
	private String roleName;
	private String roleArn;
	private Trigger trigger;
	private TriggerType triggerType;
	private Boolean publish;
	private Date lastModified;
	private Map<String, String> envVars = new HashMap<String, String>();
	private Collection<String> layers;

	private LambdaRuntimeConfig configuration = null;

	/**
	 * Instantiates a new lambda function.
	 */
	public LambdaFunction() {
	}

	/**
	 * Instantiates a new lambda function.
	 *
	 * @param name         the name
	 * @param description  the description
	 * @param handler      the handler
	 * @param version      the version
	 * @param role         the role
	 * @param lastModified the last modified
	 * @param arn          the arn
	 */
	public LambdaFunction(String name, String description, String handler, String version, String role,
			Date lastModified, String arn) {
		this.setName(name);
		this.setDescription(description);
		this.setHandler(handler);
		this.setVersion(version);
		this.setRoleName(role);
		this.setLastModified(lastModified);
		this.setArn(arn);
	}

	/**
	 * Instantiates a new lambda function.
	 *
	 * @param name        the name
	 * @param description the description
	 * @param handler     the handler
	 * @param role        the role
	 * @param trigger     the trigger
	 */
	public LambdaFunction(String name, String description, String handler, String role, Trigger trigger) {
		this.setName(name);
		// this.setApplicationName(application);
		this.setDescription(description);
		this.setHandler(handler);
		this.setRoleName(role);
		this.setTrigger(trigger);
	}

	/**
	 * Equals.
	 *
	 * @param other the other
	 * @return true, if successful
	 */
	public boolean equals(LambdaFunction other) {
		return this.getName().equals(other.name);
	}

	/**
	 * @return the application
	 */
	public String getApplication() {
		return application;
	}

	/**
	 * Gets the aws action.
	 *
	 * @return the aws action
	 */
	public Action getAwsAction() {
		return Action.builder()
				.lambda(LambdaAction.builder().functionArn(getArn()).build())
				.build();
	}

	/**
	 * Gets the configuration.
	 *
	 * @return the configuration
	 */
	public LambdaRuntimeConfig getConfiguration() {
		if (configuration == null) {
			configuration = LambdaRuntimeConfig.getDefault();
		}
		return configuration;
	}

	/**
	 * Gets the env vars.
	 *
	 * @return the env vars
	 */
	public Map<String, String> getEnvVars() {
		return envVars;
	}

	/**
	 * Gets the handler.
	 *
	 * @return the handler
	 */
	public String getHandler() {
		return handler;
	}

	/**
	 * Gets the last modified.
	 *
	 * @return the last modified
	 */
	public Date getLastModified() {
		return lastModified;
	}

	/**
	 * Gets the layers.
	 *
	 * @return the layers
	 */
	public Collection<String> getLayers() {
		return layers;
	}

	/**
	 * Gets the publish.
	 *
	 * @return the publish
	 */
	public Boolean getPublish() {
		return publish;
	}

	/**
	 * @return the roleArn
	 */
	public String getRoleArn() {
		return roleArn;
	}

	/**
	 * Gets the role.
	 *
	 * @return the role
	 */
	public String getRoleName() {
		if (roleName != null) {
			return roleName;
		} else {
			return getConfiguration().getRole();
		}
	}

	/**
	 * Gets the trigger.
	 *
	 * @return the trigger
	 */
	public Trigger getTrigger() {
		return trigger;
	}

	/**
	 * Gets the trigger type.
	 *
	 * @return the trigger type
	 */
	public TriggerType getTriggerType() {
		return triggerType;
	}

	/**
	 * Gets the version.
	 *
	 * @return the version
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * @param application the application to set
	 */
	public void setApplication(String application) {
		this.application = application;
	}

	/**
	 * Sets the configuration.
	 *
	 * @param configuration the new configuration
	 */
	public void setConfiguration(LambdaRuntimeConfig configuration) {
		this.configuration = configuration;
	}

	/**
	 * Sets the env vars.
	 *
	 * @param envVars the env vars
	 */
	public void setEnvVars(Map<String, String> envVars) {
		this.envVars = envVars;
	}

	/**
	 * Sets the handler.
	 *
	 * @param handler the new handler
	 */
	public void setHandler(String handler) {
		this.handler = handler;
	}

	/**
	 * Sets the last modified.
	 *
	 * @param lastModified the new last modified
	 */
	public void setLastModified(Date lastModified) {
		this.lastModified = lastModified;
	}

	/**
	 * Sets the layers.
	 *
	 * @param layers the new layers
	 */
	public void setLayers(Collection<String> layers) {
		this.layers = layers;
	}

	/**
	 * Sets the publish.
	 *
	 * @param publish the new publish
	 */
	public void setPublish(Boolean publish) {
		this.publish = publish;
	}

	/**
	 * @param roleArn the roleArn to set
	 */
	public void setRoleArn(String roleArn) {
		this.roleArn = roleArn;
	}

	/**
	 * Sets the role.
	 *
	 * @param role the new role
	 */
	public void setRoleName(String role) {
		this.roleName = role;
	}

	/**
	 * Sets the trigger.
	 *
	 * @param trigger the new trigger
	 */
	public void setTrigger(Trigger trigger) {
		this.trigger = trigger;
	}

	/**
	 * Sets the version.
	 *
	 * @param version the new version
	 */
	public void setVersion(String version) {
		this.version = version;
	}

	/**
	 * With configuration.
	 *
	 * @param configuration the configuration
	 * @return the lambda function
	 */
	public LambdaFunction withConfiguration(LambdaRuntimeConfig configuration) {
		this.configuration = configuration;
		return this;
	}

	/**
	 * With env vars.
	 *
	 * @param envVars the env vars
	 * @return the lambda function
	 */
	public LambdaFunction withEnvVars(Map<String, String> envVars) {
		this.envVars = envVars;
		return this;
	}

	/**
	 * With hander.
	 *
	 * @param handler the handler
	 * @return the lambda function
	 */
	public LambdaFunction withHander(String handler) {
		this.handler = handler;
		return this;
	}

	/**
	 * With last modified.
	 *
	 * @param lastModified the last modified
	 * @return the lambda function
	 */
	public LambdaFunction withLastModified(Date lastModified) {
		this.lastModified = lastModified;
		return this;
	}

	/**
	 * With publish.
	 *
	 * @param publish the publish
	 * @return the lambda function
	 */
	public LambdaFunction withPublish(Boolean publish) {
		this.publish = publish;
		return this;
	}

	/**
	 * With role.
	 *
	 * @param role the role
	 * @return the lambda function
	 */
	public LambdaFunction withRoleArn(String role) {
		this.roleArn = role;
		return this;
	}

	/**
	 * With role.
	 *
	 * @param role the role
	 * @return the lambda function
	 */
	public LambdaFunction withRoleName(String role) {
		this.roleName = role;
		return this;
	}

	/**
	 * With trigger.
	 *
	 * @param trigger the trigger
	 * @return the lambda function
	 */
	public LambdaFunction withTrigger(Trigger trigger) {
		this.trigger = trigger;
		return this;
	}

	/**
	 * With version.
	 *
	 * @param version the version
	 * @return the lambda function
	 */
	public LambdaFunction withVersion(String version) {
		this.version = version;
		return this;
	}

}
