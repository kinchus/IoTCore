/**
 * 
 */
package com.iotcore.domain;

import java.util.List;

import com.iotcore.core.IoTCloud;

/**
 * @author jmgarcia
 *
 */
public interface IHolder {

	
	String getId();
	
	
	String getOrganizationID();

	
	String getName();
	
	
	List<String> getPermissions();
	

	default boolean isRootOrganization() {
		return IoTCloud.ROOT_ORGANIZATION_ID.equals(getOrganizationID());
	}


}
