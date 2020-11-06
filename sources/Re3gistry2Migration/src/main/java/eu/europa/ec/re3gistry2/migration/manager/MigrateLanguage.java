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
package eu.europa.ec.re3gistry2.migration.manager;

import eu.europa.ec.re3gistry2.crudimplementation.RegLanguagecodeManager;
import eu.europa.ec.re3gistry2.migration.migrationmodel.Languagecode;
import eu.europa.ec.re3gistry2.model.RegLanguagecode;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import org.apache.logging.log4j.Logger;

public class MigrateLanguage {

    private final EntityManager entityManagerRe3gistry2;
    private final EntityManager entityManagerRe3gistry2Migration;
    private final Logger logger;

    /**
     * migrate language code table
     */
    MigrateLanguage(MigrationManager migrationManager) {
        this.entityManagerRe3gistry2 = migrationManager.getEntityManagerRe3gistry2();
        this.entityManagerRe3gistry2Migration = migrationManager.getEntityManagerRe3gistry2Migration();
        this.logger = migrationManager.getLogger();
    }

    public void startMigrationLanguageCode() {
        /**
         * set master language and active languages
         */

        try {
            Query queryLanguagecode = entityManagerRe3gistry2Migration.createNamedQuery("Languagecode.findAll", Languagecode.class);
            List<Languagecode> languagecodeList;
            try {
                languagecodeList = queryLanguagecode.getResultList();
            } catch (Exception ex) {
                logger.error("Error in  getting the result list for " + queryLanguagecode + " " + ex.getMessage());
                throw new Exception("Error in  getting the localization for " + queryLanguagecode + " " + ex.getMessage());
            }

            RegLanguagecodeManager regLanguagecodeManager = new RegLanguagecodeManager(entityManagerRe3gistry2);
            List<RegLanguagecode> reglanguagecodeList = regLanguagecodeManager.getAll();

            Set<String> reglanguagecodeSet = new HashSet<>();
            for (RegLanguagecode reglanguagecode : reglanguagecodeList) {

                reglanguagecodeSet.add(reglanguagecode.getIso6391code());
                boolean exist = false;
                for (Languagecode languagecode : languagecodeList) {
                    if (languagecode.getLabel().equals(reglanguagecode.getLabel()) && languagecode.getIsocode().equals(reglanguagecode.getIso6391code()) && languagecode.getIso6392().equals(reglanguagecode.getIso6392code())) {
                        reglanguagecode.setMasterlanguage(languagecode.getMasterlanguage());
                        reglanguagecode.setActive(true);
                        exist = true;
                        break;
                    }
                }
                if (exist == false) {
                    /**
                     * set active false if the language doesn't exist in the old
                     * database
                     */
                    reglanguagecode.setActive(false);
                }
                reglanguagecode.setEditdate(new Date());

                try {
                    if (!entityManagerRe3gistry2.getTransaction().isActive()) {
                        entityManagerRe3gistry2.getTransaction().begin();
                    }
                    entityManagerRe3gistry2.merge(reglanguagecode);
                    entityManagerRe3gistry2.getTransaction().commit();
                } catch (Exception ex) {
                    logger.error(ex.getMessage());
                }
            }
            addLanguageIfNotExistent(languagecodeList, reglanguagecodeSet);

        } catch (Exception ex) {
            logger.error(ex.getMessage());
        }
    }

    private void addLanguageIfNotExistent(List<Languagecode> languagecodeList, Set<String> reglanguagecodeSet) {
        /**
         * add language to the new database is not exist yet
         */
        for (Languagecode languagecode : languagecodeList) {
            if (!reglanguagecodeSet.contains(languagecode.getIsocode())) {
                RegLanguagecode regLanguagecode = new RegLanguagecode();
                regLanguagecode.setUuid(languagecode.getIsocode());
                regLanguagecode.setLabel(languagecode.getLabel());
                regLanguagecode.setIso6391code(languagecode.getIsocode());
                regLanguagecode.setIso6392code(languagecode.getIso6392());
                regLanguagecode.setActive(true);
                regLanguagecode.setMasterlanguage(languagecode.getMasterlanguage());
                regLanguagecode.setInsertdate(new Date());

                try {
                    if (!entityManagerRe3gistry2.getTransaction().isActive()) {
                        entityManagerRe3gistry2.getTransaction().begin();
                    }
                    entityManagerRe3gistry2.persist(regLanguagecode);
                    entityManagerRe3gistry2.getTransaction().commit();
                } catch (Exception ex) {
                    logger.error(ex.getMessage());
                }
            }
        }
    }

}
