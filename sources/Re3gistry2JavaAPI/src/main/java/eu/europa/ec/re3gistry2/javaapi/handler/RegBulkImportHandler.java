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
package eu.europa.ec.re3gistry2.javaapi.handler;

import eu.europa.ec.re3gistry2.base.utility.BaseConstants;
import eu.europa.ec.re3gistry2.base.utility.Configuration;
import eu.europa.ec.re3gistry2.base.utility.InputSanitizerHelper;
import eu.europa.ec.re3gistry2.base.utility.MailManager;
import eu.europa.ec.re3gistry2.base.utility.PersistenceFactory;
import eu.europa.ec.re3gistry2.base.utility.exceptions.ExceptionConstants;
import eu.europa.ec.re3gistry2.base.utility.exceptions.UnauthorizedUserException;
import eu.europa.ec.re3gistry2.crudimplementation.RegActionManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegFieldManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegFieldmappingManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegGroupManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegItemManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegItemRegGroupRegRoleMappingManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegItemclassManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegItemproposedManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegItemproposedRegGroupRegRoleMappingManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegLanguagecodeManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegLocalizationManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegLocalizationproposedManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegRelationManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegRelationpredicateManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegRelationproposedManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegStatusManager;
import eu.europa.ec.re3gistry2.javaapi.handler.action.CsvHeader;
import eu.europa.ec.re3gistry2.javaapi.handler.action.FieldsBulkImport;
import eu.europa.ec.re3gistry2.model.RegAction;
import eu.europa.ec.re3gistry2.model.RegField;
import eu.europa.ec.re3gistry2.model.RegFieldmapping;
import eu.europa.ec.re3gistry2.model.RegGroup;
import eu.europa.ec.re3gistry2.model.RegItem;
import eu.europa.ec.re3gistry2.model.RegItemRegGroupRegRoleMapping;
import eu.europa.ec.re3gistry2.model.RegItemclass;
import eu.europa.ec.re3gistry2.model.RegItemproposed;
import eu.europa.ec.re3gistry2.model.RegItemproposedRegGroupRegRoleMapping;
import eu.europa.ec.re3gistry2.model.RegLanguagecode;
import eu.europa.ec.re3gistry2.model.RegLocalization;
import eu.europa.ec.re3gistry2.model.RegLocalizationproposed;
import eu.europa.ec.re3gistry2.model.RegRelation;
import eu.europa.ec.re3gistry2.model.RegRelationpredicate;
import eu.europa.ec.re3gistry2.model.RegRelationproposed;
import eu.europa.ec.re3gistry2.model.RegStatus;
import eu.europa.ec.re3gistry2.model.RegUser;
import eu.europa.ec.re3gistry2.model.uuidhandlers.RegActionUuidHelper;
import eu.europa.ec.re3gistry2.model.uuidhandlers.RegItemUuidHelper;
import eu.europa.ec.re3gistry2.model.uuidhandlers.RegItemproposedRegGroupRegRoleMappingUuidHelper;
import eu.europa.ec.re3gistry2.model.uuidhandlers.RegItemproposedUuidHelper;
import eu.europa.ec.re3gistry2.model.uuidhandlers.RegLocalizationproposedUuidHelper;
import eu.europa.ec.re3gistry2.model.uuidhandlers.RegRelationproposedUuidHelper;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Set;
import javax.mail.internet.InternetAddress;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.logging.log4j.Logger;

public class RegBulkImportHandler {

    // Init LOGGER
    private final Logger LOGGER;

    // Setup the entity manager
    private final EntityManager entityManager;

    private String operationResult;

    private final HttpServletResponse response;
    private final HttpServletRequest request;
    private final ResourceBundle systemLocalization;

    // Synchronization object
    private static final Object sync = new Object();

    private final String BR_HTML = "<br/>";
    private final String SUBSTITUTE_LOCALID = "{localid}";
    private final String SUBSTITUTE_LINE = "{line}";
    private final String SUBSTITUTE_LANGUAGE = "{language}";
    private final String SUBSTITUTE_ITEMLOCALID = "{itemLocalID}";

    public RegBulkImportHandler(HttpServletRequest request, HttpServletResponse response) throws Exception {
        entityManager = PersistenceFactory.getEntityManagerFactory().createEntityManager();
        LOGGER = Configuration.getInstance().getLogger();

// System localization
        systemLocalization = Configuration.getInstance().getLocalization();

        this.request = request;
        this.response = response;
    }

    public void createFileTemplate(RegItemclass regItemclass, EntityManager entityManager) throws Exception {
        RegItemclassManager regItemclassManager = new RegItemclassManager(entityManager);
        RegFieldmappingManager regFieldmappingManager = new RegFieldmappingManager(entityManager);
        List<RegFieldmapping> regFieldMappingListWithoutSomeFields = new ArrayList<>();

        //Get itemclass with parent regItemclass
        List<RegItemclass> child = regItemclassManager.getChildItemclass(regItemclass);
        RegItemclass regItemclassChild = child.get(0);
        List<RegFieldmapping> regFieldMappingList = regFieldmappingManager.getAllOrderAscByListorder(regItemclassChild);

        for (RegFieldmapping regFieldMapping : regFieldMappingList) {
            final String regFieldUuid = regFieldMapping.getRegField().getRegFieldtype().getUuid();
            if (checkIfFieldTypeIsParentReferenceOrString(regFieldUuid)) {
                regFieldMappingListWithoutSomeFields.add(regFieldMapping);
            }  
        }

        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment;filename=" + regItemclassChild.getLocalid() + ".csv");

        try ( ServletOutputStream output = response.getOutputStream()) {
            StringBuilder sb = generateCsvFileBuffer(regFieldMappingListWithoutSomeFields);
            InputStream input = new ByteArrayInputStream(sb.toString().getBytes("UTF-8"));

            byte[] buffer = new byte[10240];
            for (int length = 0; (length = input.read(buffer)) > 0;) {
                output.write(buffer, 0, length);
            }

            input.close();
            output.flush();
        } catch (Exception ex) {
            throw new Exception(ex.getMessage());
        }
    }

    private static StringBuilder generateCsvFileBuffer(List<RegFieldmapping> regFieldMappingListWithoutStatus) {
        StringBuilder writer = new StringBuilder();

        writer.append(BaseConstants.KEY_BULK_LOCALID);
        writer.append("|");
        writer.append(BaseConstants.KEY_BULK_LANGUAGE);

        //Add all the fields in the order of order list
        for (RegFieldmapping field : regFieldMappingListWithoutStatus) {
            writer.append("|");
            writer.append(field.getRegField().getLocalid());
        }

        return writer;
    }

    public void processFile(HttpServletRequest request, RegItem regItem, RegUser regUser) throws Exception {
        // Get configuration properties
        final Properties properties = Configuration.getInstance().getProperties();

        // Prepare the email email to the user with the generated key
        String recipientString = regUser.getEmail();
        InternetAddress[] recipient = {
            new InternetAddress(recipientString)
        };

        String registryLink = properties.getProperty(BaseConstants.KEY_MAIL_APPLICATION_ROOTURL);
        String subject = null;
        String body = null;
        operationResult = "";

        String isBulkEditString = request.getParameter(BaseConstants.KEY_REQUEST_ISBULKEDIT);
        Boolean isBulkEdit = Boolean.parseBoolean(isBulkEditString);
        try {
            String[] linesFile = readFileFromServletFileUpload(request);
            if (linesFile != null && linesFile.length != 0) {
                List<String> fileList = new ArrayList<>(Arrays.asList(linesFile));

                ArrayList<String> additionLines = new ArrayList<>(Arrays.asList(Arrays.copyOfRange(linesFile, 1, linesFile.length)));
                additionLines.removeAll(Collections.singleton(null));
                additionLines.removeAll(Collections.singleton(""));

                String[] headerLineSplitted = fileList.get(0).split("\\|", -1);
                List<String> headerListSplitted = new ArrayList<>(Arrays.asList(headerLineSplitted));
                LOGGER.info("###");
                LOGGER.info("### ANALIZE FILE ###");
                HashMap<String, ArrayList<FieldsBulkImport>> itemsBulkImport = analyzeFile(headerListSplitted, additionLines, regItem);
                
                LOGGER.info("### THE FILE HAS BEEN ANALIZED ###");
                LOGGER.info("###");
                LOGGER.info("### START STORING ITEMS ###");
                try {
                    if(itemsBulkImport != null && !itemsBulkImport.isEmpty()) {
                        ArrayList<String> emptyFields = checkRequired(additionLines, regItem);
  
                        if(!emptyFields.isEmpty()){
                            operationResult = "<b>" + systemLocalization.getString("bulk.import.error.emptyrequired")
                                    .replace("{fields}", "<b>" + emptyFields.get(0) + "</b>")
                                    .replace("{line}", "<b>" + emptyFields.get(1) + "</b>")
                                    + "</b>" + BR_HTML + operationResult;
                            request.setAttribute(BaseConstants.KEY_REQUEST_BULK_ERROR, operationResult);
                            subject = systemLocalization.getString(BaseConstants.KEY_EMAIL_SUBJECT_BULKIMPORT_ERROR);
                            body = systemLocalization.getString(BaseConstants.KEY_EMAIL_BODY_BULKIMPORT_ERROR);
                        }
                        else{
                            storeItems(itemsBulkImport, regItem, regUser, request, additionLines, isBulkEdit);
                            LOGGER.info("### END STORING ITEMS WITH SUCCESS ###");
                            LOGGER.info("###");
                            if (operationResult.isEmpty()) {
                                operationResult = "<b>" + systemLocalization.getString("bulk.import.success") + "</b>";
                                request.setAttribute(BaseConstants.KEY_REQUEST_BULK_SUCCESS, operationResult);

                                subject = systemLocalization.getString(BaseConstants.KEY_EMAIL_SUBJECT_BULKIMPORT_SUCCESS);
                                body = systemLocalization.getString(BaseConstants.KEY_EMAIL_BODY_BULKIMPORT_SUCCESS);
                            }
                            request.setAttribute(BaseConstants.KEY_REQUEST_BULK_SUCCESS, operationResult);
                            subject = systemLocalization.getString(BaseConstants.KEY_EMAIL_SUBJECT_BULKIMPORT_SUCCESS);
                            body = systemLocalization.getString(BaseConstants.KEY_EMAIL_BODY_BULKIMPORT_SUCCESS);
                        }
                        
                    }
                } catch (Exception ex) {
                    request.setAttribute(BaseConstants.KEY_REQUEST_BULK_ERROR, operationResult);
                    subject = systemLocalization.getString(BaseConstants.KEY_EMAIL_SUBJECT_BULKIMPORT_ERROR);
                    body = systemLocalization.getString(BaseConstants.KEY_EMAIL_BODY_BULKIMPORT_ERROR);
                }
            } else {
                operationResult = "<b>" + systemLocalization.getString("bulk.import.error.emptyfile") + "</b>" + BR_HTML + operationResult;
                request.setAttribute(BaseConstants.KEY_REQUEST_BULK_ERROR, operationResult);
                subject = systemLocalization.getString(BaseConstants.KEY_EMAIL_SUBJECT_BULKIMPORT_ERROR);
                body = systemLocalization.getString(BaseConstants.KEY_EMAIL_BODY_BULKIMPORT_ERROR);
            }

        } catch (Exception ex) {
            request.setAttribute(BaseConstants.KEY_REQUEST_BULK_ERROR, operationResult);

            subject = systemLocalization.getString(BaseConstants.KEY_EMAIL_SUBJECT_BULKIMPORT_ERROR);
            body = systemLocalization.getString(BaseConstants.KEY_EMAIL_BODY_BULKIMPORT_ERROR);
        }

        subject = (subject != null)
                ? subject.replace("{itemclass}", regItem.getRegItemclass().getLocalid())
                : "";

        body = (body != null)
                ? body.replace("{name}", regUser.getName())
                        .replace("{email}", regUser.getEmail())
                        .replace("{errors}", operationResult)
                        .replace("{registry_link}", registryLink)
                : "";
        // Sending the mail
        MailManager.sendMail(recipient, subject, body);
    }

    private boolean isEdit(RegItem regItem) {

        return false;
    }

