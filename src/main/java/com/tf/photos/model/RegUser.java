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

import java.io.Serializable;

import javax.enterprise.context.RequestScoped;
import javax.inject.Named;

/**
 * Simple Java Bean to model a registered user.
 *
 * User: Heather
 * Date: 6/23/13
 */
@Named
@RequestScoped
public class RegUser implements Serializable{
    private static final long serialVersionUID = -2669522971307976086L;

    public RegUser(){}

    private String userName;
    private String password;
    private String password2;
    private String emailAddress;
    private String activationCode;

	private SecurityQuestion securityQuestion1 = SecurityQuestion.NICKNAME;
	private String securityAnswer1;
	private SecurityQuestion securityQuestion2 = SecurityQuestion.CHILDHOOD_PHONE;
	private String securityAnswer2;

    public String getUserName() { return userName; }

    public void setUserName(String userName) { this.userName = userName; }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword2() {
        return password2;
    }

    public void setPassword2(String password2) {
        this.password2 = password2;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

	public String getActivationCode() { return activationCode; 	}

	public void setActivationCode(String activationCode) { this.activationCode = activationCode; }

	public SecurityQuestion getSecurityQuestion1()
	{
		return securityQuestion1;
	}

	public void setSecurityQuestion1(SecurityQuestion securityQuestion1)
	{
		this.securityQuestion1 = securityQuestion1;
	}

	public String getSecurityAnswer1()
	{
		return securityAnswer1;
	}

	public void setSecurityAnswer1(String securityAnswer1)
	{
		this.securityAnswer1 = securityAnswer1;
	}

	public SecurityQuestion getSecurityQuestion2()
	{
		return securityQuestion2;
	}

	public void setSecurityQuestion2(SecurityQuestion securityQuestion2)
	{
		this.securityQuestion2 = securityQuestion2;
	}

	public String getSecurityAnswer2()
	{
		return securityAnswer2;
	}

	public void setSecurityAnswer2(String securityAnswer2)
	{
		this.securityAnswer2 = securityAnswer2;
	}

}
