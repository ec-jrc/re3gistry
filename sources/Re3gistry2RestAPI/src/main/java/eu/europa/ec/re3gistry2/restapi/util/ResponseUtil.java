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
package eu.europa.ec.re3gistry2.restapi.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.http.HttpServletResponse;

import eu.europa.ec.re3gistry2.model.RegLanguagecode;
import eu.europa.ec.re3gistry2.restapi.ApiError;
import eu.europa.ec.re3gistry2.restapi.ApiResponse;
import eu.europa.ec.re3gistry2.restapi.format.Formatter;
import eu.europa.ec.re3gistry2.restapi.format.JSONInternalFormatter;
import eu.europa.ec.re3gistry2.javaapi.cache.model.Item;

public class ResponseUtil {

    public static void ok(HttpServletResponse resp, Item item, RegLanguagecode lang, Formatter formatter) throws Exception {
        int sc = HttpServletResponse.SC_OK;
        String type = formatter.getContentType();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        formatter.write(item, lang, baos);
        send(resp, sc, type, baos);
    }

    public static void ok(HttpServletResponse resp, ApiResponse value) throws Exception {
        int sc = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
        String type = "application/json";
        byte[] body = JSONInternalFormatter.OM.writeValueAsBytes(value);
        send(resp, sc, type, body);
    }

    public static void err(HttpServletResponse resp, ApiError err) throws IOException {
        int sc = err.getError().getCode();
        String type = "application/json";
        byte[] body = JSONInternalFormatter.OM.writeValueAsBytes(err);
        send(resp, sc, type, body);
    }

    private static void send(HttpServletResponse resp, int sc, String type, byte[] body) throws IOException {
        resp.setStatus(sc);
        resp.setContentType(type);
        resp.setContentLength(body.length);
        try (OutputStream out = resp.getOutputStream()) {
            out.write(body);
        }
    }

    private static void send(HttpServletResponse resp, int sc, String type, ByteArrayOutputStream baos) throws IOException {
        resp.setStatus(sc);
        resp.setContentType(type);
        resp.setContentLength(baos.size());
        try (OutputStream out = resp.getOutputStream()) {
            baos.writeTo(out);
        }
    }

}
