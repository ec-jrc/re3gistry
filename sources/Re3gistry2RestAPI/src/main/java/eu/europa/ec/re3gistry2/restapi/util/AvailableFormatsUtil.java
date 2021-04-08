/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.europa.ec.re3gistry2.restapi.util;

import java.util.ArrayList;
import java.util.List;

public class AvailableFormatsUtil {

    private final List<String> formatList = new ArrayList<>();

    public AvailableFormatsUtil() {
        formatList.add("html");
        formatList.add("json");
        formatList.add("xml");
        formatList.add("iso19135xml");
        formatList.add("rdf");
        formatList.add("csv");
    }

    public List<String> getFormatList() {
        return formatList;
    }

}
