/*
 *
 * Copyright 2013-2016 Pacific Coast Professional Services, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.tf.photos.backing;

import java.io.IOException;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.ResourceBundle;

import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.RandomStringUtils;
import org.omnifaces.util.Faces;
import org.omnifaces.util.Messages;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tf.photos.backing.validator.PasswordValidator;
import com.tf.photos.model.RegUser;
import com.tf.photos.model.UserStatus;
import com.tf.photos.model.UserType;
import com.tf.photos.model.WebEvent;
import com.tf.photos.model.WebUser;
import com.tf.photos.model.WebUserSession;
import com.tf.photos.service.AuditLogService;
import com.tf.photos.service.WebUserService;
import com.tf.photos.service.util.ResourceBundleUtil;
import com.tf.photos.service.util.SpringLookup;
import com.tf.photos.util.KryptoUtil;
import com.tf.photos.util.SendEmail;

/**
 * Registers a user's account.
 *
 * Date: 5/11/13
 */
@Named
@RequestScoped
public class UserRegistrationBean implements Serializable {

	private static final long serialVersionUID = -3701239480064317218L;
    private static final Logger log = LoggerFactory.getLogger(UserRegistrationBean.class);
    private static final int ACTIVATION_CODE_EXP = 3; // days
    private static final int MAX_FAILED_LOGIN_ATTEMPTS = 4;
    private static final String ACTIVATE_LINK1 = "<a href=\"";
    private static final String ACTIVATE_LINK2 = "\">";
    private static final String ACTIVATE_LINK_END = "</a>";
    private static final String ACTIVATE_URI = "users/newUserActivation.jsf?activationCode=";
    private static final String OVERVIEW_URI = "/home/overview.jsf";
    private static final String LOGIN_URI = "/users/userLogin.jsf";
    private static final String REQ_ACTIVATION_URI = "/users/userRequiresActivation.jsf";
    private static final String NEW_USER_EMAIL_SENT_PAGE = "/users/newUserEmailSent.jsf";
	public static final int RANDOM_KEY_SIZE = 64;

	@Inject
    private RegUser regUser;

	@Inject
	private WebUserSession webUserSession;

    @Inject
    private PasswordValidator passwordValidator;

    @Inject
    ResourceBundleUtil resourceBundleUtil;

    public UserRegistrationBean() {}

    /**
     * First part of user registration.
     *
     * @return  JSF2 navigation.
     */
    public void registerUser() {

        log.info("User Registration");
        // Get rid of the remembered password.
        passwordValidator.clear();

        if (setupInitialUserRegistration()) {
	        try
	        {
		        Faces.redirect(Faces.getRequestContextPath() + NEW_USER_EMAIL_SENT_PAGE);
	        }
	        catch (IOException e)
	        {
		        log.error("Failed to redirect after successful registration request.", e);
	        }
        }

        ResourceBundle resourceBundle = resourceBundleUtil.getResourceBundle(ResourceBundleUtil.USERS);
        Messages.addGlobalError(resourceBundle.getString("errorWithRegistration"));
    }

	/**
	 * Updates the user account with a new activation code and emails out the code to the user's email address.
	 *
	 * @return  JSF2 Navigation.
	 */
	public String newActivationCode() {
		log.info("New Activation Code Requested for web user " + webUserSession.getWebUser().getUserName() + " email " + webUserSession.getWebUser().getEmailAddress());

		setupUserActivationCode(webUserSession.getWebUser());
		sendActivationEmail(webUserSession.getWebUser());

		try {
			WebUserService userService = (WebUserService) SpringLookup.findService("webUserService");
			userService.updateUser(webUserSession.getWebUser());
			getAuditLogService().recordEvent(webUserSession.getWebUser(), WebEvent.EVENT_USER_NEW_ACTIVATION_REQUEST);
		}
		catch (Exception e) {
			log.error("Failed to update user " + webUserSession.getWebUser().getUserName() + " with new activation code. ", e);
		}

		return "/users/newUserEmailSent.jsf";
	}

