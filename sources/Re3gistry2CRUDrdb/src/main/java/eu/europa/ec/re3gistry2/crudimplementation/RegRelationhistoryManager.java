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
import eu.europa.ec.re3gistry2.crudinterface.IRegRelationhistoryManager;
import eu.europa.ec.re3gistry2.model.RegItemhistory;
import eu.europa.ec.re3gistry2.model.RegRelationhistory;
import eu.europa.ec.re3gistry2.model.RegRelationpredicate;
import java.text.MessageFormat;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;

public class RegRelationhistoryManager implements IRegRelationhistoryManager {

    private EntityManager em;

    public RegRelationhistoryManager(EntityManager em) {
        this.em = em;
    }

    /**
     * Returns the RegRelationhistory object
     *
     * @param uuid The uuid of the RegRelationhistory
     * @return RegRelationhistory object with the uuid passed by parameter
     * @throws java.lang.Exception
     */
    @Override
    public RegRelationhistory get(String uuid) throws Exception {

        //Checking parameters
        if (uuid == null) {
            throw new Exception(MessageFormat.format(ErrorConstants.ERROR_MANAGER_PATTERN_NULL, "uuid"));
        }

        //Preparing query
        Query q = this.em.createNamedQuery("RegRelationhistory.findByUuid");
        q.setParameter("uuid", uuid);
        return (RegRelationhistory) q.getSingleResult();

    }

    /**
     * Returns all the RegRelationhistory
     *
     * @return all the RegRelationhistory
     * @throws Exception
     */
    @Override
    public List<RegRelationhistory> getAll() throws Exception {

        //Preparing query
        Query q = this.em.createNamedQuery("RegRelationhistory.findAll");
        return (List<RegRelationhistory>) q.getResultList();
    }

    /**
     * Adds a RegRelationhistory to the database. Returns true if the operation
     * succeed.
     *
     * @param regRelationhistory The RegRelationhistory object to be added
     * @return True if the operation was successfully completed; otherwise it
     * returns false.
     * @throws Exception
     */
    @Override
    public boolean add(RegRelationhistory regRelationhistory) throws Exception {
        //Checking parameters
        if (regRelationhistory == null) {
            throw new Exception(MessageFormat.format(ErrorConstants.ERROR_MANAGER_PATTERN_NULL, RegRelationhistory.class));
        }

        //Checking the DB managers
        if (this.em == null) {
            throw new Exception(ErrorConstants.ERROR_MANAGER_PERSISTENCE_LAYER_NULL);
        }

        //Saving the object
        this.em.persist(regRelationhistory);

        return true;
    }

    /**
     * Update the RegRelationhistory specified by parameter. Returns true if the operation
     * succeed.
     *
     * @param regRelationhistory the updated RegRelationhistory (this method update the RegRelationhistory
     * on the db with the RegRelationhistory passed by parameter)
     * @return True if the operation was successfully completed; otherwise it
     * returns false.
     * @throws java.lang.Exception
     */
    @Override
    public boolean update(RegRelationhistory regRelationhistory) throws Exception {
        //Checking parameters
        if (regRelationhistory == null) {
            throw new Exception(MessageFormat.format(ErrorConstants.ERROR_MANAGER_PATTERN_NULL, RegRelationhistory.class));
        }

        //Checking the DB managers
        if (this.em == null) {
            throw new Exception(ErrorConstants.ERROR_MANAGER_PERSISTENCE_LAYER_NULL);
        }

        //Updating the object
        em.merge(regRelationhistory);

        return true;
    }

    /**
     * Returns all the RegRelationhistory by RegItemhistory and RegRelationpredicate subject
     *
     * @param regItemhistory
     * @param regRelationPredicate
     * @return list of RegRelation
     * @throws Exception
     */
    @Override
    public List<RegRelationhistory> getAll(RegItemhistory regItemhistory, RegRelationpredicate regRelationPredicate) throws Exception {

        //Preparing query
        Query q = this.em.createQuery(SQLConstants.SQL_GET_COLLECTION_REFERENCE_HISTORY);
        q.setParameter("regitemhistory", regItemhistory);
        q.setParameter("predicate", regRelationPredicate);
        try {
            return (List<RegRelationhistory>) q.getResultList();
        } catch (Exception e) {
            return null;
        }
    }

}
