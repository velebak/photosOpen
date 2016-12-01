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

package com.tf.photos.backing;

import java.io.Serializable;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Produces;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tf.photos.backing.filter.AuditListFilter;
import com.tf.photos.backing.filter.KnownBadListFilter;
import com.tf.photos.backing.filter.KnownSafeListFilter;
import com.tf.photos.model.AuditLog;
import com.tf.photos.model.AuditLogTableModel;
import com.tf.photos.model.KnownBad;
import com.tf.photos.model.KnownBadTableModel;
import com.tf.photos.model.KnownSafe;
import com.tf.photos.model.KnownSafeTableModel;
import com.tf.photos.model.WebEvent;
import com.tf.photos.model.WebUserSession;
import com.tf.photos.service.AuditLogService;
import com.tf.photos.service.util.ResourceBundleUtil;
import com.tf.photos.service.util.SpringLookup;

/**
 * @author: Heather Stevens
 * Date: 5/11/13
 */
@Named
@SessionScoped
public class AuditLogListBean implements Serializable {

	@SuppressWarnings("unused")
	private static final Logger log = LoggerFactory.getLogger(AuditLogListBean.class);
	private static final long serialVersionUID = 1597235369084860296L;

	private static final String AUDIT_LOG_LIST_URI = "/user/audit/auditLogList.jsf";
	private static final String KNOWN_BAD_LIST_URI = "/user/audit/knownBadList.jsf";
	private static final String KNOWN_SAFE_LIST_URI = "/user/audit/knownSafeList.jsf";

	@Inject
	private WebUserSession webUserSession;

	@Inject
	private ResourceBundleUtil resourceBundleUtil;

	@Inject
	private AuditListFilter auditListFilter;

	@Inject
	private KnownBadListFilter knownBadListFilter;

	@Inject
	private KnownSafeListFilter knownSafeListFilter;

	private AuditLogTableModel auditLogTableModel = null;
	private AuditLog selectedAuditLog;

	private KnownBadTableModel knownBadTableModel = null;
	private KnownBad selectedKnownBad;

	private KnownSafeTableModel knownSafeTableModel = null;
	private KnownSafe selectedKnownSafe;

	private String newBadRemoteAddress;
	private String newSafeRemoteAddress;


	public AuditLogListBean() {
    }

	/**
	 * List of audit logs or user events.
	 *
	 * @return      List of audit log items.
	 */
    @Named
    @Produces
    @RequestScoped
    public AuditLogTableModel getAuditLogList() {

	    if (auditLogTableModel == null) {
		    auditLogTableModel = new AuditLogTableModel();
		    auditLogTableModel.setAuditListFilter(auditListFilter);
	    }

        return auditLogTableModel;
    }

	/**
	 * List of know bad remote addresses.
	 *
	 * @return  List of known bad items.
	 */
	@Named
	@Produces
	@RequestScoped
	public KnownBadTableModel getKnownBadList() {
		if (knownBadTableModel == null) {
			knownBadTableModel = new KnownBadTableModel();
			knownBadTableModel.setKnownBadListFilter(knownBadListFilter);
		}

		return knownBadTableModel;
	}

	/**
	 * List of known safe remote addresses.
	 *
	 * @return  List of known safe items.
	 */
	@Named
	@Produces
	@RequestScoped
	public KnownSafeTableModel getKnownSafeList() {
		if (knownSafeTableModel == null) {
			knownSafeTableModel = new KnownSafeTableModel();
			knownSafeTableModel.setKnownSafeListFilter(knownSafeListFilter);
		}

		return knownSafeTableModel;
	}

	/**
	 * Sets the reload audit log list flag and returns JSF 2 navigation string to list page.
	 *
	 * @return  JSF navigation.
	 */
	public String reloadList() {

		if (auditLogTableModel != null) {
			auditLogTableModel.setReloadData(true);
			auditListFilter.initializeFilters();
		}

		return AUDIT_LOG_LIST_URI;
	}

	/**
	 * Adds known bad remote address to known bad list if it is not already in the list. Removes remote address from
	 * known safe list if present.
	 */
	public void markSelectedBad() {

		addNewKnowBadAddress(selectedAuditLog.getRemoteAddress());
	}

	/**
	 * Save new blocked remote address to database created by user directly.
	 */
	public void saveUserEditedNewBlockedAddress() {
		addNewKnowBadAddress(getNewBadRemoteAddress());
		reloadKnownBadList();
	}

	/**
	 * Saves new remote address to blocked list and remote it from the safe list if present.
	 *
	 * @param knownBadAddress       Address to add to blocked list.
	 */
	private void addNewKnowBadAddress(String knownBadAddress) {
		KnownSafe knownSafe = getAuditLogService().getKnownSafeByRemoteAddress(knownBadAddress);
		if (knownSafe != null) {
			getAuditLogService().removeKnownSafe(knownSafe.getId());
			getAuditLogService().recordEvent(webUserSession.getWebUser(), WebEvent.EVENT_REMOTE_ADDRESS_UNSAFE, knownBadAddress);
		}

		if (getAuditLogService().getKnownBadByRemoteAddress(knownBadAddress) == null) {

			getAuditLogService().addKnownBad(knownBadAddress);
			getAuditLogService().recordEvent(webUserSession.getWebUser(), WebEvent.EVENT_REMOTE_ADDRESS_BLOCKED, knownBadAddress);
			FacesMessage message = new FacesMessage("Successful",
					knownBadAddress + " was added to blocked list.");
			FacesContext.getCurrentInstance().addMessage(null, message);
		}
		else {
			FacesMessage message = new FacesMessage("Already Blocked",
					knownBadAddress + " has already been added to blocked list.");
			FacesContext.getCurrentInstance().addMessage(null, message);
		}
	}

