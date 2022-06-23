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
import eu.europa.ec.re3gistry2.crudinterface.IRegRelationManager;
import eu.europa.ec.re3gistry2.model.RegItem;
import eu.europa.ec.re3gistry2.model.RegRelation;
import eu.europa.ec.re3gistry2.model.RegRelationpredicate;
import java.text.MessageFormat;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;

public class RegRelationManager implements IRegRelationManager {

    private EntityManager em;

    public RegRelationManager(EntityManager em) {
        this.em = em;
    }

    /**
     * Returns the RegRelation object
     *
     * @param uuid The uuid of the RegRelation
     * @return RegRelation object with the uuid passed by parameter
     * @throws java.lang.Exception
     */
    @Override
    public RegRelation get(String uuid) throws Exception {

        //Checking parameters
        if (uuid == null) {
            throw new Exception(MessageFormat.format(ErrorConstants.ERROR_MANAGER_PATTERN_NULL, "uuid"));
        }

        //Preparing query
        Query q = this.em.createNamedQuery("RegRelation.findByUuid");
        q.setParameter("uuid", uuid);
        return (RegRelation) q.getSingleResult();

    }

    /**
     * Returns all the RegRelation
     *
     * @return all the RegRelation
     * @throws Exception
     */
    @Override
    public List<RegRelation> getAll() throws Exception {

        //Preparing query
        Query q = this.em.createNamedQuery("RegRelation.findAll");
        return (List<RegRelation>) q.getResultList();
    }

    /**
     * Adds a RegRelation to the database. Returns true if the operation
     * succeed.
     *
     * @param regRelation The RegRelation object to be added
     * @return True if the operation was successfully completed; otherwise it
     * returns false.
     * @throws Exception
     */
    @Override
    public boolean add(RegRelation regRelation) throws Exception {
        //Checking parameters
        if (regRelation == null) {
            throw new Exception(MessageFormat.format(ErrorConstants.ERROR_MANAGER_PATTERN_NULL, RegRelation.class));
        }

        //Checking the DB managers
        if (this.em == null) {
            throw new Exception(ErrorConstants.ERROR_MANAGER_PERSISTENCE_LAYER_NULL);
        }

        //Saving the object
        this.em.persist(regRelation);

        return true;
    }

    /**
     * Update the RegRelation specified by parameter. Returns true if the
     * operation succeed.
     *
     * @param regRelation the updated RegRelation (this method update the
     * RegRelation on the db with the RegRelation passed by parameter)
     * @return True if the operation was successfully completed; otherwise it
     * returns false.
     * @throws java.lang.Exception
     */
    @Override
    public boolean update(RegRelation regRelation) throws Exception {
        //Checking parameters
        if (regRelation == null) {
            throw new Exception(MessageFormat.format(ErrorConstants.ERROR_MANAGER_PATTERN_NULL, RegRelation.class));
        }

        //Checking the DB managers
        if (this.em == null) {
            throw new Exception(ErrorConstants.ERROR_MANAGER_PERSISTENCE_LAYER_NULL);
        }

        //Updating the object
        em.merge(regRelation);

        return true;
    }

    public boolean delete(RegRelation regRelation) throws Exception {
        //Checking parameters
        if (regRelation == null) {
            throw new Exception(MessageFormat.format(ErrorConstants.ERROR_MANAGER_PATTERN_NULL, RegRelation.class));
        }

        //Checking the DB managers
        if (this.em == null) {
            throw new Exception(ErrorConstants.ERROR_MANAGER_PERSISTENCE_LAYER_NULL);
        }

        //Updating the object
        em.remove(regRelation);

        return true;
    }

    /**
     * Returns all the RegRelation where the RegItem passed as parameter is the
     * subject
     *
     * @param regItem
     * @return list of RegRelation
     * @throws Exception
     */
    @Override
    public List<RegRelation> getAllBySubject(RegItem regItem) throws Exception {

        //Preparing query
        Query q = this.em.createQuery(SQLConstants.SQL_GET_RELATION_BY_SUBJECT_ITEM);
        q.setParameter(SQLConstants.SQL_PARAMETERS_REGITEM, regItem);
        return (List<RegRelation>) q.getResultList();
    }
    
        /**
     * Returns all the RegRelation where the RegItem passed as parameter is the
     * subject
     *
     * @param regItem
     * @return list of RegRelation
     * @throws Exception
     */
    @Override
    public List<RegRelation> getAllByObject(RegItem regItem) throws Exception {

        //Preparing query
        Query q = this.em.createQuery(SQLConstants.SQL_GET_RELATION_BY_OBJECT_ITEM);
        q.setParameter(SQLConstants.SQL_PARAMETERS_REGITEM, regItem);
        return (List<RegRelation>) q.getResultList();
    }

