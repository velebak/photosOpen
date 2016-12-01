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

import com.tf.photos.model.LargePhoto;
import com.tf.photos.model.Photo;

import java.util.List;

/**
 * @author Heather Stevens on 6/1/2014.
 */
public interface ImageService
{
	Photo processSavePhoto(Photo photo);
	Photo getPhotoById(String photoId);
	LargePhoto getLargePhotoById(String photoId);
	void savePhoto(Photo photo);
	List<Photo> findPhotos();
	void removePhoto(Photo photo);
}
