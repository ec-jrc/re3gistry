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
import eu.europa.ec.re3gistry2.crudinterface.IRegStatuslocalizationManager;
import eu.europa.ec.re3gistry2.model.RegLanguagecode;
import eu.europa.ec.re3gistry2.model.RegStatus;
import eu.europa.ec.re3gistry2.model.RegStatusgroup;
import eu.europa.ec.re3gistry2.model.RegStatuslocalization;
import java.text.MessageFormat;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;

public class RegStatuslocalizationManager implements IRegStatuslocalizationManager {

    private EntityManager em;

    public RegStatuslocalizationManager(EntityManager em) {
        this.em = em;
    }

    /**
     * Returns the RegStatuslocalization object
     *
     * @param uuid The uuid of the RegStatuslocalization
     * @return RegStatuslocalization object with the uuid passed by parameter
     * @throws java.lang.Exception
     */
    @Override
    public RegStatuslocalization get(String uuid) throws Exception {

        //Checking parameters
        if (uuid == null) {
            throw new Exception(MessageFormat.format(ErrorConstants.ERROR_MANAGER_PATTERN_NULL, "uuid"));
        }

        //Preparing query
        Query q = this.em.createNamedQuery("RegStatuslocalization.findByUuid");
        q.setParameter("uuid", uuid);
        return (RegStatuslocalization) q.getSingleResult();

    }

    /**
     * Returns all the RegStatuslocalization
     *
     * @return all the RegStatuslocalization
     * @throws Exception
     */
    @Override
    public List<RegStatuslocalization> getAll() throws Exception {

        //Preparing query
        Query q = this.em.createNamedQuery("RegStatuslocalization.findAll");
        return (List<RegStatuslocalization>) q.getResultList();
    }

    /**
     * Adds a RegStatuslocalization to the database. Returns true if the
     * operation succeed.
     *
     * @param regStatuslocalization The RegStatuslocalization object to be added
     * @return True if the operation was successfully completed; otherwise it
     * returns false.
     * @throws Exception
     */
    @Override
    public boolean add(RegStatuslocalization regStatuslocalization) throws Exception {
        //Checking parameters
        if (regStatuslocalization == null) {
            throw new Exception(MessageFormat.format(ErrorConstants.ERROR_MANAGER_PATTERN_NULL, RegStatuslocalization.class));
        }

        //Checking the DB managers
        if (this.em == null) {
            throw new Exception(ErrorConstants.ERROR_MANAGER_PERSISTENCE_LAYER_NULL);
        }

        //Saving the object
        this.em.persist(regStatuslocalization);

        return true;
    }

    /**
     * Update the RegStatuslocalization specified by parameter. Returns true if
     * the operation succeed.
     *
     * @param regStatuslocalization the updated RegStatuslocalization (this
     * method update the RegStatuslocalization on the db with the
     * RegStatuslocalization passed by parameter)
     * @return True if the operation was successfully completed; otherwise it
     * returns false.
     * @throws java.lang.Exception
     */
    @Override
    public boolean update(RegStatuslocalization regStatuslocalization) throws Exception {
        //Checking parameters
        if (regStatuslocalization == null) {
            throw new Exception(MessageFormat.format(ErrorConstants.ERROR_MANAGER_PATTERN_NULL, RegStatuslocalization.class));
        }

        //Checking the DB managers
        if (this.em == null) {
            throw new Exception(ErrorConstants.ERROR_MANAGER_PERSISTENCE_LAYER_NULL);
        }

        //Updating the object
        em.merge(regStatuslocalization);

        return true;
    }

    /**
     * Returns the RegStatuslocalization object
     *
     * @param regStatus
     * @param regLanguagecode
     * @return RegStatuslocalization object with the uuid passed by parameter
     * @throws java.lang.Exception
     */
    @Override
    public RegStatuslocalization get(RegStatus regStatus, RegLanguagecode regLanguagecode) throws Exception {

        //Checking parameters
        if (regStatus == null) {
            throw new Exception(MessageFormat.format(ErrorConstants.ERROR_MANAGER_PATTERN_NULL, RegStatus.class));
        }
        if (regLanguagecode == null) {
            throw new Exception(MessageFormat.format(ErrorConstants.ERROR_MANAGER_PATTERN_NULL, RegLanguagecode.class));
        }

        //Preparing query
        Query q = this.em.createQuery(SQLConstants.SQL_GET_REGSTATUSLOCALIZATION_BY_REGSTATUS_REGLANGUAGECODE);
        q.setParameter(SQLConstants.SQL_PARAMETERS_REGSTATUS, regStatus);
        q.setParameter("regLanguagecode", regLanguagecode);

        return (RegStatuslocalization) q.getSingleResult();

    }

    /**
     * Returns the RegStatuslocalization object
     *
     * @param regStatusgroup
     * @param regLanguagecode
     * @return RegStatuslocalization object with the uuid passed by parameter
     * @throws java.lang.Exception
     */
    @Override
    public RegStatuslocalization get(RegStatusgroup regStatusgroup, RegLanguagecode regLanguagecode) throws Exception {

        //Checking parameters
        if (regStatusgroup == null) {
            throw new Exception(MessageFormat.format(ErrorConstants.ERROR_MANAGER_PATTERN_NULL, RegStatusgroup.class));
        }
        if (regLanguagecode == null) {
            throw new Exception(MessageFormat.format(ErrorConstants.ERROR_MANAGER_PATTERN_NULL, RegLanguagecode.class));
        }

        //Preparing query
        Query q = this.em.createQuery(SQLConstants.SQL_GET_REGSTATUSLOCALIZATION_BY_REGSTATUSGROUP_REGLANGUAGECODE);
        q.setParameter("regStatusgroup", regStatusgroup);
        q.setParameter("regLanguagecode", regLanguagecode);

        return (RegStatuslocalization) q.getSingleResult();

    }
}
