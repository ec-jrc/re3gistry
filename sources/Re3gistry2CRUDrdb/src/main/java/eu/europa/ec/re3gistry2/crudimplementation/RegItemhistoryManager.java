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
package eu.europa.ec.re3gistry2.crudimplementation;

import eu.europa.ec.re3gistry2.crudimplementation.constants.ErrorConstants;
import eu.europa.ec.re3gistry2.crudimplementation.constants.SQLConstants;
import eu.europa.ec.re3gistry2.crudinterface.IRegItemhistoryManager;
import eu.europa.ec.re3gistry2.model.RegAction;
import eu.europa.ec.re3gistry2.model.RegItem;
import eu.europa.ec.re3gistry2.model.RegItemclass;
import eu.europa.ec.re3gistry2.model.RegItemhistory;
import eu.europa.ec.re3gistry2.model.RegRelationpredicate;
import eu.europa.ec.re3gistry2.model.RegStatus;
import java.text.MessageFormat;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;

public class RegItemhistoryManager implements IRegItemhistoryManager {

    private EntityManager em;

    public RegItemhistoryManager(EntityManager em) {
        this.em = em;
    }

    /**
     * Returns the RegItemhistory object
     *
     * @param uuid The uuid of the RegItemhistory
     * @return RegItemhistory object with the uuid passed by parameter
     * @throws java.lang.Exception
     */
    @Override
    public RegItemhistory get(String uuid) throws Exception {

        //Checking parameters
        if (uuid == null) {
            throw new Exception(MessageFormat.format(ErrorConstants.ERROR_MANAGER_PATTERN_NULL, "uuid"));
        }

        //Preparing query
        Query q = this.em.createNamedQuery("RegItemhistory.findByUuid");
        q.setParameter("uuid", uuid);
        return (RegItemhistory) q.getSingleResult();

    }

    /**
     * Returns all the RegItemhistory
     *
     * @return all the RegItemhistory
     * @throws Exception
     */
    @Override
    public List<RegItemhistory> getAll() throws Exception {

        //Preparing query
        Query q = this.em.createNamedQuery("RegItemhistory.findAll");
        return (List<RegItemhistory>) q.getResultList();
    }

    /**
     * Adds a RegItemhistory to the database. Returns true if the operation
     * succeed.
     *
     * @param regItemhistory The RegItemhistory object to be added
     * @return True if the operation was successfully completed; otherwise it
     * returns false.
     * @throws Exception
     */
    @Override
    public boolean add(RegItemhistory regItemhistory) throws Exception {
        //Checking parameters
        if (regItemhistory == null) {
            throw new Exception(MessageFormat.format(ErrorConstants.ERROR_MANAGER_PATTERN_NULL, RegItemhistory.class));
        }

        //Checking the DB managers
        if (this.em == null) {
            throw new Exception(ErrorConstants.ERROR_MANAGER_PERSISTENCE_LAYER_NULL);
        }

        //Saving the object
        this.em.persist(regItemhistory);

        return true;
    }

    /**
     * Update the RegItemhistory specified by parameter. Returns true if the
     * operation succeed.
     *
     * @param regItemhistory the updated RegItemhistory (this method update the
     * RegItemhistory on the db with the RegItemhistory passed by parameter)
     * @return True if the operation was successfully completed; otherwise it
     * returns false.
     * @throws java.lang.Exception
     */
    @Override
    public boolean update(RegItemhistory regItemhistory) throws Exception {
        //Checking parameters
        if (regItemhistory == null) {
            throw new Exception(MessageFormat.format(ErrorConstants.ERROR_MANAGER_PATTERN_NULL, RegItemhistory.class));
        }

        //Checking the DB managers
        if (this.em == null) {
            throw new Exception(ErrorConstants.ERROR_MANAGER_PERSISTENCE_LAYER_NULL);
        }

        //Updating the object
        em.merge(regItemhistory);

        return true;
    }

    /**
     * Find the RegItemhistory specified by parameter. Returns RegItemhistory if
     * the operation succeed.
     *
     * @param localid the localid of the RegItemhistory
     * @param regItemclass the regItemclass of the RegItemhistory
     * @return True if the operation was successfully completed; otherwise it
     * returns false.
     * @throws java.lang.Exception
     */
    @Override
    public List<RegItemhistory> getByLocalidAndRegItemClass(String localid, RegItemclass regItemclass) throws Exception {
        //Checking parameters
        if (localid == null || regItemclass == null) {
            throw new Exception(MessageFormat.format(ErrorConstants.ERROR_MANAGER_PATTERN_NULL, "uuid"));
        }

        //Preparing query
        Query q = this.em.createQuery(SQLConstants.SQL_GET_REGITEMHISTORY_BY_LOCALID_REGITEMCLASS);
        q.setParameter(SQLConstants.SQL_PARAMETERS_LOCALID, localid);
        q.setParameter(SQLConstants.SQL_PARAMETERS_REGITEMCLASS, regItemclass);

        return q.getResultList();
    }

