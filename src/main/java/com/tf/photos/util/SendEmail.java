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

import java.util.Date;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.mail.smtp.SMTPTransport;

public class SendEmail {

    private static final Logger log = LoggerFactory.getLogger(SendEmail.class);

    public static boolean sendMessage(final String toAddress, final String fromAddress, final String subject, final String message) {

        log.debug("Sending email to "+toAddress);

        // Sender's email ID needs used with authentication
        final String username = "anEmailUserGoesHere*****";

        // Assuming you are sending email from host
        final String host = "emailhost";
        final String password = "*************";

        java.security.Security.setProperty("ssl.SocketFactory.provider", "sun.security.ssl.SSLSocketFactoryImpl");
        java.security.Security.setProperty("ssl.ServerSocketFactory.provider", "sun.security.ssl.SSLSocketFactoryImpl");

        // Get a Properties object
        Properties props = System.getProperties();
        props.setProperty("mail.smtps.host", host);
        props.setProperty("mail.smtp.port", "465"); // SSL
        props.setProperty("mail.smtp.socketFactory.port", "465"); // SSL

	    //props.setProperty("mail.smtp.port", "587"); // non ssl
	    //props.setProperty("mail.smtp.socketFactory.port", "587"); // non ssl

        props.setProperty("mail.smtps.auth", "true"); // SSL
        // props.setProperty("mail.smtp.auth", "true"); // no ssl
        props.put("mail.smtps.quitwait", "false"); // SSL
        //props.put("mail.smtp.quitwait", "false");

	    log.debug("Creating auth session");

	    Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });

        try {
            // -- Create a new message --
            final MimeMessage msg = new MimeMessage(session);

            // -- Set the FROM and TO fields --
            msg.setFrom(new InternetAddress(fromAddress));
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toAddress, false));

            msg.setSubject(subject);
	        msg.setContent(message,"text/html");

            msg.setSentDate(new Date());

	        log.info("Sending smtp msg");

	        SMTPTransport t = (SMTPTransport)session.getTransport("smtps"); // SSL
            //SMTPTransport t = (SMTPTransport)session.getTransport("smtp"); // no ssl

	        log.info("got smtp transport");

	        t.connect(host, username, password);

	        log.info("connected smtp");

	        t.sendMessage(msg, msg.getAllRecipients());

	        log.info("sent smtp");

	        t.close();
        }
        catch (MessagingException e) {
            log.error("Failed to send email to " + toAddress, e);
            e.printStackTrace();
	        return false;
        }

	    log.info("Completed email send to " + toAddress + " successfully.");
	    return true;
    }
}
