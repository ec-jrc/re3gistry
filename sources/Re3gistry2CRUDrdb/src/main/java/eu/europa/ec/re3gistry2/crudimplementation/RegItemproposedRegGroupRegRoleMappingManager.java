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
import eu.europa.ec.re3gistry2.crudinterface.IRegItemproposedRegGroupRegRoleMappingManager;
import eu.europa.ec.re3gistry2.model.RegGroup;
import eu.europa.ec.re3gistry2.model.RegItemRegGroupRegRoleMapping;
import eu.europa.ec.re3gistry2.model.RegItemproposed;
import eu.europa.ec.re3gistry2.model.RegItemproposedRegGroupRegRoleMapping;
import eu.europa.ec.re3gistry2.model.RegRole;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.Query;

public class RegItemproposedRegGroupRegRoleMappingManager implements IRegItemproposedRegGroupRegRoleMappingManager {

    private EntityManager em;

    public RegItemproposedRegGroupRegRoleMappingManager(EntityManager em) {
        this.em = em;
    }

    /**
     * Returns the RegItemproposedRegGroupRegRoleMapping object
     *
     * @param uuid The uuid of the RegItemproposedRegGroupRegRoleMapping
     * @return RegItemproposedRegGroupRegRoleMapping object with the uuid passed
     * by parameter
     * @throws java.lang.Exception
     */
    @Override
    public RegItemproposedRegGroupRegRoleMapping get(String uuid) throws Exception {

        //Checking parameters
        if (uuid == null) {
            throw new Exception(MessageFormat.format(ErrorConstants.ERROR_MANAGER_PATTERN_NULL, "uuid"));
        }

        //Preparing query
        Query q = this.em.createNamedQuery("RegItemproposedRegGroupRegRoleMapping.findByUuid");
        q.setParameter("uuid", uuid);
        return (RegItemproposedRegGroupRegRoleMapping) q.getSingleResult();

    }

    /**
     * Returns all the RegItemproposedRegGroupRegRoleMapping
     *
     * @return all the RegItemproposedRegGroupRegRoleMapping
     * @throws Exception
     */
    @Override
    public List<RegItemproposedRegGroupRegRoleMapping> getAll() throws Exception {

        //Preparing query
        Query q = this.em.createNamedQuery("RegItemproposedRegGroupRegRoleMapping.findAll");
        return (List<RegItemproposedRegGroupRegRoleMapping>) q.getResultList();
    }

    /**
     * Adds a RegItemproposedRegGroupRegRoleMapping to the database. Returns
     * true if the operation succeed.
     *
     * @param regItemproposedRegGroupRegRoleMapping The
     * RegItemproposedRegGroupRegRoleMapping object to be added
     * @return True if the operation was successfully completed; otherwise it
     * returns false.
     * @throws Exception
     */
    @Override
    public boolean add(RegItemproposedRegGroupRegRoleMapping regItemproposedRegGroupRegRoleMapping) throws Exception {
        //Checking parameters
        if (regItemproposedRegGroupRegRoleMapping == null) {
            throw new Exception(MessageFormat.format(ErrorConstants.ERROR_MANAGER_PATTERN_NULL, RegGroup.class));
        }

        //Checking the DB managers
        if (this.em == null) {
            throw new Exception(ErrorConstants.ERROR_MANAGER_PERSISTENCE_LAYER_NULL);
        }

        //Saving the object
        this.em.persist(regItemproposedRegGroupRegRoleMapping);

        return true;
    }

    /**
     * Update the RegItemproposedRegGroupRegRoleMapping specified by parameter.
     * Returns true if the operation succeed.
     *
     * @param regItemproposedRegGroupRegRoleMapping the updated item (this
     * method update the RegItemproposedRegGroupRegRoleMapping on the db with
     * the RegItemproposedRegGroupRegRoleMapping passed by parameter)
     * @return True if the operation was successfully completed; otherwise it
     * returns false.
     * @throws java.lang.Exception
     */
    @Override
    public boolean update(RegItemproposedRegGroupRegRoleMapping regItemproposedRegGroupRegRoleMapping) throws Exception {
        //Checking parameters
        if (regItemproposedRegGroupRegRoleMapping == null) {
            throw new Exception(MessageFormat.format(ErrorConstants.ERROR_MANAGER_PATTERN_NULL, RegItemproposedRegGroupRegRoleMapping.class));
        }

        //Checking the DB managers
        if (this.em == null) {
            throw new Exception(ErrorConstants.ERROR_MANAGER_PERSISTENCE_LAYER_NULL);
        }

        //Updating the object
        em.merge(regItemproposedRegGroupRegRoleMapping);

        return true;
    }

    @Override
    public boolean delete(RegItemproposedRegGroupRegRoleMapping regItemproposedRegGroupRegRoleMapping) throws Exception {
        //Checking parameters
        if (regItemproposedRegGroupRegRoleMapping == null) {
            throw new Exception(MessageFormat.format(ErrorConstants.ERROR_MANAGER_PATTERN_NULL, RegItemproposedRegGroupRegRoleMapping.class));
        }

        //Checking the DB managers
        if (this.em == null) {
            throw new Exception(ErrorConstants.ERROR_MANAGER_PERSISTENCE_LAYER_NULL);
        }

        //Updating the object
        em.remove(regItemproposedRegGroupRegRoleMapping);

        return true;
    }

