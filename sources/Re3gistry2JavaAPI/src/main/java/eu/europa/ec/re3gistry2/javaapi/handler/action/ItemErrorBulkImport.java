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
package eu.europa.ec.re3gistry2.javaapi.handler.action;

public class ItemErrorBulkImport {

    private String itemLocalID;
    private String language;
    private int line;
    private String message;

    public ItemErrorBulkImport(String itemLocalID, String language, int line, String message) {
        itemLocalID = (itemLocalID != null) ? itemLocalID : "-";
        language = (language != null) ? language : "-";
        message = (message != null) ? message : "-";

        this.itemLocalID = itemLocalID;
        this.language = language;
        this.line = line;
        this.message = message;
    }

    public String getItemLocalID() {
        return itemLocalID;
    }

    public void setItemLocalID(String itemLocalID) {
        itemLocalID = (itemLocalID != null) ? itemLocalID : "-";
        this.itemLocalID = itemLocalID;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        language = (language != null) ? language : "-";
        this.language = language;
    }

    public int getLine() {
        return line;
    }

    public void setLine(int line) {
        this.line = line;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        message = (message != null) ? message : "-";
        this.message = message;
    }

}
