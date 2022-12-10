package com.iotcore.aws.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.enterprise.context.SessionScoped;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iotcore.aws.AwsConfig;
import com.iotcore.aws.exception.AwsException;
import com.iotcore.aws.exception.cognito.UnsupportedChallengeException;
import com.iotcore.aws.model.cognito.CognitoUser;
import com.iotcore.core.model.exception.ObjectExistsException;
import com.iotcore.core.model.exception.ObjectNotFoundException;
import com.iotcore.core.model.exception.ObjectValidationException;
import com.iotcore.core.model.exception.ServiceException;
import com.iotcore.core.model.exception.UnsuportedOperationException;

import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminAddUserToGroupRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminAddUserToGroupResponse;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminCreateUserRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminCreateUserResponse;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminDeleteUserRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminDisableUserRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminEnableUserRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminGetUserRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminGetUserResponse;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminInitiateAuthRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminInitiateAuthResponse;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminListGroupsForUserRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminListGroupsForUserResponse;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminRemoveUserFromGroupRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminResetUserPasswordRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminRespondToAuthChallengeRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminRespondToAuthChallengeResponse;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminUpdateUserAttributesRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminUserGlobalSignOutRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminUserGlobalSignOutResponse;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AttributeType;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AuthFlowType;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AuthenticationResultType;
import software.amazon.awssdk.services.cognitoidentityprovider.model.ChallengeNameType;
import software.amazon.awssdk.services.cognitoidentityprovider.model.CodeMismatchException;
import software.amazon.awssdk.services.cognitoidentityprovider.model.ConfirmForgotPasswordRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.ExpiredCodeException;
import software.amazon.awssdk.services.cognitoidentityprovider.model.ForgotPasswordRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.GroupType;
import software.amazon.awssdk.services.cognitoidentityprovider.model.InvalidPasswordException;
import software.amazon.awssdk.services.cognitoidentityprovider.model.ListUsersRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.ListUsersResponse;
import software.amazon.awssdk.services.cognitoidentityprovider.model.PasswordResetRequiredException;
import software.amazon.awssdk.services.cognitoidentityprovider.model.UserNotConfirmedException;
import software.amazon.awssdk.services.cognitoidentityprovider.model.UserNotFoundException;
import software.amazon.awssdk.services.cognitoidentityprovider.model.UserType;
import software.amazon.awssdk.services.cognitoidentityprovider.model.UsernameExistsException;

/**
 * 
 * @author <a href="mailto:garciadjx@gmail.com">J.M. Garcia</a>
 */
@SessionScoped
public class AwsCognito extends AwsBase<CognitoIdentityProviderClient> {
	
	/**
	 * 
	 */
	public enum LoginStatus {
		LOGIN_OK,
		LOGIN_ERR,
		MUST_CHANGE_PASSWORD,
		USER_NOT_FOUND,
		SUBMITTED_INVALID_PASSWORD,
		NOT_AUTHORIZED, 
		RESET_REQUIRED;
		
		private String session;
		private String message;

		/**
		 * @return the session
		 */
		public String getSession() {
			return session;
		}

		/**
		 * @param session the session to set
		 * @return 
		 */
		public LoginStatus withSession(String session) {
			this.session = session;
			return this;
		}
		
		/**
		 * @param message the session to set
		 * @return 
		 */
		public LoginStatus withMessage(String message) {
			this.message = message;
			return this;
		}


		/**
		 * @return the message
		 */
		public String getMessage() {
			return message;
		}
		
	}



	
	private static final long serialVersionUID = 689089001630157956L;
	private static final Logger LOG = LoggerFactory.getLogger(AwsCognito.class);
	private static final String ATTR_EMAIL_VERIFIED = "email_verified";
	private static final ChallengeNameType CHANGE_PASSWD = ChallengeNameType.NEW_PASSWORD_REQUIRED;
	private static final int MAX_GROUP_PRECEDENCE = 5;

	
	
	private String identityPoolId = null;
	private String appClientId = null;

	

	@Override
	public CognitoIdentityProviderClient create() {
		return CognitoIdentityProviderClient.builder().build();
	}
	

