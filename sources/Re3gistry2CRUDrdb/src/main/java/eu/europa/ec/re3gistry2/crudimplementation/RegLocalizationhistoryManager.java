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
import eu.europa.ec.re3gistry2.crudinterface.IRegLocalizationhistoryManager;
import eu.europa.ec.re3gistry2.model.RegAction;
import eu.europa.ec.re3gistry2.model.RegField;
import eu.europa.ec.re3gistry2.model.RegItemhistory;
import eu.europa.ec.re3gistry2.model.RegLanguagecode;
import eu.europa.ec.re3gistry2.model.RegLocalizationhistory;
import java.text.MessageFormat;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;

public class RegLocalizationhistoryManager implements IRegLocalizationhistoryManager {

    private EntityManager em;

    public RegLocalizationhistoryManager(EntityManager em) {
        this.em = em;
    }

    /**
     * Returns the RegLocalizationhistory object
     *
     * @param uuid The uuid of the RegLocalizationhistory
     * @return RegLocalizationhistory object with the uuid passed by parameter
     * @throws java.lang.Exception
     */
    @Override
    public RegLocalizationhistory get(String uuid) throws Exception {

        //Checking parameters
        if (uuid == null) {
            throw new Exception(MessageFormat.format(ErrorConstants.ERROR_MANAGER_PATTERN_NULL, "uuid"));
        }

        //Preparing query
        Query q = this.em.createNamedQuery("RegLocalizationhistory.findByUuid");
        q.setParameter("uuid", uuid);
        return (RegLocalizationhistory) q.getSingleResult();

    }

    /**
     * Returns all the RegLocalizationhistory
     *
     * @return all the RegLocalizationhistory
     * @throws Exception
     */
    @Override
    public List<RegLocalizationhistory> getAll() throws Exception {

        //Preparing query
        Query q = this.em.createNamedQuery("RegLocalizationhistory.findAll");
        return (List<RegLocalizationhistory>) q.getResultList();
    }

    /**
     * Adds a RegLocalizationhistory to the database. Returns true if the
     * operation succeed.
     *
     * @param regLocalizationhistory The RegLocalizationhistory object to be
     * added
     * @return True if the operation was successfully completed; otherwise it
     * returns false.
     * @throws Exception
     */
    @Override
    public boolean add(RegLocalizationhistory regLocalizationhistory) throws Exception {
        //Checking parameters
        if (regLocalizationhistory == null) {
            throw new Exception(MessageFormat.format(ErrorConstants.ERROR_MANAGER_PATTERN_NULL, RegLocalizationhistory.class));
        }

        //Checking the DB managers
        if (this.em == null) {
            throw new Exception(ErrorConstants.ERROR_MANAGER_PERSISTENCE_LAYER_NULL);
        }

        //Saving the object
        this.em.persist(regLocalizationhistory);

        return true;
    }

    /**
     * Update the RegLocalizationhistory specified by parameter. Returns true if
     * the operation succeed.
     *
     * @param regLocalizationhistory the updated RegLocalizationhistory (this
     * method update the RegLocalizationhistory on the db with the item passed
     * by parameter)
     * @return True if the operation was successfully completed; otherwise it
     * returns false.
     * @throws java.lang.Exception
     */
    @Override
    public boolean update(RegLocalizationhistory regLocalizationhistory) throws Exception {
        //Checking parameters
        if (regLocalizationhistory == null) {
            throw new Exception(MessageFormat.format(ErrorConstants.ERROR_MANAGER_PATTERN_NULL, RegLocalizationhistory.class));
        }

        //Checking the DB managers
        if (this.em == null) {
            throw new Exception(ErrorConstants.ERROR_MANAGER_PERSISTENCE_LAYER_NULL);
        }

        //Updating the object
        em.merge(regLocalizationhistory);

        return true;
    }

