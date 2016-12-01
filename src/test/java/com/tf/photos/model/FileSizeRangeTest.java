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

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;


/**
 * User: Heather
 * Date: 7/13/2014
 */
@RunWith(JUnit4.class)
public class FileSizeRangeTest {

    @Test
    public void fileSizeRangeLess100Test() {

	    FileSizeRange fileSizeRange = FileSizeRange.getEnumForFileSize(1);
	    assertEquals(FileSizeRange.LESS_THAN_100KB, fileSizeRange);

	    fileSizeRange = FileSizeRange.getEnumForFileSize(90000);
	    assertEquals(FileSizeRange.LESS_THAN_100KB, fileSizeRange);

	    fileSizeRange = FileSizeRange.getEnumForFileSize(101 * 1024);
	    assertEquals(FileSizeRange.BETWEEN_100KB_AND_500KB, fileSizeRange);

	    fileSizeRange = FileSizeRange.getEnumForFileSize(470 * 1024);
	    assertEquals(FileSizeRange.BETWEEN_100KB_AND_500KB, fileSizeRange);
    }

	@Test
	public void fileSizeRangeBetween100And500Test() {

		FileSizeRange fileSizeRange = FileSizeRange.getEnumForFileSize(101 * 1024);
		assertEquals(FileSizeRange.BETWEEN_100KB_AND_500KB, fileSizeRange);

		fileSizeRange = FileSizeRange.getEnumForFileSize(470 * 1024);
		assertEquals(FileSizeRange.BETWEEN_100KB_AND_500KB, fileSizeRange);
	}

	@Test
	public void fileSizeRangeBetween500And1MbTest() {

		FileSizeRange fileSizeRange = FileSizeRange.getEnumForFileSize(501 * 1024);
		assertEquals(FileSizeRange.BETWEEN_500KB_AND_1MB, fileSizeRange);

		fileSizeRange = FileSizeRange.getEnumForFileSize(999 * 1024);
		assertEquals(FileSizeRange.BETWEEN_500KB_AND_1MB, fileSizeRange);
	}

	@Test
	public void fileSizeRangeBetween1MbAnd5MbTest() {

		FileSizeRange fileSizeRange = FileSizeRange.getEnumForFileSize(2 * 1024 * 1024);
		assertEquals(FileSizeRange.BETWEEN_1MB_AND_5MB, fileSizeRange);

		fileSizeRange = FileSizeRange.getEnumForFileSize(4 * 1024 * 1024);
		assertEquals(FileSizeRange.BETWEEN_1MB_AND_5MB, fileSizeRange);
	}

	@Test
	public void fileSizeRangeAbove5MbTest() {

		FileSizeRange fileSizeRange = FileSizeRange.getEnumForFileSize(6 * 1024 * 1024);
		assertEquals(FileSizeRange.ABOVE_5MB, fileSizeRange);

		fileSizeRange = FileSizeRange.getEnumForFileSize(400 * 1024 * 1024);
		assertEquals(FileSizeRange.ABOVE_5MB, fileSizeRange);
	}

	@Test
	public void fileSizeEdgeCasesTest() {

		FileSizeRange fileSizeRange = FileSizeRange.getEnumForFileSize(0);
		assertEquals(FileSizeRange.LESS_THAN_100KB, fileSizeRange);

		fileSizeRange = FileSizeRange.getEnumForFileSize(100 * 1024 - 1);
		assertEquals(FileSizeRange.LESS_THAN_100KB, fileSizeRange);

		fileSizeRange = FileSizeRange.getEnumForFileSize(100 * 1024);
		assertEquals(FileSizeRange.BETWEEN_100KB_AND_500KB, fileSizeRange);

		fileSizeRange = FileSizeRange.getEnumForFileSize(500 * 1024 - 1);
		assertEquals(FileSizeRange.BETWEEN_100KB_AND_500KB, fileSizeRange);

		fileSizeRange = FileSizeRange.getEnumForFileSize(1024 * 1024);
		assertEquals(FileSizeRange.BETWEEN_1MB_AND_5MB, fileSizeRange);

		fileSizeRange = FileSizeRange.getEnumForFileSize(5 * 1024 * 1024 - 1);
		assertEquals(FileSizeRange.BETWEEN_1MB_AND_5MB, fileSizeRange);
	}

	@Test
	public void fileSizeNegativeCheckTest() {

		FileSizeRange fileSizeRange = FileSizeRange.getEnumForFileSize(4567);
		assertNotEquals(FileSizeRange.BETWEEN_100KB_AND_500KB, fileSizeRange);

		fileSizeRange = FileSizeRange.getEnumForFileSize(100 * 1024);
		assertNotEquals(FileSizeRange.LESS_THAN_100KB, fileSizeRange);

		fileSizeRange = FileSizeRange.getEnumForFileSize(1);
		assertNotEquals(FileSizeRange.BETWEEN_100KB_AND_500KB, fileSizeRange);

		fileSizeRange = FileSizeRange.getEnumForFileSize(500 * 1024 + 1);
		assertNotEquals(FileSizeRange.BETWEEN_100KB_AND_500KB, fileSizeRange);

		fileSizeRange = FileSizeRange.getEnumForFileSize(6 * 1024 * 1024);
		assertNotEquals(FileSizeRange.BETWEEN_1MB_AND_5MB, fileSizeRange);

		fileSizeRange = FileSizeRange.getEnumForFileSize(5 * 1024 * 1024);
		assertNotEquals(FileSizeRange.BETWEEN_1MB_AND_5MB, fileSizeRange);
	}
}
