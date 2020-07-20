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
import eu.europa.ec.re3gistry2.crudinterface.IRegItemhistoryRegGroupRegRoleMappingManager;
import eu.europa.ec.re3gistry2.model.RegGroup;
import eu.europa.ec.re3gistry2.model.RegItemRegGroupRegRoleMapping;
import eu.europa.ec.re3gistry2.model.RegItemhistory;
import eu.europa.ec.re3gistry2.model.RegItemhistoryRegGroupRegRoleMapping;
import eu.europa.ec.re3gistry2.model.RegRole;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.Query;

public class RegItemhistoryRegGroupRegRoleMappingManager implements IRegItemhistoryRegGroupRegRoleMappingManager {

    private EntityManager em;

    public RegItemhistoryRegGroupRegRoleMappingManager(EntityManager em) {
        this.em = em;
    }

    /**
     * Returns the RegItemhistoryRegGroupRegRoleMapping object
     *
     * @param uuid The uuid of the RegItemhistoryRegGroupRegRoleMapping
     * @return RegItemhistoryRegGroupRegRoleMapping object with the uuid passed
     * by parameter
     * @throws java.lang.Exception
     */
    @Override
    public RegItemhistoryRegGroupRegRoleMapping get(String uuid) throws Exception {

        //Checking parameters
        if (uuid == null) {
            throw new Exception(MessageFormat.format(ErrorConstants.ERROR_MANAGER_PATTERN_NULL, "uuid"));
        }

        //Preparing query
        Query q = this.em.createNamedQuery("RegItemhistoryRegGroupRegRoleMapping.findByUuid");
        q.setParameter("uuid", uuid);
        return (RegItemhistoryRegGroupRegRoleMapping) q.getSingleResult();

    }

    /**
     * Returns all the RegItemhistoryRegGroupRegRoleMapping
     *
     * @return all the RegItemhistoryRegGroupRegRoleMapping
     * @throws Exception
     */
    @Override
    public List<RegItemhistoryRegGroupRegRoleMapping> getAll() throws Exception {

        //Preparing query
        Query q = this.em.createNamedQuery("RegItemhistoryRegGroupRegRoleMapping.findAll");
        return (List<RegItemhistoryRegGroupRegRoleMapping>) q.getResultList();
    }

    /**
     * Adds a RegItemhistoryRegGroupRegRoleMapping to the database. Returns
     * true if the operation succeed.
     *
     * @param regItemhistoryRegGroupRegRoleMapping The
     * RegItemhistoryRegGroupRegRoleMapping object to be added
     * @return True if the operation was successfully completed; otherwise it
     * returns false.
     * @throws Exception
     */
    @Override
    public boolean add(RegItemhistoryRegGroupRegRoleMapping regItemhistoryRegGroupRegRoleMapping) throws Exception {
        //Checking parameters
        if (regItemhistoryRegGroupRegRoleMapping == null) {
            throw new Exception(MessageFormat.format(ErrorConstants.ERROR_MANAGER_PATTERN_NULL, RegItemhistoryRegGroupRegRoleMapping.class));
        }

        //Checking the DB managers
        if (this.em == null) {
            throw new Exception(ErrorConstants.ERROR_MANAGER_PERSISTENCE_LAYER_NULL);
        }

        //Saving the object
        this.em.persist(regItemhistoryRegGroupRegRoleMapping);

        return true;
    }

