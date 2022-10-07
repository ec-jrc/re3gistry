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
import eu.europa.ec.re3gistry2.crudinterface.IRegLocalizationManager;
import eu.europa.ec.re3gistry2.model.RegAction;
import eu.europa.ec.re3gistry2.model.RegField;
import eu.europa.ec.re3gistry2.model.RegItem;
import eu.europa.ec.re3gistry2.model.RegItemclass;
import eu.europa.ec.re3gistry2.model.RegLanguagecode;
import eu.europa.ec.re3gistry2.model.RegLocalization;
import java.text.MessageFormat;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;

public class RegLocalizationManager implements IRegLocalizationManager {

    private EntityManager em;

    public RegLocalizationManager(EntityManager em) {
        this.em = em;
    }

    /**
     * Returns the RegLocalization object
     *
     * @param uuid The uuid of the RegLocalization
     * @return RegItemgroupmapping object with the uuid passed by parameter
     * @throws java.lang.Exception
     */
    @Override
    public RegLocalization get(String uuid) throws Exception {

        //Checking parameters
        if (uuid == null) {
            throw new Exception(MessageFormat.format(ErrorConstants.ERROR_MANAGER_PATTERN_NULL, "uuid"));
        }

        //Preparing query
        Query q = this.em.createNamedQuery("RegLocalization.findByUuid");
        q.setParameter("uuid", uuid);
        return (RegLocalization) q.getSingleResult();

    }

    /**
     * Returns all the RegLocalization
     *
     * @return all the RegLocalization
     * @throws Exception
     */
    @Override
    public List<RegLocalization> getAll() throws Exception {

        //Preparing query
        Query q = this.em.createNamedQuery("RegLocalization.findAll");
        return (List<RegLocalization>) q.getResultList();
    }

    /**
     * Adds a RegLocalization to the database. Returns true if the operation
     * succeed.
     *
     * @param regLocalization The RegLocalization object to be added
     * @return True if the operation was successfully completed; otherwise it
     * returns false.
     * @throws Exception
     */
    @Override
    public boolean add(RegLocalization regLocalization) throws Exception {
        //Checking parameters
        if (regLocalization == null) {
            throw new Exception(MessageFormat.format(ErrorConstants.ERROR_MANAGER_PATTERN_NULL, RegLocalization.class));
        }

        //Checking the DB managers
        if (this.em == null) {
            throw new Exception(ErrorConstants.ERROR_MANAGER_PERSISTENCE_LAYER_NULL);
        }

        //Saving the object
        this.em.persist(regLocalization);

        return true;
    }

    /**
     * Update the RegLocalization specified by parameter. Returns true if the
     * operation succeed.
     *
     * @param regLocalization the updated RegLocalization (this method update
     * the RegLocalization on the db with the RegLocalization passed by
     * parameter)
     * @return True if the operation was successfully completed; otherwise it
     * returns false.
     * @throws java.lang.Exception
     */
    @Override
    public boolean update(RegLocalization regLocalization) throws Exception {
        //Checking parameters
        if (regLocalization == null) {
            throw new Exception(MessageFormat.format(ErrorConstants.ERROR_MANAGER_PATTERN_NULL, RegLocalization.class));
        }

        //Checking the DB managers
        if (this.em == null) {
            throw new Exception(ErrorConstants.ERROR_MANAGER_PERSISTENCE_LAYER_NULL);
        }

        //Updating the object
        em.merge(regLocalization);

        return true;
    }

    @Override
    public boolean delete(RegLocalization regLocalization) throws Exception {
        //Checking parameters
        if (regLocalization == null) {
            throw new Exception(MessageFormat.format(ErrorConstants.ERROR_MANAGER_PATTERN_NULL, RegLocalization.class));
        }

        //Checking the DB managers
        if (this.em == null) {
            throw new Exception(ErrorConstants.ERROR_MANAGER_PERSISTENCE_LAYER_NULL);
        }

        //Updating the object
        em.remove(regLocalization);

        return true;
    }

