/**
 * 
 */
package com.iotcore.aws.model.cognito;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import com.iotcore.core.model.user.IUser;

import software.amazon.awssdk.services.cognitoidentityprovider.model.AttributeType;
import software.amazon.awssdk.services.cognitoidentityprovider.model.UserType;

/**
 * The Class CognitoUser.
 */
public class CognitoUser implements IUser, Serializable {

	private static final long serialVersionUID = -8125486807360655888L;

	public static final String ATTR_EMAIL = "email";
	public static final String ATTR_NAME = "name";
	public static final String ATTR_MIDDLE_NAME = "middle_name";
	public static final String ATTR_ADDRESS = "address";
	public static final String ATTR_CITY = "city";
	public static final String ATTR_PHONE_NUMBER = "phone_number";
	public static final String ATTR_ZONEINFO = "zoneinfo";
	public static final String ATTR_PROFILE = "profile";
	public static final String ATTR_ROLE = "custom:role";
	public static final String ATTR_OWNERID = "custom:account_id";

	/**
	 * 
	 *
	 * @param user
	 * @return
	 */
	public static CognitoUser fromUserType(UserType user) {
		final CognitoUser ret = new CognitoUser(user.username());
		ret.setAttributes(user.attributes());
		ret.setEnabled(user.enabled());
		return ret;
	}
	
	
	private String username;
	private String email;
	private String accountId;
	private String firstName;
	private String middleName;
	private String lastName;
	private String address;
	private String city;
	private String country;
	private String phoneNumber;
	private String profile;
	private List<String> groups;
	private List<String> roles;
	private String accessToken;
	private String zoneinfo;
	private Boolean enabled;
	private String status;
	private String suppliedPassword;

	/**
	 * Instantiates a new cognito user.
	 */
	public CognitoUser() {
		super();
	}

	/**
	 * Constructor
	 * 
	 * @param username
	 */
	public CognitoUser(String username) {
		this.username = username;
	}

	/**
	 * @return
	 */
	public List<AttributeType> getAttributes() {
		final List<AttributeType> attributes = new ArrayList<AttributeType>();
		if (getEmail() != null) {
			attributes.add(AttributeType.builder().name(ATTR_EMAIL).value(getEmail()).build());
		}

		if (getFirstName() != null) {
			attributes.add(AttributeType.builder().name(ATTR_NAME).value(getFirstName()).build());
		}

		if (getMiddleName() != null) {
			attributes.add(AttributeType.builder().name(ATTR_MIDDLE_NAME).value(getMiddleName()).build());
		}

		if (getAddress() != null) {
			attributes.add(AttributeType.builder().name(ATTR_ADDRESS).value(getAddress()).build());
		}

		if (getPhoneNumber() != null) {
			attributes.add(AttributeType.builder().name(ATTR_PHONE_NUMBER).value(getPhoneNumber()).build());
		}

		if (getAccountId() != null) {
			attributes.add(AttributeType.builder().name(ATTR_OWNERID).value(getAccountId()).build());
		}

		if (getRoles() != null) {
			String term = "";
			final StringBuffer rBuffStr = new StringBuffer();
			for (final String role : getRoles()) {
				rBuffStr.append(term);
				rBuffStr.append(role);
				term = ";";
			}
			attributes.add(AttributeType.builder().name(ATTR_ROLE).value(rBuffStr.toString()).build());
		}

		return attributes;
	}

	
	/**
	 * @param attributes
	 */
	public void setAttributes(Collection<AttributeType> attributes) {

		for (final AttributeType attr : attributes) {
			if (attr.name().equals(ATTR_EMAIL)) {
				setEmail(attr.value());
			} else if (attr.name().equals(ATTR_NAME)) {
				setFirstName(attr.value());
			} else if (attr.name().equals(ATTR_MIDDLE_NAME)) {
				setMiddleName(attr.value());
			} else if (attr.name().equals(ATTR_PHONE_NUMBER)) {
				setPhoneNumber(attr.value());
			} else if (attr.name().equals(ATTR_ADDRESS)) {
				setAddress(attr.value());
			} else if (attr.name().equals(ATTR_CITY)) {
				setCity(attr.value());
			} else if (attr.name().equals(ATTR_ZONEINFO)) {
				setZoneinfo(attr.value());
			} else if (attr.name().equals(ATTR_OWNERID)) {
				setAccountId(attr.value());
			} else if (attr.name().equals(ATTR_ROLE)) {
				String[] roleStr = attr.value().split(";");
				setRoles(Arrays.asList(roleStr));
			}
		}
	}

	/**
	 * @return the firstName
	 */
	@Override
	public String getUsername() {
		return username;
	}

	/**
	 * @param username the firstName to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * @return the firstName
	 */
	@Override
	public String getFirstName() {
		return firstName;
	}

	/**
	 * @param fName the firstName to set
	 */
	public void setFirstName(String fName) {
		this.firstName = fName;
	}

	/**
	 * @return the middleName
	 */
	@Override
	public String getMiddleName() {
		return middleName;
	}

