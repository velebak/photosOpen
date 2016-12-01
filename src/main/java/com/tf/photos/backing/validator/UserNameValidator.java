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

/**
 * User: Heather
 * Date: 7/31/13
 * Time: 6:06 PM
 */

import com.tf.photos.model.WebUser;
import com.tf.photos.service.WebUserService;
import com.tf.photos.service.util.ResourceBundleUtil;
import com.tf.photos.service.util.SpringLookup;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Named
public class UserNameValidator extends BaseValidator implements Serializable {

    private static final Logger log = LoggerFactory.getLogger(UserNameValidator.class);

    private static final long serialVersionUID = 2818147653829477813L;
    private Pattern pattern;
    private static final int MIN_LENGTH = 3;
    private static final String USERNAME_PATTERN = "^[A-Za-z0-9_-]*$";

    @Inject
    private ResourceBundleUtil resourceBundleUtil;

    public UserNameValidator() {
        pattern = Pattern.compile(USERNAME_PATTERN);
    }

    /**
     * Validates password entered by user to make sure it passes all the rule checks.
     *
     * @param facesContext      Faces context validator is operating under.
     * @param uiComponent       Password UI Component
     * @param o                 Object containing the password value.
     * @throws javax.faces.validator.ValidatorException
     */
    @Override
    public void validate(FacesContext facesContext, UIComponent uiComponent, Object o) throws ValidatorException {

        ResourceBundle resourceBundle = resourceBundleUtil.getResourceBundle(ResourceBundleUtil.USERS);

        String userName = o.toString();

        log.info("Validating user name "+userName);

        if (userName.length() < MIN_LENGTH) {
            createErrorMessage(resourceBundle.getString("errorUserNameInvalid"), resourceBundle.getString("errorUserNameTooShort"));
        }

        if (!validate(userName)) {
            log.info("Validating user name "+userName+", invalid");

            createErrorMessage(resourceBundle.getString("errorUserNameInvalid"), resourceBundle.getString("errorUserNameHint"));
        }

        if (!isUnique(userName)) {
            log.info("Validating user name "+userName+", duplicate");

            createErrorMessage(resourceBundle.getString("errorUserNameDuplicate"), resourceBundle.getString("errorUserNameDuplicateHint"));
        }
    }

    /**
     * Validate username with regular expression
     *
     * @param username username for validation
     * @return true valid username, false invalid username
     */
    public boolean validate(final String username){

        Matcher matcher = pattern.matcher(username);
        return matcher.matches();

    }

    /**
     * Validate username by checking for a duplication in the database.
     *
     * @param username username for validation
     * @return true username is unique having no duplicate in the database, false username duplicated and therefor invalid.
     */
    public boolean isUnique(final String username){

        WebUserService userService = (WebUserService) SpringLookup.findService("webUserService");

        WebUser webUser = userService.getUserByUserName(username.toLowerCase());

        return (webUser == null);
    }
}
