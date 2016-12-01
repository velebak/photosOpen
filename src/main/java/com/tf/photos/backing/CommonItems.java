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

import java.util.ArrayList;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;

import com.tf.photos.model.Country;
import com.tf.photos.model.SecurityQuestion;
import com.tf.photos.model.StateProvince;
import com.tf.photos.model.UserStatus;
import com.tf.photos.model.UserType;

/**
 * Used to access common lists used with multiple aspects of the application.
 *
 * @author Heather Stevens on 1/4/14.
 */
@Named
@ApplicationScoped
public class CommonItems {

	public Country[] getCountries() {
		return Country.values();
	}

	/**
	 * Get the state or provinces for country.
	 */
	public StateProvince[] getStatesProvinces(Country country) {


		ArrayList<StateProvince> list = new ArrayList<>();
		for (StateProvince stateProvince: StateProvince.values()) {
			if (country == null || country.equals(stateProvince.getCountry())) {
				list.add(stateProvince);
			}
		}

		return list.toArray(new StateProvince[list.size()]);
	}

	/**
	 * Get the user types that can be selected.
	 */
	public UserType[] getUserTypes() {

		ArrayList<UserType> list = new ArrayList<>();
		for (UserType userType : UserType.values()) {
			if (userType.getCanSelect()) {
				list.add(userType);
			}
		}

		return list.toArray(new UserType[list.size()]);
	}

	/**
	 * Get all the user status values.
	 */
	public UserStatus[] getUserStatuses() {
		return UserStatus.values();
	}

	/**
	 * Get all the security questions.
	 */
	public SecurityQuestion[] getSecurityQuestions(SecurityQuestion alreadyUsed) {

		if (alreadyUsed == null) {
			return SecurityQuestion.values();
		}

		SecurityQuestion securityQuestionList[] = new SecurityQuestion[SecurityQuestion.values().length - 1];
		int index = 0;
		for(SecurityQuestion securityQuestion: SecurityQuestion.values()) {
			if (securityQuestion != alreadyUsed) {
				securityQuestionList[index++] = securityQuestion;
			}
		}

		return securityQuestionList;
	}
}
