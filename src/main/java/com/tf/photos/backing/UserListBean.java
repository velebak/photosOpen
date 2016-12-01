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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;

import org.omnifaces.util.Faces;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tf.photos.backing.filter.UserListFilter;
import com.tf.photos.backing.validator.PasswordValidator;
import com.tf.photos.model.Address;
import com.tf.photos.model.Country;
import com.tf.photos.model.UserTableModel;
import com.tf.photos.model.UserViewMode;
import com.tf.photos.model.WebEvent;
import com.tf.photos.model.WebUser;
import com.tf.photos.model.WebUserSession;
import com.tf.photos.service.AuditLogService;
import com.tf.photos.service.WebUserService;
import com.tf.photos.service.util.ResourceBundleUtil;
import com.tf.photos.service.util.SpringLookup;
import com.tf.photos.util.StringFormatUtil;

/**
 * @author Heather Stevens
 * Date: 5/11/13
 */
@Named
@SessionScoped
public class UserListBean implements Serializable {
    private static final long serialVersionUID = 1601516567316562497L;
	private static final Logger log = LoggerFactory.getLogger(UserListBean.class);

	private static final String INIITIAL_USER_EDIT_URI = "/user/profile/userProfile.jsf?faces-redirect=true";
	private static final String USER_EDIT_URI = "/user/profile/userProfile.jsf";
	private static final String USER_ADDRESS_EDIT_URI = "/user/profile/address.jsf";
	private static final String USER_LIST_URI = "/user/profile/userList.jsf";
	private static final int MAX_ADDRESSES = 3;

	@Inject
	private WebUserSession webUserSession;

	@Inject
	private PasswordValidator passwordValidator;

	@Inject
	private ResourceBundleUtil resourceBundleUtil;

	@Inject
	private UserListFilter userListFilter;

	private UserTableModel userTableModel = null;
	private UserViewMode userViewMode = UserViewMode.PROFILE;
	private Boolean inEditMode = Boolean.FALSE;
	private Boolean newUserMode = Boolean.FALSE;

	private WebUser currentWebUser;
	private Address currentAddress;

    public UserListBean() {
    }

    @Named
    @Produces
    @RequestScoped
    public UserTableModel getWebUserList() {

	    if (userTableModel == null) {
		    userTableModel = new UserTableModel();
		    userTableModel.setUserListFilter(userListFilter);
	    }

        return userTableModel;
    }

	/**
	 * Load the currently logged in user and get it ready to view/edit. This sets up
	 * the user profile page so the user can edit their own user details.
	 *
	 * @return              Navigation URI.
	 */
	public String loadUserProfile() {
		log.info("loadUser using current logged in user " + webUserSession.getWebUser().getUserName());
		inEditMode = Boolean.FALSE;
		newUserMode = Boolean.FALSE;

		userViewMode = UserViewMode.PROFILE;
		currentWebUser = null;
		currentAddress = null;

		loadUserByName(webUserSession.getWebUser().getUserName());

		return INIITIAL_USER_EDIT_URI;
	}

	/**
	 * Uses the web user passed in to get the profile ready for viewing.
	 */
	public void loadUserFromList() {

		log.info("loadUser from list " + currentWebUser.getUserName());
		userViewMode = UserViewMode.ADMIN;
		inEditMode = Boolean.FALSE;
		newUserMode = Boolean.FALSE;

		// Reload the full user record.
		loadUserByName(currentWebUser.getUserName());
		Faces.navigate(USER_EDIT_URI);
	}


	/**
	 * Load the specified user as the current to view/edit.
	 *
	 * @param userName      Name of user to load.
	 * @return              Navigation URI.
	 */
	private String loadUserByName(String userName) {

		log.info("loadUser by name " + userName);

		inEditMode = Boolean.FALSE;
		newUserMode = Boolean.FALSE;

		WebUserService userService = (WebUserService) SpringLookup.findService("webUserService");

		WebUser webUser1 = userService.getUserByUserName(userName);
		if (webUser1 == null) {
			log.error("Failed to find user by name " + userName);
			return null;
		}

		log.info("setting current user to " + webUser1.getUserName());

		setCurrentWebUser(webUser1);

		currentAddress = new Address();

		// TODO: Use browser location to determine this.
		currentAddress.setCountry(Country.UNITED_STATES);

		cleanAddressList();

		return USER_EDIT_URI;
	}

