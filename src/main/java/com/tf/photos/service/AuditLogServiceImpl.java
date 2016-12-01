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

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.omnifaces.util.Faces;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tf.photos.model.AuditLog;
import com.tf.photos.model.KnownBad;
import com.tf.photos.model.KnownSafe;
import com.tf.photos.model.WebEvent;
import com.tf.photos.model.WebUser;
import com.tf.photos.service.repositories.AuditLogRepository;
import com.tf.photos.service.repositories.KnownBadRepository;
import com.tf.photos.service.repositories.KnownSafeRepository;

/**
 * @author Heather
 * Date: 6/28/14
 */
@Service("auditLogService")
public class AuditLogServiceImpl implements AuditLogService, Serializable {

	private static final long serialVersionUID = 401999666325341246L;

	@SuppressWarnings("unused")
	private static final Logger log = LoggerFactory.getLogger(AuditLogServiceImpl.class);
	private static ConcurrentHashMap<String,String> blockedMap = null;
	private static final int BLOCK_ON_FAIL_THRESHOLD = 12;

	@Autowired
    private AuditLogRepository auditLogRepository;

	@Autowired
	private KnownBadRepository knownBadRepository;

	@Autowired
	private KnownSafeRepository knownSafeRepository;


	public AuditLogServiceImpl() {
	}

	@Override
	public void recordEvent(WebUser webUser, WebUser adminUser, WebEvent event, String comment) {

		AuditLog auditLog = new AuditLog();

		auditLog.setWebUserId(webUser.getId());
		auditLog.setUserName(webUser.getUserName());
		if (adminUser != null) {
			auditLog.setAdminUserId(adminUser.getId());
			auditLog.setAdminUserName(adminUser.getUserName());
		}

		auditLog.setWebEvent(event);
		auditLog.setEventDate(new Date());
		auditLog.setComment(comment);

		if (webUser.getRemoteAddress() == null) {
			auditLog.setRemoteAddress(Faces.getRemoteAddr());
		}
		else {
			auditLog.setRemoteAddress(webUser.getRemoteAddress());
		}

		auditLogRepository.saveAuditLog(auditLog);
	}

	@Override
	public void recordEvent(WebUser webUser, WebUser adminUser, WebEvent event) {

		recordEvent(webUser, adminUser, event, null);
	}

	@Override
	public void recordEvent(WebUser webUser, WebEvent event, String comment) {

		recordEvent(webUser, null, event, comment);
	}

	@Override
	public void recordEvent(WebUser webUser, WebEvent event) {

		recordEvent(webUser, event, null);
	}

	@Override
	public void recordEvent(WebUser webUser, Throwable throwable, WebEvent event) {

		AuditLog auditLog = new AuditLog();

		auditLog.setWebUserId(webUser.getId());
		auditLog.setUserName(webUser.getUserName());

		auditLog.setWebEvent(event);
		auditLog.setEventDate(new Date());

		if (webUser.getRemoteAddress() == null) {
			auditLog.setRemoteAddress(Faces.getRemoteAddr());
		}
		else {
			auditLog.setRemoteAddress(webUser.getRemoteAddress());
		}

		auditLog.setComment(throwable.toString() + " " + ExceptionUtils.getStackTrace(throwable));

		auditLogRepository.saveAuditLog(auditLog);
	}

	@Override
	public void recordEvent(WebEvent event, String remoteAddress, String comment) {

		AuditLog auditLog = new AuditLog();
		auditLog.setWebEvent(event);
		auditLog.setEventDate(new Date());
		auditLog.setComment(comment);

		if (remoteAddress == null) {
			auditLog.setRemoteAddress(Faces.getRemoteAddr());
		}
		else {
			auditLog.setRemoteAddress(remoteAddress);
		}

		auditLogRepository.saveAuditLog(auditLog);
	}

	@Override
	public List<AuditLog> findEvents() {
		return auditLogRepository.findAuditLogs();
	}

	/**
	 * Lookup audit log by id.
	 *
	 * @param auditLogId        Primary key.
	 * @return                  Audit log record.
	 */
	@Override
	public AuditLog getAuditLogById(String auditLogId) {
		return auditLogRepository.getAuditLogById(auditLogId);
	}

