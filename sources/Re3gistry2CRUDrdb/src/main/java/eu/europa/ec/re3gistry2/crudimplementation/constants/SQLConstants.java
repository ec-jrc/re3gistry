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
package eu.europa.ec.re3gistry2.crudimplementation.constants;

public class SQLConstants {

    private SQLConstants() {
    }

    /**
     * * SQL custom queries **
     */
    // Parameters
    public static final String SQL_PARAMETERS_REGUSER = "regUser";
    public static final String SQL_PARAMETERS_REGSTATUS = "regStatus";
    public static final String SQL_PARAMETERS_REGITEMCLASS = "regItemclass";
    public static final String SQL_PARAMETERS_COLLECTION = "collection";
    public static final String SQL_PARAMETERS_REGITEMCLASSES = "regItemclasses";
    public static final String SQL_PARAMETERS_REGRELATIONPREDICATE = "regRelationpredicete";
    public static final String SQL_PARAMETERS_REGITEMOBJECT = "regItemObject";
    public static final String SQL_PARAMETERS_LOCALID = "localid";
    public static final String SQL_PARAMETERS_REGROLE = "regRole";
    public static final String SQL_PARAMETERS_REGITEMHISTORY = "regItemhistory";
    public static final String SQL_PARAMETERS_REGITEMPROPOSED = "regItemproposed";
    public static final String SQL_PARAMETERS_REGITEM = "regitem";
    public static final String SQL_PARAMETERS_REGITEM_LIST = "regitemList";
    public static final String SQL_PARAMETERS_ACTION = "regaction";
    public static final String SQL_PARAMETERS_REGFIELD = "regfield";
    public static final String SQL_PARAMETERS_RELATION = "relation";
    public static final String SQL_PARAMETERS_REGLANGUAGECODE = "regLanguagecode";
    public static final String SQL_PARAMETERS_LABEL = "label";
    public static final String SQL_PARAMETERS_PREDICATE = "predicate";
    public static final String SQL_PARAMETERS_NOT_PREDICATE = "notpredicate";
    public static final String SQL_PARAMETERS_VERSIONNUMBER = "versionnumber";
    public static final String SQL_PARAMETERS_REGITEMREFERENCE = "regItemReference";
    public static final String SQL_PARAMETERS_VALUE = "value";
    public static final String SQL_PARAMETERS_REGSTATUSGROUP = "regStatusgroup";

    // RegItem
    public static final String SQL_GET_REGITEM_BY_LOCALID = "SELECT r FROM RegItem r WHERE r.localid = :localid";
    public static final String SQL_GET_REGITEM_BY_LOCALID_REGITEMCLASS = "SELECT r FROM RegItem r WHERE r.localid = :localid AND r.regItemclass = :regItemclass";
    public static final String SQL_GET_REGITEM_BY_LOCALID_REGITEMCLASS_REGSTATUS = "SELECT r FROM RegItem r WHERE r.localid = :localid AND r.regItemclass = :regItemclass AND r.regStatus = :regStatus";
    public static final String SQL_GET_REGITEM_BY_REGITEMCLASSTYPE = "SELECT r FROM RegItem r JOIN r.regItemclass i WHERE i.regItemclasstype = :regItemclasstype";
    public static final String SQL_GET_REGITEM_BY_REGITEMCLASSTYPE_ACTIVE = "SELECT r FROM RegItem r JOIN r.regItemclass i WHERE i.regItemclasstype = :regItemclasstype AND i.active = TRUE";
    public static final String SQL_GET_REGITEM_ACTIVE = "SELECT r FROM RegItem r JOIN r.regItemclass i WHERE i.active = TRUE";
    public static final String SQL_GET_REGITEM_BY_REGITEMCLASS = "SELECT r FROM RegItem r WHERE r.regItemclass = :regItemclass";
    public static final String SQL_GET_REGITEM_BY_REGITEMCLASS_INTERNAL = "SELECT r FROM RegItem r WHERE r.regItemclass = :regItemclass AND r.external = FALSE";
    public static final String SQL_GET_REGITEM_BY_REGITEMCLASS_AND_STATUS = "SELECT r FROM RegItem r WHERE r.regItemclass = :regItemclass AND r.regStatus = :regStatus";

