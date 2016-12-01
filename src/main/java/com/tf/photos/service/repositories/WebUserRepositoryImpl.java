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
package com.tf.photos.service.repositories;

import java.io.Serializable;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import com.tf.photos.model.WebUser;

/**
 * @author Heather Stevens
 * Date: 6/23/13
 * Time: 12:22 PM
 */
@Repository("userRepository")
public class WebUserRepositoryImpl implements WebUserRepository, Serializable {

    private static final long serialVersionUID = 7047267517614969947L;

    @Autowired
    protected MongoOperations mongoTemplate;

	/**
	 * Find user by primary key.
	 *
	 * @param webUserId     Primary key.
	 * @return              Web user if found.
	 */
	@Override
	public WebUser getUserById(String webUserId) {
		return mongoTemplate.findById(webUserId, WebUser.class);
	}
    @Override
    public WebUser getUserByUserName(String userName) {
        Query searchUserQuery = new Query(Criteria.where("userName").is(userName));
        return mongoTemplate.findOne(searchUserQuery, WebUser.class);
    }

    @Override
    public WebUser getUserByEmailAddress(String emailAddress) {
        Query searchUserQuery = new Query(Criteria.where("emailAddress").is(emailAddress));
        return mongoTemplate.findOne(searchUserQuery, WebUser.class);
    }

    @Override
    public void saveWebUser(WebUser webUser) {
        mongoTemplate.save(webUser, "webUser");
    }

    @Override
    public List<WebUser> findUsers() {
        return mongoTemplate.findAll(WebUser.class);
    }

	@Override
	public void removeUser(WebUser webUser) {
		mongoTemplate.remove(webUser);
	}

}
