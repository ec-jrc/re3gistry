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
import eu.europa.ec.re3gistry2.crudinterface.IRegStatusManager;
import eu.europa.ec.re3gistry2.model.RegStatus;
import eu.europa.ec.re3gistry2.model.RegStatusgroup;
import java.text.MessageFormat;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;

public class RegStatusManager implements IRegStatusManager {

    private EntityManager em;

    public RegStatusManager(EntityManager em) {
        this.em = em;
    }

    /**
     * Returns the RegStatus object
     *
     * @param uuid The uuid of the RegStatus
     * @return RegStatus object with the uuid passed by parameter
     * @throws java.lang.Exception
     */
    @Override
    public RegStatus get(String uuid) throws Exception {
        //Checking parameters
        if (uuid == null) {
            throw new Exception(MessageFormat.format(ErrorConstants.ERROR_MANAGER_PATTERN_NULL, "uuid"));
        }

        //Preparing query
        Query q = this.em.createNamedQuery("RegStatus.findByUuid");
        q.setParameter("uuid", uuid);
        return (RegStatus) q.getSingleResult();
    }

    /**
     * Returns the RegStatus object
     *
     * @param localid The localid of the RegStatus
     * @return RegStatus object with the localid passed by parameter
     * @throws java.lang.Exception
     */
    @Override
    public RegStatus findByLocalid(String localid) throws Exception {
        //Checking parameters
        if (localid == null) {
            throw new Exception(MessageFormat.format(ErrorConstants.ERROR_MANAGER_PATTERN_NULL, SQLConstants.SQL_PARAMETERS_LOCALID));
        }

        //Preparing query
        Query q = this.em.createNamedQuery("RegStatus.findByLocalid");
        q.setParameter(SQLConstants.SQL_PARAMETERS_LOCALID, localid);
        return (RegStatus) q.getSingleResult();

    }

    /**
     * Returns all the RegStatus
     *
     * @return all the RegStatus
     * @throws Exception
     */
    @Override
    public List<RegStatus> getAll() throws Exception {

        //Preparing query
        Query q = this.em.createNamedQuery("RegStatus.findAll");
        return (List<RegStatus>) q.getResultList();
    }

    /**
     * Adds a RegStatus to the database. Returns true if the operation succeed.
     *
     * @param regStatus The RegStatus object to be added
     * @return True if the operation was successfully completed; otherwise it
     * returns false.
     * @throws Exception
     */
    @Override
    public boolean add(RegStatus regStatus) throws Exception {
        //Checking parameters
        if (regStatus == null) {
            throw new Exception(MessageFormat.format(ErrorConstants.ERROR_MANAGER_PATTERN_NULL, RegStatus.class));
        }

        //Checking the DB managers
        if (this.em == null) {
            throw new Exception(ErrorConstants.ERROR_MANAGER_PERSISTENCE_LAYER_NULL);
        }

        //Saving the object
        this.em.persist(regStatus);

        return true;
    }

    /**
     * Update the RegStatus specified by parameter. Returns true if the
     * operation succeed.
     *
     * @param regStatus the updated RegStatus (this method update the RegStatus
     * on the db with the RegStatus passed by parameter)
     * @return True if the operation was successfully completed; otherwise it
     * returns false.
     * @throws java.lang.Exception
     */
    @Override
    public boolean update(RegStatus regStatus) throws Exception {
        //Checking parameters
        if (regStatus == null) {
            throw new Exception(MessageFormat.format(ErrorConstants.ERROR_MANAGER_PATTERN_NULL, RegStatus.class));
        }

        //Checking the DB managers
        if (this.em == null) {
            throw new Exception(ErrorConstants.ERROR_MANAGER_PERSISTENCE_LAYER_NULL);
        }

        //Updating the object
        em.merge(regStatus);

        return true;
    }
    
    
    /**
     * Returns all the RegStatus by RegStatusgroup
     *
     * @param regStatusgroup
     * @return all the RegStatus
     * @throws Exception
     */
    @Override
    public List<RegStatus> getAll(RegStatusgroup regStatusgroup) throws Exception {

        //Preparing query              
        Query q = this.em.createQuery(SQLConstants.SQL_GET_REGSTATUS_BY_REGSTATUSGROUP);
        q.setParameter(SQLConstants.SQL_PARAMETERS_REGSTATUSGROUP, regStatusgroup);
                
        return (List<RegStatus>) q.getResultList();
    }
    /**
     * Returns all the RegStatus by RegStatusgroup
     *
     * @param regStatusgroup
     * @return all the RegStatus
     * @throws Exception
     */
    @Override
    public List<RegStatus> getAllPublic(RegStatusgroup regStatusgroup) throws Exception {

        //Preparing query              
        Query q = this.em.createQuery(SQLConstants.SQL_GET_REGSTATUSPUBIC_BY_REGSTATUSGROUP);
        q.setParameter(SQLConstants.SQL_PARAMETERS_REGSTATUSGROUP, regStatusgroup);
                
        return (List<RegStatus>) q.getResultList();
    }

}