	/**
	 * 
	 */
	public AwsCognito() {
		super();
	}
	
	


	/**
	 * @return the identityPoolId
	 */
	protected String getIdentityPoolId() {
		if (identityPoolId == null) {
			identityPoolId = getConfig().getProperty(AwsConfig.AWS_COGNITO_USERPOOLID);
		}
		return identityPoolId;
	}


	/**
	 * @return the appClientId
	 */
	protected String getAppClientId() {
		if (appClientId == null) {
			appClientId = getConfig().getProperty(AwsConfig.AWS_COGNITO_POOLCLIENTID);
		}
		return appClientId;
	}


	/**
	 * 
	 *
	 * @param username
	 * @param password
	 * @return
	 */
	public LoginStatus validate(String username, char[] password) {

		LOG.trace("Starting login for user {}", username);

		final Map<String, String> authParams = new HashMap<String, String>();
		authParams.put("USERNAME", username);
		authParams.put("PASSWORD", new String(password));

		AdminInitiateAuthResponse authRes = null;
		final AdminInitiateAuthRequest authReq = AdminInitiateAuthRequest.builder()
				.authFlow(AuthFlowType.ADMIN_NO_SRP_AUTH)
				.authParameters(authParams)
				.clientId(getAppClientId())
				.userPoolId(getIdentityPoolId())
				.build();
		
		try {
			authRes = client().adminInitiateAuth(authReq);
		} catch (final UserNotFoundException e) {
			return LoginStatus.USER_NOT_FOUND;
		} catch (PasswordResetRequiredException e) {
			return LoginStatus.RESET_REQUIRED;
		} catch (final AwsServiceException e) {
			return LoginStatus.LOGIN_ERR.withMessage(e.getMessage());
		}

		if ((authRes.challengeName() != null) && authRes.challengeName().equals(CHANGE_PASSWD)) {
			LOG.debug("Password must be changed for user", username);
			return LoginStatus.MUST_CHANGE_PASSWORD.withSession(authRes.session());
		}

		LOG.trace("Login successful");
		final AuthenticationResultType res = authRes.authenticationResult();
		return LoginStatus.LOGIN_OK.withSession(res.accessToken());
	}


	public boolean invalidate(String username) {
		
		AdminUserGlobalSignOutRequest req = AdminUserGlobalSignOutRequest.builder()
				.username(username)
				.userPoolId(getIdentityPoolId())
				.build();
		
		try {
			AdminUserGlobalSignOutResponse res = client().adminUserGlobalSignOut(req);
			return responseOk(res);
		} catch (final AwsServiceException e) {
			return false;
		}
		
	}
	
	
	/**
	 * 
	 *
	 * @param authSession
	 * @param username
	 * @param password
	 * @param newPassword
	 * @return String with the access token
	 * @throws UnsupportedChallengeException
	 * @throws AwsException
	 */
	public String changeUserPassword(Object authSession, String username, char [] password, char [] newPassword)
			throws ServiceException, UnsupportedChallengeException {

		final Map<String, String> challengeRes = new HashMap<String, String>();
		challengeRes.put("USERNAME", username);
		challengeRes.put("PASSWORD", new String(password));
		challengeRes.put("NEW_PASSWORD", new String(newPassword));
		
		String authStr = authSession.toString();

		AdminRespondToAuthChallengeRequest req = AdminRespondToAuthChallengeRequest.builder()
				.challengeName(ChallengeNameType.NEW_PASSWORD_REQUIRED)
				.challengeResponses(challengeRes)
				.clientId(getAppClientId())
				.userPoolId(getIdentityPoolId())
				.session(authStr)
				.build();
		
		AdminRespondToAuthChallengeResponse res = null;
		try {
			LOG.trace("Requesting user password change");
			res = client().adminRespondToAuthChallenge(req);
		} catch (final AwsServiceException e) {
			throw new ServiceException("AdminRespondToAuthChallenge", e);
		}

		if (res.challengeName() == null) {
			final AuthenticationResultType authResType = res.authenticationResult();
			LOG.trace("Password changed successfully");

			// Update attrubuttes: email-verified
			final AttributeType userEmailAttr = AttributeType.builder()
				.name(ATTR_EMAIL_VERIFIED)
				.value("true")
				.build();
			
			client().adminUpdateUserAttributes(AdminUpdateUserAttributesRequest.builder()
					.username(username)
					.userAttributes(userEmailAttr)
					.userPoolId(getIdentityPoolId())
					.build());

			return authResType.accessToken();
		} else {
			throw new UnsupportedChallengeException(res.challengeName().name());
		}
	}


