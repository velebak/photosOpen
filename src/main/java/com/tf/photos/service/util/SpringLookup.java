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

import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * @author Heather Stevens
 * Date: 6/23/13
 */
public class SpringLookup {
    private static final Logger log = LoggerFactory.getLogger(SpringLookup.class);

    /**
	 * Lookup a string bean from the external application context. Used to pull Spring beans into CDI app.
	 *
	 * @param beanName      Name of spring bean to find.
	 * @return Object       Reference to the spring bean.
	 */
	public static Object findService(final String beanName) {

		WebApplicationContext webApplicationContext = WebApplicationContextUtils.getWebApplicationContext(getServletContext());
		if (webApplicationContext == null ) {
			log.error("Failed to find Spring web app context.");
			return null;
		}

		return webApplicationContext.getBean(beanName);
	}

	/**
	 * Lookup a string bean from the external application context. Used to pull Spring beans into CDI app.
	 *
	 * @param request       Servlet request.
	 * @param beanName      Name of spring bean to find.
	 * @return Object       Reference to the spring bean.
	 */
	public static Object findService(ServletRequest request, final String beanName) {

		WebApplicationContext webApplicationContext = WebApplicationContextUtils.getWebApplicationContext(request.getServletContext());
		if (webApplicationContext == null ) {
			log.error("Failed to find Spring web app context.");
			return null;
		}

		return webApplicationContext.getBean(beanName);
	}

    /**
     * Utility to get the servlet context from the faces context
     *
     * @return ServletContext
     */
    public static ServletContext getServletContext() {

        return (ServletContext)FacesContext.getCurrentInstance().getExternalContext().getContext();
    }
}
