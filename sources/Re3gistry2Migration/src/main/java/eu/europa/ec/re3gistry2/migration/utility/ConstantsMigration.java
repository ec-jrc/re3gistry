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
package eu.europa.ec.re3gistry2.migration.utility;

public class ConstantsMigration {

    /**
     * simple queries
     */
    /**
     * Item Class
     */
    public static final String ITEMCLASS_BY_DATAPROCEDUREORDER_ASC = "SELECT i FROM Itemclass i ORDER BY i.dataprocedureorder asc";
    public static final String ITEMCLASS_BY_REGISTER_ORDERBY_DATAPROCEDUREORDER_ASC = "SELECT i FROM Itemclass i WHERE i.register = :register ORDER BY i.dataprocedureorder asc";
    public static final String ITEMCLASS_CUSTOMATTRIBUTE_BY_ITEMCLASS = "SELECT i FROM Itemclasscustomattribute i WHERE i.itemclass = :itemclass";
    public static final String ITEMCLASS_CUSTOMATTRIBUTE_BY_ITEMCLASS_AND_CUSTOMATTRIBUTE = "SELECT i FROM Itemclasscustomattribute i WHERE i.itemclass = :itemclass AND i.customattribute =:customattribute";

    /**
     * Item collection
     */
    public static final String ITEMCOLLECTION_BY_ITEM = "SELECT i FROM Itemcollection i WHERE i.item = :item";

    /**
     * Item parent
     */
    public static final String ITEMPARENT_BY_ITEM = "SELECT i FROM Itemparent i WHERE i.item = :item";

    /**
     * Item successor
     */
    public static final String ITEMSUCCESSOR_BY_ITEM = "SELECT i FROM Itemsuccessor i WHERE i.item = :item";

    /**
     * Custom attribute value
     */
    public static final String CUSTOMATTRIBUTEVALUE_BY_ITEM = "SELECT c FROM Customattributevalue c WHERE c.item = :item";

    /**
     * Item status
     */
    public static final String ITEMSTATUS_BY_LOCALID = "SELECT s FROM Status s WHERE s.uriname = :uriname";

    /**
     * Localization
     */
    public static final String LOCALIZATION_BY_ITEM = "SELECT l FROM Localization l WHERE l.item = :item";
    public static final String LOCALIZATION_BY_REGISTRY = "SELECT l FROM Localization l WHERE l.registry = :registry";
    public static final String LOCALIZATION_BY_REGISTRY_AND_LANGUAGE = "SELECT l FROM Localization l WHERE l.registry = :registry AND l.language = :language";
    public static final String LOCALIZATION_BY_REGISTER = "SELECT l FROM Localization l WHERE l.register = :register";
    public static final String LOCALIZATION_BY_REGISTER_AND_LANGUAGE = "SELECT l FROM Localization l WHERE l.register = :register AND l.language = :language";
    public static final String LOCALIZATION_BY_REFERENCE = "SELECT l FROM Localization l WHERE l.reference = :reference";
    public static final String LOCALIZATION_BY_STATUS = "SELECT l FROM Localization l WHERE l.status = :status";
    public static final String LOCALIZATION_BY_STATUS_NOT_NULL = "SELECT l FROM Localization l WHERE l.status is not NULL";
    public static final String LOCALIZATION_BY_REFERENCE_AND_LANGUAGE = "SELECT l FROM Localization l WHERE l.reference = :reference AND l.language = :language";
    public static final String LOCALIZATION_BY_CUSTOMATTRIBUTEVALUE = "SELECT l FROM Localization l WHERE l.customattributevalue = :customattributevalue";
    public static final String LOCALIZATION_BY_CUSTOMATTRIBUTEVALUE_AND_LANGUAGE = "SELECT l FROM Localization l WHERE l.customattributevalue = :customattributevalue AND l.language = :language";

    /**
     * complex queries
     */
    public static final String ITEMS_LATEST_VERSION_LIST_BY_ITEMCLASS = "SELECT i.uuid as uuid, "
            + "i.uriname as uriname, "
            + "i.itemclass as itemclass, "
            + "i.versionnumber as versionnumber, "
            + "i.datecreation as datecreation, "
            + "i.datelastupdate as datelastupdate, "
            + "i.status as status\n"
            + "FROM Item i \n"
            + "WHERE i.itemclass = :itemclass AND i.versionnumber = '0'";
    
