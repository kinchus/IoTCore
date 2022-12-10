/**
 * 
 */
package com.iotcore.core.annotation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;

import java.lang.annotation.Target;

@Target({TYPE, FIELD, METHOD})
/**
 * @author jmgarcia
 *
 */
public @interface AwsService {

}