    private ArrayList<String> checkRequired(List<String> additionLines, RegItem regItem) {
        RegFieldmappingManager regFieldmappingManager = new RegFieldmappingManager(entityManager);
        List<RegFieldmapping> regFieldMappingList = new ArrayList<>();
        ArrayList<String> emptyFields = new ArrayList();
        try {
            RegItemclass regItemclassChild = getItemClassChildren(regItem);
            regFieldMappingList = regFieldmappingManager.getAll(regItemclassChild);
            regFieldMappingList.remove(2);

            for (int i = 0; i < additionLines.size(); i++) {
                String[] addLineSplitted = additionLines.get(i).split("\\|", -1);
                List<String> linesListSplitted = new ArrayList<>(Arrays.asList(addLineSplitted));
                /* if (linesListSplitted.get(0).trim().equalsIgnoreCase("")) {
                    emptyFields.add("LocalId");
                    emptyFields.add(Integer.toString(i + 1));
                    return emptyFields;
                }

                if (linesListSplitted.get(1).trim().equalsIgnoreCase("")) {
                    emptyFields.add("Language");
                    emptyFields.add(Integer.toString(i + 1));
                    return emptyFields;
                }*/

                linesListSplitted.remove(1);
                linesListSplitted.remove(0);
                for (int j = 0; j < linesListSplitted.size(); j++) {
                    if (regFieldMappingList.get(j).getRequired() == true && linesListSplitted.get(j).equalsIgnoreCase("")) {
                        emptyFields.add(regFieldMappingList.get(j).getRegField().getLocalid());
                        emptyFields.add(Integer.toString(i + 1));
                        return emptyFields;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return emptyFields;
    }

    private String[] readFileFromServletFileUpload(HttpServletRequest request) throws IOException, FileUploadException {
        List<FileItem> fl = new ServletFileUpload(new DiskFileItemFactory()).parseRequest(request);
        FileItem dataFile = null;
        for (FileItem item : fl) {
            if (!item.isFormField()) {
                dataFile = item;
                break;
            }
        }

        try {
            if (dataFile != null) {
                InputStream inputStream = dataFile.getInputStream();
                StringWriter writer = new StringWriter();
                IOUtils.copy(inputStream, writer, "UTF-8");
                String entireFile = writer.toString();
                return entireFile.split("\\r?\\n");
            } else {
                String error_header = systemLocalization.getString("bulk.import.error.file");
                operationResult = operationResult + BR_HTML + error_header;
                return new String[0];
            }
        } catch (IOException ex) {
            String error_header = systemLocalization.getString("bulk.import.error.file");
            operationResult = operationResult + BR_HTML + error_header;
            return new String[0];
        }
    }

    private HashMap<String, ArrayList<FieldsBulkImport>> analyzeFile(List<String> headerListSplitted, ArrayList<String> additionLines, RegItem regItem) throws Exception {
        LOGGER.info("###");
        LOGGER.info("### ANALIZE HEADER ###");
        CsvHeader csvHeader = analyzeHeader(headerListSplitted, regItem);
        LOGGER.info("### END ANALIZING HEADER WITH SUCCESS ###");
        LOGGER.info("###");
        LOGGER.info("### START ANALIZING ADDITION LINES ###");
        HashMap<String, ArrayList<FieldsBulkImport>> itemsBulkImport = analyzeRows(csvHeader, additionLines, regItem);
        LOGGER.info("###");
        LOGGER.info("### ANALYSIS HAS BEEN COMPLETED WITH SUCCESS ###");
        LOGGER.info("###");
        return itemsBulkImport;
    }

    private CsvHeader analyzeHeader(List<String> headerListSplitted, RegItem regItem) throws Exception {
        LOGGER.info("# Format check for " + regItem.getRegItemclass().getLocalid());
        LOGGER.info("#");
        LOGGER.info("# Checking the CSV header.");

        CsvHeader csvHeader = new CsvHeader();
        checkHeaderLocalIDSpealling(headerListSplitted, csvHeader);
        checkHeaderLanguageSpealling(headerListSplitted, csvHeader);
        LOGGER.info("#");
        LOGGER.info("# Checking the CSV header.");
        checkHeaderFields(headerListSplitted, csvHeader, regItem);

        return csvHeader;
    }

    private void checkHeaderFields(List<String> headerListSplitted, CsvHeader csvHeader, RegItem regItem) throws Exception {
        ArrayList<Object> fieldsHeader = new ArrayList<>();
        StringBuilder wrongFields = findWrongFields(headerListSplitted, fieldsHeader);
        csvHeader.setRegFields(fieldsHeader);

        String expectedFields = "";
        if (wrongFields.length() == 0) {
            LOGGER.info("# CSV header check complete.");
            LOGGER.info("#");
        } else {
            LOGGER.error("# CSV header check complete with ERRORS.");
            LOGGER.info("#");

            RegItemclass regItemclassChild = null;

            RegItemclassManager regItemclassManager = new RegItemclassManager(entityManager);
            List<RegItemclass> child = regItemclassManager.getChildItemclass(regItem.getRegItemclass());
            regItemclassChild = child.get(0);
            RegFieldmappingManager regFieldmappingManager = new RegFieldmappingManager(entityManager);
            List<RegFieldmapping> fielsMapping = regFieldmappingManager.getAll(regItemclassChild);
            for (RegFieldmapping regFieldmapping : fielsMapping) {
                expectedFields += regFieldmapping.getRegField().getLocalid() + " | ";
            }

            String error_header = systemLocalization.getString("bulkimport.error.header")
                    .replace("{fields}", "<b>" + wrongFields + "</b>")
                    .replace("{expectedFields}", "<b>" + expectedFields + "</b>");
            if (!operationResult.isEmpty()) {
                operationResult = operationResult + BR_HTML;
            }
            operationResult = operationResult + error_header;
            throw new Exception(operationResult);
        }
    }

    private StringBuilder findWrongFields(List<String> headerListSplitted, ArrayList<Object> fieldsHeader) {
        RegFieldManager regFieldManager = new RegFieldManager(entityManager);

        StringBuilder wrongFields = new StringBuilder();
        for (int i = 2; i < headerListSplitted.size(); i++) {

            final String fieldString = headerListSplitted.get(i);

            try {
                RegField customFieldField = regFieldManager.getByLocalid(fieldString);
                fieldsHeader.add(customFieldField);
            } catch (Exception exx) {

                if (fieldString.startsWith(BaseConstants.KEY_BULK_COLLECTION)) {
                    try {
                        regFieldManager.getByLocalid(fieldString.replace(BaseConstants.KEY_BULK_COLLECTION, ""));
                        fieldsHeader.add(fieldString);
                    } catch (Exception exception) {
                        if (wrongFields.length() == 0) {
                            wrongFields.append(fieldString);
                        } else {
                            wrongFields.append("|").append(fieldString);
                        }
                    }
                } else {
                    if (wrongFields.length() == 0) {
                        wrongFields.append(fieldString);
                    } else {
                        wrongFields.append("|").append(fieldString);
                    }
                }
            }
        }
        return wrongFields;
    }

    private void checkHeaderLocalIDSpealling(List<String> headerListSplitted, CsvHeader csvHeader) throws Exception {
        String localID = headerListSplitted.get(0);
        if (localID.equals(BaseConstants.KEY_BULK_LOCALID)) {
            csvHeader.setLocalID(localID);
        } else {
            final String error_wrong_header_LocalID = systemLocalization.getString("bulk.import.error.header.localid");
            if (operationResult.isEmpty()) {
                operationResult = error_wrong_header_LocalID;
            } else {
                operationResult = operationResult + BR_HTML + error_wrong_header_LocalID;
            }
            throw new Exception(error_wrong_header_LocalID);
        }
    }

    private void checkHeaderLanguageSpealling(List<String> headerListSplitted, CsvHeader csvHeader) throws Exception {
        String language = headerListSplitted.get(1);
        if (language.equals(BaseConstants.KEY_BULK_LANGUAGE)) {
            csvHeader.setLanguage(language);
        } else {
            String error_wrong_header_Language = systemLocalization.getString("bulk.import.error.header.language");
            if (operationResult.isEmpty()) {
                operationResult = error_wrong_header_Language;
            } else {
                operationResult = operationResult + BR_HTML + error_wrong_header_Language;
            }
            throw new Exception(error_wrong_header_Language);
        }
    }

    private HashMap<String, ArrayList<FieldsBulkImport>> analyzeRows(CsvHeader csvHeader, ArrayList<String> additionLines, RegItem regItemCollection) throws Exception {

        RegItemclass regItemclassChild = getItemClassChildren(regItemCollection);

        HashMap<String, ArrayList<FieldsBulkImport>> itemsBulkImport = createMapWithAllTheLinesFromFile(additionLines, csvHeader, regItemCollection, regItemclassChild);

        if (regItemclassChild != null) {
            checkFiledsFromMap(itemsBulkImport, regItemclassChild, regItemCollection);

            return itemsBulkImport;
        } else {
            return null;
        }
    }

    private void checkFiledsFromMap(HashMap<String, ArrayList<FieldsBulkImport>> itemsBulkImport, RegItemclass regItemclassChild, RegItem regItemCollection) {
        for (Map.Entry<String, ArrayList<FieldsBulkImport>> items : itemsBulkImport.entrySet()) {
            String localId = items.getKey();

            ArrayList<FieldsBulkImport> array = items.getValue();

            for (FieldsBulkImport fieldsBulkImport : array) {

                HashMap<RegField, String> map = fieldsBulkImport.getRegFieldsHashMap();
                HashMap<RegField, RegItem> mapCollection = fieldsBulkImport.getRegFieldsCollectionHashMap();
                for (Map.Entry<RegField, String> entry : map.entrySet()) {
                    Object object = entry.getKey();
                    String fieldValue = entry.getValue();

                    if (object instanceof RegField) {
                        RegField regField = entry.getKey();
                        String regFieldTypeUuid = regField.getRegFieldtype().getUuid();

                        if (checkIfFieldTypeIsParentReferenceOrString(regFieldTypeUuid)) {

                            switch (regFieldTypeUuid) {
                                case BaseConstants.KEY_FIELDTYPE_PARENT_UUID:
                                    addFieldParent(fieldValue, regItemCollection, regItemclassChild);
                                    break;
                                case BaseConstants.KEY_FIELDTYPE_RELATIONREFERENCE_UUID:
                                    addFieldRelationReference(regField, mapCollection, fieldValue, regItemclassChild, localId, fieldsBulkImport);
                                    break;
                                default:
                                    addFieldLocalization(array, regItemCollection, regItemclassChild, localId, regField, fieldsBulkImport);
                                    continue;
                            }
                        }
                    }
                }
            }
        }

    }

    private static boolean checkIfFieldTypeIsParentReferenceOrString(String regFieldTypeUuid) {
        return !regFieldTypeUuid.equals(BaseConstants.KEY_FIELDTYPE_SUCCESSOR_UUID)
                && !regFieldTypeUuid.equals(BaseConstants.KEY_FIELDTYPE_PREDECESSOR_UUID)
                && !regFieldTypeUuid.equals(BaseConstants.KEY_FIELDTYPE_REGISTRY_UUID)
                && !regFieldTypeUuid.equals(BaseConstants.KEY_FIELDTYPE_REGISTER_UUID)
                && !regFieldTypeUuid.equals(BaseConstants.KEY_FIELDTYPE_COLLECTION_UUID)
                && !regFieldTypeUuid.equals(BaseConstants.KEY_FIELDTYPE_GROUPREFERENCE_UUID)
                && !regFieldTypeUuid.equals(BaseConstants.KEY_FIELDTYPE_STATUS_UUID);
    }

    private void addFieldLocalization(ArrayList<FieldsBulkImport> array, RegItem regItemCollection, RegItemclass regItemclassChild, String localId, RegField regField, FieldsBulkImport fieldsBulkImport) {
        RegLanguagecodeManager regLanguagecodeManager = new RegLanguagecodeManager(entityManager);
        RegLanguagecode masterLanguage = null;
        try {
            masterLanguage = regLanguagecodeManager.getMasterLanguage();
        } catch (Exception ex) {
            try {
                throw new Exception("Cannot get the master language");
            } catch (Exception ex1) {
                LOGGER.error(ex1);
            }
        }

        RegItemManager regItemManager = new RegItemManager(entityManager);
        boolean masterLanguageIncludedInTheFile = false;
        for (FieldsBulkImport fields : array) {
            if (masterLanguage != null && fields.getLanguage().equals(masterLanguage)) {
                masterLanguageIncludedInTheFile = true;
                break;
            }
        }

        List<RegLocalization> regLocalizationFiled = null;
        if (!masterLanguageIncludedInTheFile) {
//                                  check if masterlanguage is already in the DB
            RegLocalizationManager regLocalizationManager = new RegLocalizationManager(entityManager);

            try {
                //            check if item exist already, if yes copy the item in itemproposed
                String regItemExistentUuid;
                if (regItemCollection.getRegItemclass().equals(regItemclassChild)) {
                    regItemExistentUuid = RegItemUuidHelper.getUuid(localId, regItemCollection, regItemclassChild);
                } else {
                    regItemExistentUuid = RegItemUuidHelper.getUuid(localId, null, regItemclassChild);
                }
                RegItem regItemExist = regItemManager.get(regItemExistentUuid);
                if (masterLanguage != null) {
                    regLocalizationFiled = regLocalizationManager.getAll(regField, regItemExist, masterLanguage);
                }
            } catch (Exception ex) {
            }
        }

        if (masterLanguageIncludedInTheFile || (regLocalizationFiled != null && !regLocalizationFiled.isEmpty())) {

        } else {
            String error_locatlization = systemLocalization.getString("bulkimport.error.localization")
                    .replace(SUBSTITUTE_LOCALID, localId)
                    .replace(SUBSTITUTE_LANGUAGE, fieldsBulkImport.getLanguage().getIso6391code())
                    .replace(SUBSTITUTE_LINE, String.valueOf(fieldsBulkImport.getLine()));
            operationResult = operationResult + BR_HTML + error_locatlization;
        }
    }

    private void addFieldRelationReference(RegField regField, HashMap<RegField, RegItem> mapCollection, String fieldValue, RegItemclass regItemclassChild, String localId, FieldsBulkImport fieldsBulkImport) {
        RegItemManager regItemManager = new RegItemManager(entityManager);
        try {
            RegItemclass itemClassRelationReference = regField.getRegItemclassReference();
            String relationreferenceuuid = "";

            try {
                if (mapCollection.containsKey(regField)) {
                    RegItem itemRelationReferenceCollection = mapCollection.get(regField);
                    relationreferenceuuid = RegItemUuidHelper.getUuid(fieldValue, itemRelationReferenceCollection, itemClassRelationReference);
                } else {
                    relationreferenceuuid = RegItemUuidHelper.getUuid(fieldValue, null, itemClassRelationReference);
                }
                regItemManager.get(relationreferenceuuid);
            } catch (Exception ex) {
                relationreferenceuuid = RegItemproposedUuidHelper.getUuid(fieldValue, null, regItemclassChild, null);
                RegItemproposedManager regItemproposedManager = new RegItemproposedManager(entityManager);
                regItemproposedManager.get(relationreferenceuuid);

//                String relation_error = systemLocalization.getString("bulk.import.error.relationreferenceitem.notexistent")
//                        .replace(SUBSTITUTE_LOCALID, localId)
//                        .replace("{field}", regField.getLocalid())
//                        .replace("{fieldValue}", fieldValue)
//                        .replace(SUBSTITUTE_LINE, String.valueOf(fieldsBulkImport.getLine()));
//                operationResult = operationResult + BR_HTML + relation_error;
            }

        } catch (Exception ex) {

//            String relation_error = systemLocalization.getString("bulk.import.error.relationreferenceitem.notexistent")
//                    .replace(SUBSTITUTE_LOCALID, localId)
//                    .replace("{field}", regField.getLocalid())
//                    .replace("{fieldValue}", fieldValue)
//                    .replace(SUBSTITUTE_LINE, String.valueOf(fieldsBulkImport.getLine()));
//            operationResult = operationResult + BR_HTML + relation_error;
        }
    }

    private void addFieldParent(String fieldValue, RegItem regItemCollection, RegItemclass regItemclassChild) {
        RegItemManager regItemManager = new RegItemManager(entityManager);
        /*check if the parent exist */
        try {
            //create relation
            try {
                try {
                    String parentuuid = RegItemUuidHelper.getUuid(fieldValue, null, regItemclassChild);
                    regItemManager.get(parentuuid);
                } catch (Exception e) {
                    String parentuuid = RegItemUuidHelper.getUuid(fieldValue, regItemCollection, regItemclassChild);
                    regItemManager.get(parentuuid);
                }
            } catch (Exception ex) {
                String parentuuid = RegItemproposedUuidHelper.getUuid(fieldValue, null, regItemclassChild, null);
                RegItemproposedManager regItemproposedManager = new RegItemproposedManager(entityManager);
                regItemproposedManager.get(parentuuid);

//                String parent_error = systemLocalization.getString("bulk.import.error.parent.notexistent")
//                        .replace(SUBSTITUTE_LOCALID, localId)
//                        .replace("{parentLocalID}", fieldValue)
//                        .replace(SUBSTITUTE_LINE, String.valueOf(fieldsBulkImport.getLine()));
//                operationResult = operationResult + BR_HTML + parent_error;
            }

        } catch (Exception ex) {

//                                        check if the parent exist in the file
//            String parent_error = systemLocalization.getString("bulk.import.error.parent.notexistent")
//                    .replace(SUBSTITUTE_LOCALID, localId)
//                    .replace("{parentLocalID}", fieldValue)
//                    .replace(SUBSTITUTE_LINE, String.valueOf(fieldsBulkImport.getLine()));
//            operationResult = operationResult + BR_HTML + parent_error;
        }
    }

    private RegItemclass getItemClassChildren(RegItem regItem) {
        //                get item class of the item to be inserted
        RegItemclass regItemclassChild = null;
        try {
            RegItemclassManager regItemclassManager = new RegItemclassManager(entityManager);
            List<RegItemclass> child = regItemclassManager.getChildItemclass(regItem.getRegItemclass());
            regItemclassChild = child.get(0);
        } catch (Exception ex) {
            String error = "ERROR:  - cannot get the item child class";
            LOGGER.error(error);
        }
        return regItemclassChild;
    }

    private HashMap<String, ArrayList<FieldsBulkImport>> createMapWithAllTheLinesFromFile(ArrayList<String> additionLines, CsvHeader csvHeader, RegItem regItemCollection, RegItemclass regItemclassChild) throws Exception {
        HashMap<String, ArrayList<FieldsBulkImport>> itemsBulkImport = new HashMap<>();

        int line = 2;
        int count = 0;
        for (String additionLine : additionLines) {
            String[] lineSplitted = additionLine.split("\\|", -1);
            List<String> listListSplitted = new ArrayList<>(Arrays.asList(lineSplitted));

            final String localId = listListSplitted.get(0);
            final String language2Letters = listListSplitted.get(1);

            RegLanguagecodeManager regLanguagecodeManager = new RegLanguagecodeManager(entityManager);
            RegLanguagecode regLanguage = null;
            count++;
            if (localId.trim().equalsIgnoreCase("")) {
                operationResult = "<b>" + systemLocalization.getString("bulk.import.error.emptyrequired")
                        .replace("{fields}", "<b>" + "LocalId" + "</b>")
                        .replace("{line}", "<b>" + count + "</b>")
                        + "</b>" + BR_HTML + operationResult;
                request.setAttribute(BaseConstants.KEY_REQUEST_BULK_ERROR, operationResult);
                /* subject = systemLocalization.getString(BaseConstants.KEY_EMAIL_SUBJECT_BULKIMPORT_ERROR);
                            body = systemLocalization.getString(BaseConstants.KEY_EMAIL_BODY_BULKIMPORT_ERROR); */
            } else {
                try {
                    regLanguage = regLanguagecodeManager.getByIso6391code(language2Letters);
                } catch (Exception ex) {
                    if (language2Letters.trim().equalsIgnoreCase("")) {
                        operationResult = "<b>" + systemLocalization.getString("bulk.import.error.emptylanguage").replace("{line}", "<b>" + count + "</b>") + "</b>" + BR_HTML + operationResult;
                        request.setAttribute(BaseConstants.KEY_REQUEST_BULK_ERROR, operationResult);
                    } else {
                        operationResult = "<b>" + systemLocalization.getString("bulk.import.error.wronglanguage").replace("{line}", "<b>" + count + "</b>") + "</b>" + BR_HTML + operationResult;
                        request.setAttribute(BaseConstants.KEY_REQUEST_BULK_ERROR, operationResult);
                    }
                    /* String error_language = systemLocalization.getString("bulk.import.error.language.notexistent")
                        .replace(SUBSTITUTE_LANGUAGE, language2Letters)
                        .replace(SUBSTITUTE_LINE, String.valueOf(line));
                LOGGER.error(error_language);
                operationResult = operationResult + BR_HTML + error_language; */

                    continue;
                }
            }
            HashMap<RegField, String> hashMap = new HashMap<>();
            HashMap<RegField, RegItem> hashCollectionMap = new HashMap<>();

            fillMapWithFiledsAndCollectionMapWithFieldsCollection(listListSplitted, csvHeader, hashMap, hashCollectionMap, localId, line, regItemCollection.getRegItemclass());

            FieldsBulkImport fieldsBulkImport = new FieldsBulkImport();
            fieldsBulkImport.setLine(line);
            fieldsBulkImport.setLanguage(regLanguage);
            fieldsBulkImport.setRegFieldsHashMap(hashMap);
            fieldsBulkImport.setRegFieldsCollectionHashMap(hashCollectionMap);

            if (itemsBulkImport.containsKey(localId)) {
                itemsBulkImport.get(localId).add(fieldsBulkImport);
            } else {
                ArrayList<FieldsBulkImport> fieldsBulkImportArray = new ArrayList<>();
                fieldsBulkImportArray.add(fieldsBulkImport);

                itemsBulkImport.put(localId, fieldsBulkImportArray);
            }

            line++;
        }

        RegLanguagecodeManager regLanguagecodeManager = new RegLanguagecodeManager(entityManager);
        RegLanguagecode masterLanguage = regLanguagecodeManager.getMasterLanguage();

        for (Map.Entry<String, ArrayList<FieldsBulkImport>> entry : itemsBulkImport.entrySet()) {
            boolean containMasterLanguage = false;
            String localId = entry.getKey();
            ArrayList<FieldsBulkImport> value = entry.getValue();

//            check if the master language exist in the file
            for (FieldsBulkImport fieldsBulkImport : value) {
                if (fieldsBulkImport.getLanguage().equals(masterLanguage)) {
                    containMasterLanguage = true;
                    break;
                }
            }

//            check if the item in master language is already in the DB
            if (!containMasterLanguage) {
                try {
//                get item by regItemclassChild, language and local ID
                    RegItemManager regItemManager = new RegItemManager(entityManager);
                    regItemManager.get(RegItemUuidHelper.getUuid(localId, null, regItemclassChild));
                    containMasterLanguage = true;
                } catch (Exception ex) {

                }
            }

//            give error if master language of the item doenst exist in the file or DB
            if (!containMasterLanguage) {
//                give error missing master languages
                if (!operationResult.isEmpty()) {
                    operationResult = operationResult + BR_HTML;
                }
                operationResult = operationResult + "<b>" + systemLocalization.getString("bulk.import.error.missingmasterlanguage").
                        replace(SUBSTITUTE_LANGUAGE, masterLanguage.getIso6391code()).
                        replace(SUBSTITUTE_ITEMLOCALID, localId) + "</b>";
            }
        }

        if (!operationResult.isEmpty()) {
            throw new Exception(operationResult);
        }

        return itemsBulkImport;
    }

    private void fillMapWithFiledsAndCollectionMapWithFieldsCollection(List<String> listListSplitted, CsvHeader csvHeader, HashMap<RegField, String> hashMap, HashMap<RegField, RegItem> hashCollectionMap, final String localId, int line, RegItemclass regItemclassChild) {
        RegItemManager regItemManager = new RegItemManager(entityManager);
        RegFieldManager regFieldManager = new RegFieldManager(entityManager);

        for (int i = 2; i < listListSplitted.size(); i++) {

            Object object = csvHeader.getRegFields().get(i - 2);
            final String value = listListSplitted.get(i);

            if (!value.isEmpty()) {
                if (object instanceof RegField) {
                    RegField field = (RegField) object;

                    hashMap.put(field, value);
                } else if (object instanceof String) {
                    String itemValue = (String) object;
                    if (itemValue.startsWith(BaseConstants.KEY_BULK_COLLECTION)) {
                        try {
                            final RegField fieldbyLocalid = regFieldManager.getByLocalid(itemValue.replace(BaseConstants.KEY_BULK_COLLECTION, ""));
                            String uuidCollection;
                            if (fieldbyLocalid.getRegItemclassReference() != null) {
                                RegItemclass regItemCollectionItemclass = fieldbyLocalid.getRegItemclassReference().getRegItemclassParent();
                                uuidCollection = RegItemUuidHelper.getUuid(value, null, regItemCollectionItemclass);
                            } else {
                                uuidCollection = RegItemUuidHelper.getUuid(value, null, regItemclassChild);
                            }
                            hashCollectionMap.put(fieldbyLocalid, regItemManager.get(uuidCollection));
                        } catch (Exception ex) {
                            final String error = systemLocalization.getString("bulk.import.error.collection.notexistent")
                                    .replace("{collectionlocalid}", value)
                                    .replace("{itemLocalID}", localId)
                                    .replace(SUBSTITUTE_LINE, String.valueOf(line));
                            LOGGER.error(error);
                            operationResult = operationResult + BR_HTML + error;
                        }
                    }
                }
            } else {
                if (object instanceof RegField) {
                    RegField field = (RegField) object;

                    hashMap.put(field, value);
                }
            }
        }
    }

    private void storeItems(HashMap<String, ArrayList<FieldsBulkImport>> itemsBulkImport, RegItem regItemContainer, RegUser regUser, HttpServletRequest request, ArrayList<String> additionLines, Boolean isBulkEdit) throws Exception {
        RegItemclass regItemclassChild = getItemClassChildren(regItemContainer);

        if (regItemclassChild != null) {
            RegAction regAction = addRegActionForAllProposedItemsCSV(regUser, regItemContainer, regItemclassChild);
            if (regAction != null) {
                storeProposedItems(itemsBulkImport, regItemContainer, regUser, regItemclassChild, regAction, request, additionLines, isBulkEdit);
                try {
                    if (!entityManager.getTransaction().isActive()) {
                        entityManager.getTransaction().begin();
                    }
                    entityManager.getTransaction().commit();
                } catch (Exception e) {
                    if (entityManager.getTransaction().isActive()) {
                        entityManager.getTransaction().rollback();
                    }
                } finally {
                    if (entityManager.isOpen()) {
                        entityManager.close();
                    }
                }
            }
        }
    }

    public void handleBulkRegItemproposedSave(RegUser regUser, Map requestParameters) throws Exception {

        String regItemUuid = (String) requestParameters.get("form_item-uuid");

        // Getting the RegItem on which the proposal is done
        RegItem regItem = null;
        try {
            RegItemManager regItemManager = new RegItemManager(entityManager);
            regItem = regItemManager.get(regItemUuid);
        } catch (NoResultException e) {
        }

        // Init managers
        RegItemproposedManager regItemproposedManager = new RegItemproposedManager(entityManager);

        // Check if the RegItemproposed is already available
        try {
            RegItemproposed regItemproposed = regItemproposedManager.getByRegItemReference(regItem);

            // Checking the ownership
            if (!regItemproposed.getRegUser().getUuid().equals(regUser.getUuid())) {
                // If the user is not the owner of the current RegItemproposed, deny the update
                throw new UnauthorizedUserException(ExceptionConstants.KEY_EXCEPTION_UNAUTHORIZED_USER_OWNER);
            }

            // The writing operation on the Database are synchronized
            /* ## Start Synchronized ## */
            synchronized (sync) {
                // Update the RegItemproposed
                if (!entityManager.getTransaction().isActive()) {
                    entityManager.getTransaction().begin();
                }
                updateRegItemproposed(regItemproposed, requestParameters);
                entityManager.getTransaction().commit();
            }
            /* ## End Synchronized ## */

        } catch (NoResultException e) {
            // If the RegItemproposed is not available, creating it

            // The writing operation on the Database are synchronized
            /* ## Start Synchronized ## */
            synchronized (sync) {
                if (!entityManager.getTransaction().isActive()) {
                    entityManager.getTransaction().begin();
                }
                // Copy the RegItem to the RegItemproposed
                RegItemproposed regItemproposed = copyRegItemToRegItemproposed(regItem, regUser);

                // Copy all the regRelations
                copyRegRelationsToRegRelationproposeds(regItem, regItemproposed);

                // Copy groups relations
                copyRegItemRegGroupRegRoleMappingToRegItemproposedRegGroupRegRoleMapping(regItem, regItemproposed);

                // Register Federation export
                String registerFederationExportTmp = (String) requestParameters.get("registerFederationExport");
                // !!! Sanitizing input
                String registerFederationExport;
                boolean registerFederationExportBol;

                if (registerFederationExportTmp != null) {
                    registerFederationExport = InputSanitizerHelper.sanitizeInput(registerFederationExportTmp);
                    registerFederationExportBol = (registerFederationExport != null && registerFederationExport.equals(BaseConstants.KEY_BOOLEAN_STRING_TRUE));
                } else {
                    registerFederationExportBol = false;
                }

                if (regItemproposed != null) {
                    regItemproposed.setRorExport(registerFederationExportBol);
                }

                // Update the RegFields
                updateFields(regItemproposed, requestParameters);
                entityManager.getTransaction().commit();
            }
            /* ## End Synchronized ## */
        } catch (Exception e) {
            LOGGER.error(e);
        } finally {
            if (entityManager != null) {
                entityManager.close();
            }
        }
    }

    // Copy all the RegItemRegGroupRegRoleMapping related to the item passed by parameter
    // to RegItemproposedRegGroupRegRoleMapping
    private void copyRegItemRegGroupRegRoleMappingToRegItemproposedRegGroupRegRoleMapping(RegItem regItem, RegItemproposed regItemproposed) throws Exception {

        RegItemRegGroupRegRoleMappingManager regItemRegGroupRegRoleMappingManager = new RegItemRegGroupRegRoleMappingManager(entityManager);
        RegItemproposedRegGroupRegRoleMappingManager regItemproposedRegGroupRegRoleMappingManager = new RegItemproposedRegGroupRegRoleMappingManager(entityManager);

        // Getting all the RegItemRegGroupRegRoleMapping related to that RegItem
        List<RegItemRegGroupRegRoleMapping> regItemRegGroupRegRoleMappings = regItemRegGroupRegRoleMappingManager.getAll(regItem);

        for (RegItemRegGroupRegRoleMapping tmpRegItemRegGroupRegRoleMapping : regItemRegGroupRegRoleMappings) {

            RegItemproposedRegGroupRegRoleMapping regItemproposedRegGroupRegRoleMapping = new RegItemproposedRegGroupRegRoleMapping();
            String newUuid = RegItemproposedRegGroupRegRoleMappingUuidHelper.getUuid(regItemproposed.getUuid(), tmpRegItemRegGroupRegRoleMapping.getRegGroup().getUuid(), tmpRegItemRegGroupRegRoleMapping.getRegRole().getUuid());

            regItemproposedRegGroupRegRoleMapping.setUuid(newUuid);
            regItemproposedRegGroupRegRoleMapping.setRegItemRegGroupRegRoleMappingReference(tmpRegItemRegGroupRegRoleMapping);
            regItemproposedRegGroupRegRoleMapping.setInsertdate(new Date());
            regItemproposedRegGroupRegRoleMapping.setRegGroup(tmpRegItemRegGroupRegRoleMapping.getRegGroup());
            regItemproposedRegGroupRegRoleMapping.setRegItemproposed(regItemproposed);
            regItemproposedRegGroupRegRoleMapping.setRegRole(tmpRegItemRegGroupRegRoleMapping.getRegRole());

            regItemproposedRegGroupRegRoleMappingManager.add(regItemproposedRegGroupRegRoleMapping);
        }
    }

    // Copy all the RegRelation related to the item passed by parameter and all the
    // RegLocalization to RegRelationproposed and RegLocalizationproposed
    private void copyRegRelationsToRegRelationproposeds(RegItem regItem, RegItemproposed regItemproposed) throws Exception {

        RegRelationManager regRelationManager = new RegRelationManager(entityManager);
        RegLocalizationManager regLocalizationManager = new RegLocalizationManager(entityManager);
        RegRelationproposedManager regRelationproposedManager = new RegRelationproposedManager(entityManager);
        RegLocalizationproposedManager regLocalizationproposedManager = new RegLocalizationproposedManager(entityManager);

        // Getting all the relations related to that object
        List<RegRelation> regRelations = regRelationManager.getAllBySubject(regItem);

        // Copying RegRelations to RegRelationProposeds
        HashMap<String, RegRelationproposed> tempHashmap = new HashMap();

        for (RegRelation regRelation : regRelations) {

            RegRelationproposed regRelationproposed = new RegRelationproposed();
            String regRelationproposedUuid = RegRelationproposedUuidHelper.getUuid(regItemproposed, null, regRelation.getRegRelationpredicate(), null, regRelation.getRegItemObject());

            regRelationproposed.setUuid(regRelationproposedUuid);
            regRelationproposed.setRegItemSubject(null);
            regRelationproposed.setRegItemproposedSubject(regItemproposed);
            regRelationproposed.setRegItemObject(regRelation.getRegItemObject());
            regRelationproposed.setRegItemproposedObject(null);
            regRelationproposed.setRegRelationReference(regRelation);
            regRelationproposed.setRegRelationpredicate(regRelation.getRegRelationpredicate());
            regRelationproposed.setInsertdate(new Date());

            regRelationproposedManager.add(regRelationproposed);

            tempHashmap.put(regRelation.getUuid(), regRelationproposed);
        }

        // Replicating the relevant Reglocalizationproposeds pointing to the 
        //  RegRelationproposed copyied above
        // Getting all the localization with a reference to a reg relation 
        // related to the current RegItem
        List<RegLocalization> regLocalizations = regLocalizationManager.getAllWithRelationReference(regItem);
        for (RegLocalization regLocalization : regLocalizations) {
            // Creating the regLocalizationproposed for the regRelationreference
            RegLocalizationproposed regLocalizationproposed = new RegLocalizationproposed();
            String newUuid = RegLocalizationproposedUuidHelper.getUuid(regLocalization.getFieldValueIndex(), regLocalization.getRegLanguagecode(), regItemproposed, regLocalization.getRegField());
            regLocalizationproposed.setUuid(newUuid);
            regLocalizationproposed.setFieldValueIndex(regLocalization.getFieldValueIndex());
            regLocalizationproposed.setHref(regLocalization.getHref());
            regLocalizationproposed.setRegField(regLocalization.getRegField());
            regLocalizationproposed.setRegItemproposed(regItemproposed);
            regLocalizationproposed.setRegLanguagecode(regLocalization.getRegLanguagecode());
            regLocalizationproposed.setRegLocalizationReference(regLocalization);
            regLocalizationproposed.setRegRelationproposedReference(tempHashmap.get(regLocalization.getRegRelationReference().getUuid()));
            regLocalizationproposed.setValue(regLocalization.getValue());
            regLocalizationproposed.setInsertdate(new Date());
            regLocalizationproposed.setRegAction(regItemproposed.getRegAction());

            regLocalizationproposedManager.add(regLocalizationproposed);
        }
    }

    /* -- Supporting methods -- */
    // Create the new RegItemproposed
    private RegItemproposed copyRegItemToRegItemproposed(RegItem regItem, RegUser regUser) throws Exception {
        if (regItem != null) {
            // Init managers
            RegItemproposedManager regItemproposedManager = new RegItemproposedManager(entityManager);
            RegStatusManager regStatusManager = new RegStatusManager(entityManager);
            RegRelationpredicateManager regRelationpredicateManager = new RegRelationpredicateManager(entityManager);

            // Getting the eventual collection relation
            RegRelationManager regRelationManager = new RegRelationManager(entityManager);
            RegRelationpredicate regRelationpredicateRegistry = regRelationpredicateManager.get(BaseConstants.KEY_PREDICATE_REGISTRY);
            RegRelationpredicate regRelationpredicateRegister = regRelationpredicateManager.get(BaseConstants.KEY_PREDICATE_REGISTER);
            RegRelationpredicate regRelationpredicateCollection = regRelationpredicateManager.get(BaseConstants.KEY_PREDICATE_COLLECTION);
            List<RegRelation> regRelationCollections = regRelationManager.getAll(regItem, regRelationpredicateCollection);
            RegRelation regRelationCollection = null;
            if (!regRelationCollections.isEmpty()) {
                // Every items can have just one relation of type collection
                regRelationCollection = regRelationCollections.get(0);
            }

            // Setting the register
            RegItem regItemRegister;
            if (!regItem.getRegItemclass().getRegItemclasstype().getLocalid().equals(BaseConstants.KEY_ITEMCLASS_TYPE_REGISTER)) {
                List<RegRelation> regRelationRegisters = regRelationManager.getAll(regItem, regRelationpredicateRegister);
                try {
                    regItemRegister = regRelationRegisters.get(0).getRegItemObject();
                } catch (Exception e2) {
                    regItemRegister = null;
                }
            } else {
                regItemRegister = regItem;
            }

            // Setting the registry
            RegItem regItemRegistry;
            if (!regItem.getRegItemclass().getRegItemclasstype().getLocalid().equals(BaseConstants.KEY_ITEMCLASS_TYPE_REGISTRY)) {
                List<RegRelation> regRelationRegistries = regRelationManager.getAll(regItem, regRelationpredicateRegistry);
                try {
                    regItemRegistry = regRelationRegistries.get(0).getRegItemObject();

                } catch (Exception e2) {
                    regItemRegistry = null;
                }
            } else {
                regItemRegistry = regItem;
            }

            // Getting the reg status draft for the proposed item
            RegStatus regStatusDraft = regStatusManager.findByLocalid(BaseConstants.KEY_STATUS_LOCALID_DRAFT);

            //Copying it to the RegItemproposed
            RegItemproposed regItemproposed = new RegItemproposed();

            String itemProposedUuid = RegItemproposedUuidHelper.getUuid(regItem.getLocalid(), ((regRelationCollection != null) ? regRelationCollection.getRegItemObject() : null), regItem.getRegItemclass(), regItem);
            regItemproposed.setUuid(itemProposedUuid);
            regItemproposed.setLocalid(regItem.getLocalid());
            regItemproposed.setRegStatus(regStatusDraft);
            regItemproposed.setRegItemclass(regItem.getRegItemclass());
            regItemproposed.setInsertdate(regItem.getInsertdate());
            regItemproposed.setRegUser(regUser);
            regItemproposed.setRorExport(regItem.getRorExport());
            regItemproposed.setRegItemReference(regItem);

            // Check if the related RegAction is already available, otherwise
            // create the RegAction.
            RegAction regAction = regActionCheck(regUser, regItemRegister, regItemRegistry);

            regItemproposed.setRegAction(regAction);

            // Saving the RegItemproposed
            regItemproposedManager.add(regItemproposed);

            return regItemproposed;
        } else {
            return null;
        }
    }

    private RegAction regActionCheck(RegUser regUser, RegItem regItemRegister, RegItem regItemRegistry) throws Exception {
        RegAction regAction = null;

        RegActionManager regActionManager = new RegActionManager(entityManager);
        RegStatusManager regStatusManager = new RegStatusManager(entityManager);
        RegLocalizationManager regLocalizationManager = new RegLocalizationManager(entityManager);
        RegFieldManager regFieldManager = new RegFieldManager(entityManager);
        RegLanguagecodeManager regLanguagecodeManager = new RegLanguagecodeManager(entityManager);
        RegLanguagecode masterLanguage = regLanguagecodeManager.getMasterLanguage();

        ResourceBundle systemLocalization = Configuration.getInstance().getLocalization();

        List<RegAction> regActions;
        RegStatus regStatus;

        // Getting the RegStatus draft, to check that there are not other RegAction
        // dratf from the same user
        regStatus = regStatusManager.findByLocalid(BaseConstants.KEY_STATUS_LOCALID_DRAFT);

        boolean notFound = false;

        // checking if the RegAction is available
        try {
            regActions = regActionManager.getAllWithNoComments(regUser, regItemRegister, regItemRegistry, regStatus);
            if (regActions != null && regActions.isEmpty()) {
                notFound = true;
            } else if (regActions != null) {
                // There should be only one regAction in status draft with 
                // no comments per each user. 
                regAction = regActions.get(0);
            }
        } catch (NoResultException e) {
            notFound = true;
        }

        // Creating the RegAction if not found
        if (notFound) {

            regAction = new RegAction();
            Date currentDate = new Date();
            String newUuid = RegActionUuidHelper.getUuid(regUser, currentDate);

            // Creating the default label
            RegItem regItemcheck = (regItemRegister != null) ? regItemRegister : regItemRegistry;
            List<RegLocalization> regLocalization = regLocalizationManager.getAll(regFieldManager.getTitleRegField(), regItemcheck, masterLanguage);
            String defaultLabel = systemLocalization.getString("label.actionon") + " " + regLocalization.get(0).getValue();

            regAction.setUuid(newUuid);
            regAction.setLabel(defaultLabel);
            regAction.setInsertdate(currentDate);
            regAction.setRegUser(regUser);
            regAction.setChangeRequest(null);
            regAction.setRejectMessage(null);
            regAction.setIssueTrackerLink(null);
            regAction.setRegStatus(regStatus);
            regAction.setChangesImplemented(false);
            regAction.setRegItemRegister(regItemRegister);
            regAction.setRegItemRegistry(regItemRegistry);

            regActionManager.add(regAction);

        }

        return regAction;
    }

    /**
     * DELETE THIS METHOD ENEKO
     *
     * @param itemsBulkImport
     * @param regItemContainer
     * @param regUser
     * @param regItemclassChild
     * @param regAction
     * @param request
     * @throws Exception
     */

    private void storeProposedItems(HashMap<String, ArrayList<FieldsBulkImport>> itemsBulk, RegItem regItemContainer, RegUser regUser, RegItemclass regItemclassChild, RegAction regAction, HttpServletRequest request, ArrayList<String> additionLines, Boolean isBulkEdit) throws Exception {

        if(isBulkEdit){
            storeProposedItemsBulkEdit(itemsBulk, regUser, regItemclassChild, request, additionLines); 
        }else{     
            storeProposedItemsBulkImport(itemsBulk, regItemContainer, regUser, regItemclassChild, regAction, request);
        }
    }
    
    private void storeProposedItemsBulkImport(HashMap<String, ArrayList<FieldsBulkImport>> itemsBulkImport, RegItem regItemContainer, RegUser regUser, RegItemclass regItemclassChild, RegAction regAction, HttpServletRequest request) throws Exception{

        RegItemproposed regItemproposed;
        
        for (Map.Entry<String, ArrayList<FieldsBulkImport>> items : itemsBulkImport.entrySet()) {
                try {
                    if (items.getValue().size() > 1) {
                        throw new Exception();
                    }
                } catch (Exception ex) {
                    operationResult = "<b>" + systemLocalization.getString("bulk.import.error.duplicate").replace("{localid}", "<b>" + items.getKey() + "</b>")
                            + "</b>" + BR_HTML + operationResult;
                    request.setAttribute(BaseConstants.KEY_REQUEST_BULK_ERROR, operationResult);
                    break;
                }
                String localId = items.getKey();
                regItemproposed = createItemProposed(regItemContainer, regItemclassChild, localId, regUser, regAction, request);
                ArrayList<FieldsBulkImport> array = items.getValue();

                RegLanguagecodeManager regLanguagecodeManager = new RegLanguagecodeManager(entityManager);
                RegLanguagecode masterLanguage = regLanguagecodeManager.getMasterLanguage();

                ArrayList<FieldsBulkImport> fieldsBulkImportListLocal = new ArrayList<>();
                for (FieldsBulkImport fieldsBulkImport : array) {
                    RegLanguagecode fieldLanguage = fieldsBulkImport.getLanguage();
                    if (fieldLanguage.equals(masterLanguage)) {
                        storeItemFromBulk(fieldsBulkImport, regItemContainer, regItemclassChild, regItemproposed, localId, array, fieldLanguage, masterLanguage);
                    } else {
                        fieldsBulkImportListLocal.add(fieldsBulkImport);
                    }
                }

                if (!fieldsBulkImportListLocal.isEmpty()) {
                    for (FieldsBulkImport fieldsBulkImport : fieldsBulkImportListLocal) {
                        RegLanguagecode fieldLanguage = fieldsBulkImport.getLanguage();
                        storeItemFromBulk(fieldsBulkImport, regItemContainer, regItemclassChild, regItemproposed, localId, array, fieldLanguage, masterLanguage);
                    }
                }
            }
        
                
                
        
    }
    
    private void storeProposedItemsBulkEdit(HashMap<String, ArrayList<FieldsBulkImport>> itemsBulkImport, RegUser regUser, RegItemclass regItemclassChild, HttpServletRequest request, ArrayList<String> additionLines) throws Exception {

        RegItemManager regItemManager = new RegItemManager(entityManager);
        RegItemproposedHandler regItemproposedHandler = new RegItemproposedHandler();
        
        Map.Entry<String, ArrayList<FieldsBulkImport>> any = itemsBulkImport.entrySet().iterator().next();
            RegItem regItemExistentAlready = regItemManager.getByLocalidAndRegItemClass(any.getKey(), regItemclassChild);
            for (Map.Entry<String, ArrayList<FieldsBulkImport>> items : itemsBulkImport.entrySet()) {
                try {
                    try {
                        if (items.getValue().size() > 1) {
                            throw new Exception();
                        }
                    } catch (Exception ex) {
                        operationResult = "<b>" + systemLocalization.getString("bulk.import.error.duplicate").replace("{localid}", "<b>" + items.getKey() + "</b>")
                                + "</b>" + BR_HTML + operationResult;
                        request.setAttribute(BaseConstants.KEY_REQUEST_BULK_ERROR, operationResult);
                        break;
                    }
                    if (regItemExistentAlready != null) {
                        items.getValue().get(0).getRegFieldsHashMap().values();
                        HashMap<RegField, String> fields = items.getValue().get(0).getRegFieldsHashMap();
                        RegItem regItemIterator = regItemManager.getByLocalidAndRegItemClass(items.getKey(), regItemclassChild);
                        String language = items.getValue().get(0).getLanguage().getUuid();
                        regItemproposedHandler.completeCopyRegItemToRegItemporposedBulkEdit(regItemIterator, regUser, fields, additionLines, language);
                    }
                } catch (Exception ex) {
                    
                }
            }
        
    }

    private void storeItemFromBulk(FieldsBulkImport fieldsBulkImport, RegItem regItemContainer, RegItemclass regItemclassChild, RegItemproposed regItemproposed, String localId, ArrayList<FieldsBulkImport> array, RegLanguagecode fieldLanguage, RegLanguagecode masterLanguage) throws Exception {
        HashMap<RegField, String> map = fieldsBulkImport.getRegFieldsHashMap();
        HashMap<RegField, RegItem> mapCollection = fieldsBulkImport.getRegFieldsCollectionHashMap();
        for (Map.Entry<RegField, String> entry : map.entrySet()) {
            Object object = entry.getKey();
            String fieldValue = entry.getValue();

            if (object instanceof RegField) {
                RegField regField = (RegField) entry.getKey();
                String regFieldTypeUuid = regField.getRegFieldtype().getUuid();

                if (checkIfFieldTypeIsParentReferenceOrString(regFieldTypeUuid)) {
                    switch (regFieldTypeUuid) {
                        case BaseConstants.KEY_FIELDTYPE_PARENT_UUID:
                            if (fieldLanguage.equals(masterLanguage)) {
                                storeParent(fieldValue, regItemContainer, regItemproposed, localId, fieldsBulkImport);
                            }
                            break;
                        case BaseConstants.KEY_FIELDTYPE_RELATIONREFERENCE_UUID:
                            if (fieldLanguage.equals(masterLanguage)) {
                                storeRelarionReference(fieldValue, regField, regItemproposed, mapCollection, regItemclassChild, fieldLanguage, localId, fieldsBulkImport);
                            }
                            break;
                        default:
                            storeLocalization(fieldLanguage, regItemproposed, regField, masterLanguage, fieldValue, localId, array);
                            break;
                    }
                }
            }
        }
    }

    private RegItemproposed createItemProposed(RegItem regItemContainer, RegItemclass regItemclassChild, String localId, RegUser regUser, RegAction regAction, HttpServletRequest request) throws Exception {
        RegItemManager regItemManager = new RegItemManager(entityManager);
        RegItemproposed regItemproposed = null;

        try {
            //String regItemExistentAlready = RegItemUuidHelper.getUuid(localId, regItemContainer, regItemclassChild);
            RegItem regItemExistentAlready = regItemManager.getByLocalidAndRegItemClass(localId, regItemclassChild);
            if (regItemExistentAlready != null) {
                //            check if item exist already, if yes copy the item in itemproposed
                String regItemExistentUuid;
                if (regItemContainer.getRegItemclass().equals(regItemclassChild)) {
                    regItemExistentUuid = RegItemUuidHelper.getUuid(localId, regItemContainer, regItemclassChild);
                } else {
                    regItemExistentUuid = RegItemUuidHelper.getUuid(localId, null, regItemclassChild);
                }
                RegItem regItemExist = regItemManager.get(regItemExistentUuid);

                RegItemproposedHandler regItemproposedHandler = new RegItemproposedHandler();
                regItemproposed = regItemproposedHandler.completeCopyRegItemToRegItemporposed(regItemExist, regUser);
            } else {
                regItemproposed = createRegItemProposed(localId, regItemclassChild, regUser, regAction);
                addRegistryRegisterRelation(regItemContainer, regItemproposed);
                addCollectionRelation(regItemContainer, regItemproposed);
            }

        } catch (Exception ex) {
//                    create propose item
            regItemproposed = createRegItemProposed(localId, regItemclassChild, regUser, regAction);
            addRegistryRegisterRelation(regItemContainer, regItemproposed);
            addCollectionRelation(regItemContainer, regItemproposed);
        }
        return regItemproposed;
    }

    public void handleRegItemproposedNewSave(Map requestParameters, RegUser regUser) throws Exception {

        // Getting the ID of the RegItem (original item)
        String[] regItemUuidTmp = (String[]) requestParameters.get(BaseConstants.KEY_REQUEST_FORM_ITEMUUID);
        // !!! Sanitizing input
        String regItemUuid = InputSanitizerHelper.sanitizeInput(regItemUuidTmp[0]);

        // Init managers
        RegItemproposedManager regItemproposedManager = new RegItemproposedManager(entityManager);

        // Getting the RegItem on which the proposal is done
        RegItemproposed regItemproposed = null;
        try {
            regItemproposed = regItemproposedManager.get(regItemUuid);
        } catch (NoResultException e) {
        }

        // Check if the RegItemproposed is already available
        try {

            // Checking the ownership
            if (regItemproposed != null && !regItemproposed.getRegUser().getUuid().equals(regUser.getUuid())) {
                // If the user is not the owner of the current RegItemproposed, deny the update
                throw new UnauthorizedUserException(ExceptionConstants.KEY_EXCEPTION_UNAUTHORIZED_USER_OWNER);
            }

            // The writing operation on the Database are synchronized
            /* ## Start Synchronized ## */
            synchronized (sync) {
                // Update the RegItemproposed
                if (!entityManager.getTransaction().isActive()) {
                    entityManager.getTransaction().begin();
                }
                updateRegItemproposed(regItemproposed, requestParameters);
                entityManager.getTransaction().commit();
            }
            /* ## End Synchronized ## */

        } catch (NoResultException e) {
        } finally {
            if (entityManager != null) {
                entityManager.close();
            }
        }
    }

    // Update an existing RegItemproposed
    private void updateRegItemproposed(RegItemproposed regItemproposed, Map requestParameters) throws Exception {

        //Updating the regItemproposed edit date
        regItemproposed.setEditdate(new Date());

        // Register Federation export
        String[] registerFederationExportTmp = (String[]) requestParameters.get(BaseConstants.KEY_FORM_FIELD_NAME_REGISTERFEDERATIONEXPORT);
        // !!! Sanitizing input
        String registerFederationExport;
        boolean registerFederationExportBol;

        if (registerFederationExportTmp != null) {
            registerFederationExport = InputSanitizerHelper.sanitizeInput(registerFederationExportTmp[0]);
            registerFederationExportBol = (registerFederationExport != null && registerFederationExport.equals(BaseConstants.KEY_BOOLEAN_STRING_TRUE));
        } else {
            registerFederationExportBol = false;
        }

        regItemproposed.setRorExport(registerFederationExportBol);

        // Updating fields
        updateFields(regItemproposed, requestParameters);

    }

    // Update fields
    private String updateFields(RegItemproposed regItemproposed, Map requestParameters) throws Exception {

        // Init variables        
        Set s = requestParameters.entrySet();
        Iterator it = s.iterator();
        String regLanguagecodeUuidTemp = (String) requestParameters.get("languagecodeUuid");
        // !!! Sanitizing input
        String regLanguagecodeUuid = InputSanitizerHelper.sanitizeInput(regLanguagecodeUuidTemp);

        // Init managers
        RegFieldmappingManager regFieldmappingManager = new RegFieldmappingManager(entityManager);
        RegLocalizationproposedManager regLocalizationproposedManager = new RegLocalizationproposedManager(entityManager);
        RegLocalizationManager regLocalizationManager = new RegLocalizationManager(entityManager);
        RegLanguagecodeManager regLanguagecodeManager = new RegLanguagecodeManager(entityManager);

        // Getting the reg languagecode of the current edited field
        RegLanguagecode regLanguagecode = regLanguagecodeManager.get(regLanguagecodeUuid);
        RegLanguagecode masterLanguage = regLanguagecodeManager.getMasterLanguage();

        // Errors string
        String errors = "";

        // Iterate on all the form fields
        while (it.hasNext()) {

            Map.Entry<String, String[]> entry = (Map.Entry<String, String[]>) it.next();

            // ** Each field is univocally numbered: in this way there are no field 
            // in the request with more than one value **
            // This is better to keep the consistency with right value since with
            // fields with the same name and multi values different browsers may
            // have different implementation on the handling of the index/order of the value
            String key = entry.getKey();
            String[] tmp = processFieldName(key);
            String fieldUuid = tmp[0];

            String referenceRegLocalizationUuid = tmp[2];

            // Getting the value
            String[] values = entry.getValue();
            String value = values[0];

            // Handling charset            
            byte[] bytes = value.getBytes(Charset.defaultCharset());
            value = new String(bytes, StandardCharsets.UTF_8);

            // !!! Sanitizing form input
            value = (value.length() == 0) ? "" : InputSanitizerHelper.sanitizeInput(value);

            // Getting the eventual href
            String hrefFieldName = processFieldHrefName(key);
            String[] paramHrefs = (String[]) requestParameters.get(hrefFieldName);
            String href = "";
            if (paramHrefs != null) {
                //For each value there is just one link
                href = paramHrefs[0];
                // !!! Sanitizing form input
                href = InputSanitizerHelper.sanitizeInput(href);
            }

            try {
                // Getting the current regFieldMapping
                RegFieldmapping regFieldmapping = regFieldmappingManager.get(fieldUuid);

                // Getting the RegField
                RegField regField = regFieldmapping.getRegField();

                // Handling the RelationReference case
                if (regField.getRegFieldtype().getLocalid().equals(BaseConstants.KEY_FIELD_TYPE_RELATION)) {
                    if (value.trim().length() > 0) {
                        RegItemManager regItemManager = new RegItemManager(entityManager);
                        RegRelationproposedManager regRelationproposedManager = new RegRelationproposedManager(entityManager);
                        RegRelationpredicateManager regRelationpredicateManager = new RegRelationpredicateManager(entityManager);

                        // Getting the right RegRelationPredicate
                        RegRelationpredicate regRelationPredicateRealtion = regRelationpredicateManager.get(BaseConstants.KEY_PREDICATE_REFERENCE);

                        // Getting the RegItem pointed by the relation
                        RegItem regItemRelation = regItemManager.get(value);

                        // Creating the new RegRelationproposed
                        String newRegRelationproposedUuid = RegRelationproposedUuidHelper.getUuid(regItemproposed, null, regRelationPredicateRealtion, null, regItemRelation);
                        RegRelationproposed newRegRelationproposed = new RegRelationproposed();
                        newRegRelationproposed.setInsertdate(new Date());
                        newRegRelationproposed.setRegItemproposedSubject(regItemproposed);
                        newRegRelationproposed.setRegRelationpredicate(regRelationPredicateRealtion);
                        newRegRelationproposed.setRegItemObject(regItemRelation);
                        newRegRelationproposed.setUuid(newRegRelationproposedUuid);

                        // Adding the newly created RegRelationproposed
                        regRelationproposedManager.add(newRegRelationproposed);

                        // Creating the new RegLocalizationproposed
                        RegLocalizationproposed regLocalizationproposed = new RegLocalizationproposed();

                        // Getting the new fieldValueIndex (max of the field index
                        // of the localizations for the current field)
                        List<RegLocalizationproposed> tmpLocalizations = regLocalizationproposedManager.getAll(regField, regItemproposed, regLanguagecode);
                        int fieldValueIndexNew = -1;
                        for (RegLocalizationproposed tmpRegLocalization : tmpLocalizations) {
                            if (tmpRegLocalization.getFieldValueIndex() > fieldValueIndexNew) {
                                fieldValueIndexNew = tmpRegLocalization.getFieldValueIndex();
                            }
                        }
                        fieldValueIndexNew++;

                        // Creating the new uuid
                        String regLocalizationproposedUuid = "";
                        regLocalizationproposedUuid = RegLocalizationproposedUuidHelper.getUuid(fieldValueIndexNew, masterLanguage, regItemproposed, regField);

                        // Setting fields
                        regLocalizationproposed.setUuid(regLocalizationproposedUuid);
                        regLocalizationproposed.setRegItemproposed(regItemproposed);
                        regLocalizationproposed.setRegField(regField);
                        regLocalizationproposed.setInsertdate(new Date());
                        regLocalizationproposed.setFieldValueIndex(fieldValueIndexNew);

                        regLocalizationproposed.setRegLanguagecode(masterLanguage);

                        regLocalizationproposed.setValue(null);
                        regLocalizationproposed.setHref(null);

                        regLocalizationproposed.setRegRelationproposedReference(newRegRelationproposed);

                        regLocalizationproposed.setRegAction(regItemproposed.getRegAction());

                        RegLocalization tmpRegLocalizationReference = null;
                        if (!regFieldmapping.getMultivalue()) {
                            // Check if there is a null value to remove for this regField
                            List<RegLocalizationproposed> nullpRegLocalizationproposeds = regLocalizationproposedManager.getAllNull(regField, regItemproposed);
                            for (RegLocalizationproposed tmpRegLocalizationProposed : nullpRegLocalizationproposeds) {
                                tmpRegLocalizationReference = tmpRegLocalizationProposed.getRegLocalizationReference();
                                regLocalizationproposedManager.delete(tmpRegLocalizationProposed);
                            }
                        }

                        if (tmpRegLocalizationReference != null) {
                            regLocalizationproposed.setRegLocalizationReference(tmpRegLocalizationReference);
                        }

                        // Saving the RegLocalizationproposed
                        regLocalizationproposedManager.add(regLocalizationproposed);
                    }

                } else if (regField.getRegFieldtype().getLocalid().equals(BaseConstants.KEY_FIELD_TYPE_PARENT)) {
                    // Handling the Parent case
                    if (value.trim().length() > 0) {
                        RegItemManager regItemManager = new RegItemManager(entityManager);
                        RegRelationproposedManager regRelationproposedManager = new RegRelationproposedManager(entityManager);
                        RegRelationpredicateManager regRelationpredicateManager = new RegRelationpredicateManager(entityManager);

                        // Getting the right RegRelationPredicate
                        RegRelationpredicate regRelationPredicateRealtion = regRelationpredicateManager.get(BaseConstants.KEY_PREDICATE_PARENT);

                        // Getting the RegItem pointed by the relation
                        RegItem regItemRelation = regItemManager.get(value);

                        // Creating the new RegRelationproposed
                        String newRegRelationproposedUuid = RegRelationproposedUuidHelper.getUuid(regItemproposed, null, regRelationPredicateRealtion, null, regItemRelation);
                        RegRelationproposed newRegRelationproposed = new RegRelationproposed();
                        newRegRelationproposed.setInsertdate(new Date());
                        newRegRelationproposed.setRegItemproposedSubject(regItemproposed);
                        newRegRelationproposed.setRegRelationpredicate(regRelationPredicateRealtion);
                        newRegRelationproposed.setRegItemObject(regItemRelation);
                        newRegRelationproposed.setUuid(newRegRelationproposedUuid);

                        // Adding the newly created RegRelationproposed
                        regRelationproposedManager.add(newRegRelationproposed);

                    }
                } else if (regField.getRegFieldtype().getLocalid().equals(BaseConstants.KEY_FIELD_TYPE_GROUP)) {
                    // Handling the group case
                    if (value.trim().length() > 0) {
                        RegItemproposedRegGroupRegRoleMappingManager regItemproposedRegGroupRegRoleMappingManager = new RegItemproposedRegGroupRegRoleMappingManager(entityManager);
                        String newUuid = RegItemproposedRegGroupRegRoleMappingUuidHelper.getUuid(regItemproposed.getUuid(), value, regFieldmapping.getRegField().getRegRoleReference().getUuid());

                        try {
                            // Check if the mapping is already available
                            regItemproposedRegGroupRegRoleMappingManager.get(newUuid);

                        } catch (NoResultException e) {

                            //Getting the reg group passed from parameter
                            RegGroupManager regGroupManager = new RegGroupManager(entityManager);
                            RegGroup regGroup = null;
                            try {
                                regGroup = regGroupManager.get(value);

                                RegItemproposedRegGroupRegRoleMapping regItemproposedRegGroupRegRoleMapping = new RegItemproposedRegGroupRegRoleMapping();
                                regItemproposedRegGroupRegRoleMapping.setUuid(newUuid);
                                regItemproposedRegGroupRegRoleMapping.setInsertdate(new Date());
                                regItemproposedRegGroupRegRoleMapping.setRegGroup(regGroup);
                                regItemproposedRegGroupRegRoleMapping.setRegItemproposed(regItemproposed);
                                regItemproposedRegGroupRegRoleMapping.setRegRole(regFieldmapping.getRegField().getRegRoleReference());

                                regItemproposedRegGroupRegRoleMappingManager.add(regItemproposedRegGroupRegRoleMapping);

                            } catch (NoResultException e1) {
                            }
                        }
                    }
                } else {

                    // Check if the RegLocalizationproposed for the specific language is already there
                    RegLocalization regLocalizationReference = null;
                    try {
                        RegLocalizationproposed regLocalizationproposed;

                        // If the reference to the RegLocalizationReference is available,
                        // checking if the related regLocalizationproposed is available
                        try {
                            if (referenceRegLocalizationUuid == null) {
                                throw new NoResultException();
                            }

                            regLocalizationReference = regLocalizationManager.get(referenceRegLocalizationUuid);
                            regLocalizationproposed = regLocalizationproposedManager.getByRegLocalizationReferenceAndLanguage(regLocalizationReference, regLanguagecode);

                            // In case the current language is not the master language,
                            // search for eventual RegLocalization proposed in that language
                            if (!regLanguagecode.getMasterlanguage()) {
                                regLocalizationproposed = getRegLocalizationproposedInCurrentLanguage(regLocalizationproposed, regLocalizationproposedManager, regLanguagecode);
                            }

                        } catch (NoResultException ex) {
                            if (referenceRegLocalizationUuid == null) {
                                throw new NoResultException();
                            }
                            // Otherwise, the reference to the RegLocalizationproposed
                            // is stored in the same field name fragment: checking if a
                            /// new RegLocalizationproposed is already in the db
                            regLocalizationproposed = regLocalizationproposedManager.get(referenceRegLocalizationUuid);

                            // In case the current language is not the master language,
                            // search for eventual RegLocalization proposed in that language
                            if (!regLanguagecode.getMasterlanguage()) {
                                regLocalizationproposed = getRegLocalizationproposedInCurrentLanguage(regLocalizationproposed, regLocalizationproposedManager, regLanguagecode);
                            }

                            // If a new Localizationproposed is in the DB, removing the eventual
                            // reference to the RegLocalization and setting it to null because in this
                            // case it is not representing the related RegLocalization
                            regLocalizationReference = null;
                        }

                        // Checking if the field has been changed and needs to be updated
                        if ((regLocalizationproposed.getValue() == null && value != null && value.length() != 0) || (regLocalizationproposed.getValue() != null && !regLocalizationproposed.getValue().equals(value)) || (regFieldmapping.getHashref() && regLocalizationproposed.getHref() != null && !regLocalizationproposed.getHref().equals(href)) || (regFieldmapping.getHashref() && regLocalizationproposed.getHref() == null && href != null && href.length() > 0)) {

                            // Setting the values in the new RegLocalizationproposed
                            regLocalizationproposed.setValue(value);

                            // Updating the href if the RegFieldmapping has also the href
                            if (regFieldmapping.getHashref()) {
                                regLocalizationproposed.setHref((href != null && href.length() > 0) ? href : null);
                            }

                            // Setting the RegLocalization reference (if available)
                            if (regLocalizationReference != null) {
                                regLocalizationproposed.setRegLocalizationReference(regLocalizationReference);
                            }

                            regLocalizationproposed.setRegAction(regItemproposed.getRegAction());

                            regLocalizationproposedManager.add(regLocalizationproposed);
                        }

                    } // If the RegLocalizationproposed is not there copy and create it
                    // in the RegLocalizationproposed
                    catch (NoResultException e) {

                        // Getting the eventual regLocalization reference
                        if (referenceRegLocalizationUuid != null) {
                            try {
                                regLocalizationReference = regLocalizationManager.get(referenceRegLocalizationUuid);
                            } catch (NoResultException nr) {
                                regLocalizationReference = null;
                            }
                        } else {
                            regLocalizationReference = null;
                        }

                        if (value == null || (value != null && value.length() < 0)) {
                            value = "";
                        }

                        RegLocalizationproposed regLocalizationproposed = new RegLocalizationproposed();

                        // Getting the new fieldValueIndex (max of the field index
                        // of the localizations for the current field)
                        List<RegLocalizationproposed> tmpLocalizations = regLocalizationproposedManager.getAll(regField, regItemproposed, regLanguagecode);
                        int fieldValueIndexNew = -1;
                        for (RegLocalizationproposed tmpRegLocalization : tmpLocalizations) {
                            if (tmpRegLocalization.getFieldValueIndex() > fieldValueIndexNew) {
                                fieldValueIndexNew = tmpRegLocalization.getFieldValueIndex();
                            }
                        }
                        fieldValueIndexNew++;

                        // Creating the new uuid
                        String regLocalizationproposedUuid = "";
                        // If the regFieldType of the current field is number or date,
                        // the element is stored only in the master language
                        if (regField.getRegFieldtype().getLocalid().equals(BaseConstants.KEY_FIELD_TYPE_NUMBER) || regField.getRegFieldtype().getLocalid().equals(BaseConstants.KEY_FIELD_TYPE_DATE)) {
                            regLocalizationproposedUuid = RegLocalizationproposedUuidHelper.getUuid(fieldValueIndexNew, masterLanguage, regItemproposed, regField);
                        } else {
                            regLocalizationproposedUuid = RegLocalizationproposedUuidHelper.getUuid(fieldValueIndexNew, regLanguagecode, regItemproposed, regField);
                        }

                        // Setting fields
                        regLocalizationproposed.setUuid(regLocalizationproposedUuid);
                        regLocalizationproposed.setRegItemproposed(regItemproposed);
                        regLocalizationproposed.setRegField(regField);
                        regLocalizationproposed.setInsertdate(new Date());
                        regLocalizationproposed.setFieldValueIndex(fieldValueIndexNew);
                        regLocalizationproposed.setRegAction(regItemproposed.getRegAction());

                        // If the regFieldType of the current field is number or date,
                        // the element is stored only in the master language 
                        if (regField.getRegFieldtype().getLocalid().equals(BaseConstants.KEY_FIELD_TYPE_NUMBER) || regField.getRegFieldtype().getLocalid().equals(BaseConstants.KEY_FIELD_TYPE_DATE)) {
                            regLocalizationproposed.setRegLanguagecode(masterLanguage);
                        } else {
                            regLocalizationproposed.setRegLanguagecode(regLanguagecode);
                        }

                        // Setting the RegLocalization refderence (if available)
                        if (regLocalizationReference != null) {
                            regLocalizationproposed.setRegLocalizationReference(regLocalizationReference);
                        }

                        //Setting the value
                        regLocalizationproposed.setValue(value);

                        // Setting the href if the RegFieldmapping has also the href
                        if (regFieldmapping.getHashref()) {
                            regLocalizationproposed.setHref((href != null && href.length() > 0) ? href : null);
                        }

                        // Saving the RegLocalizationproposed
                        regLocalizationproposedManager.add(regLocalizationproposed);

                    }
                }

            } catch (NoResultException e) {
                // The form field is not a RegField: no action needed
            } catch (Exception e) {
                //logger.error(e.getMessage(), e);
            }
        }

        return errors;

    }

    private String processFieldHrefName(String key) {
        String fieldHrefName;

        // Checking if there is a reference to a RegLocalization
        String[] tmp;
        if (key.contains(BaseConstants.KEY_FORM_FIELD_NAME_REFERENCEKEY)) {
            tmp = key.split(BaseConstants.KEY_FORM_FIELD_NAME_REFERENCEKEY);
            fieldHrefName = tmp[0] + BaseConstants.KEY_FORM_FIELD_NAME_HREF_SUFFIX;
        } else {
            fieldHrefName = key + BaseConstants.KEY_FORM_FIELD_NAME_HREF_SUFFIX;
        }

        return fieldHrefName;
    }

    private RegLocalizationproposed getRegLocalizationproposedInCurrentLanguage(RegLocalizationproposed regLocalizationproposed, RegLocalizationproposedManager regLocalizationproposedManager, RegLanguagecode regLanguagecode) throws Exception {

        String tmpUuid = RegLocalizationproposedUuidHelper.getUuid(
                regLocalizationproposed.getFieldValueIndex(),
                regLanguagecode,
                regLocalizationproposed.getRegItemproposed(),
                regLocalizationproposed.getRegField()
        );
        regLocalizationproposed = regLocalizationproposedManager.get(tmpUuid);

        return regLocalizationproposed;
    }

    // This method handles the fields name (composed by multiple parts)
    private String[] processFieldName(String key) {
        String fieldUuid;
        String fieldValueIndex = null;
        String referenceRegLocalizationUuid = null;

        // Checking if there is a reference to a RegLocalization
        String[] tmp;
        if (key.contains(BaseConstants.KEY_FORM_FIELD_NAME_REFERENCEKEY)) {
            tmp = key.split(BaseConstants.KEY_FORM_FIELD_NAME_REFERENCEKEY);

            // Getting the field uuid and field value index
            String[] fieldTmp;
            String tmpUuidAndFieldValueIndex = tmp[0];
            fieldTmp = tmpUuidAndFieldValueIndex.split(BaseConstants.KEY_FORM_FIELD_NAME_INDEXKEY);
            fieldUuid = fieldTmp[0];
            fieldValueIndex = fieldTmp[1];

            // Getting the referenc e localization
            referenceRegLocalizationUuid = (tmp.length > 1) ? tmp[1] : null;
        } else if (key.contains(BaseConstants.KEY_FORM_FIELD_NAME_INDEXKEY) && !key.contains(BaseConstants.KEY_FORM_FIELD_NAME_HREF_SUFFIX)) {
            String[] fieldTmp;
            fieldTmp = key.split(BaseConstants.KEY_FORM_FIELD_NAME_INDEXKEY);
            fieldUuid = fieldTmp[0];
            fieldValueIndex = fieldTmp[1];
        } else {
            fieldUuid = key;
        }

        // Removing the field name index
        String[] outs = new String[3];
        outs[0] = fieldUuid;
        outs[1] = fieldValueIndex;
        outs[2] = referenceRegLocalizationUuid;

        return outs;
    }

    private void storeLocalization(RegLanguagecode fieldLanguage, RegItemproposed regItemproposed, RegField regField, RegLanguagecode masterLanguage, String fieldValue, String localId, ArrayList<FieldsBulkImport> array) throws Exception {
        RegLocalizationproposedManager regLocalizationproposedManager = new RegLocalizationproposedManager(entityManager);

        RegLocalizationproposed regLocalizationproposed = new RegLocalizationproposed();
        final String uuidLocalizationField = RegLocalizationproposedUuidHelper.getUuid(0, fieldLanguage, regItemproposed, regField);

        String uuidLocalizationMasterField = RegLocalizationproposedUuidHelper.getUuid(0, masterLanguage, regItemproposed, regField);
//      add localization in masterlanguage
        if (masterLanguage.equals(fieldLanguage)) {
            regLocalizationproposed.setUuid(uuidLocalizationMasterField);
            regLocalizationproposed.setFieldValueIndex(0);
            regLocalizationproposed.setInsertdate(new Date());
            regLocalizationproposed.setRegField(regField);
            regLocalizationproposed.setRegItemproposed(regItemproposed);
            regLocalizationproposed.setRegLanguagecode(fieldLanguage);
            regLocalizationproposed.setRegLocalizationReference(null);
            regLocalizationproposed.setValue(fieldValue);
            regLocalizationproposed.setHref(null);
            regLocalizationproposed.setRegAction(regItemproposed.getRegAction());

            regLocalizationproposedManager.add(regLocalizationproposed);
        } else {
//      check if exist localization in master language
            try {
                regLocalizationproposedManager.get(uuidLocalizationMasterField);

//              if master language exist add the new language
                regLocalizationproposed.setUuid(uuidLocalizationField);
                regLocalizationproposed.setFieldValueIndex(0);
                regLocalizationproposed.setInsertdate(new Date());
                regLocalizationproposed.setRegField(regField);
                regLocalizationproposed.setRegItemproposed(regItemproposed);
                regLocalizationproposed.setRegLanguagecode(fieldLanguage);
                regLocalizationproposed.setRegLocalizationReference(null);
                regLocalizationproposed.setValue(fieldValue);
                regLocalizationproposed.setHref(null);
                regLocalizationproposed.setRegAction(regItemproposed.getRegAction());

                regLocalizationproposedManager.add(regLocalizationproposed);
            } catch (Exception exx) {
                //if master language doesnt exist add item to a list
                String masterLanguagemissing = systemLocalization.getString("bulk.import.error.missingmasterlanguage").
                        replace(SUBSTITUTE_LANGUAGE, masterLanguage.getIso6391code()).
                        replace(SUBSTITUTE_ITEMLOCALID, localId) + "</b>";
                operationResult = operationResult + BR_HTML + masterLanguagemissing;
            }
        }
    }

    private void storeRelarionReference(String fieldValue, RegField regField, RegItemproposed regItemproposed, HashMap<RegField, RegItem> mapCollection, RegItemclass regItemclassChild, RegLanguagecode fieldLanguage, String localId, FieldsBulkImport fieldsBulkImport) {
        RegRelationproposedManager regRelationproposedManager = new RegRelationproposedManager(entityManager);
        RegRelationpredicateManager regRelationpredicateManager = new RegRelationpredicateManager(entityManager);
        RegLocalizationproposedManager regLocalizationproposedManager = new RegLocalizationproposedManager(entityManager);
        RegItemManager regItemManager = new RegItemManager(entityManager);
        ArrayList<String> relationReferenceList = new ArrayList<>();

        if (fieldValue.contains(",")) {
            relationReferenceList = new ArrayList<>(Arrays.asList(fieldValue.split(",")));
        } else {
            relationReferenceList.add(fieldValue);
        }

        int i = 0;
        for (String valueOfTheField : relationReferenceList) {

            try {
                RegItemclass itemClassRelationReference = regField.getRegItemclassReference();
                String relationreferenceuuid = "";

                try {
                    if (mapCollection.containsKey(regField)) {
                        RegItem itemRelationReferenceCollection = mapCollection.get(regField);
                        relationreferenceuuid = RegItemUuidHelper.getUuid(valueOfTheField, itemRelationReferenceCollection, itemClassRelationReference);
                    } else {
                        relationreferenceuuid = RegItemUuidHelper.getUuid(valueOfTheField, null, itemClassRelationReference);
                    }

                    RegItem regItemRelationReference = regItemManager.get(relationreferenceuuid);

                    //create relation
                    RegRelationpredicate regRelationpredicateReference = regRelationpredicateManager.get(BaseConstants.KEY_PREDICATE_REFERENCE);
                    RegRelationproposed regRelationproposedReference = new RegRelationproposed();
                    regRelationproposedReference.setInsertdate(new Date());
                    regRelationproposedReference.setRegItemSubject(null);
                    regRelationproposedReference.setRegItemproposedSubject(regItemproposed);
                    regRelationproposedReference.setUuid(RegRelationproposedUuidHelper.getUuid(regItemproposed, null, regRelationpredicateReference, null, regItemRelationReference));
                    regRelationproposedReference.setRegItemObject(regItemRelationReference);

                    regRelationproposedReference.setRegItemproposedObject(null);
                    regRelationproposedReference.setRegRelationReference(null);
                    regRelationproposedReference.setRegRelationpredicate(regRelationpredicateReference);

                    regRelationproposedManager.add(regRelationproposedReference);

//                                            create localization for red relation proposed
                    RegLocalizationproposed regLocalizationproposed = new RegLocalizationproposed();
                    regLocalizationproposed.setUuid(RegLocalizationproposedUuidHelper.getUuid(i, fieldLanguage, regItemproposed, regField));
                    regLocalizationproposed.setRegLanguagecode(fieldLanguage);
                    regLocalizationproposed.setRegItemproposed(regItemproposed);
                    regLocalizationproposed.setRegField(regField);
                    regLocalizationproposed.setRegRelationproposedReference(regRelationproposedReference);
                    regLocalizationproposed.setInsertdate(new Date());
                    regLocalizationproposed.setRegAction(regItemproposed.getRegAction());

                    regLocalizationproposedManager.add(regLocalizationproposed);
                } catch (Exception ex) {
                    relationreferenceuuid = RegItemproposedUuidHelper.getUuid(valueOfTheField, null, regItemclassChild, null);
                    RegItemproposedManager regItemproposedManager = new RegItemproposedManager(entityManager);
                    RegItemproposed regItemproposedrelation = regItemproposedManager.get(relationreferenceuuid);

                    String relation_error = systemLocalization.getString("bulk.import.error.relationreferenceitem.notexistent")
                            .replace(SUBSTITUTE_LOCALID, localId)
                            .replace("{field}", regField.getLocalid())
                            .replace("{fieldValue}", fieldValue)
                            .replace(SUBSTITUTE_LINE, String.valueOf(fieldsBulkImport.getLine()));
                    operationResult = operationResult + BR_HTML + relation_error;
                }

            } catch (Exception ex) {
                String relation_error = systemLocalization.getString("bulk.import.error.relationreferenceitem.notexistent")
                        .replace(SUBSTITUTE_LOCALID, localId)
                        .replace("{field}", regField.getLocalid())
                        .replace("{fieldValue}", fieldValue)
                        .replace(SUBSTITUTE_LINE, String.valueOf(fieldsBulkImport.getLine()));
                operationResult = operationResult + BR_HTML + relation_error;
            }
            i++;
        }
    }

    private void storeParent(String fieldValue, RegItem regItemContainer, RegItemproposed regItemproposed, String localId, FieldsBulkImport fieldsBulkImport) {
        RegRelationproposedManager regRelationproposedManager = new RegRelationproposedManager(entityManager);
        RegRelationpredicateManager regRelationpredicateManager = new RegRelationpredicateManager(entityManager);
        RegItemManager regItemManager = new RegItemManager(entityManager);
        /*check if the parent exist */
        try {
            //create relation
            RegRelationpredicate regRelationpredicateParent = regRelationpredicateManager.get(BaseConstants.KEY_PREDICATE_PARENT);
            RegRelationproposed regRelationproposedParent = new RegRelationproposed();
            try {
                RegItem parentItem;
                try {
                    String parentuuid = RegItemUuidHelper.getUuid(fieldValue, null, regItemproposed.getRegItemclass());
                    parentItem = regItemManager.get(parentuuid);
                } catch (Exception ex) {
                    String parentuuid = RegItemUuidHelper.getUuid(fieldValue, regItemContainer, regItemproposed.getRegItemclass());
                    parentItem = regItemManager.get(parentuuid);
                }
                regRelationproposedParent.setUuid(RegRelationproposedUuidHelper.getUuid(regItemproposed, null, regRelationpredicateParent, null, parentItem));
                regRelationproposedParent.setRegItemObject(parentItem);
                regRelationproposedParent.setRegItemproposedObject(null);

                regRelationproposedParent.setInsertdate(new Date());
                regRelationproposedParent.setRegItemSubject(null);
                regRelationproposedParent.setRegItemproposedSubject(regItemproposed);
                regRelationproposedParent.setRegRelationReference(null);
                regRelationproposedParent.setRegRelationpredicate(regRelationpredicateParent);

                regRelationproposedManager.add(regRelationproposedParent);
            } catch (Exception ex) {
                String parentuuid = RegItemproposedUuidHelper.getUuid(fieldValue, null, regItemproposed.getRegItemclass(), null);
                RegItemproposedManager regItemproposedManager = new RegItemproposedManager(entityManager);
                RegItemproposed regItemproposedrelation = regItemproposedManager.get(parentuuid);

                String parent_error = systemLocalization.getString("bulk.import.error.parent.notexistent")
                        .replace(SUBSTITUTE_LOCALID, localId)
                        .replace("{parentLocalID}", fieldValue)
                        .replace(SUBSTITUTE_LINE, String.valueOf(fieldsBulkImport.getLine()));
                operationResult = operationResult + BR_HTML + parent_error;
            }
        } catch (Exception ex) {
//                                        check if the parent exist in the file
            String parent_error = systemLocalization.getString("bulk.import.error.parent.notexistent")
                    .replace(SUBSTITUTE_LOCALID, localId)
                    .replace("{parentLocalID}", fieldValue)
                    .replace(SUBSTITUTE_LINE, String.valueOf(fieldsBulkImport.getLine()));
            operationResult = operationResult + BR_HTML + parent_error;
        }
    }

    private void addRegistryRegisterRelation(RegItem regItemCollection, RegItemproposed regItemproposed) throws Exception {
        RegRelationpredicateManager regRelationpredicateManager = new RegRelationpredicateManager(entityManager);
        RegItemManager regItemManager = new RegItemManager(entityManager);
        RegRelationproposedManager regRelationproposedManager = new RegRelationproposedManager(entityManager);
        //                                        if the language inserted is master language add relation registry
        RegRelationManager regRelationManager = new RegRelationManager(entityManager);
        // Loading the needed RegRelation predicates (to create the URI)
        RegRelationpredicate regRelationpredicateRegistry = regRelationpredicateManager.get(BaseConstants.KEY_PREDICATE_REGISTRY);
        RegRelationpredicate regRelationpredicateRegister = regRelationpredicateManager.get(BaseConstants.KEY_PREDICATE_REGISTER);
        // Setting the registry
        RegItem regItemRegistry = null;
        if (regItemCollection != null && !regItemCollection.getRegItemclass().getRegItemclasstype().getLocalid().equals(BaseConstants.KEY_ITEMCLASS_TYPE_REGISTRY)) {
            List<RegRelation> regRelationRegistries = regRelationManager.getAll(regItemCollection, regRelationpredicateRegistry);
            try {
                regItemRegistry = regRelationRegistries.get(0).getRegItemObject();
            } catch (Exception e) {
                regItemRegistry = null;
            }
        }

        // Setting the register
        RegItem regItemRegister = null;
        if (regItemCollection != null && !regItemCollection.getRegItemclass().getRegItemclasstype().getLocalid().equals(BaseConstants.KEY_ITEMCLASS_TYPE_REGISTER)) {
            List<RegRelation> regRelationRegisters = regRelationManager.getAll(regItemCollection, regRelationpredicateRegister);
            try {
                regItemRegister = regRelationRegisters.get(0).getRegItemObject();
            } catch (Exception e) {
                regItemRegister = null;
            }
        } else {
            regItemRegister = regItemCollection;
        }

        if (regItemproposed != null) {

            RegRelationproposed regRelationproposedRegistry = new RegRelationproposed();
            final String uuidRegistry = RegRelationproposedUuidHelper.getUuid(regItemproposed, null, regRelationpredicateRegistry, null, regItemRegistry);

            try {
                regItemManager.get(uuidRegistry);
            } catch (Exception exx) {
                regRelationproposedRegistry.setUuid(uuidRegistry);
                regRelationproposedRegistry.setInsertdate(new Date());
                regRelationproposedRegistry.setRegItemproposedSubject(regItemproposed);
                regRelationproposedRegistry.setRegItemSubject(null);
                regRelationproposedRegistry.setRegItemObject(regItemRegistry);
                regRelationproposedRegistry.setRegItemproposedObject(null);
                regRelationproposedRegistry.setRegRelationReference(null);
                regRelationproposedRegistry.setRegRelationpredicate(regRelationpredicateRegistry);

                regRelationproposedManager.add(regRelationproposedRegistry);
            }

//                                        if the language inserted is master language add relation register
            RegRelationproposed regRelationproposedRegister = new RegRelationproposed();
            final String uuidRegister = RegRelationproposedUuidHelper.getUuid(regItemproposed, null, regRelationpredicateRegister, null, regItemRegister);
            try {
                regItemManager.get(uuidRegister);
            } catch (Exception exx) {
                regRelationproposedRegister.setUuid(uuidRegister);
                regRelationproposedRegister.setInsertdate(new Date());
                regRelationproposedRegister.setRegItemproposedSubject(regItemproposed);
                regRelationproposedRegister.setRegItemSubject(null);
                regRelationproposedRegister.setRegItemObject(regItemRegister);
                regRelationproposedRegister.setRegItemproposedObject(null);
                regRelationproposedRegister.setRegRelationReference(null);
                regRelationproposedRegister.setRegRelationpredicate(regRelationpredicateRegister);

                regRelationproposedManager.add(regRelationproposedRegister);
            }
        }
    }

    private void addCollectionRelation(RegItem regItemContainer, RegItemproposed regItemproposed) throws Exception {
        String parentItemClassLocalID = regItemproposed.getRegItemclass().getRegItemclassParent().getRegItemclasstype().getLocalid();
        final String regItemProposedItemClassLocalID = regItemproposed.getRegItemclass().getRegItemclasstype().getLocalid();

        if (parentItemClassLocalID.equals(regItemProposedItemClassLocalID)
                && regItemProposedItemClassLocalID.equals(BaseConstants.KEY_ITEMCLASS_TYPE_ITEM)) {

            RegItemManager regItemManager = new RegItemManager(entityManager);
            RegRelationproposedManager regRelationproposedManager = new RegRelationproposedManager(entityManager);

            // Loading the needed RegRelation predicates (to create the URI)
            RegRelationpredicateManager regRelationpredicateManager = new RegRelationpredicateManager(entityManager);
            RegRelationpredicate regRelationpredicateCollection = regRelationpredicateManager.get(BaseConstants.KEY_PREDICATE_COLLECTION);

            if (regItemproposed != null) {
                RegRelationproposed regRelationproposedRegistry = new RegRelationproposed();
                final String uuidRegistry = RegRelationproposedUuidHelper.getUuid(regItemproposed, null, regRelationpredicateCollection, null, regItemContainer);

                try {
                    regItemManager.get(uuidRegistry);
                } catch (Exception exx) {
                    regRelationproposedRegistry.setUuid(uuidRegistry);
                    regRelationproposedRegistry.setInsertdate(new Date());
                    regRelationproposedRegistry.setRegItemproposedSubject(regItemproposed);
                    regRelationproposedRegistry.setRegItemSubject(null);
                    regRelationproposedRegistry.setRegItemObject(regItemContainer);
                    regRelationproposedRegistry.setRegItemproposedObject(null);
                    regRelationproposedRegistry.setRegRelationReference(null);
                    regRelationproposedRegistry.setRegRelationpredicate(regRelationpredicateCollection);

                    regRelationproposedManager.add(regRelationproposedRegistry);
                }
            }
        }
    }

    private RegItemproposed createRegItemProposed(String itemRecordLocalID, RegItemclass regItemclass, RegUser regUser, RegAction regAction) throws Exception {
        RegStatusManager regStatusManager = new RegStatusManager(entityManager);

        RegItemproposedManager regItemproposedManager = new RegItemproposedManager(entityManager);

        RegItemproposed regItemproposed = new RegItemproposed();
        //                add item proposed if not existent
        try {
            regItemproposed = regItemproposedManager.get(itemRecordLocalID);
        } catch (Exception ex) {

            //Getting the status draft
            RegStatus regStatusDraft = regStatusManager.findByLocalid(BaseConstants.KEY_STATUS_LOCALID_DRAFT);
            /**
             * create RegItem for the register
             */
            regItemproposed.setUuid(RegItemproposedUuidHelper.getUuid(itemRecordLocalID, null, regItemclass, null));

            regItemproposed.setLocalid(itemRecordLocalID);
            regItemproposed.setInsertdate(new Date());
            regItemproposed.setRegItemReference(null);
            regItemproposed.setRegItemclass(regItemclass);
            regItemproposed.setRegUser(regUser);
            regItemproposed.setRegStatus(regStatusDraft);
            regItemproposed.setRorExport(true);

            if (itemRecordLocalID.startsWith(BaseConstants.KEY_PARAMETER_HTTP) || itemRecordLocalID.startsWith(BaseConstants.KEY_PARAMETER_HTTPS)) {
                regItemproposed.setExternal(Boolean.TRUE);
            } else {
                regItemproposed.setExternal(Boolean.FALSE);
            }

        }
        regItemproposed.setRegAction(regAction);

        regItemproposedManager.add(regItemproposed);
        return regItemproposed;
    }

    private RegAction addRegActionForAllProposedItemsCSV(RegUser regUser, RegItem regItem, RegItemclass regItemclass) throws Exception {
        RegStatusManager regStatusManager = new RegStatusManager(entityManager);
        RegStatus regStatus = regStatusManager.findByLocalid(BaseConstants.KEY_STATUS_LOCALID_DRAFT);

        ResourceBundle systemLocalizationBundle = Configuration.getInstance().getLocalization();
        String defaultLabel = systemLocalizationBundle.getString("label.actionon") + " " + regItemclass.getLocalid() + " made by:" + regUser.getEmail() + " Data:" + new Date().toString();

        RegAction regAction = new RegAction();
        regAction.setUuid(RegActionUuidHelper.getUuid(regUser, new Date()));
        regAction.setLabel(defaultLabel);
        regAction.setInsertdate(new Date());
        regAction.setRegUser(regUser);
        regAction.setSubmittedBy(regUser);

        RegRelationpredicateManager relationpredicateManager = new RegRelationpredicateManager(entityManager);
        RegRelationManager regRelationManager = new RegRelationManager(entityManager);

        try {
            regAction.setRegItemRegistry(regRelationManager.getAllByRegItemSubjectAndPredicate(regItem, relationpredicateManager.get(BaseConstants.KEY_PREDICATE_REGISTRY)).get(0).getRegItemObject());
        } catch (Exception exception) {

        }
        try {
            regAction.setRegItemRegister(regRelationManager.getAllByRegItemSubjectAndPredicate(regItem, relationpredicateManager.get(BaseConstants.KEY_PREDICATE_REGISTER)).get(0).getRegItemObject());
        } catch (Exception exception) {
            if (regItem.getRegItemclass().getRegItemclasstype().getLocalid().equals(BaseConstants.KEY_ITEMCLASS_TYPE_REGISTER)) {
                regAction.setRegItemRegister(regItem);
            }
        }
        regAction.setChangeRequest(null);
        regAction.setRejectMessage(null);
        regAction.setIssueTrackerLink(null);
        regAction.setRegStatus(regStatus);
        regAction.setChangesImplemented(false);

        RegActionManager regActionManager = new RegActionManager(entityManager);
        regActionManager.add(regAction);
        return regAction;
    }
}
