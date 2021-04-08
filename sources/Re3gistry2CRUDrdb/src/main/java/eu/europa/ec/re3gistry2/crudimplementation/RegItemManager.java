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
import eu.europa.ec.re3gistry2.model.RegItem;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import eu.europa.ec.re3gistry2.crudinterface.IRegItemManager;
import eu.europa.ec.re3gistry2.model.RegAction;
import eu.europa.ec.re3gistry2.model.RegItemclass;
import eu.europa.ec.re3gistry2.model.RegItemclasstype;
import eu.europa.ec.re3gistry2.model.RegRelationpredicate;
import eu.europa.ec.re3gistry2.model.RegStatus;
import java.text.MessageFormat;
import java.util.Iterator;

public class RegItemManager implements IRegItemManager {

    private EntityManager em;

    public RegItemManager(EntityManager em) {
        this.em = em;
    }

    /**
     * Returns the RegItem object
     *
     * @param uuid The uuid of the RegItem
     * @return RegItem object with the uuid passed by parameter
     * @throws java.lang.Exception
     */
    @Override
    public RegItem get(String uuid) throws Exception {

        //Checking parameters
        if (uuid == null) {
            throw new Exception(MessageFormat.format(ErrorConstants.ERROR_MANAGER_PATTERN_NULL, "uuid"));
        }

        //Preparing query
        Query q = this.em.createNamedQuery("RegItem.findByUuid");
        q.setParameter("uuid", uuid);
        return (RegItem) q.getSingleResult();

    }

    /**
     * Returns all the RegItems
     *
     * @return all the RegItems
     * @throws Exception
     */
    @Override
    public List<RegItem> getAll() throws Exception {

        //Preparing query
        Query q = this.em.createNamedQuery("RegItem.findAll");
        return (List<RegItem>) q.getResultList();
    }

    /**
     * Adds a RegItem to the database. Returns true if the operation succeed.
     *
     * @param regItem The RegItem object to be added
     * @return True if the operation was successfully completed; otherwise it
     * returns false.
     * @throws Exception
     */
    @Override
    public boolean add(RegItem regItem) throws Exception {
        //Checking parameters
        if (regItem == null) {
            throw new Exception(MessageFormat.format(ErrorConstants.ERROR_MANAGER_PATTERN_NULL, RegItem.class));
        }

        //Checking the DB managers
        if (this.em == null) {
            throw new Exception(ErrorConstants.ERROR_MANAGER_PERSISTENCE_LAYER_NULL);
        }

        //Saving the object
        this.em.persist(regItem);

        return true;
    }

    /**
     * Update the RegItem specified by parameter. Returns true if the operation
     * succeed.
     *
     * @param regItem the updated RegItem (this method update the RegItem on the
     * db with the RegItem passed by parameter)
     * @return True if the operation was successfully completed; otherwise it
     * returns false.
     * @throws java.lang.Exception
     */
    @Override
    public boolean update(RegItem regItem) throws Exception {
        //Checking parameters
        if (regItem == null) {
            throw new Exception(MessageFormat.format(ErrorConstants.ERROR_MANAGER_PATTERN_NULL, RegItem.class));
        }

        //Checking the DB managers
        if (this.em == null) {
            throw new Exception(ErrorConstants.ERROR_MANAGER_PERSISTENCE_LAYER_NULL);
        }

        //Updating the object
        em.merge(regItem);

        return true;
    }

    @Override
    public boolean delete(RegItem regItem) throws Exception {
        //Checking parameters
        if (regItem == null) {
            throw new Exception(MessageFormat.format(ErrorConstants.ERROR_MANAGER_PATTERN_NULL, RegItem.class));
        }

        //Checking the DB managers
        if (this.em == null) {
            throw new Exception(ErrorConstants.ERROR_MANAGER_PERSISTENCE_LAYER_NULL);
        }

        //Updating the object
        if (!em.contains(regItem)) {
            regItem = em.merge(regItem);
        }

        em.remove(regItem);

        return true;
    }

    /**
     * Returns all RegItem by localid
     *
     * @param localid the localid of the RegItem
     * @throws java.lang.Exception
     */
    @Override
    public List<RegItem> getByLocalid(String localid) throws Exception {
        //Checking parameters
        if (localid == null) {
            throw new Exception(MessageFormat.format(ErrorConstants.ERROR_MANAGER_PATTERN_NULL, "uuid"));
        }

        //Preparing query
        Query q = this.em.createQuery(SQLConstants.SQL_GET_REGITEM_BY_LOCALID);
        q.setParameter(SQLConstants.SQL_PARAMETERS_LOCALID, localid);

        return (List<RegItem>) q.getResultList();
    }