    /**
     * Create an initial account that requires email activation to be used.
     */
    private boolean setupInitialUserRegistration() {

        ResourceBundle resourceBundle = resourceBundleUtil.getResourceBundle(ResourceBundleUtil.USERS);

        WebUser webUser = new WebUser();
        webUser.setUserName(regUser.getUserName().toLowerCase());
        webUser.setEmailAddress(regUser.getEmailAddress());
	    webUserSession.setWebUser(webUser);

        try {
            webUser.setPassword(KryptoUtil.createHash(regUser.getPassword()));
        }
        catch (Exception e) {
            log.error("Failed to hash password.", e);

            // In a managed bean action method.
            Messages.addGlobalError(resourceBundle.getString("errorSystemWithRegistration"));
            return false;
        }

        webUser.setUserStatus(UserStatus.NEW);
        webUser.setUserType(UserType.SELLER);
        webUser.setCreatedDate(new Date());

	    webUser.setRemoteAddress(Faces.getRemoteAddr());

	    if (!setupUserActivationCode(webUser)) {

		    // This should not happen, but there is no need to process with sending out an email if we are in error mode.
		    return false;
	    }

	    log.info("preparing to send email ");
	    sendActivationEmail(webUser);

        try {
            WebUserService userService = (WebUserService)SpringLookup.findService("webUserService");
            userService.addUser(webUser);
        }
        catch (Exception e) {
            log.error("Failed to setup user activate for user "+regUser.getUserName(), e);

            // In a managed bean action method.
            Messages.addGlobalError(resourceBundle.getString("errorWithRegistration"));
            return false;
        }

	    getAuditLogService().recordEvent(webUser, WebEvent.EVENT_USER_REGISTERED);

	    return true; //success
    }

	/**
	 * Create an activation code for user.
	 *
	 * @param webUser   User to create the code for.
	 * @return          True if successful.
	 */
	private boolean setupUserActivationCode(WebUser webUser) {
		try {
			webUser.setActivationCode(RandomStringUtils.randomAlphanumeric(RANDOM_KEY_SIZE));

		} catch ( Exception e) {

			log.error("Failed to activation code.", e);

			ResourceBundle resourceBundle = resourceBundleUtil.getResourceBundle(ResourceBundleUtil.USERS);
			Messages.addGlobalError(resourceBundle.getString("errorWithRegistration"));
			return false;
		}

		webUser.setActivationExpiration(determineActivationExpiration());
		return true;
	}


    /**
     * User activation, requires proper code.
     *
     * @return  JSF2 navigation.
     */
    public void activateUser() {

	    WebUser webUser1 = lookupUser(regUser.getUserName());
	    String nextPage = null;

	    try {
		    // Was a username entered that we have in the db?
		    if (webUser1 == null) {
			    log.info("no user found");
			    failedAttempt(null, "errorUserNamePassword");
		    }
		    else if (UserStatus.LOCKED.equals(webUser1.getUserStatus())) {
			    showLockedMessage(webUser1);
		    }
		    // Check for correct password
		    else if (!KryptoUtil.matches(regUser.getPassword(), webUser1.getPassword())) {
			    log.info("wrong password");
			    failedAttempt(webUser1, "errorUserNamePassword");
		    }
		    // Is user activating their account again needlessly?
		    else if (!UserStatus.ANON.equals(webUser1.getUserStatus()) && !UserStatus.NEW.equals(webUser1.getUserStatus())) {
			    noNeedToActivateErrorMessage();
			    webUserSession.setLoggedIn(Boolean.TRUE);
			    webUserSession.setWebUser(webUser1);
			    nextPage = OVERVIEW_URI;
		    }
		    else if (regUser.getActivationCode() == null ||
							 webUser1.getActivationExpiration().before(new Date()) ||
				             !regUser.getActivationCode().equals(webUser1.getActivationCode())) {

			    dealWithInvalidActivationCode(webUser1);
			    nextPage = REQ_ACTIVATION_URI;
		    }
		    else {
			    // Activation of user passed validation.
			    setUserAccountActive(webUser1);
			    getAuditLogService().recordEvent(webUser1, WebEvent.EVENT_USER_ACTIVATED);

			    nextPage = OVERVIEW_URI;
		    }

		    if (webUser1 != null) {
			    updateUser(webUser1);
		    }
	    }
	    catch (Exception e) {
		    log.error("Failed to setup user activate for user "+regUser.getUserName(), e);
		    failedAttempt(webUser1, "errorActivationCode");
	    }

	    try
	    {
		    if (nextPage != null) {
			    Faces.redirect(Faces.getRequestContextPath() + nextPage);
		    }
	    }
	    catch (IOException e)
	    {
		    log.error("Failed to redirect after successful registration request.", e);
	    }
    }