    public static final String SQL_GET_REGITEM_BY_REGITEMCLASSES = "SELECT r FROM RegItem r WHERE r.regItemclass IN :regItemclasses ORDER BY r.localid";
    public static final String SQL_GET_REGITEM_BY_REGITEMCLASSES_COUNT = "SELECT count(r) FROM RegItem r WHERE r.regItemclass IN :regItemclasses";

    public static final String SQL_GET_REGITEM_BY_REGITEMCLASSES_NO_SYSTEMITEMS = "SELECT r FROM RegItem r JOIN r.regItemclass c WHERE r.regItemclass IN :regItemclasses AND c.systemitem = FALSE ORDER BY r.localid";
    public static final String SQL_GET_REGITEM_BY_REGITEMCLASSES_COUNT_NO_SYSTEMITEMS = "SELECT count(r) FROM RegItem r JOIN r.regItemclass c WHERE r.regItemclass IN :regItemclasses AND c.systemitem = FALSE";

    public static final String SQL_GET_REGITEM_BY_RELATION_AND_ITEMOBJECT = "SELECT i.regItemSubject FROM RegRelation i JOIN i.regItemObject r WHERE i.regRelationpredicate=:regRelationpredicete AND i.regItemObject = :regItemObject";
    public static final String SQL_GET_REGITEM_BY_RELATION_AND_ITEMOBJECT_COUNT = "SELECT count(i.regItemSubject) FROM RegRelation i JOIN i.regItemObject r WHERE i.regRelationpredicate=:regRelationpredicete AND i.regItemObject = :regItemObject";

    public static final String SQL_GET_REGITEM_BY_REGACTION = "SELECT r FROM RegItem r WHERE r.regAction = :regAction";

    public static final String SQL_GET_REGITEM_BY_RELATION_AND_ITEMOBJECT_NO_SYSTEMITEM = "SELECT i.regItemSubject FROM RegRelation i JOIN i.regItemObject r JOIN r.regItemclass c WHERE i.regRelationpredicate=:regRelationpredicete AND i.regItemObject = :regItemObject AND c.systemitem = FALSE";
    public static final String SQL_GET_REGITEM_BY_RELATION_AND_ITEMOBJECT_COUNT_NO_SYSTEMITEM = "SELECT count(i.regItemSubject) FROM RegRelation i JOIN i.regItemObject r JOIN r.regItemclass c WHERE i.regRelationpredicate=:regRelationpredicete AND i.regItemObject = :regItemObject AND c.systemitem = FALSE";

    public static final String SQL_GET_REG_ITEM_BY_OBJECT_PREDICATE_AND_SUBJECT_FILTER = "SELECT r.regItemSubject FROM RegRelation r WHERE r.regItemObject = :regitem AND r.regRelationpredicate = :predicate AND r.regItemSubject NOT IN (SELECT r1.regItemSubject FROM RegRelation r1 WHERE r1.regRelationpredicate = :notpredicate)";
    public static final String SQL_GET_REG_ITEM_BY_SUBJECT_PREDICATE_AND_FILTER_PREDICATE = "SELECT r0.regItemSubject FROM (SELECT * FROM RegRelation r JOIN RegItem ri on ri.uuid = r.regItemSubject WHERE ri.regStatus = :regStatus AND r.regItemObject = :regitem AND r.regRelationpredicate = :predicate) as r0 WHERE r0.regItemSubject NOT IN (SELECT r1.regItemSubject FROM RegRelation r1 WHERE r1.regRelationpredicate = :notpredicate)";