	@Override
	public void addKnownBad(String remoteAddress) {

		KnownBad knownBad = new KnownBad();
		knownBad.setRemoteAddress(remoteAddress);
		knownBad.setCreatedDate(new Date());
		knownBadRepository.saveKnownBad(knownBad);
		getBlockedMap().put(remoteAddress, remoteAddress);
	}

	/**
	 * Remove known bad item using primary key.
	 *
	 * @param knownBadId        Primary key of known bad item.
	 */
	@Override
	public void removeKnownBad(String knownBadId) {

		KnownBad knownBad = getKnownBadById(knownBadId);
		getBlockedMap().remove(knownBad.getRemoteAddress());

		knownBadRepository.removeKnownBad(knownBadId);
	}

	@Override
	public void updateKnownBad(KnownBad knownBad) {
		knownBadRepository.saveKnownBad(knownBad);
	}

	@Override
	public KnownBad getKnownBadById(String knownBadId) {
		return knownBadRepository.getKnownBadById(knownBadId);
	}

	@Override
	public KnownBad getKnownBadByRemoteAddress(String remoteAddress) {
		return knownBadRepository.getKnownBadByRemoteAddress(remoteAddress);
	}

	@Override
	public List<KnownBad> findAllKnownBad() {
		return knownBadRepository.findAllKnownBad();
	}

	@Override
	public void addKnownSafe(String remoteAddress) {
		KnownSafe knownSafe = new KnownSafe();
		knownSafe.setRemoteAddress(remoteAddress);
		knownSafe.setCreatedDate(new Date());
		knownSafeRepository.saveKnownSafe(knownSafe);
	}

	@Override
	public void removeKnownSafe(String knownSafeId) {
		knownSafeRepository.removeKnownSafe(knownSafeId);
	}

	@Override
	public void updateKnownSafe(KnownSafe knownSafe) {
		knownSafeRepository.saveKnownSafe(knownSafe);
	}

	@Override
	public KnownSafe getKnownSafeById(String knownSafeId) {
		return knownSafeRepository.getKnownSafeById(knownSafeId);
	}

	@Override
	public KnownSafe getKnownSafeByRemoteAddress(String remoteAddress) {
		return knownSafeRepository.getKnownSafeByRemoteAddress(remoteAddress);
	}

	@Override
	public List<KnownSafe> findAllKnownSafe() {
		return knownSafeRepository.findAllKnownSafe();
	}

	/**
	 * Checks failed login attempt to see if the user's remote address should be blocked.
	 *
	 * @param remoteAddress     Remote IP Address of login attempt.
	 * @param webUser           Web user accessed by attempt if any.
	 */
	@Override
	public void processFailedLoginAttempts(String remoteAddress, WebUser webUser) {

		// Let known safe addresses slide.
		if (getKnownSafeByRemoteAddress(remoteAddress) != null) {
			log.info("is safe");
			return;
		}

		AuditLog lastSuccessfulLogin = auditLogRepository.getLastSuccessfulLogin(remoteAddress);

		Integer failedAttempts = 0;
		if (lastSuccessfulLogin != null) {
			log.info("last good login was on " + lastSuccessfulLogin.getEventDate());
			failedAttempts = auditLogRepository.getFailedLoginAttemptsSince(remoteAddress, lastSuccessfulLogin.getEventDate());
		}
		else {
			failedAttempts = auditLogRepository.getFailedLoginAttemptsSince(remoteAddress, null);
		}

		if (failedAttempts >= BLOCK_ON_FAIL_THRESHOLD) {
			log.warn("locking em out " + failedAttempts);

			addKnownBad(remoteAddress);
			AuditLog auditLog = new AuditLog();
			auditLog.setRemoteAddress(remoteAddress);
			auditLog.setWebEvent(WebEvent.EVENT_REMOTE_ADDRESS_BLOCKED);
			auditLog.setEventDate(new Date());
			auditLog.setComment("Failed login attempt threshold of " + failedAttempts + " was exceeded.");
		}
	}

	@Override
	public Map<String, String> getBlockedMap() {

		if (blockedMap == null) {
			List<KnownBad> knownBadList = findAllKnownBad();
			blockedMap = new ConcurrentHashMap<>(knownBadList.size());

			for (KnownBad knownBad: knownBadList) {
				blockedMap.put(knownBad.getRemoteAddress(), knownBad.getRemoteAddress());
			}
		}

		return blockedMap;
	}
}
