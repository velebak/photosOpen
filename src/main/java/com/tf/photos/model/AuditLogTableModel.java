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
import java.util.ResourceBundle;

import javax.inject.Named;

import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tf.photos.backing.filter.AuditListFilter;
import com.tf.photos.service.AuditLogService;
import com.tf.photos.service.util.SpringLookup;
import com.tf.photos.util.DeepSorter;

/**
 * @author Heather Stevens
 *
 * Used to model audit logs for data table.
 */
@Named
public class AuditLogTableModel extends LazyDataModel<AuditLog> implements Serializable
{
	private static final Logger log = LoggerFactory.getLogger(AuditLogTableModel.class);
	private static final long serialVersionUID = 2842839400516269728L;
	private static final String WEB_EVENT_LABEL = "webEvent.label";
	private static final String EVENT_DATE = "eventDate";

	private AuditListFilter auditListFilter;

	private List<AuditLog> dataSource;
	private Map<String, AuditLog> auditLogMap;
	private boolean reloadData = true;

	public AuditLogTableModel() {
	}

	public void setDataSource(List<AuditLog> dataSource) {
		this.dataSource = dataSource;
	}

	@Override
	public AuditLog getRowData(String rowKey) {

		AuditLog auditLog = null;

		try {
			auditLog = auditLogMap.get(rowKey);

			if (auditLog == null) {
				log.info("getRowData, Looking up audit log. " +rowKey);
				AuditLogService auditLogService = (AuditLogService) SpringLookup.findService("auditLogService");
				auditLog = auditLogService.getAuditLogById(rowKey);

				if (auditLog != null) {
					auditLogMap.put(auditLog.getId(), auditLog);
					reloadData = true;
				}
			}
		}
		catch (Exception e) {
			log.error("getRowData, Failed to find audit log " + rowKey, e);
		}

		if (auditLog == null) {
			log.error("!!!!!!!!!!!!Failed to lookup audit log from row key " + rowKey);
		}
		return auditLog;
	}

	@Override
	public Object getRowKey(AuditLog auditLog) {
		return auditLog.getId();
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
	public List<AuditLog> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String,Object> filters) {

		List<AuditLog> data = new ArrayList<>();
		if (reloadData) {
			log.info("loading new data into audit log list.");
			loadAuditLogs();
		}

		ResourceBundle rb = null;
		try {
			rb = ResourceBundle.getBundle("/users/UserEvent");
		}
		catch (Exception e) {
			log.error("Failed to find resource bundle.");
		}

		auditListFilter.filter(dataSource, data, filters);

		//sort
		if(sortField != null) {
			Collections.sort(data, new DeepSorter<>(sortField, sortOrder));
		}

		//rowCount
		int dataSize = data.size();
		this.setRowCount(dataSize);

		return auditListFilter.filterToOnePage(data, pageSize, first);
	}

	public void setReloadData(boolean reloadData) {
		this.reloadData = reloadData;
	}

	public void setAuditListFilter(AuditListFilter auditListFilter) {
		this.auditListFilter = auditListFilter;
	}

	/**
	 * Load users from database.
	 */
	private void loadAuditLogs() {
		AuditLogService auditLogService = (AuditLogService)SpringLookup.findService("auditLogService");


		dataSource = auditLogService.findEvents();
		auditLogMap = new HashMap<>(dataSource.size());

		for (AuditLog auditLog : dataSource) {
			auditLogMap.put(auditLog.getId(), auditLog);
		}

		reloadData = false;
	}
}
