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
 *  * through Action 2016.10: European Location Interoperability Solutions for e-Government (ELISE)
 *  * for e-Government (ELISE)
 */
package eu.europa.ec.re3gistry2.javaapi.cache.model;

import java.io.Serializable;
import java.util.List;

public class LocalizedProperty implements Serializable{

    private final String lang;
    private final String id;
    private final String istitle;
    private final String label;
    private final List<LocalizedPropertyValue> values;
    private final int order;
    private final String tablevisible;

    public LocalizedProperty(String lang, String id, boolean istitle,
            String label, List<LocalizedPropertyValue> values, int order, boolean tablevisible) {
        this.lang = lang;
        this.id = id;
        this.istitle = istitle ? "true" : null;
        this.label = label;
        this.values = values;
        this.order = order;
        this.tablevisible = tablevisible ? "true" : "null";
    }

    public String getLang() {
        return lang;
    }

    public String getId() {
        return id;
    }

    public String getIstitle() {
        return istitle;
    }

    public String getLabel() {
        return label;
    }

    public List<LocalizedPropertyValue> getValues() {
        return values;
    }

    public int getOrder() {
        return order;
    }

    public String getTablevisible() {
        return tablevisible;
    }

}
