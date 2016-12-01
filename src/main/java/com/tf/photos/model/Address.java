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

/**
 * @author Heather Stevens
 * Date: 6/22/13
 */
public class Address implements Serializable {
    private static final long serialVersionUID = 4953114751942291862L;

    public Address() {
	    country = Country.UNITED_STATES;
    }

	private String addressLabel;
	private Boolean isNew;
	private Boolean toBeDeleted = Boolean.FALSE;
    private String street1;
    private String street2;
    private String city;
    private StateProvince state;
    private String zipCode;
    private Country country;
    private Double longitude;
    private Double latitude;

	public Boolean isComplete() {
		if (addressLabel == null || street1 == null || city == null || country == null || state == null || zipCode == null) {
			return Boolean.FALSE;
		}

		return Boolean.TRUE;
	}

	public String getAddressLabel()
	{
		return addressLabel;
	}

	public void setAddressLabel(String addressLabel)
	{
		this.addressLabel = addressLabel;
	}

	public Boolean getIsNew()
	{
		return isNew;
	}

	public void setIsNew(Boolean isNew)
	{
		this.isNew = isNew;
	}

	public Boolean getToBeDeleted()
	{
		return toBeDeleted;
	}

	public void setToBeDeleted(Boolean toBeDeleted)
	{
		this.toBeDeleted = toBeDeleted;
	}

	public String getStreet1() {
        return street1;
    }

    public void setStreet1(String street1) {
        this.street1 = street1;
    }

    public String getStreet2() {
        return street2;
    }

    public void setStreet2(String street2) {
        this.street2 = street2;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public StateProvince getState() {
        return state;
    }

    public void setState(StateProvince state) {
        this.state = state;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }
}
