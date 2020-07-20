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
import eu.europa.ec.re3gistry2.model.RegItemproposed;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import eu.europa.ec.re3gistry2.crudinterface.IRegItemproposedManager;
import eu.europa.ec.re3gistry2.model.RegAction;
import eu.europa.ec.re3gistry2.model.RegItem;
import eu.europa.ec.re3gistry2.model.RegItemclass;
import eu.europa.ec.re3gistry2.model.RegItemclasstype;
import eu.europa.ec.re3gistry2.model.RegRelationpredicate;
import eu.europa.ec.re3gistry2.model.RegUser;
import java.text.MessageFormat;

public class RegItemproposedManager implements IRegItemproposedManager {

    private EntityManager em;

    public RegItemproposedManager(EntityManager em) {
        this.em = em;
    }

    /**
     * Returns the RegItemproposed object
     *
     * @param uuid The uuid of the RegItemproposed
     * @return RegItemproposed object with the uuid passed by parameter
     * @throws java.lang.Exception
     */
    @Override
    public RegItemproposed get(String uuid) throws Exception {

        //Checking parameters
        if (uuid == null) {
            throw new Exception(MessageFormat.format(ErrorConstants.ERROR_MANAGER_PATTERN_NULL, "uuid"));
        }

        //Preparing query
        Query q = this.em.createNamedQuery("RegItemproposed.findByUuid");
        q.setParameter("uuid", uuid);
        return (RegItemproposed) q.getSingleResult();

    }

    /**
     * Returns all the RegItemproposeds
     *
     * @return all the RegItemproposeds
     * @throws Exception
     */
    @Override
    public List<RegItemproposed> getAll() throws Exception {

        //Preparing query
        Query q = this.em.createNamedQuery("RegItemproposed.findAll");
        return (List<RegItemproposed>) q.getResultList();
    }

    /**
     * Adds a RegItemproposed to the database. Returns true if the operation
     * succeed.
     *
     * @param regItemproposed The RegItemproposed object to be added
     * @return True if the operation was successfully completed; otherwise it
     * returns false.
     * @throws Exception
     */
    @Override
    public boolean add(RegItemproposed regItemproposed) throws Exception {
        //Checking parameters
        if (regItemproposed == null) {
            throw new Exception(MessageFormat.format(ErrorConstants.ERROR_MANAGER_PATTERN_NULL, RegItemproposed.class));
        }

        //Checking the DB managers
        if (this.em == null) {
            throw new Exception(ErrorConstants.ERROR_MANAGER_PERSISTENCE_LAYER_NULL);
        }

        //Saving the object
        this.em.persist(regItemproposed);

        return true;
    }

