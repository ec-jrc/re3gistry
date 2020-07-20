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
import eu.europa.ec.re3gistry2.crudinterface.IRegLanguagecodeManager;
import eu.europa.ec.re3gistry2.model.RegLanguagecode;
import java.text.MessageFormat;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;

public class RegLanguagecodeManager implements IRegLanguagecodeManager {

    private EntityManager em;

    public RegLanguagecodeManager(EntityManager em) {
        this.em = em;
    }

    /**
     * Returns the RegLanguagecode object
     *
     * @param uuid The uuid of the RegLanguagecode
     * @return RegLanguagecode object with the uuid passed by parameter
     * @throws java.lang.Exception
     */
    @Override
    public RegLanguagecode get(String uuid) throws Exception {

        //Checking parameters
        if (uuid == null) {
            throw new Exception(MessageFormat.format(ErrorConstants.ERROR_MANAGER_PATTERN_NULL, "uuid"));
        }

        //Preparing query
        Query q = this.em.createNamedQuery("RegLanguagecode.findByUuid");
        q.setParameter("uuid", uuid);
        return (RegLanguagecode) q.getSingleResult();
    }

    @Override
    public RegLanguagecode getByIso6391code(String iso6391code) throws Exception {
        if (iso6391code == null) {
            throw new Exception(MessageFormat.format(ErrorConstants.ERROR_MANAGER_PATTERN_NULL, "iso6391code"));
        }

        //Preparing query
        Query q = this.em.createNamedQuery("RegLanguagecode.findByIso6391code");
        q.setParameter("iso6391code", iso6391code);
        return (RegLanguagecode) q.getSingleResult();
    }

    /**
     * Returns all the RegLanguagecode
     *
     * @return all the RegLanguagecode
     * @throws Exception
     */
    @Override
    public List<RegLanguagecode> getAll() throws Exception {

        //Preparing query
        Query q = this.em.createNamedQuery("RegLanguagecode.findAll");
        return (List<RegLanguagecode>) q.getResultList();
    }

     /**
     * Returns all active RegLanguagecode
     *
     * @return all active RegLanguagecode
     * @throws Exception
     */
    @Override
    public List<RegLanguagecode> getAllActive() throws Exception {
         //Preparing query
        Query q = this.em.createNamedQuery("RegLanguagecode.findAllActive");
        return (List<RegLanguagecode>) q.getResultList();
    }
   
    
    /**
     * Adds a RegLanguagecode to the database. Returns true if the operation
     * succeed.
     *
     * @param regLanguagecode The RegLanguagecode object to be added
     * @return True if the operation was successfully completed; otherwise it
     * returns false.
     * @throws Exception
     */
    @Override
    public boolean add(RegLanguagecode regLanguagecode) throws Exception {
        //Checking parameters
        if (regLanguagecode == null) {
            throw new Exception(MessageFormat.format(ErrorConstants.ERROR_MANAGER_PATTERN_NULL, RegLanguagecode.class));
        }

        //Checking the DB managers
        if (this.em == null) {
            throw new Exception(ErrorConstants.ERROR_MANAGER_PERSISTENCE_LAYER_NULL);
        }

        //Saving the object
        this.em.persist(regLanguagecode);

        return true;
    }

    /**
     * Update the RegLanguagecode specified by parameter. Returns true if the operation
     * succeed.
     *
     * @param regLanguagecode the updated RegLanguagecode (this method update the RegLanguagecode on
     * the db with the RegLanguagecode passed by parameter)
     * @return True if the operation was successfully completed; otherwise it
     * returns false.
     * @throws java.lang.Exception
     */
    @Override
    public boolean update(RegLanguagecode regLanguagecode) throws Exception {
        //Checking parameters
        if (regLanguagecode == null) {
            throw new Exception(MessageFormat.format(ErrorConstants.ERROR_MANAGER_PATTERN_NULL, RegLanguagecode.class));
        }

        //Checking the DB managers
        if (this.em == null) {
            throw new Exception(ErrorConstants.ERROR_MANAGER_PERSISTENCE_LAYER_NULL);
        }

        //Updating the object
        em.merge(regLanguagecode);

        return true;
    }

    /**
     * Returns the RegLanguagecode object set as master language
     *
     * @return RegItemgroupmapping object with the master language code
     * @throws java.lang.Exception
     */
    @Override
    public RegLanguagecode getMasterLanguage() throws Exception {
        //Preparing query
        Query q = this.em.createQuery(SQLConstants.SQL_GET_LANGUAGECODE_MASTERLANGUAGE);
        return (RegLanguagecode) q.getSingleResult();
    }
}
