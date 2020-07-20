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
import eu.europa.ec.re3gistry2.crudinterface.IRegActionManager;
import eu.europa.ec.re3gistry2.model.RegAction;
import eu.europa.ec.re3gistry2.model.RegItem;
import eu.europa.ec.re3gistry2.model.RegStatus;
import eu.europa.ec.re3gistry2.model.RegUser;
import java.text.MessageFormat;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;

public class RegActionManager implements IRegActionManager {

    private EntityManager em;

    public RegActionManager(EntityManager em) {
        this.em = em;
    }

    /**
     * Returns the RegAction object
     *
     * @param uuid The uuid of the RegAction
     * @return RegAction object with the uuid passed by parameter
     * @throws java.lang.Exception
     */
    @Override
    public RegAction get(String uuid) throws Exception {

        //Checking parameters
        if (uuid == null) {
            throw new Exception(MessageFormat.format(ErrorConstants.ERROR_MANAGER_PATTERN_NULL, "uuid"));
        }

        //Preparing query
        Query q = this.em.createNamedQuery("RegAction.findByUuid");
        q.setParameter("uuid", uuid);
        return (RegAction) q.getSingleResult();

    }

    /**
     * Returns all the RegAction
     *
     * @return all the RegAction
     * @throws Exception
     */
    @Override
    public List<RegAction> getAll() throws Exception {

        //Preparing query
        Query q = this.em.createNamedQuery("RegAction.findAll");
        return (List<RegAction>) q.getResultList();
    }

    /**
     * Adds a RegAction to the database. Returns true if the operation succeed.
     *
     * @param regAction The RegAction object to be added
     * @return True if the operation was successfully completed; otherwise it
     * returns false.
     * @throws Exception
     */
    @Override
    public boolean add(RegAction regAction) throws Exception {
        //Checking parameters
        if (regAction == null) {
            throw new Exception(MessageFormat.format(ErrorConstants.ERROR_MANAGER_PATTERN_NULL, RegAction.class));
        }

        //Checking the DB managers
        if (this.em == null) {
            throw new Exception(ErrorConstants.ERROR_MANAGER_PERSISTENCE_LAYER_NULL);
        }

        //Saving the object
        this.em.persist(regAction);

        return true;
    }

    /**
     * Update the RegAction specified by parameter. Returns true if the
     * operation succeed.
     *
     * @param regAction the updated RegAction (this method update the RegAction
     * on the db with the RegAction passed by parameter)
     * @return True if the operation was successfully completed; otherwise it
     * returns false.
     * @throws java.lang.Exception
     */
    @Override
    public boolean update(RegAction regAction) throws Exception {
        //Checking parameters
        if (regAction == null) {
            throw new Exception(MessageFormat.format(ErrorConstants.ERROR_MANAGER_PATTERN_NULL, RegAction.class));
        }

        //Checking the DB managers
        if (this.em == null) {
            throw new Exception(ErrorConstants.ERROR_MANAGER_PERSISTENCE_LAYER_NULL);
        }

        //Updating the object
        em.merge(regAction);

        return true;
    }
    
    @Override
    public boolean delete(RegAction regAction) throws Exception {
        //Checking parameters
        if (regAction == null) {
            throw new Exception(MessageFormat.format(ErrorConstants.ERROR_MANAGER_PATTERN_NULL, RegAction.class));
        }

        //Checking the DB managers
        if (this.em == null) {
            throw new Exception(ErrorConstants.ERROR_MANAGER_PERSISTENCE_LAYER_NULL);
        }

        //Updating the object
        if (!em.contains(regAction)) {
            regAction = em.merge(regAction);
        }

        em.remove(regAction);

        return true;
    }

    /**
     * Returns the RegAction object by RegUser and RegStatus
     *
     * @param regUser
     * @param regStatus
     * @return RegAction object with the uuid passed by parameter
     * @throws java.lang.Exception
     */
    @Override
    public List<RegAction> getAll(RegUser regUser, RegStatus regStatus) throws Exception {

        //Checking parameters
        if (regUser == null) {
            throw new Exception(MessageFormat.format(ErrorConstants.ERROR_MANAGER_PATTERN_NULL, RegUser.class));
        }
        if (regStatus == null) {
            throw new Exception(MessageFormat.format(ErrorConstants.ERROR_MANAGER_PATTERN_NULL, RegStatus.class));
        }

        //Preparing query
        Query q = this.em.createQuery(SQLConstants.SQL_GET_REGACTION_BY_USER_STATUS);
        q.setParameter(SQLConstants.SQL_PARAMETERS_REGUSER, regUser);
        q.setParameter(SQLConstants.SQL_PARAMETERS_REGSTATUS, regStatus);
        return (List<RegAction>) q.getResultList();

    }