	/**
	 * Get ready for adding a new user.
	 *
	 * @return      Navigation URI.
	 */
	public String newUser() {

		setCurrentWebUser(new WebUser());

		currentAddress = new Address();

		// TODO: Use browser location to determine this.
		currentAddress.setCountry(Country.UNITED_STATES);

		cleanAddressList();
		userViewMode = UserViewMode.ADMIN;
		inEditMode = Boolean.TRUE;
		newUserMode = Boolean.TRUE;
		return USER_EDIT_URI;
	}

	/**
	 * Make sure all the addresses list are complete.
	 * Otherwise, remove them.
	 */
	private void cleanAddressList() {
		boolean removedAddress = false;

		if (currentWebUser.getAddressList() != null) {
			for (Address address: currentWebUser.getAddressList()) {
				if (!address.isComplete() || address.getToBeDeleted()) {
					currentWebUser.getAddressList().remove(address);
					removedAddress = true;
				}
			}

			if (removedAddress) {
				saveUser();
			}
		}
	}

	/**
	 * Get the user record ready to edit. Switch to edit mode.
	 */
	public void editUser() {

		// Reload the current user record to pickup any async changes by another user.
		WebUserService userService = (WebUserService) SpringLookup.findService("webUserService");
		currentWebUser = userService.getUserByUserName(getCurrentWebUser().getUserName());

		inEditMode = Boolean.TRUE;
		newUserMode = Boolean.FALSE;
	}

	/**
	 * On valid submission, save the user's edits for the current user record and
	 * switch to view mode. Display any errors if unable to save.
	 */
	public void saveUser() {

		WebUserService userService = (WebUserService) SpringLookup.findService("webUserService");

		if (currentWebUser.getAddressList() != null && currentWebUser.getAddressList().size() > 0) {
			int index = currentWebUser.getAddressList().size();
			while (--index >= 0) {
				if (currentWebUser.getAddressList().get(index).getToBeDeleted()) {
					currentWebUser.getAddressList().remove(index);
				}
			}
		}

		currentWebUser.setFailedLoginAttempts(0);
		userService.updateUser(currentWebUser);

		// Reload current user from db.
		currentWebUser = userService.getUserByUserName(currentWebUser.getUserName());

		if (userTableModel != null) {
			userTableModel.setReloadData(true);
		}

		if (UserViewMode.ADMIN.equals(userViewMode)) {
			// Admin has updated a user account.
			getAuditLogService().recordEvent(getCurrentWebUser(), webUserSession.getWebUser(), WebEvent.EVENT_PROFILE_UPDATED);
		}
		else {
			getAuditLogService().recordEvent(getCurrentWebUser(), WebEvent.EVENT_PROFILE_UPDATED);
		}

		inEditMode = Boolean.FALSE;
		newUserMode = Boolean.FALSE;
	}

	/**
	 * The user cancelled their changes, reload content of current user from db. Switch
	 * to view mode.
	 */
	public String cancelEdit() {

		String returnUri = null;
		inEditMode = Boolean.FALSE;

		if (!newUserMode)
		{
			// Reload the current user record to pickup any async changes by another user.
			WebUserService userService = (WebUserService) SpringLookup.findService("webUserService");
			setCurrentWebUser(userService.getUserByUserName(getCurrentWebUser().getUserName()));
		}
		else {
			newUserMode = Boolean.FALSE;
			returnUri = USER_LIST_URI;
		}

		return returnUri;
	}

	/**
	 * The user cancelled their changes, reload content of current user from db. Switch
	 * to view mode.
	 */
	public String deleteUser() {

		try {
			// Reload the current user record to pickup any async changes by another user.
			WebUserService userService = (WebUserService) SpringLookup.findService("webUserService");
			userService.removeUser(getCurrentWebUser());
			getAuditLogService().recordEvent(getCurrentWebUser(), webUserSession.getWebUser(),  WebEvent.EVENT_USER_REMOVED);

			setCurrentWebUser(userService.getUserByUserName(getCurrentWebUser().getUserName()));
		}
		catch (Exception e) {
			log.error("Failed to delete user", e);
		}

		if (userTableModel != null) {
			userTableModel.setReloadData(true);
		}

		return USER_LIST_URI;
	}

