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

import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tf.photos.model.AuditLog;
import com.tf.photos.service.util.ResourceBundleUtil;

/**
 * Support advanced filtering of data tables.
 *
 * @author Heather Stevens
 *         Date 7/12/2014
 */
@Named
@SessionScoped
public class AuditListFilter extends DataListFilter<AuditLog> implements Serializable {
	private static final long serialVersionUID = -4669483284488669061L;
	private static final Logger log = LoggerFactory.getLogger(AuditListFilter.class);
	private static final String EVENT_DATE = "eventDate";

	@Inject
	private ResourceBundleUtil resourceBundleUtil;

	private Date maxDate;

	public AuditListFilter() {
	}

	/**
	 * Prepares to filter photos. Sets all filters to their initial state.
	 */
	public void initializeFilters()
	{
		maxDate = new Date();
	}

	/**
	 * Filters content based on passed in filters.
	 *
	 * @param sourceList            Full list of results from db.
	 * @param displayContent        Content to eventually display.
	 * @param filters               Filters to be applied.
	 */
	public void filter(List<AuditLog> sourceList, List<AuditLog> displayContent, Map<String,Object> filters) {

		//log.info("filtering, original size " +sourceList.size() + " filter property map size " + filters.size());

		for(AuditLog auditLog : sourceList) {
			boolean match = true;
			for (String filterProperty : filters.keySet()) {

				switch (filterProperty) {
					case EVENT_DATE:
						match = filterByDate(auditLog, filterProperty, filters.get(filterProperty));
						break;

					default:
						match = filterByStringType(auditLog, filterProperty, filters.get(filterProperty));
						break;
				}

				if (!match) break;
			}

			if (match) {
				displayContent.add(auditLog);
			}
		}
	}

	public Date getMaxDate() {
		return maxDate;
	}
}