    // RegItemproposed
    public static final String SQL_GET_REGITEMPROPOSED_BY_LOCALID_REGITEMCLASS = "SELECT r FROM RegItemproposed r WHERE r.localid = :localid AND r.regItemclass = :regItemclass";
    public static final String SQL_GET_REGITEMPROPOSED_BY_LOCALID_REGITEMCLASS_REGSTATUS = "SELECT r FROM RegItemproposed r WHERE r.localid = :localid AND r.regItemclass = :regItemclass AND r.regStatus = :regStatus";
    public static final String SQL_GET_REGITEMPROPOSED_BY_REGITEMCLASSTYPE = "SELECT r FROM RegItemproposed r JOIN r.regItemclass i WHERE i.regItemclasstype = :regItemclasstype";
    public static final String SQL_GET_REGITEMPROPOSED_BY_REGITEMCLASS = "SELECT r FROM RegItemproposed r WHERE r.regItemclass = :regItemclass";
    public static final String SQL_GET_REGITEMPROPOSED_BY_REGITEMCLASSES = "SELECT r FROM RegItemproposed r WHERE r.regItemclass IN :regItemclasses ORDER BY r.localid";
    public static final String SQL_GET_REGITEMPROPOSED_NEW_BY_REGITEMCLASSES = "SELECT r FROM RegItemproposed r WHERE r.regItemclass IN :regItemclasses AND r.regItemReference IS NULL ORDER BY r.localid";
    public static final String SQL_GET_REGITEMPROPOSED_BY_REGITEMCLASSES_COUNT = "SELECT count(r) FROM RegItemproposed r WHERE r.regItemclass IN :regItemclasses";
    public static final String SQL_GET_REGITEMPROPOSED_NEW_BY_REGITEMCLASSES_COUNT = "SELECT count(r) FROM RegItemproposed r WHERE r.regItemclass IN :regItemclasses AND r.regItemReference IS NULL";
    public static final String SQL_GET_REGITEMPROPOSED_BY_RELATION_AND_ITEMOBJECT = "SELECT i.regItemproposedSubject FROM RegRelationproposed i JOIN i.regItemObject r WHERE i.regRelationpredicate=:regRelationpredicete AND i.regItemproposedObject = :regItemObject";
    public static final String SQL_GET_REGITEMPROPOSED_BY_RELATION_AND_ITEMOBJECT_COUNT = "SELECT count(i.regItemproposedSubject) FROM RegRelationproposed i JOIN i.regItemObject r WHERE i.regRelationpredicate=:regRelationpredicete AND i.regItemproposedObject = :regItemObject";
    public static final String SQL_GET_REGITEMPROPOSED_BY_REGITEMREFERENCE = "SELECT r FROM RegItemproposed r WHERE r.regItemReference = :regItemReference";
    public static final String SQL_GET_REGITEMPROPOSED_BY_REGUSER = "SELECT r FROM RegItemproposed r WHERE r.regUser = :regUser";
    public static final String SQL_GET_REGITEMPROPOSED_BY_REGUSER_COUNT = "SELECT count(r) FROM RegItemproposed r WHERE r.regUser = :regUser";
    public static final String SQL_GET_REGITEMPROPOSED_BY_REGACTION = "SELECT r FROM RegItemproposed r WHERE r.regAction = :regAction";
    public static final String SQL_GET_REGITEMPROPOSED_BY_LOCALID= "SELECT r FROM RegItemproposed r WHERE r.localid = :localid";
   
    public static final String SQL_GET_REGITEMPROPOSED_NEW_BY_REGITEMCLASSES_COLLECTION = "SELECT p.regItemproposedSubject FROM RegRelationproposed p JOIN  p.regItemproposedSubject r WHERE r.regItemclass IN :regItemclasses AND r.regItemReference IS NULL and p.regItemObject = :regItemObject and p.regRelationpredicate=:regRelationpredicete ORDER BY r.localid";
    public static final String SQL_GET_REGITEMPROPOSED_NEW_BY_REGITEMCLASSES_COLLECTION_COUNT = "SELECT count(p.regItemproposedSubject) FROM RegRelationproposed p JOIN  p.regItemproposedSubject r WHERE r.regItemclass IN :regItemclasses AND r.regItemReference IS NULL and p.regItemObject = :regItemObject and p.regRelationpredicate=:regRelationpredicete";

