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
 * @author Heather Stevens
 * Date: 6/22/13
 */
public enum UserType {

	ADVERTISER("advertiser", Boolean.TRUE),
	ANONYMOUS("guest", Boolean.FALSE),
	BLOGGER("blogger", Boolean.TRUE),
    BUYER("buyer", Boolean.TRUE),
	REGISTERED("registered", Boolean.TRUE),
	SELLER("seller", Boolean.TRUE),
	ADMIN("admin", Boolean.TRUE);

	private String label; // Used with resource bundle.
	private Boolean canSelect;

	UserType(String label, Boolean canSelect) {
		this.label = label;
		this.canSelect = canSelect;
	}

	public String getLabel() {
		return this.label;
	}

	public Boolean getCanSelect()
	{
		return canSelect;
	}
}
