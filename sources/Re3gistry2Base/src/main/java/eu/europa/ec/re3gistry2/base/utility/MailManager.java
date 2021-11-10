/*
 * Copyright 2007,2016 EUROPEAN UNION
 * Licensed under the EUPL, Version 1.2 or - as soon they will be approved by
 * the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 * https://ec.europa.eu/isa2/solutions/european-union-public-licence-eupl_en
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 *
 * Date: 2020/05/11
 * Authors:
 * European Commission, Joint Research Centre - jrc-inspire-support@ec.europa.eu
 *
 * This work was supported by the Interoperability solutions for public
 * administrations, businesses and citizens programme (http://ec.europa.eu/isa2)
 * through Action 2016.10: European Location Interoperability Solutions for e-Government (ELISE)
 */
package eu.europa.ec.re3gistry2.base.utility;

import java.security.InvalidParameterException;
import java.util.Date;
import java.util.Properties;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import org.apache.logging.log4j.Logger;

public class MailManager {

    public static void sendMail(InternetAddress[] toAddresses, String subject, String body) throws InvalidParameterException, AddressException, MessagingException {
        sendMail(toAddresses, null, null, subject, body);
    }

    public static void sendMail(InternetAddress[] toAddresses, InternetAddress[] ccAddresses, String subject, String body) throws InvalidParameterException, AddressException, MessagingException {
        sendMail(toAddresses, ccAddresses, null, subject, body);
    }

    public static void sendMail(InternetAddress[] toAddresses, InternetAddress[] ccAddresses, InternetAddress[] bccAddresses, String subject, String body) throws InvalidParameterException, AddressException, MessagingException {

        Logger logger = Configuration.getInstance().getLogger();

        // Check the required fields
        if (toAddresses == null || toAddresses.length < 1) {
            throw new InvalidParameterException("At least one e-mail address is required");
        }
        if (subject == null || subject.trim().length() < 1) {
            throw new InvalidParameterException("The subject field is required");
        }
        if (body == null || body.trim().length() < 1) {
            throw new InvalidParameterException("The body field is required");
        }

        // Validate mail addresses (addresses shall be separated by  semicolon)      
        try {
            for (InternetAddress tmp : toAddresses) {
                tmp.validate();
            }
            if (ccAddresses != null && ccAddresses.length > 0) {
                for (InternetAddress tmp : ccAddresses) {
                    tmp.validate();
                }
            }
            if (bccAddresses != null && bccAddresses.length > 0) {
                for (InternetAddress tmp : bccAddresses) {
                    tmp.validate();
                }
            }
        } catch (AddressException e) {
            throw new AddressException("Check the recipient addresses. " + e.getMessage());
        }

        // Prepare and send the mail
        try {
            // Get configuration properties
            final Properties configurationProperties = Configuration.getInstance().getProperties();

            String mailHost = configurationProperties.getProperty(BaseConstants.KEY_MAIL_HOST);
            String mailPort = configurationProperties.getProperty(BaseConstants.KEY_MAIL_PORT);
            String mailSender = configurationProperties.getProperty(BaseConstants.KEY_MAIL_SENDER);
            String mailTemplate = configurationProperties.getProperty(BaseConstants.KEY_MAIL_TEMPLATE);
            String mailStarTTLS = configurationProperties.getProperty(BaseConstants.KEY_MAIL_STARTTLS);
            String mailAuth = configurationProperties.getProperty(BaseConstants.KEY_MAIL_AUTH);
            String mailUser = configurationProperties.getProperty(BaseConstants.KEY_MAIL_USER);
            String mailPassword = configurationProperties.getProperty(BaseConstants.KEY_MAIL_PASSWORD);
            String applicationRootURL = configurationProperties.getProperty(BaseConstants.KEY_MAIL_APPLICATION_ROOTURL);

            // Setting the properties object for the mail session
            Properties properties = new Properties();
            properties.put(BaseConstants.KEY_MAIL_HOST, mailHost);
            properties.put(BaseConstants.KEY_MAIL_PORT, mailPort);
            properties.put(BaseConstants.KEY_MAIL_STARTTLS, mailStarTTLS);
            properties.put(BaseConstants.KEY_MAIL_AUTH, mailAuth);
            properties.put(BaseConstants.KEY_MAIL_USER, mailUser);
            properties.put(BaseConstants.KEY_MAIL_PASSWORD, mailPassword);

            // Create the Authenticator
            Authenticator auth = new Authenticator() {
                @Override
                public PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(mailUser, mailPassword);
                }
            };

            // Create the session with the related properties
            Session session = Session.getInstance(properties, auth);

            // Creates a new message
            Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(mailSender));
            msg.setRecipients(Message.RecipientType.TO, toAddresses);
            if (ccAddresses != null && ccAddresses.length > 0) {
                msg.setRecipients(Message.RecipientType.CC, ccAddresses);
            }
            if (bccAddresses != null && bccAddresses.length > 0) {
                msg.setRecipients(Message.RecipientType.BCC, bccAddresses);
            }
            msg.setSubject(subject);
            msg.setSentDate(new Date());

            // Creates message part
            MimeBodyPart messageBodyPart = new MimeBodyPart();

            // Filling the mail template (if available)
            if (mailTemplate != null && mailTemplate.length() > 0) {
                messageBodyPart.setContent(mailTemplate.replace("%mailbody%", body).replace("%subject%", subject).replace("%application.rooturl%", applicationRootURL), "text/html");
            } else {
                messageBodyPart.setContent(body, "text/html");
            }

            // creates multi-part
            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(messageBodyPart);

            // sets the multi-part as e-mail's content
            msg.setContent(multipart);

            // sends the e-mail
            Transport.send(msg);

        } catch (MessagingException e) {
            logger.error(e.getMessage(), e);
            throw new MessagingException(e.getMessage());
        }
    }
}