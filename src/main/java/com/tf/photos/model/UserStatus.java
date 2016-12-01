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
 * Enum to track the current status of users.
 *
 * @author Heather Stevens
 * Date: 6/22/13
 */
public enum UserStatus {

    ANON("anonymous"),
	NEW("new"),
	ACTIVE("active"),
	LOCKED("locked"),
	RESET_REQUESTED("resetRequested"),
	RESET_MODE("resetMode"),
	BLOCKED("blocked");

	private String label; // Used with resource bundle.

	UserStatus(String label) {
		this.label = label;
	}

	public String getLabel() {
		return this.label;
	}

}