	public void resetUserPassword(String username)
			throws ObjectNotFoundException, ServiceException {

		LOG.trace("Reset password for for {} ", username);
		final AdminResetUserPasswordRequest request = AdminResetUserPasswordRequest.builder()
				.username(username)
				.userPoolId(getIdentityPoolId())
				.build();
				

		try {
			client().adminResetUserPassword(request);
		} catch (final UserNotFoundException e) {
			throw new ObjectNotFoundException("User not found in pool: " + username);
		} catch (final UserNotConfirmedException e) {
			throw new ServiceException("User not confirmed: " + username);
		} catch (final AwsServiceException e) {
			throw new ServiceException(e);
		}

	}

	/**
	 * @param username
	 * @throws ObjectNotFoundException
	 * @throws UnsuportedOperationException
	 * @throws ServiceException
	 * @see com.laiwa.iotcloud.domain.service.domain.service.UserLoginService#sendVerificationCode(java.lang.String)
	 */
	public void sendVerificationCode(String username)
			throws ObjectNotFoundException, ServiceException {

		LOG.trace("Sending verification code to {} ", username);
		final ForgotPasswordRequest request = ForgotPasswordRequest.builder()
				.username(username)
				.clientId(getAppClientId())
				.build();
				

		try {
			client().forgotPassword(request);
		} catch (final UserNotFoundException e) {
			throw new ObjectNotFoundException("User not found in pool: " + username);
		} catch (final UserNotConfirmedException e) {
			throw new ServiceException("User not confirmed: " + username);
		} catch (final AwsServiceException e) {
			throw new ServiceException(e);
		}
	}


	/**
	 * 
	 *
	 * @param username
	 * @param code
	 * @param newPassword
	 * @throws AwsException
	 */
	public void confirmVerificationCode(String username, String code, char [] newPassword) throws ServiceException {

		LOG.trace("Confirm verification code for {} ", username);

		final ConfirmForgotPasswordRequest request = ConfirmForgotPasswordRequest.builder()
				.username(username)
				.password(new String(newPassword))
				.confirmationCode(code)
				.clientId(getAppClientId())
				.build();
		
		try {
			client().confirmForgotPassword(request);
		} catch (InvalidPasswordException | CodeMismatchException | ExpiredCodeException e) {
			throw new ObjectValidationException("Code confirmation failed: " + e.getMessage());
		} catch (final AwsServiceException e) {
			throw new ServiceException(e);
		}
	}

	
	
	
	
	

	/**
	 *
	 */
	public CognitoUser getByUsername(String username) {
		LOG.trace("Retrieve user data for {}", username);
		CognitoUser aux = null;

		final AdminGetUserRequest getUserReq = AdminGetUserRequest.builder()
				.username(username)
				.userPoolId(getIdentityPoolId())
				.build();
		
		AdminGetUserResponse gres = null;
		try {
			gres = client().adminGetUser(getUserReq);
		} catch (final UserNotFoundException nfe) {
			return null;
		}

		if (!responseOk(gres)) {
			LOG.error("Request status: {}", gres.sdkHttpResponse().statusCode());
			return null;
		}

		aux = new CognitoUser(username);
		aux.setEnabled(gres.enabled());
		aux.setStatus(gres.userStatus().name());
		aux.setAttributes(gres.userAttributes());

		final AdminListGroupsForUserRequest grReq = AdminListGroupsForUserRequest.builder()
				.userPoolId(getIdentityPoolId())
				.username(aux.getUsername())
				.build();

		final AdminListGroupsForUserResponse grRes = client().adminListGroupsForUser(grReq);
		final List<String> groups = new ArrayList<String>(grRes.groups().size());

		int actualPrec = 0;
		while (++actualPrec < MAX_GROUP_PRECEDENCE) {
			for (final GroupType group : grRes.groups()) {
				if (group.precedence() == actualPrec) {
					groups.add(group.groupName());
				}
			}
		}

		if (groups.size() > 0) {
			aux.setGroups(groups);
		}

		return aux;
	}

	
	

