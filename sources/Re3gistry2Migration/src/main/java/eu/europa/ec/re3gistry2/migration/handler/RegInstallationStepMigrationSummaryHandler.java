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
package eu.europa.ec.re3gistry2.migration.handler;

import eu.europa.ec.re3gistry2.base.utility.BaseConstants;
import eu.europa.ec.re3gistry2.base.utility.Configuration;
import eu.europa.ec.re3gistry2.migration.manager.MigrationManager;
import eu.europa.ec.re3gistry2.migration.migrationmodel.Itemclass;
import eu.europa.ec.re3gistry2.migration.migrationmodel.Languagecode;
import eu.europa.ec.re3gistry2.migration.migrationmodel.Localization;
import eu.europa.ec.re3gistry2.migration.migrationmodel.Register;
import eu.europa.ec.re3gistry2.migration.migrationmodel.Registry;
import eu.europa.ec.re3gistry2.migration.utility.ConstantsMigration;
import eu.europa.ec.re3gistry2.migration.utility.ItemclassStatistics;
import eu.europa.ec.re3gistry2.migration.utility.RegisterStatistics;
import eu.europa.ec.re3gistry2.migration.utility.RegistryStatistics;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.apache.logging.log4j.Logger;

public class RegInstallationStepMigrationSummaryHandler {

    // Init logger
    Logger logger;

    // System localization
    ResourceBundle systemLocalization;
    private final HttpServletRequest request;
    private final EntityManager entityManagerRe3gistry2;

    /**
     * This method initializes the class
     *
     * @param request
     * @throws Exception
     */
    public RegInstallationStepMigrationSummaryHandler(HttpServletRequest request, EntityManager entityManagerRe3gistry2) throws Exception {
        this.request = request;
        logger = Configuration.getInstance().getLogger();
        this.entityManagerRe3gistry2 = entityManagerRe3gistry2;

        startMigration();
    }

