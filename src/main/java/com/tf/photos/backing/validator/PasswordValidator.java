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

import com.tf.photos.service.util.ResourceBundleUtil;
import com.tf.photos.util.PasswordStrengthUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.ResourceBundle;

@Named
@SessionScoped
public class PasswordValidator extends BaseValidator implements Serializable {

	private static final Logger log = LoggerFactory.getLogger(PasswordValidator.class);

	private static final long serialVersionUID = -3327188422039799695L;
    private String password;

    @Inject
    private ResourceBundleUtil resourceBundleUtil;

    public PasswordValidator() {}

    /**
     * Validates password entered by user to make sure it passes all the rule checks.
     *
     * @param facesContext      Faces context validator is operating under.
     * @param uiComponent       Password UI Component
     * @param o                 Object containing the password value.
     * @throws ValidatorException
     */
    @Override
    public void validate(FacesContext facesContext, UIComponent uiComponent, Object o) throws ValidatorException {

        ResourceBundle resourceBundle = resourceBundleUtil.getResourceBundle(ResourceBundleUtil.USERS);
        this.password = o.toString();

        if (!PasswordStrengthUtil.isPasswordValid(o.toString())) {
            createErrorMessage(resourceBundle.getString("errorPasswordValidationFailed"), resourceBundle.getString("passwordHint"));
        }
    }

    /**
     * Make sure the passwords entered by the user match.
     *
     * @param o                 Object containing the password value.
     */
    public void validateMatch(FacesContext facesContext, UIComponent uiComponent, Object o) throws ValidatorException {

        ResourceBundle resourceBundle = resourceBundleUtil.getResourceBundle(ResourceBundleUtil.USERS);

        String passwordConfirm = o.toString();
        String oriPassword;

        // Find the actual JSF component for the client ID.
        UIInput textInput = (UIInput) uiComponent.findComponent("passwordInputId");

        if (textInput == null || textInput.getSubmittedValue() == null) {
            oriPassword = this.password;
        }
        else {
            oriPassword = textInput.getSubmittedValue().toString();
        }

        if (!passwordConfirm.equals(oriPassword)) {
            createErrorMessage(resourceBundle.getString("errorPasswordValidationFailed"), resourceBundle.getString("errorPasswordsDontMatch"));
        }
    }

    /**
     * Clears the password from user's session.
     */
    public void clear() {
        this.password = null;
    }
}
