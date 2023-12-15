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
package eu.europa.ec.ror.managers;

import eu.europa.ec.ror.managers.util.EntityManagerCustom;
import eu.europa.ec.ror.model.Organization;
import eu.europa.ec.ror.model.Procedurehistory;
import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;

public class ProcedurehistoryMgr {

    /* Common DB operations */
    public static List<Procedurehistory> getAllProcedurehistories() {
        try {
            EntityManagerCustom emc = new EntityManagerCustom();
            EntityManager em = emc.getEntityManager();

            Query q = em.createNamedQuery("Procedurehistory.findAll");
            List<Procedurehistory> list = (List<Procedurehistory>) q.getResultList();

            return list;
        } catch (Exception ex) {
            return null;
        }
    }

    public static Procedurehistory getProcedurehistoryByID(String id) {
        try {
            EntityManagerCustom emc = new EntityManagerCustom();
            EntityManager em = emc.getEntityManager();

            Query q = em.createNamedQuery("Procedurehistory.findByUuid");
            q.setParameter("uuid", id);
            Procedurehistory list = (Procedurehistory) q.getSingleResult();

            return list;
        } catch (Exception ex) {
            return null;
        }
    }

    public static List<Procedurehistory> getProcedurehistoryByStatus(String status) {
        try {
            EntityManagerCustom emc = new EntityManagerCustom();
            EntityManager em = emc.getEntityManager();

            Query q = em.createNamedQuery("Procedurehistory.findByStatus");
            q.setParameter("status", status);
            List<Procedurehistory> list = (List<Procedurehistory>) q.getResultList();

            return list;
        } catch (Exception ex) {
            return null;
        }
    }

    public static List<Procedurehistory> getProcedurehistoriesByOrganization(Organization organization) {
        try {
            EntityManagerCustom emc = new EntityManagerCustom();
            EntityManager em = emc.getEntityManager();

            Query q = em.createNamedQuery("Procedurehistory.findByOrganization");
            q.setParameter("organization", organization);
            List<Procedurehistory> list = (List<Procedurehistory>) q.getResultList();

            return list;
        } catch (Exception ex) {
            return null;
        }
    }

    public static boolean insertProcedure(Procedurehistory r) throws Exception {
        EntityManagerCustom emc = new EntityManagerCustom();
        EntityManager em = emc.getEntityManager();

        emc.beginTransaction(em);

        //Checking parameters
        if (r == null) {
            throw new Exception("The Procedurehistory passed by parameter is null.");
        }

        if (r.getStartdate()== null) {
            r.setStartdate(new Date());
        }

        //Checking the DB managers
        if (em == null) {
            throw new Exception("The persistence layer is not initialized: entyty manager is null.");
        }

        //Saving the object
        em.persist(r);

        emc.commitAndCloseTransaction(em);

        return true;

    }

    public static boolean updateProcedure(Procedurehistory r) throws Exception {
        EntityManagerCustom emc = new EntityManagerCustom();
        EntityManager em = emc.getEntityManager();

        emc.beginTransaction(em);

        //Checking parameters
        if (r == null) {
            throw new Exception("The Procedurehistory passed by parameter is null.");
        }

        //Checking the DB managers
        if (em == null) {
            throw new Exception("The persistence layer is not initialized: entyty manager is null.");
        }

        //Saving the object
        em.merge(r);

        emc.commitAndCloseTransaction(em);

        return true;

    }
}