	/**
	 * @param email
	 * @return
	 */
	public CognitoUser getByEmail(String email) {

		final ListUsersRequest listReq = ListUsersRequest.builder()
				.filter(String.format("email = \"%s\"", email))
				.userPoolId(getIdentityPoolId())
				.build();

		final ListUsersResponse res = client().listUsers(listReq);
		if (!responseOk(res) || res.users().isEmpty()) {
			return null;
		}

		final UserType usr = res.users().get(0);
		final CognitoUser aux = new CognitoUser(usr.username());
		aux.setEnabled(usr.enabled());
		aux.setStatus(usr.userStatus().name());
		aux.setAttributes(usr.attributes());

		final AdminListGroupsForUserRequest grReq = AdminListGroupsForUserRequest.builder()
				.userPoolId(getIdentityPoolId())
				.username(aux.getUsername())
				.build();

		final AdminListGroupsForUserResponse grRes = client().adminListGroupsForUser(grReq);

		final List<String> groups = new ArrayList<String>(grRes.groups().size());

		int actualPrec = 0;
		while (++actualPrec < MAX_GROUP_PRECEDENCE) {
			for (final GroupType group : grRes.groups()) {
				if (group.precedence() == actualPrec) {
					groups.add(group.groupName());
				}
			}
		}

		if (groups.size() > 0) {
			aux.setGroups(groups);
		}

		return aux;
	}

	
	
	/**
	 * @param offset
	 * @param maxResponses
	 * @return
	 */
	public List<CognitoUser> getAll(int offset, int maxResponses) {
		List<CognitoUser> ret = null;
		final ListUsersRequest listReq = ListUsersRequest.builder()
				.userPoolId(getIdentityPoolId())
				.build();

		final ListUsersResponse res = client().listUsers(listReq);
		if (!responseOk(res) || res.users().isEmpty()) {
			return null;
		}

		final int n = res.users().size();
		ret = new ArrayList<CognitoUser>(n);

		for (final UserType usr : res.users()) {
			final CognitoUser aux = new CognitoUser(usr.username());
			aux.setEnabled(usr.enabled());
			aux.setStatus(usr.userStatus().name());
			aux.setAttributes(usr.attributes());

			final AdminListGroupsForUserResponse grRes = client()
					.adminListGroupsForUser(AdminListGroupsForUserRequest.builder()
							.userPoolId(getIdentityPoolId())
							.username(aux.getUsername())
							.build());
			
			final List<String> groups = new ArrayList<String>(grRes.groups().size());
			int actualPrec = 0;
			while (++actualPrec < MAX_GROUP_PRECEDENCE) {
				for (final GroupType group : grRes.groups()) {
					if (group.precedence() == actualPrec) {
						groups.add(group.groupName());
					}
				}
			}

			if (groups.size() > 0) {
				aux.setGroups(groups);
			}
			
			ret.add(aux);

		}

		return ret;
	}
	