    /**
     * Returns all the RegRelation by RegItem (subject) and RegPredicate
     *
     * @param regItem
     * @param regRelationPredicate
     * @return list of RegRelation
     * @throws Exception
     */
    @Override
    public List<RegRelation> getAll(RegItem regItem, RegRelationpredicate regRelationPredicate) throws Exception {

        //Preparing query
        Query q = this.em.createQuery(SQLConstants.SQL_GET_RELATION_COLLECTION_REFERENCE);
        q.setParameter(SQLConstants.SQL_PARAMETERS_REGITEM, regItem);
        q.setParameter(SQLConstants.SQL_PARAMETERS_PREDICATE, regRelationPredicate);
        try {
            return (List<RegRelation>) q.getResultList();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Returns all the RegRelation by RegItem (object) and RegPredicate
     *
     * @param regItem
     * @param regRelationPredicate
     * @return list of RegRelation
     * @throws Exception
     */
    @Override
    public List<RegRelation> getAllByRegItemObjectAndPredicate(RegItem regItem, RegRelationpredicate regRelationPredicate) throws Exception {

        //Preparing query
        Query q = this.em.createQuery(SQLConstants.SQL_GET_RELATION_OBJECT_PREDICATE);
        q.setParameter(SQLConstants.SQL_PARAMETERS_REGITEM, regItem);
        q.setParameter(SQLConstants.SQL_PARAMETERS_PREDICATE, regRelationPredicate);
        try {
            return (List<RegRelation>) q.getResultList();
        } catch (Exception e) {
            return null;
        }
    }
      
    /**
     * Returns all the RegRelation by RegItem (object) and RegPredicate
     *
     * @param regItem
     * @param regRelationPredicate
     * @param start
     * @param length
     * @return list of RegRelation
     * @throws Exception
     */
    @Override
    public List<RegRelation> getAllByRegItemObjectAndPredicate(RegItem regItem, RegRelationpredicate regRelationPredicate, int start, int length) throws Exception {

        //Preparing query
        Query q = this.em.createQuery(SQLConstants.SQL_GET_RELATION_OBJECT_PREDICATE);
        q.setParameter(SQLConstants.SQL_PARAMETERS_REGITEM, regItem);
        q.setParameter(SQLConstants.SQL_PARAMETERS_PREDICATE, regRelationPredicate);
        q.setMaxResults(length);
        q.setFirstResult(start);
        try {
            return (List<RegRelation>) q.getResultList();
        } catch (Exception e) {
            return null;
        }
    }
    
     /**
     * Returns all the RegRelation by RegItem (object) and RegPredicate
     *
     * @param regItem
     * @param regRelationPredicate
     * @return list of RegRelation
     * @throws Exception
     */
    @Override
    public List<RegRelation> getAllByRegItemSubjectAndPredicate(RegItem regItem, RegRelationpredicate regRelationPredicate) throws Exception {

        //Preparing query
        Query q = this.em.createQuery(SQLConstants.SQL_GET_RELATION_SUBJECT_PREDICATE);
        q.setParameter(SQLConstants.SQL_PARAMETERS_REGITEM, regItem);
        q.setParameter(SQLConstants.SQL_PARAMETERS_PREDICATE, regRelationPredicate);
        try {
            return (List<RegRelation>) q.getResultList();
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * Returns all the RegRelation by RegItems (subject) and RegPredicate
     *
     * @param regItems
     * @param regRelationPredicate
     * @return list of RegRelation
     * @throws Exception
     */
    @Override
    public List<RegRelation> getAllByRegItemsSubjectAndPredicate(List<RegItem> regItems, RegRelationpredicate regRelationPredicate) throws Exception {
        //Preparing query
        Query q = this.em.createQuery(SQLConstants.SQL_GET_RELATION_SUBJECTS_PREDICATE);
        q.setParameter(SQLConstants.SQL_PARAMETERS_REGITEM_LIST, regItems);
        q.setParameter(SQLConstants.SQL_PARAMETERS_PREDICATE, regRelationPredicate);
        try {
            return (List<RegRelation>) q.getResultList();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Returns all the RegRelation by RegItem (object) and RegPredicate where subject RegItems can not satisfy the second RegPredicate
     * Useful for getting items that have specific register but don't have any collection (directly contained in the register)
     *
     * @param regItem
     * @param regRelationPredicate
     * @param subjectNotHavingPredicate
     * @return list of RegRelation
     * @throws Exception
     */
    @Override
    public List<RegRelation> getAllByRegItemObjectAndPredicateAndSubjectNotPredicate(RegItem regItem, RegRelationpredicate regRelationPredicate, RegRelationpredicate subjectNotHavingPredicate) throws Exception {
        //Preparing query
        Query q = this.em.createQuery(SQLConstants.SQL_GET_SUBJECT_BY_OBJECT_PREDICATE_AND_SUBJECT_FILTER);
        q.setParameter(SQLConstants.SQL_PARAMETERS_REGITEM, regItem);
        q.setParameter(SQLConstants.SQL_PARAMETERS_PREDICATE, regRelationPredicate);
        q.setParameter(SQLConstants.SQL_PARAMETERS_NOT_PREDICATE, subjectNotHavingPredicate);
        try {
            return (List<RegRelation>) q.getResultList();
        } catch (Exception e) {
            return null;
        }
    }

}
