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
package eu.europa.ec.ror.managers;

import eu.europa.ec.ror.model.Procedure;
import eu.europa.ec.ror.utility.Configuration;
import eu.europa.ec.ror.utility.Constants;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.Properties;
import java.util.ResourceBundle;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.w3c.dom.Document;

public class ValidationMgr {

    /**
     *
     * @param procedure
     * @param localization
     * @return
     * @throws Exception
     */
    public static JSONObject validateRegistryDescriptor(Procedure procedure, ResourceBundle localization) throws Exception {

        StringBuilder sb = DescriptorMgr.checkURL(procedure.getDescriptor().getUrl(), localization, true);

        // XML validation
        checkXML(sb, localization);

        // Registry format check and return Registry Json Object
        return processRegistryDescriptorFormat(sb, procedure, localization);
    }

    /**
     *
     * @param procedure
     * @param localization
     * @return
     * @throws Exception
     */
    public static JSONObject validateRegistrDescriptor(Procedure procedure, ResourceBundle localization) throws Exception {

        StringBuilder sb = DescriptorMgr.checkURL(procedure.getDescriptor().getUrl(), localization, true);

        // XML validation
        checkXML(sb, localization);

        // Register format check and return Register Json Object
        return processRegisterDescriptorFormat(sb, procedure, localization);
    }

    /**
     * -- Private functions -- *
     */
    private static void checkXML(StringBuilder sb, ResourceBundle localization) throws Exception {
        Logger log = Configuration.getInstance().getLogger();

        try {
            // Reading the file
            InputStream is = new ByteArrayInputStream(sb.toString().getBytes(Constants.INPUT_STREAM_DEFAULT_CHARSET));

            // Creating the XML document to check error in the XML
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(is);

            is.close();
        } catch (Exception e) {
            // Getting the erroro message
            String errorMessage = localization.getString("error.validation.xml.error") + e.getMessage();

            // Raise the exception
            log.error(errorMessage);
            throw new Exception(errorMessage);
        }
    }

    private static JSONObject processRegistryDescriptorFormat(StringBuilder in, Procedure procedure, ResourceBundle localization) throws Exception {
        Logger log = Configuration.getInstance().getLogger();
        Properties properties = Configuration.getInstance().getProperties();
        String tempDir = System.getProperty(Constants.JAVA_TMP_DIR);

        try {
            // Getting the URL of the XSL validator for the Registry Descriptors and creating temp file
            String registryValidatorPath = properties.getProperty(Constants.KEY_XSL_VALIDATOR_PATH_REGISTRY);
            File xslRegistryValidator = new File(registryValidatorPath);

            // Setting up the transformer
            TransformerFactory factory = TransformerFactory.newInstance();
            Source xslt = new StreamSource(xslRegistryValidator);
            Transformer transformer = factory.newTransformer(xslt);

            // Setting up in and out data
            InputStream is = new ByteArrayInputStream(in.toString().getBytes(Constants.INPUT_STREAM_DEFAULT_CHARSET));
            Source inData = new StreamSource(is);
            StringWriter outWriter = new StringWriter();
            StreamResult result = new StreamResult(outWriter);

            // Transforming the data
            transformer.setParameter("json", "true");
            transformer.transform(inData, result);

            // Getting the results
            StringBuffer sb = outWriter.getBuffer();
            String jsonOutput = sb.toString();

            //Closing objects
            outWriter.close();
            is.close();

            // Creating the JSON Object from the results
            JSONObject jsonObject = new JSONObject(jsonOutput);

            //Check if the validation has passed
            if (jsonObject.getJSONArray("conformanceClasses").length() <= 0) {

                is = new ByteArrayInputStream(in.toString().getBytes(Constants.INPUT_STREAM_DEFAULT_CHARSET));
                inData = new StreamSource(is);

                //Getting the HTML validation details
                outWriter = new StringWriter();
                result = new StreamResult(outWriter);
                transformer.setParameter("json", "");
                transformer.transform(inData, result);
                sb = outWriter.getBuffer();
                String htmlOutput = sb.toString();

                //Closing objects
                outWriter.close();
                is.close();

                // Raise an error
                String errorMessage = htmlOutput;
                log.error(errorMessage);
                throw new Exception(errorMessage);
            }

            // Returning the Json Object    
            return jsonObject;

        } catch (Exception e) {
            String error = "";

            // Filtering the case of HTML error
            if (e.getMessage().contains("<html")) {
                error = e.getMessage();
            } else {
                error = localization.getString("error.validation.processing.descriptor").replace("{0}", procedure.getDescriptor().getUrl()) + e.getMessage();
            }

            // Raise an error
            throw new Exception(error);
        }
    }

