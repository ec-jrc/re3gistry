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
import eu.europa.ec.re3gistry2.crudinterface.IRegItemRegGroupRegRoleMappingManager;
import eu.europa.ec.re3gistry2.model.RegGroup;
import eu.europa.ec.re3gistry2.model.RegItem;
import eu.europa.ec.re3gistry2.model.RegItemRegGroupRegRoleMapping;
import eu.europa.ec.re3gistry2.model.RegRole;
import java.text.MessageFormat;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;

public class RegItemRegGroupRegRoleMappingManager implements IRegItemRegGroupRegRoleMappingManager {

    private EntityManager em;

    public RegItemRegGroupRegRoleMappingManager(EntityManager em) {
        this.em = em;
    }

    /**
     * Returns the RegItemRegGroupRegRoleMapping object
     *
     * @param uuid The uuid of the RegItemRegGroupRegRoleMapping
     * @return RegItemRegGroupRegRoleMapping object with the uuid passed by
     * parameter
     * @throws java.lang.Exception
     */
    @Override
    public RegItemRegGroupRegRoleMapping get(String uuid) throws Exception {

        //Checking parameters
        if (uuid == null) {
            throw new Exception(MessageFormat.format(ErrorConstants.ERROR_MANAGER_PATTERN_NULL, "uuid"));
        }

        //Preparing query
        Query q = this.em.createNamedQuery("RegItemRegGroupRegRoleMapping.findByUuid");
        q.setParameter("uuid", uuid);
        return (RegItemRegGroupRegRoleMapping) q.getSingleResult();

    }

    /**
     * Returns all the RegItemRegGroupRegRoleMapping
     *
     * @return all the RegItemRegGroupRegRoleMapping
     * @throws Exception
     */
    @Override
    public List<RegItemRegGroupRegRoleMapping> getAll() throws Exception {

        //Preparing query
        Query q = this.em.createNamedQuery("RegItemRegGroupRegRoleMapping.findAll");
        return (List<RegItemRegGroupRegRoleMapping>) q.getResultList();
    }

    /**
     * Adds a RegItemRegGroupRegRoleMapping to the database. Returns true if the
     * operation succeed.
     *
     * @param regItemRegGroupRegRoleMapping
     * @return True if the operation was successfully completed; otherwise it
     * returns false.
     * @throws Exception
     */
    @Override
    public boolean add(RegItemRegGroupRegRoleMapping regItemRegGroupRegRoleMapping) throws Exception {
        //Checking parameters
        if (regItemRegGroupRegRoleMapping == null) {
            throw new Exception(MessageFormat.format(ErrorConstants.ERROR_MANAGER_PATTERN_NULL, RegGroup.class));
        }

        //Checking the DB managers
        if (this.em == null) {
            throw new Exception(ErrorConstants.ERROR_MANAGER_PERSISTENCE_LAYER_NULL);
        }

        //Saving the object
        this.em.persist(regItemRegGroupRegRoleMapping);

        return true;
    }

    /**
     * Update the RegItemRegGroupRegRoleMapping specified by parameter. Returns
     * true if the operation succeed.
     *
     * @param regItemRegGroupRegRoleMapping the updated item (this method update
     * the RegItemRegGroupRegRoleMapping on the db with the
     * RegItemRegGroupRegRoleMapping passed by parameter)
     * @return True if the operation was successfully completed; otherwise it
     * returns false.
     * @throws java.lang.Exception
     */
    @Override
    public boolean update(RegItemRegGroupRegRoleMapping regItemRegGroupRegRoleMapping) throws Exception {
        //Checking parameters
        if (regItemRegGroupRegRoleMapping == null) {
            throw new Exception(MessageFormat.format(ErrorConstants.ERROR_MANAGER_PATTERN_NULL, RegItemRegGroupRegRoleMapping.class));
        }

        //Checking the DB managers
        if (this.em == null) {
            throw new Exception(ErrorConstants.ERROR_MANAGER_PERSISTENCE_LAYER_NULL);
        }

        //Updating the object
        em.merge(regItemRegGroupRegRoleMapping);

        return true;
    }

    @Override
    public boolean delete(RegItemRegGroupRegRoleMapping regItemRegGroupRegRoleMapping) throws Exception {
        //Checking parameters
        if (regItemRegGroupRegRoleMapping == null) {
            throw new Exception(MessageFormat.format(ErrorConstants.ERROR_MANAGER_PATTERN_NULL, RegItemRegGroupRegRoleMapping.class));
        }

        //Checking the DB managers
        if (this.em == null) {
            throw new Exception(ErrorConstants.ERROR_MANAGER_PERSISTENCE_LAYER_NULL);
        }

        //Updating the object
        em.remove(regItemRegGroupRegRoleMapping);

        return true;
    }

    /**
     * Returns all RegItemRegGroupRegRoleMapping by RegGroup
     *
     * @param regGroup
     * @return all the RegItemRegGroupRegRoleMapping by RegUser
     * @throws Exception
     */
    @Override
    public List<RegItemRegGroupRegRoleMapping> getAll(RegGroup regGroup) throws Exception {

        //Preparing query
        Query q = this.em.createQuery(SQLConstants.SQL_GET_REGITEMREGGROUPREGROLEMAPPING_BY_REGGROUP);
        q.setParameter("regGroup", regGroup);
        return (List<RegItemRegGroupRegRoleMapping>) q.getResultList();
    }

    /**
     * Returns all RegItemRegGroupRegRoleMapping by RegGroup
     *
     * @param regRole
     * @return all the RegItemRegGroupRegRoleMapping by RegUser
     * @throws Exception
     */
    @Override
    public List<RegItemRegGroupRegRoleMapping> getAll(RegRole regRole) throws Exception {

        //Preparing query
        Query q = this.em.createQuery(SQLConstants.SQL_GET_REGITEMREGGROUPREGROLEMAPPING_BY_REGROLE);
        q.setParameter(SQLConstants.SQL_PARAMETERS_REGROLE, regRole);
        return (List<RegItemRegGroupRegRoleMapping>) q.getResultList();
    }

    /**
     * Returns all RegItemRegGroupRegRoleMapping by RegItem, RegRole
     *
     * @param regItem
     * @param regRole
     * @return all the RegItemRegGroupRegRoleMapping by RegItem, RegRole
     * @throws Exception
     */
    @Override
    public List<RegItemRegGroupRegRoleMapping> getAll(RegItem regItem, RegRole regRole) throws Exception {

        //Preparing query
        Query q = this.em.createQuery(SQLConstants.SQL_GET_REGITEMREGGROUPREGROLEMAPPING_BY_REGITEMREGROLE);
        q.setParameter(SQLConstants.SQL_PARAMETERS_REGROLE, regRole);
        q.setParameter("regItem", regItem);
        return (List<RegItemRegGroupRegRoleMapping>) q.getResultList();
    }

    /**
     * Returns all RegItemRegGroupRegRoleMapping by RegItem
     *
     * @param regItem
     * @return all the RegItemRegGroupRegRoleMapping by RegItem
     * @throws Exception
     */
    @Override
    public List<RegItemRegGroupRegRoleMapping> getAll(RegItem regItem) throws Exception {

        //Preparing query
        Query q = this.em.createQuery(SQLConstants.SQL_GET_REGITEMREGGROUPREGROLEMAPPING_BY_REGITEM);
        q.setParameter("regItem", regItem);
        return (List<RegItemRegGroupRegRoleMapping>) q.getResultList();
    }

}
