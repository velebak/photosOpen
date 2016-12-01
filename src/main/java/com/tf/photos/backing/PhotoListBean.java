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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Produces;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseId;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.io.IOUtils;
import org.omnifaces.util.Faces;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tf.photos.backing.filter.PhotoListFilter;
import com.tf.photos.model.LargePhoto;
import com.tf.photos.model.Photo;
import com.tf.photos.model.PhotoTableModel;
import com.tf.photos.model.WebEvent;
import com.tf.photos.model.WebUserSession;
import com.tf.photos.service.AuditLogService;
import com.tf.photos.service.ImageService;
import com.tf.photos.service.util.SpringLookup;

/**
 * @author Heather Stevens
 * Date: 5/11/13
 */
@Named
@SessionScoped
public class PhotoListBean implements Serializable {
    private static final long serialVersionUID = 1601516567316562497L;
	private static final Logger log = LoggerFactory.getLogger(PhotoListBean.class);

	@Inject
	private WebUserSession webUserSession;

	@Inject
	private PhotoListFilter photoListFilter;

	private static final String PHOTO_LIST_URI = "/photos/photos.jsf?faces-redirect=true";
	private static final String PHOTO_UPLOAD_URI = "/photos/photoUpload.jsf";
	private static final String PHOTO_DISPLAY_URI = "/photos/photoDisplay.jsf";
	private static final String JPG = ".jpg";
	private static final String JPEG = ".jpeg";

	// List of photos recently uploaded.
	private List<Photo> newlyUpdatedPhotoList = new ArrayList<>();
	private List<Photo> selectedPhotoList = new ArrayList<>();
	private LargePhoto currentLargePhoto;

	private PhotoTableModel photoTableModel;

    public PhotoListBean() {
    }

    @Named
    @Produces
    @RequestScoped
    public PhotoTableModel getPhotoList() {
	    if (photoTableModel == null)  {
		    photoTableModel = new PhotoTableModel();
		    photoTableModel.setPhotoListFilter(photoListFilter);
	    }

	    return photoTableModel;
    }

	/**
	 * Loads or reloads photos from database. Initializes filters to start state.
	 *
	 * @return      JSF 2 navigation uri.
	 */
	public String initializePhotoList() {

		getPhotoList().setReloadData(true);
		getPhotoList().loadPhotos();
		photoListFilter.initializeFilters(getPhotoList().getUserNamesInList(), getPhotoList().getYearsInList());
		return PHOTO_LIST_URI;
	}

	/**
	 * Get photo as a stream. Parameter is expected to be present.
	 *
	 * @return      StreamedContent for pf graphic image.
	 */
	public StreamedContent getPhotoStreamedContent() {

		try
		{
			FacesContext context = FacesContext.getCurrentInstance();

			if (context.getCurrentPhaseId() == PhaseId.RENDER_RESPONSE)
			{
				// So, we're rendering the view. Return a stub StreamedContent so that it will generate right URL.
				return new DefaultStreamedContent();
			}

			// So, browser is requesting the image. Get ID value from actual request param.
			String photoIdStr = context.getExternalContext().getRequestParameterMap().get("photoId");
			if (photoIdStr == null)
			{
				log.error("No photoParamId found!!");
				return null;
			}

			Photo photo = photoTableModel.getRowData(photoIdStr);
			if (photo == null)
			{
				log.error("Failed to find photo in map with id " + photoIdStr);
				return null;
			}

			return new DefaultStreamedContent(new ByteArrayInputStream(photo.getPhotoContent()), photo.getPhotoType());

		}
		catch (Exception e) {
			log.error("Failed to find photo", e);
		}

		return null;
	}

