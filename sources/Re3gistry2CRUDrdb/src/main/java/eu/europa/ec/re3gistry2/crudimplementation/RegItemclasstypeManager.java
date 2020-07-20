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
import eu.europa.ec.re3gistry2.crudinterface.IRegItemclasstypeManager;
import eu.europa.ec.re3gistry2.model.RegItemclasstype;
import java.text.MessageFormat;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;

public class RegItemclasstypeManager implements IRegItemclasstypeManager {

    private EntityManager em;

    public RegItemclasstypeManager(EntityManager em) {
        this.em = em;
    }

    /**
     * Returns the RegItemclasstype object
     *
     * @param uuid The uuid of the RegItemclasstype
     * @return RegItemclasstype object with the uuid passed by parameter
     * @throws java.lang.Exception
     */
    @Override
    public RegItemclasstype get(String uuid) throws Exception {

        //Checking parameters
        if (uuid == null) {
            throw new Exception(MessageFormat.format(ErrorConstants.ERROR_MANAGER_PATTERN_NULL, "uuid"));
        }

        //Preparing query
        Query q = this.em.createNamedQuery("RegItemclasstype.findByUuid");
        q.setParameter("uuid", uuid);
        return (RegItemclasstype) q.getSingleResult();

    }

    @Override
    public RegItemclasstype getByLocalid(String localid) throws Exception {
         //Checking parameters
        if (localid == null) {
            throw new Exception(MessageFormat.format(ErrorConstants.ERROR_MANAGER_PATTERN_NULL, "uuid"));
        }

        //Preparing query
        Query q = this.em.createNamedQuery("RegItemclasstype.findByLocalid");
        q.setParameter(SQLConstants.SQL_PARAMETERS_LOCALID, localid);
        return (RegItemclasstype) q.getSingleResult();
    }

    /**
     * Returns all the RegItemclasstype
     *
     * @return all the RegItemclasstype
     * @throws Exception
     */
    @Override
    public List<RegItemclasstype> getAll() throws Exception {

        //Preparing query
        Query q = this.em.createNamedQuery("RegItemclasstype.findAll");
        return (List<RegItemclasstype>) q.getResultList();
    }

    /**
     * Adds a RegItemclasstype to the database. Returns true if the operation
     * succeed.
     *
     * @param regItemclasstype The RegItemclasstype object to be added
     * @return True if the operation was successfully completed; otherwise it
     * returns false.
     * @throws Exception
     */
    @Override
    public boolean add(RegItemclasstype regItemclasstype) throws Exception {
        //Checking parameters
        if (regItemclasstype == null) {
            throw new Exception(MessageFormat.format(ErrorConstants.ERROR_MANAGER_PATTERN_NULL, RegItemclasstype.class));
        }

        //Checking the DB managers
        if (this.em == null) {
            throw new Exception(ErrorConstants.ERROR_MANAGER_PERSISTENCE_LAYER_NULL);
        }

        //Saving the object
        this.em.persist(regItemclasstype);

        return true;
    }

    /**
     * Update the RegItemclasstype specified by parameter. Returns true if the operation
     * succeed.
     *
     * @param regItemclasstype the updated RegItemclasstype (this method update the RegItemclasstype on
     * the db with the RegItemclasstype passed by parameter)
     * @return True if the operation was successfully completed; otherwise it
     * returns false.
     * @throws java.lang.Exception
     */
    @Override
    public boolean update(RegItemclasstype regItemclasstype) throws Exception {
        //Checking parameters
        if (regItemclasstype == null) {
            throw new Exception(MessageFormat.format(ErrorConstants.ERROR_MANAGER_PATTERN_NULL, RegItemclasstype.class));
        }

        //Checking the DB managers
        if (this.em == null) {
            throw new Exception(ErrorConstants.ERROR_MANAGER_PERSISTENCE_LAYER_NULL);
        }

        //Updating the object
        em.merge(regItemclasstype);

        return true;
    }

}
