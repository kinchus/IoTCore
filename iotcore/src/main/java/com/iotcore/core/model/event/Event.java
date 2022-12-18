package com.iotcore.core.model.event;

import com.iotcore.core.util.json.JsonSerializable;

public interface Event<E extends Event<E>> extends JsonSerializable<E>  {
	
	/** BUSNAME_DEFAULT */
	String BUSNAME_DEFAULT = "default";
	/** SOURCE_DEFAULT */
	String SOURCE = "com.iotcore";


	/**
	 * @return
	 */
	String getId();
	
	/**
	 * @param _id
	 */
	void setId(String id);
	
	/**
	 * @return
	 */
	String getEventName();



//	/**
//	 * @return
//	 */
//	Date getTime();
//	
//	
//	/**
//	 * @return
//	 */
//	Object getDetail();
//	
	/**
	 * @return
	 */
	default String getSource() {
		return SOURCE;
	}

	/**
	 * @return
	 */
	default String getBusName() {
		return BUSNAME_DEFAULT;
	}
//	
//	/**
//	 * @return
//	 */
//	default String getDetailString() {
//		return getDetail().toString();
//	}
//		

}
