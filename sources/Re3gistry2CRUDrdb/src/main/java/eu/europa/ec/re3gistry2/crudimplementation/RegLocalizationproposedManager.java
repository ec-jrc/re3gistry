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
import eu.europa.ec.re3gistry2.crudinterface.IRegLocalizationproposedManager;
import eu.europa.ec.re3gistry2.model.RegAction;
import eu.europa.ec.re3gistry2.model.RegField;
import eu.europa.ec.re3gistry2.model.RegItemproposed;
import eu.europa.ec.re3gistry2.model.RegItemclass;
import eu.europa.ec.re3gistry2.model.RegLanguagecode;
import eu.europa.ec.re3gistry2.model.RegLocalizationproposed;
import eu.europa.ec.re3gistry2.model.RegLocalization;
import java.text.MessageFormat;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;

public class RegLocalizationproposedManager implements IRegLocalizationproposedManager {

    private EntityManager em;

    public RegLocalizationproposedManager(EntityManager em) {
        this.em = em;
    }

    /**
     * Returns the RegLocalizationproposed object
     *
     * @param uuid The uuid of the RegLocalizationproposed
     * @return RegLocalizationproposed object with the uuid passed by parameter
     * @throws java.lang.Exception
     */
    @Override
    public RegLocalizationproposed get(String uuid) throws Exception {

        //Checking parameters
        if (uuid == null) {
            throw new Exception(MessageFormat.format(ErrorConstants.ERROR_MANAGER_PATTERN_NULL, "uuid"));
        }

        //Preparing query
        Query q = this.em.createNamedQuery("RegLocalizationproposed.findByUuid");
        q.setParameter("uuid", uuid);
        return (RegLocalizationproposed) q.getSingleResult();

    }

    /**
     * Returns all the RegLocalizationproposed
     *
     * @return all the RegLocalizationproposed
     * @throws Exception
     */
    @Override
    public List<RegLocalizationproposed> getAll() throws Exception {

        //Preparing query
        Query q = this.em.createNamedQuery("RegLocalizationproposed.findAll");
        return (List<RegLocalizationproposed>) q.getResultList();
    }

