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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tf.photos.service.AuditLogService;
import com.tf.photos.service.util.SpringLookup;

/**
 * @author Heather.
 * Date: 7/5/2014
 *
 * Checks to see if requests are on the block ip address list and redirects user it they are.
 */
public class BlockedFilter implements Filter
{
	private static final Logger log = LoggerFactory.getLogger(BlockedFilter.class);

	/**
	 * Checks if user is logged in. If not it redirects to the login.xhtml page.
	 */
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

		// Look for the user's webUserSession bean.
		String remoteAddress = null;
		boolean addressIsBlocked = false;
		try {
			AuditLogService auditLogService = getAuditLogService(request);

			remoteAddress = request.getRemoteAddr();
			//log.info("checking " + remoteAddress);
			if (auditLogService.getBlockedMap().containsKey(remoteAddress)) {
				addressIsBlocked = true;
			}
			else {
				for (String blockedAddress: auditLogService.getBlockedMap().keySet()) {
					if (remoteAddress.startsWith(blockedAddress)) {
						addressIsBlocked = true;
						break;
					}
				}
			}
		}
		catch (Exception e) {
			log.error("Failed to check remote address " + remoteAddress, e);
		}

		if (addressIsBlocked) {

			log.warn("remoteAddress " + remoteAddress + " is restricted.");

			response.getOutputStream().print("Restricted");
			response.flushBuffer();
			return;
		}

		// User is allowed to continue.
		chain.doFilter(request, response);
	}

	/**
	 * Gets access to audit log service.
	 *
	 * @return      AuditLogService Spring service bean.
	 */
	private AuditLogService getAuditLogService(ServletRequest request) {

		return (AuditLogService) SpringLookup.findService(request, "auditLogService");
	}


	@Override
	public void init(FilterConfig config) throws ServletException
	{
	}

	@Override
	public void destroy() {
	}
}
