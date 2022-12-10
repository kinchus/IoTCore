/**
 * 
 */
package com.iotcore.aws.annotation;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Rule definition for triggering a handler instance. 
 * 
 * @author <a href="mailto:garciadjx@gmail.com">J.M. Garcia</a>
 */
@Documented
@Retention(RUNTIME)
@Target({ TYPE, METHOD })
public @interface AwsTrigger {
	
	
	/**
	 * Types of rules
	 */
	public enum TriggerType {
		/** NONE: No triggering. Left to implementation or direct invocation */
		None,
		/** IoTRule: Mqtt message filtering rule */
		IoTRule,
		/** EventRule: Event handler */
		EventRule,
		/** SHEDULED: Scheduled (programmed) activation */
		Schedule,
		/** S3Event: Storage*/
		S3Event
	}

	
	/**
	 * Type of this trigger 
	 * @return
	 */
	TriggerType type() default TriggerType.None;

	/**
	 * Rule name.  
	 * @return
	 */
	String name() default "";
	
	/**
	 * Target topic for this rule
	 * <ul>
	 * <li>IoTRule: IoTRule Topic</dd>
	 * <li>SNSTopic: </dd>
	 * <li>S3Event: S3 bucket action (S3ObjectCreated, S3ObjectDeleted, ect)</li>
	 * <li>EventRule: Name of the events bus</li>
	 * </ul>
	 * @return
	 */
	String topic() default "";
	
	String action() default "";
	
	/**
	 * Trigger filter<br/> 
	 * The exact meaning of the source depends on the type of trigger being defined:
	 * <ul>
	 * <li>IoTRule: SQL Filter</dd>
	 * <li>SNSTopic: </dd>
	 * <li>S3Event: S3 filter</li>
	 * <li>EventRule: Name of the events bus</li>
	 * </ul>
	 * @return The filter source
	 */
	String filter() default "";
	
	
}
