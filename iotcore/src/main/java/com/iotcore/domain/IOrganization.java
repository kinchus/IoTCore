/**
 * 
 */
package com.iotcore.domain;

import com.iotcore.core.IoTCloud;

/**
 * @author jmgarcia
 *
 */
public interface IOrganization {


	String getId();
	
	
	String getName();
	
	
	default boolean isRootOrganization() {
		return IoTCloud.ROOT_ORGANIZATION_ID.equals(getId());
	}
	
}
