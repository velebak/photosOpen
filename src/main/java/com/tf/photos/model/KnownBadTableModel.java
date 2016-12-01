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
import java.util.List;
import java.util.Map;

import javax.inject.Named;

import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tf.photos.backing.filter.KnownBadListFilter;
import com.tf.photos.service.AuditLogService;
import com.tf.photos.service.util.SpringLookup;
import com.tf.photos.util.DeepSorter;

/**
 * @author Heather Stevens
 *
 * Used to model known bad remote addresses for data table.
 */
@Named
public class KnownBadTableModel extends LazyDataModel<KnownBad> implements Serializable
{
	private static final Logger log = LoggerFactory.getLogger(KnownBadTableModel.class);
	private static final long serialVersionUID = 1523170971123423681L;

	private List<KnownBad> dataSource;
	private Map<String, KnownBad> knownBadMap;
	private KnownBadListFilter knownBadListFilter;
	private boolean reloadData = true;

	public KnownBadTableModel() {
	}

	@Override
	public KnownBad getRowData(String rowKey) {

		KnownBad knownBad = null;

		try {
			knownBad = knownBadMap.get(rowKey);

			if (knownBad == null) {
				log.info("getRowData, Looking up known safe " +rowKey);
				AuditLogService auditLogService = (AuditLogService)SpringLookup.findService("auditLogService");
				knownBad = auditLogService.getKnownBadById(rowKey);

				if (knownBad != null) {
					knownBadMap.put(knownBad.getId(), knownBad);
					reloadData = true;
				}
			}
		}
		catch (Exception e) {
			log.error("getRowData, Failed to known safe " + rowKey, e);
		}

		if (knownBad == null) {
			log.error("!!!!!!!!!!!!Failed to lookup known safe from row key " + rowKey);
		}
		return knownBad;
	}

	@Override
	public Object getRowKey(KnownBad knownBad) {
		return knownBad.getId();
	}

	/**
	 * Loads the known bad remote addresses, filters and sorts the list. Filtering and sorting are case insensitive.
	 *
	 * @param first         First page to load.
	 * @param pageSize      Number of rows per page.
	 * @param sortField     How rows are to be sorted.
	 * @param sortOrder     Sort order of rows.
	 * @param filters       Map of filters
	 * @return              Row data to display.
	 */
	public List<KnownBad> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String,Object> filters) {

		List<KnownBad> data = new ArrayList<>();
		if (reloadData) {
			log.info("loading new data into known safe list.");
			loadKnownBadItems();
		}

		knownBadListFilter.filter(dataSource, data, filters);

		//sort
		if(sortField != null) {
			Collections.sort(data, new DeepSorter<>(sortField, sortOrder));
		}

		//rowCount
		int dataSize = data.size();
		this.setRowCount(dataSize);

		return knownBadListFilter.filterToOnePage(data, pageSize, first);
	}

	public void setReloadData(boolean reloadData) {
		this.reloadData = reloadData;
	}

	public void setKnownBadListFilter(KnownBadListFilter knownBadListFilter) {
		this.knownBadListFilter = knownBadListFilter;
	}

	/**
	 * Load known bad remote addresses from database.
	 */
	private void loadKnownBadItems() {
		AuditLogService auditLogService = (AuditLogService)SpringLookup.findService("auditLogService");


		dataSource = auditLogService.findAllKnownBad();
		knownBadMap = new HashMap<>(dataSource.size());

		for (KnownBad knownBad : dataSource) {
			knownBadMap.put(knownBad.getId(), knownBad);
		}

		reloadData = false;
	}
}
