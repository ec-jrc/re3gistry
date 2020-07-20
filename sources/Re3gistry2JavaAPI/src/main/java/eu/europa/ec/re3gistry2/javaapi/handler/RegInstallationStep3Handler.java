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
package eu.europa.ec.re3gistry2.javaapi.handler;

import eu.europa.ec.re3gistry2.base.utility.BaseConstants;
import eu.europa.ec.re3gistry2.model.RegUser;
import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class RegInstallationStep3Handler {

    private final HttpServletRequest request;
    private final EntityManager entityManagerRe3gistry2;

    /**
     * This method initializes the class
     *
     * @param request
     * @param entityManagerRe3gistry2
     * @throws Exception
     */
    public RegInstallationStep3Handler(HttpServletRequest request, EntityManager entityManagerRe3gistry2) throws Exception {
        this.request = request;
        this.entityManagerRe3gistry2 = entityManagerRe3gistry2;
    }

    public RegUser addUser() throws Exception {
        String adminUsename = request.getParameter(BaseConstants.KEY_REQUEST_ADMIN_USERNAME);
        String adminPassword = request.getParameter(BaseConstants.KEY_REQUEST_ADMIN_PASSWORD);

        boolean commit = true;
        RegInstallationHandler regInstallationHandler = new RegInstallationHandler(entityManagerRe3gistry2);
        RegUser adminUser = regInstallationHandler.createRegUser(adminUsename, adminPassword, commit, true);
        regInstallationHandler.createAllGroups(commit);
        regInstallationHandler.setAllGroupsAndAllRightsByUser(adminUser, commit);

        if (adminUser != null) {
            HttpSession session = request.getSession();

            session.setAttribute(BaseConstants.KEY_SESSION_USER, adminUser);
        }

        return adminUser;
    }

}
