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
import eu.europa.ec.re3gistry2.crudinterface.IRegRelationpredicateManager;
import eu.europa.ec.re3gistry2.model.RegRelationpredicate;
import java.text.MessageFormat;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;

public class RegRelationpredicateManager implements IRegRelationpredicateManager {

    private EntityManager em;

    public RegRelationpredicateManager(EntityManager em) {
        this.em = em;
    }

    /**
     * Returns the RegRelationpredicate object
     *
     * @param uuid The uuid of the RegRelationpredicate
     * @return RegRelationpredicate object with the uuid passed by parameter
     * @throws java.lang.Exception
     */
    @Override
    public RegRelationpredicate get(String uuid) throws Exception {

        //Checking parameters
        if (uuid == null) {
            throw new Exception(MessageFormat.format(ErrorConstants.ERROR_MANAGER_PATTERN_NULL, "uuid"));
        }

        //Preparing query
        Query q = this.em.createNamedQuery("RegRelationpredicate.findByUuid");
        q.setParameter("uuid", uuid);
        return (RegRelationpredicate) q.getSingleResult();

    }

    /**
     * Returns all the RegRelationpredicate
     *
     * @return all the RegRelationpredicate
     * @throws Exception
     */
    @Override
    public List<RegRelationpredicate> getAll() throws Exception {

        //Preparing query
        Query q = this.em.createNamedQuery("RegRelationpredicate.findAll");
        return (List<RegRelationpredicate>) q.getResultList();
    }

    /**
     * Adds a RegRelationpredicate to the database. Returns true if the
     * operation succeed.
     *
     * @param regRelationpredicate The RegRelationpredicate object to be added
     * @return True if the operation was successfully completed; otherwise it
     * returns false.
     * @throws Exception
     */
    @Override
    public boolean add(RegRelationpredicate regRelationpredicate) throws Exception {
        //Checking parameters
        if (regRelationpredicate == null) {
            throw new Exception(MessageFormat.format(ErrorConstants.ERROR_MANAGER_PATTERN_NULL, RegRelationpredicate.class));
        }

        //Checking the DB managers
        if (this.em == null) {
            throw new Exception(ErrorConstants.ERROR_MANAGER_PERSISTENCE_LAYER_NULL);
        }

        //Saving the object
        this.em.persist(regRelationpredicate);

        return true;
    }

    /**
     * Update the RegRelationpredicate specified by parameter. Returns true if
     * the operation succeed.
     *
     * @param regRelationpredicate the updated RegRelationpredicate (this method
     * update the RegRelationpredicate on the db with the RegRelationpredicate
     * passed by parameter)
     * @return True if the operation was successfully completed; otherwise it
     * returns false.
     * @throws java.lang.Exception
     */
    @Override
    public boolean update(RegRelationpredicate regRelationpredicate) throws Exception {
        //Checking parameters
        if (regRelationpredicate == null) {
            throw new Exception(MessageFormat.format(ErrorConstants.ERROR_MANAGER_PATTERN_NULL, RegRelationpredicate.class));
        }

        //Checking the DB managers
        if (this.em == null) {
            throw new Exception(ErrorConstants.ERROR_MANAGER_PERSISTENCE_LAYER_NULL);
        }

        //Updating the object
        em.merge(regRelationpredicate);

        return true;
    }

    @Override
    public RegRelationpredicate getByLocalid(String localid) throws Exception {
        //Checking parameters
        if (localid == null) {
            throw new Exception(MessageFormat.format(ErrorConstants.ERROR_MANAGER_PATTERN_NULL, "localid"));
        }

        //Preparing query
        Query q = this.em.createNamedQuery("RegRelationpredicate.findByLocalid");
        q.setParameter("localid", localid);
        return (RegRelationpredicate) q.getSingleResult();
    }

}
