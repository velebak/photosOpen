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
package com.tf.photos.backing.validator;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * User: Heather
 * Date: 8/3/13
 */
@RunWith(JUnit4.class)
public class EmailValidatorTest {


    @Test
    public void testBuildUp() {
        Pattern pattern = Pattern.compile("^[\\w]+$");
        Matcher matcher = pattern.matcher("test");
        assertTrue("Should work", matcher.matches());

        pattern = Pattern.compile("^[\\w.-_+]+$");
        matcher = pattern.matcher("test+test.com");
        assertTrue("Should work", matcher.matches());

        pattern = Pattern.compile("^.+$");
        matcher = pattern.matcher("test+test.com");
        assertTrue("Should work", matcher.matches());

        pattern = Pattern.compile("^.+@.+$");
        matcher = pattern.matcher("test+test@test.com");
        assertTrue("Should work", matcher.matches());
    }

    @Test
    public void goodEmails() {
        Pattern pattern = Pattern.compile(EmailValidator.LIBERAL_EMAIL_PATTERN);
        Matcher matcher = pattern.matcher("test@test.com");
        assertTrue("Should work", matcher.matches());

        matcher = pattern.matcher("Test1@test.test.com");
        assertTrue("Should work", matcher.matches());

        matcher = pattern.matcher("Test1-123@test.test.com");
        assertTrue("Should work", matcher.matches());

        matcher = pattern.matcher("Test+111@test.com");
        assertTrue("Should work", matcher.matches());

        matcher = pattern.matcher("Test+-!#$%^&*@test.com");
        assertTrue("Should work", matcher.matches());
    }

    @Test
    public void notSooGoodEmails() {
        Pattern pattern = Pattern.compile(EmailValidator.LIBERAL_EMAIL_PATTERN);
        Matcher matcher = pattern.matcher("test@@test.com");
        assertTrue("Should work", matcher.matches());

        matcher = pattern.matcher("Test1@test.test.com.");
        assertTrue("Should work", matcher.matches());

        matcher = pattern.matcher(".@test.test.com");
        assertTrue("Should work", matcher.matches());

        matcher = pattern.matcher("@@@");
        assertTrue("Should work", matcher.matches());
    }

    @Test
    public void notAllowedEmails() {
        Pattern pattern = Pattern.compile(EmailValidator.LIBERAL_EMAIL_PATTERN);
        Matcher matcher = pattern.matcher("test.com");
        assertFalse("Should not work", matcher.matches());

        matcher = pattern.matcher("@test.com");
        assertFalse("Should not work", matcher.matches());

        matcher = pattern.matcher("test@");
        assertFalse("Should not work", matcher.matches());

        matcher = pattern.matcher("blah");
        assertFalse("Should not work", matcher.matches());
    }
}
