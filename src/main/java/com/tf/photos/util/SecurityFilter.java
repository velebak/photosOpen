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

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tf.photos.model.WebUserSession;

/**
 * @author Heather Stevens on 5/27/2014.
 *
 * All requests must have a session with their webUserSession.
 */
public class SecurityFilter implements Filter
{

	private static final Logger log = LoggerFactory.getLogger(SecurityFilter.class);
	private static final String VIEW_EXPIRED_URI = "/home/viewExpired.jsf";

	/**
	 * Checks if user is logged in. If not it redirects to the login.xhtml page.
	 */
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

		// Look for the user's webUserSession bean.
		WebUserSession webUserSession = null;

		try {

			HttpSession httpSession = ((HttpServletRequest)request).getSession(false);

			if (httpSession != null)
			{
				webUserSession = (WebUserSession)httpSession.getAttribute("webUserSessionSecured");
			}
		}
		catch (Exception e) {
			log.error("Failed to acquire user's webUserSession", e);
			webUserSession = null;
		}

		if (webUserSession == null || !webUserSession.getLoggedIn()) {

			log.warn("User attempted to access secured page without an active web session.");
			((HttpServletResponse)response).sendRedirect(((HttpServletRequest)request).getContextPath() + VIEW_EXPIRED_URI);
			return;
		}

		// User is allowed to continue.
		chain.doFilter(request, response);
	}

	@Override
	public void init(FilterConfig config) throws ServletException
	{
	}

	@Override
	public void destroy() {
	}
}
