/*
 * Version 1.0 3/3/2015
 * 
 */
package com.iotcore.core.dao;

import com.iotcore.core.util.StringUtil;

/**
 * Base class for entities with a String id
 * @author <a href="mailto:garciadjx@gmail.com">J.M. Garcia</a>
 * @version 1.0
 */
public abstract class StringIdEntity implements IdEntity<String> {

	private static final long serialVersionUID = 3098897372214630390L;
	
	/** OID_LENGTH */
	public static final int OID_LENGTH = 24;
	/** SID_LENGTH */
	public static final int SID_LENGTH = 11;

	private String id;
	
	
	/**
	 * @return the id
	 */
	@Override
	public String getId() {
		String retId = null;
		if (id != null) {
			// Discard the zeroes of the beginning
			retId = StringUtil.last(id.toString(), SID_LENGTH);
		}
		return retId;
		//return id;
	}

	/**
	 * Accepts an String as entity ID but takes only the last 11 digits. 
	 * @param idStr the id to set
	 */
	@Override
	public void setId(String idStr) {
		this.id = idStr;
	}


}

