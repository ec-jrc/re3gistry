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
import eu.europa.ec.re3gistry2.crudinterface.IRegItemclassManager;
import eu.europa.ec.re3gistry2.model.RegItemclass;
import eu.europa.ec.re3gistry2.model.RegItemproposed;
import java.text.MessageFormat;
import java.util.Iterator;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;

public class RegItemclassManager implements IRegItemclassManager {

    private EntityManager em;

    public RegItemclassManager(EntityManager em) {
        this.em = em;
    }

    /**
     * Returns the RegItemclass object
     *
     * @param uuid The uuid of the RegItemclass
     * @return RegItemclass object with the uuid passed by parameter
     * @throws java.lang.Exception
     */
    @Override
    public RegItemclass get(String uuid) throws Exception {

        //Checking parameters
        if (uuid == null) {
            throw new Exception(MessageFormat.format(ErrorConstants.ERROR_MANAGER_PATTERN_NULL, "uuid"));
        }

        //Preparing query
        Query q = this.em.createNamedQuery("RegItemclass.findByUuid");
        q.setParameter("uuid", uuid);
        return (RegItemclass) q.getSingleResult();

    }

    /**
     * Returns all the RegItemclass
     *
     * @return all the RegItemclass
     * @throws Exception
     */
    @Override
    public List<RegItemclass> getAll() throws Exception {

        //Preparing query
        Query q = this.em.createNamedQuery("RegItemclass.findAll");
        return (List<RegItemclass>) q.getResultList();
    }

    /**
     * Returns all the RegItemclass checking if they are system items
     *
     * @return all the RegItemclass
     * @throws Exception
     */
    @Override
    public List<RegItemclass> getAll(boolean systemItems) throws Exception {

        //Preparing query
        Query q = this.em.createNamedQuery("RegItemclass.findAll");

        List<RegItemclass> tmps = q.getResultList();

        // Removing the system regItemclasses (if needed)
        if (!systemItems) {
            Iterator i = tmps.iterator();
            while (i.hasNext()) {
                RegItemclass tmp = (RegItemclass) i.next();
                if (tmp.getSystemitem()) {
                    i.remove();
                }
            }
        }

        return tmps;
    }

    /**
     * Adds a RegItemclass to the database. Returns true if the operation
     * succeed.
     *
     * @param regItemclass The RegItemclass object to be added
     * @return True if the operation was successfully completed; otherwise it
     * returns false.
     * @throws Exception
     */
    @Override
    public boolean add(RegItemclass regItemclass) throws Exception {
        //Checking parameters
        if (regItemclass == null) {
            throw new Exception(MessageFormat.format(ErrorConstants.ERROR_MANAGER_PATTERN_NULL, RegItemclass.class));
        }

        //Checking the DB managers
        if (this.em == null) {
            throw new Exception(ErrorConstants.ERROR_MANAGER_PERSISTENCE_LAYER_NULL);
        }

        //Saving the object
        this.em.persist(regItemclass);

        return true;
    }

    /**
     * Update the RegItemclass specified by parameter. Returns true if the
     * operation succeed.
     *
     * @param regItemclass the updated RegItemclass (this method update the
     * RegItemclass on the db with the RegItemclass passed by parameter)
     * @return True if the operation was successfully completed; otherwise it
     * returns false.
     * @throws java.lang.Exception
     */
    @Override
    public boolean update(RegItemclass regItemclass) throws Exception {
        //Checking parameters
        if (regItemclass == null) {
            throw new Exception(MessageFormat.format(ErrorConstants.ERROR_MANAGER_PATTERN_NULL, RegItemclass.class));
        }

        //Checking the DB managers
        if (this.em == null) {
            throw new Exception(ErrorConstants.ERROR_MANAGER_PERSISTENCE_LAYER_NULL);
        }

        //Updating the object
        em.merge(regItemclass);

        return true;
    }

    @Override
    public boolean delete(RegItemclass regItemclass) throws Exception {
        //Checking parameters
        if (regItemclass == null) {
            throw new Exception(MessageFormat.format(ErrorConstants.ERROR_MANAGER_PATTERN_NULL, RegItemproposed.class));
        }

        //Checking the DB managers
        if (this.em == null) {
            throw new Exception(ErrorConstants.ERROR_MANAGER_PERSISTENCE_LAYER_NULL);
        }

        //Updating the object
        if (!em.contains(regItemclass)) {
            regItemclass = em.merge(regItemclass);
        }

        em.remove(regItemclass);

        return true;
    }

    @Override
    public RegItemclass getByLocalid(String localid) throws Exception {
        //Checking parameters
        if (localid == null) {
            throw new Exception(MessageFormat.format(ErrorConstants.ERROR_MANAGER_PATTERN_NULL, "uuid"));
        }

        //Preparing query
        Query q = this.em.createNamedQuery("RegItemclass.findByLocalid");
        q.setParameter(SQLConstants.SQL_PARAMETERS_LOCALID, localid);
        return (RegItemclass) q.getSingleResult();
    }

    /**
     * It retrieves the child RegItemclass of the RegItemclass passed by
     * parameter if available, otherwise it returns null
     *
     * @param regItemclass
     * @return RegItemclass
     * @throws Exception
     */
    @Override
    public List<RegItemclass> getChildItemclass(RegItemclass regItemclass) throws Exception {
        //Checking parameters
        if (regItemclass == null) {
            throw new Exception(MessageFormat.format(ErrorConstants.ERROR_MANAGER_PATTERN_NULL, RegItemclass.class));
        }

        //Preparing query
        Query q = this.em.createQuery(SQLConstants.SQL_GET_ITEMCLASS_CHILD_BY_ITEMCLASS);
        q.setParameter("regitemclass", regItemclass);
        try {
            return (List<RegItemclass>) q.getResultList();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * It retrieves the max value of the dataprocedureorder
     *
     * @return int
     * @throws Exception
     */
    @Override
    public int getRegItemclassMaxDataprocedureorder() throws Exception {

        //Preparing query
        Query q = this.em.createQuery(SQLConstants.SQL_GET_REGITEMCLASS_MAX_DATAPROCEDUREORDER);

        return ((Number) q.getSingleResult()).intValue();

    }
}
