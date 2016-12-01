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

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifIFD0Directory;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import com.drew.metadata.exif.GpsDirectory;
import com.mongodb.MongoException;
import com.tf.photos.model.LargePhoto;
import com.tf.photos.model.MediumPhoto;
import com.tf.photos.model.Photo;
import com.tf.photos.service.repositories.LargePhotoRepository;
import com.tf.photos.service.repositories.MediumPhotoRepository;
import com.tf.photos.service.repositories.PhotoRepository;

/**
 * @author Heather Stevens
 * Date: 6/23/13
 */
@Service("imageService")
public class ImageServiceImpl implements ImageService, Serializable {

	private static final Logger log = LoggerFactory.getLogger(ImageServiceImpl.class);
	private static final long serialVersionUID = -87445144021475771L;
	private static final int THUMBNAIL_WIDTH = 72;
	private static final int THUMBNAIL_HEIGHT = 72;

	private static final int SCREEN_WIDTH = 1920;
	private static final int SCREEN_HEIGHT = 1080;
	private static final int LARGER_WIDTH = 3000;
	private static final int LARGER_HEIGHT = 2000;

	@Autowired
	PhotoRepository photoRepository;

	@Autowired
	MediumPhotoRepository mediumPhotoRepository;

	@Autowired
	LargePhotoRepository largePhotoRepository;

	public ImageServiceImpl() {
	}

	/**
	 * Adds meta data to photo, creates thumbnail, medium and large image content. Saves all images and meta data
	 * to database.
	 *
	 * @param photo     Photo to process and save.
	 * @return          Photo saved to db, with content updated as thumbnail.
	 */
	@Override
	public synchronized Photo processSavePhoto(Photo photo) {

		addPhotoInfo(photo);

		byte[] originalImageContent = photo.getPhotoContent();

		try {
			byte[] thumbnail = createThumbnail(originalImageContent);
			if (thumbnail != null && thumbnail.length > 0) {
				photo.setPhotoContent(thumbnail);
			}
			else {
				log.error("Unable to create thumbnail sized photo " + photo.getOriginalFileName());
			}

			savePhoto(photo);
		}
		catch (Exception e) {
			log.error("Failed to create thumbnail photo " + photo.getOriginalFileName(), e);
		}
		createLargePhoto(photo, originalImageContent);

		try {
			if (photo.getWidth() != 0 && photo.getHeight() != 0) {
				MediumPhoto mediumPhoto = createMedium(originalImageContent, photo.getPhotoType(), photo.getWidth(), photo.getHeight());

				if (mediumPhoto != null) {
					mediumPhoto.setPhotoId(photo.getId());
					mediumPhotoRepository.saveMediumPhoto(mediumPhoto);
					photo.setMediumPhotoId(mediumPhoto.getId());
				}

				// Re-save photo with medium and large photo id's.
				savePhoto(photo);
			}
		}
		catch (Exception e) {
			log.error("Failed to create medium photo size " + photo.getOriginalFileName(), e);
		}

		return photo;
	}

	/**
	 * Pull properties information out of photo content.
	 *
	 * @param photo     Photo to process.
	 */
	private synchronized void addPhotoInfo(Photo photo) {

		try {
			BufferedInputStream inputStream = new BufferedInputStream(new ByteArrayInputStream(photo.getPhotoContent()));
			Metadata metadata = ImageMetadataReader.readMetadata(inputStream, false);
			addMetaData(photo, metadata);
		}
		catch (Exception e) {
			log.error("Failed to add photo information to " + photo.getOriginalFileName(), e);
		}
    }

	/**
	 * Creates thumbnail sized photo from original photo.
	 *
	 * @param sourcePhotoContent    Byte array containing original image.
	 * @return                      Byte array resized image.
	 */
	private synchronized byte[] createThumbnail(byte[] sourcePhotoContent) {

		return resizeImageAsJpeg(sourcePhotoContent, THUMBNAIL_WIDTH, THUMBNAIL_HEIGHT);
	}

	private synchronized boolean createLargePhoto(Photo photo, byte[] originalImageContent) {

		try {
			LargePhoto largePhoto = new LargePhoto();
			largePhoto.setPhotoContent(originalImageContent);
			largePhoto.setPhotoId(photo.getId());
			largePhoto.setPhotoType(photo.getPhotoType());
			largePhoto.setWidth(photo.getWidth());
			largePhoto.setHeight(photo.getHeight());

			try {
				largePhotoRepository.saveLargePhoto(largePhoto);
			}
			catch (MongoException  e) {
				log.warn("Large photo size is over mongo db version 2 limit, full photo size " + originalImageContent.length);
				MediumPhoto mediumPhoto =  createMedium(originalImageContent, photo.getPhotoType(), photo.getWidth(), photo.getHeight(), LARGER_WIDTH, LARGER_HEIGHT);
				largePhoto.setPhotoContent(mediumPhoto.getPhotoContent());
				largePhoto.setPhotoType(mediumPhoto.getPhotoType());
				largePhoto.setWidth(mediumPhoto.getWidth());
				largePhoto.setHeight(mediumPhoto.getHeight());
				largePhotoRepository.saveLargePhoto(largePhoto);
			}

			photo.setLargePhotoId(largePhoto.getId());
			savePhoto(photo);

			log.info("Created large photo, size " + largePhoto.getPhotoContent().length + " width " +
					largePhoto.getWidth() + " height " + largePhoto.getHeight());
		}
		catch (Exception e) {
			log.error("Failed to create large photo size " + photo.getOriginalFileName(), e);
			return false;
		}

		return true;
	}