    /**
     * Returns all the RegLocalization for the specified RegItem
     *
     * @param regItem The regItem to find the localization fields
     * @return all the RegLocalization
     * @throws Exception
     */
    @Override
    public List<RegLocalization> getAll(RegItem regItem) throws Exception {

        //Preparing query
        Query q = this.em.createQuery(SQLConstants.SQL_GET_LOCALIZATION_FIELDS_BY_ITEM);
        q.setParameter(SQLConstants.SQL_PARAMETERS_REGITEM, regItem);
        return (List<RegLocalization>) q.getResultList();
    }

    /**
     * Returns all the RegLocalization for the specified RegItem in the specific
     * language
     *
     * @param regItem The regItem to find the localization fields
     * @param regLanguagecode
     * @return all the RegLocalization
     * @throws Exception
     */
    @Override
    public List<RegLocalization> getAll(RegItem regItem, RegLanguagecode regLanguagecode) throws Exception {

        //Preparing query
        Query q = this.em.createQuery(SQLConstants.SQL_GET_LOCALIZATION_FIELDS_BY_ITEM_AND_LANGUAGE);
        q.setParameter(SQLConstants.SQL_PARAMETERS_REGITEM, regItem);
        q.setParameter(SQLConstants.SQL_PARAMETERS_REGLANGUAGECODE, regLanguagecode);
        return (List<RegLocalization>) q.getResultList();
    }

    /**
     * Returns all the RegLocalization for the specified RegItems in the
     * specific language
     *
     * @param regItem The regItem to find the localization fields
     * @param regLanguagecode
     * @return all the RegLocalization
     * @throws Exception
     */
    @Override
    public List<RegLocalization> getAll(RegLanguagecode regLanguagecode, List<RegItem> regItems) throws Exception {
        //Preparing query
        Query q = this.em.createQuery(SQLConstants.SQL_GET_LOCALIZATION_FIELDS_BY_LANGUAGE_AND_ITEMS);
        q.setParameter(SQLConstants.SQL_PARAMETERS_REGLANGUAGECODE, regLanguagecode);
        q.setParameter(SQLConstants.SQL_PARAMETERS_REGITEM_LIST, regItems);
        return (List<RegLocalization>) q.getResultList();
    }

    /**
     * Returns all the RegLocalization for the specified RegItem
     *
     * @param regItem The regItem to find the localization fields
     * @return all the RegLocalization
     * @throws Exception
     */
    @Override
    public List<RegLocalization> getAllWithRelationReference(RegItem regItem) throws Exception {

        //Preparing query
        Query q = this.em.createQuery(SQLConstants.SQL_GET_LOCALIZATION_WITH_RELATION_REFERENCE_BY_ITEM);
        q.setParameter(SQLConstants.SQL_PARAMETERS_REGITEM, regItem);
        return (List<RegLocalization>) q.getResultList();
    }

    /**
     * Returns all the RegLocalization for the specified RegItemclass
     *
     * @param regItemclass The regItemclass to find the localization fields
     * @return all the RegLocalization
     * @throws Exception
     */
    @Override
    public List<RegLocalization> getAll(RegItemclass regItemclass) throws Exception {

        //Preparing query
        Query q = this.em.createQuery(SQLConstants.SQL_GET_LOCALIZATION_FIELDS_BY_ITEMCLASS);
        q.setParameter(SQLConstants.SQL_PARAMETERS_REGITEMCLASS, regItemclass);
        return (List<RegLocalization>) q.getResultList();
    }

    /**
     * Returns all the RegLocalization for the specified RegField
     *
     * @param regField The regField to find the localization
     * @return all the RegLocalization
     * @throws Exception
     */
    @Override
    public List<RegLocalization> getAll(RegField regField) throws Exception {

        //Preparing query
        Query q = this.em.createQuery(SQLConstants.SQL_GET_LOCALIZATION_BY_FIELD);
        q.setParameter(SQLConstants.SQL_PARAMETERS_REGFIELD, regField);
        return (List<RegLocalization>) q.getResultList();
    }