	/**
	 * Gets backing bean ready to support the addition of a new address.
	 */
	public void prepareToAddAddress() {
		currentAddress = new Address();
		currentAddress.setIsNew(true);
		Faces.navigate(USER_ADDRESS_EDIT_URI);
	}

	/**
	 * Add the current address to the user's list. If this is the first
	 * address, create the address list.
	 */
	public void saveAddress() {

		if (currentAddress.getIsNew() != null && currentAddress.getIsNew()) {
			if (currentWebUser.getAddressList() == null) {
				currentWebUser.setAddressList(new ArrayList<>());
			}
			currentAddress.setIsNew(false);
			currentWebUser.getAddressList().add(currentAddress);
		}

		clearAddress();
		Faces.navigate(USER_EDIT_URI);
	}

	public void removeAddress(Address address) {

		address.setToBeDeleted(Boolean.TRUE);
	}

	public void undoRemoveAddress(Address address) {

		address.setToBeDeleted(Boolean.FALSE);
	}

	public void editAddress(Address address) {
		currentAddress = address;
		Faces.navigate(USER_ADDRESS_EDIT_URI);
	}

	public void clearAddress() {
		currentAddress = new Address();
		Faces.navigate(USER_EDIT_URI);
	}

	public Boolean getCanAddAddress() {
		if (currentWebUser != null && currentWebUser.getAddressList() != null && currentWebUser.getAddressList().size() >= MAX_ADDRESSES) {
			return Boolean.FALSE;
		}

		return Boolean.TRUE;
	}

	public Boolean getHasAtLeastOneAddress() {
		if (currentWebUser != null && currentWebUser.getAddressList() != null && currentWebUser.getAddressList().size() > 0) {
			return Boolean.TRUE;
		}

		return Boolean.FALSE;
	}

	public Boolean getInEditMode()
	{
		return inEditMode;
	}

	public Boolean getNewUserMode()
	{
		return newUserMode;
	}

	public UserViewMode getUserViewMode()
	{
		return userViewMode;
	}

	public WebUser getCurrentWebUser()
	{
		return currentWebUser;
	}

	public void setCurrentWebUser(WebUser currentWebUser)
	{
		this.currentWebUser = currentWebUser;
	}

	/**
	 * Formats an address into a single String
	 *
	 * @return   Address
	 */
	public String formatAddress(Address address) {

		try {
			ResourceBundle resourceBundle = resourceBundleUtil.getResourceBundle(ResourceBundleUtil.USERS);

			if (address != null && address.getCountry() != null && address.getState() != null) {
				String countryLabel = resourceBundle.getString(address.getCountry().getLabel());
				String stateProvinceLabel = resourceBundle.getString(address.getState().getLabel());
				return StringFormatUtil.formatSingleLine(address, countryLabel, stateProvinceLabel);
			}
		}
		catch (Exception e) {
			log.error("Failed to format address.", e);
		}

		return null;
	}

	public Address getCurrentAddress()
	{
		return currentAddress;
	}

	public void setCurrentAddress(Address address)
	{
		if (address != null && address.getAddressLabel() != null && address.getAddressLabel().trim().length() > 0) {
			log.info("setCurrentAddress cur addr "+ address.getAddressLabel()+": " +address.getStreet1());
			this.currentAddress = address;
		}
	}

	/**
	 * Used by validator to when checking for duplicates.
	 *
	 * @return      Current email address in question or null.
	 */
	public String getCurrentEmailEditEmailAddress()
	{
		if (inEditMode && userViewMode.equals(UserViewMode.ADMIN) && currentWebUser != null) {
			return currentWebUser.getEmailAddress();
		}
		return null;
	}

	private AuditLogService getAuditLogService() {
		return (AuditLogService)SpringLookup.findService("auditLogService");
	}
}