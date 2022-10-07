/*
 * /*
 *  * Copyright 2007,2016 EUROPEAN UNION
 *  * Licensed under the EUPL, Version 1.2 or - as soon they will be approved by
 *  * the European Commission - subsequent versions of the EUPL (the "Licence");
 *  * You may not use this work except in compliance with the Licence.
 *  * You may obtain a copy of the Licence at:
 *  *
 *  * https://ec.europa.eu/isa2/solutions/european-union-public-licence-eupl_en
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the Licence is distributed on an "AS IS" basis,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the Licence for the specific language governing permissions and
 *  * limitations under the Licence.
 *  *
 *  * Date: 2020/05/11
 *  * Authors:
 *  * European Commission, Joint Research Centre - jrc-inspire-support@ec.europa.eu
 *  * National Land Survey of Finland, SDI Services - inspire@nls.fi
 *  *
 *  * This work was supported by the Interoperability solutions for public
 *  * administrations, businesses and citizens programme (http://ec.europa.eu/isa2)
 *  * through Action 2016.10: European Location Interoperability Solutions
 *  * for e-Government (ELISE)
 */
package eu.europa.ec.re3gistry2.javaapi.cache.supplier;

import eu.europa.ec.re3gistry2.base.utility.BaseConstants;
import eu.europa.ec.re3gistry2.crudimplementation.RegFieldmappingManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegItemclassManager;
import eu.europa.ec.re3gistry2.javaapi.cache.model.ItemClass;
import eu.europa.ec.re3gistry2.model.RegFieldmapping;
import eu.europa.ec.re3gistry2.model.RegItemclass;
import java.util.HashMap;
import java.util.List;
import javax.persistence.EntityManager;

/**
 * Fetch RegItems from DB and convert them to Item objects
 */
public class XSDSchemaSupplier {

    private final EntityManager em;
    private final RegItemclassManager regItemcassManager;
    private final RegFieldmappingManager regFieldmappingManager;

    public XSDSchemaSupplier(EntityManager em,
            RegItemclass regItemclass) throws Exception {

        this.em = em;
        this.regItemcassManager = new RegItemclassManager(em);
        this.regFieldmappingManager = new RegFieldmappingManager(em);
    }

    public ItemClass getItemClass(RegItemclass regItemclass) throws Exception {
        ItemClass itemClass = new ItemClass();

        itemClass.setId(regItemclass.getLocalid());
        itemClass.setType(regItemclass.getRegItemclasstype().getLocalid());

        String baseuri;
        if (regItemclass.getBaseuri() != null) {
            baseuri = regItemclass.getBaseuri();
        } else if (regItemclass.getRegItemclassParent().getBaseuri() != null) {
            baseuri = regItemclass.getRegItemclassParent().getBaseuri();
        } else {
            baseuri = regItemclass.getRegItemclassParent().getRegItemclassParent().getBaseuri();
        }
        itemClass.setBaseuri(baseuri);

        if (regItemclass.getRegItemclassParent() != null) {
            itemClass.setParentid(regItemclass.getRegItemclassParent().getLocalid());
            itemClass.setParentItemClassType(regItemclass.getRegItemclassParent().getRegItemclasstype().getLocalid());
        }

        String startElement;
        switch (regItemclass.getRegItemclasstype().getLocalid()) {
            case BaseConstants.KEY_ITEMCLASS_TYPE_REGISTRY:
                startElement = "registry";
                break;
            case BaseConstants.KEY_ITEMCLASS_TYPE_REGISTER:
                startElement = "register";
                break;
            default:
                if (regItemclass.getRegItemclassParent().getRegItemclasstype().getLocalid().equals("register")) {
                    startElement = regItemclass.getRegItemclassParent().getLocalid();
                    itemClass.setRegisterLocalId(regItemclass.getRegItemclassParent().getLocalid());
                } else {
                    startElement = "value";
                    RegItemclass grandparent = regItemclass.getRegItemclassParent().getRegItemclassParent();
                    if (grandparent != null && grandparent.getRegItemclasstype().getLocalid().equals("register")) {
                        itemClass.setRegisterLocalId(grandparent.getLocalid());
                    }
                }
                break;
        }
        itemClass.setStartElement(startElement);

        HashMap<String, String> fields = getFieldmappings(regItemclass);
        itemClass.setFields(fields);

        List<RegItemclass> childItemClass = regItemcassManager.getChildItemclass(regItemclass);
        HashMap<String, String> childfields = new HashMap<>();

        for (RegItemclass childItemClas : childItemClass) {
            childfields.putAll(getFieldmappings(childItemClas));
        }

        itemClass.setContainedItemsFields(childfields);

        if (!childItemClass.isEmpty() && childItemClass.size() >= 1) {
            itemClass.setChildItemClassType(childItemClass.get(0).getRegItemclasstype().getLocalid());
            itemClass.setChildItemClassLocalId(childItemClass.get(0).getLocalid());
        }

        return itemClass;
    }

    private HashMap<String, String> getFieldmappings(RegItemclass regItemclass) throws Exception {
        HashMap<String, String> hash = new HashMap<>();
        List<RegFieldmapping> fieldMappingsRegItemclass = regFieldmappingManager.getAll(regItemclass);
        for (RegFieldmapping fieldMappingsRegItemclas : fieldMappingsRegItemclass) {
            String fieldType = fieldMappingsRegItemclas.getRegField().getRegFieldtype().getLocalid();
            String fieldLocalId = fieldMappingsRegItemclas.getRegField().getLocalid();
            hash.put(fieldLocalId, fieldType);
        }
        return hash;
    }

}
