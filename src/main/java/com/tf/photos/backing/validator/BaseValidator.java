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
 * Time: 1:59 PM
 */

import javax.faces.application.FacesMessage;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

public abstract class BaseValidator implements Validator {

    /**
     * Creates faces error message and throws in in validator exception.
     *
     * @param title     Title text of the message to create.
     * @param text      Content of message.
     * @throws ValidatorException
     */
    public void createErrorMessage(String title, String text) throws ValidatorException {

        FacesMessage msg =
                new FacesMessage(title,text);

        msg.setSeverity(FacesMessage.SEVERITY_ERROR);
        throw new ValidatorException(msg);
    }
}
