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
import static org.junit.Assert.assertTrue;


/**
 * Created with IntelliJ IDEA.
 * User: heather
 * Date: 12/17/13
 * Time: 10:02 PM
 * To change this template use File | Settings | File Templates.
 */
@RunWith(JUnit4.class)
public class SendMailTest {

	public SendMailTest() {}

    @Test
	public void sendEmail() {

	    boolean success =
			    SendEmail.sendMessage("heather92115@yahoo.com", "heather92115@yahoo.com", "Title", "Msg Goes Here...");

	    assertTrue("Emailed Failed.", success);
    }

}