    /**
     * Update the RegItemproposed specified by parameter. Returns true if the
     * operation succeed.
     *
     * @param regItem the updated RegItemproposed (this method update the
     * RegItemproposed on the db with the RegItemproposed passed by parameter)
     * @return True if the operation was successfully completed; otherwise it
     * returns false.
     * @throws java.lang.Exception
     */
    @Override
    public boolean update(RegItemproposed regItem) throws Exception {
        //Checking parameters
        if (regItem == null) {
            throw new Exception(MessageFormat.format(ErrorConstants.ERROR_MANAGER_PATTERN_NULL, RegItemproposed.class));
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
    public boolean delete(RegItemproposed regItemproposed) throws Exception {
        //Checking parameters
        if (regItemproposed == null) {
            throw new Exception(MessageFormat.format(ErrorConstants.ERROR_MANAGER_PATTERN_NULL, RegItemproposed.class));
        }

        //Checking the DB managers
        if (this.em == null) {
            throw new Exception(ErrorConstants.ERROR_MANAGER_PERSISTENCE_LAYER_NULL);
        }

        //Updating the object
        if (!em.contains(regItemproposed)) {
            regItemproposed = em.merge(regItemproposed);
        }

        em.remove(regItemproposed);

        return true;
    }

    /**
     * Find the RegItemproposed specified by parameter. Returns RegItemproposed
     * if the operation succeed.
     *
     * @param localid the localid of the RegItemproposed
     * @param regItemclass the RegItemclass of the RegItemproposed
     * @return True if the operation was successfully completed; otherwise it
     * returns false.
     * @throws java.lang.Exception
     */
    @Override
    public RegItemproposed getByLocalidAndRegItemClass(String localid, RegItemclass regItemclass) throws Exception {
        //Checking parameters
        if (localid == null || regItemclass == null) {
            throw new Exception(MessageFormat.format(ErrorConstants.ERROR_MANAGER_PATTERN_NULL, "uuid"));
        }

        //Preparing query
        Query q = this.em.createQuery(SQLConstants.SQL_GET_REGITEMPROPOSED_BY_LOCALID_REGITEMCLASS);
        q.setParameter(SQLConstants.SQL_PARAMETERS_LOCALID, localid);
        q.setParameter(SQLConstants.SQL_PARAMETERS_REGITEMCLASS, regItemclass);

        return (RegItemproposed) q.getSingleResult();
    }

    /**
     * Returns all the RegItemproposeds by RegItemType
     *
     * @param regItemcalsstype
     * @return all the RegItemproposeds
     * @throws Exception
     */
    @Override
    public List<RegItemproposed> getAll(RegItemclasstype regItemcalsstype) throws Exception {

        //Preparing query
        Query q = this.em.createQuery(SQLConstants.SQL_GET_REGITEMPROPOSED_BY_REGITEMCLASSTYPE);
        q.setParameter("regItemclasstype", regItemcalsstype);
        return (List<RegItemproposed>) q.getResultList();
    }

    /**
     * Returns all the RegItemproposeds by RegItemclass
     *
     * @param regItemcalss
     * @return all the RegItemproposeds
     * @throws Exception
     */
    @Override
    public List<RegItemproposed> getAll(RegItemclass regItemcalss) throws Exception {

        //Preparing query
        Query q = this.em.createQuery(SQLConstants.SQL_GET_REGITEMPROPOSED_BY_REGITEMCLASS);
        q.setParameter(SQLConstants.SQL_PARAMETERS_REGITEMCLASS, regItemcalss);
        return (List<RegItemproposed>) q.getResultList();
    }

    /**
     * Returns all the RegItemproposeds by a List of RegItemclass paged
     *
     * @param regItemcalsses
     * @param start
     * @param maxResults
     * @return all the RegItemproposeds
     * @throws Exception
     */
    @Override
    public List<RegItemproposed> getAll(List<RegItemclass> regItemcalsses, int start, int maxResults) throws Exception {

        //Preparing query
        Query q = this.em.createQuery(SQLConstants.SQL_GET_REGITEMPROPOSED_BY_REGITEMCLASSES);
        q.setParameter(SQLConstants.SQL_PARAMETERS_REGITEMCLASSES, regItemcalsses);
        q.setMaxResults(maxResults);
        q.setFirstResult(start);
        return (List<RegItemproposed>) q.getResultList();
    }

    /**
     * Returns all the newly proposed RegItemproposeds by a List of RegItemclass
     * paged
     *
     * @param regItemcalsses
     * @param start
     * @param maxResults
     * @return all the RegItemproposeds
     * @throws Exception
     */
    @Override
    public List<RegItemproposed> getAllNew(List<RegItemclass> regItemcalsses, int start, int maxResults) throws Exception {

        //Preparing query
        Query q = this.em.createQuery(SQLConstants.SQL_GET_REGITEMPROPOSED_NEW_BY_REGITEMCLASSES);
        q.setParameter(SQLConstants.SQL_PARAMETERS_REGITEMCLASSES, regItemcalsses);
        q.setMaxResults(maxResults);
        q.setFirstResult(start);
        return (List<RegItemproposed>) q.getResultList();
    }

    /**
     * Returns all the newly proposed RegItemproposeds by a List of RegItemclass
     * paged
     *
     * @param regItemcalsses
     * @param collection
     * @param regRelationpredicateCollection
     * @param start
     * @param maxResults
     * @return all the RegItemproposeds
     * @throws Exception
     */
    @Override
    public List<RegItemproposed> getAllNew(List<RegItemclass> regItemcalsses, RegItem collection, RegRelationpredicate regRelationpredicateCollection, int start, int maxResults) throws Exception {

        //Preparing query
        Query q = this.em.createQuery(SQLConstants.SQL_GET_REGITEMPROPOSED_NEW_BY_REGITEMCLASSES_COLLECTION);
        q.setParameter(SQLConstants.SQL_PARAMETERS_REGITEMCLASSES, regItemcalsses);
        q.setParameter(SQLConstants.SQL_PARAMETERS_REGITEMOBJECT, collection);
        q.setParameter(SQLConstants.SQL_PARAMETERS_REGRELATIONPREDICATE, regRelationpredicateCollection);
        q.setMaxResults(maxResults);
        q.setFirstResult(start);
        return (List<RegItemproposed>) q.getResultList();
    }

    /**
     * Returns all the RegItemproposeds by a List of RegItemclass paged
     *
     * @param regItemcalsses
     * @return all the RegItemproposeds
     * @throws Exception
     */
    @Override
    public int countAll(List<RegItemclass> regItemcalsses) throws Exception {

        //Preparing query
        Query q = this.em.createQuery(SQLConstants.SQL_GET_REGITEMPROPOSED_BY_REGITEMCLASSES_COUNT);
        q.setParameter(SQLConstants.SQL_PARAMETERS_REGITEMCLASSES, regItemcalsses);

        return ((Number) q.getSingleResult()).intValue();
    }

    /**
     * Returns all the newly proposed RegItemproposeds by a List of RegItemclass
     * paged
     *
     * @param regItemcalsses
     * @return all the RegItemproposeds
     * @throws Exception
     */
    @Override
    public int countAllNew(List<RegItemclass> regItemcalsses) throws Exception {

        //Preparing query
        Query q = this.em.createQuery(SQLConstants.SQL_GET_REGITEMPROPOSED_NEW_BY_REGITEMCLASSES_COUNT);
        q.setParameter(SQLConstants.SQL_PARAMETERS_REGITEMCLASSES, regItemcalsses);

        return ((Number) q.getSingleResult()).intValue();
    }

    /**
     * Returns all the newly proposed RegItemproposeds by a List of RegItemclass
     * paged
     *
     * @param regItemcalsses
     * @param collection
     * @param regRelationpredicateCollection
     * @return all the RegItemproposeds
     * @throws Exception
     */
    @Override
    public int countAllNew(List<RegItemclass> regItemcalsses, RegItem collection, RegRelationpredicate regRelationpredicateCollection) throws Exception {

        //Preparing query
        Query q = this.em.createQuery(SQLConstants.SQL_GET_REGITEMPROPOSED_NEW_BY_REGITEMCLASSES_COLLECTION_COUNT);
        q.setParameter(SQLConstants.SQL_PARAMETERS_REGITEMCLASSES, regItemcalsses);
        q.setParameter(SQLConstants.SQL_PARAMETERS_REGITEMOBJECT, collection);
        q.setParameter(SQLConstants.SQL_PARAMETERS_REGRELATIONPREDICATE, regRelationpredicateCollection);

        return ((Number) q.getSingleResult()).intValue();
    }

    /**
     * Returns all the RegItemproposed subject of a relation passed by parameter
     * with object the RegItemproposed passed by parameter
     *
     * @param regRelationpredicate
     * @param regItemObject
     * @param start
     * @param length
     * @return all the RegItemproposeds
     * @throws Exception
     */
    @Override
    public List<RegItemproposed> getAll(RegRelationpredicate regRelationpredicate, RegItemproposed regItemObject, int start, int length) throws Exception {

        //Preparing query
        Query q = this.em.createQuery(SQLConstants.SQL_GET_REGITEMPROPOSED_BY_RELATION_AND_ITEMOBJECT);
        q.setParameter(SQLConstants.SQL_PARAMETERS_REGRELATIONPREDICATE, regRelationpredicate);
        q.setParameter(SQLConstants.SQL_PARAMETERS_REGITEMOBJECT, regItemObject);
        q.setMaxResults(length);
        q.setFirstResult(start);

        return (List<RegItemproposed>) q.getResultList();
    }

    /**
     * Counts all the RegItemproposed subject of a relation passed by parameter
     * with object the RegItemproposed passed by parameter
     *
     * @param regRelationpredicate
     * @param regItemObject
     * @return all the RegItemproposeds
     * @throws Exception
     */
    @Override
    public int countAll(RegRelationpredicate regRelationpredicate, RegItemproposed regItemObject) throws Exception {

        //Preparing query
        Query q = this.em.createQuery(SQLConstants.SQL_GET_REGITEMPROPOSED_BY_RELATION_AND_ITEMOBJECT_COUNT);
        q.setParameter(SQLConstants.SQL_PARAMETERS_REGRELATIONPREDICATE, regRelationpredicate);
        q.setParameter(SQLConstants.SQL_PARAMETERS_REGITEMOBJECT, regItemObject);
        return ((Number) q.getSingleResult()).intValue();
    }

    /**
     * Returns the RegItemproposed object
     *
     * @param regItemReference
     * @return RegItemproposed object with the uuid passed by parameter
     * @throws java.lang.Exception
     */
    @Override
    public RegItemproposed getByRegItemReference(RegItem regItemReference) throws Exception {

        //Checking parameters
        if (regItemReference == null) {
            throw new Exception(MessageFormat.format(ErrorConstants.ERROR_MANAGER_PATTERN_NULL, "uuid"));
        }

        //Preparing query
        Query q = this.em.createQuery(SQLConstants.SQL_GET_REGITEMPROPOSED_BY_REGITEMREFERENCE);
        q.setParameter("regItemReference", regItemReference);
        return (RegItemproposed) q.getSingleResult();

    }

    /**
     * Returns all the RegItemproposeds by RegUser
     *
     * @param regUser
     * @return all the RegItemproposeds
     * @throws Exception
     */
    @Override
    public List<RegItemproposed> getAll(RegUser regUser) throws Exception {

        //Preparing query
        Query q = this.em.createQuery(SQLConstants.SQL_GET_REGITEMPROPOSED_BY_REGUSER);
        q.setParameter(SQLConstants.SQL_PARAMETERS_REGUSER, regUser);
        return (List<RegItemproposed>) q.getResultList();
    }

    /**
     * Returns all the RegItemproposed with the associated user by parameter
     *
     * @param regUser
     * @param start
     * @param length
     * @return all the RegItemproposeds
     * @throws Exception
     */
    @Override
    public List<RegItemproposed> getAll(RegUser regUser, int start, int length) throws Exception {

        //Preparing query
        Query q = this.em.createQuery(SQLConstants.SQL_GET_REGITEMPROPOSED_BY_REGUSER);
        q.setParameter(SQLConstants.SQL_PARAMETERS_REGUSER, regUser);

        q.setMaxResults(length);
        q.setFirstResult(start);

        return (List<RegItemproposed>) q.getResultList();
    }

    /**
     * Returns all the RegItemproposeds by User paged
     *
     * @param regUser
     * @return all the RegItemproposeds
     * @throws Exception
     */
    @Override
    public int countAll(RegUser regUser) throws Exception {

        //Preparing query
        Query q = this.em.createQuery(SQLConstants.SQL_GET_REGITEMPROPOSED_BY_REGUSER_COUNT);
        q.setParameter(SQLConstants.SQL_PARAMETERS_REGUSER, regUser);

        return ((Number) q.getSingleResult()).intValue();
    }

    /**
     * Returns all the RegItemproposeds by RegAction
     *
     * @param regAction
     * @return all the RegItemproposeds
     * @throws Exception
     */
    @Override
    public List<RegItemproposed> getAll(RegAction regAction) throws Exception {

        //Preparing query
        Query q = this.em.createQuery(SQLConstants.SQL_GET_REGITEMPROPOSED_BY_REGACTION);
        q.setParameter("regAction", regAction);
        return (List<RegItemproposed>) q.getResultList();
    }
}
