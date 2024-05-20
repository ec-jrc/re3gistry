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
package eu.europa.ec.re3gistry2.web.utility;

import eu.europa.ec.re3gistry2.base.utility.BaseConstants;
import eu.europa.ec.re3gistry2.base.utility.MailManager;
import eu.europa.ec.re3gistry2.model.RegAction;
import java.security.InvalidParameterException;
import java.util.LinkedHashSet;
import java.util.ResourceBundle;
import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

public class SendEmailFromAction {

    public SendEmailFromAction() {
    }

    public static void sendEmailToAllUsersOfAnAction(RegAction regAction, ResourceBundle systemLocalization) throws InvalidParameterException, MessagingException, AddressException {
        // Send email notification
        // email in the action. changes_request, changes_implemented, submitted_by
        LinkedHashSet<InternetAddress> users = new LinkedHashSet<>();
        users.add(new InternetAddress(regAction.getSubmittedBy().getEmail()));
        users.add(new InternetAddress(regAction.getApprovedBy().getEmail()));
        users.add(new InternetAddress(regAction.getPublishedBy().getEmail()));
        users.add(new InternetAddress(regAction.getRegUser().getEmail()));

        if (!users.isEmpty()) {
            InternetAddress[] recipient = new InternetAddress[users.size()];
            users.toArray(recipient);

            String subject = systemLocalization.getString(BaseConstants.KEY_EMAIL_SUBJECT_ITEM_PUBLISHED);
            String body = systemLocalization.getString(BaseConstants.KEY_EMAIL_BODY_ITEM_PUBLISHED);
            subject = (subject != null)
                    ? subject.replace("{label}", regAction.getLabel())
                    : "";
            body = (body != null)
                    ? body.replace("{label}", regAction.getLabel())
                            .replace("{changelog}", regAction.getChangelog())
                    : "";
            MailManager.sendMail(recipient, subject, body);
        }
    }

}