	/**
	 * @param accountId
	 * @return
	 * @see com.laiwa.iotcloud.domain.service.UserDao.service.UserService#getAllByHolder(java.lang.String)
	 */
	public List<CognitoUser> getAllByAccountId(String accountId) {

		List<CognitoUser> ret = null;
		final ListUsersRequest listReq = ListUsersRequest.builder()
				.userPoolId(getIdentityPoolId())
				.attributesToGet(CognitoUser.ATTR_OWNERID)
				.build();

		final ListUsersResponse res = client().listUsers(listReq);
		if (!responseOk(res) || res.users().isEmpty()) {
			return null;
		}

		final int n = res.users().size();
		ret = new ArrayList<CognitoUser>(n);

		for (final UserType usr : res.users()) {
			final List<AttributeType> attrs = usr.attributes();
			boolean found = false;
			for (final AttributeType attr : attrs) {
				if (attr.name().equals(CognitoUser.ATTR_OWNERID) && !attr.value().equals(accountId)) {
					found = true;
					continue;
				}
			}

			if (!found) {
				continue;
			}

			final CognitoUser aux = new CognitoUser(usr.username());
			aux.setEnabled(usr.enabled());
			aux.setStatus(usr.userStatus().name());
			aux.setAttributes(attrs);
			
			final AdminListGroupsForUserResponse grRes = client().adminListGroupsForUser(
					AdminListGroupsForUserRequest.builder()
							.userPoolId(getIdentityPoolId())
							.username(aux.getUsername())
							.build());
			
			final List<String> groups = new ArrayList<String>(grRes.groups().size());
			int actualPrec = 0;
			while (++actualPrec < MAX_GROUP_PRECEDENCE) {
				for (final GroupType group : grRes.groups()) {
					if (group.precedence() == actualPrec) {
						groups.add(group.groupName());
					}
				}
			}

			if (groups.size() > 0) {
				aux.setGroups(groups);
			}

			ret.add(aux);
		}

		return ret;
	}

	/**
	 *
	 */
	
