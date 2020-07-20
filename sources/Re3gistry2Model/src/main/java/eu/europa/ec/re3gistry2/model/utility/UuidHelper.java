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
package eu.europa.ec.re3gistry2.model.utility;

import java.security.MessageDigest;

public class UuidHelper {
    
    private UuidHelper(){
    }
    
    /**
     * Create UUID for a generic object with multiple parameters.
     * Parameters:
     * @param parameters an array of arbitrary strings parameters to create the Uuid
     * @param objectType the object's class
     * @return
     * @throws Exception
     */
    public static String createUuid(String [] parameters, Class objectType) throws Exception {
        String processedParameters = "";     
        for(String tmpParameter : parameters){
            processedParameters += tmpParameter;
        }        
        return getMD5(normalize(processedParameters), objectType);
    }

    /**
     * Create UUID for a generic object with multiple parameters.
     * Parameters:
     * @param parameter a parameter to create the Uuid
     * @param objectType the object's class
     * @return
     * @throws Exception
     */
    public static String createUuid(String parameter, Class objectType) throws Exception {
        return (parameter != null) ? getMD5(normalize(parameter), objectType) : null;
    }

    /**
     * Converts a string to a MD5 hash
     * @param in the string to be converted
     * @param objectType the type of the object
     * @return out the in string converted in MD5 hash
     * @throws Exception
     */
    public static String getMD5(String in, Class objectType) throws Exception {
        String out = null;
        in = objectType.getName() + "-" + in;
        try {
            MessageDigest md = MessageDigest.getInstance(ModelConstants.MD5_MESSAGE_DIGEST_KEY);
            md.update(in.getBytes());

            byte [] byteData = md.digest();

            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < byteData.length; i++) {
                sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
            }
            out = sb.toString();

        } catch (Exception e) {
            throw new Exception("@@ Error while calculating MD5 digest for string: " + in + " - " + e.getMessage());
        }

        return out;
    }

    /**
     * Normalizes the string given as input: it trim the string and it removes any spaces;     *
     * @param in the string to be converted
     * @return out the in string normalized
     */
    public static String normalize(String in) {
        return (in != null) ? in.trim().replace(" ", "") : null;
    }

}
