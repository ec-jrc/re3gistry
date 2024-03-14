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
import eu.europa.ec.ror.model.Procedurehistory;
import eu.europa.ec.ror.model.Registry;
import eu.europa.ec.ror.utility.Configuration;
import eu.europa.ec.ror.utility.Constants;
import eu.europa.ec.ror.utility.Mail;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

public class RegistryMgr {

    public static final String URI = "uri";
    public static final String LABEL = "label";
    public static final String ID = "id";

    public static List<Registry> getAllRegistries() {
        try {
            EntityManagerCustom emc = new EntityManagerCustom();
            EntityManager em = emc.getEntityManager();

            Query q = em.createNamedQuery("Registry.findAll");
            List<Registry> list = (List<Registry>) q.getResultList();

            return list;
        } catch (Exception ex) {
            return null;
        }
    }

    public static Registry getRegistryByUri(String uri) {
        try {
            EntityManagerCustom emc = new EntityManagerCustom();
            EntityManager em = emc.getEntityManager();

            Query q = em.createNamedQuery("Registry.findByUri");
            q.setParameter("uri", uri);
            Registry list = (Registry) q.getSingleResult();

            return list;
        } catch (Exception ex) {
            return null;
        }
    }

    public static Registry getRegistryByID(String id) {
        try {
            EntityManagerCustom emc = new EntityManagerCustom();
            EntityManager em = emc.getEntityManager();

            Query q = em.createNamedQuery("Registry.findByUuid");
            q.setParameter("uuid", id);
            Registry list = (Registry) q.getSingleResult();

            return list;
        } catch (Exception ex) {
            return null;
        }
    }

    public static List<Registry> getRegistryByManager(String manager) {
        try {
            EntityManagerCustom emc = new EntityManagerCustom();
            EntityManager em = emc.getEntityManager();

            Query q = em.createNamedQuery("Registry.findByManager");
            q.setParameter("manager", manager);
            List<Registry> list = (List<Registry>) q.getResultList();

            return list;
        } catch (Exception ex) {
            return null;
        }
    }

    public static List<Registry> getRegistryByContactpoint(String contactpoint) {
        try {
            EntityManagerCustom emc = new EntityManagerCustom();
            EntityManager em = emc.getEntityManager();

            Query q = em.createNamedQuery("Registry.findByContactpoint");
            q.setParameter("contactpoint", contactpoint);
            List<Registry> list = (List<Registry>) q.getResultList();

            return list;
        } catch (Exception ex) {
            return null;
        }
    }

    public static List<Registry> getRegistryByManagerAndContactPoint(String manager, String contactpoint) {
        try {
            EntityManagerCustom emc = new EntityManagerCustom();
            EntityManager em = emc.getEntityManager();

            Query q = em.createNamedQuery("Registry.findByManagerAndContactpoint");
            q.setParameter("contactpoint", contactpoint);
            q.setParameter("manager", manager);
            List<Registry> list = (List<Registry>) q.getResultList();

            return list;
        } catch (Exception ex) {
            return null;
        }
    }

    public static Registry getRegistryByDescriptor(Descriptor descriptor) {
        try {
            EntityManagerCustom emc = new EntityManagerCustom();
            EntityManager em = emc.getEntityManager();

            Query q = em.createNamedQuery("Registry.findByDescriptor");
            q.setParameter("descriptor", descriptor);

            return (Registry) q.getSingleResult();
        } catch (Exception ex) {
            return null;
        }
    }