    /**
     * Find the RegItem specified by parameter. Returns RegItem if the operation
     * succeed.
     *
     * @param localid the localid of the RegItem
     * @param regItemclass the RegItemclass of the RegItem
     * @return True if the operation was successfully completed; otherwise it
     * returns false.
     * @throws java.lang.Exception
     */
    @Override
    public RegItem getByLocalidAndRegItemClass(String localid, RegItemclass regItemclass) throws Exception {
        //Checking parameters
        if (localid == null || regItemclass == null) {
            throw new Exception(MessageFormat.format(ErrorConstants.ERROR_MANAGER_PATTERN_NULL, "uuid"));
        }

        //Preparing query
        Query q = this.em.createQuery(SQLConstants.SQL_GET_REGITEM_BY_LOCALID_REGITEMCLASS);
        q.setParameter(SQLConstants.SQL_PARAMETERS_LOCALID, localid);
        q.setParameter(SQLConstants.SQL_PARAMETERS_REGITEMCLASS, regItemclass);

        return (RegItem) q.getSingleResult();
    }

    /**
     * Returns all the RegItems by RegItemType
     *
     * @param regItemcalsstype
     * @return all the RegItems
     * @throws Exception
     */
    @Override
    public List<RegItem> getAll(RegItemclasstype regItemcalsstype) throws Exception {

        //Preparing query
        Query q = this.em.createQuery(SQLConstants.SQL_GET_REGITEM_BY_REGITEMCLASSTYPE);
        q.setParameter("regItemclasstype", regItemcalsstype);
        return (List<RegItem>) q.getResultList();
    }

    /**
     * Returns all the RegItems by RegItemType
     *
     * @param regItemcalsstype
     * @return all the RegItems
     * @throws Exception
     */
    @Override
    public List<RegItem> getAllActive(RegItemclasstype regItemcalsstype) throws Exception {

        //Preparing query
        Query q = this.em.createQuery(SQLConstants.SQL_GET_REGITEM_BY_REGITEMCLASSTYPE_ACTIVE);
        q.setParameter("regItemclasstype", regItemcalsstype);
        return (List<RegItem>) q.getResultList();
    }

    /**
     * Returns all the RegItems by RegItemclass
     *
     * @param regItemcalss
     * @return all the RegItems
     * @throws Exception
     */
    @Override
    public List<RegItem> getAll(RegItemclass regItemcalss) throws Exception {

        //Preparing query
        Query q = this.em.createQuery(SQLConstants.SQL_GET_REGITEM_BY_REGITEMCLASS);
        q.setParameter(SQLConstants.SQL_PARAMETERS_REGITEMCLASS, regItemcalss);
        return (List<RegItem>) q.getResultList();
    }

    /**
     * Returns all the RegItems by RegAction
     *
     * @param regAction
     * @return all the RegItems
     * @throws Exception
     */
    @Override
    public List<RegItem> getAll(RegAction regAction) throws Exception {

        //Preparing query
        Query q = this.em.createQuery(SQLConstants.SQL_GET_REGITEM_BY_REGACTION);
        q.setParameter("regAction", regAction);
        return (List<RegItem>) q.getResultList();
    }

    /**
     * Returns all the RegItems by a List of RegItemclass paged
     *
     * @param regItemcalsses
     * @param start
     * @param maxResults
     * @return all the RegItems
     * @throws Exception
     */
    @Override
    public List<RegItem> getAll(List<RegItemclass> regItemcalsses, int start, int maxResults) throws Exception {

        //Preparing query
        Query q = this.em.createQuery(SQLConstants.SQL_GET_REGITEM_BY_REGITEMCLASSES);
        q.setParameter(SQLConstants.SQL_PARAMETERS_REGITEMCLASSES, regItemcalsses);
        q.setMaxResults(maxResults);
        q.setFirstResult(start);
        return (List<RegItem>) q.getResultList();
    }

    /**
     * Returns all the RegItems by a List of RegItemclass paged
     *
     * @param regItemcalsses
     * @param start
     * @param maxResults
     * @param systemItems
     * @return all the RegItems
     * @throws Exception
     */
    @Override
    public List<RegItem> getAll(List<RegItemclass> regItemcalsses, int start, int maxResults, boolean systemItems) throws Exception {

        //Preparing query
        Query q;
        if (systemItems) {
            q = this.em.createQuery(SQLConstants.SQL_GET_REGITEM_BY_REGITEMCLASSES);
        } else {
            q = this.em.createQuery(SQLConstants.SQL_GET_REGITEM_BY_REGITEMCLASSES_NO_SYSTEMITEMS);
        }

        q.setParameter(SQLConstants.SQL_PARAMETERS_REGITEMCLASSES, regItemcalsses);
        q.setMaxResults(maxResults);
        q.setFirstResult(start);

        List<RegItem> tmps = (List<RegItem>) q.getResultList();

        if (!systemItems) {
            Iterator<RegItem> iter = tmps.iterator();
            while (iter.hasNext()) {
                RegItem tmp = iter.next();
                if (tmp.getRegItemclass().getSystemitem()) {
                    iter.remove();
                }
            }
        }

        return tmps;
    }

