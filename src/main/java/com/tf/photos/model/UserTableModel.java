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

import com.tf.photos.backing.filter.UserListFilter;
import com.tf.photos.service.WebUserService;
import com.tf.photos.service.util.SpringLookup;
import com.tf.photos.util.DeepSorter;

/**
 * @author Heather Stevens on 5/4/2014.
 *
 * Used to model user for data table.
 */
@Named
public class UserTableModel extends LazyDataModel<WebUser> implements Serializable
{
	private static final Logger log = LoggerFactory.getLogger(UserTableModel.class);
	private static final long serialVersionUID = 2768344734004171245L;

	private List<WebUser> dataSource;
	private Map<String, WebUser> webUserMap;
	private boolean reloadData = true;
	private UserListFilter userListFilter;

	public UserTableModel() {
	}

	@Override
	public WebUser getRowData(String rowKey) {

		WebUser webUser = null;

		try {
			webUser = webUserMap.get(rowKey);

			if (webUser == null) {
				log.info("getRowData, Looking up new user. " +rowKey);
				WebUserService userService = (WebUserService) SpringLookup.findService("webUserService");
				webUser = userService.getUserById(rowKey);

				if (webUser != null) {
					webUserMap.put(webUser.getId(), webUser);
					reloadData = true;
				}
			}
		}
		catch (Exception e) {
			log.error("getRowData, Failed to find web user " + rowKey, e);
		}

		if (webUser == null) {
			log.error("Failed to lookup web user from row key " + rowKey);
		}
		return webUser;
	}

	@Override
	public Object getRowKey(WebUser webUser) {
		return webUser.getId();
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
	public List<WebUser> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String,Object> filters) {

		List<WebUser> data = new ArrayList<>();
		if (reloadData) {
			log.info("loading new data into web user list.");
			loadWebUsers();
		}

		userListFilter.filter(dataSource, data, filters);

		//sort
		if(sortField != null) {
			Collections.sort(data, new DeepSorter<>(sortField, sortOrder));
		}

		//rowCount
		int dataSize = data.size();
		this.setRowCount(dataSize);

		return userListFilter.filterToOnePage(data, pageSize, first);
	}

	public void setReloadData(boolean reloadData) {
		this.reloadData = reloadData;
	}

	public void setUserListFilter(UserListFilter userListFilter) {
		this.userListFilter = userListFilter;
	}

	/**
	 * Load users from database.
	 */
	private void loadWebUsers() {
		WebUserService userService = (WebUserService) SpringLookup.findService("webUserService");

		dataSource = userService.findUsers();
		webUserMap = new HashMap<>(dataSource.size());

		for (WebUser webUser: dataSource) {
			webUserMap.put(webUser.getId(), webUser);
		}

		reloadData = false;
	}
}
