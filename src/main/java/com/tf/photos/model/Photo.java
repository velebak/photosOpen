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
package com.tf.photos.model;

import java.io.Serializable;
import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author Heather Stevens
 * Date: 6/1/2014
 *
 * Models a photograph and some of its embedded properties.
 */
@Document
public class Photo implements Serializable {
    private static final long serialVersionUID = 8830879444033149233L;

	@Id
	private String id;

	private String userId;
	private String userName;
	private String mediumPhotoId;
	private String largePhotoId;

    private String photoName;
    private String description;
    private Integer rating;
	private String originalFileName;
	private Long size;

	private String url;
	private byte[] photoContent;

    private String photoType;

	private Date taken;
	private Date uploaded;

	// Exif SubIFD
	private Integer height;
	private Integer width;
	private String exposureTime;
	private String exposureBias;
	private String aperture;
	private String isoSpeed;
	private String whiteBalance;
	private String focalLength;
	private String lensModel;

	// Exif IFD0
	private String cameraMake;
	private String cameraModel;

	// GPS
	private Double gpsLatitude;
	private String gpsLatitudeRef;
	private Double gpsLongitude;
	private String gpsLongitudeRef;
	private String gpsAltitude;
	private String gpsAltitudeRef;
	private String gpsTrack;

    public Photo() {
    }

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUserId()
	{
		return userId;
	}

	public void setUserId(String userId)
	{
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getMediumPhotoId()
	{
		return mediumPhotoId;
	}

	public void setMediumPhotoId(String mediumPhotoId)
	{
		this.mediumPhotoId = mediumPhotoId;
	}

	public String getLargePhotoId()
	{
		return largePhotoId;
	}

	public void setLargePhotoId(String largePhotoId)
	{
		this.largePhotoId = largePhotoId;
	}

	public String getPhotoName() {

		if (photoName == null && originalFileName != null) {
			photoName = originalFileName;
		}

        return photoName;
    }

    public void setPhotoName(String photoName) {
        this.photoName = photoName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public byte[] getPhotoContent() {
        return photoContent;
    }

    public void setPhotoContent(byte[] photoContent) {
        this.photoContent = photoContent;
    }

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

	public Integer getRating()
	{
		return rating;
	}

	public void setRating(Integer rating)
	{
		this.rating = rating;
	}

	public String getOriginalFileName()
	{
		return originalFileName;
	}

	public void setOriginalFileName(String originalFileName)
	{
		this.originalFileName = originalFileName;
	}

	public Long getSize()
	{
		return size;
	}

	public void setSize(Long size)
	{
		this.size = size;
	}

	public String getPhotoType()
	{
		return photoType;
	}

	public void setPhotoType(String photoType)
	{
		this.photoType = photoType;
	}

	public Date getTaken()
	{
		return taken;
	}

	public void setTaken(Date taken)
	{
		this.taken = taken;
	}

	public Date getUploaded()
	{
		return uploaded;
	}

	public void setUploaded(Date uploaded)
	{
		this.uploaded = uploaded;
	}

	public Integer getHeight()
	{
		return height;
	}

	public void setHeight(Integer height)
	{
		this.height = height;
	}

	public Integer getWidth()
	{
		return width;
	}

	public void setWidth(Integer width)
	{
		this.width = width;
	}

	public String getExposureTime()
	{
		return exposureTime;
	}

	public void setExposureTime(String exposureTime)
	{
		this.exposureTime = exposureTime;
	}

	public String getExposureBias()
	{
		return exposureBias;
	}

	public void setExposureBias(String exposureBias)
	{
		this.exposureBias = exposureBias;
	}

	public String getAperture()
	{
		return aperture;
	}

	public void setAperture(String aperture)
	{
		this.aperture = aperture;
	}

	public String getIsoSpeed()
	{
		return isoSpeed;
	}

	public void setIsoSpeed(String isoSpeed)
	{
		this.isoSpeed = isoSpeed;
	}

	public String getWhiteBalance()
	{
		return whiteBalance;
	}

	public void setWhiteBalance(String whiteBalance)
	{
		this.whiteBalance = whiteBalance;
	}

	public String getFocalLength()
	{
		return focalLength;
	}

	public void setFocalLength(String focalLength)
	{
		this.focalLength = focalLength;
	}

	public String getCameraMake()
	{
		return cameraMake;
	}

	public void setCameraMake(String cameraMake)
	{
		this.cameraMake = cameraMake;
	}

	public String getCameraModel()
	{
		return cameraModel;
	}

	public void setCameraModel(String cameraModel)
	{
		this.cameraModel = cameraModel;
	}

	public String getLensModel()
	{
		return lensModel;
	}

	public void setLensModel(String lensModel)
	{
		this.lensModel = lensModel;
	}

	public Double getGpsLatitude()
	{
		return gpsLatitude;
	}

	public void setGpsLatitude(Double gpsLatitude)
	{
		this.gpsLatitude = gpsLatitude;
	}

	public String getGpsLatitudeRef()
	{
		return gpsLatitudeRef;
	}

	public void setGpsLatitudeRef(String gpsLatitudeRef)
	{
		this.gpsLatitudeRef = gpsLatitudeRef;
	}

	public Double getGpsLongitude()
	{
		return gpsLongitude;
	}

	public void setGpsLongitude(Double gpsLongitude)
	{
		this.gpsLongitude = gpsLongitude;
	}

	public String getGpsLongitudeRef()
	{
		return gpsLongitudeRef;
	}

	public void setGpsLongitudeRef(String gpsLongitudeRef)
	{
		this.gpsLongitudeRef = gpsLongitudeRef;
	}

	public String getGpsAltitude()
	{
		return gpsAltitude;
	}

	public void setGpsAltitude(String gpsAltitude)
	{
		this.gpsAltitude = gpsAltitude;
	}

	public String getGpsAltitudeRef()
	{
		return gpsAltitudeRef;
	}

	public void setGpsAltitudeRef(String gpsAltitudeRef)
	{
		this.gpsAltitudeRef = gpsAltitudeRef;
	}

	public String getGpsTrack()
	{
		return gpsTrack;
	}

	public void setGpsTrack(String gpsTrack)
	{
		this.gpsTrack = gpsTrack;
	}
}
