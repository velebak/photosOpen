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

import com.tf.photos.model.KnownBad;

/**
 * Saves and retrieves known bad ip addresses to and from database.
 *
 * @author Heather Stevens on 6/28/14
 */
@Repository("knownBadRepository")
public class KnownBadRepositoryImpl implements KnownBadRepository
{
	private static final Logger log = LoggerFactory.getLogger(KnownBadRepositoryImpl.class);

	@Autowired
	protected MongoOperations mongoTemplate;

	@Override
	public KnownBad getKnownBadById(String knownBadId)
	{
		return mongoTemplate.findById(knownBadId, KnownBad.class);
	}

	@Override
	public void saveKnownBad(KnownBad knownBad)
	{
		mongoTemplate.save(knownBad);
	}

	@Override
	public void removeKnownBad(String knownBadId) {

		try {
			Query query = new Query(Criteria.where("id").is(knownBadId));
			mongoTemplate.remove(query, KnownBad.class);
		}
		catch (Exception e) {
			log.warn("Unable to remove known bad item with id " + knownBadId);
		}
	}

	@Override
	public KnownBad getKnownBadByRemoteAddress(String remoteAddress) {
		Query searchUserQuery = new Query(Criteria.where("remoteAddress").is(remoteAddress));
		return mongoTemplate.findOne(searchUserQuery, KnownBad.class);
	}

	@Override
	public List<KnownBad> findAllKnownBad() {
		return mongoTemplate.findAll(KnownBad.class);
	}
}
