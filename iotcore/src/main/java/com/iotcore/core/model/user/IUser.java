package com.iotcore.core.model.user;

import java.util.List;

public interface IUser {

	/**
	 * @return the firstName
	 */
	String getUsername();

	/**
	 * @return the firstName
	 */
	String getFirstName();

	/**
	 * @return the middleName
	 */
	String getMiddleName();

	/**
	 * @return the lastName
	 */
	String getLastName();

	String getFullName();

	/**
	 * @return the email
	 */
	String getEmail();

	/**
	 * @return the accountId
	 */
	String getAccountId();

	/**
	 * @return the address
	 */
	String getAddress();

	/**
	 * @return the city
	 */
	String getCity();

	/**
	 * @return the country
	 */
	String getCountry();

	/**
	 * @return the phoneNumber
	 */
	String getPhoneNumber();

	/**
	 * @return the enabled
	 */
	Boolean getEnabled();

	/**
	 * @return the profile
	 */
	String getProfile();

	/**
	 * @return the accessToken
	 */
	String getAccessToken();

	/**
	 * @return the groups
	 */
	List<String> getGroups();

	/**
	 * Checks for group.
	 *
	 * @param group the group
	 * @return true, if successful
	 */
	boolean hasGroup(String group);

	/**
	 * @return the roles
	 */
	List<String> getRoles();

	/**
	 * @param role
	 * @return
	 */
	Boolean hasRole(String role);

	/**
	 * Check it the actual user has any of the given roles
	 * @param roles role names
	 * @return True if at least one of the role names matches one of the user roles  
	 */
	Boolean hasAnyRole(String... roles);

}