    /**
     * Find the RegItemhistory specified by parameter. Returns RegItemhistory if
     * the operation succeed.
     *
     * @param localid the localid of the RegItemhistory
     * @param versionnumber the version of the RegItemhistory
     * @param regItemclass the regItemclass of the RegItemhistory
     * @return True if the operation was successfully completed; otherwise it
     * returns false.
     * @throws java.lang.Exception
     */
    @Override
    public RegItemhistory getByLocalidVersionnumberAndRegItemClass(String localid, int versionnumber, RegItemclass regItemclass) throws Exception {
        //Checking parameters
        if (localid == null || regItemclass == null) {
            throw new Exception(MessageFormat.format(ErrorConstants.ERROR_MANAGER_PATTERN_NULL, "uuid"));
        }

        //Preparing query
        Query q = this.em.createQuery(SQLConstants.SQL_GET_REGITEMHISTORY_BY_LOCALID_VERSION_REGITEMCLASS);
        q.setParameter(SQLConstants.SQL_PARAMETERS_LOCALID, localid);
        q.setParameter(SQLConstants.SQL_PARAMETERS_VERSIONNUMBER, versionnumber);
        q.setParameter(SQLConstants.SQL_PARAMETERS_REGITEMCLASS, regItemclass);

        return (RegItemhistory) q.getSingleResult();
    }

    @Override
    public RegItemhistory getByLocalidVersionnumberRegItemClassRegItemReference(String localid, int versionnumber, RegItemclass regItemclass, RegItem regItemReference) throws Exception {
        //Checking parameters
        if (localid == null || regItemclass == null) {
            throw new Exception(MessageFormat.format(ErrorConstants.ERROR_MANAGER_PATTERN_NULL, "uuid"));
        }

        //Preparing query
        Query q = this.em.createQuery(SQLConstants.SQL_GET_REGITEMHISTORY_BY_LOCALID_VERSION_REGITEMCLASS_REITEMREFERENCE);
        q.setParameter(SQLConstants.SQL_PARAMETERS_LOCALID, localid);
        q.setParameter(SQLConstants.SQL_PARAMETERS_VERSIONNUMBER, versionnumber);
        q.setParameter(SQLConstants.SQL_PARAMETERS_REGITEMCLASS, regItemclass);
        q.setParameter(SQLConstants.SQL_PARAMETERS_REGITEMREFERENCE, regItemReference);

        return (RegItemhistory) q.getSingleResult();
    }

    @Override
    public List<RegItemhistory> getByRegItemReference(RegItem regItemReference) throws Exception {
        //Checking parameters
        if (regItemReference == null) {
            throw new Exception(MessageFormat.format(ErrorConstants.ERROR_MANAGER_PATTERN_NULL, "uuid"));
        }

        //Preparing query
        Query q = this.em.createQuery(SQLConstants.SQL_GET_REGITEMHISTORY_BY_REITEMREFERENCE);
        q.setParameter(SQLConstants.SQL_PARAMETERS_REGITEMREFERENCE, regItemReference);

        return (List<RegItemhistory>) q.getResultList();
    }

    /**
     * Find the RegItemhistory specified by parameter. Returns RegItemhistory if
     * the operation succeed.
     *
     * @param localid the localid of the RegItemhistory
     * @param regItemclass the regItemclass of the RegItemhistory
     * @return True if the operation was successfully completed; otherwise it
     * returns false.
     * @throws java.lang.Exception
     */
    @Override
    public RegItemhistory getMaxVersionByLocalidAndRegItemClass(String localid, RegItemclass regItemclass) throws Exception {
        //Checking parameters
        if (localid == null || regItemclass == null) {
            throw new Exception(MessageFormat.format(ErrorConstants.ERROR_MANAGER_PATTERN_NULL, SQLConstants.SQL_PARAMETERS_LOCALID));
        }

        //Preparing query
        Query q = this.em.createQuery(SQLConstants.SQL_GET_REGITEMHISTORY_BY_LOCALID_REGITEMCLASS_MAX_VERSION);
        q.setParameter(SQLConstants.SQL_PARAMETERS_LOCALID, localid);
        q.setParameter(SQLConstants.SQL_PARAMETERS_REGITEMCLASS, regItemclass);

        return (RegItemhistory) q.getSingleResult();
    }

