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
import eu.europa.ec.re3gistry2.crudinterface.IRegRelationproposedManager;
import eu.europa.ec.re3gistry2.model.RegItemproposed;
import eu.europa.ec.re3gistry2.model.RegRelation;
import eu.europa.ec.re3gistry2.model.RegRelationproposed;
import eu.europa.ec.re3gistry2.model.RegRelationpredicate;
import java.text.MessageFormat;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;

public class RegRelationproposedManager implements IRegRelationproposedManager {

    private EntityManager em;

    public RegRelationproposedManager(EntityManager em) {
        this.em = em;
    }

    /**
     * Returns the RegRelationproposed object
     *
     * @param uuid The uuid of the RegRelationproposed
     * @return RegRelationproposed object with the uuid passed by parameter
     * @throws java.lang.Exception
     */
    @Override
    public RegRelationproposed get(String uuid) throws Exception {

        //Checking parameters
        if (uuid == null) {
            throw new Exception(MessageFormat.format(ErrorConstants.ERROR_MANAGER_PATTERN_NULL, "uuid"));
        }

        //Preparing query
        Query q = this.em.createNamedQuery("RegRelationproposed.findByUuid");
        q.setParameter("uuid", uuid);
        return (RegRelationproposed) q.getSingleResult();

    }

    /**
     * Returns all the RegRelationproposed
     *
     * @return all the RegRelationproposed
     * @throws Exception
     */
    @Override
    public List<RegRelationproposed> getAll() throws Exception {

        //Preparing query
        Query q = this.em.createNamedQuery("RegRelationproposed.findAll");
        return (List<RegRelationproposed>) q.getResultList();
    }

    /**
     * Adds a RegRelationproposed to the database. Returns true if the operation
     * succeed.
     *
     * @param regRelationproposed The RegRelationproposed object to be added
     * @return True if the operation was successfully completed; otherwise it
     * returns false.
     * @throws Exception
     */
    @Override
    public boolean add(RegRelationproposed regRelationproposed) throws Exception {
        //Checking parameters
        if (regRelationproposed == null) {
            throw new Exception(MessageFormat.format(ErrorConstants.ERROR_MANAGER_PATTERN_NULL, RegRelationproposed.class));
        }

        //Checking the DB managers
        if (this.em == null) {
            throw new Exception(ErrorConstants.ERROR_MANAGER_PERSISTENCE_LAYER_NULL);
        }

        //Saving the object
        this.em.persist(regRelationproposed);

        return true;
    }

    /**
     * Update the RegRelationproposed specified by parameter. Returns true if
     * the operation succeed.
     *
     * @param regRelationproposed the updated RegRelationproposed (this method
     * update the RegRelationproposed on the db with the RegRelationproposed
     * passed by parameter)
     * @return True if the operation was successfully completed; otherwise it
     * returns false.
     * @throws java.lang.Exception
     */
    @Override
    public boolean update(RegRelationproposed regRelationproposed) throws Exception {
        //Checking parameters
        if (regRelationproposed == null) {
            throw new Exception(MessageFormat.format(ErrorConstants.ERROR_MANAGER_PATTERN_NULL, RegRelationproposed.class));
        }

        //Checking the DB managers
        if (this.em == null) {
            throw new Exception(ErrorConstants.ERROR_MANAGER_PERSISTENCE_LAYER_NULL);
        }

        //Updating the object
        em.merge(regRelationproposed);

        return true;
    }

    /**
     *
     * @param regRelationproposed
     * @return
     * @throws Exception
     */
    @Override
    public boolean delete(RegRelationproposed regRelationproposed) throws Exception {
        //Checking parameters
        if (regRelationproposed == null) {
            throw new Exception(MessageFormat.format(ErrorConstants.ERROR_MANAGER_PATTERN_NULL, RegRelationproposed.class));
        }

        //Checking the DB managers
        if (this.em == null) {
            throw new Exception(ErrorConstants.ERROR_MANAGER_PERSISTENCE_LAYER_NULL);
        }

        //Updating the object
        em.remove(regRelationproposed);

        return true;
    }

    /**
     * Returns all the RegRelationproposed where the RegItemproposed passed as
     * parameter is the subject
     *
     * @param regItemproposed
     * @return list of RegRelationproposed
     * @throws Exception
     */
    @Override
    public List<RegRelationproposed> getAllBySubject(RegItemproposed regItemproposed) throws Exception {

        //Preparing query
        Query q = this.em.createQuery(SQLConstants.SQL_GET_RELATIONPROPOSED_BY_SUBJECT_ITEM);
        q.setParameter(SQLConstants.SQL_PARAMETERS_REGITEM, regItemproposed);
        return (List<RegRelationproposed>) q.getResultList();
    }
    
        /**
     * Returns all the RegRelationproposed where the RegItemproposed passed as
     * parameter is the object
     *
     * @param regItemproposed
     * @return list of RegRelationproposed
     * @throws Exception
     */
    @Override
    public List<RegRelationproposed> getAllByObject(RegItemproposed regItemproposed) throws Exception {

        //Preparing query
        Query q = this.em.createQuery(SQLConstants.SQL_GET_RELATIONPROPOSED_BY_OBJECT_ITEM);
        q.setParameter(SQLConstants.SQL_PARAMETERS_REGITEM, regItemproposed);
        return (List<RegRelationproposed>) q.getResultList();
    }

    /**
     * Returns all the relation by RegItemproposed and predicate subject
     *
     * @param regItemproposed
     * @param regRelationPredicate
     * @return list of RegRelationproposed
     * @throws Exception
     */
    @Override
    public List<RegRelationproposed> getAll(RegItemproposed regItemproposed, RegRelationpredicate regRelationPredicate) throws Exception {

        //Preparing query
        Query q = this.em.createQuery(SQLConstants.SQL_GET_RELATIONPROPOSED_COLLECTION_REFERENCE);
        q.setParameter(SQLConstants.SQL_PARAMETERS_REGITEM, regItemproposed);
        q.setParameter("predicate", regRelationPredicate);
        try {
            return (List<RegRelationproposed>) q.getResultList();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Returns all the RegRelationproposed by the RegRelation reference
     *
     * @param regRelationReference
     * @return list of RegRelationproposed
     * @throws Exception
     */
    @Override
    public RegRelationproposed getByRegRelationReference(RegRelation regRelationReference) throws Exception {

        //Preparing query
        Query q = this.em.createQuery(SQLConstants.SQL_GET_RELATIONPROPOSED_BY_REG_RELATION_REFERENCE);
        q.setParameter("regrelationreference", regRelationReference);
        return (RegRelationproposed) q.getSingleResult();
    }
    
    /**
     * Returns all the relation by RegItemproposed and predicate subject
     *
     * @param regItemproposed
     * @param regRelationPredicate
     * @return list of RegRelationproposed
     * @throws Exception
     */
    @Override
    public List<RegRelationproposed> getAllNew(RegItemproposed regItemproposed, RegRelationpredicate regRelationPredicate) throws Exception {

        //Preparing query
        Query q = this.em.createQuery(SQLConstants.SQL_GET_RELATIONPROPOSED_COLLECTION_REFERENCE_NEW);
        q.setParameter(SQLConstants.SQL_PARAMETERS_REGITEM, regItemproposed);
        q.setParameter("predicate", regRelationPredicate);
        try {
            return (List<RegRelationproposed>) q.getResultList();
        } catch (Exception e) {
            return null;
        }
    }

}