    // RegItemHistory
    public static final String SQL_GET_REGITEMHISTORY_BY_LOCALID_REGITEMCLASS = "SELECT r FROM RegItemhistory r WHERE r.localid = :localid AND r.regItemclass = :regItemclass";
    public static final String SQL_GET_REGITEMHISTORY_BY_LOCALID_REGITEMCLASS_MAX_VERSION = "SELECT r FROM RegItemhistory r WHERE r.localid = :localid AND r.regItemclass = :regItemclass AND r.versionnumber = (SELECT max(r.versionnumber) FROM RegItemhistory r WHERE r.localid = :localid AND r.regItemclass = :regItemclass)";
    public static final String SQL_GET_REGITEMHISTORY_BY_LOCALID_REGITEMCLASS_MIN_VERSION = "SELECT r FROM RegItemhistory r WHERE r.localid = :localid AND r.regItemclass = :regItemclass AND r.versionnumber = (SELECT min(r.versionnumber) FROM RegItemhistory r WHERE r.localid = :localid AND r.regItemclass = :regItemclass)";
    public static final String SQL_GET_REGITEMHISTORY_BY_LOCALID_VERSION_REGITEMCLASS = "SELECT r FROM RegItemhistory r WHERE r.localid = :localid AND r.regItemclass = :regItemclass AND r.versionnumber = :versionnumber";
    public static final String SQL_GET_REGITEMHISTORY_BY_REGITEMCLASS = "SELECT r FROM RegItemhistory r WHERE r.regItemclass = :regItemclass";
    public static final String SQL_GET_REGITEMHISTORY_BY_LOCALID_VERSION_REGITEMCLASS_REITEMREFERENCE = "SELECT r FROM RegItemhistory r WHERE r.localid = :localid AND r.regItemclass = :regItemclass AND r.versionnumber = :versionnumber AND r.regItemReference = :regItemReference";
    public static final String SQL_GET_REGITEMHISTORY_BY_REITEMREFERENCE = "SELECT r FROM RegItemhistory r WHERE r.regItemReference = :regItemReference";
    public static final String SQL_GET_REGITEMHISTORY_BY_REGACTION = "SELECT r FROM RegItemhistory r WHERE r.regAction = :regAction";

    // RegFieldmapping
    public static final String SQL_GET_REG_FIELDMAPPING_BY_FIELD_AND_ITEM_CLASS = "SELECT r FROM RegFieldmapping r WHERE r.regField = :regField AND r.regItemclass = :regItemclass ORDER BY r.listorder";
    public static final String SQL_GET_REG_FIELDMAPPING_BY_ITEMCLASS = "SELECT r FROM RegFieldmapping r WHERE r.regItemclass = :regItemclass ORDER BY r.listorder";
    public static final String SQL_GET_REG_FIELDMAPPING_BY_ITEMCLASS_ORDER_BY_LISTORDER = "SELECT r FROM RegFieldmapping r WHERE r.regItemclass = :regItemclass ORDER BY r.listorder ASC";
    public static final String SQL_GET_FIELDMAPPING_MAX_LISTORDER = "SELECT max(r.listorder) FROM RegFieldmapping r WHERE r.regItemclass = :regItemclass";

    // RegField
    public static final String SQL_GET_REGFIELD_TITLE = "SELECT r FROM RegField r WHERE r.istitle = TRUE";
    public static final String SQL_REGFIELD_LOCALID_LIKE = "SELECT r FROM RegField r WHERE r.localid LIKE %?%";

    // RegLanguagecode
    public static final String SQL_GET_LANGUAGECODE_MASTERLANGUAGE = "SELECT r FROM RegLanguagecode r WHERE r.masterlanguage = TRUE";

    // RegLocalization
    public static final String SQL_GET_LOCALIZATION_FIELDS_BY_ITEM = "SELECT r FROM RegLocalization r WHERE r.regItem = :regitem AND r.regField IS NOT NULL";
    public static final String SQL_GET_LOCALIZATION_FIELDS_BY_ITEM_AND_LANGUAGE = "SELECT r FROM RegLocalization r WHERE r.regItem = :regitem AND r.regLanguagecode = :regLanguagecode AND r.regField IS NOT NULL";
    public static final String SQL_GET_LOCALIZATION_FIELDS_BY_LANGUAGE_AND_ITEMS = "SELECT r FROM RegLocalization r WHERE r.regLanguagecode = :regLanguagecode AND r.regField IS NOT NULL AND r.regItem IN :regitemList";
    public static final String SQL_GET_LOCALIZATION_FIELDS_BY_ITEMCLASS = "SELECT r FROM RegLocalization r WHERE r.regItemclass = :regItemclass";
   