    /**
     * Find the RegItemhistory specified by parameter. Returns RegItemhistory if
     * the operation succeed.
     *
     * @param localid the localid of the RegItemhistory
     * @param regItemclass the regItemclass of the RegItemhistory
     * @return True if the operation was successfully completed; otherwise it
     * returns false.
     * @throws java.lang.Exception
     */
    @Override
    public RegItemhistory getMinVersionByLocalidAndRegItemClass(String localid, RegItemclass regItemclass) throws Exception {
        //Checking parameters
        if (localid == null || regItemclass == null) {
            throw new Exception(MessageFormat.format(ErrorConstants.ERROR_MANAGER_PATTERN_NULL, SQLConstants.SQL_PARAMETERS_LOCALID));
        }

        //Preparing query
        Query q = this.em.createQuery(SQLConstants.SQL_GET_REGITEMHISTORY_BY_LOCALID_REGITEMCLASS_MIN_VERSION);
        q.setParameter(SQLConstants.SQL_PARAMETERS_LOCALID, localid);
        q.setParameter(SQLConstants.SQL_PARAMETERS_REGITEMCLASS, regItemclass);

        return (RegItemhistory) q.getSingleResult();
    }

    /**
     * Returns all the RegItemproposeds by RegAction
     *
     * @param regAction
     * @return all the RegItemproposeds
     * @throws Exception
     */
    @Override
    public List<RegItemhistory> getAll(RegAction regAction) throws Exception {

        //Preparing query
        Query q = this.em.createQuery(SQLConstants.SQL_GET_REGITEMHISTORY_BY_REGACTION);
        q.setParameter("regAction", regAction);
        return (List<RegItemhistory>) q.getResultList();
    }

    /**
     * Returns all the RegItemproposeds by RegAction
     *
     * @param regItemclass
     * @return all the RegItemhistory
     * @throws Exception
     */
    @Override
    public List<RegItemhistory> getByRegItemClass(RegItemclass regItemclass) throws Exception {
        //Checking parameters
        if (regItemclass == null) {
            throw new Exception(MessageFormat.format(ErrorConstants.ERROR_MANAGER_PATTERN_NULL, "uuid"));
        }

        //Preparing query
        Query q = this.em.createQuery(SQLConstants.SQL_GET_REGITEMHISTORY_BY_REGITEMCLASS);
        q.setParameter(SQLConstants.SQL_PARAMETERS_REGITEMCLASS, regItemclass);

        return (List<RegItemhistory>) q.getResultList();
    }