    /**
     * Returns all the RegItems by a List of RegItemclass paged
     *
     * @param regItemcalsses
     * @return all the RegItems
     * @throws Exception
     */
    @Override
    public int countAll(List<RegItemclass> regItemcalsses) throws Exception {

        //Preparing query
        Query q = this.em.createQuery(SQLConstants.SQL_GET_REGITEM_BY_REGITEMCLASSES_COUNT);
        q.setParameter(SQLConstants.SQL_PARAMETERS_REGITEMCLASSES, regItemcalsses);

        return ((Number) q.getSingleResult()).intValue();
    }

    /**
     * Returns all the RegItems by a List of RegItemclass paged
     *
     * @param regItemcalsses
     * @return all the RegItems
     * @throws Exception
     */
    @Override
    public int countAll(List<RegItemclass> regItemcalsses, boolean systemItems) throws Exception {

        //Preparing query
        Query q;
        if (systemItems) {
            q = this.em.createQuery(SQLConstants.SQL_GET_REGITEM_BY_REGITEMCLASSES_COUNT);
        } else {
            q = this.em.createQuery(SQLConstants.SQL_GET_REGITEM_BY_REGITEMCLASSES_COUNT_NO_SYSTEMITEMS);
        }
        q.setParameter(SQLConstants.SQL_PARAMETERS_REGITEMCLASSES, regItemcalsses);

        return ((Number) q.getSingleResult()).intValue();
    }

    /**
     * Returns all the items subject of a relation passed by parameter with
     * object the RegItem passed by parameter
     *
     * @param regRelationpredicate
     * @param regItemObject
     * @param start
     * @param length
     * @return all the RegItems
     * @throws Exception
     */
    @Override
    public List<RegItem> getAll(RegRelationpredicate regRelationpredicate, RegItem regItemObject, int start, int length) throws Exception {

        //Preparing query
        Query q = this.em.createQuery(SQLConstants.SQL_GET_REGITEM_BY_RELATION_AND_ITEMOBJECT);
        q.setParameter(SQLConstants.SQL_PARAMETERS_REGRELATIONPREDICATE, regRelationpredicate);
        q.setParameter(SQLConstants.SQL_PARAMETERS_REGITEMOBJECT, regItemObject);
        q.setMaxResults(length);
        q.setFirstResult(start);

        return (List<RegItem>) q.getResultList();
    }

    /**
     * Returns all the items subject of a relation passed by parameter with
     * object the RegItem passed by parameter
     *
     * @param regRelationpredicate
     * @param regItemObject
     * @param start
     * @param length
     * @param systemItems
     * @return all the RegItems
     * @throws Exception
     */
    @Override
    public List<RegItem> getAll(RegRelationpredicate regRelationpredicate, RegItem regItemObject, int start, int length, boolean systemItems) throws Exception {

        //Preparing query
        Query q;
        if (systemItems) {
            q = this.em.createQuery(SQLConstants.SQL_GET_REGITEM_BY_RELATION_AND_ITEMOBJECT);
        } else {
            q = this.em.createQuery(SQLConstants.SQL_GET_REGITEM_BY_RELATION_AND_ITEMOBJECT_NO_SYSTEMITEM);
        }

        q.setParameter(SQLConstants.SQL_PARAMETERS_REGRELATIONPREDICATE, regRelationpredicate);
        q.setParameter(SQLConstants.SQL_PARAMETERS_REGITEMOBJECT, regItemObject);
        q.setMaxResults(length);
        q.setFirstResult(start);

        List<RegItem> tmps = (List<RegItem>) q.getResultList();

        if (!systemItems) {
            Iterator<RegItem> iter = tmps.iterator();
            while (iter.hasNext()) {
                RegItem tmp = iter.next();
                if (tmp.getRegItemclass().getSystemitem()) {
                    iter.remove();
                }
            }
        }

        return tmps;
    }

    /**
     * Returns all the RegItem subject of a relation passed by parameter with
     * object the RegItem passed by parameter
     *
     * @param regRelationpredicate
     * @param regItemObject
     * @return all the RegItems
     * @throws Exception
     */
    @Override
    public int countAll(RegRelationpredicate regRelationpredicate, RegItem regItemObject) throws Exception {

        //Preparing query
        Query q = this.em.createQuery(SQLConstants.SQL_GET_REGITEM_BY_RELATION_AND_ITEMOBJECT_COUNT);
        q.setParameter(SQLConstants.SQL_PARAMETERS_REGRELATIONPREDICATE, regRelationpredicate);
        q.setParameter(SQLConstants.SQL_PARAMETERS_REGITEMOBJECT, regItemObject);
        return ((Number) q.getSingleResult()).intValue();
    }

