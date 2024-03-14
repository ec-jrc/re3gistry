/*
 * Copyright 2010,2015 EUROPEAN UNION
 * Licensed under the EUPL, Version 1.1 or - as soon they will be approved by
 * the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 * http://ec.europa.eu/idabc/eupl
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 *
 * Date: __/__/____
 * Authors: European Commission, Joint Research Centre
 * inspire-registry-dev@jrc.ec.europa.eu
 *
 * This work was supported by the EU  Interoperability Solutions for
 * European Public Administrations Programme (http://ec.europa.eu/isa)
 * through Action 1.17: Re-usable INSPIRE Reference Platform 
 */
package eu.europa.ec.ror.utility;

import java.io.IOException;
import java.util.Date;
import java.util.Properties;
import java.util.ResourceBundle;
import javax.servlet.ServletException;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class Mail {

    public static void sendMail(ResourceBundle localization, String recipient, String subject, String body) throws ServletException, IOException {
        try {
            // sets SMTP server properties
            final Properties props = Configuration.getInstance().getProperties();

            //Mail properties
            Properties properties = new Properties();
            properties.put("mail.smtp.host", props.getProperty(Constants.KEY_EXPORT_MAIL_HOST));
            properties.put("mail.smtp.port", props.getProperty(Constants.KEY_EXPORT_MAIL_PORT));
            properties.put("mail.smtp.auth", props.getProperty(Constants.KEY_EXPORT_MAIL_AUTH));
            properties.put("mail.smtp.starttls.enable", props.getProperty(Constants.KEY_EXPORT_MAIL_STARTTLS));
            properties.put("mail.user", props.getProperty(Constants.KEY_EXPORT_MAIL_USER));
            properties.put("mail.password", props.getProperty(Constants.KEY_EXPORT_MAIL_PASSWORD));

            // creates a new session with an authenticator
            Authenticator auth = new Authenticator() {
                @Override
                public PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(props.getProperty(Constants.KEY_EXPORT_MAIL_USER), props.getProperty(Constants.KEY_EXPORT_MAIL_PASSWORD));
                }
            };
            Session session = Session.getInstance(properties, auth);

            // creates a new e-mail message
            Message msg = new MimeMessage(session);
            subject = localization.getString("mail.subject.prefix") + subject;

            msg.setFrom(new InternetAddress(props.getProperty(Constants.KEY_EXPORT_MAIL_SENDER)));
            InternetAddress[] toAddresses = {new InternetAddress(recipient)};
            msg.setRecipients(Message.RecipientType.TO, toAddresses);
            msg.setSubject(subject);
            msg.setSentDate(new Date());

            // creates message part
            MimeBodyPart messageBodyPart = new MimeBodyPart();

            messageBodyPart.setContent(localization.getString("mail.html").replace("%mailbody%",body).replaceAll("%subject%",subject), "text/html");

            // creates multi-part
            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(messageBodyPart);

            // sets the multi-part as e-mail's content
            msg.setContent(multipart);

            // sends the e-mail
            Transport.send(msg);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
