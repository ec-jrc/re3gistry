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
package eu.europa.ec.ror.managers.util;

import eu.europa.ec.ror.utility.Configuration;
import eu.europa.ec.ror.utility.Constants;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import org.apache.logging.log4j.Logger;

public class PersistenceFactory {

    public static EntityManagerFactory getEntityManagerFactory() throws Exception {

        String persistenceUnitName = Configuration.getInstance().getProperties().getProperty(Constants.KEY_PERSISTENCE_UNIT_NAME, null);

        if (persistenceUnitName == null || persistenceUnitName.trim().length() <= 0) {
            throw new Exception("@@ Error while getting the persistence unit name; check the configuration.properties file.");
        }

        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory(persistenceUnitName);
        
        return entityManagerFactory;
    }
}