	/**
	 * Looks up large photo for later use.
	 *
	 * @param photo     Photo containing id of large photo
	 */
	public String setupLargePhoto(Photo photo) {

		log.info("setup large photo..." + photo.getOriginalFileName()+ " lrg id " + photo.getLargePhotoId());

		try {
			ImageService imageService = (ImageService) SpringLookup.findService("imageService");
			currentLargePhoto = imageService.getLargePhotoById(photo.getLargePhotoId());

			if (currentLargePhoto == null){
				return null;
			}

			if (currentLargePhoto.getPhotoType() == null) {
				currentLargePhoto.setPhotoType(photo.getPhotoType());
			}

		}
		catch (Exception e) {
			log.error("Failed to lookup large photo content " + photo.getLargePhotoId(), e);
			getAuditLogService().recordEvent(webUserSession.getWebUser(), e, WebEvent.EVENT_ERROR_OCCURRED);
		}

		log.info("setup large photo.  done");
		return PHOTO_DISPLAY_URI;
	}

	/**
	 * Get original or large photo as a stream. Parameter is expected to be present.
	 *
	 * @return      StreamedContent for pf graphic image.
	 */
	public StreamedContent getLargePhotoStreamedContent() {

		StreamedContent streamedContent = null;
		FacesContext context = FacesContext.getCurrentInstance();

		if (context.getCurrentPhaseId() == PhaseId.RENDER_RESPONSE) {
			// So, we're rendering the view. Return a stub StreamedContent so that it will generate right URL.
			return new DefaultStreamedContent();
		}

		try {
			streamedContent = new DefaultStreamedContent(new ByteArrayInputStream(currentLargePhoto.getPhotoContent()), currentLargePhoto.getPhotoType());
			log.info("Returning content for photo " + currentLargePhoto.getPhotoId());
		}
		catch (Exception e) {
			log.error("Failed to lookup large photo content " + currentLargePhoto.getPhotoId(), e);
			getAuditLogService().recordEvent(webUserSession.getWebUser(), e, WebEvent.EVENT_ERROR_OCCURRED);
		}

		return streamedContent;
	}

	/**
	 * Get the photo update page ready.
	 *
	 * @return  JSF Navigation uri.
	 */
	public String initializePhotoUpload() {

		newlyUpdatedPhotoList = new ArrayList<>();
		return PHOTO_UPLOAD_URI;
	}

	/**
	 *  Navigates user back to photo list on ajax call.
	 */
	public void completedUpload() {

		try
		{
			Faces.redirect(Faces.getRequestBaseURL() + PHOTO_LIST_URI);
		} catch (IOException e)
		{
			log.error("Failed to redirect to photo list.", e);
		}
	}

	/**
	 * Process file upload event. Saves photo to photo list.
	 *
	 * @param event     File upload event to process.
	 */
	public void handleFileUpload(FileUploadEvent event) {
		FacesMessage message = new FacesMessage("Successful", event.getFile().getFileName() + " was uploaded.");
		FacesContext.getCurrentInstance().addMessage(null, message);

		Photo photo = convertUploadedFileToPhoto(event.getFile());

		if (photo != null)
		{
			processSavePhoto(photo);
			newlyUpdatedPhotoList.add(photo);
		}
	}

	/**
	 * Converts uploaded file into photo model.
	 *
	 * @param uploadedFile      Newly uploaded photo to convert.
	 * @return                  Photo model representation.
	 */
	private Photo convertUploadedFileToPhoto(final UploadedFile uploadedFile) {

		if (uploadedFile == null) {
			log.error("Event fired without file content!");
			return null;
		}

		log.info("Uploaded file name " + uploadedFile.getFileName() + " " + uploadedFile.getContentType() + " " + uploadedFile.getSize());

		Photo photo = new Photo();
		photo.setPhotoContent(getContents(uploadedFile));

		if (photo.getPhotoContent() == null || photo.getPhotoContent().length == 0) {
			return null;
		}

		photo.setUserId(webUserSession.getWebUser().getId());
		photo.setUserName(webUserSession.getWebUser().getUserName());
		photo.setPhotoName(derivePhotoName(uploadedFile.getFileName()));
		photo.setOriginalFileName(uploadedFile.getFileName());
		photo.setPhotoType(uploadedFile.getContentType());
		photo.setSize(uploadedFile.getSize());
		photo.setUploaded(new Date());

		return photo;
	}

