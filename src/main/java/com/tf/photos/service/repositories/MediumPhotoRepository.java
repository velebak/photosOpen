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

import com.tf.photos.model.MediumPhoto;

/**
 * Persists medium sized photos to and from database.
 *
 * @author  Heather Stevens
 * Date 6/14/2014
 */
public interface MediumPhotoRepository
{
	MediumPhoto getMediumPhotoById(String mediumPhotoId);
	void saveMediumPhoto(MediumPhoto mediumPhoto);
	void removeMediumPhoto(String mediumPhotoId);
}