    public static final String SQL_GET_LOCALIZATION_FIELDS_BY_REGFIELD_ITEMCLASS_HREFNORNULL = "SELECT * FROM RegLocalization r INNER JOIN RegItem ri ON ri.uuid = r.regItem AND ri.regItemclass = :regItemclass WHERE r.regField = :regfield AND r.href IS NOT NULL";
    public static final String SQL_GET_LOCALIZATION_BY_RELATION = "SELECT r FROM RegLocalization r WHERE r.regRelationReference = :relation";
    public static final String SQL_GET_LOCALIZATION_BY_FIELD = "SELECT r FROM RegLocalization r WHERE r.regField = :regfield AND r.regItem IS NULL";
    public static final String SQL_GET_LOCALIZATION_BY_FIELD_LANGUAGECODE = "SELECT r FROM RegLocalization r WHERE r.regField = :regfield AND r.regLanguagecode = :regLanguagecode AND r.regItem IS NULL";
    public static final String SQL_GET_LOCALIZATION_BY_FIELD_ITEM = "SELECT r FROM RegLocalization r WHERE r.regField = :regfield AND r.regItem = :regitem";
    public static final String SQL_GET_LOCALIZATION_BY_FIELD_ITEM_LANGUAGECODE = "SELECT r FROM RegLocalization r WHERE r.regField = :regfield AND r.regItem = :regitem AND r.regLanguagecode = :regLanguagecode";
    public static final String SQL_GET_LOCALIZATION_BY_FIELD_ITEM_ACTION = "SELECT r FROM RegLocalization r WHERE r.regField = :regfield AND r.regItem = :regitem AND r.regAction = :regaction";
    public static final String SQL_GET_LOCALIZATION_WITH_RELATION_REFERENCE_BY_ITEM = "SELECT r FROM RegLocalization r WHERE r.regItem = :regitem AND r.regField IS NOT NULL AND r.regRelationReference IS NOT NULL";

    public static final String SQL_GET_LOCALIZATION_FIELD_BY_VALUE = "SELECT r FROM RegLocalization r WHERE r.value = :value AND r.regField IS NOT NULL AND r.regItem IS NULL";

    // RegLocalizationproposed
    public static final String SQL_GET_LOCALIZATIONPROPOSED_FIELDS_BY_ITEM = "SELECT r FROM RegLocalizationproposed r WHERE r.regItemproposed = :regitem AND r.regField IS NOT NULL";
    public static final String SQL_GET_LOCALIZATIONPROPOSED_FIELDS_BY_ITEMCLASS = "SELECT r FROM RegLocalizationproposed r WHERE r.regItemclass = :regitemclass";
    public static final String SQL_GET_LOCALIZATIONPROPOSED_BY_FIELD = "SELECT r FROM RegLocalizationproposed r WHERE r.regField = :regfield AND r.regItemproposed IS NULL";
    public static final String SQL_GET_LOCALIZATIONPROPOSED_BY_FIELD_LANGUAGECODE = "SELECT r FROM RegLocalizationproposed r WHERE r.regField = :regfield AND r.regLanguagecode = :regLanguagecode AND r.regItemproposed IS NULL";
    public static final String SQL_GET_LOCALIZATIONPROPOSED_BY_FIELD_ITEM = "SELECT r FROM RegLocalizationproposed r WHERE r.regField = :regfield AND r.regItemproposed = :regitem";
    public static final String SQL_GET_LOCALIZATIONPROPOSED_NULL_BY_FIELD_ITEM = "SELECT r FROM RegLocalizationproposed r WHERE r.regField = :regfield AND r.regItemproposed = :regitem AND r.value IS NULL AND r.regRelationproposedReference IS NULL";
    public static final String SQL_GET_LOCALIZATIONPROPOSED_BY_FIELD_ITEM_LANGUAGECODE = "SELECT r FROM RegLocalizationproposed r WHERE r.regField = :regfield AND r.regItemproposed = :regitem AND r.regLanguagecode = :language";
    public static final String SQL_GET_LOCALIZATIONPROPOSED_BY_FIELD_ITEM_ACTION = "SELECT r FROM RegLocalizationproposed r WHERE r.regField = :regfield AND r.regItemproposed = :regitem AND r.regAction = :regaction";
    public static final String SQL_GET_LOCALIZATIONPROPOSED_BY_FIELD_ITEM_LANGUAGECODE_NEW = "SELECT r FROM RegLocalizationproposed r WHERE r.regField = :regfield AND r.regItemproposed = :regitem AND r.regLanguagecode = :language AND r.regLocalizationReference IS NULL";
    public static final String SQL_GET_LOCALIZATIONPROPOSED_BY_REGLOCALIZATION_REFERENCE_AND_LANGUAGE = "SELECT r FROM RegLocalizationproposed r WHERE r.regLocalizationReference = :regLocalizationReference AND r.regLanguagecode = :regLanguagecode";
    public static final String SQL_GET_LOCALIZATIONPROPOSED_BY_REGITEMPROPOSED_LANGUAGECODE = "SELECT r FROM RegLocalizationproposed r WHERE r.regItemproposed = :regItemproposed AND r.regLanguagecode = :regLanguagecode";
    public static final String SQL_GET_LOCALIZATIONPROPOSED_ONLY_RELATION_BY_REGITEMPROPOSED_LANGUAGECODE = "SELECT r FROM RegLocalizationproposed r WHERE r.regItemproposed = :regItemproposed AND r.regLanguagecode = :regLanguagecode AND r.regRelationproposedReference IS NOT NULL";

