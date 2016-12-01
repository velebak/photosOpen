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

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import com.tf.photos.model.KnownSafe;

/**
 * Saves and retrieves known safe ip addresses to and from database.
 *
 * @author Heather Stevens on 6/28/14
 */
@Repository("knownSafeRepository")
public class KnownSafeRepositoryImpl implements KnownSafeRepository
{
	private static final Logger log = LoggerFactory.getLogger(KnownSafeRepositoryImpl.class);

	@Autowired
	protected MongoOperations mongoTemplate;

	@Override
	public KnownSafe getKnownSafeById(String knownSafeId)
	{
		return mongoTemplate.findById(knownSafeId, KnownSafe.class);
	}

	@Override
	public void saveKnownSafe(KnownSafe knownSafe)
	{
		mongoTemplate.save(knownSafe);
	}

	@Override
	public void removeKnownSafe(String knownSafeId) {

		try {
			Query query = new Query(Criteria.where("id").is(knownSafeId));
			mongoTemplate.remove(query, KnownSafe.class);
		}
		catch (Exception e) {
			log.warn("Failed to remove known safe item with id " + knownSafeId);
		}
	}

	@Override
	public KnownSafe getKnownSafeByRemoteAddress(String remoteAddress) {
		Query searchUserQuery = new Query(Criteria.where("remoteAddress").is(remoteAddress));
		return mongoTemplate.findOne(searchUserQuery, KnownSafe.class);
	}

	@Override
	public List<KnownSafe> findAllKnownSafe() {
		return mongoTemplate.findAll(KnownSafe.class);
	}
}
