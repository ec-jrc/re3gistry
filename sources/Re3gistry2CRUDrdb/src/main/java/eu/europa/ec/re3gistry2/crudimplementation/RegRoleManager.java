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
import eu.europa.ec.re3gistry2.crudinterface.IRegRoleManager;
import eu.europa.ec.re3gistry2.model.RegRole;
import java.text.MessageFormat;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

public class RegRoleManager implements IRegRoleManager {

    private EntityManager em;

    public RegRoleManager(EntityManager em) {
        this.em = em;
    }

    /**
     * Returns the RegRole object
     *
     * @param uuid The uuid of the RegRole
     * @return RegRole object with the uuid passed by parameter
     * @throws java.lang.Exception
     */
    @Override
    public RegRole get(String uuid) throws Exception {

        //Checking parameters
        if (uuid == null) {
            throw new Exception(MessageFormat.format(ErrorConstants.ERROR_MANAGER_PATTERN_NULL, "uuid"));
        }

        //Preparing query
        Query q = this.em.createNamedQuery("RegRole.findByUuid");
        q.setParameter("uuid", uuid);
        return (RegRole) q.getSingleResult();

    }

    /**
     * Returns the RegRole object
     *
     * @param name The name of the RegRole
     * @return RegRole object with the name passed by parameter
     * @throws java.lang.Exception
     */
    @Override
    public RegRole getByName(String name) throws Exception {
        //Checking parameters
        if (name == null) {
            throw new Exception(MessageFormat.format(ErrorConstants.ERROR_MANAGER_PATTERN_NULL, "name"));
        }

        //Preparing query
        Query q = this.em.createNamedQuery("RegRole.findByName");
        q.setParameter("name", name);
        return (RegRole) q.getSingleResult();
    }

    /**
     * Returns all the RegRole
     *
     * @return all the RegRole
     * @throws Exception
     */
    @Override
    public List<RegRole> getAll() throws Exception {

        //Preparing query
        Query q = this.em.createNamedQuery("RegRole.findAll");
        return (List<RegRole>) q.getResultList();
    }

    /**
     * Adds a RegRole to the database. Returns true if the operation succeed.
     *
     * @param regRole The RegRole object to be added
     * @return True if the operation was successfully completed; otherwise it
     * returns false.
     * @throws Exception
     */
    @Override
    public boolean add(RegRole regRole) throws Exception {
        //Checking parameters
        if (regRole == null) {
            throw new Exception(MessageFormat.format(ErrorConstants.ERROR_MANAGER_PATTERN_NULL, RegRole.class));
        }

        //Checking the DB managers
        if (this.em == null) {
            throw new Exception(ErrorConstants.ERROR_MANAGER_PERSISTENCE_LAYER_NULL);
        }

        //Saving the object
        this.em.persist(regRole);

        return true;
    }

    /**
     * Update the RegRole specified by parameter. Returns true if the operation
     * succeed.
     *
     * @param regRole the updated RegRole (this method update the RegRole on the
     * db with the RegRole passed by parameter)
     * @return True if the operation was successfully completed; otherwise it
     * returns false.
     * @throws java.lang.Exception
     */
    @Override
    public boolean update(RegRole regRole) throws Exception {
        //Checking parameters
        if (regRole == null) {
            throw new Exception(MessageFormat.format(ErrorConstants.ERROR_MANAGER_PATTERN_NULL, RegRole.class));
        }

        //Checking the DB managers
        if (this.em == null) {
            throw new Exception(ErrorConstants.ERROR_MANAGER_PERSISTENCE_LAYER_NULL);
        }

        //Updating the object
        em.merge(regRole);

        return true;
    }

    /**
     * Returns the RegRole object
     *
     * @param localId
     * @return RegRole object with the name passed by parameter
     * @throws java.lang.Exception
     */
    @Override
    public RegRole getByLocalId(String localId) throws Exception {
        //Checking parameters
        if (localId == null) {
            throw new Exception(MessageFormat.format(ErrorConstants.ERROR_MANAGER_PATTERN_NULL, SQLConstants.SQL_PARAMETERS_LOCALID));
        }

        //Preparing query
        Query q = this.em.createNamedQuery("RegRole.findByLocalid");
        q.setParameter(SQLConstants.SQL_PARAMETERS_LOCALID, localId);
        try {
            return (RegRole) q.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }

    }

}
