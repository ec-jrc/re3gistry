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

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum ApiError {

    UUID_URI_REQUIRED(400, "bad-request", "Either uri or uuid query parameter required"),
    ITEMCLASS_REQUIRED(404, "not-found", "The requested itemclass is not available."),
    NOT_FOUND(404, "not-found", "Element not found"),
    VERSION_NOT_FOUND(404, "version-not-found", "Element with specified version not found"),
    FORMAT_NOT_SUPPORTED(406, "unknown-format", "The requested media type is not supported"),
    LANGUAGE_NOT_SUPPORTED(406, "unknown-language", "The requested language is not available"),
    INTERNAL_SERVER_ERROR(500, "internal-server-error", "The server had an internal error");

    private final ApiResponse error;

    private ApiError(int code, String descriptionCode, String description) {
        this.error = new ApiResponse(code, descriptionCode, description);
    }

    public ApiResponse getError() {
        return error;
    }

}