	/**
	 * Creates medium sized photo from original photo.
	 *
	 * @param sourcePhotoContent    Byte array containing original image.
   	 * @param photoType             Type of photo.
	 * @param originalWidth         Width of original image.
	 * @param originalHeight        Height of original image.
	 * @return                      Newly created scaled photo.
	 */
	private synchronized MediumPhoto createMedium(byte[] sourcePhotoContent, String photoType, int originalWidth, int originalHeight) {
		return createMedium(sourcePhotoContent, photoType, originalWidth, originalHeight, SCREEN_WIDTH, SCREEN_HEIGHT);
	}

	/**
	 * Creates medium sized photo from original photo.
	 *
	 * @param sourcePhotoContent    Byte array containing original image.
	 * @param photoType             Type of photo.
	 * @param originalWidth         Width of original image.
	 * @param originalHeight        Height of original image.
	 * @param scaledWidth           New width of image.
	 * @param scaledHeight          New height of image.
	 * @return                      Newly created scaled photo.
	 */
	private synchronized MediumPhoto createMedium(byte[] sourcePhotoContent, String photoType, int originalWidth, int originalHeight, int scaledWidth, int scaledHeight) {

		MediumPhoto mediumPhoto = new MediumPhoto();
		if (originalWidth < scaledWidth || originalHeight < scaledHeight) {
			mediumPhoto.setPhotoContent(sourcePhotoContent);
			mediumPhoto.setWidth(originalWidth);
			mediumPhoto.setHeight(originalHeight);
		}
		else
		{
			int newWidth = scaledWidth;
			int newHeight = scaledHeight;

			if (originalWidth > originalHeight) {
				float resizeRatio = ((float)newWidth) / ((float)originalWidth);
				newHeight = Math.round(resizeRatio * ((float)originalHeight));
			}
			else {
				float resizeRatio = ((float)newHeight) / ((float)originalHeight);
				newWidth = Math.round(resizeRatio * ((float)originalWidth));
			}

			mediumPhoto.setPhotoContent(resizeImageAsJpeg(sourcePhotoContent, newWidth, newHeight));

			if (mediumPhoto.getPhotoContent() == null || mediumPhoto.getPhotoContent().length == 0) {
				log.error("Failed to create medium photo.");
				return null;
			}

			mediumPhoto.setWidth(newWidth);
			mediumPhoto.setHeight(newHeight);
		}

		log.info("Created medium photo, size " + mediumPhoto.getPhotoContent().length + " width " +
				mediumPhoto.getWidth() + " height " + mediumPhoto.getHeight());

		return mediumPhoto;
	}

	@Override
	public Photo getPhotoById(String photoId)
	{
		return photoRepository.getPhotoById(photoId);
	}

	@Override
	public LargePhoto getLargePhotoById(String largePhotoId)
	{
		return largePhotoRepository.getLargePhotoById(largePhotoId);
	}

	@Override
	public void savePhoto(Photo photo)
	{
		photoRepository.savePhoto(photo);
	}

	@Override
	public List<Photo> findPhotos()
	{
		return photoRepository.findPhotos();
	}

	/**
	 * Removes photo from storage.
	 *
	 * @param photo     Photo to remove.
	 */
	@Override
	public void removePhoto(Photo photo) {

		try {
			if (photo.getLargePhotoId() != null) {
				largePhotoRepository.removeLargePhoto(photo.getLargePhotoId());
			}

			if (photo.getMediumPhotoId() != null) {
				mediumPhotoRepository.removeMediumPhoto(photo.getMediumPhotoId());
			}

			photoRepository.removePhoto(photo);
			log.info("Finished removing photo with file name " +photo.getOriginalFileName());
		}
		catch (Exception e) {
			log.error("Failed to remove photo " + photo.getId() + " " + photo.getOriginalFileName(), e);
		}

	}

