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
package com.tf.photos.service.util;

import java.io.Serializable;
import java.util.ResourceBundle;

import javax.faces.context.FacesContext;
import javax.inject.Named;

/**
 * User: Heather
 * Date: 7/31/13
 * Time: 6:11 PM
 */
@Named
public class ResourceBundleUtil implements Serializable {
    private static final long serialVersionUID = -6588029774002162881L;
    public static final String USERS = "users.UserLabels";
    public static final String PHOTOS = "photos.PhotoLabels";
    public ResourceBundleUtil() {}

    public ResourceBundle getResourceBundle(String baseName) {
        return ResourceBundle.getBundle(baseName, FacesContext.getCurrentInstance().getViewRoot().getLocale());
    }
}