	/**
	 * User login.
	 *
	 * @return  JSF2 navigation.
	 */
	public String loginUser() {

		log.info("login attempt " + regUser.getUserName());
		WebUser webUser1 = lookupUser(regUser.getUserName());
		String nextPage = null;

		try {

			if (webUser1 == null) {
				failedAttempt(null, "errorUserNamePassword");
			}
			else if (UserStatus.LOCKED.equals(webUser1.getUserStatus()) ||
					UserStatus.BLOCKED.equals(webUser1.getUserStatus()) ||
					webUser1.getPassword() == null) {

				showLockedMessage(webUser1);
			}
			else if (!KryptoUtil.matches(regUser.getPassword(), webUser1.getPassword())) {

				failedAttempt(webUser1, "errorUserNamePassword");
			}
			// Username and password were correct, but the acct hasn't been activated.
			else if (!UserStatus.ACTIVE.equals(webUser1.getUserStatus())) {

				prepareUserNeedsActivation(webUser1);
				nextPage = REQ_ACTIVATION_URI;
			}
			else {
				// Set user to logged in state.
				setUserAccountActive(webUser1);
				nextPage = OVERVIEW_URI;
			}

			updateUser(webUser1);
		}
		catch (Exception e) {
			log.error("Failed to login user " + regUser.getUserName(), e);
			failedAttempt(webUser1, "errorGeneral");
		}

		return nextPage;
	}

	/**
	 * Pull user from db.
	 *
	 * @param userName  Name of the user to look for.
	 * @return          Web user if found.
	 */
	private WebUser lookupUser(String userName) {
		try
		{
			if (userName != null)
			{
				WebUserService userService = (WebUserService) SpringLookup.findService("webUserService");
				return userService.getUserByUserName(userName.toLowerCase());
			}
		}
		catch (Exception e) {
			log.warn("Failed to lookup user " + userName);
		}

		return null;
	}

	/**
	 * Save changes to user account plus add the current ip address in use.
	 *
	 * @param webUser       User record to save in db.
	 * @return              True if user record was saved successfully.
	 */
	private boolean updateUser(WebUser webUser) {
		try
		{
			if (webUser != null)
			{
				WebUserService userService = (WebUserService) SpringLookup.findService("webUserService");
				webUser.setRemoteAddress(Faces.getRemoteAddr());
				userService.updateUser(webUser);
				return true;
			}
		}
		catch (Exception e)
		{
			log.error("Failed to update user with name " + webUser.getUserName(), e);
			getAuditLogService().recordEvent(webUserSession.getWebUser(), e, WebEvent.EVENT_ERROR_OCCURRED);
		}

		return false;
	}

	/**
	 * Get ready to display User Needs Activation page.
	 */
	private void prepareUserNeedsActivation(WebUser webUser) {

		webUserSession.setWebUser(webUser);

		if (webUser.getActivationEmailSent() == null) {
			webUser.setActivationEmailSent(webUser.getCreatedDate());
		}

		getAuditLogService().recordEvent(webUser, WebEvent.EVENT_USER_LOGIN_FAILED, "Activation Still Required.");
	}

