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
 * Country enum to support select one menus and country
 * dependent logic.
 *
 * @author Heather Stevens on 1/4/14.
 */
public enum Country
{
	CANADA("country.Canada", "province", "*9* 9*9", "[A-Z]\\d[A-Z] \\d[A-Z]\\d"),
	MEXICO("country.Mexico", "state", "99999", "\\d{5}"),
	UNITED_KINGDOM("country.UnitedKingdom", "state", "99999", "\\d{5}"),
	UNITED_STATES("country.UnitedStates", "state", "99999?-9999", "^\\d{5}-?\\d{4})?$");

	private String label;
	private String stateOrProvince;
	private String zipCodeMask;
	private String zipCodeRegex;

	private Country(String label, String stateOrProvince, String zipCodeMask, String zipCodeRegex) {
		this.label = label;
		this.stateOrProvince = stateOrProvince;
		this.zipCodeMask = zipCodeMask;
		this.zipCodeRegex = zipCodeRegex;
	}

	public String getLabel() {
		return label;
	}

	public String getStateOrProvince() {
		return stateOrProvince;
	}

	public String getZipCodeMask() {
		return zipCodeMask;
	}

	public String getZipCodeRegex()
	{
		return zipCodeRegex;
	}
}
