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

/**
 * Enum used with photo filtering.
 *
 * @author Heather Stevens on 1/4/14.
 */
public enum FileSizeRange {

	LESS_THAN_100KB("fileSizeRange.lessThan100KB", 0, 100 * 1024 - 1),
	BETWEEN_100KB_AND_500KB("fileSizeRange.between100KBAnd500KB", 100 * 1024, 500 * 1024 - 1),
	BETWEEN_500KB_AND_1MB("fileSizeRange.between500KBAnd1MB", 500 * 1024, 1024 * 1024 - 1),
	BETWEEN_1MB_AND_5MB("fileSizeRange.between1MBAnd5MB", 1024 * 1024, 5 * 1024 * 1024 - 1),
	ABOVE_5MB("fileSizeRange.above5MB", -1, -1); // chosen by default.

	private String label; // Used with resource bundle.
	private long fromSize;
	private long toSize;

	FileSizeRange(String label, long fromSize, long toSize) {
		this.label = label;
		this.fromSize = fromSize;
		this.toSize = toSize;
	}

	public String getLabel() {
		return this.label;
	}

	/**
	 * Returns matching enum for given file size.
	 *
	 * @param fileSize      File size to consider.
	 * @return              Enum containing file size within its range.
	 */
	public static FileSizeRange getEnumForFileSize(long fileSize) {

		for (FileSizeRange fileSizeRange: FileSizeRange.values()) {
			if (fileSize >= fileSizeRange.fromSize && fileSize <= fileSizeRange.toSize) {
				return fileSizeRange;
			}
		}

		return ABOVE_5MB;
	}
}
