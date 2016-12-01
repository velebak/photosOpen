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


package com.tf.photos.backing.filter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tf.photos.model.FileSizeRange;
import com.tf.photos.model.Photo;
import com.tf.photos.service.util.ResourceBundleUtil;

/**
 * Support advanced filtering of data tables.
 *
 * @author Heather Stevens
 *         Date 7/12/2014
 */
@Named
@SessionScoped
public class PhotoListFilter extends DataListFilter<Photo> implements Serializable {
	private static final long serialVersionUID = -4669483284488669061L;
	private static final Logger log = LoggerFactory.getLogger(PhotoListFilter.class);
	private static final String SIZE = "size";
	private static final String TAKEN = "taken";
	private static final String UPLOADED = "uploaded";
	private static final String USER_NAME = "userName";

	@Inject
	private ResourceBundleUtil resourceBundleUtil;

	private Map<FileSizeRange, String> fileSizeRangeMap;
	private List<String> selectedFileSizeRangeList = null;

	private List<String> selectedUserNameList = null;
	private List<String> fullUserNameList = null;

	private List<String> selectedYearList = null;
	private List<String> fullYearList = null;

	private Date maxDate;

	public PhotoListFilter() {
	}

	/**
	 * Prepares to filter photos. Sets all filters to their initial state.
	 *
	 * @param userNamesList        List of all user names found in the full unfiltered source list of photos.
	 * @param yearList             List of years in which photos were taken.
	 *
	 */
	public void initializeFilters(List<String> userNamesList, List<String> yearList)
	{
		selectedFileSizeRangeList = null;
		selectedUserNameList = userNamesList;
		selectedYearList = yearList;
		fullUserNameList = userNamesList;
		fullYearList = yearList;
		maxDate = new Date();
	}

	/**
	 * Filters content based on passed in filters.
	 *
	 * @param sourceList            Full list of results from db.
	 * @param displayContent        Content to eventually display.
	 * @param filters               Filters to be applied.
	 */
	public void filter(List<Photo> sourceList, List<Photo> displayContent, Map<String,Object> filters) {

		//log.info("filtering, original size " +sourceList.size() + " filter property map size " + filters.size());

		for(Photo photo : sourceList) {
			boolean match = true;
			for (String filterProperty : filters.keySet()) {

				switch (filterProperty) {
					case SIZE:
						match = filterFileSize(photo, filters.get(filterProperty));
						break;
					case USER_NAME:
						match = filterUserName(photo, filters.get(filterProperty));
						break;

					case TAKEN:
						match = filterTaken(photo, filters.get(filterProperty));
						break;

					case UPLOADED:
						match = filterByDate(photo, filterProperty, filters.get(filterProperty));
						break;

					default:
						match = filterByStringType(photo, filterProperty, filters.get(filterProperty));
						break;
				}

				if (!match) break;
			}

			if (match) {
				displayContent.add(photo);
			}
		}
	}

	/**
	 * Filters photos based on file size range.
	 *
	 * @param photo         Photo to consider for inclusion.
	 * @param filterValue   Value used to determine if the filter is in use.
	 * @return              True if photo should be included in the resultant list to display.
	 */
	private boolean filterFileSize(Photo photo, Object filterValue) {

		if (filterValue == null) {
			return true;
		}

		FileSizeRange photoFileSizeRange = FileSizeRange.getEnumForFileSize(photo.getSize());
		for (String fileSizeRangeName: getSelectedFileSizeRangeList()) {
			if (fileSizeRangeName.equals(photoFileSizeRange.name())) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Filters photos based on selected user names.
	 *
	 * @param photo         Photo to consider for inclusion.
	 * @param filterValue   Value used to determine if the filter is in use.
	 * @return              True if photo should be included in the resultant list to display.
	 */
	private boolean filterUserName(Photo photo, Object filterValue) {

		if (filterValue == null) {
			return true; // This filter isn't being used.
		}

		for (String userName: getSelectedUserNameList()) {
			if (userName.equals(photo.getUserName())) {
				return true;
			}
		}

		return false; // No match
	}

	/**
	 * Filters photos based on taken date to and from range.
	 *
	 * @param photo         Photo to consider for inclusion.
	 * @param filterValue   Value used to determine if the filter is in use.
	 * @return              True if photo should be included in the resultant list to display.
	 */
	private boolean filterTaken(Photo photo, Object filterValue) {

		if (filterValue == null) {
			return true; // This filter isn't being used.
		}

		if (photo.getTaken() != null) {

			DateTime takenDate = new DateTime(photo.getTaken().getTime());

			for (String year : getSelectedYearList()) {

				if (StringUtils.isNotEmpty(year)) {

					int yearNum = Integer.parseInt(year);

					if (yearNum == takenDate.getYear()) {
						return true;
					}
				}
			}
		}
		else if (getSelectedYearList().contains("")) {
			return true;
		}

		return false; // No match
	}

	/**
	 * Lazy builds map of file size ranges.
	 *
	 * @return      File size range map with resource loaded labels.
	 */
	public Map<FileSizeRange, String> getFileSizeRangeMap() {

		if (fileSizeRangeMap == null) {

			ResourceBundle resourceBundle = resourceBundleUtil.getResourceBundle(ResourceBundleUtil.PHOTOS);

			fileSizeRangeMap = new LinkedHashMap<>(FileSizeRange.values().length);

			for (FileSizeRange fileSizeRange: FileSizeRange.values()) {
				fileSizeRangeMap.put(fileSizeRange, resourceBundle.getString(fileSizeRange.getLabel()));
			}
		}

		return fileSizeRangeMap;
	}

	/**
	 * Lazy creates list of file size range names.
	 *
	 * @return      Non null list of file sizes by enum name.
	 */
	public List<String> getSelectedFileSizeRangeList() {

		if (selectedFileSizeRangeList == null) {
			selectedFileSizeRangeList = new ArrayList<>(getFileSizeRangeMap().size());

			selectedFileSizeRangeList.addAll(getFileSizeRangeMap().keySet().stream().map(FileSizeRange::name).collect(Collectors.toList()));
		}

		return selectedFileSizeRangeList;
	}

	/**
	 * Sets the list of file size range names to be used for filtering.
	 */
	public void setSelectedFileSizeRangeList(List<String> selectedFileSizeRangeList) {
		this.selectedFileSizeRangeList = selectedFileSizeRangeList;
	}

	public List<String> getSelectedUserNameList() {
		return selectedUserNameList;
	}

	public void setSelectedUserNameList(List<String> selectedUserNameList) {
		this.selectedUserNameList = selectedUserNameList;
	}

	public List<String> getFullUserNameList() {
		return fullUserNameList;
	}

	public List<String> getSelectedYearList()
	{
		return selectedYearList;
	}

	public void setSelectedYearList(List<String> selectedYearList)
	{
		this.selectedYearList = selectedYearList;
	}

	public List<String> getFullYearList()
	{
		return fullYearList;
	}

	public void setFullYearList(List<String> fullYearList)
	{
		this.fullYearList = fullYearList;
	}

	public Date getMaxDate() {
		return maxDate;
	}
}
