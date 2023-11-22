/*
 * Copyright 2010,2015 EUROPEAN UNION
 * Licensed under the EUPL, Version 1.1 or - as soon they will be approved by
 * the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 * http://ec.europa.eu/idabc/eupl
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 *
 * Date: __/__/____
 * Authors: European Commission, Joint Research Centre
 * inspire-registry-dev@jrc.ec.europa.eu
 *
 * This work was supported by the EU  Interoperability Solutions for
 * European Public Administrations Programme (http://ec.europa.eu/isa)
 * through Action 1.17: Re-usable INSPIRE Reference Platform 
 */
package eu.europa.ec.ror.utility;

import java.security.MessageDigest;

public class StringUtils {

    /**
     * Converts a string to a MD5 hash
     *
     * @param in the string to be converted
     * @return out the in string converted in MD5 hash
     * @throws Exception
     */
    public static String getMD5(String in) throws Exception {
        String out = null;
        try {
            MessageDigest md = MessageDigest.getInstance(Constants.MD5_MESSAGE_DIGEST_KEY);
            md.update(in.getBytes());

            byte byteData[] = md.digest();

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
     * Normalizes the string given as input: removes any spaces and convert it
     * in lowercase;
     *
     * @param in the string to be converted
     * @return out the in string normalized
     */
    public static String normalize(String in) {
        return (in != null) ? in.trim().replace(" ", "") : null;
    }

    /**
     * Converts a string to a UUID: converts the string in lowercase and removes
     * any spaces; finally converts it to MD5
     *
     * @param in the string to be converted
      * @return out the in string converted as UUID
     * @throws Exception
     */
    public static String createUUID(String in) throws Exception {
        return (in != null) ? getMD5(normalize(in)) : null;
    }

    /**
     * Converts a string to a code
     *
     * @param in the string to be converted
     * @param replaceRegex the tegular expression to find the character to
     * replace
     * @return out the in string with no space and special chars replaced
     * @throws Exception
     */
    public static String generateCode(String in, String replaceRegex) throws Exception {
        String out = null;
        try {
            out = in.replaceAll(replaceRegex, "");
        } catch (Exception e) {
            throw new Exception("@@ Error while generating code for string: " + in + " - " + e.getMessage());
        }

        return out;
    }

    /**
     *
     * @param word the string where to replace the spacial characters
     * @return the new word with the special character replaced
     */
    public String replaceSpecialCharacters(String word) {
        if (word != null) {
            String wordWithoutSpacialCharacters = word.replace("&", "&amp;").replace("‘", "&apos;").replace("“", "&quot;").replace("<", "&lt;").replace(">", "&gt;");
            return wordWithoutSpacialCharacters;
        } else {
            return word;
        }
    }
}
