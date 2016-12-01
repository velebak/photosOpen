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
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Heather Stevens on 11/26/16.
 */
public class DataListFilter<T> implements Serializable {

	private static final long serialVersionUID = -1734211798021308577L;

	private static final String MM_DD_YY_PATTERN = "MM/dd/yy";
	private static final Logger log = LoggerFactory.getLogger(DataListFilter.class);

	public DataListFilter() {
	}

	/**
	 * Default String contains filter processing.
	 *
	 * @param item          Photo to consider for inclusion.
	 * @param filterProperty Filter property name used to determine which photo field to compare with.
	 * @param filterValue    Value of the filter.
	 * @return True if photo should be included in the resultant list to display.
	 */
	protected boolean filterByDate(T item, String filterProperty, Object filterValue) {
		FastDateFormat dateInstance = FastDateFormat.getInstance(MM_DD_YY_PATTERN);

		try {
			Object fieldValue = PropertyUtils.getNestedProperty(item, filterProperty);
			Date theDate = (Date) fieldValue;
			String dateStr = dateInstance.format((Date) fieldValue);

			if (filterValue == null || (dateInstance.format((Date) fieldValue).contains(filterValue.toString()))) {
				return true;
			}
		} catch (Exception e) {
			log.error("Failed to filter. ", e);
		}

		return false;
	}

	/**
	 * Default String contains filter processing.
	 *
	 * @param item          Photo to consider for inclusion.
	 * @param filterProperty Filter property name used to determine which photo field to compare with.
	 * @param filterValue    Value of the filter.
	 * @return True if photo should be included in the resultant list to display.
	 */
	protected boolean filterByStringType(T item, String filterProperty, Object filterValue) {

		try {
			Object fieldValue = PropertyUtils.getNestedProperty(item, filterProperty);

			if (filterValue == null || (fieldValue != null && fieldValue.toString().toLowerCase().contains(filterValue.toString().toLowerCase()))) {
				return true;
			}
		} catch (Exception e) {
			log.error("Failed to filter. ", e);
		}

		return false;
	}

	/**
	 * Filters content based on passed in filters.
	 *
	 * @param sourceList            Full list of results from db.
	 * @param displayContent        Content to eventually display.
	 * @param filters               Filters to be applied.
	 */
	public void filter(List<T> sourceList, List<T> displayContent, Map<String,Object> filters) {

		//log.info("filtering, original size " +sourceList.size() + " filter property map size " + filters.size());

		for(T item : sourceList) {
			boolean match = true;
			for (String filterProperty : filters.keySet()) {
				match = filterByStringType(item, filterProperty, filters.get(filterProperty));
				if (!match) break;
			}

			if (match) {
				displayContent.add(item);
			}
		}
	}

	/**
	 * Basically, introduces pagination. This should be moved to the database.
	 *
	 * @param source        Full source list after filtering.
	 * @param pageSize      Size of pages.
	 * @param first         Offset to start page.
	 * @return              One page of items.
	 */
	public List<T> filterToOnePage(List<T> source, int pageSize, int first) {

		int dataSize = source.size();

		//paginate
		if(dataSize > pageSize) {
			try {
				return source.subList(first, first + pageSize);
			}
			catch(IndexOutOfBoundsException e) {
				return source.subList(first, first + (dataSize % pageSize));
			}
		}
		else {
			return source;
		}
	}
}
