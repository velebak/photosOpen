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
import java.util.ArrayList;
import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author Heather Stevens
 * Date: 6/22/13
 * Time: 10:49 AM
 */
@Document
public class WebUser implements Serializable {
    private static final long serialVersionUID = -6603479926257222214L;

    public WebUser() {}

    @Id
    private String id;

    public String userName;
    private String password;
    private String emailAddress;
    private SecurityQuestion securityQuestion1;
    private byte[] securityAnswer1;
	private SecurityQuestion securityQuestion2;
	private byte[] securityAnswer2;
	private String resetCode;
	private String phone;
	public String firstName;
	public String lastName;
	public UserType userType;
	public UserStatus userStatus;
    private String lockedReason;
    private Date lockedDate;
	private Integer failedLoginAttempts;
    private String activationCode;
    private Date activationEmailSent;
    private Date activationExpiration;
    private Date lastLoginDate;
    private Date createdDate;
    private String remoteAddress;
	private ArrayList<Address> addressList;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public SecurityQuestion getSecurityQuestion1() {
        return securityQuestion1;
    }

    public void setSecurityQuestion1(SecurityQuestion securityQuestion1) {
        this.securityQuestion1 = securityQuestion1;
    }

    public byte[] getSecurityAnswer1() {
        return securityAnswer1;
    }

    public void setSecurityAnswer1(byte[] securityAnswer1) {
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

	public byte[] getSecurityAnswer2()
	{
		return securityAnswer2;
	}

	public void setSecurityAnswer2(byte[] securityAnswer2)
	{
		this.securityAnswer2 = securityAnswer2;
	}

	public String getResetCode()
	{
		return resetCode;
	}

	public void setResetCode(String resetCode)
	{
		this.resetCode = resetCode;
	}

	public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public UserType getUserType() {
        return userType;
    }

    public void setUserType(UserType userType) {
        this.userType = userType;
    }

    public String getActivationCode() {
        return activationCode;
    }

    public void setActivationCode(String activationCode) {
        this.activationCode = activationCode;
    }

    public UserStatus getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(UserStatus userStatus) {
        this.userStatus = userStatus;
    }

    public String getLockedReason() {
        return lockedReason;
    }

    public void setLockedReason(String lockedReason) {
        this.lockedReason = lockedReason;
    }

    public Date getLockedDate() {
        return lockedDate;
    }

    public void setLockedDate(Date lockedDate) {
        this.lockedDate = lockedDate;
    }

	public Integer getFailedLoginAttempts()	{ return failedLoginAttempts; }

	public void setFailedLoginAttempts(Integer failedLoginAttempts) { this.failedLoginAttempts = failedLoginAttempts; }

	public Date getActivationEmailSent()
	{
		return activationEmailSent;
	}

	public void setActivationEmailSent(Date activationEmailSent)
	{
		this.activationEmailSent = activationEmailSent;
	}

	public Date getActivationExpiration() {
        return activationExpiration;
    }

    public void setActivationExpiration(Date activationExpiration) {
        this.activationExpiration = activationExpiration;
    }

    public Date getLastLoginDate() {
        return lastLoginDate;
    }

    public void setLastLoginDate(Date lastLoginDate) {
        this.lastLoginDate = lastLoginDate;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public String getRemoteAddress() {
        return remoteAddress;
    }

    public void setRemoteAddress(String remoteAddress) {
        this.remoteAddress = remoteAddress;
    }

	public ArrayList<Address> getAddressList()
	{
		return addressList;
	}

	public void setAddressList(ArrayList<Address> addressList)
	{
		this.addressList = addressList;
	}
}
