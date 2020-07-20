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
import eu.europa.ec.re3gistry2.crudinterface.IRegUserRegGroupMappingManager;
import eu.europa.ec.re3gistry2.model.RegGroup;
import eu.europa.ec.re3gistry2.model.RegUser;
import eu.europa.ec.re3gistry2.model.RegUserRegGroupMapping;
import java.text.MessageFormat;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;

public class RegUserRegGroupMappingManager implements IRegUserRegGroupMappingManager {

    private EntityManager em;

    public RegUserRegGroupMappingManager(EntityManager em) {
        this.em = em;
    }

    /**
     * Returns the RegUserRegGroupMapping object
     *
     * @param uuid The uuid of the RegUserRegGroupMapping
     * @return RegUserRegGroupMapping object with the uuid passed by parameter
     * @throws java.lang.Exception
     */
    @Override
    public RegUserRegGroupMapping get(String uuid) throws Exception {

        //Checking parameters
        if (uuid == null) {
            throw new Exception(MessageFormat.format(ErrorConstants.ERROR_MANAGER_PATTERN_NULL, "uuid"));
        }

        //Preparing query
        Query q = this.em.createNamedQuery("RegUserRegGroupMapping.findByUuid");
        q.setParameter("uuid", uuid);
        return (RegUserRegGroupMapping) q.getSingleResult();
    }

    /**
     * Returns all the RegUserRegGroupMapping
     *
     * @return all the RegUserRegGroupMapping
     * @throws Exception
     */
    @Override
    public List<RegUserRegGroupMapping> getAll() throws Exception {

        //Preparing query
        Query q = this.em.createNamedQuery("RegUserRegGroupMapping.findAll");
        return (List<RegUserRegGroupMapping>) q.getResultList();
    }

    /**
     * Adds a RegUserRegGroupMapping to the database. Returns true if the
     * operation succeed.
     *
     * @param regUserRegGroupMapping The RegUserRegGroupMapping object to be
     * added
     * @return True if the operation was successfully completed; otherwise it
     * returns false.
     * @throws Exception
     */
    @Override
    public boolean add(RegUserRegGroupMapping regUserRegGroupMapping) throws Exception {
        //Checking parameters
        if (regUserRegGroupMapping == null) {
            throw new Exception(MessageFormat.format(ErrorConstants.ERROR_MANAGER_PATTERN_NULL, RegUserRegGroupMapping.class));
        }

        //Checking the DB managers
        if (this.em == null) {
            throw new Exception(ErrorConstants.ERROR_MANAGER_PERSISTENCE_LAYER_NULL);
        }

        //Saving the object
        this.em.persist(regUserRegGroupMapping);

        return true;
    }

    /**
     * Update the RegUserRegGroupMapping specified by parameter. Returns true if
     * the operation succeed.
     *
     * @param regUserRegGroupMapping the updated item (this method update the
     * RegUserRegGroupMapping on the db with the RegUserRegGroupMapping passed
     * by parameter)
     * @return True if the operation was successfully completed; otherwise it
     * returns false.
     * @throws java.lang.Exception
     */
    @Override
    public boolean update(RegUserRegGroupMapping regUserRegGroupMapping) throws Exception {
        //Checking parameters
        if (regUserRegGroupMapping == null) {
            throw new Exception(MessageFormat.format(ErrorConstants.ERROR_MANAGER_PATTERN_NULL, RegUserRegGroupMapping.class));
        }

        //Checking the DB managers
        if (this.em == null) {
            throw new Exception(ErrorConstants.ERROR_MANAGER_PERSISTENCE_LAYER_NULL);
        }

        //Updating the object
        em.merge(regUserRegGroupMapping);

        return true;
    }
    
    @Override
    public boolean delete(RegUserRegGroupMapping regUserRegGroupMapping) throws Exception {
        //Checking parameters
        if (regUserRegGroupMapping == null) {
            throw new Exception(MessageFormat.format(ErrorConstants.ERROR_MANAGER_PATTERN_NULL, RegUserRegGroupMapping.class));
        }

        //Checking the DB managers
        if (this.em == null) {
            throw new Exception(ErrorConstants.ERROR_MANAGER_PERSISTENCE_LAYER_NULL);
        }

        //Updating the object
        if (!em.contains(regUserRegGroupMapping)) {
            regUserRegGroupMapping = em.merge(regUserRegGroupMapping);
        }

        em.remove(regUserRegGroupMapping);

        return true;
    }

    /**
     * Returns all RegUserRegGroupMapping by RegUser
     *
     * @param regUser
     * @return all the RegUserRegGroupMapping by RegUser
     * @throws Exception
     */
    @Override
    public List<RegUserRegGroupMapping> getAll(RegUser regUser) throws Exception {

        //Preparing query
        Query q = this.em.createQuery(SQLConstants.SQL_GET_REGUSERREGGROUPMAPPING_BY_REGUSER);
        q.setParameter(SQLConstants.SQL_PARAMETERS_REGUSER, regUser);
        return (List<RegUserRegGroupMapping>) q.getResultList();
    }

    /**
     * Returns all RegUserRegGroupMapping by RegUser and RegGroup
     *
     * @param regUser
     * @param regGroup
     * @return all the RegUserRegGroupMapping by RegUser
     * @throws Exception
     */
    @Override
    public RegUserRegGroupMapping get(RegUser regUser, RegGroup regGroup) throws Exception {

        //Preparing query
        Query q = this.em.createQuery(SQLConstants.SQL_GET_REGUSERREGGROUPMAPPING_BY_REGUSER_REGGROUP);
        q.setParameter(SQLConstants.SQL_PARAMETERS_REGUSER, regUser);
        q.setParameter("regGroup", regGroup);
        return (RegUserRegGroupMapping) q.getSingleResult();
    }

    /**
     * Returns all RegUserRegGroupMapping by RegGroup
     *
     * @param regGroup
     * @return all the RegUserRegGroupMapping by RegUser
     * @throws Exception
     */
    @Override
    public List<RegUserRegGroupMapping> getAll(RegGroup regGroup) throws Exception {

        //Preparing query
        Query q = this.em.createQuery(SQLConstants.SQL_GET_REGUSERREGGROUPMAPPING_BY_REGGROUP);
        q.setParameter("regGroup", regGroup);
        return (List<RegUserRegGroupMapping>) q.getResultList();
    }
}
