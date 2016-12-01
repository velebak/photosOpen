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
package com.tf.photos.service;

import com.tf.photos.model.AuditLog;
import com.tf.photos.model.KnownBad;
import com.tf.photos.model.KnownSafe;
import com.tf.photos.model.WebEvent;
import com.tf.photos.model.WebUser;

import java.util.List;
import java.util.Map;

/**
 * @author Heather
 * Date: 6/27/14
 */
public interface AuditLogService {

	/**
	 * Record user event on web site.
	 *
	 * @param webUser       Web user action or event.
	 * @param adminUser     Admin web user action or event. Admin who changed a web user.
	 * @param event         The action or event that occurred.
	 * @param comment       Comment for further information about the event.
	 */
	void recordEvent(WebUser webUser, WebUser adminUser, WebEvent event, String comment);

	/**
	 * Record user event on web site.
	 *
	 * @param webUser       Web user action or event.
	 * @param adminUser     Admin web user action or event. Admin who changed a web user.
	 * @param event         The action or event that occurred.
	 */
	void recordEvent(WebUser webUser, WebUser adminUser, WebEvent event);

	/**
	 * Record user event on web site.
	 *
	 * @param webUser       Web user action or event.
	 * @param throwable     Throwable exception to log.
	 * @param event         The action or event that occurred.
	 */
	void recordEvent(WebUser webUser, Throwable throwable, WebEvent event);

	/**
	 * Record user event on web site.
	 *
	 * @param webUser       Web user action or event.
	 * @param event         The action or event that occurred.
	 * @param comment       Comment for further information about the event.
	 */
	void recordEvent(WebUser webUser, WebEvent event, String comment);

	/**
	 * Record user event on web site.
	 *
	 * @param webUser       Web user action or event.
	 * @param event         The action or event that occurred.
	 */
	void recordEvent(WebUser webUser, WebEvent event);

	/**
	 * Record user event on web site.
	 *
	 * @param event             The action or event that occurred.
	 * @param remoteAddress     IP address that originated event.
	 * @param comment           Comment for further information about the event.
	 */
	void recordEvent(WebEvent event, String remoteAddress, String comment);

	/**
	 * Returns all audit log events.
	 *
	 * @return      List of audit logs.
	 */
	List<AuditLog> findEvents();

	/**
	 * Lookup audit log by id.
	 *
	 * @param auditLogId        Primary key.
	 * @return                  Audit log record.
	 */
	AuditLog getAuditLogById(String auditLogId);

	void addKnownBad(String remoteAddress);
	void removeKnownBad(String knownBadId);
	void updateKnownBad(KnownBad knownBad);
	KnownBad getKnownBadById(String knownBadId);
	KnownBad getKnownBadByRemoteAddress(String remoteAddress);
	List<KnownBad> findAllKnownBad();
	Map<String,String> getBlockedMap();

	void addKnownSafe(String remoteAddress);
	void removeKnownSafe(String knownSafeId);
	void updateKnownSafe(KnownSafe knownSafe);
	KnownSafe getKnownSafeById(String knownSafeId);
	KnownSafe getKnownSafeByRemoteAddress(String remoteAddress);
	List<KnownSafe> findAllKnownSafe();
	void processFailedLoginAttempts(String remoteAddress, WebUser webUser);
}