    private static JSONObject processRegisterDescriptorFormat(StringBuilder in, Procedure procedure, ResourceBundle localization) throws Exception {
        Logger log = Configuration.getInstance().getLogger();
        Properties properties = Configuration.getInstance().getProperties();
        String tempDir = System.getProperty(Constants.JAVA_TMP_DIR);

        try {
            // Getting the URL of the XSL validator for the Register Descriptors and creating temp file
            String registerValidatorPath = properties.getProperty(Constants.KEY_XSL_VALIDATOR_PATH_REGISTER);
            File xslRegisterValidator = new File(registerValidatorPath);

            // Setting up the transformer
            TransformerFactory factory = TransformerFactory.newInstance();
            Source xslt = new StreamSource(xslRegisterValidator);
            Transformer transformer = factory.newTransformer(xslt);

            // Setting up in and out data
            InputStream is = new ByteArrayInputStream(in.toString().getBytes(Constants.INPUT_STREAM_DEFAULT_CHARSET));
            Source inData = new StreamSource(is);
            StringWriter outWriter = new StringWriter();
            StreamResult result = new StreamResult(outWriter);

            // Transforming the data
            transformer.setParameter("json", "true");
            transformer.transform(inData, result);

            // Getting the results
            StringBuffer sb = outWriter.getBuffer();
            String jsonOutput = sb.toString();

            //Closing objects
            outWriter.close();
            is.close();

            // Creating the JSON Object from the results
            JSONObject jsonObject = new JSONObject(jsonOutput);

            //Check if the validation has passed
            if (jsonObject.getJSONArray("conformanceClasses").length() <= 0) {

                is = new ByteArrayInputStream(in.toString().getBytes(Constants.INPUT_STREAM_DEFAULT_CHARSET));
                inData = new StreamSource(is);

                //Getting the HTML validation details
                outWriter = new StringWriter();
                result = new StreamResult(outWriter);
                transformer.setParameter("json", "");
                transformer.transform(inData, result);
                sb = outWriter.getBuffer();
                String htmlOutput = sb.toString();

                //Closing objects
                outWriter.close();
                is.close();

                // Raise an error
                String errorMessage = htmlOutput;
                log.error(errorMessage);
                throw new Exception(errorMessage);
            }

            // Returning the Json Object    
            return jsonObject;

        } catch (Exception e) {
            String error = "";

            // Filtering the case of HTML error
            if (e.getMessage().contains("<html")) {
                error = e.getMessage();
            } else {
                error = localization.getString("error.validation.processing.descriptor").replace("{0}", procedure.getDescriptor().getUrl()) + e.getMessage();
            }

            // Raise an error
            throw new Exception(error);
        }
    }

    public static String getValidationHTML(Procedure procedure, ResourceBundle localization) {
        Logger log = Configuration.getInstance().getLogger();
        Properties properties = Configuration.getInstance().getProperties();
        String tempDir = System.getProperty(Constants.JAVA_TMP_DIR);

        try {
            StringBuilder in = DescriptorMgr.checkURL(procedure.getDescriptor().getUrl(), localization, true);

            // Getting the URL of the XSL validator for the Register Descriptors and creating temp file            
            String validatorPath = null;
            File xslValidator = null;
            if (procedure.getDescriptor().getDescriptortype().equals(Constants.DESCRIPTOR_TYPE_REGISTRY)) {
                validatorPath = properties.getProperty(Constants.KEY_XSL_VALIDATOR_PATH_REGISTRY);
                xslValidator = new File(validatorPath);
            } else if (procedure.getDescriptor().getDescriptortype().equals(Constants.DESCRIPTOR_TYPE_REGISTER)) {
                validatorPath = properties.getProperty(Constants.KEY_XSL_VALIDATOR_PATH_REGISTER);
                xslValidator = new File(validatorPath);
            } else {
                throw new Exception();
            }

            // Setting up the transformer
            TransformerFactory factory = TransformerFactory.newInstance();
            Source xslt = new StreamSource(xslValidator);
            Transformer transformer = factory.newTransformer(xslt);

            // Setting up in and out data
            InputStream is = new ByteArrayInputStream(in.toString().getBytes(Constants.INPUT_STREAM_DEFAULT_CHARSET));
            Source inData = new StreamSource(is);
            StringWriter outWriter = new StringWriter();
            StreamResult result = new StreamResult(outWriter);

            is = new ByteArrayInputStream(in.toString().getBytes(Constants.INPUT_STREAM_DEFAULT_CHARSET));
            inData = new StreamSource(is);

            //Getting the HTML validation details
            outWriter = new StringWriter();
            result = new StreamResult(outWriter);
            transformer.setParameter("json", "");
            transformer.transform(inData, result);
            StringBuffer sb = outWriter.getBuffer();
            String htmlOutput = sb.toString();

            //Closing objects
            outWriter.close();
            is.close();

            return htmlOutput;

        } catch (Exception e) {
            return null;
        }
    }
}