    //RegLocalizationhistory
    public static final String SQL_GET_LOCALIZATION_BY_FIELD_ITEMHISTORY = "SELECT r FROM RegLocalizationhistory r WHERE r.regField = :regfield AND r.regItemhistory = :regItemhistory";
    public static final String SQL_GET_LOCALIZATION_BY_TEMHISTORY = "SELECT r FROM RegLocalizationhistory r WHERE r.regItemhistory = :regItemhistory";
    public static final String SQL_GET_LOCALIZATIONHISTORY_BY_FIELD_ITEM_LANGUAGECODE = "SELECT r FROM RegLocalizationhistory r WHERE r.regField = :regfield AND r.regItemhistory = :regitem AND r.regLanguagecode = :language";
    public static final String SQL_GET_LOCALIZATIONHISTORY_BY_ITEM_LANGUAGECODE = "SELECT r FROM RegLocalizationhistory r WHERE r.regItemhistory = :regitem AND r.regLanguagecode = :regLanguagecode";
    public static final String SQL_GET_LOCALIZATIONHISTORY_BY_FIELD_ITEM_ACTION = "SELECT r FROM RegLocalizationhistory r WHERE r.regField = :regfield AND r.regItemhistory = :regitem AND r.regAction = :regaction";
    public static final String SQL_GET_LOCALIZATION_FIELDS_BY_ITEMHISTORY = "SELECT r FROM RegLocalizationhistory r WHERE r.regItemhistory = :regItemhistory AND r.regField IS NOT NULL";

    // RegRelation
    public static final String SQL_GET_RELATION_BY_SUBJECT_ITEM = "SELECT r FROM RegRelation r WHERE r.regItemSubject = :regitem";
    public static final String SQL_GET_RELATION_BY_OBJECT_ITEM = "SELECT r FROM RegRelation r WHERE r.regItemObject = :regitem";
    public static final String SQL_GET_RELATION_COLLECTION_REFERENCE = "SELECT r FROM RegRelation r WHERE r.regItemSubject = :regitem AND r.regRelationpredicate = :predicate";
    public static final String SQL_GET_RELATION_OBJECT_PREDICATE = "SELECT r FROM RegRelation r WHERE r.regItemObject = :regitem AND r.regRelationpredicate = :predicate";
    public static final String SQL_GET_RELATION_SUBJECT_PREDICATE = "SELECT r FROM RegRelation r WHERE r.regItemSubject = :regitem AND r.regRelationpredicate = :predicate";
    public static final String SQL_GET_RELATION_SUBJECTS_PREDICATE = "SELECT r FROM RegRelation r WHERE r.regItemSubject IN :regitemList AND r.regRelationpredicate = :predicate";
    public static final String SQL_GET_SUBJECT_BY_OBJECT_PREDICATE_AND_SUBJECT_FILTER = "SELECT r FROM RegRelation r WHERE r.regItemObject = :regitem AND r.regRelationpredicate = :predicate AND r.regItemSubject NOT IN (SELECT r1.regItemSubject FROM RegRelation r1 WHERE r1.regRelationpredicate = :notpredicate)";

    // RegRelationProposed
    public static final String SQL_GET_RELATIONPROPOSED_BY_SUBJECT_ITEM = "SELECT r FROM RegRelationproposed r WHERE r.regItemproposedSubject = :regitem";
    public static final String SQL_GET_RELATIONPROPOSED_BY_OBJECT_ITEM = "SELECT r FROM RegRelationproposed r WHERE r.regItemproposedObject = :regitem";
    public static final String SQL_GET_RELATIONPROPOSED_OBJECT_PREDICATE = "SELECT r FROM RegRelationproposed r WHERE r.regItemObject = :regitem AND r.regRelationpredicate = :predicate";
    public static final String SQL_GET_RELATIONPROPOSED_COLLECTION_REFERENCE = "SELECT r FROM RegRelationproposed r WHERE r.regItemproposedSubject = :regitem AND r.regRelationpredicate = :predicate";
    public static final String SQL_GET_RELATIONPROPOSED_COLLECTION_REFERENCE_NEW = "SELECT r FROM RegRelationproposed r WHERE r.regItemproposedSubject = :regitem AND r.regRelationpredicate = :predicate AND r.regRelationReference IS NULL";
    public static final String SQL_GET_RELATIONPROPOSED_BY_REG_RELATION_REFERENCE = "SELECT r FROM RegRelationproposed r WHERE r.regRelationReference = :regrelationreference";

