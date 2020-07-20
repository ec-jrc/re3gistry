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
import eu.europa.ec.re3gistry2.base.utility.MailManager;
import eu.europa.ec.re3gistry2.base.utility.PersistenceFactory;
import eu.europa.ec.re3gistry2.crudimplementation.RegActionManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegFieldManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegFieldmappingManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegItemManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegItemclassManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegItemproposedManager;
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
import eu.europa.ec.re3gistry2.model.RegItem;
import eu.europa.ec.re3gistry2.model.RegItemclass;
import eu.europa.ec.re3gistry2.model.RegItemproposed;
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
import eu.europa.ec.re3gistry2.model.uuidhandlers.RegItemproposedUuidHelper;
import eu.europa.ec.re3gistry2.model.uuidhandlers.RegLocalizationproposedUuidHelper;
import eu.europa.ec.re3gistry2.model.uuidhandlers.RegRelationproposedUuidHelper;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;
import javax.mail.internet.InternetAddress;
import javax.persistence.EntityManager;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;
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

//        get itemclass with parent regItemclass
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

        try (ServletOutputStream output = response.getOutputStream()) {
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

//        add all the fields in the order of order list
        for (RegFieldmapping field : regFieldMappingListWithoutStatus) {
            writer.append("|");

            if (field.getRegField().getRegFieldtype().getUuid().equals(BaseConstants.KEY_FIELDTYPE_RELATIONREFERENCE_UUID)
                    || field.getRegField().getRegFieldtype().getUuid().equals(BaseConstants.KEY_FIELDTYPE_PARENT_UUID)) {
                writer.append(field.getRegField().getLocalid());
                writer.append("|");
                String collectionField = BaseConstants.KEY_BULK_COLLECTION + field.getRegField().getLocalid();
                writer.append(collectionField);
            } else {
                writer.append(field.getRegField().getLocalid());
            }
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
                    if (itemsBulkImport != null && !itemsBulkImport.isEmpty()) {
                        storeItems(itemsBulkImport, regItem, regUser);
                        LOGGER.info("### END STORING ITEMS WITH SUCCESS ###");
                        LOGGER.info("###");
                        operationResult = operationResult + "<b>" + systemLocalization.getString("bulk.import.success") + "</b>";
                        request.setAttribute(BaseConstants.KEY_REQUEST_BULK_SUCCESS, operationResult);

                        subject = systemLocalization.getString(BaseConstants.KEY_EMAIL_SUBJECT_BULKIMPORT_SUCCESS);
                        body = systemLocalization.getString(BaseConstants.KEY_EMAIL_BODY_BULKIMPORT_SUCCESS);
                    }
                } catch (Exception ex) {
//                    operationResult = "<b>" + systemLocalization.getString("bulk.import.error.emptyfile") + "</b>" + BR_HTML + operationResult;
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
//            operationResult = BR_HTML + operationResult;
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
        HashMap<String, ArrayList<FieldsBulkImport>> itemsParentAndRelationToBeAddedAtTheEnd = new HashMap<>();

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
                                    addFieldParent(mapCollection, regField, fieldValue, regItemclassChild, itemsBulkImport, itemsParentAndRelationToBeAddedAtTheEnd, localId, array, fieldsBulkImport);
                                    break;
                                case BaseConstants.KEY_FIELDTYPE_RELATIONREFERENCE_UUID:
                                    addFieldRelationReference(regField, mapCollection, fieldValue, regItemclassChild, itemsBulkImport, itemsParentAndRelationToBeAddedAtTheEnd, localId, array, fieldsBulkImport);
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

        //TODO i dont know why i put it here
//        if (!itemsParentAndRelationToBeAddedAtTheEnd.isEmpty()) {
//            checkFiledsFromMap(itemsParentAndRelationToBeAddedAtTheEnd, regItemclassChild, regItemCollection);
//        }
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

    private void addFieldRelationReference(RegField regField, HashMap<RegField, RegItem> mapCollection, String fieldValue, RegItemclass regItemclassChild, HashMap<String, ArrayList<FieldsBulkImport>> itemsBulkImport, HashMap<String, ArrayList<FieldsBulkImport>> itemsParentAndRelationToBeAddedAtTheEnd, String localId, ArrayList<FieldsBulkImport> array, FieldsBulkImport fieldsBulkImport) {
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
            }

        } catch (Exception ex) {

            boolean relationreferenceExistInTheFile = false;
            if (regField.getRegItemclassReference().equals(regItemclassChild)) {

                for (Map.Entry<String, ArrayList<FieldsBulkImport>> item : itemsBulkImport.entrySet()) {
                    String localIdrelationreference = item.getKey();
                    if (localIdrelationreference.equals(fieldValue)) {
                        itemsParentAndRelationToBeAddedAtTheEnd.put(localId, array);
                        relationreferenceExistInTheFile = true;
                        break;
                    }
                }
            }

            if (!relationreferenceExistInTheFile) {
                if (!itemsParentAndRelationToBeAddedAtTheEnd.containsKey(localId)) {
                    itemsParentAndRelationToBeAddedAtTheEnd.put(localId, array);
                } else {
                    String relation_error = systemLocalization.getString("bulk.import.error.relationreferenceitem.notexistent")
                            .replace(SUBSTITUTE_LOCALID, localId)
                            .replace("{field}", regField.getLocalid())
                            .replace("{fieldValue}", fieldValue)
                            .replace(SUBSTITUTE_LINE, String.valueOf(fieldsBulkImport.getLine()));
                    operationResult = operationResult + BR_HTML + relation_error;
                }
            }
        }
    }

    private void addFieldParent(HashMap<RegField, RegItem> mapCollection, RegField regField, String fieldValue, RegItemclass regItemclassChild, HashMap<String, ArrayList<FieldsBulkImport>> itemsBulkImport, HashMap<String, ArrayList<FieldsBulkImport>> itemsParentAndRelationToBeAddedAtTheEnd, String localId, ArrayList<FieldsBulkImport> array, FieldsBulkImport fieldsBulkImport) {
        RegItemManager regItemManager = new RegItemManager(entityManager);
        /*check if the parent exist */
        try {
            //create relation
            try {
                if (mapCollection.containsKey(regField)) {
                    mapCollection.get(regField);
                } else {
                    String parentuuid = RegItemUuidHelper.getUuid(fieldValue, null, regItemclassChild);
                    regItemManager.get(parentuuid);
                }
            } catch (Exception ex) {
                String parentuuid = RegItemproposedUuidHelper.getUuid(fieldValue, null, regItemclassChild, null);
                RegItemproposedManager regItemproposedManager = new RegItemproposedManager(entityManager);
                regItemproposedManager.get(parentuuid);
            }

        } catch (Exception ex) {

//                                        check if the parent exist in the file
            boolean parentExistInTheFile = false;
            for (Map.Entry<String, ArrayList<FieldsBulkImport>> item : itemsBulkImport.entrySet()) {
                String localIdParent = item.getKey();
                if (localIdParent.equals(fieldValue)) {
                    itemsParentAndRelationToBeAddedAtTheEnd.put(localId, array);
                    parentExistInTheFile = true;
                    break;
                }
            }

            if (!parentExistInTheFile) {
                String parent_error = systemLocalization.getString("bulk.import.error.parent.notexistent")
                        .replace(SUBSTITUTE_LOCALID, localId)
                        .replace("{parentLocalID}", fieldValue)
                        .replace(SUBSTITUTE_LINE, String.valueOf(fieldsBulkImport.getLine()));
                operationResult = operationResult + BR_HTML + parent_error;
            }
        }
    }

    private RegItemclass getItemClassChildren(RegItem regItemCollection) {
        //                get item class of the item to be inserted
        RegItemclass regItemclassChild = null;
        try {
            RegItemclassManager regItemclassManager = new RegItemclassManager(entityManager);
            List<RegItemclass> child = regItemclassManager.getChildItemclass(regItemCollection.getRegItemclass());
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
        for (String additionLine : additionLines) {
            String[] lineSplitted = additionLine.split("\\|", -1);
            List<String> listListSplitted = new ArrayList<>(Arrays.asList(lineSplitted));

            final String localId = listListSplitted.get(0);
            final String language2Letters = listListSplitted.get(1);

            RegLanguagecodeManager regLanguagecodeManager = new RegLanguagecodeManager(entityManager);
            RegLanguagecode regLanguage = null;
            try {
                regLanguage = regLanguagecodeManager.getByIso6391code(language2Letters);
            } catch (Exception ex) {
                String error_language = systemLocalization.getString("bulk.import.error.language.notexistent")
                        .replace(SUBSTITUTE_LANGUAGE, language2Letters)
                        .replace(SUBSTITUTE_LINE, String.valueOf(line));
                LOGGER.error(error_language);
                operationResult = operationResult + BR_HTML + error_language;

                continue;
            }

            HashMap<RegField, String> hashMap = new HashMap<>();
            HashMap<RegField, RegItem> hashCollectionMap = new HashMap<>();

            fillMapWithFiledsAndCollectionMapWithFieldsCollection(listListSplitted, csvHeader, hashMap, hashCollectionMap, localId, line);

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
                    regItemManager.get(RegItemUuidHelper.getUuid(localId, regItemCollection, regItemclassChild));
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
                operationResult = operationResult + "<b>" + systemLocalization.getString("bulk.import.error.missingmasterlanguage").replace(SUBSTITUTE_LANGUAGE, masterLanguage.getIso6391code()).replace(SUBSTITUTE_ITEMLOCALID, localId) + "</b>";
            }
        }

        if (!operationResult.isEmpty()) {
//            request.setAttribute(BaseConstants.KEY_REQUEST_BULK_ERROR, operationResult);
            throw new Exception(operationResult);
        }

//        for (Map.Entry<String, ArrayList<FieldsBulkImport>> entry : itemsBulkImport.entrySet()) {
//             String localId = entry.getKey();
//            ArrayList<FieldsBulkImport> value = entry.getValue();
//        }
//        
        return itemsBulkImport;
    }

    private void fillMapWithFiledsAndCollectionMapWithFieldsCollection(List<String> listListSplitted, CsvHeader csvHeader, HashMap<RegField, String> hashMap, HashMap<RegField, RegItem> hashCollectionMap, final String localId, int line) {
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
                            RegItemclass regItemCollectionItemclass = fieldbyLocalid.getRegItemclassReference().getRegItemclassParent();
                            String uuidCollection = RegItemUuidHelper.getUuid(value, null, regItemCollectionItemclass);
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
            }
        }
    }

    private void storeItems(HashMap<String, ArrayList<FieldsBulkImport>> itemsBulkImport, RegItem regItemCollection, RegUser regUser) throws Exception {
        RegItemclass regItemclassChild = getItemClassChildren(regItemCollection);

        if (regItemclassChild != null) {
            RegAction regAction = addRegActionForAllProposedItemsCSV(regUser, regItemCollection, regItemclassChild);
            if (regAction != null) {

                storeProposedItems(itemsBulkImport, regItemCollection, regUser, regItemclassChild, regAction);

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

    private void storeProposedItems(HashMap<String, ArrayList<FieldsBulkImport>> itemsBulkImport, RegItem regItemCollection, RegUser regUser, RegItemclass regItemclassChild, RegAction regAction) throws Exception {
        HashMap<String, ArrayList<FieldsBulkImport>> itemsParentAndRelationToBeAddedAtTheEnd = new HashMap<>();

        for (Map.Entry<String, ArrayList<FieldsBulkImport>> items : itemsBulkImport.entrySet()) {
            String localId = items.getKey();

            RegItemproposed regItemproposed = createItemProposed(regItemCollection, regItemclassChild, localId, regUser, regAction);

            ArrayList<FieldsBulkImport> array = items.getValue();

            RegLanguagecodeManager regLanguagecodeManager = new RegLanguagecodeManager(entityManager);
            RegLanguagecode masterLanguage = regLanguagecodeManager.getMasterLanguage();

            ArrayList<FieldsBulkImport> fieldsBulkImportListLocal = new ArrayList<>();
            for (FieldsBulkImport fieldsBulkImport : array) {

                RegLanguagecode fieldLanguage = fieldsBulkImport.getLanguage();

                if (fieldLanguage.equals(masterLanguage)) {
                    storeItemFromBulk(fieldsBulkImport, regItemclassChild, regItemproposed, itemsBulkImport, itemsParentAndRelationToBeAddedAtTheEnd, localId, array, fieldLanguage, masterLanguage);
                } else {
                    fieldsBulkImportListLocal.add(fieldsBulkImport);
                }
            }
            if (!fieldsBulkImportListLocal.isEmpty()) {
                for (FieldsBulkImport fieldsBulkImport : fieldsBulkImportListLocal) {
                    RegLanguagecode fieldLanguage = fieldsBulkImport.getLanguage();
                    storeItemFromBulk(fieldsBulkImport, regItemclassChild, regItemproposed, itemsBulkImport, itemsParentAndRelationToBeAddedAtTheEnd, localId, array, fieldLanguage, masterLanguage);
                }

            }

        }

        if (!itemsParentAndRelationToBeAddedAtTheEnd.isEmpty()) {
            checkFiledsFromMap(itemsParentAndRelationToBeAddedAtTheEnd, regItemclassChild, regItemCollection);
        }
    }

    private void storeItemFromBulk(FieldsBulkImport fieldsBulkImport, RegItemclass regItemclassChild, RegItemproposed regItemproposed, HashMap<String, ArrayList<FieldsBulkImport>> itemsBulkImport, HashMap<String, ArrayList<FieldsBulkImport>> itemsParentAndRelationToBeAddedAtTheEnd, String localId, ArrayList<FieldsBulkImport> array, RegLanguagecode fieldLanguage, RegLanguagecode masterLanguage) throws Exception {
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
                                storeParent(mapCollection, regField, fieldValue, regItemclassChild, regItemproposed, itemsBulkImport, itemsParentAndRelationToBeAddedAtTheEnd, localId, array);
                            }
                            break;
                        case BaseConstants.KEY_FIELDTYPE_RELATIONREFERENCE_UUID:
                            if (fieldLanguage.equals(masterLanguage)) {
                                storeRelarionReference(fieldValue, regField, regItemproposed, mapCollection, regItemclassChild, fieldLanguage, itemsBulkImport, itemsParentAndRelationToBeAddedAtTheEnd, localId, array);
                            }
                            break;
                        default:
                            storeLocalization(fieldLanguage, regItemproposed, regField, masterLanguage, fieldValue, itemsParentAndRelationToBeAddedAtTheEnd, localId, array);
                            break;
                    }
                }
            }
        }
    }

    private RegItemproposed createItemProposed(RegItem regItemCollection, RegItemclass regItemclassChild, String localId, RegUser regUser, RegAction regAction) throws Exception {
        RegItemManager regItemManager = new RegItemManager(entityManager);
        RegItemproposed regItemproposed = null;
        try {
            //            check if item exist already, if yes copy the item in itemproposed
            String regItemExistentUuid;
            if (regItemCollection.getRegItemclass().equals(regItemclassChild)) {
                regItemExistentUuid = RegItemUuidHelper.getUuid(localId, regItemCollection, regItemclassChild);
            } else {
                regItemExistentUuid = RegItemUuidHelper.getUuid(localId, null, regItemclassChild);
            }
            RegItem regItemExist = regItemManager.get(regItemExistentUuid);

            RegItemproposedHandler regItemproposedHandler = new RegItemproposedHandler();
            regItemproposed = regItemproposedHandler.completeCopyRegItemToRegItemporposed(regItemExist, regUser);

        } catch (Exception ex) {
//                    create propose item
            regItemproposed = createRegItemProposed(localId, regItemclassChild, regUser, regAction);
            addRegistryRegisterRelation(regItemCollection, regItemproposed);
        }
        return regItemproposed;
    }

    private void storeLocalization(RegLanguagecode fieldLanguage, RegItemproposed regItemproposed, RegField regField, RegLanguagecode masterLanguage, String fieldValue, HashMap<String, ArrayList<FieldsBulkImport>> itemsParentAndRelationToBeAddedAtTheEnd, String localId, ArrayList<FieldsBulkImport> array) throws Exception {
        RegLocalizationproposedManager regLocalizationproposedManager = new RegLocalizationproposedManager(entityManager);

        RegLocalizationproposed regLocalizationproposed = new RegLocalizationproposed();
        final String uuidLocalizationField = RegLocalizationproposedUuidHelper.getUuid(0, fieldLanguage, regItemproposed, regField);

        String uuidLocalizationMasterField = RegLocalizationproposedUuidHelper.getUuid(0, masterLanguage, regItemproposed, regField);
        if (masterLanguage.equals(fieldLanguage)) {
//                                      add localization in masterlanguage
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
//                                          check if exist localization in master language
            try {
                regLocalizationproposedManager.get(uuidLocalizationMasterField);

//                                              if master language exist add the new language
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
                itemsParentAndRelationToBeAddedAtTheEnd.put(localId, array);
            }
        }
    }

    private void storeRelarionReference(String fieldValue, RegField regField, RegItemproposed regItemproposed, HashMap<RegField, RegItem> mapCollection, RegItemclass regItemclassChild, RegLanguagecode fieldLanguage, HashMap<String, ArrayList<FieldsBulkImport>> itemsBulkImport, HashMap<String, ArrayList<FieldsBulkImport>> itemsParentAndRelationToBeAddedAtTheEnd, String localId, ArrayList<FieldsBulkImport> array) {
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

        for (String valueOfTheField : relationReferenceList) {

            try {
                RegItemclass itemClassRelationReference = regField.getRegItemclassReference();
                String relationreferenceuuid = "";

                //create relation
                RegRelationpredicate regRelationpredicateReference = regRelationpredicateManager.get(BaseConstants.KEY_PREDICATE_REFERENCE);
                RegRelationproposed regRelationproposedReference = new RegRelationproposed();
                regRelationproposedReference.setInsertdate(new Date());
                regRelationproposedReference.setRegItemSubject(null);
                regRelationproposedReference.setRegItemproposedSubject(regItemproposed);

                try {
                    if (mapCollection.containsKey(regField)) {
                        RegItem itemRelationReferenceCollection = mapCollection.get(regField);
                        relationreferenceuuid = RegItemUuidHelper.getUuid(valueOfTheField, itemRelationReferenceCollection, itemClassRelationReference);
                    } else {
                        relationreferenceuuid = RegItemUuidHelper.getUuid(valueOfTheField, null, itemClassRelationReference);
                    }

                    RegItem regItemRelationReference = regItemManager.get(relationreferenceuuid);
                    regRelationproposedReference.setUuid(RegRelationproposedUuidHelper.getUuid(null, regItemRelationReference, regRelationpredicateReference, null, regItemRelationReference));
                    regRelationproposedReference.setRegItemObject(regItemRelationReference);
                } catch (Exception ex) {
                    relationreferenceuuid = RegItemproposedUuidHelper.getUuid(valueOfTheField, null, regItemclassChild, null);
                    RegItemproposedManager regItemproposedManager = new RegItemproposedManager(entityManager);
                    RegItemproposed regItemproposedrelation = regItemproposedManager.get(relationreferenceuuid);

                    regRelationproposedReference.setUuid(RegRelationproposedUuidHelper.getUuid(regItemproposed, null, regRelationpredicateReference, regItemproposedrelation, null));
                    regRelationproposedReference.setRegItemproposedObject(regItemproposedrelation);
                }

                regRelationproposedReference.setRegItemproposedObject(null);
                regRelationproposedReference.setRegRelationReference(null);
                regRelationproposedReference.setRegRelationpredicate(regRelationpredicateReference);

                regRelationproposedManager.add(regRelationproposedReference);

//                                            create localization for red relation proposed
                RegLocalizationproposed regLocalizationproposed = new RegLocalizationproposed();
                regLocalizationproposed.setUuid(RegLocalizationproposedUuidHelper.getUuid(0, fieldLanguage, regItemproposed, regField));
                regLocalizationproposed.setRegLanguagecode(fieldLanguage);
                regLocalizationproposed.setRegItemproposed(regItemproposed);
                regLocalizationproposed.setRegField(regField);
                regLocalizationproposed.setRegRelationproposedReference(regRelationproposedReference);
                regLocalizationproposed.setInsertdate(new Date());
                regLocalizationproposed.setRegAction(regItemproposed.getRegAction());

                regLocalizationproposedManager.add(regLocalizationproposed);
            } catch (Exception ex) {

                if (regField.getRegItemclassReference().equals(regItemclassChild)) {

                    for (Map.Entry<String, ArrayList<FieldsBulkImport>> item : itemsBulkImport.entrySet()) {
                        String localIdrelationreference = item.getKey();
                        if (localIdrelationreference.equals(valueOfTheField)) {
                            itemsParentAndRelationToBeAddedAtTheEnd.put(localId, array);
                            break;
                        }
                    }
                }
            }
        }
    }

    private void storeParent(HashMap<RegField, RegItem> mapCollection, RegField regField, String fieldValue, RegItemclass regItemclassChild, RegItemproposed regItemproposed, HashMap<String, ArrayList<FieldsBulkImport>> itemsBulkImport, HashMap<String, ArrayList<FieldsBulkImport>> itemsParentAndRelationToBeAddedAtTheEnd, String localId, ArrayList<FieldsBulkImport> array) {
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
                if (mapCollection.containsKey(regField)) {
                    parentItem = mapCollection.get(regField);
                } else {
                    String parentuuid = RegItemUuidHelper.getUuid(fieldValue, null, regItemclassChild);
                    parentItem = regItemManager.get(parentuuid);
                }
                regRelationproposedParent.setUuid(RegRelationproposedUuidHelper.getUuid(regItemproposed, null, regRelationpredicateParent, null, parentItem));
                regRelationproposedParent.setRegItemObject(parentItem);
            } catch (Exception ex) {
                String parentuuid = RegItemproposedUuidHelper.getUuid(fieldValue, null, regItemclassChild, null);
                RegItemproposedManager regItemproposedManager = new RegItemproposedManager(entityManager);
                RegItemproposed regItemproposedrelation = regItemproposedManager.get(parentuuid);

                regRelationproposedParent.setUuid(RegRelationproposedUuidHelper.getUuid(regItemproposed, null, regRelationpredicateParent, regItemproposedrelation, null));
                regRelationproposedParent.setRegItemproposedObject(regItemproposedrelation);
            }

            regRelationproposedParent.setInsertdate(new Date());
            regRelationproposedParent.setRegItemSubject(null);
            regRelationproposedParent.setRegItemproposedSubject(regItemproposed);
            regRelationproposedParent.setRegItemproposedObject(null);
            regRelationproposedParent.setRegRelationReference(null);
            regRelationproposedParent.setRegRelationpredicate(regRelationpredicateParent);

            regRelationproposedManager.add(regRelationproposedParent);

        } catch (Exception ex) {

//                                        check if the parent exist in the file
            for (Map.Entry<String, ArrayList<FieldsBulkImport>> item : itemsBulkImport.entrySet()) {
                String localIdParent = item.getKey();
                if (localIdParent.equals(fieldValue)) {
                    itemsParentAndRelationToBeAddedAtTheEnd.put(localId, array);
                    break;
                }
            }

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

    private RegItemproposed createRegItemProposed(String itemRecordLocalID, RegItemclass regItemclass, RegUser regUser, RegAction regAction) throws Exception {
        RegStatusManager regStatusManager = new RegStatusManager(entityManager);

        RegItemproposedManager regItemproposedManager = new RegItemproposedManager(entityManager);

        RegItemproposed regItemproposed = new RegItemproposed();
        //                add item proposed id not existent
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
        String defaultLabel = systemLocalizationBundle.getString("label.actionon") + " " + regItemclass.getLocalid() + " made by:" + regUser.getEmail();

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