	/**
	 * Derives name of photo from original file name.
	 *
	 * @param fileName      Name of file that use just uploaded.
	 * @return              Name of photo.
	 */
	private String derivePhotoName(String fileName) {

		String name = fileName;
		if (fileName.toLowerCase().endsWith(JPG)) {
			name = fileName.substring(0, fileName.length() - 4);
		}
		else if (fileName.toLowerCase().endsWith(JPEG)) {
			name = fileName.substring(0, fileName.length() - 5);
		}

		return name;
	}

	/**
	 * Pulls photo contents in the form of a byte array form uploaded file.
	 *
	 * @param uploadedFile      PF uploaded file.
	 * @return                  Photo in the form or a byte array.
	 */
	public byte[] getContents(final UploadedFile uploadedFile) {

		try {
			if (uploadedFile.getContents() != null && uploadedFile.getContents().length > 0) {
				return uploadedFile.getContents();
			}

			if (uploadedFile.getSize() > 0) {

				byte[] bytes = IOUtils.toByteArray(uploadedFile.getInputstream());

				if (bytes != null && bytes.length > 0) {
					return bytes;
				}
				else {
					log.error("Failed to read input stream for file named " + uploadedFile.getFileName());
				}
			}
			else
			{
				log.error("Failed to load photo, no content found.");
			}
		}
		catch (IOException e) {
			log.error("Failed to read file stream.", e);
			getAuditLogService().recordEvent(webUserSession.getWebUser(), e, WebEvent.EVENT_ERROR_OCCURRED);
		}

		return null;
	}

	/**
	 * Removes the selected photos from the list.
	 */
	public void removeSelectedPhotos() {
		if (selectedPhotoList.size() > 0) {
			log.info("remove called " + selectedPhotoList.size());
			ImageService imageService = (ImageService) SpringLookup.findService("imageService");

			for (Photo photoToRemove: selectedPhotoList) {
				imageService.removePhoto(photoToRemove);
				getAuditLogService().recordEvent(webUserSession.getWebUser(), WebEvent.EVENT_PHOTO_REMOVED, photoToRemove.getOriginalFileName());
			}

			selectedPhotoList.clear();
			photoTableModel.setReloadData(true);
		}
		else {
			log.info("no photos selected.");
		}

	}

	/**
	 * Starts slide show using the selected photos from the list.
	 */
	public void startSlideShow() {
		if (selectedPhotoList.size() > 0) {
			log.info("sss called " + selectedPhotoList.size());
		}
		else {
			log.info("no photos selected.");
		}

	}

	/**
	 * Process image to determine further information about the photo.
	 *
	 * @param photo     Photograph to process.
	 */
	private void processSavePhoto(Photo photo) {

		try {
			ImageService imageService = (ImageService) SpringLookup.findService("imageService");
			imageService.processSavePhoto(photo);
			photoTableModel.setReloadData(true);
			log.info("Added photo with id " + photo.getId());
			getAuditLogService().recordEvent(webUserSession.getWebUser(), WebEvent.EVENT_PHOTO_UPLOADED, photo.getOriginalFileName());
		}
		catch (Exception e) {
			log.error("Failed to process and save photo " + photo.getOriginalFileName(), e);
			getAuditLogService().recordEvent(webUserSession.getWebUser(), e, WebEvent.EVENT_ERROR_OCCURRED);
		}
	}

	public List<Photo> getNewlyUpdatedPhotoList()
	{
		return newlyUpdatedPhotoList;
	}

	public List<Photo> getSelectedPhotoList() {
		return selectedPhotoList;
	}

	public void setSelectedPhotoList(List<Photo> selectedPhotoList) {
		this.selectedPhotoList = selectedPhotoList;
	}

	private AuditLogService getAuditLogService() {
		return (AuditLogService)SpringLookup.findService("auditLogService");
	}
}
