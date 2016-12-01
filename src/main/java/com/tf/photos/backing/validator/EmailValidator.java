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
 * Date: 8/3/13
 * Time: 3:27 PM
 */

import com.tf.photos.backing.UserListBean;
import com.tf.photos.model.WebUser;
import com.tf.photos.model.WebUserSession;
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
public class EmailValidator extends BaseValidator implements Serializable {

    private static final Logger log = LoggerFactory.getLogger(EmailValidator.class);
    private static final long serialVersionUID = 8477692851456951954L;
    protected static  final String LIBERAL_EMAIL_PATTERN = "^.+@.+$";
    private Pattern pattern;

    @Inject
    private ResourceBundleUtil resourceBundleUtil;

	@Inject
	private WebUserSession webUserSession;

	@Inject
	private UserListBean userListBean;

    public EmailValidator() {
        pattern = Pattern.compile(LIBERAL_EMAIL_PATTERN);
    }

    /**
     * Validates emailAddress entered by user.
     *
     * @param facesContext      Faces context validator is operating under.
     * @param uiComponent       Password UI Component
     * @param o                 Object containing the password value.
     * @throws javax.faces.validator.ValidatorException
     */
    @Override
    public void validate(FacesContext facesContext, UIComponent uiComponent, Object o) throws ValidatorException {

        ResourceBundle resourceBundle = resourceBundleUtil.getResourceBundle(ResourceBundleUtil.USERS);

        String emailAddress = o.toString();

        log.info("Validating emailAddress "+emailAddress);

        if (!validate(emailAddress)) {
            log.info("Validating email address "+emailAddress+", invalid");

            createErrorMessage(resourceBundle.getString("errorEmailInvalid"), resourceBundle.getString("errorEmailInvalid"));
        }

        if (!isUnique(emailAddress)) {
            log.info("Validating emailAddress "+emailAddress+", duplicate");

            createErrorMessage(resourceBundle.getString("errorEmailDuplicate"), resourceBundle.getString("errorEmailDuplicate"));
        }
    }

    /**
     * Validate emailAddress with regular expression
     *
     * @param emailAddress emailAddress for validation
     * @return true valid emailAddress, false invalid emailAddress
     */
    public boolean validate(final String emailAddress){

        Matcher matcher = pattern.matcher(emailAddress);
        return matcher.matches();

    }

    /**
     * Validate emailAddress by checking for a duplication in the database.
     *
     * @param emailAddress to check
     * @return true emailAddress is unique having no duplicate in the database, false emailAddress duplicated and therefor invalid.
     */
    public boolean isUnique(final String emailAddress){

        WebUserService userService = (WebUserService) SpringLookup.findService("webUserService");

        WebUser webUser = userService.getUserByEmailAddress(emailAddress);

	    // Is the user's email address already? Then it is okay.
	    if (webUser != null && webUserSession != null && webUserSession.getWebUser() != null &&
			    webUserSession.getWebUser().getEmailAddress() != null &&
			    webUserSession.getWebUser().getEmailAddress().equals(emailAddress)) {
			return true;
	    }

	    // Is this an admin editing the email address and it's the same one from before? Then it's okay.
	    String adminEditEmailAddress = userListBean.getCurrentEmailEditEmailAddress();
	    if (adminEditEmailAddress != null && adminEditEmailAddress.equals(emailAddress)) {
		    return true;
	    }

        return (webUser == null);
    }
}
