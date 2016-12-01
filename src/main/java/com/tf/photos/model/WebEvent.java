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
package com.tf.photos.model;

/**
 * WebEvent enum used to track web user action events.
 *
 * @author Heather
 * Date: 6/29/2014
 */
public enum WebEvent
{
	EVENT_USER_ADMIN_LOGIN("webEvent.userAdminLogin"),
	EVENT_USER_LOGIN("webEvent.userLogin"),
	EVENT_USER_LOGOUT("webEvent.userLogout"),
	EVENT_USER_REGISTERED("webEvent.userRegistered"),
	EVENT_USER_NEW_ACTIVATION_REQUEST("webEvent.userNewActivationRequest"),
	EVENT_USER_ACTIVATION_FAILED("webEvent.userActivationFailed"),
	EVENT_USER_ACTIVATED("webEvent.userActivated"),
	EVENT_USER_LOGIN_FAILED("webEvent.userLoginFailed"),
	EVENT_USER_LOCKED("webEvent.userLocked"),
	EVENT_USER_REMOVED("webEvent.userRemoved"),
	EVENT_PROFILE_UPDATED("webEvent.profileUpdated"),
	EVENT_SECURITY_PROFILE_UPDATED("webEvent.securityProfileUpdated"),

	EVENT_REMOTE_ADDRESS_BLOCKED("webEvent.remoteAddressBlocked"),
	EVENT_REMOTE_ADDRESS_UNBLOCKED("webEvent.remoteAddressUnblocked"),
	EVENT_REMOTE_ADDRESS_SAFE("webEvent.remoteAddressSafe"),
	EVENT_REMOTE_ADDRESS_UNSAFE("webEvent.remoteAddressUnsafe"),

	EVENT_ERROR_OCCURRED("webEvent.errorOccurred"),

	EVENT_PHOTO_UPLOADED("webEvent.photoUploaded"),
	EVENT_PHOTO_REMOVED("webEvent.photoRemoved"),
	EVENT_PHOTO_UPDATED("webEvent.photoUpdated"),

	EVENT_VIEW_PHOTOS("webEvent.viewPhotos"),
	EVENT_VIEW_HOME("webEvent.viewHome"),
	EVENT_VIEW_USERS("webEvent.viewUsers"),
	EVENT_VIEW_PROFILE("webEvent.viewProfile");

	private String label;

	private WebEvent(String label) {
		this.label = label;
	}

	public String getLabel() {
		return label;
	}
}
