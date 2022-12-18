/**
 * 
 */
package com.iotcore.aws.model.event;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.JavaType;
import com.iotcore.core.model.event.DomainEvent;
import com.iotcore.core.util.json.JsonSerializable;

/**
 * @author jmgarcia
 *
 */
@JsonInclude( Include.NON_NULL )
@JsonIgnoreProperties( ignoreUnknown = true )
public class AwsEvent<D extends DomainEvent<D>> implements JsonSerializable<AwsEvent<D>> {

	public static  <D extends DomainEvent<D>> AwsEvent<D> deserialize(InputStream input, Class<D> detailClass) throws IOException {
		JavaType type = mapper.getTypeFactory().constructParametricType(AwsEvent.class, detailClass);
		return mapper.readValue(input, type);
	}
	
	
	private String id;
	private Date time;
	private String[] resources;
	private String version;
	private String account;
	private String region;
	private String detailType;
	private D detail;

	
	public AwsEvent() {
	}

	/**
	 * @return the _id
	 */
	public String getId() {
		return id;
	}


	/**
	 * @param _id the _id to set
	 */
	public void setId(String id) {
		this.id = id;
	}


	/**
	 * @return the time
	 */
	public Date getTime() {
		return time;
	}


	/**
	 * @param time the time to set
	 */
	public void setTime(Date time) {
		this.time = time;
	}


	/**
	 * @return the resources
	 */
	public String[] getResources() {
		return resources;
	}


	/**
	 * @param resources the resources to set
	 */
	public void setResources(String[] resources) {
		this.resources = resources;
	}


	/**
	 * @return the version
	 */
	public String getVersion() {
		return version;
	}


	/**
	 * @param version the version to set
	 */
	public void setVersion(String version) {
		this.version = version;
	}


	/**
	 * @return the account
	 */
	public String getAccount() {
		return account;
	}


	/**
	 * @param account the account to set
	 */
	public void setAccount(String account) {
		this.account = account;
	}


	/**
	 * @return the region
	 */
	public String getRegion() {
		return region;
	}


	/**
	 * @param region the region to set
	 */
	public void setRegion(String region) {
		this.region = region;
	}


	/**
	 * @return the detailType
	 */
	public String getDetailType() {
		return detailType;
	}


	/**
	 * @param detailType the detailType to set
	 */
	public void setDetailType(String detailType) {
		this.detailType = detailType;
	}


	/**
	 * @return the detail
	 */
	public D getDetail() {
		return detail;
	}


	/**
	 * @param detail the detail to set
	 */
	public void setDetail(D detail) {
		this.detail = detail;
	}


	
	

	
	public static void main(String[] args) {
		
	}


	

}
