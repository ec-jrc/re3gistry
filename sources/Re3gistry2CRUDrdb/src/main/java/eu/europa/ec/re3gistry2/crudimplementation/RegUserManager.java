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
import eu.europa.ec.re3gistry2.crudinterface.IRegUserManager;
import eu.europa.ec.re3gistry2.model.RegUser;
import java.text.MessageFormat;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;

public class RegUserManager implements IRegUserManager {

    private EntityManager em;

    public RegUserManager(EntityManager em) {
        this.em = em;
    }

    /**
     * Returns the RegUser object
     *
     * @param uuid The uuid of the RegUser
     * @return RegUser object with the uuid passed by parameter
     * @throws java.lang.Exception
     */
    @Override
    public RegUser get(String uuid) throws Exception {

        //Checking parameters
        if (uuid == null) {
            throw new Exception(MessageFormat.format(ErrorConstants.ERROR_MANAGER_PATTERN_NULL, "uuid"));
        }

        //Preparing query
        Query q = this.em.createNamedQuery("RegUser.findByUuid");
        q.setParameter("uuid", uuid);
        return (RegUser) q.getSingleResult();

    }

    /**
     * Returns all the RegUser
     *
     * @return all the RegUser
     * @throws Exception
     */
    @Override
    public List<RegUser> getAll() throws Exception {

        //Preparing query
        Query q = this.em.createNamedQuery("RegUser.findAll");
        return (List<RegUser>) q.getResultList();
    }

    /**
     * Adds a RegUser to the database. Returns true if the operation succeed.
     *
     * @param regUser The RegUser object to be added
     * @return True if the operation was successfully completed; otherwise it
     * returns false.
     * @throws Exception
     */
    @Override
    public boolean add(RegUser regUser) throws Exception {
        //Checking parameters
        if (regUser == null) {
            throw new Exception(MessageFormat.format(ErrorConstants.ERROR_MANAGER_PATTERN_NULL, RegUser.class));
        }

        //Checking the DB managers
        if (this.em == null) {
            throw new Exception(ErrorConstants.ERROR_MANAGER_PERSISTENCE_LAYER_NULL);
        }

        //Saving the object
        this.em.persist(regUser);

        return true;
    }

    /**
     * Update the RegUser specified by parameter. Returns true if the operation
     * succeed.
     *
     * @param regUser the updated RegUser (this method update the RegUser on the
     * db with the RegUser passed by parameter)
     * @return True if the operation was successfully completed; otherwise it
     * returns false.
     * @throws java.lang.Exception
     */
    @Override
    public boolean update(RegUser regUser) throws Exception {
        //Checking parameters
        if (regUser == null) {
            throw new Exception(MessageFormat.format(ErrorConstants.ERROR_MANAGER_PATTERN_NULL, RegUser.class));
        }

        //Checking the DB managers
        if (this.em == null) {
            throw new Exception(ErrorConstants.ERROR_MANAGER_PERSISTENCE_LAYER_NULL);
        }

        //Updating the object
        em.merge(regUser);

        return true;
    }

    /**
     * Returns the RegUser with the specified mail
     *
     * @param email
     * @return the RegUser with the specified mail
     * @throws Exception
     */
    @Override
    public RegUser findByEmail(String email) throws Exception {
        Query q = this.em.createNamedQuery("RegUser.findByEmail");
        q.setParameter("email", email);
        return (RegUser) q.getSingleResult();
    }

}