    /**
     * Returns all RegItems (subject) by RegItem (object) and RegPredicate where
     * subject RegItems must not have second RegPredicate Useful for example for
     * getting items that have specific register but don't have any collection
     * (e.g. find the ones that are directly contained in register)
     *
     * @param regItem object
     * @param regRelationPredicate
     * @param subjectNotHavingPredicate
     * @return list of RegItem
     * @throws Exception
     */
    @Override
    public List<RegItem> getAllSubjectsByRegItemObjectAndPredicateAndSubjectNotPredicate(RegItemhistory regItem, RegRelationpredicate regRelationPredicate, RegRelationpredicate subjectNotHavingPredicate) throws Exception {
        //Preparing query
        Query q = this.em.createQuery(SQLConstants.SQL_GET_RELATIONHISTORY_BY_OBJECT_PREDICATE_AND_SUBJECT_FILTER);
        q.setParameter(SQLConstants.SQL_PARAMETERS_REGITEM, regItem);
        q.setParameter(SQLConstants.SQL_PARAMETERS_PREDICATE, regRelationPredicate);
        q.setParameter(SQLConstants.SQL_PARAMETERS_NOT_PREDICATE, subjectNotHavingPredicate);
        try {
            return (List<RegItem>) q.getResultList();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Returns all RegItems (subject) by RegItem (object) and RegPredicate where
     * subject RegItems must not have second RegPredicate Useful for example for
     * getting items that have specific register but don't have any collection
     * (e.g. find the ones that are directly contained in register)
     *
     * @param regItem object
     * @param regStatus
     * @param regRelationPredicate
     * @param subjectNotHavingPredicate
     * @return list of RegItem
     * @throws Exception
     */
    public List<String> getAllItemByRegItemObjectAndPredicateAndSubjectNotPredicate(RegItem regItem, RegStatus regStatus, RegRelationpredicate regRelationPredicate, RegRelationpredicate subjectNotHavingPredicate) throws Exception {
        //Preparing query
        Query q = null;
        try {
            q = this.em.createQuery(SQLConstants.SQL_GET_REG_ITEM_BY_SUBJECT_PREDICATE_AND_FILTER_PREDICATE);
            q.setParameter(SQLConstants.SQL_PARAMETERS_REGITEM, regItem);
            q.setParameter(SQLConstants.SQL_PARAMETERS_REGSTATUS, regStatus);
            q.setParameter(SQLConstants.SQL_PARAMETERS_PREDICATE, regRelationPredicate);
            q.setParameter(SQLConstants.SQL_PARAMETERS_NOT_PREDICATE, subjectNotHavingPredicate);
        } catch (Exception e) {
            try {
                String query = "select r0.reg_item_subject from (select * from reg_relation r JOIN reg_item ri on ri.uuid = r.reg_item_subject WHERE ri.reg_status = ':regStatus' AND r.reg_item_object = ':regitem' and r.reg_relationpredicate = ':predicate') r0 where r0.reg_item_subject not in (select r1.reg_item_subject from reg_relation r1 where r1.reg_relationpredicate = ':notpredicate')";
                query = query.replace(":" + SQLConstants.SQL_PARAMETERS_REGSTATUS, regStatus.getUuid()).replace(":" + SQLConstants.SQL_PARAMETERS_REGITEM, regItem.getUuid()).replace(":" + SQLConstants.SQL_PARAMETERS_PREDICATE, regRelationPredicate.getUuid()).replace(":" + SQLConstants.SQL_PARAMETERS_NOT_PREDICATE, subjectNotHavingPredicate.getUuid());
                q = this.em.createNativeQuery(query);
            } catch (Exception ex) {
                return null;
            }
        }
        return (List<String>) q.getResultList();
    }

    /**
     * Returns all RegItems (subject) by RegItem (object) and RegPredicate where
     * subject RegItems must not have second RegPredicate Useful for example for
     * getting items that have specific register but don't have any collection
     * (e.g. find the ones that are directly contained in register)
     *
     * @param regItem object
     * @param regStatus
     * @param regRelationPredicate
     * @param subjectNotHavingPredicate
     * @return list of RegItem
     * @throws Exception
     */
    @Override
    public List<String> getAllItemByRegItemObjectAndPredicateAndSubjectNotPredicate(RegItemhistory regItem, RegStatus regStatus, RegRelationpredicate regRelationPredicate, RegRelationpredicate subjectNotHavingPredicate) throws Exception {
        //Preparing query
        Query q = null;
        try {
            q = this.em.createQuery(SQLConstants.SQL_GET_REG_ITEM_BY_SUBJECT_PREDICATE_AND_FILTER_PREDICATE);
            q.setParameter(SQLConstants.SQL_PARAMETERS_REGITEM, regItem);
            q.setParameter(SQLConstants.SQL_PARAMETERS_REGSTATUS, regStatus);
            q.setParameter(SQLConstants.SQL_PARAMETERS_PREDICATE, regRelationPredicate);
            q.setParameter(SQLConstants.SQL_PARAMETERS_NOT_PREDICATE, subjectNotHavingPredicate);
        } catch (Exception e) {
            try {
                String query = "select r0.reg_item_subject from (select * from reg_relation r JOIN reg_item ri on ri.uuid = r.reg_item_subject WHERE ri.reg_status = ':regStatus' AND r.reg_item_object = ':regitem' and r.reg_relationpredicate = ':predicate') r0 where r0.reg_item_subject not in (select r1.reg_item_subject from reg_relation r1 where r1.reg_relationpredicate = ':notpredicate')";
                query = query.replace(":" + SQLConstants.SQL_PARAMETERS_REGSTATUS, regStatus.getUuid()).replace(":" + SQLConstants.SQL_PARAMETERS_REGITEM, regItem.getUuid()).replace(":" + SQLConstants.SQL_PARAMETERS_PREDICATE, regRelationPredicate.getUuid()).replace(":" + SQLConstants.SQL_PARAMETERS_NOT_PREDICATE, subjectNotHavingPredicate.getUuid());
                q = this.em.createNativeQuery(query);
            } catch (Exception ex) {
                return null;
            }
        }
        return (List<String>) q.getResultList();
    }

}
