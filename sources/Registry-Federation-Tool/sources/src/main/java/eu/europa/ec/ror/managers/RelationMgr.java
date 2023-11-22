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
import eu.europa.ec.ror.model.Register;
import eu.europa.ec.ror.model.Relation;
import eu.europa.ec.ror.utility.Configuration;
import eu.europa.ec.ror.utility.Constants;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

public class RelationMgr {

    public static final String ID = "id";
    
    public static final String OBJECT_REGISTER_ID = "objectRegister_id";
    public static final String OBJECT_REGISTER_URI = "objectRegister_uri";

    public static final String SUBJECT_REGISTER_ID = "subjectRegister_id";
    public static final String SUBJECT_REGISTER_URI = "subjectRegister_uri";

    public static final String STATUS = "status";
    
    public static final String PREDICATE = "predicate";

    public static final String SUBJECT_REGISTER_OR_OBJECT_REGISTER_ID = "subjectRegister_objectRegister_id";

    public static List<Relation> getAllRelations() {
        try {
            EntityManagerCustom emc = new EntityManagerCustom();
            EntityManager em = emc.getEntityManager();

            Query q = em.createNamedQuery("Relation.findAll");
            List<Relation> list = (List<Relation>) q.getResultList();

            return list;
        } catch (Exception ex) {
            return null;
        }
    }

    public static Relation getRelationByID(String ID) {
        try {
            EntityManagerCustom emc = new EntityManagerCustom();
            EntityManager em = emc.getEntityManager();

            Query q = em.createNamedQuery("Relation.findByUuid");
            q.setParameter("uuid", ID);
            Relation list = (Relation) q.getSingleResult();

            return list;
        } catch (Exception ex) {
            return null;
        }
    }

    public static List<Relation> getAllRelationsBySubjectregister(Register subjectregister) {
        try {
            EntityManagerCustom emc = new EntityManagerCustom();
            EntityManager em = emc.getEntityManager();

            Query q = em.createNamedQuery("Relation.findBySubjectasset");
            q.setParameter("subjectasset", subjectregister);
            List<Relation> list = (List<Relation>) q.getResultList();

            return list;
        } catch (Exception ex) {
            return null;
        }
    }

    public static List<Relation> getAllRelationsBySubjectregisterID(String sourceregisterID) {
        try {
            EntityManagerCustom emc = new EntityManagerCustom();
            EntityManager em = emc.getEntityManager();

            Query q = em.createNamedQuery("Relation.findBySubjectasset");
            q.setParameter("subjectasset", RegisterMgr.getRegisterByID(sourceregisterID));
            List<Relation> list = (List<Relation>) q.getResultList();

            return list;
        } catch (Exception ex) {
            return null;
        }
    }

    public static List<Relation> getAllRelationsBySourceregisterIDorRelatedregisterID(String registerID) {
        try {
            EntityManagerCustom emc = new EntityManagerCustom();
            EntityManager em = emc.getEntityManager();

            Query q = em.createNamedQuery("Relation.findBySubjectassetOrRelatedasset");
            q.setParameter("register", RegisterMgr.getRegisterByID(registerID));
            List<Relation> list = (List<Relation>) q.getResultList();

            return list;
        } catch (Exception ex) {
            return null;
        }
    }

    public static List<Relation> getAllRelationsBySubjectregisterURI(String sourceregisterURI) {
        try {
            EntityManagerCustom emc = new EntityManagerCustom();
            EntityManager em = emc.getEntityManager();

            Query q = em.createNamedQuery("Relation.findBySubjectasset");
            q.setParameter("subjectasset", RegisterMgr.getRegisterByUri(sourceregisterURI));
            List<Relation> list = (List<Relation>) q.getResultList();

            return list;
        } catch (Exception ex) {
            return null;
        }
    }

    public static List<Relation> getAllRelationsByRelatedregister(Register relatedregister) {
        try {
            EntityManagerCustom emc = new EntityManagerCustom();
            EntityManager em = emc.getEntityManager();

            Query q = em.createNamedQuery("Relation.findByRelatedregister");
            q.setParameter("relatedregister", relatedregister);
            List<Relation> list = (List<Relation>) q.getResultList();

            return list;
        } catch (Exception ex) {
            return null;
        }
    }

