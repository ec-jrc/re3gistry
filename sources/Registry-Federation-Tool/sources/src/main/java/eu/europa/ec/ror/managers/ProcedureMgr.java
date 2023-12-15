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
import eu.europa.ec.ror.model.Descriptor;
import eu.europa.ec.ror.model.Procedure;
import eu.europa.ec.ror.model.Organization;
import eu.europa.ec.ror.model.User;
import eu.europa.ec.ror.utility.Configuration;
import eu.europa.ec.ror.utility.Constants;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import org.apache.logging.log4j.Logger;

public class ProcedureMgr {

    /* Common DB operations */
    public static List<Procedure> getAllProcedures() {
        try {
            EntityManagerCustom emc = new EntityManagerCustom();
            EntityManager em = emc.getEntityManager();

            Query q = em.createNamedQuery("Procedure.findAll");
            List<Procedure> list = (List<Procedure>) q.getResultList();

            return list;
        } catch (Exception ex) {
            return null;
        }
    }

    public static List<Procedure> getAllProceduresToRunToday() {
        try {
            EntityManagerCustom emc = new EntityManagerCustom();
            EntityManager em = emc.getEntityManager();

            Query q = em.createNamedQuery("Procedure.findByNextharvestdateToday");
            List<Procedure> list = (List<Procedure>) q.getResultList();

            return list;
        } catch (Exception ex) {
            return null;
        }
    }

    public static Procedure getProcedureByID(String id) {
        try {
            EntityManagerCustom emc = new EntityManagerCustom();
            EntityManager em = emc.getEntityManager();

            Query q = em.createNamedQuery("Procedure.findByUuid");
            q.setParameter("uuid", id);
            Procedure list = (Procedure) q.getSingleResult();

            return list;
        } catch (Exception ex) {
            return null;
        }
    }

    public static Procedure getProcedureByDescriptor(Descriptor descriptor) {
        try {
            EntityManagerCustom emc = new EntityManagerCustom();
            EntityManager em = emc.getEntityManager();

            Query q = em.createNamedQuery("Procedure.findByDescriptor");
            q.setParameter("descriptor", descriptor);
            Procedure procedure = (Procedure) q.getSingleResult();

            return procedure;
        } catch (Exception ex) {
            return null;
        }
    }

    public static List<Procedure> getProcedureByStatus(String status) {
        try {
            EntityManagerCustom emc = new EntityManagerCustom();
            EntityManager em = emc.getEntityManager();

            Query q = em.createNamedQuery("Procedure.findByStatus");
            q.setParameter("status", status);
            List<Procedure> list = (List<Procedure>) q.getResultList();

            return list;
        } catch (Exception ex) {
            return null;
        }
    }

    public static List<Procedure> getProcedureByOrganization(Organization organization) {
        try {
            EntityManagerCustom emc = new EntityManagerCustom();
            EntityManager em = emc.getEntityManager();

            Query q = em.createNamedQuery("Procedure.findByOrganization");
            q.setParameter("organization", organization);
            List<Procedure> list = (List<Procedure>) q.getResultList();

            return list;
        } catch (Exception ex) {
            return null;
        }
    }