	public CognitoUser create(CognitoUser cogUser) throws ObjectExistsException, ServiceException {
		Objects.requireNonNull(cogUser);


		
		// CreateUser request
		AdminCreateUserResponse res = null;
		final AdminCreateUserRequest request = AdminCreateUserRequest.builder()
				.userPoolId(getIdentityPoolId())
				.username(cogUser.getUsername())
				.userAttributes(cogUser.getAttributes())
				.temporaryPassword(cogUser.getSuppliedPassword())
				.build();

	
		try {
			LOG.trace("Request for user {} creation.", cogUser.getUsername());
			res = client().adminCreateUser(request);
		} catch (final UsernameExistsException e) {
			throw new ObjectExistsException(cogUser.getUsername());
		} catch (final AwsServiceException e) {
			throw new ServiceException("AdminCreateUser", e);
		}

		if (!responseOk(res)) {
			LOG.error("An error occured when creating user {}: ERROR {}", cogUser.getUsername(), res.sdkHttpResponse().statusCode());
			return null;
		}

		CognitoUser ret = null;

		// Añade el usuario a los grupos a los que pertenece
		if (res.user() != null) {
			ret = CognitoUser.fromUserType(res.user());

			// Añade el usuario a los grupos
			for (final String groupName : cogUser.getGroups()) {

				final AdminAddUserToGroupRequest addGrpReq = AdminAddUserToGroupRequest.builder()
						.userPoolId(getIdentityPoolId())
						.username(cogUser.getUsername())
						.groupName(groupName)
						.build();
				
				try {
					final AdminAddUserToGroupResponse addGrpRes = client().adminAddUserToGroup(addGrpReq);
					if (responseOk(addGrpRes)) {
						LOG.trace("Couldn't add user {} to group {}. HTTP Status = {} ", cogUser.getUsername(), groupName, addGrpRes.sdkHttpResponse().statusCode());
					} else {
						LOG.trace("Added user {} to group {}.", cogUser.getUsername(), groupName);
					}
				} catch (final AwsServiceException e) {
					throw new ServiceException("AdminAddUserToGroup", e);
				}
			}
		}

		return ret;
	}

	
	public CognitoUser update(CognitoUser cUser) throws ServiceException {
		Objects.requireNonNull(cUser);

		// Build user ATTRIBUTES spec
		final List<AttributeType> attributes = cUser.getAttributes();
		final String userName = cUser.getUsername();

		// Request for user attribute update
		try {
			LOG.trace("Updating attributes");
			final AdminUpdateUserAttributesRequest updAttrReq = AdminUpdateUserAttributesRequest.builder()
					.username(userName)
					.userAttributes(attributes)
					.userPoolId(getIdentityPoolId())
					.build();
			
			client().adminUpdateUserAttributes(updAttrReq);
		} catch (final UserNotFoundException e) {
			throw new ObjectNotFoundException(userName);
		}

		LOG.trace("Updating groups for user {}", userName);
		// Load stored USER GROUPS
		final List<String> oldGroups = new ArrayList<String>();
		String token = null;
		do {
			AdminListGroupsForUserResponse resp = null;
			final AdminListGroupsForUserRequest adminListGroupsForUserReq = AdminListGroupsForUserRequest
					.builder()
					.userPoolId(getIdentityPoolId()).username(userName)
					.build();
					
			try {
				resp = client().adminListGroupsForUser(adminListGroupsForUserReq);
				for (final GroupType grpT : resp.groups()) {
					oldGroups.add(grpT.groupName());
				}
				token = resp.nextToken();
			} catch (final AwsServiceException e) {
				LOG.warn("An error occured while requesting user groups from {}: {}", userName, e.getMessage());
			}
		} while (token != null);

		// Check the stored user groups against the actual user groups
		// First add the new groups not included in the old ones
		for (final String newGrp : cUser.getGroups()) {
			if (!oldGroups.contains(newGrp)) {
				final AdminAddUserToGroupRequest addGrpReq = AdminAddUserToGroupRequest.builder()
						.userPoolId(getIdentityPoolId()).username(userName).groupName(newGrp)
						.build();
						
				try {
					client().adminAddUserToGroup(addGrpReq);
				} catch (final AwsServiceException e) {
					LOG.warn("An error occured while adding the user {} to the group {}: {}", userName, newGrp,
							e.getMessage());
				}
			}
		}

		// Then remove the old groups not present in the new ones
		for (final String oldGrp : oldGroups) {
			if (!cUser.getGroups().contains(oldGrp)) {
				final AdminRemoveUserFromGroupRequest addGrpReq = AdminRemoveUserFromGroupRequest.builder()
						.userPoolId(getIdentityPoolId())
						.username(userName)
						.groupName(oldGrp)
						.build();
				try {
					client().adminRemoveUserFromGroup(addGrpReq);
				} catch (final AwsServiceException e) {
					LOG.warn("An error occured while removing the user {} from the group {}.\n{}: {}", userName, oldGrp,
							e, e.getMessage());
				}
			}
		}

		return cUser;
	}


	
	public void delete(String username) throws ServiceException {
		Objects.requireNonNull(username);
		final AdminDeleteUserRequest delReq = AdminDeleteUserRequest.builder()
				.username(username)
				.userPoolId(getIdentityPoolId())
				.build();
				
		try {
			client().adminDeleteUser(delReq);
		} catch (final UserNotFoundException e) {
			throw new ObjectNotFoundException(username);
		}
	}

	

	/**
	 * @param username
	 * @throws Exception
	 * @see com.laiwa.iotcloud.domain.service.domain.service.UserLoginService#disableUser(java.lang.String)
	 */
	
	public void disableUser(String username) throws ObjectNotFoundException, ServiceException {
		final AdminDisableUserRequest req = AdminDisableUserRequest.builder()
				.username(username)
				.userPoolId(getIdentityPoolId())
				.build();
				

		try {
			client().adminDisableUser(req);
		} catch (final UserNotFoundException e) {
			throw new ObjectNotFoundException("User not found in pool: " + username);
		}
	}

	/**
	 * @param username
	 * @throws Exception
	 * @see com.laiwa.iotcloud.domain.service.domain.service.UserLoginService#enableUser(java.lang.String)
	 */
	
	public void enableUser(String username) throws ObjectNotFoundException, ServiceException {

		final AdminEnableUserRequest req = AdminEnableUserRequest.builder()
				.username(username)
				.userPoolId(getIdentityPoolId())
				.build();
				
		try {
			client().adminEnableUser(req);
		} catch (final UserNotFoundException e) {
			throw new ObjectNotFoundException("User not found in pool: " + username);
		}
	}


}
