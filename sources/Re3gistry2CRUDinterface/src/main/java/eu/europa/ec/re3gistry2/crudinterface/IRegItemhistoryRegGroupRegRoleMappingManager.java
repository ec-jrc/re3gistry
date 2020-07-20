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

import eu.europa.ec.re3gistry2.model.RegGroup;
import eu.europa.ec.re3gistry2.model.RegItemRegGroupRegRoleMapping;
import eu.europa.ec.re3gistry2.model.RegItemhistory;
import eu.europa.ec.re3gistry2.model.RegItemhistoryRegGroupRegRoleMapping;
import eu.europa.ec.re3gistry2.model.RegRole;
import java.util.List;
import java.util.Map;

public interface IRegItemhistoryRegGroupRegRoleMappingManager{
    
    public RegItemhistoryRegGroupRegRoleMapping get(String uuid) throws Exception;
    public List<RegItemhistoryRegGroupRegRoleMapping> getAll() throws Exception;
    
    public boolean add(RegItemhistoryRegGroupRegRoleMapping i) throws Exception;
    public boolean update(RegItemhistoryRegGroupRegRoleMapping i) throws Exception;
    public boolean delete(RegItemhistoryRegGroupRegRoleMapping i) throws Exception;
    
    public List<RegItemhistoryRegGroupRegRoleMapping> getAll(RegGroup regGroup) throws Exception;
    public List<RegItemhistoryRegGroupRegRoleMapping> getAll(RegItemhistory regItemhistory, RegRole regRole) throws Exception;
    public List<RegItemhistoryRegGroupRegRoleMapping> getAll(RegItemhistory regItemhistory) throws Exception;
    public List<RegItemhistoryRegGroupRegRoleMapping> getAll(RegRole regRole) throws Exception;
    public RegItemhistoryRegGroupRegRoleMapping getByReference(RegItemRegGroupRegRoleMapping regItemRegGroupRegRoleMapping) throws Exception;
    public List<RegItemhistoryRegGroupRegRoleMapping> getAllNew(RegItemhistory regItemhistory, RegRole regRole) throws Exception;
    public List<RegItemhistoryRegGroupRegRoleMapping> getAll(Map<String, RegGroup> regGroups) throws Exception;
    
}