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

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;

import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;

/**
 * @author Heather Stevens
 * Date 10/22/16.
 */
@Configuration
public class MongoApplicationContextConfig extends AbstractMongoConfiguration
{
	@Override
	public String getDatabaseName() {
		return "dbName***";
	}

	@Override
	@Bean
	public Mongo mongo() throws Exception {

		List<MongoCredential> mongoCredentialList = new ArrayList<>();

		mongoCredentialList.add(MongoCredential.createCredential("dbUser", "dbName", "secretPassword".toCharArray()));

		return new MongoClient(new ServerAddress("127.0.0.1", 27017), mongoCredentialList);
	}

}
