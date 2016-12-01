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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import com.tf.photos.model.LargePhoto;

/**
 * Saves and retrieves full sized/large photos from/to database.
 *
 * @author Heather Stevens on 6/14/2014
 */
@Repository("largePhotoRepository")
public class LargePhotoRepositoryImpl implements LargePhotoRepository, Serializable
{
	private static final long serialVersionUID = -2421162070277164257L;
	@Autowired
	protected MongoOperations mongoTemplate;

	public LargePhotoRepositoryImpl() {
	}

	@Override
	public LargePhoto getLargePhotoById(String largePhotoId)
	{
		return mongoTemplate.findById(largePhotoId, LargePhoto.class);
	}

	@Override
	public void saveLargePhoto(LargePhoto largePhoto)
	{
		mongoTemplate.save(largePhoto);
	}

	@Override
	public void removeLargePhoto(String largePhotoId) {

		Query query = new Query(Criteria.where("id").is(largePhotoId));
		mongoTemplate.remove(query, LargePhoto.class);
	}
}
