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
package eu.europa.ec.re3gistry2.model.uuidhandlers;

import eu.europa.ec.re3gistry2.model.RegRelationhistory;
import eu.europa.ec.re3gistry2.model.RegItemhistory;
import eu.europa.ec.re3gistry2.model.RegItem;
import eu.europa.ec.re3gistry2.model.RegRelationpredicate;
import eu.europa.ec.re3gistry2.model.utility.UuidHelper;


public class RegRelationhistoryUuidHelper {
    
    private RegRelationhistoryUuidHelper(){
    }
    
    public static String getUuid(RegItemhistory regItemhistorySubject, RegItem regItemSubject, RegRelationpredicate regRelationPredicate, RegItemhistory regItemhistoryObject, RegItem regItemObject) throws Exception{
        
        String[] params = new String[5];
        params[0] = (regItemhistorySubject!=null)? regItemhistorySubject.getUuid() : "";
        params[1] = (regItemSubject!=null)? regItemSubject.getUuid() : "";
        params[2] = (regRelationPredicate!=null)? regRelationPredicate.getUuid() : "";
        params[3] = (regItemhistoryObject!=null)? regItemhistoryObject.getUuid() : "";
        params[4] = (regItemObject!=null)? regItemObject.getUuid() : "";
        
        return UuidHelper.createUuid(params, RegRelationhistory.class);
    } 
    
}
