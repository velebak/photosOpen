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

import static org.junit.Assert.fail;

/**
 * User: Heather
 * Date: 7/29/13
 */
@RunWith(JUnit4.class)
public class PasswordStrengthUtilTest  {

    public PasswordStrengthUtilTest() {}

    @Test
    public void testPasswordStrengthUtilEmpty() {

        try
        {
            if (PasswordStrengthUtil.isPasswordValid("")) {
                fail("Password is empty");
            }

            if (PasswordStrengthUtil.isPasswordValid(null)) {
                fail("Password is empty");
            }
        }

        catch(Exception ex)
        {
            System.out.println("ERROR: " + ex);
            fail("Exception caught "+ex.getLocalizedMessage());
        }
    }

    @Test
    public void testPasswordStrengthUtilRequirements() {

        try
        {
            if (PasswordStrengthUtil.isPasswordValid("password")) {
                fail("Password doesn't meet requirements");
            }

            if (PasswordStrengthUtil.isPasswordValid("PASSWORD")) {
                fail("Password doesn't meet requirements");
            }

            if (PasswordStrengthUtil.isPasswordValid("94825930")) {
                fail("Password doesn't meet requirements");
            }

            if (PasswordStrengthUtil.isPasswordValid("!#%&)_+$")) {
                fail("Password doesn't meet requirements");
            }

            if (PasswordStrengthUtil.isPasswordValid("Password")) {
                fail("Password doesn't meet requirements");
            }

            if (PasswordStrengthUtil.isPasswordValid("password1")) {
                fail("Password doesn't meet requirements");
            }

            if (PasswordStrengthUtil.isPasswordValid("password*")) {
                fail("Password doesn't meet requirements");
            }
        }
        catch(Exception ex)
        {
            System.out.println("ERROR: " + ex);
            fail("Exception caught "+ex.getLocalizedMessage());
        }
    }

    @Test
    public void testPasswordStrengthUtilLengthToShort() {
        try
        {
            if (PasswordStrengthUtil.isPasswordValid("passW1")) {
                fail("Password doesn't meet requirements");
            }

            if (PasswordStrengthUtil.isPasswordValid("WORD2w")) {
                fail("Password doesn't meet requirements");
            }

            if (PasswordStrengthUtil.isPasswordValid("94Qw")) {
                fail("Password doesn't meet requirements");
            }

            if (PasswordStrengthUtil.isPasswordValid("!Qw3$")) {
                fail("Password doesn't meet requirements");
            }

            if (PasswordStrengthUtil.isPasswordValid("ord")) {
                fail("Password doesn't meet requirements");
            }

            if (PasswordStrengthUtil.isPasswordValid("pd1")) {
                fail("Password doesn't meet requirements");
            }

            if (PasswordStrengthUtil.isPasswordValid("prd*")) {
                fail("Password doesn't meet requirements");
            }
        }
        catch(Exception ex)
        {
            System.out.println("ERROR: " + ex);
            fail("Exception caught "+ex.getLocalizedMessage());
        }
    }


    @Test
    public void testPasswordStrengthUtilGood() {
        try
        {
            if (!PasswordStrengthUtil.isPasswordValid("passWord1")) {
                fail("Password should meet requirements");
            }

            if (!PasswordStrengthUtil.isPasswordValid("244ORD2w18264")) {
                fail("Password should meet requirements");
            }

            if (!PasswordStrengthUtil.isPasswordValid("94Q@&gjsynqffw")) {
                fail("Password should meet requirements");
            }

            if (!PasswordStrengthUtil.isPasswordValid("!Q848jtrjhstfrw3$")) {
                fail("Password should meet requirements");
            }

            if (!PasswordStrengthUtil.isPasswordValid("1lki8u**jhuHH")) {
                fail("Password should meet requirements");
            }

            if (!PasswordStrengthUtil.isPasswordValid("pd28465hjJJU*1")) {
                fail("Password should meet requirements");
            }

            if (!PasswordStrengthUtil.isPasswordValid("prd*workwJJT%")) {
                fail("Password should meet requirements");
            }
        }
        catch(Exception ex)
        {
            System.out.println("ERROR: " + ex);
            fail("Exception caught "+ex.getLocalizedMessage());
        }
    }

}
