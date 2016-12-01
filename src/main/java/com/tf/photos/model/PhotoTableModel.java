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
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import javax.inject.Named;

import org.joda.time.DateTime;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tf.photos.backing.filter.PhotoListFilter;
import com.tf.photos.service.ImageService;
import com.tf.photos.service.util.SpringLookup;
import com.tf.photos.util.DeepSorter;

/**
 * @author Heather Stevens on 5/4/2014.
 *
 * Used to model user for data table.
 */
@Named
public class PhotoTableModel extends LazyDataModel<Photo> implements Serializable {

	private static final Logger log = LoggerFactory.getLogger(PhotoTableModel.class);
	private static final long serialVersionUID = -2534139844969943252L;

	private PhotoListFilter photoListFilter;

	private List<Photo> photoSource;
	private Map<String, Photo> photoMap;
	private boolean reloadData = true;
	private List<String> userNamesInList = new ArrayList<>();
	private List<String> yearsInList = new ArrayList<>();

	public PhotoTableModel() {
	}

	@Override
	public Photo getRowData(String rowKey) {

		Photo photo = null;

		try {
			photo = photoMap.get(rowKey);

			if (photo == null) {
				ImageService imageService = (ImageService) SpringLookup.findService("imageService");
				photo = imageService.getPhotoById(rowKey);

				if (photo != null) {
					photoMap.put(photo.getId(), photo);
					reloadData = true;
				}
			}
		}
		catch (Exception e) {
			log.error("Failed to find photo " + rowKey, e);
		}

		return photo;
	}

	@Override
	public Object getRowKey(Photo photo) {
		return photo.getId();
	}

	/**
	 * Loads the web user data, filters and sorts the list. Filtering and sorting are case insensitive.
	 *
	 * @param first         First page to load.
	 * @param pageSize      Number of rows per page.
	 * @param sortField     How rows are to be sorted.
	 * @param sortOrder     Sort order of rows.
	 * @param filters       Map of filters
	 * @return              Row data to display.
	 */
	@Override
	public List<Photo> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String,Object> filters) {

		List<Photo> displayContent = new ArrayList<>();

		if (reloadData) {
			log.info("loading new data into photo list.");

			loadPhotos();
		}

		photoListFilter.filter(photoSource, displayContent, filters);

		//sort
		if(sortField != null) {
			Collections.sort(displayContent, new DeepSorter<>(sortField, sortOrder));
		}

		//rowCount
		int dataSize = displayContent.size();
		this.setRowCount(dataSize);

		return photoListFilter.filterToOnePage(displayContent, pageSize, first);
	}

	/**
	 * Sets flag to reload data from data base the next time the display list is requested.
	 *
	 * @param reloadData        Reload content from database flag.
	 */
	public void setReloadData(boolean reloadData) {
		this.reloadData = reloadData;
	}

	/**
	 * Load photos from database.
	 */
	public void loadPhotos() {
		ImageService imageService = (ImageService) SpringLookup.findService("imageService");
		photoSource = imageService.findPhotos();
		photoMap = new HashMap<>(photoSource.size());
		HashSet<String> userNameSet = new LinkedHashSet<>();
		HashSet<String> yearSet = new LinkedHashSet<>();

		for (Photo photo: photoSource) {
			photoMap.put(photo.getId(), photo);
			userNameSet.add(photo.getUserName());

			if (photo.getTaken() != null) {
				DateTime takenDate = new DateTime(photo.getTaken().getTime());
				yearSet.add("" + takenDate.getYear());
			}
			else {
				yearSet.add("");
			}
		}

		log.debug("found user names " +userNameSet.size());
		userNamesInList.clear();
		userNamesInList.addAll(userNameSet);
		Collections.sort(userNamesInList);

		yearsInList.clear();
		yearsInList.addAll(yearSet);
		Collections.sort(yearsInList);

		reloadData = false;
	}

	public List<String> getUserNamesInList() {
		return userNamesInList;
	}

	public List<String> getYearsInList() {
		return yearsInList;
	}

	public void setPhotoListFilter(PhotoListFilter photoListFilter) {
		this.photoListFilter = photoListFilter;
	}
}
