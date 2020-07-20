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
package eu.europa.ec.re3gistry2.crudinterface;

import eu.europa.ec.re3gistry2.model.RegAction;
import eu.europa.ec.re3gistry2.model.RegField;
import eu.europa.ec.re3gistry2.model.RegItemproposed;
import eu.europa.ec.re3gistry2.model.RegItemclass;
import eu.europa.ec.re3gistry2.model.RegLanguagecode;
import eu.europa.ec.re3gistry2.model.RegLocalization;
import eu.europa.ec.re3gistry2.model.RegLocalizationproposed;
import java.util.List;

public interface IRegLocalizationproposedManager{
    
    public RegLocalizationproposed get(String uuid) throws Exception;
    public List<RegLocalizationproposed> getAll() throws Exception;
    
    public boolean add(RegLocalizationproposed i) throws Exception;
    public boolean update(RegLocalizationproposed i) throws Exception;
    public boolean delete(RegLocalizationproposed i) throws Exception;
    
    public List<RegLocalizationproposed> getAll(RegItemproposed regItem) throws Exception;
    public List<RegLocalizationproposed> getAll(RegItemclass regItemclass) throws Exception;
    public List<RegLocalizationproposed> getAll(RegField regField) throws Exception;
    public List<RegLocalizationproposed> getAll(RegField regField, RegItemproposed regItem) throws Exception;
    public List<RegLocalizationproposed> getAllNull(RegField regField, RegItemproposed regItemproposed) throws Exception;
    public RegLocalizationproposed get(RegField regField, RegLanguagecode regLanguagecode) throws Exception;
    public List<RegLocalizationproposed> getAll(RegItemproposed regItemproposed, RegLanguagecode regLanguagecode) throws Exception;
    public List<RegLocalizationproposed> getAllRelationReference(RegItemproposed regItemproposed, RegLanguagecode regLanguagecode) throws Exception;
    public List<RegLocalizationproposed> getAll(RegField regField, RegItemproposed regItem, RegLanguagecode regLanguagecode) throws Exception;
    public RegLocalizationproposed getByRegLocalizationReferenceAndLanguage(RegLocalization regLocalization, RegLanguagecode regLangagecode) throws Exception;
    public List<RegLocalizationproposed> getAllNew(RegField regField, RegItemproposed regItemproposed, RegLanguagecode regLanguagecode) throws Exception;
    public List<RegLocalizationproposed> getAll(RegField regField, RegItemproposed regItemproposed, RegAction regAction) throws Exception;
       
}