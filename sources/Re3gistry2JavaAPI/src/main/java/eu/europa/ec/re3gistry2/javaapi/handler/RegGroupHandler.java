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

import eu.europa.ec.re3gistry2.base.utility.Configuration;
import eu.europa.ec.re3gistry2.base.utility.PersistenceFactory;
import eu.europa.ec.re3gistry2.crudimplementation.RegGroupManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegUserRegGroupMappingManager;
import eu.europa.ec.re3gistry2.model.RegGroup;
import eu.europa.ec.re3gistry2.model.RegUser;
import eu.europa.ec.re3gistry2.model.RegUserRegGroupMapping;
import eu.europa.ec.re3gistry2.model.uuidhandlers.RegUserRegGroupMappingUuidHelper;
import java.util.Date;
import java.util.ResourceBundle;
import javax.persistence.EntityManager;
import org.apache.logging.log4j.Logger;

public class RegGroupHandler {

    // Init logger
    Logger logger;

    // Setup the entity manager
    EntityManager entityManager;

    // System localization
    ResourceBundle systemLocalization;

    // Synchronization object
    private static final Object sync = new Object();

    /**
     * This method initializes the class
     *
     * @throws Exception
     */
    public RegGroupHandler() throws Exception {
        entityManager = PersistenceFactory.getEntityManagerFactory().createEntityManager();
        logger = Configuration.getInstance().getLogger();
        systemLocalization = Configuration.getInstance().getLocalization();
    }

    public boolean updateGroup(RegGroup regGroup) {
        // initializing managers
        RegGroupManager regGroupManager = new RegGroupManager(entityManager);

        boolean operationResult = false;

        try {
            // The writing operation on the Database are synchronized
            /* ## Start Synchronized ## */
            synchronized (sync) {
                if (!entityManager.getTransaction().isActive()) {
                    entityManager.getTransaction().begin();
                }
                // Update the user
                regGroupManager.update(regGroup);
                operationResult = true;

                entityManager.getTransaction().commit();
            }
            /* ## End Synchronized ## */

        } catch (Exception e) {
            logger.error("@ RegGroupHandler.updateGroup: generic error.", e);
        } finally {
            if (entityManager != null) {
                entityManager.close();
            }
        }
        return operationResult;
    }

    public boolean addGroup(RegGroup regGroup, RegUser regUser) {
        // initializing managers
        RegGroupManager regGroupManager = new RegGroupManager(entityManager);

        boolean operationResult = false;

        try {
            // The writing operation on the Database are synchronized
            /* ## Start Synchronized ## */
            synchronized (sync) {
                if (!entityManager.getTransaction().isActive()) {
                    entityManager.getTransaction().begin();
                }
                // Update the user
                regGroupManager.add(regGroup);

                // Add the current user to the group
                RegUserRegGroupMappingManager regUserRegGroupMappingManager = new RegUserRegGroupMappingManager(entityManager);
                RegUserRegGroupMapping regUserRegGroupMapping = new RegUserRegGroupMapping();
                String newUuid = RegUserRegGroupMappingUuidHelper.getUuid(regUser, regGroup);
                regUserRegGroupMapping.setUuid(newUuid);
                regUserRegGroupMapping.setRegGroup(regGroup);
                regUserRegGroupMapping.setRegUser(regUser);
                regUserRegGroupMapping.setIsGroupadmin(true);
                regUserRegGroupMapping.setInsertdate(new Date());

                regUserRegGroupMappingManager.add(regUserRegGroupMapping);

                operationResult = true;

                entityManager.getTransaction().commit();
            }
            /* ## End Synchronized ## */

        } catch (Exception e) {
            logger.error("@ RegGroupHandler.updateGroup: generic error.", e);
        } finally {
            if (entityManager != null) {
                entityManager.close();
            }
        }
        return operationResult;
    }
}
