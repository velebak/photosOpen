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

import java.nio.charset.StandardCharsets;
import java.security.AlgorithmParameters;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Heather Stevens on 10/6/16.
 *
 * Creates and compares hashed passwords prefixed with iteration number and random salt. Segments of hash are deliminated
 * with colons.
 *
 * Example out come of createHash:
 *
 * 400000:IvRY2w5McPoEvoMMBH0BeUB9XPoUqeyYTI5Frq9/jeG/UgGp9DwZHe0si5j5bOAIea68twHVbLMfp39hhVSPC+qFwGtTDIY5HJn/Y6uotfAVjQR59xE6+FIuOFx69xO9jnohQgoifYAoHNWSxMAxl6GAY/Ul3K+5rNboPJ/z/zo=:GlN+z6/IW5d4NYziSiM+XQ==
 */
public class KryptoUtil
{
	private static final Logger log = LoggerFactory.getLogger(KryptoUtil.class);

	// hash
	private static final int HASH_BYTE_SIZE = 128;
	private static final int PBKDF2_ITERATIONS = 500000; // 2016 hardware
	private static final int SEGMENTS_EXPECTED = 3;
	private static final char DELIMINATOR = ':';
	private static final String PBKDF_2_WITH_HMAC_SHA_512 = "PBKDF2WithHmacSHA512"; // Modern algorithm included with Java 8

	// round trip
	private static final String SECRET_KEY = "NA86BhUMbipbDHBnqYlm9Ccg";
	private static final String AES_CBC_PKCS5_PADDING = "AES/CBC/PKCS5PADDING";
	private static final String AES = "AES";

	/**
	 * Creates the initial hash of the user's chosen password. The original password is not stored
	 * and cannot be easily determined if at all.
	 *
	 * @param password  Password to hash.
	 * @return          Hashed password prefixed with number of iterations and random salt.
	 */
	public static String createHash(String password) {

		if (StringUtils.isEmpty(password)) {
			return null;
		}

		byte salt[] = createRandomSalt();
		byte hash[] = createHashedWithSalt(password.toCharArray(), salt, PBKDF2_ITERATIONS);

		Base64.Encoder encoder = Base64.getEncoder();
		return  "" + PBKDF2_ITERATIONS + DELIMINATOR + encoder.encodeToString(salt) + DELIMINATOR + encoder.encodeToString(hash);
	}

	/**
	 * Parses the user's actual hashed password information to determine the iteration and salt to create the hash. Then hashes
	 * the entered password for comparison.
	 *
	 * @param enteredPassword   User entered password to be checked.
	 * @param actualHash        Hash prefixed with iteration number and salt.
	 * @return                  True if password matches the originally entered password.
	 */
	public static boolean matches(String enteredPassword, String actualHash) {

		if (StringUtils.isEmpty(enteredPassword)) {
			return false;
		}

		try
		{
			String segments[] = StringUtils.split(actualHash, DELIMINATOR);

			if (segments.length == SEGMENTS_EXPECTED)
			{
				Base64.Encoder encoder = Base64.getEncoder();
				Base64.Decoder decoder = Base64.getDecoder();
				int iterations = Integer.parseInt(segments[0]);

				String entered = encoder.encodeToString(
						createHashedWithSalt(
							enteredPassword.toCharArray(),
								decoder.decode(segments[1].getBytes(StandardCharsets.UTF_8)),
							iterations));

				return entered.equals(segments[2]);
			}
		}
		catch (Exception e) {
			log.error("Error while attempting to match password to hash.", e);
			e.printStackTrace();
		}

		return false;
	}

	/**
	 * Creates the hash of the password.
	 *
	 * @param password      Password to one way hash.
	 * @param salt          Salt to be used.
	 * @param iterations    Number of iterations.
	 * @return              Hashed password.
	 */
	private static byte[] createHashedWithSalt(char[] password, byte[] salt, int iterations) {


		PBEKeySpec spec = new PBEKeySpec(password, salt, iterations, HASH_BYTE_SIZE);
		SecretKeyFactory skf = null;
		try 		{
			skf = SecretKeyFactory.getInstance(PBKDF_2_WITH_HMAC_SHA_512);
			return skf.generateSecret(spec).getEncoded();


		}
		catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
			log.error("Error while attempting to match password to hash.", e);

			e.printStackTrace();
		}

		return null;
	}

	private static byte[] createRandomSalt() {
		SecureRandom secureRandom = new SecureRandom();
		byte salt[] = new byte[HASH_BYTE_SIZE];
		secureRandom.nextBytes(salt);
		return salt;
	}

	/**
	 * Encryption for text.
	 *
	 * @param source     Text to encrypt.
	 * @return           Encrypted text
	 */
	public static byte[] encryptText(String source) {

		if (StringUtils.isEmpty(source)) {
			throw new IllegalArgumentException(
					"Source is null or empty.");
		}
		byte ciphered[] = null;

		try {
			Base64.Encoder encoder = Base64.getEncoder();
			byte base64Encoded[] =	encoder.encode(source.getBytes(StandardCharsets.UTF_8));

			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
			SecretKey secretKey = new SecretKeySpec(SECRET_KEY.getBytes(StandardCharsets.UTF_8), "AES");
			cipher.init(Cipher.ENCRYPT_MODE, secretKey);

			AlgorithmParameters algorithmParameters = cipher.getParameters();

			byte initializationVector[] = cipher.getIV(); // algorithmParameters.getParameterSpec(IvParameterSpec.class).getIV();

			ciphered = new byte[initializationVector.length
					+ cipher.getOutputSize(base64Encoded.length)];

			System.arraycopy(initializationVector, 0, ciphered, 0, initializationVector.length);
			cipher.doFinal(base64Encoded, 0, base64Encoded.length, ciphered,
					initializationVector.length);



		}
		catch (Exception e) {
			log.error("Failed to encrypt text", e);
			e.printStackTrace();
			throw new IllegalArgumentException(
					"Source too small to contain IV");
		}


		return ciphered;
	}

	/**
	 * Decrypts text.
	 *
	 * @param source     Text to decrypt.
	 * @return           Plain text
	 */
	public static String decryptText(byte[] source) {

		if (source == null) {
			throw new IllegalArgumentException(
					"Source is null");
		}

		String result = null;
		try {
			Cipher cipher = Cipher.getInstance(AES_CBC_PKCS5_PADDING);
			SecretKeySpec secretKey = new SecretKeySpec(SECRET_KEY.getBytes(StandardCharsets.UTF_8),
					AES);

			if (source.length < cipher.getBlockSize()) {
				throw new IllegalArgumentException(
						"Source too small to contain IV");
			}

			IvParameterSpec ivSpec = new IvParameterSpec(source, 0,
					cipher.getBlockSize());

			cipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec);

			byte plainTextBytes[] = new byte[cipher.getOutputSize(source.length
					- cipher.getBlockSize())];
			int outcome = cipher.doFinal(source, cipher.getBlockSize(), source.length
					- cipher.getBlockSize(), plainTextBytes, 0);

			byte trimmed[] = new byte[outcome];
			System.arraycopy(plainTextBytes, 0, trimmed, 0, outcome);

			Base64.Decoder decoder = Base64.getDecoder();
			byte decoded[] = decoder.decode(trimmed);
			result = new String(decoded, StandardCharsets.UTF_8);

		}
		catch (Exception e) {
			e.printStackTrace();
			log.error("Failed to encrypt text", e);
		}

		return  result;
	}
}
