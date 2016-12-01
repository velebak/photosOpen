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

import com.tf.photos.backing.filter.KnownSafeListFilter;
import com.tf.photos.service.AuditLogService;
import com.tf.photos.service.util.SpringLookup;
import com.tf.photos.util.DeepSorter;

/**
 * @author Heather Stevens
 *
 * Used to model known safe remote addresses for data table.
 */
@Named
public class KnownSafeTableModel extends LazyDataModel<KnownSafe> implements Serializable
{
	private static final Logger log = LoggerFactory.getLogger(KnownSafeTableModel.class);
	private static final long serialVersionUID = 4239544230035698723L;

	private List<KnownSafe> dataSource;
	private Map<String, KnownSafe> knownSafeMap;
	private boolean reloadData = true;

	private KnownSafeListFilter knownSafeListFilter;

	public KnownSafeTableModel() {
	}

	@Override
	public KnownSafe getRowData(String rowKey) {

		KnownSafe knownSafe = null;

		try {
			knownSafe = knownSafeMap.get(rowKey);

			if (knownSafe == null) {
				log.info("getRowData, Looking up known safe " +rowKey);
				AuditLogService auditLogService = (AuditLogService)SpringLookup.findService("auditLogService");
				knownSafe = auditLogService.getKnownSafeById(rowKey);

				if (knownSafe != null) {
					knownSafeMap.put(knownSafe.getId(), knownSafe);
					reloadData = true;
				}
			}
		}
		catch (Exception e) {
			log.error("getRowData, Failed to find known safe item " + rowKey, e);
		}

		if (knownSafe == null) {
			log.error("!!!!!!!!!!!!Failed to lookup known safe from row key " + rowKey);
		}
		return knownSafe;
	}

	@Override
	public Object getRowKey(KnownSafe knownSafe) {
		return knownSafe.getId();
	}

	/**
	 * Loads the known safe data, filters and sorts the list. Filtering and sorting are case insensitive.
	 *
	 * @param first         First page to load.
	 * @param pageSize      Number of rows per page.
	 * @param sortField     How rows are to be sorted.
	 * @param sortOrder     Sort order of rows.
	 * @param filters       Map of filters
	 * @return              Row data to display.
	 */
	public List<KnownSafe> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String,Object> filters) {

		List<KnownSafe> data = new ArrayList<>();
		if (reloadData) {
			log.info("loading new data into known safe list.");
			loadKnownSafeItems();
		}

		knownSafeListFilter.filter(dataSource, data, filters);

		//sort
		if(sortField != null) {
			Collections.sort(data, new DeepSorter<>(sortField, sortOrder));
		}

		//rowCount
		int dataSize = data.size();
		this.setRowCount(dataSize);

		return knownSafeListFilter.filterToOnePage(data, pageSize, first);
	}

	public void setReloadData(boolean reloadData) {
		this.reloadData = reloadData;
	}

	public void setKnownSafeListFilter(KnownSafeListFilter knownSafeListFilter) {
		this.knownSafeListFilter = knownSafeListFilter;
	}

	/**
	 * Load known safe remote addresses from database.
	 */
	private void loadKnownSafeItems() {
		AuditLogService auditLogService = (AuditLogService)SpringLookup.findService("auditLogService");


		dataSource = auditLogService.findAllKnownSafe();
		knownSafeMap = new HashMap<>(dataSource.size());

		for (KnownSafe knownSafe : dataSource) {
			knownSafeMap.put(knownSafe.getId(), knownSafe);
		}

		reloadData = false;
	}
}