    /**
     * Returns all the RegLocalization for the specified RegItem
     *
     * @param regLocalizationhistory
     * @return all the RegLocalizationhistory
     * @throws Exception
     */
    @Override
    public List<RegLocalizationhistory> getAll(RegItemhistory regLocalizationhistory) throws Exception {

        //Preparing query
        Query q = this.em.createQuery(SQLConstants.SQL_GET_LOCALIZATION_FIELDS_BY_ITEMHISTORY);
        q.setParameter(SQLConstants.SQL_PARAMETERS_REGITEMHISTORY, regLocalizationhistory);
        return (List<RegLocalizationhistory>) q.getResultList();
    }

    /**
     * Returns all the RegLocalization for the specified RegField and RegItem
     *
     * @param regField The RegField to find the localization
     * @param regItemhistory
     * @return all the RegLocalizationhistory
     * @throws Exception
     */
    @Override
    public List<RegLocalizationhistory> getAll(RegField regField, RegItemhistory regItemhistory) throws Exception {

        //Preparing query
        Query q = this.em.createQuery(SQLConstants.SQL_GET_LOCALIZATION_BY_FIELD_ITEMHISTORY);
        q.setParameter(SQLConstants.SQL_PARAMETERS_REGFIELD, regField);
        q.setParameter("regitemhistory", regItemhistory);
        return (List<RegLocalizationhistory>) q.getResultList();
    }

    /**
     * Returns all the RegLocalizationhistory for the specified RegField,
     * RegItemhistory and RegLanguagecode
     *
     * @param regField The regField to find the localization
     * @param regItemhistory
     * @param regLanguagecode The regLanguagecode to find the localization
     * @return all the RegLocalizationproposed
     * @throws Exception
     */
    @Override
    public List<RegLocalizationhistory> getAll(RegField regField, RegItemhistory regItemhistory, RegLanguagecode regLanguagecode) throws Exception {

        //Preparing query
        Query q = this.em.createQuery(SQLConstants.SQL_GET_LOCALIZATIONHISTORY_BY_FIELD_ITEM_LANGUAGECODE);
        q.setParameter(SQLConstants.SQL_PARAMETERS_REGFIELD, regField);
        q.setParameter(SQLConstants.SQL_PARAMETERS_REGITEM, regItemhistory);
        q.setParameter("language", regLanguagecode);
        return (List<RegLocalizationhistory>) q.getResultList();
    }

    /**
     * Returns all the RegLocalizationhistory for the specified RegField,
     * RegItemhistory and RegLanguagecode
     *
     * @param regField The regField to find the localization
     * @param regItemhistory
     * @param regAction
     * @return all the RegLocalizationproposed
     * @throws Exception
     */
    @Override
    public List<RegLocalizationhistory> getAll(RegField regField, RegItemhistory regItemhistory, RegAction regAction) throws Exception {

        //Preparing query
        Query q = this.em.createQuery(SQLConstants.SQL_GET_LOCALIZATIONHISTORY_BY_FIELD_ITEM_ACTION);
        q.setParameter(SQLConstants.SQL_PARAMETERS_REGFIELD, regField);
        q.setParameter(SQLConstants.SQL_PARAMETERS_REGITEM, regItemhistory);
        q.setParameter(SQLConstants.SQL_PARAMETERS_ACTION, regAction);
        return (List<RegLocalizationhistory>) q.getResultList();
    }

     /**
     * Returns all the RegLocalization for the specified RegItem in the specific
     * language
     *
     * @param regItemhistory The regItem to find the localization fields
     * @param regLanguagecode
     * @return all the RegLocalization
     * @throws Exception
     */
    @Override
    public List<RegLocalizationhistory> getAll(RegItemhistory regItemhistory, RegLanguagecode regLanguagecode) throws Exception {

        //Preparing query
        Query q = this.em.createQuery(SQLConstants.SQL_GET_LOCALIZATIONHISTORY_BY_ITEM_LANGUAGECODE);
        q.setParameter(SQLConstants.SQL_PARAMETERS_REGITEM, regItemhistory);
        q.setParameter(SQLConstants.SQL_PARAMETERS_REGLANGUAGECODE, regLanguagecode);
        return (List<RegLocalizationhistory>) q.getResultList();
    }
}
