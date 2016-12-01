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

import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

/**
 * User: Heather
 * Date: 6/23/13
 */
@Named
@SessionScoped
public class WebUserSession implements Serializable{

    private static final long serialVersionUID = -2593368679480873571L;
    public WebUserSession(){}

    private WebUser webUser;
    private Boolean loggedIn = Boolean.FALSE;

	public WebUser getWebUser()
	{
		if (webUser == null) {
			webUser = new WebUser();
		}

		return webUser;
	}

	public void setWebUser(WebUser webUser)
	{
		this.webUser = webUser;
	}

	public Boolean getLoggedIn() {
		return loggedIn;
	}

    public void setLoggedIn(Boolean loggedIn) {
	    this.loggedIn = loggedIn;
    }
}