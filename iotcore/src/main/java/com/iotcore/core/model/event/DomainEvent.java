package com.iotcore.core.model.event;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.iotcore.core.util.json.JsonSerializable;


/**
 * @author jmgarcia
 *
 * @param <E>
 */
@JsonInclude( Include.NON_NULL )
@JsonIgnoreProperties( ignoreUnknown = true )
public abstract class DomainEvent<D extends DomainEvent<D>> implements JsonSerializable<D>, Event<D> {

	
	@JsonIgnore
	private String id;
	@JsonIgnore
	private String eventName;


	/**
	 * @param eventName
	 */
	public DomainEvent(String name) {
		this.eventName = name;
	}


	/**
	 * @return the id
	 */
	@Override
	public String getId() {
		return id;
	}


	/**
	 * @param id the id to set
	 */
	@Override
	public void setId(String id) {
		this.id = id;
	}
	

	/**
	 * @return the eventName
	 */
	@Override
	public String getEventName() {
		return eventName;
	}


	/**
	 * @return
	 */
	public String getDetailType() {
		return eventName;
	}


}
