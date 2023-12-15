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

import javax.persistence.EntityManager;

public class EntityManagerCustom {

    public EntityManagerCustom() {
    }

    public EntityManager getEntityManager() {
        try {
            /**
             * create the EntityManager and return it
             */
            EntityManager em = PersistenceFactory.getEntityManagerFactory().createEntityManager();
            return em;
        } catch (Exception ex) {
            return null;
        }
    }

    public void beginTransaction(EntityManager em) {
        /**
         * begin transaction
         */
        em.getTransaction().begin();
    }

    public void commitTransaction(EntityManager em) {
        /**
         * Commit transaction
         */
        em.getTransaction().commit();
    }

    public void commitAndCloseTransaction(EntityManager em) {
        /**
         * Closing transaction
         */
        em.getTransaction().commit();
        em.close();
    }

    public void closeTransaction(EntityManager em) {
        /**
         * Closing transaction
         */
        em.close();
    }

    public void rollbackTransaction(EntityManager em) {
        if (em.getTransaction().isActive()) {
            em.getTransaction().rollback();
        }
    }
}
