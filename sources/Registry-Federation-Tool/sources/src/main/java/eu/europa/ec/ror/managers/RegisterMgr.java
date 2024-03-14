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
import eu.europa.ec.ror.model.Register;
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
import org.json.JSONObject;

public class RegisterMgr {

    public static final String URI = "uri";
    public static final String LABEL = "label";
    public static final String ID = "id";
    public static final String REGISTRY_ID = "registry_id";
    public static final String REGISTRY_URI = "registry_uri";

    public static List<Register> getAllRegisters() {
        try {
            EntityManagerCustom emc = new EntityManagerCustom();
            EntityManager em = emc.getEntityManager();

            Query q = em.createNamedQuery("Register.findAll");
            List<Register> list = (List<Register>) q.getResultList();

            return list;
        } catch (Exception ex) {
            return null;
        }
    }

    public static List<Register> getAllRegistersByRegistryUri(String registerUri) {
        try {
            EntityManagerCustom emc = new EntityManagerCustom();
            EntityManager em = emc.getEntityManager();

            Registry registry = RegistryMgr.getRegistryByUri(registerUri);

            Query q = em.createNamedQuery("Register.findByRegistry");
            q.setParameter("registry", registry);
            List<Register> list = (List<Register>) q.getResultList();

            return list;
        } catch (Exception ex) {
            return null;
        }
    }

    public static List<Register> getAllRegistersByRegistryID(String registerID) {
        try {
            EntityManagerCustom emc = new EntityManagerCustom();
            EntityManager em = emc.getEntityManager();

            Registry registry = RegistryMgr.getRegistryByID(registerID);

            Query q = em.createNamedQuery("Register.findByRegistry");
            q.setParameter("registry", registry);
            List<Register> list = (List<Register>) q.getResultList();

            return list;
        } catch (Exception ex) {
            return null;
        }
    }

    public static List<Register> getAllRegistersByRegistry(Registry registry) {
        try {
            EntityManagerCustom emc = new EntityManagerCustom();
            EntityManager em = emc.getEntityManager();

            Query q = em.createNamedQuery("Register.findByRegistry");
            q.setParameter("registry", registry);
            List<Register> list = (List<Register>) q.getResultList();

            return list;
        } catch (Exception ex) {
            return null;
        }
    }

    public static Register getRegisterByUri(String uri) {
        try {
            EntityManagerCustom emc = new EntityManagerCustom();
            EntityManager em = emc.getEntityManager();

            Query q = em.createNamedQuery("Register.findByUri");
            q.setParameter("uri", uri);
            Register list = (Register) q.getSingleResult();

            return list;
        } catch (Exception ex) {
            return null;
        }
    }

    public static Register getRegisterByDescriptor(Descriptor descriptor) {
        try {
            EntityManagerCustom emc = new EntityManagerCustom();
            EntityManager em = emc.getEntityManager();

            Query q = em.createNamedQuery("Register.findByDescriptor");
            q.setParameter("descriptor", descriptor);
            Register list = (Register) q.getSingleResult();

            return list;
        } catch (Exception ex) {
            return null;
        }
    }

    public static Register getRegisterByID(String id) {
        try {
            EntityManagerCustom emc = new EntityManagerCustom();
            EntityManager em = emc.getEntityManager();

            Query q = em.createNamedQuery("Register.findByUuid");
            q.setParameter("uuid", id);
            Register list = (Register) q.getSingleResult();

            return list;
        } catch (Exception ex) {
            return null;
        }
    }

    public static boolean insertRegister(Register r) throws Exception {
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

    public static boolean updateRegister(Register r) throws Exception {
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

    public static void harvestRegister(Procedure procedure, ResourceBundle localization) {
        Logger log = Configuration.getInstance().getLogger();

        String mailSubject = localization.getString("mail.subject.prefix.procedure.harvesting");
        String mailBody = "";

        try {
            // Validating the RDF/XML resource and getting the Register Json Object
            JSONObject registerJsonObject = ValidationMgr.validateRegistrDescriptor(procedure, localization);

            // Creating Register object
            Register register = RegisterMgr.createRegisterFromJsonObject(registerJsonObject, procedure.getDescriptor());

            // Storing or updating the register object
            if (RegisterMgr.getRegisterByID(register.getUuid()) != null) {
                // Updating the existing register object
                RegisterMgr.updateRegister(register);
            } else {
                // Storing the register object
                RegisterMgr.insertRegister(register);
            }
            
            //Update Solr index whith the current register
            SolrMgr.indexRegister(register, localization);

            // Creating the relation using the reliesOn property
            try {
                RelationMgr.setRelation(register, registerJsonObject, localization);
            } catch (Exception e) {
                // Getting the erroro message
                String errorMessage = localization.getString("error.insert.relation.register.error").replace("{0}", register.getUri()) + e.getMessage();

                // Raise the exception
                log.error(errorMessage);
                throw new Exception(errorMessage);
            }

            // Indexing items content to SOLR
            SolrMgr.indexDocs(registerJsonObject, localization, register);

            //Updating the procedure status to completed
            try {
                procedure.setStatus(Constants.PROCEDURE_STATUS_SUCCESS);
                procedure.setMessage(ValidationMgr.getValidationHTML(procedure, localization));
                
                //Setting the date next harvest if needed
                if (register.getUpdatefrequency() != null && register.getUpdatefrequency().length() > 0) {
                    Date nextDate = AutomaticHarvestingMgr.calculateNextDate(register.getUpdatefrequency());
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

                String message = localization.getString("info.procedure.completed").replace("{0}", procedure.getDescriptor().getUrl());
                log.info(message);
                mailSubject += localization.getString("mail.subject.success");
                mailBody = message;

            } catch (Exception ex) {
                // Cannot update the procedure, logging the error
                String error = localization.getString("error.procedure.status.cantupdate").replace("{0}", procedure.getDescriptor().getUrl()) + ex.getMessage();
                log.error(error);
                mailSubject = localization.getString("mail.subject.error");
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
            mailSubject = localization.getString("mail.subject.error");
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

            } catch (Exception ex) {
                // Cannot update the procedure, logging the error
                error = localization.getString("error.procedure.status.cantupdate").replace("{0}", procedure.getDescriptor().getUrl()) + ex.getMessage();
                log.error(error);
                mailSubject = localization.getString("mail.subject.error");
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

    public static Register createRegisterFromJsonObject(JSONObject jsonObject, Descriptor descriptor) throws Exception {

        JSONObject fields = jsonObject.getJSONObject("fields");

        Register register = new Register();

        // Getting the Registry to create the Register object
        Registry registry = RegistryMgr.getRegistryByUri(fields.getString("registryUri"));

        // Setting fields from the JSON Object
        register.setUuid(register.createUUID(descriptor));
        register.setUri(fields.getString("registerUri"));
        register.setRegistry(registry);
        register.setLabel(fields.getString("registerName"));
        register.setDescription(fields.getString("registerDescription"));
        register.setPublisheruri(fields.getString("registerPublisherUri"));
        register.setPublishername(fields.getString("registerPublisherName"));
        register.setPublisheremail(fields.getString("registerPublisherEmail"));
        register.setPublisherhomepage(fields.getString("registerPublisherHomepage"));
        register.setUpdatefrequency(fields.getString("registerUpdateFrequency"));
        register.setDescriptor(descriptor);

        return register;
    }
}
