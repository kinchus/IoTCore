/**
 * 
 */
package com.iotcore.aws.model.lambda;

import com.iotcore.aws.model.AwsEntity;

/**
 * The Class Action.
 */
public class Action {

	/**
	 * The Enum ActionType.
	 */
	public enum ActionType {
		/**
		 * 
		 */
		AWSLambda_InvokeFunction
	}

	/**
	 * From invoke lambda function.
	 *
	 * @param function the function
	 * @return the action
	 */
	public static Action fromInvokeLambdaFunction(AwsEntity function) {
		return new Action(ActionType.AWSLambda_InvokeFunction, function.getName(), function.getArn());
	}

	private final ActionType type;
	private String name;

	private String targetArn;

	/**
	 * Instantiates a new action.
	 *
	 * @param type      the type
	 * @param targetArn the target arn
	 */
	public Action(ActionType type, String targetArn) {
		this.type = type;
		this.targetArn = targetArn;
	}

	/**
	 * Instantiates a new action.
	 *
	 * @param type      the type
	 * @param name      the name
	 * @param targetArn the target arn
	 */
	public Action(ActionType type, String name, String targetArn) {
		this.type = type;
		this.name = name;
		this.targetArn = targetArn;
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
	 * Gets the target arn.
	 *
	 * @return the target arn
	 */
	public String getTargetArn() {
		return targetArn;
	}

	/**
	 * Gets the type.
	 *
	 * @return the type
	 */
	public ActionType getType() {
		return type;
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
	 * Sets the target arn.
	 *
	 * @param targetArn the new target arn
	 */
	public void setTargetArn(String targetArn) {
		this.targetArn = targetArn;
	}

}
