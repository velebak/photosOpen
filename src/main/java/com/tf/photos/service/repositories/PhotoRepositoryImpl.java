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
import org.springframework.stereotype.Repository;

import com.tf.photos.model.Photo;

/**
 * Saves and retrieves photos from/to database.
 *
 * @author Heather Stevens on 6/14/2014
 */
@Repository("photoRepository")
public class PhotoRepositoryImpl implements PhotoRepository, Serializable
{
	private static final long serialVersionUID = 157708001131217388L;

	@Autowired
	protected MongoOperations mongoTemplate;

	public PhotoRepositoryImpl() {
	}

	@Override
	public Photo getPhotoById(String photoId)
	{
		return mongoTemplate.findById(photoId, Photo.class);
	}

	@Override
	public void savePhoto(Photo photo)
	{
		mongoTemplate.save(photo);
	}

	@Override
	public List<Photo> findPhotos()
	{
		return mongoTemplate.findAll(Photo.class);
	}

	@Override
	public void removePhoto(Photo photo) {
		mongoTemplate.remove(photo);
	}
}