	/**
	 * Change the user's state to not logged in.
	 */
	public String logoutUser() {
		webUserSession.setLoggedIn(Boolean.FALSE);
		getAuditLogService().recordEvent(webUserSession.getWebUser(), WebEvent.EVENT_USER_LOGOUT);
		return LOGIN_URI;
	}

	/**
	 * Deal with failed attempt to activate user account or simply login.
	 *
	 * @param webUser1          User looked up by user name, may be null.
	 * @param errorMessageKey   Key used to lookup error message in resource bundle.
	 */
	private void failedAttempt(WebUser webUser1, String errorMessageKey) {

		String message = resourceBundleUtil.getResourceBundle(ResourceBundleUtil.USERS).getString(errorMessageKey);

		if (webUser1 != null) {

			if (webUser1.getFailedLoginAttempts() == null) {
				webUser1.setFailedLoginAttempts(1);
			}
			else
			{
				webUser1.setFailedLoginAttempts(webUser1.getFailedLoginAttempts() + 1);
			}

			getAuditLogService().recordEvent(webUser1, WebEvent.EVENT_USER_LOGIN_FAILED, message);

			if (webUser1.getFailedLoginAttempts() >= MAX_FAILED_LOGIN_ATTEMPTS) {
				webUser1.setLockedDate(new Date());
				webUser1.setLockedReason(message);
				webUser1.setUserStatus(UserStatus.LOCKED);

				getAuditLogService().recordEvent(webUser1, WebEvent.EVENT_USER_LOCKED, message);
			}
		}
		else {
			getAuditLogService().recordEvent(WebEvent.EVENT_USER_LOGIN_FAILED, Faces.getRemoteAddr(), message);
		}

		// Further process failed attempt by checking number of attempts from remote address.
		getAuditLogService().processFailedLoginAttempts(Faces.getRemoteAddr(), webUser1);

		log.info("UserRegistrationBean user failed attempt: "+message);
		Messages.addGlobalError(message);
	}

	/**
	 * Adds user locked message to faces global message component.
	 *
	 * @param webUser       User with locked account.
	 */
	private void showLockedMessage(WebUser webUser) {
		String message = resourceBundleUtil.getResourceBundle(ResourceBundleUtil.USERS).getString("errorAccountLocked");
		Messages.addGlobalError(message);

		getAuditLogService().recordEvent(webUser, WebEvent.EVENT_USER_LOGIN_FAILED, message);
	}

	/**
	 * Add message to faces global message component, that the activation attempt failed.
	 *
	 * @param webUser1  User attempting to activate their account.
	 */
	private void dealWithInvalidActivationCode(WebUser webUser1) {
		if (webUser1.getActivationExpiration() != null && webUser1.getActivationExpiration().before(new Date()) ) {
			failedAttempt(webUser1, "errorActivationCodeExpired");
			log.info("User activate code has expired and can't be used. expired at " +webUser1.getActivationExpiration() + " current date time " +new Date());
		}
		else {
			failedAttempt(webUser1, "errorActivationCodeInvalid");
			log.info("User activation code doesn't match, from email " + regUser.getActivationCode() + ", expected " + webUser1.getActivationCode());
		}

		getAuditLogService().recordEvent(webUser1, WebEvent.EVENT_USER_ACTIVATION_FAILED);

		webUserSession.setLoggedIn(Boolean.FALSE);
		webUserSession.setWebUser(webUser1);
	}