    /**
     * Returns all RegItemproposedRegGroupRegRoleMapping by RegGroup
     *
     * @param regGroup
     * @return all the RegItemproposedRegGroupRegRoleMapping by RegUser
     * @throws Exception
     */
    @Override
    public List<RegItemproposedRegGroupRegRoleMapping> getAll(RegGroup regGroup) throws Exception {

        //Preparing query
        Query q = this.em.createQuery(SQLConstants.SQL_GET_REGITEMPROPOSEDREGGROUPREGROLEMAPPING_BY_REGGROUP);
        q.setParameter("regGroup", regGroup);
        return (List<RegItemproposedRegGroupRegRoleMapping>) q.getResultList();
    }

    /**
     * Returns all RegItemproposedRegGroupRegRoleMapping by RegGroup
     *
     * @param regRole
     * @return all the RegItemproposedRegGroupRegRoleMapping by RegUser
     * @throws Exception
     */
    @Override
    public List<RegItemproposedRegGroupRegRoleMapping> getAll(RegRole regRole) throws Exception {

        //Preparing query
        Query q = this.em.createQuery(SQLConstants.SQL_GET_REGITEMPROPOSEDREGGROUPREGROLEMAPPING_BY_REGROLE);
        q.setParameter(SQLConstants.SQL_PARAMETERS_REGROLE, regRole);
        return (List<RegItemproposedRegGroupRegRoleMapping>) q.getResultList();
    }

    /**
     * Returns all RegItemproposedRegGroupRegRoleMapping by RegItem, RegRole
     *
     * @param regItemproposed
     * @param regRole
     * @return all the RegItemproposedRegGroupRegRoleMapping by RegItem, RegRole
     * @throws Exception
     */
    @Override
    public List<RegItemproposedRegGroupRegRoleMapping> getAll(RegItemproposed regItemproposed, RegRole regRole) throws Exception {

        //Preparing query
        Query q = this.em.createQuery(SQLConstants.SQL_GET_REGITEMPROPOSEDREGGROUPREGROLEMAPPING_BY_REGITEMREGROLE);
        q.setParameter(SQLConstants.SQL_PARAMETERS_REGROLE, regRole);
        q.setParameter(SQLConstants.SQL_PARAMETERS_REGITEMPROPOSED, regItemproposed);
        return (List<RegItemproposedRegGroupRegRoleMapping>) q.getResultList();
    }

    /**
     * Returns all RegItemproposedRegGroupRegRoleMapping by RegItem
     *
     * @param regItemproposed
     * @return all the RegItemproposedRegGroupRegRoleMapping by RegItem
     * @throws Exception
     */
    @Override
    public List<RegItemproposedRegGroupRegRoleMapping> getAll(RegItemproposed regItemproposed) throws Exception {

        //Preparing query
        Query q = this.em.createQuery(SQLConstants.SQL_GET_REGITEMPROPOSEDREGGROUPREGROLEMAPPING_BY_REGITEM);
        q.setParameter(SQLConstants.SQL_PARAMETERS_REGITEMPROPOSED, regItemproposed);
        return (List<RegItemproposedRegGroupRegRoleMapping>) q.getResultList();
    }

    /**
     * Returns the RegItemproposedRegGroupRegRoleMapping object by reference to
     * the RegItemRegGroupRegRoleMapping
     *
     * @param regItemRegGroupRegRoleMappingReference
     * @return RegItemproposedRegGroupRegRoleMapping object with the uuid passed
     * by parameter
     * @throws java.lang.Exception
     */
    @Override
    public RegItemproposedRegGroupRegRoleMapping getByReference(RegItemRegGroupRegRoleMapping regItemRegGroupRegRoleMappingReference) throws Exception {

        //Checking parameters
        if (regItemRegGroupRegRoleMappingReference == null) {
            throw new Exception(MessageFormat.format(ErrorConstants.ERROR_MANAGER_PATTERN_NULL, RegItemRegGroupRegRoleMapping.class));
        }

        //Preparing query
        Query q = this.em.createQuery(SQLConstants.SQL_GET_REGITEMPROPOSEDREGGROUPREGROLEMAPPING_BY_REFERENCE);
        q.setParameter("regItemRegGroupRegRoleMappingReference", regItemRegGroupRegRoleMappingReference);
        return (RegItemproposedRegGroupRegRoleMapping) q.getSingleResult();

    }

    /**
     * Returns all RegItemproposedRegGroupRegRoleMapping by RegItem, RegRole
     * that are new No regItemRegGroupRegRoleMapping reference available
     *
     * @param regItemproposed
     * @param regRole
     * @return all the RegItemproposedRegGroupRegRoleMapping by RegItem, RegRole
     * @throws Exception
     */
    @Override
    public List<RegItemproposedRegGroupRegRoleMapping> getAllNew(RegItemproposed regItemproposed, RegRole regRole) throws Exception {

        //Preparing query
        Query q = this.em.createQuery(SQLConstants.SQL_GET_NEW_REGITEMPROPOSEDREGGROUPREGROLEMAPPING_BY_REGITEMREGROLE);
        q.setParameter(SQLConstants.SQL_PARAMETERS_REGROLE, regRole);
        q.setParameter(SQLConstants.SQL_PARAMETERS_REGITEMPROPOSED, regItemproposed);
        return (List<RegItemproposedRegGroupRegRoleMapping>) q.getResultList();
    }

    /**
     * Returns all the RegItemproposedRegGroupRegRoleMapping by the groups
     * specified
     *
     * @param regGroups
     * @return all the RegItemproposedRegGroupRegRoleMapping
     * @throws Exception
     */
    @Override
    public List<RegItemproposedRegGroupRegRoleMapping> getAll(Map<String, RegGroup> regGroups) throws Exception {
        List<RegItemproposedRegGroupRegRoleMapping> outList = new ArrayList();

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
