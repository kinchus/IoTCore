package com.iotcore.aws.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.iotcore.aws.annotation.AwsTrigger.TriggerType;

/**
 *  Definition of an IoTCloud Application handler
 *  
 * @author <a href="mailto:garciadjx@gmail.com">J.M. Garcia</a>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface AwsBackendHandler {
	
	/**
	 * Name of this handler (4-64 characters, digits, letters, and underscore chars allowed)
	 */
	String name();
	
	/**
	 * Textual description 
	 */
	String description();

	/**
	 * Role from which this handler will be run
	 */
	String role();
	
	/**
	 * Action that causes the invocation of this handler
	 */
	AwsTrigger trigger() default @AwsTrigger(type = TriggerType.None);

}