    /**
     * Adds a RegLocalizationproposed to the database. Returns true if the operation
     * succeed.
     *
     * @param regLocalization The RegLocalizationproposed object to be added
     * @return True if the operation was successfully completed; otherwise it
     * returns false.
     * @throws Exception
     */
    @Override
    public boolean add(RegLocalizationproposed regLocalization) throws Exception {
        //Checking parameters
        if (regLocalization == null) {
            throw new Exception(MessageFormat.format(ErrorConstants.ERROR_MANAGER_PATTERN_NULL, RegLocalizationproposed.class));
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
     * Update the RegLocalizationproposed specified by parameter. Returns true if the operation
     * succeed.
     *
     * @param regLocalization the updated RegLocalizationproposed (this method update the RegLocalizationproposed on
     * the db with the RegLocalizationproposed passed by parameter)
     * @return True if the operation was successfully completed; otherwise it
     * returns false.
     * @throws java.lang.Exception
     */
    @Override
    public boolean update(RegLocalizationproposed regLocalization) throws Exception {
        //Checking parameters
        if (regLocalization == null) {
            throw new Exception(MessageFormat.format(ErrorConstants.ERROR_MANAGER_PATTERN_NULL, RegLocalizationproposed.class));
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
    public boolean delete(RegLocalizationproposed regLocalization) throws Exception {
        //Checking parameters
        if (regLocalization == null) {
            throw new Exception(MessageFormat.format(ErrorConstants.ERROR_MANAGER_PATTERN_NULL, RegLocalizationproposed.class));
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
     * Returns all the RegLocalizationproposed for the specified RegItemproposed
     *
     * @param regItemproposed The RegItemproposed to find the localization fields
     * @return all the RegLocalizationproposed
     * @throws Exception
     */
    @Override
    public List<RegLocalizationproposed> getAll(RegItemproposed regItemproposed) throws Exception {

        //Preparing query
        Query q = this.em.createQuery(SQLConstants.SQL_GET_LOCALIZATIONPROPOSED_FIELDS_BY_ITEM);
        q.setParameter(SQLConstants.SQL_PARAMETERS_REGITEM, regItemproposed);
        return (List<RegLocalizationproposed>) q.getResultList();
    }

    /**
     * Returns all the RegLocalizationproposed for the specified RegItemclass
     *
     * @param regItemclass The regItemclass to find the localization fields
     * @return all the RegLocalizationproposed
     * @throws Exception
     */
    @Override
    public List<RegLocalizationproposed> getAll(RegItemclass regItemclass) throws Exception {

        //Preparing query
        Query q = this.em.createQuery(SQLConstants.SQL_GET_LOCALIZATIONPROPOSED_FIELDS_BY_ITEMCLASS);
        q.setParameter("regitemclass", regItemclass);
        return (List<RegLocalizationproposed>) q.getResultList();
    }
    
    /**
     * Returns all the RegLocalizationproposed for the specified RegField
     *
     * @param regField The regField to find the localization
     * @return all the RegLocalizationproposed
     * @throws Exception
     */
    @Override
    public List<RegLocalizationproposed> getAll(RegField regField) throws Exception {

        //Preparing query
        Query q = this.em.createQuery(SQLConstants.SQL_GET_LOCALIZATIONPROPOSED_BY_FIELD);
        q.setParameter(SQLConstants.SQL_PARAMETERS_REGFIELD, regField);
        return (List<RegLocalizationproposed>) q.getResultList();
    }
    
    /**
     * Returns all the RegLocalizationproposed for the specified RegItemproposed and RegLanguagecode
     *
     * @param regItemproposed
     * @param regLanguagecode The regLanguagecode to find the localization
     * @return all the RegLocalizationproposed
     * @throws Exception
     */
    @Override
    public List<RegLocalizationproposed> getAll(RegItemproposed regItemproposed, RegLanguagecode regLanguagecode) throws Exception {

        //Preparing query
        Query q = this.em.createQuery(SQLConstants.SQL_GET_LOCALIZATIONPROPOSED_BY_REGITEMPROPOSED_LANGUAGECODE);
        q.setParameter(SQLConstants.SQL_PARAMETERS_REGITEMPROPOSED, regItemproposed);
        q.setParameter(SQLConstants.SQL_PARAMETERS_REGLANGUAGECODE, regLanguagecode);
        return (List<RegLocalizationproposed>) q.getResultList();
    }
    
        /**
     * Returns all the RegLocalizationproposed representing RegRelations for the specified RegItemproposed and RegLanguagecode
     *
     * @param regItemproposed
     * @param regLanguagecode The regLanguagecode to find the localization
     * @return all the RegLocalizationproposed
     * @throws Exception
     */
    @Override
    public List<RegLocalizationproposed> getAllRelationReference(RegItemproposed regItemproposed, RegLanguagecode regLanguagecode) throws Exception {

        //Preparing query
        Query q = this.em.createQuery(SQLConstants.SQL_GET_LOCALIZATIONPROPOSED_ONLY_RELATION_BY_REGITEMPROPOSED_LANGUAGECODE);
        q.setParameter(SQLConstants.SQL_PARAMETERS_REGITEMPROPOSED, regItemproposed);
        q.setParameter(SQLConstants.SQL_PARAMETERS_REGLANGUAGECODE, regLanguagecode);
        return (List<RegLocalizationproposed>) q.getResultList();
    }
    
    /**
     * Returns all the RegLocalizationproposed for the specified RegField and RegLanguagecode
     *
     * @param regField The regField to find the localization
     * @param regLanguagecode The regLanguagecode to find the localization
     * @return all the RegLocalizationproposed
     * @throws Exception
     */
    @Override
    public RegLocalizationproposed get(RegField regField, RegLanguagecode regLanguagecode) throws Exception {

        //Preparing query
        Query q = this.em.createQuery(SQLConstants.SQL_GET_LOCALIZATIONPROPOSED_BY_FIELD_LANGUAGECODE);
        q.setParameter(SQLConstants.SQL_PARAMETERS_REGFIELD, regField);
        q.setParameter(SQLConstants.SQL_PARAMETERS_REGLANGUAGECODE, regLanguagecode);
        return (RegLocalizationproposed) q.getSingleResult();
    }
    
    /**
     * Returns all the RegLocalizationproposed for the specified RegField and RegItemproposed
     *
     * @param regField The regField to find the localization
     * @param regItemproposed The regItemproposed to find the localization
     * @return all the RegLocalizationproposed
     * @throws Exception
     */
    @Override
    public List<RegLocalizationproposed> getAll(RegField regField, RegItemproposed regItemproposed) throws Exception {

        //Preparing query
        Query q = this.em.createQuery(SQLConstants.SQL_GET_LOCALIZATIONPROPOSED_BY_FIELD_ITEM);
        q.setParameter(SQLConstants.SQL_PARAMETERS_REGFIELD, regField);
        q.setParameter(SQLConstants.SQL_PARAMETERS_REGITEM, regItemproposed);
        return (List<RegLocalizationproposed>) q.getResultList();
    }
    
    /**
     * Returns all the RegLocalizationproposed for the specified RegField and RegItemproposed
     * where the value and regRelationproposedreference is null
     *
     * @param regField The regField to find the localization
     * @param regItemproposed The regItemproposed to find the localization
     * @return all the RegLocalizationproposed
     * @throws Exception
     */
    @Override
    public List<RegLocalizationproposed> getAllNull(RegField regField, RegItemproposed regItemproposed) throws Exception {

        //Preparing query
        Query q = this.em.createQuery(SQLConstants.SQL_GET_LOCALIZATIONPROPOSED_NULL_BY_FIELD_ITEM);
        q.setParameter(SQLConstants.SQL_PARAMETERS_REGFIELD, regField);
        q.setParameter(SQLConstants.SQL_PARAMETERS_REGITEM, regItemproposed);
        return (List<RegLocalizationproposed>) q.getResultList();
    }
    
    /**
     * Returns all the RegLocalizationproposed for the specified RegField, RegItemproposed and RegLanguagecode
     *
     * @param regField The regField to find the localization
     * @param regItemproposed The regItemproposed to find the localization
     * @param regLanguagecode The regLanguagecode to find the localization
     * @return all the RegLocalizationproposed
     * @throws Exception
     */
    @Override
    public List<RegLocalizationproposed> getAll(RegField regField, RegItemproposed regItemproposed, RegLanguagecode regLanguagecode) throws Exception {

        //Preparing query
        Query q = this.em.createQuery(SQLConstants.SQL_GET_LOCALIZATIONPROPOSED_BY_FIELD_ITEM_LANGUAGECODE);
        q.setParameter(SQLConstants.SQL_PARAMETERS_REGFIELD, regField);
        q.setParameter(SQLConstants.SQL_PARAMETERS_REGITEM, regItemproposed);
         q.setParameter("language", regLanguagecode);
        return (List<RegLocalizationproposed>) q.getResultList();
    }
    
        /**
     * Returns all the RegLocalizationproposed for the specified RegField, RegItemproposed and RegLanguagecode
     *
     * @param regField The regField to find the localization
     * @param regItemproposed The regItemproposed to find the localization
     * @param regAction
     * @return all the RegLocalizationproposed
     * @throws Exception
     */
    @Override
    public List<RegLocalizationproposed> getAll(RegField regField, RegItemproposed regItemproposed, RegAction regAction) throws Exception {

        //Preparing query
        Query q = this.em.createQuery(SQLConstants.SQL_GET_LOCALIZATIONPROPOSED_BY_FIELD_ITEM_ACTION);
        q.setParameter(SQLConstants.SQL_PARAMETERS_REGFIELD, regField);
        q.setParameter(SQLConstants.SQL_PARAMETERS_REGITEM, regItemproposed);
        q.setParameter(SQLConstants.SQL_PARAMETERS_ACTION, regAction);
        return (List<RegLocalizationproposed>) q.getResultList();
    }
    
     /**
     * Returns all the RegLocalizationproposed for the specified RegField, RegItemproposed and RegLanguagecode
     * that are new (no reference to the RegLocalization)
     * 
     * @param regField The regField to find the localization
     * @param regItemproposed The regItemproposed to find the localization
     * @param regLanguagecode The regLanguagecode to find the localization
     * @return all the RegLocalizationproposed
     * @throws Exception
     */
    @Override
    public List<RegLocalizationproposed> getAllNew(RegField regField, RegItemproposed regItemproposed, RegLanguagecode regLanguagecode) throws Exception {

        //Preparing query
        Query q = this.em.createQuery(SQLConstants.SQL_GET_LOCALIZATIONPROPOSED_BY_FIELD_ITEM_LANGUAGECODE_NEW);
        q.setParameter(SQLConstants.SQL_PARAMETERS_REGFIELD, regField);
        q.setParameter(SQLConstants.SQL_PARAMETERS_REGITEM, regItemproposed);
         q.setParameter("language", regLanguagecode);
        return (List<RegLocalizationproposed>) q.getResultList();
    }
    
    /**
     *
     * @param regLocalization
     * @return
     * @throws Exception
     */
    @Override
    public RegLocalizationproposed getByRegLocalizationReferenceAndLanguage(RegLocalization regLocalization, RegLanguagecode regLangagecode) throws Exception {
    
        //Preparing query
        Query q = this.em.createQuery(SQLConstants.SQL_GET_LOCALIZATIONPROPOSED_BY_REGLOCALIZATION_REFERENCE_AND_LANGUAGE);
        q.setParameter("regLocalizationReference", regLocalization);
        q.setParameter("regLanguagecode", regLangagecode);

        return (RegLocalizationproposed) q.getSingleResult();
    }
    
}