    public static final String ITEM_WITHOUT_PARENT_WITHOUT_COLLECTION_LASTVERSION = "SELECT i.uuid as uuid, "
            + "i.uriname as uriname, "
            + "i.itemclass as itemclass, "
            + "i.versionnumber as versionnumber, "
            + "i.status as status\n"
            + "FROM Item i \n"
            + "WHERE i.uuid NOT IN (SELECT ip.item FROM Itemparent ip)\n"
            + "AND i.uuid NOT IN (SELECT ic.item FROM Itemcollection ic)\n"
            + "AND i.itemclass = :itemclass AND i.versionnumber = '0'";

    public static final String ITEM_WITH_PARENT_WITHOUT_COLLECTION_LASTVERSION = "SELECT i.uuid as uuid, "
            + "i.uriname as uriname, "
            + "i.itemclass as itemclass, "
            + "i.versionnumber as versionnumber, "
            + "i.status as status\n"
            + "FROM Item i \n"
            + "WHERE i.uuid IN (SELECT ip.item FROM Itemparent ip)\n"
            + "AND i.uuid NOT IN (SELECT ic.item FROM Itemcollection ic)\n"
            + "AND i.itemclass = :itemclass AND i.versionnumber = '0'";

    public static final String ITEM_WITHOUT_PARENT_WITH_COLLECTION_LASTVERSION = "SELECT i.uuid as uuid, "
            + "i.uriname as uriname, "
            + "i.itemclass as itemclass, "
            + "i.versionnumber as versionnumber, "
            + "i.status as status\n"
            + "FROM Item i \n"
            + "WHERE i.uuid NOT IN (SELECT ip.item FROM Itemparent ip)\n"
            + "AND i.uuid IN (SELECT ic.item FROM Itemcollection ic)\n"
            + "AND i.itemclass = :itemclass AND i.versionnumber = '0'";

    public static final String ITEM_WITH_PARENT_WITH_COLLECTION_LASTVERSION = "SELECT i.uuid as uuid, "
            + "i.uriname as uriname, "
            + "i.itemclass as itemclass, "
            + "i.versionnumber as versionnumber, "
            + "i.status as status\n"
            + "FROM Item i \n"
            + "WHERE i.uuid  IN (SELECT ip.item FROM Itemparent ip)\n"
            + "AND i.uuid IN (SELECT ic.item FROM Itemcollection ic)\n"
            + "AND i.itemclass = :itemclass AND i.versionnumber = '0'";

    public static final String GET_REGITEM_BY_URINAME_AND_REGITEMCLASS = "SELECT r FROM RegItem r "
            + "WHERE r.uriname = :uriname AND r.regItemclass = :regItemclass";

    public static final String GET_ALL_BY_FIELD_AND_ITEM_CLASS = "SELECT r FROM RegFieldmapping r "
            + "WHERE r.regField = :regField AND r.regItemclass = :regItemclass";

    public static final String GET_ITEM_ALL_VERSION_COLLECTION = "SELECT i FROM Item i WHERE i IN "
            + "(SELECT ic.item FROM Itemcollection ic WHERE ic.item IN "
            + "(SELECT ii FROM Item ii WHERE ii.uriname = :itemuriname AND ii.itemclass = :itemclass) AND ic.collection IN "
            + "(SELECT iii FROM Item iii WHERE iii.uriname = :collectionuriname)) ORDER BY i.versionnumber ASC";

    public static final String GET_ITEM_BY_ITEMACLASS_URINAME_DESC = "SELECT i FROM Item i "
            + "WHERE i.itemclass = :itemclass AND i.uriname = :uriname AND i.versionnumber <> '0' "
            + "ORDER BY i.versionnumber DESC";
    
    public static final String GET_COUNT_ITEM_BY_ITEMACLASS = "SELECT count(i) FROM Item i "
            + "WHERE i.itemclass = :itemclass AND i.versionnumber = '0'";

    /**
     * Parameters
     */
    public static final String KEY_PARAMETER_LABEL = "label";
    public static final String KEY_PARAMETER_DEFINITION = "definition";
    public static final String KEY_PARAMETER_DESCRIPTION = "description";
    public static final String KEY_PARAMETER_ITEMCLASS = "itemclass";
    public static final String KEY_PARAMETER_ITEMRESULT = "itemsResult";
    public static final String KEY_PARAMETER_REFERENCE = "reference";

    /*
    * Errors
     */
    public static final String ERROR_CANTCREATEREGLOALIZATIONFORREGITEMHISTORY = "Could not create reglocalization for regitemhistory:  {0}, reg field: {1} and language: {2}";
    public static final String ERROR_CANTCREATEREGLOALIZATIONFORREGITEM = "Could not create reglocalization for regitem:  {0}, reg field: {1} and language: {2}";

}
