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

import java.text.DecimalFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tf.photos.model.Address;

/**
 * @author Heather Stevens on 1/1/14.
 */
public class StringFormatUtil {

	private static final Logger log = LoggerFactory.getLogger(StringFormatUtil.class);

	/**
	 * Create billing address as a single String
	 *
	 * @return  Full billing address of current user.
	 */
	public static String formatSingleLine(Address address, String countryName, String stateName) {
		StringBuilder sb = new StringBuilder();
		boolean needPunctuation = false;

		if (address.getStreet1() != null && address.getStreet1().length() > 0) {
			sb.append(address.getStreet1());
			needPunctuation = true;
		}

		if (address.getStreet2() != null && address.getStreet2().length() > 0) {
			if (needPunctuation) {
				sb.append(", ");
			}
			sb.append(address.getStreet2());
			needPunctuation = true;
		}

		if (address.getCity() != null && address.getCity().length() > 0) {
			if (needPunctuation) {
				sb.append(", ");
			}
			sb.append(address.getCity());
			needPunctuation = true;
		}

		if (stateName != null) {
			if (needPunctuation) {
				sb.append(", ");
			}
			sb.append(stateName);
			needPunctuation = true;
		}

		if (address.getZipCode() != null && address.getZipCode().length() > 0) {
			if (needPunctuation) {
				sb.append(" ");
			}
			sb.append(address.getZipCode());
			needPunctuation = true;
		}

		if (countryName != null) {
			if (needPunctuation) {
				sb.append(", ");
			}
			sb.append(countryName);
		}

		return sb.toString();
	}

	private static final long K = 1024;
	private static final long M = K * K;
	private static final long G = M * K;
	private static final long T = G * K;
	private static final long[] dividers = new long[] { T, G, M, K, 1 };
	private static final String[] units = new String[] { "TB", "GB", "MB", "KB", "B" };

	/**
	 * Converts file size to human readable form.
	 *
	 * @param value     Raw file szie value.
	 * @return          Formatted file size.
	 */
	public static String fileSizeToString(final Object value){

		if (value == null) {
			return null;
		}

		try
		{
			Long inValue = Long.valueOf(value.toString());

			if (inValue < 1)
				throw new IllegalArgumentException("Invalid file size: " + value);
			String result = null;
			for (int i = 0; i < dividers.length; i++)
			{
				final long divider = dividers[i];
				if (inValue >= divider)
				{
					result = format(inValue, divider, units[i]);
					break;
				}
			}
			return result;
		}
		catch (Exception e) {
			log.error("Failed to convert file size. " + value, e);
		}

		return null;
	}

	/**
	 * Converts formatted file size back to Long value.
	 * @param formattedFileSize
	 * @return
	 */
	public static Long formattedFileSizeToObject(String formattedFileSize) {
		Long fileSize = null;

		if (formattedFileSize != null) {

			for (int i = 0; i < units.length; i++) {
				if (formattedFileSize.endsWith(" " + units[i])) {

					System.out.println("formattedFileSize " +formattedFileSize + " " + formattedFileSize.length() + " " + units[i] + " " + (units[i].length() + 1) + " new len " + (formattedFileSize.length() - units[i].length() - 1));

					String valueStr = formattedFileSize.substring(0,(formattedFileSize.length() - units[i].length() - 1));
					System.out.println("value " +valueStr);
					double fileSizeD = Double.valueOf(valueStr) * ((double)dividers[i]);
					System.out.println("fileSizeD " +fileSizeD);
					fileSize = Math.round(fileSizeD);
					System.out.println("fileSize " +fileSize);

				}
			}
		}

		return fileSize;
	}

	/**
	 * Format file size using components.
	 *
	 * @param value     Non formatted original value.
	 * @param divider   Used to compute value to display.
	 * @param unit      Unit to display after value.
	 * @return          Formatted String with file size to display.
	 */
	private static String format(final long value,
	                             final long divider,
	                             final String unit){
		final double result =
				divider > 1 ? (double) value / (double) divider : (double) value;
		return new DecimalFormat("#,##0.#").format(result) + " " + unit;
	}
}