	/**
	 * Uses meta data to add properties about the photo to the photo model.
	 *
	 * @param photo         Photo to add properties too.
	 * @param metadata      Meta data containing properties read from photo byte array.
	 */
	private synchronized void addMetaData(Photo photo, Metadata metadata) {

		try {
			ExifSubIFDDirectory exifSubIFDDirectory = metadata.getDirectory(ExifSubIFDDirectory.class);

			if (exifSubIFDDirectory != null) {
				photo.setTaken(exifSubIFDDirectory.getDate(ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL));
				photo.setHeight(exifSubIFDDirectory.getInteger(ExifSubIFDDirectory.TAG_EXIF_IMAGE_HEIGHT));
				photo.setWidth(exifSubIFDDirectory.getInteger(ExifSubIFDDirectory.TAG_EXIF_IMAGE_WIDTH));
				photo.setAperture(exifSubIFDDirectory.getDescription(ExifSubIFDDirectory.TAG_APERTURE));
				photo.setExposureTime(exifSubIFDDirectory.getDescription(ExifSubIFDDirectory.TAG_EXPOSURE_TIME));
				photo.setIsoSpeed(exifSubIFDDirectory.getDescription(ExifSubIFDDirectory.TAG_ISO_EQUIVALENT));
				photo.setExposureBias(exifSubIFDDirectory.getDescription(ExifSubIFDDirectory.TAG_EXPOSURE_BIAS));
				photo.setWhiteBalance(exifSubIFDDirectory.getDescription(ExifSubIFDDirectory.TAG_WHITE_BALANCE));
				photo.setFocalLength(exifSubIFDDirectory.getDescription(ExifSubIFDDirectory.TAG_FOCAL_LENGTH));
				photo.setLensModel(exifSubIFDDirectory.getDescription(ExifSubIFDDirectory.TAG_LENS_MODEL));
			}
		}
		catch (Exception e) {
			log.error("Failed to get exif subIFD metadata for photo.", e);
		}

		try {
			ExifIFD0Directory exifIFD0Directory = metadata.getDirectory(ExifIFD0Directory.class);

			if (exifIFD0Directory != null) {
				photo.setCameraMake(exifIFD0Directory.getDescription(ExifIFD0Directory.TAG_MAKE));
				photo.setCameraModel(exifIFD0Directory.getDescription(ExifIFD0Directory.TAG_MODEL));
			}
		}
		catch (Exception e) {
			log.error("Failed to get exif IFDO metadata for photo.", e);
		}

		try {
			GpsDirectory gpsDirectory = metadata.getDirectory(GpsDirectory.class);

			if (gpsDirectory != null && gpsDirectory.getGeoLocation() != null) {
				photo.setGpsLongitude(gpsDirectory.getGeoLocation().getLongitude());
				photo.setGpsLongitudeRef(gpsDirectory.getDescription(GpsDirectory.TAG_GPS_LONGITUDE_REF));
				photo.setGpsLatitude(gpsDirectory.getGeoLocation().getLatitude());
				photo.setGpsLatitudeRef(gpsDirectory.getDescription(GpsDirectory.TAG_GPS_LATITUDE_REF));
				photo.setGpsAltitude(gpsDirectory.getDescription(GpsDirectory.TAG_GPS_ALTITUDE));
				photo.setGpsAltitudeRef(gpsDirectory.getDescription(GpsDirectory.TAG_GPS_ALTITUDE_REF));
				photo.setGpsTrack(gpsDirectory.getDescription(GpsDirectory.TAG_GPS_TRACK));
			}
		}
		catch (Exception e) {
			log.error("Failed to get GPS metadata for photo.", e);
		}
	}

	/**
	 * Resizes image width and height.
	 *
	 * @param sourcePhotoContent    Original image content in form of btye array.
	 * @param width                 New width to use.
	 * @param height                New height to use.
	 * @return                      New image byte array.
	 */
	private synchronized byte[] resizeImageAsJpeg(byte[] sourcePhotoContent, int width, int height) {

		ByteArrayInputStream imageInputStream = null;
		ByteArrayOutputStream imageOutputStream = null;
		try {
			imageInputStream = new ByteArrayInputStream(sourcePhotoContent);
			Image image = ImageIO.read(imageInputStream);

			Toolkit.getDefaultToolkit().createImage(sourcePhotoContent);

			BufferedImage resizedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
			Graphics2D g = resizedImage.createGraphics();
			g.drawImage(image, 0, 0, width, height, null);
			g.dispose();

			imageOutputStream = new ByteArrayOutputStream();
			ImageIO.write(resizedImage, "jpg", imageOutputStream);
			imageOutputStream.flush();
			return imageOutputStream.toByteArray();
		}
		catch (Exception e) {
			log.error("Error while resizing image.", e);
		}
		finally {
			try
			{
				if (imageInputStream != null)
				{
					imageInputStream.close();
				}
			}
			catch (IOException e) {
				log.error("Failed to close photo input stream.");
			}
			try
			{
				if (imageOutputStream != null)
				{
					imageOutputStream.close();
				}
			}
			catch (IOException e) {
				log.error("Failed to close photo output stream.");
			}
		}

		return null;
	}

}
