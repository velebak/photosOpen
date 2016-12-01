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
package com.tf.photos.service.repositories;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import com.tf.photos.model.AuditLog;
import com.tf.photos.model.WebEvent;

/**
 * Saves and retrieves audit logs to and from database.
 *
 * @author Heather Stevens on 6/28/14
 */
@Repository("auditLogRepository")
public class AuditLogRepositoryImpl implements AuditLogRepository, Serializable {

	private static final long serialVersionUID = -8365438335994982932L;

	@Autowired
	protected MongoOperations mongoTemplate;

	public AuditLogRepositoryImpl() {
	}

	@Override
	public AuditLog getAuditLogById(String auditLogId)
	{
		return mongoTemplate.findById(auditLogId, AuditLog.class);
	}

	@Override
	public void saveAuditLog(AuditLog auditLog)
	{
		mongoTemplate.save(auditLog);
	}

	@Override
	public void removeAuditLog(String auditLogId) {

		Query query = new Query(Criteria.where("id").is(auditLogId));
		mongoTemplate.remove(query, AuditLog.class);
	}

	@Override
	public AuditLog getLastSuccessfulLogin(String remoteAddress) {

		AuditLog auditLog = null;
		Query query = new Query(Criteria.where("remoteAddress").is(remoteAddress).and("webEvent").is(WebEvent.EVENT_USER_LOGIN));

		List<AuditLog> list = mongoTemplate.find(query, AuditLog.class);
		list.sort(new Comparator<AuditLog>() {
			@Override
			public int compare(AuditLog o1, AuditLog o2) {

				return - o1.getEventDate().compareTo(o2.getEventDate());
			}
		});

		if (list != null && list.size() > 0) {
			auditLog = list.get(0);
		}
		return auditLog;
	}

	@Override
	public Integer getFailedLoginAttemptsSince(String remoteAddress, Date since) {

		Integer failed = 0;
		Criteria criteria = Criteria.where("remoteAddress").is(remoteAddress).and("webEvent").is(WebEvent.EVENT_USER_LOGIN_FAILED);
		if (since != null) {
			criteria.and("eventDate").gt(since);
		}

		Query query = new Query(criteria);

		List<AuditLog> list = mongoTemplate.find(query, AuditLog.class);
		if (list != null) {
			failed = list.size();
		}
		return failed;
	}

	@Override
	public List<AuditLog> findAuditLogs() {
		return mongoTemplate.findAll(AuditLog.class);
	}
}