    private void startMigration() throws Exception {
        HttpSession session = request.getSession();

        String dbAddress = request.getParameter(BaseConstants.KEY_REQUEST_MIGRATION_DBADDRESS);
        String dbPort = request.getParameter(BaseConstants.KEY_REQUEST_MIGRATION_DBPORT);
        String dbName = request.getParameter(BaseConstants.KEY_REQUEST_MIGRATION_DBNAME);
        String dbUsername = request.getParameter(BaseConstants.KEY_REQUEST_MIGRATION_DBUSERNAME);
        String dbPassword = request.getParameter(BaseConstants.KEY_REQUEST_MIGRATION_DBPASSWORD);

        session.setAttribute(BaseConstants.KEY_REQUEST_MIGRATION_DBADDRESS, dbAddress);
        session.setAttribute(BaseConstants.KEY_REQUEST_MIGRATION_DBPORT, dbPort);
        session.setAttribute(BaseConstants.KEY_REQUEST_MIGRATION_DBNAME, dbName);
        session.setAttribute(BaseConstants.KEY_REQUEST_MIGRATION_DBUSERNAME, dbUsername);
        session.setAttribute(BaseConstants.KEY_REQUEST_MIGRATION_DBPASSWORD, dbPassword);

        try {
            MigrationManager migrationManager = new MigrationManager(dbAddress, dbPort, dbName, dbUsername, dbPassword,entityManagerRe3gistry2);
            
            EntityManager entityManagerRe3gistry2Migration = migrationManager.getEntityManagerRe3gistry2Migration();
            if (!entityManagerRe3gistry2Migration.getTransaction().isActive()) {
                entityManagerRe3gistry2Migration.getTransaction().begin();
            }

            Query queryMasterLanguagecode = entityManagerRe3gistry2Migration.createNamedQuery("Languagecode.findByMasterlanguage", Languagecode.class);
            queryMasterLanguagecode.setParameter("masterlanguage", Boolean.TRUE);
            Languagecode masterLanguagecode = null;
            try {
                masterLanguagecode = (Languagecode) queryMasterLanguagecode.getSingleResult();
            } catch (Exception ex) {
                logger.error("Error in  Languagecode.findByMasterlanguage" + ex.getMessage());
            }

            Query queryRegistry = entityManagerRe3gistry2Migration.createNamedQuery("Registry.findAll", Registry.class);
            List<Registry> registryList = queryRegistry.getResultList();
            List<RegistryStatistics> registryStatisticsList = new ArrayList<>();
            for (Registry registry : registryList) {
                Query queryLocalizationRegistry = entityManagerRe3gistry2Migration.createQuery(ConstantsMigration.LOCALIZATION_BY_REGISTRY_AND_LANGUAGE);
                queryLocalizationRegistry.setParameter("registry", registry);
                queryLocalizationRegistry.setParameter("language", masterLanguagecode);
                Localization localizationRegistry = (Localization) queryLocalizationRegistry.getSingleResult();

                RegistryStatistics registryStatistics = new RegistryStatistics();
                registryStatistics.setRegistry(registry);
                registryStatistics.setRegistryLocalization(localizationRegistry);

                registryStatisticsList.add(registryStatistics);
            }
            request.setAttribute(BaseConstants.KEY_REQUEST_REGISTRY_STATISTICS, registryStatisticsList);

            Query queryRegister = entityManagerRe3gistry2Migration.createNamedQuery("Register.findAll", Register.class);
            List<Register> registerList = queryRegister.getResultList();

            List<RegisterStatistics> registerStatisticsList = new ArrayList<>();
            for (Register register : registerList) {
                RegisterStatistics registerStatistics = new RegisterStatistics();
                registerStatistics.setRegister(register);
                /**
                 * get localization by register
                 */
                Query queryLocalizationRegistry = entityManagerRe3gistry2Migration.createQuery(ConstantsMigration.LOCALIZATION_BY_REGISTER_AND_LANGUAGE);
                queryLocalizationRegistry.setParameter("register", register);
                queryLocalizationRegistry.setParameter("language", masterLanguagecode);
                Localization localizationRegister = (Localization) queryLocalizationRegistry.getSingleResult();
                registerStatistics.setRegisterLocalization(localizationRegister);

                /**
                 * get item class by register order by data procedure
                 */
                Query queryItemclass = entityManagerRe3gistry2Migration.createQuery(ConstantsMigration.ITEMCLASS_BY_REGISTER_ORDERBY_DATAPROCEDUREORDER_ASC);
                queryItemclass.setParameter("register", register);
                List<Itemclass> itemclassList = queryItemclass.getResultList();
                List<ItemclassStatistics> itemclassStatisticsList = new ArrayList<>();

                /**
                 * for each item class get statistics
                 */
                itemclassList.stream().map((itemclass) -> {
                    Query queryCountItemsInItemclass = entityManagerRe3gistry2Migration.createQuery(ConstantsMigration.GET_COUNT_ITEM_BY_ITEMACLASS);
                    queryCountItemsInItemclass.setParameter("itemclass", itemclass);
                    long count = (long) queryCountItemsInItemclass.getSingleResult();
                    ItemclassStatistics itemclassStatistics = new ItemclassStatistics();
                    itemclassStatistics.setItemclass(itemclass);
                    itemclassStatistics.setStatistics((int) count);
                    return itemclassStatistics;
                }).forEachOrdered((itemclassStatistics) -> {
                    itemclassStatisticsList.add(itemclassStatistics);
                });

                registerStatistics.setItemclassStatistics(itemclassStatisticsList);

                registerStatisticsList.add(registerStatistics);
            }
            request.setAttribute(BaseConstants.KEY_REQUEST_REGISTER_LIST_STATISTICS, registerStatisticsList);
        } catch (Exception ex) {
            throw new Exception(ex.getMessage());
        }
    }

}
