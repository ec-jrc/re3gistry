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
package eu.europa.ec.re3gistry2.javaapi.cache.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * ContainedItem information
 */
public class BasicContainedItem implements Serializable{

    private String uri;
    private String label;
    private List <LocalizedPropertyValue> values = new ArrayList <LocalizedPropertyValue>();
    private BasicContainedItem topConceptOf;

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }
    
    public String getLabel(){
        return label;
    }
    
    public void setLabel(String label){
        this.label=label;
    }
    
    public List <LocalizedPropertyValue> getValues(){
        return values;
    }
    
    public void setValues(List <LocalizedPropertyValue> values){
        this.values=values;
    }
    
    public void addValue(LocalizedPropertyValue value){
        this.values.add(value);
    }
    
    public BasicContainedItem getTopConceptOf(){
        return topConceptOf;
    }
    
    public void setTopConceptOf(BasicContainedItem topConceptOf){
        this.topConceptOf=topConceptOf;
    }

}
