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
package com.tf.photos.util;

import java.util.Comparator;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.primefaces.model.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Heather Stevens on 11/26/16.
 */
public class DeepSorter<T> implements Comparator<T> {

	private static final Logger log = LoggerFactory.getLogger(DeepSorter.class);

	private String sortField;
	private SortOrder sortOrder;

	public DeepSorter(String sortField, SortOrder sortOrder) {
		this.sortField = sortField;
		this.sortOrder = sortOrder;
	}

	@Override
	public int compare(T item1, T item2) {
		try {
			Comparable fieldValue1 = (Comparable) PropertyUtils.getNestedProperty(item1, sortField);
			Comparable fieldValue2 = (Comparable)PropertyUtils.getNestedProperty(item2, sortField);

			int retVal = ObjectUtils.compare(fieldValue1, fieldValue2);

			if (SortOrder.DESCENDING.equals(sortOrder)) {
				retVal = -retVal;
			}

			return retVal;
		}
		catch (Exception e) {
			log.error("Failed to sort with sortField " + sortField, e);
		}
		return 0;
	}
}
