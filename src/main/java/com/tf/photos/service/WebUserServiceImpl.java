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
package com.tf.photos.service;

import java.io.Serializable;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tf.photos.model.WebUser;
import com.tf.photos.service.repositories.WebUserRepository;

/**
 * @author Heather Stevens
 * Date: 6/23/13
 */
@Service("webUserService")
public class WebUserServiceImpl implements WebUserService, Serializable {
    private static final long serialVersionUID = -6330914784517791396L;

	@Autowired
    private WebUserRepository webUserRepository;

	public WebUserServiceImpl() {
	}

	@Override
	public WebUser getUserById(String webUserId) {

		return webUserRepository.getUserById(webUserId);
	}

    @Override
    public WebUser getUserByEmailAddress(String emailAddress) {
        return webUserRepository.getUserByEmailAddress(emailAddress);
    }

    @Override
    public  WebUser getUserByUserName(String userName) {

	    return webUserRepository.getUserByUserName(userName);
    }

    @Override
    public void addUser(WebUser webUser) {
        webUserRepository.saveWebUser(webUser);
    }

    @Override
    public void updateUser(WebUser webUser) {
        webUserRepository.saveWebUser(webUser);
    }

    @Override
    public List<WebUser> findUsers() {
        return webUserRepository.findUsers();
    }

	@Override
	public void removeUser(WebUser webUser) {
		webUserRepository.removeUser(webUser);
	}
}