	/**
	 * @param middleName the middleName to set
	 */
	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}

	/**
	 * @return the lastName
	 */
	@Override
	public String getLastName() {
		return lastName;
	}

	/**
	 * @param lastName the lastName to set
	 */
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	
	@Override
	public String getFullName() {
		StringBuffer strBuff = new StringBuffer();
		String sep = "";
		if (firstName != null) {
			strBuff.append(firstName);
			sep = " ";
		}
		if (middleName != null) {
			strBuff.append(sep + middleName);
			sep = " ";
		}
		if (lastName != null) {
			strBuff.append(sep + lastName);
			sep = " ";
		}
		return strBuff.toString();
	}

	/**
	 * @return the email
	 */
	@Override
	public String getEmail() {
		return email;
	}

	/**
	 * @param email the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}
	
	/**
	 * @return the accountId
	 */
	@Override
	public String getAccountId() {
		return accountId;
	}

	/**
	 * @param accountId the accountId to set
	 */
	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}



	/**
	 * @return the address
	 */
	@Override
	public String getAddress() {
		return address;
	}

	/**
	 * @param address the address to set
	 */
	public void setAddress(String address) {
		this.address = address;
	}

	/**
	 * @return the city
	 */
	@Override
	public String getCity() {
		return city;
	}

	/**
	 * @param city the city to set
	 */
	public void setCity(String city) {
		this.city = city;
	}

	/**
	 * @return the country
	 */
	@Override
	public String getCountry() {
		return country;
	}

	/**
	 * @param country the country to set
	 */
	public void setCountry(String country) {
		this.country = country;
	}

	/**
	 * @return the phoneNumber
	 */
	@Override
	public String getPhoneNumber() {
		return phoneNumber;
	}

	/**
	 * @param phoneNumber the phoneNumber to set
	 */
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	
	/**
	 * @return the profile
	 */
	public String getProfile() {
		return profile;
	}
	
	/**
	 * @return the zoneinfo
	 */
	public String getZoneinfo() {
		return zoneinfo;
	}

	/**
	 * @param zoneinfo the zoneinfo to set
	 */
	public void setZoneinfo(String zoneinfo) {
		this.zoneinfo = zoneinfo;
	}



	/**
	 * @param profile the profile to set
	 */
	public void setProfile(String profile) {
		this.profile = profile;
	}
	

	/**
	 * @return the accessToken
	 */
	@Override
	public String getAccessToken() {
		return accessToken;
	}

	/**
	 * @param accessToken the accessToken to set
	 */
	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	/**
	 * @return the groups
	 */
	@Override
	public List<String> getGroups() {
		return groups;
	}

	/**
	 * @param groups the groups to set
	 */
	public void setGroups(List<String> groups) {
		this.groups = groups;
	}

	
	/**
	 * Checks for group.
	 *
	 * @param group the group
	 * @return true, if successful
	 */
	@Override
	public boolean hasGroup(String group) {
		for (String userGrp:getGroups()) {
			if (group.equals(userGrp)) {
				return true;
			}
		}
		return false;
	}
	

	/**
	 * @return the roles
	 */
	@Override
	public List<String> getRoles() {
		return roles;
	}

	/**
	 * @param roles the roles to set
	 */
	public void setRoles(List<String> roles) {
		this.roles = roles;
	}
	

	
    /**
     * @param role
     * @return
     */
	@Override
	public Boolean hasRole(String role) {
    	if (roles != null) {
    		for (String r:roles) {
	    		if (r.equals(role)) {
	    			return true;
	    		}
    		}
    	}	
    	return false;
    }
    
    /**
     * Check it the actual user has any of the given roles
     * @param roles role names
     * @return True if at least one of the role names matches one of the user roles  
     */
	@Override
	public Boolean hasAnyRole(String ... roles) {
    	for (String r:roles) {
	    	for(String userRole:this.roles) {
	    		if (r.equals(userRole)) {
	    			return true;
	    		}
	    	}
    	}
    	return false;
    }
   

	/**
	 * @param role 
	 *
	 */
	public void addRole(String role) {
		if (roles == null) {
			roles = new ArrayList<String>();
		}
		if (!roles.contains(role)) {
			roles.add(role);
		}
		return;
	}

	/**
	 * @param role 
	 *
	 */
	public void removeRole(String role) {
		if (roles != null) {
			roles.remove(role);
		}
		return;
	}


	/**
	 * @param group 
	 *
	 */
	public void addGroup(String group) {
		if (groups == null) {
			groups = new ArrayList<String>();
		}
		groups.add(group);
		return;
	}

	/**
	 * @param group 
	 *
	 */
	public void removeGroup(String group) {
		if (groups != null) {
			groups.remove(group);
		}
		return;
	}


	@Override
	public boolean equals(Object obj) {
		if (obj instanceof CognitoUser) {
			boolean ret = true;
			CognitoUser other = (CognitoUser)obj;
			if ((getUsername() != null)&& (other.getUsername() != null)) {
				ret = getUsername().equals(other.getUsername());
			}
			if (ret && (getEmail() != null) && (other.getEmail() != null)) {
				ret = getEmail().equals(other.getEmail());
			}
			return ret;
		}
		return false;
	}


	
	/**
	 * @return the enabled
	 */
	@Override
	public Boolean getEnabled() {
		return enabled;
	}

	/**
	 * @param enabled the enabled to set
	 */
	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}

	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * @return the suppliedPassword
	 */
	public String getSuppliedPassword() {
		return suppliedPassword;
	}

	/**
	 * @param suppliedPassword the suppliedPassword to set
	 */
	public void setSuppliedPassword(String suppliedPassword) {
		this.suppliedPassword = suppliedPassword;
	}

}
