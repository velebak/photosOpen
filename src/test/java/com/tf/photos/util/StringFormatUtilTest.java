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

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * User: Heather
 * Date: 6/15/14
 */
@RunWith(JUnit4.class)
public class StringFormatUtilTest {

    public StringFormatUtilTest() {}

    @Test
    public void testFileConversion() {

        try
        {
			Long fileSize = 100L;
	        String fileSizeStr = StringFormatUtil.fileSizeToString(fileSize);
	        assertEquals("File Size", "100 B", fileSizeStr);

	        fileSize = 2100L;
	        fileSizeStr = StringFormatUtil.fileSizeToString(fileSize);
	        assertEquals("File Size", "2.1 KB", fileSizeStr);

	        fileSize = 2111L;
	        fileSizeStr = StringFormatUtil.fileSizeToString(fileSize);
	        assertEquals("File Size", "2.1 KB", fileSizeStr);

	        fileSize = 560000L;
	        fileSizeStr = StringFormatUtil.fileSizeToString(fileSize);
	        assertEquals("File Size", "546.9 KB", fileSizeStr);

	        fileSize = 560000L;
	        fileSizeStr = StringFormatUtil.fileSizeToString(fileSize);
	        assertEquals("File Size", "546.9 KB", fileSizeStr);

	        fileSize = 1048576L;
	        fileSizeStr = StringFormatUtil.fileSizeToString(fileSize);
	        assertEquals("File Size", "1 MB", fileSizeStr);

	        fileSize = 1073741824L;
	        fileSizeStr = StringFormatUtil.fileSizeToString(fileSize);
	        assertEquals("File Size", "1 GB", fileSizeStr);

	        fileSize = 1099511627776L;
	        fileSizeStr = StringFormatUtil.fileSizeToString(fileSize);
	        assertEquals("File Size", "1 TB", fileSizeStr);

	        fileSize = 2199023255552L;
	        fileSizeStr = StringFormatUtil.fileSizeToString(fileSize);
	        assertEquals("File Size", "2 TB", fileSizeStr);
        }
        catch(Exception ex)
        {
            System.out.println("ERROR: " + ex);
            fail("Exception caught "+ex.getLocalizedMessage());
        }
    }

	@Test
	public void testFileConversionRoundTrip() {

		try
		{
			Long fileSize = 100L;
			String fileSizeStr = StringFormatUtil.fileSizeToString(fileSize);
			assertEquals("File Size", "100 B", fileSizeStr);
			assertEquals("File Size", fileSize, StringFormatUtil.formattedFileSizeToObject(fileSizeStr));

			Long fileSize2 = 2100L;
			String fileSizeStr2 = StringFormatUtil.fileSizeToString(fileSize2);
			assertTrue("File Size", fileSizeStr2.startsWith("2"));

			assertTrue("File Size", StringFormatUtil.formattedFileSizeToObject(fileSizeStr2).toString().startsWith("2"));

		}
		catch(Exception ex)
		{
			System.out.println("ERROR: " + ex);
			fail("Exception caught "+ex.getLocalizedMessage());
		}
	}
}