    public static List<Relation> getAllRelationsByRelatedregisterID(String relatedregisterID) {
        try {
            EntityManagerCustom emc = new EntityManagerCustom();
            EntityManager em = emc.getEntityManager();

            Query q = em.createNamedQuery("Relation.findByObjectasset");
            q.setParameter("objectasset", RegisterMgr.getRegisterByID(relatedregisterID));
            List<Relation> list = (List<Relation>) q.getResultList();

            return list;
        } catch (Exception ex) {
            return null;
        }
    }

    public static List<Relation> getAllRelationsByRelatedregisterURI(String relatedregisterURI) {
        try {
            EntityManagerCustom emc = new EntityManagerCustom();
            EntityManager em = emc.getEntityManager();

            Query q = em.createNamedQuery("Relation.findByObjectasset");
            q.setParameter("objectasset", RegisterMgr.getRegisterByUri(relatedregisterURI));
            List<Relation> list = (List<Relation>) q.getResultList();

            return list;
        } catch (Exception ex) {
            return null;
        }
    }
    
    public static List<Relation> getAllRelationsByStatus(String status) {
        try {
            EntityManagerCustom emc = new EntityManagerCustom();
            EntityManager em = emc.getEntityManager();

            Query q = em.createNamedQuery("Relation.findByStatus");
            q.setParameter("status", status);
            List<Relation> list = (List<Relation>) q.getResultList();

            return list;
        } catch (Exception ex) {
            return null;
        }
    }

    public static List<Relation> getAllRelationsByPredicate(String predicate) {
        try {
            EntityManagerCustom emc = new EntityManagerCustom();
            EntityManager em = emc.getEntityManager();

            Query q = em.createNamedQuery("Relation.findByPredicate");
            q.setParameter("predicate", predicate);
            List<Relation> list = (List<Relation>) q.getResultList();

            return list;
        } catch (Exception ex) {
            return null;
        }
    }

    public static boolean insertRelation(Relation r) throws Exception {
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

    public static boolean updateRelation(Relation r) throws Exception {
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
    
    public static boolean deleteRelation(Relation relation) throws Exception {
        EntityManagerCustom emc = new EntityManagerCustom();
        EntityManager em = emc.getEntityManager();

        emc.beginTransaction(em);

        //Checking parameters
        if (relation == null) {
            throw new Exception("The Relation passed by parameter is null.");
        }

        Relation relationtmp = em.merge(relation);

        em.remove(relationtmp);

        em.getEntityManagerFactory().getCache().evictAll();

        emc.commitTransaction(em);

        return true;
    }

    public static void setRelation(Register register, JSONObject registerJsonObject, ResourceBundle localization) throws Exception {
        Logger log = Configuration.getInstance().getLogger();

        //Getting the relies on propetry
        String reliesOn = registerJsonObject.getJSONObject("fields").getString("registerReliesOn");

        if (reliesOn != null && reliesOn.length() > 0) {

            // Check if the Register on which the currenr Register relies on is available in the federation
            Register objectRegister = RegisterMgr.getRegisterByUri(reliesOn);

            // If the referenced Register is null, it throws an error
            if (objectRegister == null) {
                // Getting the erroro message
                String errorMessage = localization.getString("error.insert.relation.register.notavailable").replace("{0}",reliesOn);

                // Raise the exception
                log.error(errorMessage);
                throw new Exception(errorMessage);
            }

            // Creating the relation object
            Relation relation = new Relation();

            String uuid = relation.createUUID(register, objectRegister);

            relation.setUuid(uuid);
            relation.setSubjectasset(register);
            relation.setPredicate(Constants.RELATION_PREDICATE_KEY);
            relation.setObjectasset(objectRegister);
            relation.setStatus(Constants.RELATION_STATUS_ACTIVE);

            //If the relation is already in the DB, update it
            if (RelationMgr.getRelationByID(uuid) != null) {
                RelationMgr.updateRelation(relation);
            } else {
                RelationMgr.insertRelation(relation);
            }
        }else{
            // If the reliesOn is not available, check if there is a relation 
            // for this register in the DB: in this case the relation is removed.
            
            List <Relation>relations = RelationMgr.getAllRelationsBySubjectregister(register);            
            if(relations!=null && relations.size()>0){                
                for(Relation tmpr : relations){
                    RelationMgr.deleteRelation(tmpr);
                }                
            }      
        }
    }
}