	/**
	 * Update webUser to active status.
	 *
	 * @param webUser1      User record to update.
	 */
	private void setUserAccountActive(WebUser webUser1) {
		webUser1.setLockedReason(null);
		webUser1.setLockedDate(null);
		webUser1.setFailedLoginAttempts(0);
		webUser1.setUserStatus(UserStatus.ACTIVE);
		webUser1.setActivationCode(null);
		webUser1.setActivationExpiration(null);
		webUser1.setLastLoginDate(new Date());
		webUser1.setRemoteAddress(Faces.getRemoteAddr());

		webUser1.setSecurityQuestion1(regUser.getSecurityQuestion1());
		webUser1.setSecurityQuestion2(regUser.getSecurityQuestion2());

		if (regUser.getSecurityAnswer1() != null) {
			webUser1.setSecurityAnswer1(KryptoUtil.encryptText(regUser.getSecurityAnswer1()));
		}

		if (regUser.getSecurityAnswer2() != null)
		{
			webUser1.setSecurityAnswer2(KryptoUtil.encryptText(regUser.getSecurityAnswer2()));
		}

		webUserSession.setLoggedIn(Boolean.TRUE);
		webUserSession.setWebUser(webUser1);
		HttpServletRequest httpRequest = (HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest();
		httpRequest.getSession().setAttribute("webUserSessionSecured", webUserSession);

		// heather1 is always an admin.
		if ("heather1".equalsIgnoreCase(webUser1.getUserName())) {
			log.info("user heather1 logged as admin at ." +new Date());
			webUser1.setUserType(UserType.ADMIN);
		}

		getAuditLogService().recordEvent(webUser1, WebEvent.EVENT_USER_LOGIN);

		if (UserType.ADMIN.equals(webUser1.getUserType())) {
			getAuditLogService().recordEvent(webUser1, WebEvent.EVENT_USER_ADMIN_LOGIN);
		}

		log.info("UserRegistrationBean user "+webUser1.getUserName()+" was activated.");
	}

	/**
	 * Add error message.
	 */
	private void noNeedToActivateErrorMessage() {
		String message = resourceBundleUtil.getResourceBundle(ResourceBundleUtil.USERS).getString("errorAlreadyActivated");
		Messages.addGlobalError(message);
	}

    /**
     * Set the time when the activation code will no longer be accepted.
     *
     * @return Date     expiration of the activation code.
     */
    private Date determineActivationExpiration() {

        GregorianCalendar cal = new GregorianCalendar();
        cal.add(Calendar.DATE, ACTIVATION_CODE_EXP);
        return cal.getTime();
    }

	/**
	 * Send out email to user so they can activate their account.
	 *
	 * @param webUser   User's stored model.
	 */
    private void sendActivationEmail(WebUser webUser) {
        ResourceBundle resourceBundle = resourceBundleUtil.getResourceBundle(ResourceBundleUtil.USERS);

        final String message = createActivationMessage(webUser.getActivationCode());
        String noreply = "heather92115@yahoo.com";
        SendEmail.sendMessage(webUser.getEmailAddress(), noreply, resourceBundle.getString("siteActivationEmailTitle"), message);
		webUser.setActivationEmailSent(new Date());

        log.info("sent activation email to " + webUser.getEmailAddress() + " for user " + webUser.getUserName());
    }

	/**
	 * Creates the message of the activation email.
	 *
	 * @param activationCode    Activation code to be added to the link.
	 * @return                  Message contents.
	 */
    private String createActivationMessage(final String activationCode) {

        ResourceBundle resourceBundle = resourceBundleUtil.getResourceBundle(ResourceBundleUtil.USERS);

        final String activationUrl = Faces.getRequestBaseURL()+ACTIVATE_URI+activationCode;
        log.info("activationUrl "+activationUrl);

        StringBuilder msgBldr = new StringBuilder();
        msgBldr.append(resourceBundle.getString("toActivate")).append(" ");

	    msgBldr.append(ACTIVATE_LINK1).append(activationUrl).append(ACTIVATE_LINK2);
	    msgBldr.append(activationUrl).append(ACTIVATE_LINK_END);

	    log.info("Activation email msg content: "+msgBldr.toString());

        return msgBldr.toString();
    }

	/**
	 * Used to determine if the current activation code has expired.
	 *
	 * @return      The current system date and time.
	 */
	public Date getNow() {
		return new  Date();
	}

	private AuditLogService getAuditLogService() {
		return (AuditLogService)SpringLookup.findService("auditLogService");
	}
}
