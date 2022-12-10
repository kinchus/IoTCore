/**
 * 
 */
package com.iotcore.core.annotation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;

import java.lang.annotation.Target;

/**
 * @author jmgarcia
 *
 */
@Target({TYPE, FIELD, METHOD})
public @interface MongoDAO {

}