    /**
     * Returns all the RegLocalization for the specified RegField and
     * RegLanguagecode
     *
     * @param regField The regField to find the localization
     * @param regLanguagecode The regLanguagecode to find the localization
     * @return all the RegLocalization
     * @throws Exception
     */
    @Override
    public RegLocalization get(RegField regField, RegLanguagecode regLanguagecode) throws Exception {

        //Preparing query
        Query q = this.em.createQuery(SQLConstants.SQL_GET_LOCALIZATION_BY_FIELD_LANGUAGECODE);
        q.setParameter(SQLConstants.SQL_PARAMETERS_REGFIELD, regField);
        q.setParameter(SQLConstants.SQL_PARAMETERS_REGLANGUAGECODE, regLanguagecode);
        return (RegLocalization) q.getSingleResult();
    }

    /**
     * Returns all the RegLocalization for the specified RegField and RegItem
     *
     * @param regField The regField to find the localization
     * @param regItem The regItem to find the localization
     * @return all the RegLocalization
     * @throws Exception
     */
    @Override
    public List<RegLocalization> getAll(RegField regField, RegItem regItem) throws Exception {

        //Preparing query
        Query q = this.em.createQuery(SQLConstants.SQL_GET_LOCALIZATION_BY_FIELD_ITEM);
        q.setParameter(SQLConstants.SQL_PARAMETERS_REGFIELD, regField);
        q.setParameter(SQLConstants.SQL_PARAMETERS_REGITEM, regItem);
        return (List<RegLocalization>) q.getResultList();
    }

    /**
     * Returns all the RegLocalization for the specified RegField, RegItem and
     * RegLanguagecode
     *
     * @param regField The regField to find the localization
     * @param regItem The regItem to find the localization
     * @param regLanguagecode The regLanguagecode to find the localization
     * @return all the RegLocalization
     * @throws Exception
     */
    @Override
    public List<RegLocalization> getAll(RegField regField, RegItem regItem, RegLanguagecode regLanguagecode) throws Exception {

        //Preparing query
        Query q = this.em.createQuery(SQLConstants.SQL_GET_LOCALIZATION_BY_FIELD_ITEM_LANGUAGECODE);
        q.setParameter(SQLConstants.SQL_PARAMETERS_REGFIELD, regField);
        q.setParameter(SQLConstants.SQL_PARAMETERS_REGITEM, regItem);
        q.setParameter(SQLConstants.SQL_PARAMETERS_REGLANGUAGECODE, regLanguagecode);
        return (List<RegLocalization>) q.getResultList();
    }

    /**
     * Returns all the RegLocalization for the specified RegField, RegItem and
     * RegAction
     *
     * @param regField The regField to find the localization
     * @param regItem The regItem to find the localization
     * @param regAction
     * @return all the RegLocalization
     * @throws Exception
     */
    @Override
    public List<RegLocalization> getAll(RegField regField, RegItem regItem, RegAction regAction) throws Exception {

        //Preparing query
        Query q = this.em.createQuery(SQLConstants.SQL_GET_LOCALIZATION_BY_FIELD_ITEM_ACTION);
        q.setParameter(SQLConstants.SQL_PARAMETERS_REGFIELD, regField);
        q.setParameter(SQLConstants.SQL_PARAMETERS_REGITEM, regItem);
        q.setParameter(SQLConstants.SQL_PARAMETERS_ACTION, regAction);
        return (List<RegLocalization>) q.getResultList();
    }

    @Override
    public List<RegLocalization> getAllFieldsByValue(String value) throws Exception {
        //Preparing query
        Query q = this.em.createQuery(SQLConstants.SQL_GET_LOCALIZATION_FIELD_BY_VALUE);
        q.setParameter(SQLConstants.SQL_PARAMETERS_VALUE, value);
        return (List<RegLocalization>) q.getResultList();
    }

    @Override
    public List<RegLocalization> getAllHrefNotNull(RegField regField, RegItemclass regItemclass) throws Exception {

        //Preparing query
        Query q = this.em.createQuery(SQLConstants.SQL_GET_LOCALIZATION_FIELDS_BY_REGFIELD_ITEMCLASS_HREFNORNULL);
        q.setParameter(SQLConstants.SQL_PARAMETERS_REGFIELD, regField);
        q.setParameter(SQLConstants.SQL_PARAMETERS_REGITEMCLASS, regItemclass);
        return (List<RegLocalization>) q.getResultList();
    }

}