    /**
     * Update the RegItemhistoryRegGroupRegRoleMapping specified by parameter.
     * Returns true if the operation succeed.
     *
     * @param regItemhistoryRegGroupRegRoleMapping the updated item (this
     * method update the RegItemhistoryRegGroupRegRoleMapping on the db with
     * the RegItemhistoryRegGroupRegRoleMapping passed by parameter)
     * @return True if the operation was successfully completed; otherwise it
     * returns false.
     * @throws java.lang.Exception
     */
    @Override
    public boolean update(RegItemhistoryRegGroupRegRoleMapping regItemhistoryRegGroupRegRoleMapping) throws Exception {
        //Checking parameters
        if (regItemhistoryRegGroupRegRoleMapping == null) {
            throw new Exception(MessageFormat.format(ErrorConstants.ERROR_MANAGER_PATTERN_NULL, RegItemhistoryRegGroupRegRoleMapping.class));
        }

        //Checking the DB managers
        if (this.em == null) {
            throw new Exception(ErrorConstants.ERROR_MANAGER_PERSISTENCE_LAYER_NULL);
        }

        //Updating the object
        em.merge(regItemhistoryRegGroupRegRoleMapping);

        return true;
    }

    @Override
    public boolean delete(RegItemhistoryRegGroupRegRoleMapping regItemhistoryRegGroupRegRoleMapping) throws Exception {
        //Checking parameters
        if (regItemhistoryRegGroupRegRoleMapping == null) {
            throw new Exception(MessageFormat.format(ErrorConstants.ERROR_MANAGER_PATTERN_NULL, RegItemhistoryRegGroupRegRoleMapping.class));
        }

        //Checking the DB managers
        if (this.em == null) {
            throw new Exception(ErrorConstants.ERROR_MANAGER_PERSISTENCE_LAYER_NULL);
        }

        //Updating the object
        em.remove(regItemhistoryRegGroupRegRoleMapping);

        return true;
    }

    /**
     * Returns all RegItemhistoryRegGroupRegRoleMapping by RegGroup
     *
     * @param regGroup
     * @return all the RegItemhistoryRegGroupRegRoleMapping by RegUser
     * @throws Exception
     */
    @Override
    public List<RegItemhistoryRegGroupRegRoleMapping> getAll(RegGroup regGroup) throws Exception {

        //Preparing query
        Query q = this.em.createQuery(SQLConstants.SQL_GET_REGITEMPROPOSEDREGGROUPREGROLEMAPPING_BY_REGGROUP);
        q.setParameter("regGroup", regGroup);
        return (List<RegItemhistoryRegGroupRegRoleMapping>) q.getResultList();
    }

    /**
     * Returns all RegItemhistoryRegGroupRegRoleMapping by RegGroup
     *
     * @param regRole
     * @return all the RegItemhistoryRegGroupRegRoleMapping by RegUser
     * @throws Exception
     */
    @Override
    public List<RegItemhistoryRegGroupRegRoleMapping> getAll(RegRole regRole) throws Exception {

        //Preparing query
        Query q = this.em.createQuery(SQLConstants.SQL_GET_REGITEMPROPOSEDREGGROUPREGROLEMAPPING_BY_REGROLE);
        q.setParameter(SQLConstants.SQL_PARAMETERS_REGROLE, regRole);
        return (List<RegItemhistoryRegGroupRegRoleMapping>) q.getResultList();
    }

    /**
     * Returns all RegItemhistoryRegGroupRegRoleMapping by RegItem, RegRole
     *
     * @param regItemhistory
     * @param regRole
     * @return all the RegItemhistoryRegGroupRegRoleMapping by RegItem, RegRole
     * @throws Exception
     */
    @Override
    public List<RegItemhistoryRegGroupRegRoleMapping> getAll(RegItemhistory regItemhistory, RegRole regRole) throws Exception {

        //Preparing query
        Query q = this.em.createQuery(SQLConstants.SQL_GET_REGITEMPROPOSEDREGGROUPREGROLEMAPPING_BY_REGITEMREGROLE);
        q.setParameter(SQLConstants.SQL_PARAMETERS_REGROLE, regRole);
        q.setParameter(SQLConstants.SQL_PARAMETERS_REGITEMHISTORY, regItemhistory);
        return (List<RegItemhistoryRegGroupRegRoleMapping>) q.getResultList();
    }

