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
package eu.europa.ec.re3gistry2.restapi.format;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.xml.namespace.NamespaceContext;

public class MapNameSpaceContext implements NamespaceContext {

    protected Map<String, String> prefixToNS = new LinkedHashMap<>();
    protected Map<String, String> nsToPrefix = new LinkedHashMap<>();

    public void add(String prefix, String ns) {
        prefixToNS.putIfAbsent(prefix, ns);
        nsToPrefix.putIfAbsent(ns, prefix);
    }

    @Override
    public String getNamespaceURI(String prefix) {
        return prefixToNS.get(prefix);
    }

    @Override
    public String getPrefix(String namespaceURI) {
        return nsToPrefix.get(namespaceURI);
    }

    @Override
    public Iterator<String> getPrefixes(String namespaceURI) {
        if (namespaceURI == null) {
            throw new IllegalArgumentException();
        }
        String prefix = getPrefix(namespaceURI);
        return Collections.singletonList(prefix).iterator();
    }

}
