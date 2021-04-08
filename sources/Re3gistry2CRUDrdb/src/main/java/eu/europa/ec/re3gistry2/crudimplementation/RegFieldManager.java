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
import eu.europa.ec.re3gistry2.crudinterface.IRegFieldManager;
import eu.europa.ec.re3gistry2.model.RegField;
import java.text.MessageFormat;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;

public class RegFieldManager implements IRegFieldManager {

    private EntityManager em;

    public RegFieldManager(EntityManager em) {
        this.em = em;
    }

    /**
     * Returns the RegField object
     *
     * @param uuid The uuid of the RegField
     * @return RegField object with the uuid passed by parameter
     * @throws java.lang.Exception
     */
    @Override
    public RegField get(String uuid) throws Exception {

        //Checking parameters
        if (uuid == null) {
            throw new Exception(MessageFormat.format(ErrorConstants.ERROR_MANAGER_PATTERN_NULL, "uuid"));
        }

        //Preparing query
        Query q = this.em.createNamedQuery("RegField.findByUuid");
        q.setParameter("uuid", uuid);
        return (RegField) q.getSingleResult();

    }

    /**
     * Returns all the RegField
     *
     * @return all the RegField
     * @throws Exception
     */
    @Override
    public List<RegField> getAll() throws Exception {

        //Preparing query
        Query q = this.em.createNamedQuery("RegField.findAll");
        return (List<RegField>) q.getResultList();
    }

    /**
     * Adds a RegField to the database. Returns true if the operation succeed.
     *
     * @param regField The RegField object to be added
     * @return True if the operation was successfully completed; otherwise it
     * returns false.
     * @throws Exception
     */
    @Override
    public boolean add(RegField regField) throws Exception {
        //Checking parameters
        if (regField == null) {
            throw new Exception(MessageFormat.format(ErrorConstants.ERROR_MANAGER_PATTERN_NULL, RegField.class));
        }

        //Checking the DB managers
        if (this.em == null) {
            throw new Exception(ErrorConstants.ERROR_MANAGER_PERSISTENCE_LAYER_NULL);
        }

        //Saving the object
        this.em.persist(regField);

        return true;
    }

    /**
     * Update the RegField specified by parameter. Returns true if the operation
     * succeed.
     *
     * @param regField the updated RegField (this method update the RegField on
     * the db with the RegField passed by parameter)
     * @return True if the operation was successfully completed; otherwise it
     * returns false.
     * @throws java.lang.Exception
     */
    @Override
    public boolean update(RegField regField) throws Exception {
        //Checking parameters
        if (regField == null) {
            throw new Exception(MessageFormat.format(ErrorConstants.ERROR_MANAGER_PATTERN_NULL, RegField.class));
        }

        //Checking the DB managers
        if (this.em == null) {
            throw new Exception(ErrorConstants.ERROR_MANAGER_PERSISTENCE_LAYER_NULL);
        }

        //Updating the object
        em.merge(regField);

        return true;
    }

    @Override
    public RegField getByLocalid(String localid) throws Exception {
        //Checking parameters
        if (localid == null) {
            throw new Exception(MessageFormat.format(ErrorConstants.ERROR_MANAGER_PATTERN_NULL, "uuid"));
        }

        //Preparing query
        Query q = this.em.createNamedQuery("RegField.findByLocalid");
        q.setParameter(SQLConstants.SQL_PARAMETERS_LOCALID, localid);
        return (RegField) q.getSingleResult();
    }
    
    @Override
    public RegField getByLabel(String label) throws Exception {
        //Checking parameters
        if (label == null) {
            throw new Exception(MessageFormat.format(ErrorConstants.ERROR_MANAGER_PATTERN_NULL, "uuid"));
        }

        //Preparing query
        Query q = this.em.createNamedQuery("RegField.findByLabel");
        q.setParameter(SQLConstants.SQL_PARAMETERS_REGLANGUAGECODE, label);
        return (RegField) q.getSingleResult();
    }

    /**
     * Returns the RegField object that represent the title for the item (e.g.
     * "Label")
     *
     * @return RegItem object
     * @throws java.lang.Exception
     */
    @Override
    public RegField getTitleRegField() throws Exception {
        //Preparing query
        Query q = this.em.createQuery(SQLConstants.SQL_GET_REGFIELD_TITLE);
        return (RegField) q.getSingleResult();
    }
}