    /**
     * Returns all RegItemhistoryRegGroupRegRoleMapping by RegItem
     *
     * @param regItemhistory
     * @return all the RegItemhistoryRegGroupRegRoleMapping by RegItem
     * @throws Exception
     */
    @Override
    public List<RegItemhistoryRegGroupRegRoleMapping> getAll(RegItemhistory regItemhistory) throws Exception {

        //Preparing query
        Query q = this.em.createQuery(SQLConstants.SQL_GET_REGITEMPROPOSEDREGGROUPREGROLEMAPPING_BY_REGITEM);
        q.setParameter(SQLConstants.SQL_PARAMETERS_REGITEMHISTORY, regItemhistory);
        return (List<RegItemhistoryRegGroupRegRoleMapping>) q.getResultList();
    }

    /**
     * Returns the RegItemhistoryRegGroupRegRoleMapping object by reference to
     * the RegItemRegGroupRegRoleMapping
     *
     * @param regItemRegGroupRegRoleMappingReference
     * @return RegItemhistoryRegGroupRegRoleMapping object with the uuid passed
     * by parameter
     * @throws java.lang.Exception
     */
    @Override
    public RegItemhistoryRegGroupRegRoleMapping getByReference(RegItemRegGroupRegRoleMapping regItemRegGroupRegRoleMappingReference) throws Exception {

        //Checking parameters
        if (regItemRegGroupRegRoleMappingReference == null) {
            throw new Exception(MessageFormat.format(ErrorConstants.ERROR_MANAGER_PATTERN_NULL, RegItemRegGroupRegRoleMapping.class));
        }

        //Preparing query
        Query q = this.em.createQuery(SQLConstants.SQL_GET_REGITEMPROPOSEDREGGROUPREGROLEMAPPING_BY_REFERENCE);
        q.setParameter("regItemRegGroupRegRoleMappingReference", regItemRegGroupRegRoleMappingReference);
        return (RegItemhistoryRegGroupRegRoleMapping) q.getSingleResult();

    }

    /**
     * Returns all RegItemhistoryRegGroupRegRoleMapping by RegItem, RegRole
     * that are new No regItemRegGroupRegRoleMapping reference available
     *
     * @param regItemhistory
     * @param regRole
     * @return all the RegItemhistoryRegGroupRegRoleMapping by RegItem, RegRole
     * @throws Exception
     */
    @Override
    public List<RegItemhistoryRegGroupRegRoleMapping> getAllNew(RegItemhistory regItemhistory, RegRole regRole) throws Exception {

        //Preparing query
        Query q = this.em.createQuery(SQLConstants.SQL_GET_NEW_REGITEMPROPOSEDREGGROUPREGROLEMAPPING_BY_REGITEMREGROLE);
        q.setParameter(SQLConstants.SQL_PARAMETERS_REGROLE, regRole);
        q.setParameter(SQLConstants.SQL_PARAMETERS_REGITEMHISTORY, regItemhistory);
        return (List<RegItemhistoryRegGroupRegRoleMapping>) q.getResultList();
    }

    /**
     * Returns all the RegItemhistoryRegGroupRegRoleMapping by the groups
     * specified
     *
     * @param regGroups
     * @return all the RegItemhistoryRegGroupRegRoleMapping
     * @throws Exception
     */
    @Override
    public List<RegItemhistoryRegGroupRegRoleMapping> getAll(Map<String, RegGroup> regGroups) throws Exception {
        List<RegItemhistoryRegGroupRegRoleMapping> outList = new ArrayList();

        Iterator it = regGroups.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            RegGroup tmp = (RegGroup) pair.getValue();
            Query q = this.em.createQuery(SQLConstants.SQL_GET_REGITEMPROPOSEDREGGROUPREGROLEMAPPING_BY_REGGROUP);
            q.setParameter("regGroup", tmp);
            outList.addAll(q.getResultList());
        }

        return outList;
    }
}