    //RegRelationistory
    public static final String SQL_GET_COLLECTION_REFERENCE_HISTORY = "SELECT r FROM RegRelationhistory r WHERE r.regItemhistorySubject = :regItemhistory AND r.regRelationpredicate = :predicate";
    public static final String SQL_GET_RELATIONHISTORY_SUBJECT_PREDICATE = "SELECT r FROM RegRelationhistory r WHERE r.regItemhistorySubject = :regItemhistory AND r.regRelationpredicate = :predicate";
    public static final String SQL_GET_RELATIONHISTORY_OBJECT_PREDICATE = "SELECT r FROM RegRelationhistory r WHERE r.regItemhistoryObject = :regItemhistory AND r.regRelationpredicate = :predicate";
    public static final String SQL_GET_RELATIONHISTORY_BY_OBJECT_PREDICATE_AND_SUBJECT_FILTER = "SELECT r.regItemSubject FROM RegRelationhistory r WHERE r.regItemhistoryObject = :regitem AND r.regRelationpredicate = :predicate AND r.regItemSubject NOT IN (SELECT r1.regItemSubject FROM RegRelation r1 WHERE r1.regRelationpredicate = :notpredicate)";
    public static final String SQL_GET_RELATIONHISTORYBY_SUBJECT_PREDICATE_AND_FILTER_PREDICATE = "SELECT r0.regItemSubject FROM (SELECT * FROM RegRelationhistory r JOIN RegItem ri on ri.uuid = r.regItemSubject WHERE ri.regStatus = :regStatus AND r.regItemObjectHistory = :regitem AND r.regRelationpredicate = :predicate) as r0 WHERE r0.regItemSubject NOT IN (SELECT r1.regItemSubject FROM RegRelation r1 WHERE r1.regRelationpredicate = :notpredicate)";

    // RegItemclass
    public static final String SQL_GET_ITEMCLASS_CHILD_BY_ITEMCLASS = "SELECT r FROM RegItemclass r WHERE r.regItemclassParent = :regitemclass";
    public static final String SQL_GET_ALL_ITEMCLASS_CHILD_BY_ITEMCLASS_ORDERASC = "SELECT r FROM RegItemclass r ORDER BY r.dataprocedureorder ASC";
    public static final String SQL_GET_REGITEMCLASS_MAX_DATAPROCEDUREORDER = "SELECT max(r.dataprocedureorder) FROM RegItemclass r";

    // RegItemclasstype
    public static final String SQL_GET_ITEMCLASSTYPE_BY_TYPE = "SELECT r FROM RegItemclasstype r WHERE r.localid = :regitemclasstype";

    // RegUserRegGroupMapping
    public static final String SQL_GET_REGUSERREGGROUPMAPPING_BY_REGUSER = "SELECT r FROM RegUserRegGroupMapping r WHERE r.regUser = :regUser";
    public static final String SQL_GET_REGUSERREGGROUPMAPPING_BY_REGGROUP = "SELECT r FROM RegUserRegGroupMapping r WHERE r.regGroup = :regGroup";
    public static final String SQL_GET_REGUSERREGGROUPMAPPING_BY_REGUSER_REGGROUP = "SELECT r FROM RegUserRegGroupMapping r WHERE r.regUser = :regUser AND r.regGroup = :regGroup";