    /**
     * Returns the RegAction object by RegUser and RegStatus with no comments
     *
     * @param regUser
     * @param regItemRegister
     * @param regItemRegistry
     * @param regStatus
     * @return RegAction object with the uuid passed by parameter
     * @throws java.lang.Exception
     */
    @Override
    public List<RegAction> getAllWithNoComments(RegUser regUser, RegItem regItemRegister, RegItem regItemRegistry, RegStatus regStatus) throws Exception {

        //Checking parameters
        if (regUser == null) {
            throw new Exception(MessageFormat.format(ErrorConstants.ERROR_MANAGER_PATTERN_NULL, RegUser.class));
        }
        if (regStatus == null) {
            throw new Exception(MessageFormat.format(ErrorConstants.ERROR_MANAGER_PATTERN_NULL, RegStatus.class));
        }

        //Preparing query
        Query q = this.em.createQuery(SQLConstants.SQL_GET_REGACTION_BY_USER_STATUS_NO_COMMENTS);
        q.setParameter(SQLConstants.SQL_PARAMETERS_REGUSER, regUser);
        q.setParameter(SQLConstants.SQL_PARAMETERS_REGSTATUS, regStatus);
        q.setParameter("regItemRegister", regItemRegister);
        q.setParameter("regItemRegistry", regItemRegistry);
        return (List<RegAction>) q.getResultList();

    }

    /**
     * Returns the RegAction object by RegUser
     *
     * @param regUser
     * @return RegAction object with the uuid passed by parameter
     * @throws java.lang.Exception
     */
    @Override
    public List<RegAction> getAll(RegUser regUser) throws Exception {

        //Checking parameters
        if (regUser == null) {
            throw new Exception(MessageFormat.format(ErrorConstants.ERROR_MANAGER_PATTERN_NULL, RegUser.class));
        }

        //Preparing query
        Query q = this.em.createQuery(SQLConstants.SQL_GET_REGACTION_BY_USER);
        q.setParameter(SQLConstants.SQL_PARAMETERS_REGUSER, regUser);
        return (List<RegAction>) q.getResultList();

    }
    
        /**
     * Returns the RegAction object by RegUser
     *
     * @param regUser
     * @param start
     * @param length
     * @return RegAction object with the uuid passed by parameter
     * @throws java.lang.Exception
     */
    @Override
    public List<RegAction> getAll(RegUser regUser, int start, int length) throws Exception {

        //Checking parameters
        if (regUser == null) {
            throw new Exception(MessageFormat.format(ErrorConstants.ERROR_MANAGER_PATTERN_NULL, RegUser.class));
        }

        //Preparing query
        Query q = this.em.createQuery(SQLConstants.SQL_GET_REGACTION_BY_USER);
        q.setParameter(SQLConstants.SQL_PARAMETERS_REGUSER, regUser);
        q.setMaxResults(length);
        q.setFirstResult(start);
        return (List<RegAction>) q.getResultList();

    }

    /**
     * Returns the RegAction object by RegItem register
     *
     * @param regIremRegister
     * @return RegAction object with the uuid passed by parameter
     * @throws java.lang.Exception
     */
    @Override
    public List<RegAction> getAllByRegister(RegItem regIremRegister) throws Exception {

        //Checking parameters
        if (regIremRegister == null) {
            throw new Exception(MessageFormat.format(ErrorConstants.ERROR_MANAGER_PATTERN_NULL, RegItem.class));
        }

        //Preparing query
        Query q = this.em.createQuery(SQLConstants.SQL_GET_REGACTION_BY_REGISTER);
        q.setParameter("regItemRegister", regIremRegister);
        return (List<RegAction>) q.getResultList();

    }

    /**
     * Returns the RegAction object by RegItem registry
     *
     * @param regIremRegistry
     * @return RegAction object with the uuid passed by parameter
     * @throws java.lang.Exception
     */
    @Override
    public List<RegAction> getAllByRegistry(RegItem regIremRegistry) throws Exception {

        //Checking parameters
        if (regIremRegistry == null) {
            throw new Exception(MessageFormat.format(ErrorConstants.ERROR_MANAGER_PATTERN_NULL, RegItem.class));
        }

        //Preparing query
        Query q = this.em.createQuery(SQLConstants.SQL_GET_REGACTION_BY_REGISTRY);
        q.setParameter("regItemRegistry", regIremRegistry);
        return (List<RegAction>) q.getResultList();

    }

}
