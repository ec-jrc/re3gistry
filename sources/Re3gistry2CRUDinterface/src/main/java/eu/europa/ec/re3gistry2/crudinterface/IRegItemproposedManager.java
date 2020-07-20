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
import eu.europa.ec.re3gistry2.model.RegItem;
import eu.europa.ec.re3gistry2.model.RegItemproposed;
import eu.europa.ec.re3gistry2.model.RegItemclass;
import eu.europa.ec.re3gistry2.model.RegItemclasstype;
import eu.europa.ec.re3gistry2.model.RegRelationpredicate;
import eu.europa.ec.re3gistry2.model.RegUser;
import java.util.List;

public interface IRegItemproposedManager{
    
    public RegItemproposed get(String uuid) throws Exception;
    public List<RegItemproposed> getAll() throws Exception;
    public RegItemproposed getByLocalidAndRegItemClass(String localid, RegItemclass regItemclass) throws Exception;
    public List<RegItemproposed> getAll(RegItemclasstype regItemcalsstype) throws Exception;
    public List<RegItemproposed> getAll(RegItemclass regItemcalss) throws Exception;
    public List<RegItemproposed> getAll(List<RegItemclass> regItemcalsses, int start, int maxResults) throws Exception;
    public int countAll(List<RegItemclass> regItemcalsses) throws Exception;
    public List<RegItemproposed> getAllNew(List<RegItemclass> regItemcalsses, int start, int maxResults) throws Exception;
    public int countAllNew(List<RegItemclass> regItemcalsses) throws Exception;
    public List<RegItemproposed> getAll(RegRelationpredicate regRelationpredicate, RegItemproposed regItemObject, int start, int length) throws Exception;
    public int countAll(RegRelationpredicate regRelationpredicate, RegItemproposed regItemObject) throws Exception;
    
    public List<RegItemproposed> getAllNew(List<RegItemclass> regItemcalsses, RegItem collection, RegRelationpredicate regRelationpredicateCollection, int start, int maxResults) throws Exception;
    public int countAllNew(List<RegItemclass> regItemcalsses, RegItem collection, RegRelationpredicate regRelationpredicateCollection) throws Exception;
    
    public boolean add(RegItemproposed i) throws Exception;
    public boolean update(RegItemproposed i) throws Exception;
    public boolean delete(RegItemproposed i) throws Exception;
    
    public RegItemproposed getByRegItemReference(RegItem regItemReference) throws Exception;
    public List<RegItemproposed> getAll(RegUser regUser) throws Exception;
    public List<RegItemproposed> getAll(RegUser regUser, int start, int length) throws Exception;
    public int countAll(RegUser regUser) throws Exception;
    public List<RegItemproposed> getAll(RegAction regAction) throws Exception;
    
}