    public static boolean insertRegistry(Registry r) throws Exception {
        EntityManagerCustom emc = new EntityManagerCustom();
        EntityManager em = emc.getEntityManager();

        emc.beginTransaction(em);

        //Checking parameters
        if (r == null) {
            throw new Exception("The Item passed by parameter is null.");
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

    public static boolean updateRegistry(Registry r) throws Exception {
        EntityManagerCustom emc = new EntityManagerCustom();
        EntityManager em = emc.getEntityManager();

        emc.beginTransaction(em);

        //Checking parameters
        if (r == null) {
            throw new Exception("The Item passed by parameter is null.");
        }

        if (r.getDblasteditdate() == null) {
            r.setDblasteditdate(new Date());
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

    public static void harvestRegistry(Procedure procedure, ResourceBundle localization) {
        Logger log = Configuration.getInstance().getLogger();

        // Initializing the subject with the name of the type of procvedure
        String mailSubject = localization.getString("mail.subject.prefix.procedure.harvesting");
        String mailBody = "";

        try {
            // Validating the RDF/XML resource and getting the Registry Json Object
            JSONObject registryJsonObject = ValidationMgr.validateRegistryDescriptor(procedure, localization);

            // Creating Registry object
            Registry registry = RegistryMgr.createRegistryFromJsonObject(registryJsonObject, procedure.getDescriptor());

            // Storing or updating the registry object
            if (RegistryMgr.getRegistryByID(registry.getUuid()) != null) {
                // Updating the existing registry object
                RegistryMgr.updateRegistry(registry);
            } else {
                // Storing the registry object
                RegistryMgr.insertRegistry(registry);
            }

            //Update Solr index whith the current registry
            SolrMgr.indexRegistry(registry, localization);

            // Check registers contained in the Registry descriptor
            JSONArray registers = registryJsonObject.getJSONArray("registers");

            // Check if some Register are in the DB and need to be deleted (no more in the descriptor)
            List<Descriptor> availableRegisterDescriptors = DescriptorMgr.getDescriptorByParentDescriptor(procedure.getDescriptor());

            // Check and remove the Registers that have been removed from the Registry descriptor
            String deletedRegisters = DescriptorMgr.checkAndRemoveRegisterDescriptor(availableRegisterDescriptors, registers, localization);

            // For each Register in the Registry Descriptor, add the Register Descriptor
            String registerDescriptorAddingErrors = "";
            for (int i = 0; i < registers.length(); i++) {
                JSONObject tmp = registers.getJSONObject(i).getJSONObject("register");
                try {
                    // Add register descriptor
                    Procedure registerProcedure = addRegisterDescriptorAndProcedure(tmp, procedure, localization);

                    // Starting the Register harvesting
                    if (registerProcedure != null) {
                        ProcedureMgr.startProcedure(registerProcedure, localization, procedure.getStartedby());
                    }

                } catch (Exception e) {
                    // Adding the current Register Descriptor to the list of errors.
                    registerDescriptorAddingErrors += "[" + tmp.getString("registerUrl") + " - " + e.getMessage() + "]; ";
                }
            }

            // If the addition of the Register Descriptor produces some errors, the system set the error and updates the procedure
            if (registerDescriptorAddingErrors.length() > 0) {
                // Getting the error message
                String error = localization.getString("error.procedure.registry.addRegisterDescriptor") + registerDescriptorAddingErrors;

                log.error(error);
                throw new Exception(error);
            }

            //Updating the procedure status to completed
            try {
                procedure.setStatus(Constants.PROCEDURE_STATUS_SUCCESS);
                procedure.setMessage(ValidationMgr.getValidationHTML(procedure, localization));

                //Setting the date next harvest if needed
                if (registry.getUpdatefrequency() != null && registry.getUpdatefrequency().length() > 0) {
                    Date nextDate = AutomaticHarvestingMgr.calculateNextDate(registry.getUpdatefrequency());
                    if (nextDate != null) {
                        procedure.setNextharvestdate(nextDate);
                    }
                }

                ProcedureMgr.updateProcedure(procedure);

                //Creating the procedure history object
                Procedurehistory procedurehistory = new Procedurehistory();
                procedurehistory.setUuid(procedurehistory.createUUID(procedure));
                procedurehistory.setProcedure(procedure);
                procedurehistory.setStartedby(procedure.getStartedby());
                procedurehistory.setStartdate(procedure.getLastharvestdate());
                procedurehistory.setEnddate(new Date());
                procedurehistory.setStatus(procedure.getStatus());
                procedurehistory.setMessage(procedure.getMessage());
                ProcedurehistoryMgr.insertProcedure(procedurehistory);

                if (deletedRegisters != null && deletedRegisters.length() > 0) {
                    deletedRegisters = localization.getString("mail.body.note.deletedregisters").replace("{0}", "<ul>" + deletedRegisters + "</ul>");
                }

                String message = localization.getString("info.procedure.completed").replace("{0}", procedure.getDescriptor().getUrl()) + deletedRegisters;
                log.info(message);
                mailSubject += localization.getString("mail.subject.success");
                mailBody = message;

            } catch (Exception ex) {
                // Cannot update the procedure, logging the error
                String error = localization.getString("error.procedure.status.cantupdate").replace("{0}", procedure.getDescriptor().getUrl()) + ex.getMessage();
                log.error(error);
                mailSubject += localization.getString("mail.subject.error");
                mailBody = localization.getString("mail.body.error") + error;
            }
        } catch (Exception e) {
            String error;
            String infoError;

            // Filtering the case of HTML error
            if (e.getMessage().contains("<html")) {
                error = e.getMessage();
                infoError = localization.getString("error.procedure.suffix.checkweb").replace("{0}", procedure.getDescriptor().getUrl());
            } else {
                error = localization.getString("error.procedure.suffix").replace("{0}", procedure.getDescriptor().getUrl()) + e.getMessage();
                infoError = error;
            }
            log.error(infoError);
            mailSubject += localization.getString("mail.subject.error");
            mailBody = localization.getString("mail.body.error") + infoError;

            try {
                // Setting the procedure status to "error"
                procedure.setStatus(Constants.PROCEDURE_STATUS_ERROR);
                procedure.setMessage(error);
                ProcedureMgr.updateProcedure(procedure);

                //Creating the procedure history object
                Procedurehistory procedurehistory = new Procedurehistory();
                procedurehistory.setUuid(procedurehistory.createUUID(procedure));
                procedurehistory.setProcedure(procedure);
                procedurehistory.setStartedby(procedure.getStartedby());
                procedurehistory.setStartdate(procedure.getLastharvestdate());
                procedurehistory.setEnddate(new Date());
                procedurehistory.setStatus(procedure.getStatus());
                procedurehistory.setMessage(error);
                ProcedurehistoryMgr.insertProcedure(procedurehistory);

                log.error(error);
            } catch (Exception ex) {
                // Cannot update the procedure, logging the error
                error = localization.getString("error.procedure.status.cantupdate").replace("{0}", procedure.getDescriptor().getUrl()) + ex.getMessage();
                log.error(error);
                mailSubject += localization.getString("mail.subject.error");
                mailBody = localization.getString("mail.body.error") + error;
            }
        }

        // Sending mail
        try {
            Properties properties = Configuration.getInstance().getProperties();
            String sendMailLevel = properties.getProperty("mail.send.level");
            if (sendMailLevel.equals("1") || (sendMailLevel.equals("0") && (mailSubject.contains(localization.getString("mail.subject.error")) || !mailSubject.contains(localization.getString("mail.subject.success"))))) {
                Mail.sendMail(localization, procedure.getOrganization().getEmail(), mailSubject, mailBody);
            }
        } catch (Exception e) {
            log.error(localization.getString("mail.error.sending") + e.getMessage());
        }
    }

    public static Registry createRegistryFromJsonObject(JSONObject jsonObject, Descriptor descriptor) throws Exception {

        JSONObject fields = jsonObject.getJSONObject("fields");

        Registry registry = new Registry();

        // Setting fields from the JSON Object
        registry.setUuid(registry.createUUID(descriptor));
        registry.setUri(fields.getString("registryUri"));
        registry.setLabel(fields.getString("registryName"));
        registry.setDescription(fields.getString("registryDescription"));
        registry.setPublisheruri(fields.getString("registryPublisherUri"));
        registry.setPublishername(fields.getString("registryPublisherName"));
        registry.setPublisheremail(fields.getString("registryPublisherEmail"));
        registry.setPublisherhomepage(fields.getString("registryPublisherHomepage"));
        registry.setUpdatefrequency(fields.getString("registryUpdateFrequency"));
        registry.setDescriptor(descriptor);

        return registry;
    }

    private static Procedure addRegisterDescriptorAndProcedure(JSONObject jsonObject, Procedure procedure, ResourceBundle localization) throws Exception {
        Logger log = Configuration.getInstance().getLogger();

        // Adding register descriptor and procedure
        Procedure newProcedure = null;

        if (DescriptorMgr.getDescriptorByUrl(jsonObject.getString("registerUrl")) == null) {
            // If the Descriptor is not in the system

            // Add the new descriptor to the DB
            Descriptor descriptor = DescriptorMgr.addDescriptor(jsonObject.getString("registerUrl"), Constants.DESCRIPTOR_TYPE_REGISTER, procedure.getDescriptor().getOrganization(), procedure.getStartedby(), procedure.getDescriptor(), localization);

            // Add the procedure to the procedure list
            newProcedure = ProcedureMgr.addProcedure(descriptor, procedure.getStartedby(), localization);

            log.info(localization.getString("info.descriptor.register.added").replace("{0}", jsonObject.getString("registerUrl")));

            return newProcedure;
        } else {
            log.info(localization.getString("info.descriptor.register.already.available").replace("{0}", jsonObject.getString("registerUrl")));

            // If the Register descriptor is already available, getting it and the related procedure.
            // Buto, the procedure is returned to be runned only if the current procedure has not been started by the automatic harvester.
            Procedure registerProcedure = null;
            Properties properties = Configuration.getInstance().getProperties();
            String automaticUserUuid = properties.getProperty("application.automaticharvesting.userid");
            if (!procedure.getStartedby().getUuid().equals(automaticUserUuid)) {
                Descriptor registerDescriptor = DescriptorMgr.getDescriptorByUrl(jsonObject.getString("registerUrl"));
                registerProcedure = ProcedureMgr.getProcedureByDescriptor(registerDescriptor);

            }
            return registerProcedure;
        }
    }
}