    // RegItemRegGroupRegRoleMapping
    public static final String SQL_GET_REGITEMREGGROUPREGROLEMAPPING_BY_REGGROUP = "SELECT r FROM RegItemRegGroupRegRoleMapping r WHERE r.regGroup = :regGroup";
    public static final String SQL_GET_REGITEMREGGROUPREGROLEMAPPING_BY_REGITEMREGROLE = "SELECT r FROM RegItemRegGroupRegRoleMapping r WHERE r.regItem = :regItem AND r.regRole = :regRole";
    public static final String SQL_GET_REGITEMREGGROUPREGROLEMAPPING_BY_REGITEM = "SELECT r FROM RegItemRegGroupRegRoleMapping r WHERE r.regItem = :regItem";
    public static final String SQL_GET_REGITEMREGGROUPREGROLEMAPPING_BY_REGROLE = "SELECT r FROM RegItemRegGroupRegRoleMapping r WHERE r.regRole = :regRole";

    // RegItemproposedRegGroupRegRoleMapping
    public static final String SQL_GET_REGITEMPROPOSEDREGGROUPREGROLEMAPPING_BY_REGGROUP = "SELECT r FROM RegItemproposedRegGroupRegRoleMapping r WHERE r.regGroup = :regGroup";
    public static final String SQL_GET_REGITEMPROPOSEDREGGROUPREGROLEMAPPING_BY_REGITEMREGROLE = "SELECT r FROM RegItemproposedRegGroupRegRoleMapping r WHERE r.regItemproposed = :regItemproposed AND r.regRole = :regRole";
    public static final String SQL_GET_REGITEMPROPOSEDREGGROUPREGROLEMAPPING_BY_REGITEM = "SELECT r FROM RegItemproposedRegGroupRegRoleMapping r WHERE r.regItemproposed = :regItemproposed";
    public static final String SQL_GET_REGITEMPROPOSEDREGGROUPREGROLEMAPPING_BY_REGROLE = "SELECT r FROM RegItemproposedRegGroupRegRoleMapping r WHERE r.regRole = :regRole";
    public static final String SQL_GET_REGITEMPROPOSEDREGGROUPREGROLEMAPPING_BY_REFERENCE = "SELECT r FROM RegItemproposedRegGroupRegRoleMapping r WHERE r.regItemRegGroupRegRoleMappingReference = :regItemRegGroupRegRoleMappingReference";
    public static final String SQL_GET_NEW_REGITEMPROPOSEDREGGROUPREGROLEMAPPING_BY_REGITEMREGROLE = "SELECT r FROM RegItemproposedRegGroupRegRoleMapping r WHERE r.regItemproposed = :regItemproposed AND r.regRole = :regRole AND r.regItemRegGroupRegRoleMappingReference IS NULL";

    //RegStatusLocalization
    public static final String SQL_GET_REGSTATUSLOCALIZATION_BY_REGSTATUS_REGLANGUAGECODE = "SELECT r FROM RegStatuslocalization r WHERE r.regStatus = :regStatus AND r.regLanguagecode = :regLanguagecode";
    public static final String SQL_GET_REGSTATUSLOCALIZATION_BY_REGSTATUSGROUP_REGLANGUAGECODE = "SELECT r FROM RegStatuslocalization r WHERE r.regStatusgroup = :regStatusgroup AND r.regLanguagecode = :regLanguagecode";

    // RegAction
    public static final String SQL_GET_REGACTION_BY_USER_STATUS = "SELECT r FROM RegAction r WHERE r.regUser = :regUser AND r.regStatus = :regStatus";
    public static final String SQL_GET_REGACTION_BY_USER_STATUS_NO_COMMENTS = "SELECT r FROM RegAction r WHERE r.regUser = :regUser AND r.regStatus = :regStatus AND r.regItemRegister = :regItemRegister AND r.regItemRegistry = :regItemRegistry AND r.changeRequest IS NULL";
    public static final String SQL_GET_REGACTION_BY_USER = "SELECT r FROM RegAction r WHERE r.regUser = :regUser";
    public static final String SQL_GET_REGACTION_BY_REGISTER = "SELECT r FROM RegAction r WHERE r.regItemRegister = :regItemRegister";
    public static final String SQL_GET_REGACTION_BY_REGISTRY = "SELECT r FROM RegAction r WHERE r.regItemRegistry = :regItemRegistry";

    // RegStatus
    public static final String SQL_GET_REGSTATUS_BY_REGSTATUSGROUP = "SELECT r FROM RegStatus r WHERE r.regStatusgroup = :regStatusgroup";
    public static final String SQL_GET_REGSTATUSPUBIC_BY_REGSTATUSGROUP = "SELECT r FROM RegStatus r WHERE r.regStatusgroup = :regStatusgroup AND r.ispublic = TRUE";
   
}
