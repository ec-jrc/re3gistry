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
import eu.europa.ec.re3gistry2.crudinterface.IRegFieldmappingManager;
import eu.europa.ec.re3gistry2.model.RegField;
import eu.europa.ec.re3gistry2.model.RegFieldmapping;
import eu.europa.ec.re3gistry2.model.RegItemclass;
import java.text.MessageFormat;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;

public class RegFieldmappingManager implements IRegFieldmappingManager {

    private EntityManager em;

    public RegFieldmappingManager(EntityManager em) {
        this.em = em;
    }

    /**
     * Returns the RegFieldmapping object
     *
     * @param uuid The uuid of the RegFieldmapping
     * @return RegFieldmapping object with the uuid passed by parameter
     * @throws java.lang.Exception
     */
    @Override
    public RegFieldmapping get(String uuid) throws Exception {

        //Checking parameters
        if (uuid == null) {
            throw new Exception(MessageFormat.format(ErrorConstants.ERROR_MANAGER_PATTERN_NULL, "uuid"));
        }

        //Preparing query
        Query q = this.em.createNamedQuery("RegFieldmapping.findByUuid");
        q.setParameter("uuid", uuid);
        return (RegFieldmapping) q.getSingleResult();

    }

    /**
     * Returns all the RegFieldmapping
     *
     * @return all the RegFieldmapping
     * @throws Exception
     */
    @Override
    public List<RegFieldmapping> getAll() throws Exception {

        //Preparing query
        Query q = this.em.createNamedQuery("RegFieldmapping.findAll");
        return (List<RegFieldmapping>) q.getResultList();
    }

    /**
     * Adds a RegFieldmapping to the database. Returns true if the operation
     * succeed.
     *
     * @param regFieldmapping The RegFieldmapping object to be added
     * @return True if the operation was successfully completed; otherwise it
     * returns false.
     * @throws Exception
     */
    @Override
    public boolean add(RegFieldmapping regFieldmapping) throws Exception {
        //Checking parameters
        if (regFieldmapping == null) {
            throw new Exception(MessageFormat.format(ErrorConstants.ERROR_MANAGER_PATTERN_NULL, RegField.class));
        }

        //Checking the DB managers
        if (this.em == null) {
            throw new Exception(ErrorConstants.ERROR_MANAGER_PERSISTENCE_LAYER_NULL);
        }

        //Saving the object
        this.em.persist(regFieldmapping);

        return true;
    }

    /**
     * Update the RegFieldmapping specified by parameter. Returns true if the
     * operation succeed.
     *
     * @param regFieldmapping the updated RegFieldmapping (this method update
     * the RegFieldmapping on the db with the RegFieldmapping passed by
     * parameter)
     * @return True if the operation was successfully completed; otherwise it
     * returns false.
     * @throws java.lang.Exception
     */
    @Override
    public boolean update(RegFieldmapping regFieldmapping) throws Exception {
        //Checking parameters
        if (regFieldmapping == null) {
            throw new Exception(MessageFormat.format(ErrorConstants.ERROR_MANAGER_PATTERN_NULL, RegFieldmapping.class));
        }

        //Checking the DB managers
        if (this.em == null) {
            throw new Exception(ErrorConstants.ERROR_MANAGER_PERSISTENCE_LAYER_NULL);
        }

        //Updating the object
        em.merge(regFieldmapping);

        return true;
    }

    /**
     *
     * @param regFieldmapping
     * @return
     * @throws Exception
     */
    @Override
    public boolean delete(RegFieldmapping regFieldmapping) throws Exception {
        //Checking parameters
        if (regFieldmapping == null) {
            throw new Exception(MessageFormat.format(ErrorConstants.ERROR_MANAGER_PATTERN_NULL, RegFieldmapping.class));
        }

        //Checking the DB managers
        if (this.em == null) {
            throw new Exception(ErrorConstants.ERROR_MANAGER_PERSISTENCE_LAYER_NULL);
        }

        //Updating the object
        em.remove(regFieldmapping);

        return true;
    }

    @Override
    public RegFieldmapping getByFieldAndItemClass(RegField regField, RegItemclass regItemclass) throws Exception {
        //Checking parameters
        if (regField == null || regItemclass == null) {
            throw new Exception(MessageFormat.format(ErrorConstants.ERROR_MANAGER_PATTERN_NULL, RegFieldmapping.class + " or " + RegItemclass.class));
        }

        //Preparing query
        Query q = this.em.createQuery(SQLConstants.SQL_GET_REG_FIELDMAPPING_BY_FIELD_AND_ITEM_CLASS);

        q.setParameter("regField", regField);
        q.setParameter(SQLConstants.SQL_PARAMETERS_REGITEMCLASS, regItemclass);
        return (RegFieldmapping) q.getSingleResult();
    }

    /**
     * Returns all the RegFieldmapping by RegItemclass
     *
     * @param regItemclass The RegItemclass to use as filter
     * @return all the RegFieldmapping by RegItemclass
     * @throws Exception
     */
    @Override
    public List<RegFieldmapping> getAll(RegItemclass regItemclass) throws Exception {
        //Preparing query
        Query q = this.em.createQuery(SQLConstants.SQL_GET_REG_FIELDMAPPING_BY_ITEMCLASS);
        q.setParameter(SQLConstants.SQL_PARAMETERS_REGITEMCLASS, regItemclass);

        return (List<RegFieldmapping>) q.getResultList();
    }

    /**
     * It retrieves the max value of the listorder
     *
     * @param regItemclass The RegItemclass to use as filter
     * @return the max value of the listorder
     * @throws Exception
     */
    @Override
    public int getRegFieldmappingMaxListorder(RegItemclass regItemclass) throws Exception {
        //Preparing query
        Query q = this.em.createQuery(SQLConstants.SQL_GET_FIELDMAPPING_MAX_LISTORDER);
        q.setParameter(SQLConstants.SQL_PARAMETERS_REGITEMCLASS, regItemclass);

        return ((Number) q.getSingleResult()).intValue();
    }

    /**
     * It retrieves all the fieldmapping ordered asc bu listorder
     *
     * @param regItemclass The RegItemclass to use as filter
     * @return List<RegFieldmapping>
     * @throws Exception
     */
    @Override
    public List<RegFieldmapping> getAllOrderAscByListorder(RegItemclass regItemclass) throws Exception {
        //Preparing query
        Query q = this.em.createQuery(SQLConstants.SQL_GET_REG_FIELDMAPPING_BY_ITEMCLASS_ORDER_BY_LISTORDER);
        q.setParameter(SQLConstants.SQL_PARAMETERS_REGITEMCLASS, regItemclass);

        return (List<RegFieldmapping>) q.getResultList();
    }

}
