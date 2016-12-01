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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.security.SecureRandom;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * User: Heather
 * Date: 7/30/2016
 */
@RunWith(JUnit4.class)
public class KryptoUtilTest
{

    public KryptoUtilTest() {}

	@Test
	public void testPasswordHashCreation() {

		try {
			String password = "testsecret1";
			String hashed = KryptoUtil.createHash(password);
			assertNotNull(hashed);

			for(int i = 0; i < 10; i++)
			{
				String hash = KryptoUtil.createHash("p\r\nassw0Rd!");
				assertNotNull(hashed);
				System.out.println("Hash: " + hash);
			}

		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Test failed " + e);
		}
	}

	@Test
	public void getSecureRandomSecretKey() {

		String key = RandomStringUtils.randomAlphanumeric(128);
		System.out.println("key: " + key);
	}

    @Test
    public void testPasswordHashes() {

        try
        {
	        SecureRandom secureRandom = new SecureRandom();
	        for (int index = 30; index < 40; index ++)
	        {
		        byte rPasswordSeg[] = new byte[index];
		        secureRandom.nextBytes(rPasswordSeg); // get non printables

		        String password = "b@ds3c3t" + new String(rPasswordSeg);
		        System.out.println("Testing: " + password);

		        String hash = KryptoUtil.createHash(password);

		        if (!KryptoUtil.matches(password, hash))
		        {
			        System.out.println("FAILURE: Password doesn't match hash, password: " + password);
			        fail("Password doesn't match hash, password: " + password);
		        }
	        }

        }
        catch(Exception ex)
        {
            System.out.println("ERROR: " + ex);
            fail("Exception caught "+ex.getLocalizedMessage());
        }
    }

	@Test
	public void testEncryptionDecryptionEnglish() {
		try {
			String password = "Testing123456789";
			byte[] encrypted = KryptoUtil.encryptText(password);
			assertNotNull(encrypted);

			String backAgain = KryptoUtil.decryptText(encrypted);
			assertEquals(password, backAgain);
		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Test failed " + e);
		}
	}

	@Test
	public void testEncryptionDecryptionLatin() {
		try {
			String password = "México";
			byte[] encrypted = KryptoUtil.encryptText(password);
			assertNotNull(encrypted);

			String backAgain = KryptoUtil.decryptText(encrypted);
			assertEquals(password, backAgain);
		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Test failed " + e);
		}
	}
}