    /**
     * Returns all the RegItem subject of a relation passed by parameter with
     * object the RegItem passed by parameter
     *
     * @param regRelationpredicate
     * @param regItemObject
     * @param systemItems
     * @return all the RegItems
     * @throws Exception
     */
    @Override
    public int countAll(RegRelationpredicate regRelationpredicate, RegItem regItemObject, boolean systemItems) throws Exception {

        //Preparing query
        Query q;
        if (systemItems) {
            q = this.em.createQuery(SQLConstants.SQL_GET_REGITEM_BY_RELATION_AND_ITEMOBJECT_COUNT);
        } else {
            q = this.em.createQuery(SQLConstants.SQL_GET_REGITEM_BY_RELATION_AND_ITEMOBJECT_COUNT_NO_SYSTEMITEM);
        }
        q.setParameter(SQLConstants.SQL_PARAMETERS_REGRELATIONPREDICATE, regRelationpredicate);
        q.setParameter(SQLConstants.SQL_PARAMETERS_REGITEMOBJECT, regItemObject);
        return ((Number) q.getSingleResult()).intValue();
    }

    @Override
    public List<RegItem> getChildItemsList(RegItem regItemObject) throws Exception {
        //Preparing query
        //Preparing query
        RegRelationpredicateManager regRelationpredicateManager = new RegRelationpredicateManager(em);

        Query q = this.em.createQuery(SQLConstants.SQL_GET_REGITEM_BY_RELATION_AND_ITEMOBJECT);
        q.setParameter(SQLConstants.SQL_PARAMETERS_REGRELATIONPREDICATE, regRelationpredicateManager.getByLocalid("hasParent"));
        q.setParameter(SQLConstants.SQL_PARAMETERS_REGITEMOBJECT, regItemObject);

        return (List<RegItem>) q.getResultList();
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
    public List<RegItem> getAllSubjectsByRegItemObjectAndPredicateAndSubjectNotPredicate(RegItem regItem, RegRelationpredicate regRelationPredicate, RegRelationpredicate subjectNotHavingPredicate) throws Exception {
        //Preparing query
        Query q = this.em.createQuery(SQLConstants.SQL_GET_REG_ITEM_BY_OBJECT_PREDICATE_AND_SUBJECT_FILTER);
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
    @Override
    public List<String> getAllItemByRegItemObjectAndPredicateAndSubjectNotPredicate(RegItem regItem, RegStatus regStatus, RegRelationpredicate regRelationPredicate, RegRelationpredicate subjectNotHavingPredicate) throws Exception {
        //Preparing query
        Query q = null;
//            q = this.em.createQuery(SQLConstants.SQL_GET_REG_ITEM_BY_SUBJECT_PREDICATE_AND_FILTER_PREDICATE);
//            q.setParameter(SQLConstants.SQL_PARAMETERS_REGITEM, regItem);
//            q.setParameter(SQLConstants.SQL_PARAMETERS_REGSTATUS, regStatus);
//            q.setParameter(SQLConstants.SQL_PARAMETERS_PREDICATE, regRelationPredicate);
//            q.setParameter(SQLConstants.SQL_PARAMETERS_NOT_PREDICATE, subjectNotHavingPredicate);
        try {
            String query = "select r0.reg_item_subject from (select * from reg_relation r JOIN reg_item ri on ri.uuid = r.reg_item_subject WHERE ri.reg_status = ':regStatus' AND r.reg_item_object = ':regitem' and r.reg_relationpredicate = ':predicate') as r0 where r0.reg_item_subject not in (select r1.reg_item_subject from reg_relation r1 where r1.reg_relationpredicate = ':notpredicate')";
            query = query.replace(":" + SQLConstants.SQL_PARAMETERS_REGSTATUS, regStatus.getUuid()).replace(":" + SQLConstants.SQL_PARAMETERS_REGITEM, regItem.getUuid()).replace(":" + SQLConstants.SQL_PARAMETERS_PREDICATE, regRelationPredicate.getUuid()).replace(":" + SQLConstants.SQL_PARAMETERS_NOT_PREDICATE, subjectNotHavingPredicate.getUuid());
            q = this.em.createNativeQuery(query);
        } catch (Exception ex) {
            System.err.println("RegItemManager.getAllItemByRegItemObjectAndPredicateAndSubjectNotPredicate(), regItem: " + regItem.getLocalid());
            return null;
        }
        return (List<String>) q.getResultList();
    }

}