	/**
	 * Removes the selected known bad item.
	 */
	public String removeSelectedBlockedAddress() {

		getAuditLogService().removeKnownBad(selectedKnownBad.getId());
		getAuditLogService().recordEvent(webUserSession.getWebUser(), WebEvent.EVENT_REMOTE_ADDRESS_UNBLOCKED, selectedKnownBad.getRemoteAddress());
		return reloadKnownBadList();
	}

	/**
	 * Adds selected remote address to known safe list if it is not already in the list. Removes remote address from
	 * known blocked list if present.
	 */
	public void markSelectedSafe() {

		addNewKnownSafeAddress(selectedAuditLog.getRemoteAddress());
	}

	/**
	 * Save new safe remote address to database created by user directly.
	 */
	public void saveUserEditedNewSafeAddress() {
		addNewKnownSafeAddress(getNewSafeRemoteAddress());
		reloadKnownSafeList();
	}

	/**
	 * Adds selected remote address to known safe list if it is not already in the list. Removes remote address from
	 * known blocked list if present.
	 */
	private void addNewKnownSafeAddress(String knownSafeAddress) {

		KnownBad knownBad = getAuditLogService().getKnownBadByRemoteAddress(knownSafeAddress);
		if (knownBad != null) {
			getAuditLogService().removeKnownBad(knownBad.getId());
			getAuditLogService().recordEvent(webUserSession.getWebUser(), WebEvent.EVENT_REMOTE_ADDRESS_UNBLOCKED, knownSafeAddress);
		}

		if (getAuditLogService().getKnownSafeByRemoteAddress(knownSafeAddress) == null) {
			getAuditLogService().addKnownSafe(knownSafeAddress);
			getAuditLogService().recordEvent(webUserSession.getWebUser(), WebEvent.EVENT_REMOTE_ADDRESS_SAFE, knownSafeAddress);

			FacesMessage message = new FacesMessage("Successful",
					knownSafeAddress + " was added to safe list.");
			FacesContext.getCurrentInstance().addMessage(null, message);
		}
		else {
			FacesMessage message = new FacesMessage("Already Safe",
					knownSafeAddress + " has already been added to safe list.");
			FacesContext.getCurrentInstance().addMessage(null, message);
		}
	}

	/**
	 * Removes the selected known safe item.
	 */
	public String removeSelectedSafeAddress() {

		getAuditLogService().removeKnownSafe(selectedKnownSafe.getId());
		getAuditLogService().recordEvent(webUserSession.getWebUser(), WebEvent.EVENT_REMOTE_ADDRESS_UNSAFE, selectedKnownSafe.getRemoteAddress());
		return reloadKnownSafeList();
	}

	/**
	 * Sets reload flag for known bad list and returns JSF 2 navigation string to known bad list page.
	 *
	 * @return  JSF navigation.
	 */
	public String reloadKnownBadList() {

		if (knownBadTableModel != null) {
			knownBadTableModel.setReloadData(true);
		}

		return KNOWN_BAD_LIST_URI;
	}

	/**
	 * Sets reload flag for known safe list and returns JSF 2 navigation string to known safe list page.
	 *
	 * @return  JSF navigation.
	 */
	public String reloadKnownSafeList() {

		if (knownSafeTableModel != null) {
			knownSafeTableModel.setReloadData(true);
		}

		return KNOWN_SAFE_LIST_URI;
	}


	/**
	 * Gets access to audit log service.
	 *
	 * @return      AuditLogService Spring service bean.
	 */
	private AuditLogService getAuditLogService() {
		return (AuditLogService) SpringLookup.findService("auditLogService");
	}

	/**
	 * Gets the selected audit log.
	 *
	 * @return      Audit log item selected by data table.
	 */
	public AuditLog getSelectedAuditLog() {
		return selectedAuditLog;
	}

	/**
	 * Sets an audit log item, called by data table.
	 *
	 * @param selectedAuditLog      Item to select.
	 */
	public void setSelectedAuditLog(AuditLog selectedAuditLog) {
		this.selectedAuditLog = selectedAuditLog;
	}

	public KnownSafe getSelectedKnownSafe() {
		return selectedKnownSafe;
	}

	public void setSelectedKnownSafe(KnownSafe selectedKnownSafe) {
		this.selectedKnownSafe = selectedKnownSafe;
	}

	public KnownBad getSelectedKnownBad() {
		return selectedKnownBad;
	}

	public void setSelectedKnownBad(KnownBad selectedKnownBad) {
		this.selectedKnownBad = selectedKnownBad;
	}

	public String getNewBadRemoteAddress() {
		return newBadRemoteAddress;
	}

	public void setNewBadRemoteAddress(String newBadRemoteAddress) {
		this.newBadRemoteAddress = newBadRemoteAddress;
	}

	public String getNewSafeRemoteAddress() {
		return newSafeRemoteAddress;
	}

	public void setNewSafeRemoteAddress(String newSafeRemoteAddress) {
		this.newSafeRemoteAddress = newSafeRemoteAddress;
	}
}