    public static boolean insertProcedure(Procedure r) throws Exception {
        EntityManagerCustom emc = new EntityManagerCustom();
        EntityManager em = emc.getEntityManager();

        emc.beginTransaction(em);

        //Checking parameters
        if (r == null) {
            throw new Exception("The Procedure passed by parameter is null.");
        }

        if (r.getDbcreationdate() == null) {
            r.setDbcreationdate(new Date());
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

    public static boolean updateProcedure(Procedure r) throws Exception {
        EntityManagerCustom emc = new EntityManagerCustom();
        EntityManager em = emc.getEntityManager();

        emc.beginTransaction(em);

        //Checking parameters
        if (r == null) {
            throw new Exception("The Procedure passed by parameter is null.");
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

    /* Specific operations on Descriptors */
    /**
     * Checks the number of Procedures that are currently in the "running"
     * status
     *
     * @return @throws Exception
     */
    public static int checkRunningProcedures() throws Exception {
        try {
            EntityManagerCustom emc = new EntityManagerCustom();
            EntityManager em = emc.getEntityManager();

            Query q = em.createNamedQuery("Procedure.findByStatus");
            q.setParameter("status", Constants.PROCEDURE_STATUS_RUNNING);
            List<Procedure> list = (List<Procedure>) q.getResultList();

            int n = list.size();

            return n;
        } catch (Exception ex) {
            throw new Exception("Cannot retrieve the number of current running threads");
        }
    }

    // Add the Procedure to the Procedure list
    public static Procedure addProcedure(Descriptor descriptor, User user, ResourceBundle localization) throws Exception {

        Logger log = Configuration.getInstance().getLogger();

        //Parameter check
        if (descriptor != null) {
            // Instantiating variables
            Procedure procedure;

            // Creating the procedure object
            procedure = new Procedure();
            procedure.setUuid(procedure.createUUID(descriptor));
            procedure.setDescriptor(descriptor);
            procedure.setOrganization(descriptor.getOrganization());
            procedure.setStartedby(user);
            procedure.setStatus(Constants.PROCEDURE_STATUS_FIRSTINSERT);

            try {
                //Saving the procedure to the database
                ProcedureMgr.insertProcedure(procedure);
                return procedure;

            } catch (Exception e) {
                // Removing the previously entered descriptor
                DescriptorMgr.deleteDescriptor(descriptor);

                // Raise an error if the insert procedure went wrong
                String errorMessage = localization.getString("error.insert.procedure");
                log.error(errorMessage);
                throw new Exception(errorMessage);
            }

        } else {
            // Raise an error if the insert procedure went wrong
            String errorMessage = localization.getString("error.missing.descriptor");
            log.error(errorMessage);
            throw new Exception(errorMessage);
        }
    }

    /**
     * Runs the procedure related to the specified Descriptor
     *
     * @param procedure
     * @param localization
     * @throws java.lang.Exception
     */
    public static void startProcedure(Procedure procedure, ResourceBundle localization, User user) throws Exception {

        Properties properties = Configuration.getInstance().getProperties();
        Logger log = Configuration.getInstance().getLogger();

        // If the procedure is not already dunning, start the procedure
        if (!procedure.getStatus().equals(Constants.PROCEDURE_STATUS_RUNNING)) {

            // Checking if the currently running procedures are under the limit of maximum simultaneous thread
            int maxThreadNumber = Integer.parseInt(properties.getProperty(Constants.KEY_THREAD_MAX_NUMBER, "5"));
            if (checkRunningProcedures() > maxThreadNumber) {
                // Set this procedure in queue ("waiting status")
                procedure.setStatus(Constants.PROCEDURE_STATUS_WAITING);

                // Updating the procedure
                ProcedureMgr.updateProcedure(procedure);

                // Logging the insert in the queue
                log.info(localization.getString("info.procedure.queued") + procedure.getUuid());

                //If the procedure can run
            } else {
                // Setting the procedure status to "running"
                procedure.setStatus(Constants.PROCEDURE_STATUS_RUNNING);
                procedure.setLastharvestdate(new Date());
                procedure.setStartedby(user);
                ProcedureMgr.updateProcedure(procedure);

                // Start the harvesting procedure (Thread)
                HarvestingMgr hmg = new HarvestingMgr(procedure, localization);
                hmg.start();
            }

        }
    }

    public synchronized static void processQueue(ResourceBundle localization, User user) {
        Logger log = Configuration.getInstance().getLogger();
        try {
            // Getting the list of all the waiting procedures
            List<Procedure> procedures = getProcedureByStatus(Constants.PROCEDURE_STATUS_WAITING);

            for (Procedure procedure : procedures) {
                // Running the procedures
                startProcedure(procedure, localization, user);
            }
        } catch (Exception e) {
            // Raise an error if the queue procedure went wrong
            String errorMessage = localization.getString("error.procedure.queue.error") + e.getMessage();
            log.error(errorMessage);
            //throw new Exception(errorMessage);
        }
    }

}
