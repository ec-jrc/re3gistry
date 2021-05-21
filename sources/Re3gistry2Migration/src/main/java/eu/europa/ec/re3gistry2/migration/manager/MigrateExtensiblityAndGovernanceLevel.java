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
package eu.europa.ec.re3gistry2.migration.manager;

import eu.europa.ec.re3gistry2.base.utility.BaseConstants;
import eu.europa.ec.re3gistry2.crudimplementation.RegFieldManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegGroupManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegItemManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegItemclassManager;
import eu.europa.ec.re3gistry2.crudimplementation.RegItemclasstypeManager;
import eu.europa.ec.re3gistry2.javaapi.handler.RegInstallationHandler;
import eu.europa.ec.re3gistry2.javaapi.solr.SolrHandler;
import eu.europa.ec.re3gistry2.migration.migrationmodel.Registry;
import eu.europa.ec.re3gistry2.model.RegField;
import eu.europa.ec.re3gistry2.model.RegGroup;
import eu.europa.ec.re3gistry2.model.RegItem;
import eu.europa.ec.re3gistry2.model.RegItemclass;
import eu.europa.ec.re3gistry2.model.RegItemclasstype;
import eu.europa.ec.re3gistry2.model.uuidhandlers.RegItemclassUuidHelper;
import java.util.ArrayList;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import org.apache.logging.log4j.Logger;

public class MigrateExtensiblityAndGovernanceLevel {

    private final EntityManager entityManagerRe3gistry2;
    private final EntityManager entityManagerRe3gistry2Migration;
    private final Logger logger;
    private final MigrationManager migrationManager;

    private final String QUERY_ITEMCLASS_1
            = "INSERT INTO public.reg_itemclass VALUES ('09f068599d89407a4fd5b48d8ad58061', 'governance-level', 'BASE_URI_REGISTRY', true, true, 'REGISTRY_ITEMCLASS_UUID', '2', '1', 'DATAPROCEDUREORDER', '2019-07-19 15:56:43.784', NULL);";
    private final String QUERY_ITEMCLASS_2
            = "INSERT INTO public.reg_itemclass VALUES ('233b80ac5ad1731cb789f3dbf33811b7', 'governance-level-item', NULL, false, true, '09f068599d89407a4fd5b48d8ad58061', '3', '1', 'DATAPROCEDUREORDER', '2019-07-19 15:56:43.838', NULL);";
    private final String QUERY_ITEMCLASS_3
            = "INSERT INTO public.reg_itemclass VALUES ('4d843e0e6ac86113f543de29b0c8e81d', 'extensibility', 'BASE_URI_REGISTRY', true, true, 'REGISTRY_ITEMCLASS_UUID', '2', '1', 'DATAPROCEDUREORDER', '2019-07-30 09:30:44.656', NULL);";
    private final String QUERY_ITEMCLASS_4
            = "INSERT INTO public.reg_itemclass VALUES ('1053ab3f4a9e29a3d6d24d1ac5125667', 'extensibility-item', NULL, false, true, '4d843e0e6ac86113f543de29b0c8e81d', '3', '1', 'DATAPROCEDUREORDER', '2019-07-30 09:30:44.765', NULL);";

    private final String QUERY_FIELDMAPPING
            = "INSERT INTO public.reg_fieldmapping VALUES ('549ffecfbf23afd16ac4ccf2fbb8179c', 'FIELD_CONTENTBODY_UUID', '09f068599d89407a4fd5b48d8ad58061', 5, false, '1', true, false, false, '2019-07-19 15:56:43.801', NULL, false);\n"
            + "INSERT INTO public.reg_fieldmapping VALUES ('0fb4ae52510a5df95fada92f2c52c79b', 'FIELD_REGISTERMANAGER_UUID', '09f068599d89407a4fd5b48d8ad58061', 3, false, '1', true, false, false, '2019-07-19 15:56:43.801', NULL, false);\n"
            + "INSERT INTO public.reg_fieldmapping VALUES ('f5c840d507b4e3b2559715458d2ec5e9', 'FIELD_SUBMITTINGORGANIZATION_UUID', '09f068599d89407a4fd5b48d8ad58061', 6, false, '1', true, false, true, '2019-07-19 15:56:43.801', NULL, false);\n"
            + "INSERT INTO public.reg_fieldmapping VALUES ('189e1b8132d0efcd414d52f68f1deb58', 'FIELD_CONTENTSUMMARY_UUID', '09f068599d89407a4fd5b48d8ad58061', 1, true, '1', true, false, false, '2019-07-19 15:56:43.801', NULL, false);\n"
            + "INSERT INTO public.reg_fieldmapping VALUES ('6d488bd3698fc50a23f6fe32391e0b4c', 'FIELD_LABEL_UUID', '09f068599d89407a4fd5b48d8ad58061', 0, true, '1', true, false, false, '2019-07-19 15:56:43.801', NULL, false);\n"
            + "INSERT INTO public.reg_fieldmapping VALUES ('617f610e036020b86dfa7d20c5394c9e', 'FIELD_STATUS_UUID', '09f068599d89407a4fd5b48d8ad58061', 2, true, '1', true, false, false, '2019-07-19 15:56:43.801', NULL, false);\n"
            + "INSERT INTO public.reg_fieldmapping VALUES ('5e63e42ebdf0f501692f736c9765d5bf', 'FIELD_REGISTEROWNER_UUID', '09f068599d89407a4fd5b48d8ad58061', 4, false, '1', true, false, false, '2019-07-19 15:56:43.801', NULL, false);\n"
            + "INSERT INTO public.reg_fieldmapping VALUES ('c61b272097d17e920244d60a855649ed', 'FIELD_DEFINITION_UUID', '233b80ac5ad1731cb789f3dbf33811b7', 1, true, '1', true, false, false, '2019-07-19 15:56:43.849', NULL, false);\n"
            + "INSERT INTO public.reg_fieldmapping VALUES ('e6be9e7bf3984c9cb149816ea5532a57', 'FIELD_STATUS_UUID', '233b80ac5ad1731cb789f3dbf33811b7', 2, true, '1', true, false, false, '2019-07-19 15:56:43.849', NULL, false);\n"
            + "INSERT INTO public.reg_fieldmapping VALUES ('819e2d846d6d64d7e5a4f87fc26b9996', 'FIELD_LABEL_UUID', '233b80ac5ad1731cb789f3dbf33811b7', 0, true, '1', true, false, false, '2019-07-19 15:56:43.849', NULL, false);\n"
            + "INSERT INTO public.reg_fieldmapping VALUES ('25d75c262d052475cc7ae86f439364b6', 'FIELD_CONTENTBODY_UUID', '4d843e0e6ac86113f543de29b0c8e81d', 5, false, '1', true, false, false, '2019-07-30 09:30:44.703', NULL, false);\n"
            + "INSERT INTO public.reg_fieldmapping VALUES ('407b742be797baad13c0c4a804fada78', 'FIELD_REGISTERMANAGER_UUID', '4d843e0e6ac86113f543de29b0c8e81d', 3, false, '1', true, false, false, '2019-07-30 09:30:44.703', NULL, false);\n"
            + "INSERT INTO public.reg_fieldmapping VALUES ('92ffa2daab988bf7776fc32a656b2053', 'FIELD_CONTENTSUMMARY_UUID', '4d843e0e6ac86113f543de29b0c8e81d', 1, true, '1', true, false, false, '2019-07-30 09:30:44.703', NULL, false);\n"
            + "INSERT INTO public.reg_fieldmapping VALUES ('c6d88350c419c13485dcdcff25ba464b', 'FIELD_SUBMITTINGORGANIZATION_UUID', '4d843e0e6ac86113f543de29b0c8e81d', 6, false, '1', true, false, true, '2019-07-30 09:30:44.703', NULL, false);\n"
            + "INSERT INTO public.reg_fieldmapping VALUES ('5584f27e4c31661f6ddd24da9f177ee1', 'FIELD_LABEL_UUID', '4d843e0e6ac86113f543de29b0c8e81d', 0, true, '1', true, false, false, '2019-07-30 09:30:44.703', NULL, false);\n"
            + "INSERT INTO public.reg_fieldmapping VALUES ('a9d437f0709767f5be3d1d68815fee5b', 'FIELD_STATUS_UUID', '4d843e0e6ac86113f543de29b0c8e81d', 2, true, '1', true, false, false, '2019-07-30 09:30:44.703', NULL, false);\n"
            + "INSERT INTO public.reg_fieldmapping VALUES ('e20e8c3c1b4a0d61cc1a914533167930', 'FIELD_REGISTEROWNER_UUID', '4d843e0e6ac86113f543de29b0c8e81d', 4, false, '1', true, false, false, '2019-07-30 09:30:44.703', NULL, false);\n"
            + "INSERT INTO public.reg_fieldmapping VALUES ('48ec9ccd0fa7e98fd6a374ff9c39409f', 'FIELD_DEFINITION_UUID', '1053ab3f4a9e29a3d6d24d1ac5125667', 1, true, '1', true, false, false, '2019-07-30 09:30:44.765', NULL, false);\n"
            + "INSERT INTO public.reg_fieldmapping VALUES ('5430d51618a0be29084b5745c63fa83d', 'FIELD_STATUS_UUID', '1053ab3f4a9e29a3d6d24d1ac5125667', 2, true, '1', true, false, false, '2019-07-30 09:30:44.765', NULL, false);\n"
            + "INSERT INTO public.reg_fieldmapping VALUES ('44877fee54e2c8e153aeff694de6e3e9', 'FIELD_LABEL_UUID', '1053ab3f4a9e29a3d6d24d1ac5125667', 0, true, '1', true, false, false, '2019-07-30 09:30:44.765', NULL, false);";

    private final String QUERY_ITEM
            = "INSERT INTO public.reg_item VALUES ('050ef0d0e3bb244923bd94c183852445', 'governance-level', '09f068599d89407a4fd5b48d8ad58061', '1', NULL, false, 0, '2019-07-19 15:57:31.852', NULL, 'USERMIGRATION_UUID', true);\n"
            + "INSERT INTO public.reg_item VALUES ('5247138f69b59f59ddacf742cdb5b448', 'eu-legal', '233b80ac5ad1731cb789f3dbf33811b7', '1', NULL, false, 0, '2019-07-30 09:25:06.544', '2019-07-29 17:09:47.852', 'USERMIGRATION_UUID', false);\n"
            + "INSERT INTO public.reg_item VALUES ('24bb2984999c116018d3ba95c1aa3082', 'eu-technical', '233b80ac5ad1731cb789f3dbf33811b7', '1', NULL, false, 0, '2019-07-31 11:07:50.016', '2019-07-31 11:07:50.011398', 'USERMIGRATION_UUID', false);\n"
            + "INSERT INTO public.reg_item VALUES ('e7462cf53d17f16c4c0f8e48d40055a7', 'eu-good-practice', '233b80ac5ad1731cb789f3dbf33811b7', '1', NULL, false, 0, '2021-04-15 11:07:50.016', '2021-04-15 11:07:50.011398', 'USERMIGRATION_UUID', false);\n"
            + "INSERT INTO public.reg_item VALUES ('a2290d202bbd591377234ce2eee350df', 'extensibility', '4d843e0e6ac86113f543de29b0c8e81d', '1', NULL, false, 0, '2019-07-30 10:56:01.607', '2019-07-30 10:56:01.548234', 'USERMIGRATION_UUID', false);\n"
            + "INSERT INTO public.reg_item VALUES ('aad13ff0ff9fb151cb8410801e4eeb0a', 'open', '1053ab3f4a9e29a3d6d24d1ac5125667', '1', NULL, false, 0, '2019-07-30 14:33:39.699', '2019-07-30 14:33:39.601507', 'USERMIGRATION_UUID', false);\n"
            + "INSERT INTO public.reg_item VALUES ('31082311814c08a2ae0e5e647669c73f', 'none', '1053ab3f4a9e29a3d6d24d1ac5125667', '1', NULL, false, 0, '2019-07-30 14:33:47.792', '2019-07-30 14:33:47.695846', 'USERMIGRATION_UUID', false);\n"
            + "INSERT INTO public.reg_item VALUES ('088f861f19d356685c0da1916fbb260a', 'any', '1053ab3f4a9e29a3d6d24d1ac5125667', '1', NULL, false, 0, '2019-07-30 14:34:09.993', '2019-07-30 14:34:09.90134', 'USERMIGRATION_UUID', false);\n"
            + "INSERT INTO public.reg_item VALUES ('91e00e7f8d73c54fa2f80c67d3d9d5b0', 'narrower', '1053ab3f4a9e29a3d6d24d1ac5125667', '1', NULL, false, 0, '2019-07-30 14:35:08.175', '2019-07-30 14:35:08.09368', 'USERMIGRATION_UUID', false);";

    String[] itemsUUID = {
        "050ef0d0e3bb244923bd94c183852445",
        "5247138f69b59f59ddacf742cdb5b448",
        "24bb2984999c116018d3ba95c1aa3082",
        "e7462cf53d17f16c4c0f8e48d40055a7",
        "a2290d202bbd591377234ce2eee350df",
        "aad13ff0ff9fb151cb8410801e4eeb0a",
        "31082311814c08a2ae0e5e647669c73f",
        "088f861f19d356685c0da1916fbb260a",
        "91e00e7f8d73c54fa2f80c67d3d9d5b0"};

    private final String QUERY_ITEM_GROUP_ROLE
            = "INSERT INTO public.reg_item_reg_group_reg_role_mapping VALUES ('690a97c469c9979230df939d5b3f3ae5', '050ef0d0e3bb244923bd94c183852445', 'GROUP_REGISTERMANAGER_UUID', '2', '2019-07-19 15:57:31.899', NULL);\n"
            + "INSERT INTO public.reg_item_reg_group_reg_role_mapping VALUES ('f6d77d88d315b56005bd04e1e1e3634e', '050ef0d0e3bb244923bd94c183852445', 'GROUP_CONTENTBODY_UUID', '4', '2019-07-19 15:57:31.904', NULL);\n"
            + "INSERT INTO public.reg_item_reg_group_reg_role_mapping VALUES ('3ab3f7c6738165d1f1c26dba97c3063e', '050ef0d0e3bb244923bd94c183852445', 'GROUP_SUBMITTINGORGANIZATION_UUID', '5', '2019-07-19 15:57:31.908', NULL);\n"
            + "INSERT INTO public.reg_item_reg_group_reg_role_mapping VALUES ('eb04b2c72c9d80b77e2f1e872b600464', 'a2290d202bbd591377234ce2eee350df', 'GROUP_REGISTEROWNER_UUID', '3', '2019-07-30 10:34:07.426', NULL);\n"
            + "INSERT INTO public.reg_item_reg_group_reg_role_mapping VALUES ('02fbd1b2b20bbc6027b6ae27f8c4c638', 'a2290d202bbd591377234ce2eee350df', 'GROUP_REGISTERMANAGER_UUID', '2', '2019-07-30 10:34:07.442', NULL);\n"
            + "INSERT INTO public.reg_item_reg_group_reg_role_mapping VALUES ('21863b78bcbe549879778f5b6197cc28', 'a2290d202bbd591377234ce2eee350df', 'GROUP_CONTENTBODY_UUID', '4', '2019-07-30 10:34:07.446', NULL);\n"
            + "INSERT INTO public.reg_item_reg_group_reg_role_mapping VALUES ('6caf0d9f6da96458c1db9a5e44f7a380', 'a2290d202bbd591377234ce2eee350df', 'GROUP_SUBMITTINGORGANIZATION_UUID', '5', '2019-07-30 10:34:07.449', NULL);";

    private final String QUERY_RELATION
            = "INSERT INTO public.reg_relation VALUES ('3d342b5c93bbc62011c5ee983b59e4d6', '050ef0d0e3bb244923bd94c183852445', '1', 'REGISTRY_UUID', '2019-07-19 15:57:31.861', NULL);\n"
            + "INSERT INTO public.reg_relation VALUES ('9a774ff38b9efe06876169cf5ec80ba1', '5247138f69b59f59ddacf742cdb5b448', '1', 'REGISTRY_UUID', '2019-07-30 09:25:06.591', NULL);\n"
            + "INSERT INTO public.reg_relation VALUES ('a92761d17cc0630afb859df3c59985df', '5247138f69b59f59ddacf742cdb5b448', '2', '050ef0d0e3bb244923bd94c183852445', '2019-07-30 09:25:06.591', NULL);\n"
            + "INSERT INTO public.reg_relation VALUES ('dddfb7c954965948bd515f542f15f5ed', 'a2290d202bbd591377234ce2eee350df', '1', 'REGISTRY_UUID', '2019-07-30 10:34:07.396', NULL);\n"
            + "INSERT INTO public.reg_relation VALUES ('99beb008e7755f6991a01214f9e8c53b', '91e00e7f8d73c54fa2f80c67d3d9d5b0', '1', 'REGISTRY_UUID', '2019-07-30 10:41:43.879', NULL);\n"
            + "INSERT INTO public.reg_relation VALUES ('9f3b664331a45827a1d9f2b87f3987d7', '91e00e7f8d73c54fa2f80c67d3d9d5b0', '2', 'a2290d202bbd591377234ce2eee350df', '2019-07-30 10:41:43.879', NULL);\n"
            + "INSERT INTO public.reg_relation VALUES ('519e987ebea57b8399b7413168dc41c7', '088f861f19d356685c0da1916fbb260a', '1', 'REGISTRY_UUID', '2019-07-30 10:45:49.888', NULL);\n"
            + "INSERT INTO public.reg_relation VALUES ('9e147ef61e15460d6e3f12ba681e75b6', '088f861f19d356685c0da1916fbb260a', '2', 'a2290d202bbd591377234ce2eee350df', '2019-07-30 10:45:49.888', NULL);\n"
            + "INSERT INTO public.reg_relation VALUES ('2fbc99dbf6f7564f8e6323d124d270f8', 'aad13ff0ff9fb151cb8410801e4eeb0a', '1', 'REGISTRY_UUID', '2019-07-30 10:45:51.981', NULL);\n"
            + "INSERT INTO public.reg_relation VALUES ('b8e6350c84fd8d646471ca53c7a2b8e9', 'aad13ff0ff9fb151cb8410801e4eeb0a', '2', 'a2290d202bbd591377234ce2eee350df', '2019-07-30 10:45:51.981', NULL);\n"
            + "INSERT INTO public.reg_relation VALUES ('cdbafce40ef0bd588686e936eee8a30d', '31082311814c08a2ae0e5e647669c73f', '2', 'a2290d202bbd591377234ce2eee350df', '2019-07-30 10:46:04.223', NULL);\n"
            + "INSERT INTO public.reg_relation VALUES ('9499c1e96e073a939393ecd3b6b33508', '31082311814c08a2ae0e5e647669c73f', '1', 'REGISTRY_UUID', '2019-07-30 10:46:04.223', NULL);\n"
            + "INSERT INTO public.reg_relation VALUES ('20edf377e7c22992aff2102ecc7a3a63', '24bb2984999c116018d3ba95c1aa3082', '1', 'REGISTRY_UUID', '2019-07-31 11:05:04.812', NULL);\n"
            + "INSERT INTO public.reg_relation VALUES ('e0670112b1d4ee26027689d8c625a5ac', '24bb2984999c116018d3ba95c1aa3082', '2', '050ef0d0e3bb244923bd94c183852445', '2019-07-31 11:05:04.812', NULL);"
            + "INSERT INTO public.reg_relation VALUES ('2fda34c09e874729192fb35a8f9a8dda', 'e7462cf53d17f16c4c0f8e48d40055a7', '1', 'REGISTRY_UUID', '2019-07-31 11:05:04.812', NULL);\n"
            + "INSERT INTO public.reg_relation VALUES ('a3cd7b9f291cf9851971f4c47509a501', 'e7462cf53d17f16c4c0f8e48d40055a7', '2', '050ef0d0e3bb244923bd94c183852445', '2019-07-31 11:05:04.812', NULL);";

    private final String QUERY_GOVERNANCELEVEL
            = "INSERT INTO public.reg_localization VALUES ('a7a42f83dff18daf47035605d1e1d6e8', 'en', '050ef0d0e3bb244923bd94c183852445', 'FIELD_LABEL_UUID', 0, 'Governance level', NULL, NULL, NULL, '2019-07-19 15:57:31.872', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('c82a9a1a2ca98853cb96c512db7f2766', 'en', '050ef0d0e3bb244923bd94c183852445', 'FIELD_CONTENTSUMMARY_UUID', 0, '<p>Level at which the item is governed.</p>', NULL, NULL, NULL, '2019-07-19 15:57:31.88', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('6120284b40977e32ebee3f707db03123', 'it', '050ef0d0e3bb244923bd94c183852445', 'FIELD_LABEL_UUID', 0, 'Ambito', NULL, NULL, NULL, '2019-07-31 11:03:36.926', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('6e5cb090baecd581dc81b30a215f28eb', 'it', '050ef0d0e3bb244923bd94c183852445', 'FIELD_CONTENTSUMMARY_UUID', 0, '<p>L''Ambito degli articoli del registro di INSPIRE basati sul modello concettuale generico di INSPIRE, le Direttive tecniche - specifica dei dati e del regolamento UE n 1253/2012 &amp; 1205/2008.</p>', NULL, NULL, NULL, '2019-07-31 11:03:36.926', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('598bf3c8ddcfe0fe2566984f659b6a34', 'de', '050ef0d0e3bb244923bd94c183852445', 'FIELD_LABEL_UUID', 0, 'Regelungsebene', NULL, NULL, NULL, '2019-07-31 11:03:36.926', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('0f30e0871fe84fa1bb5cb33cfc24b735', 'de', '050ef0d0e3bb244923bd94c183852445', 'FIELD_CONTENTSUMMARY_UUID', 0, '<p>Ebene, bei denen das Element geregelt wird.</p>', NULL, NULL, NULL, '2019-07-31 11:03:36.941', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('78363d6193e8ba2e9045f9ad9bd45be2', 'el', '050ef0d0e3bb244923bd94c183852445', 'FIELD_LABEL_UUID', 0, 'Επίπεδο διαχείρισης', NULL, NULL, NULL, '2019-07-31 11:03:36.941', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('f1abe5114e92844d603b9cb857e36dc4', 'el', '050ef0d0e3bb244923bd94c183852445', 'FIELD_CONTENTSUMMARY_UUID', 0, '<p>Επίπεδο κατα το οποίο το στοιχείο διαχείριζεται</p>', NULL, NULL, NULL, '2019-07-31 11:03:36.941', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('a7a31b1b2f365312f834503dea826e4e', 'es', '050ef0d0e3bb244923bd94c183852445', 'FIELD_LABEL_UUID', 0, 'Nivel de gobernanza', NULL, NULL, NULL, '2019-07-31 11:03:36.941', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('b66fafe6024bbc997cfc4eb62f399ddd', 'es', '050ef0d0e3bb244923bd94c183852445', 'FIELD_CONTENTSUMMARY_UUID', 0, '<p>Nivel por el que se rige el elemento</p>', NULL, NULL, NULL, '2019-07-31 11:03:36.941', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('7e94620ce19eb4cb48cd62168a77907b', 'hr', '050ef0d0e3bb244923bd94c183852445', 'FIELD_LABEL_UUID', 0, 'Razina upravljanja', NULL, NULL, NULL, '2019-07-31 11:03:36.972', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('9c0c9fbfce1d72f8ace5dcda23035741', 'hr', '050ef0d0e3bb244923bd94c183852445', 'FIELD_CONTENTSUMMARY_UUID', 0, '<p>Razina na kojoj se upravlja stavkom</p>', NULL, NULL, NULL, '2019-07-31 11:03:36.972', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('97b329892ecae60f9a638e6ca05e3e84', 'ro', '050ef0d0e3bb244923bd94c183852445', 'FIELD_LABEL_UUID', 0, 'Nivel de guvernare', NULL, NULL, NULL, '2019-07-31 11:03:36.988', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('7c5b9e9840fc9fe636684a2264d221f7', 'ro', '050ef0d0e3bb244923bd94c183852445', 'FIELD_CONTENTSUMMARY_UUID', 0, '<p>Nivelul la care elementul este reglementat.</p>', NULL, NULL, NULL, '2019-07-31 11:03:36.988', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('18bef825e80b386a1635d6593d63110f', 'en', '5247138f69b59f59ddacf742cdb5b448', 'FIELD_LABEL_UUID', 0, 'Legal (EU)', NULL, NULL, NULL, '2019-07-30 09:25:06.622', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('a9deff255400ff0676ca06884c9f837c', 'en', '5247138f69b59f59ddacf742cdb5b448', 'FIELD_DEFINITION_UUID', 0, '<p>The item is governed at the level of INSPIRE legal acts, including the INSPIRE Directive and Implementing Rules.</p>', NULL, NULL, NULL, '2019-07-30 09:25:06.638', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('3a1c46b2a8574a31b4e25438aa988b08', 'it', '5247138f69b59f59ddacf742cdb5b448', 'FIELD_LABEL_UUID', 0, 'Linee guida tecniche sulla specifica dei dati', NULL, NULL, NULL, '2019-07-30 09:25:06.638', '2019-07-29 16:45:03.384');\n"
            + "INSERT INTO public.reg_localization VALUES ('cbd323257278c63b8b3f2ed2ab68415c', 'it', '5247138f69b59f59ddacf742cdb5b448', 'FIELD_DEFINITION_UUID', 0, '<p>Il contenuto del registro include ulteriori valori raccomandati dalle linee guida tecniche INSPIRE - specifiche dei dati.</p>', NULL, NULL, NULL, '2019-07-30 09:25:06.638', '2019-07-29 16:45:03.384');\n"
            + "INSERT INTO public.reg_localization VALUES ('86b2704624dcb7347fcc214db3543048', 'de', '5247138f69b59f59ddacf742cdb5b448', 'FIELD_LABEL_UUID', 0, 'Rechtsgültig (EU)', NULL, NULL, NULL, '2019-07-30 09:25:06.653', '2019-07-29 16:40:36.561');\n"
            + "INSERT INTO public.reg_localization VALUES ('6179688e29afaf463dfdd2362686cff4', 'de', '5247138f69b59f59ddacf742cdb5b448', 'FIELD_DEFINITION_UUID', 0, '<p>Das Element ist durch gesetzliche INSPIRE Bestimmungen einschließlich der INSPIRE-Richtlinie und Durchführungsbestimmungen geregelt.</p>', NULL, NULL, NULL, '2019-07-30 09:25:06.653', '2019-07-29 16:35:47.121');\n"
            + "INSERT INTO public.reg_localization VALUES ('5f031c642a675a0240427c9805d2a769', 'el', '5247138f69b59f59ddacf742cdb5b448', 'FIELD_LABEL_UUID', 0, 'Νομικό (ΕΕ)', NULL, NULL, NULL, '2019-07-30 09:25:06.653', '2019-07-29 16:40:36.563');\n"
            + "INSERT INTO public.reg_localization VALUES ('fb463530f9a2a5d8ff9a091ca02d908e', 'el', '5247138f69b59f59ddacf742cdb5b448', 'FIELD_DEFINITION_UUID', 0, '<p>Το στοιχείο διαχειριζεται στο επίπεδο των νομικώς πράξεων INSPIRE, συμπεριλαμβανομένης της οδηγίας INSPIRE και τους κανόνες εφαρμογής.</p>', NULL, NULL, NULL, '2019-07-30 09:25:06.669', '2019-07-29 16:36:23.213');\n"
            + "INSERT INTO public.reg_localization VALUES ('ca76242aeaf5959992d7df19a91f7dd1', 'es', '5247138f69b59f59ddacf742cdb5b448', 'FIELD_LABEL_UUID', 0, 'Legal (UE)', NULL, NULL, NULL, '2019-07-30 09:25:06.653', '2019-07-29 16:43:12.635');\n"
            + "INSERT INTO public.reg_localization VALUES ('42776f16401990212f7eb3d891f9d949', 'es', '5247138f69b59f59ddacf742cdb5b448', 'FIELD_DEFINITION_UUID', 0, '<p>El elemento se rige por la regulación INSPIRE, incluyendo la Directiva INSPIRE y sus reglamentos.</p>', NULL, NULL, NULL, '2019-07-30 09:25:06.669', '2019-07-29 16:43:12.635');\n"
            + "INSERT INTO public.reg_localization VALUES ('192de7ef7a44c56d831c03cd7f7b0c50', 'hr', '5247138f69b59f59ddacf742cdb5b448', 'FIELD_LABEL_UUID', 0, 'Pravni (EU)', NULL, NULL, NULL, '2019-07-30 09:25:06.669', '2019-07-29 16:44:41.928');\n"
            + "INSERT INTO public.reg_localization VALUES ('3541b84bdc50ad42181ec27279779eff', 'hr', '5247138f69b59f59ddacf742cdb5b448', 'FIELD_DEFINITION_UUID', 0, '', NULL, NULL, NULL, '2019-07-30 09:25:06.669', '2019-07-29 16:44:41.928');\n"
            + "INSERT INTO public.reg_localization VALUES ('94174d6b7307a13e848616096051b301', 'ro', '5247138f69b59f59ddacf742cdb5b448', 'FIELD_LABEL_UUID', 0, 'Legal (EU)', NULL, NULL, NULL, '2019-07-30 09:25:06.653', '2019-07-29 16:43:40.026');\n"
            + "INSERT INTO public.reg_localization VALUES ('d769687f216ff29b2ca40fd4a2b14676', 'ro', '5247138f69b59f59ddacf742cdb5b448', 'FIELD_DEFINITION_UUID', 0, '<p>Elementul este reglementat la nivelul actelor juridice INSPIRE, inclusiv Directiva INSPIRE și normele de aplicare.</p>', NULL, NULL, NULL, '2019-07-30 09:25:06.669', '2019-07-29 17:08:56.442');\n"
            + "INSERT INTO public.reg_localization VALUES ('0b1e284c746dd880ab92d12d2f62ed6c', 'en', '24bb2984999c116018d3ba95c1aa3082', 'FIELD_LABEL_UUID', 0, 'Technical (EU)', NULL, NULL, NULL, '2019-07-31 11:05:04.812', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('82f6cf74d357265d46dad08ccb1b7c17', 'en', '24bb2984999c116018d3ba95c1aa3082', 'FIELD_DEFINITION_UUID', 0, '<p>The item is governed at the level of INSPIRE Technical Guidelines.</p>', NULL, NULL, NULL, '2019-07-31 11:05:04.828', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('60f5acc564659da7c39833519653b77c', 'it', '24bb2984999c116018d3ba95c1aa3082', 'FIELD_LABEL_UUID', 0, 'Norme di attuazione EU1253/2013', NULL, NULL, NULL, '2019-07-31 11:07:50.016', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('b47e79f525ecda87ab65b1b6f92851a7', 'it', '24bb2984999c116018d3ba95c1aa3082', 'FIELD_DEFINITION_UUID', 0, '<p>I valori sono il contenuto giuridicamente vincolante delle norme di attuazione ISDSS (UE N. 1253/2013 dal 21 ottobre 2013).</p>', NULL, NULL, NULL, '2019-07-31 11:07:50.032', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('18f46d46d877c133365b420495f745ce', 'de', '24bb2984999c116018d3ba95c1aa3082', 'FIELD_LABEL_UUID', 0, 'Technisch (EU)', NULL, NULL, NULL, '2019-07-31 11:07:50.032', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('9944681a9d8a80a1108c83c5d777c8ea', 'de', '24bb2984999c116018d3ba95c1aa3082', 'FIELD_DEFINITION_UUID', 0, '<p>Das Element wird auf der Ebene der INSPIRE Technischen Dokuments geregelt.</p>', NULL, NULL, NULL, '2019-07-31 11:07:50.032', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('bae71270630e200bda121ff1f86f123c', 'el', '24bb2984999c116018d3ba95c1aa3082', 'FIELD_LABEL_UUID', 0, 'Τεχνικό (ΕΕ)', NULL, NULL, NULL, '2019-07-31 11:07:50.048', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('77bc5eb2d4017a05d9e0b98dce373f00', 'el', '24bb2984999c116018d3ba95c1aa3082', 'FIELD_DEFINITION_UUID', 0, '<p>Το στοιχείο διαχειριζεται στο επίπεδο των τεχνικών οδηγιών INSPIRE.</p>', NULL, NULL, NULL, '2019-07-31 11:07:50.048', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('d9097d14aefab3399cdcbfb76be33505', 'es', '24bb2984999c116018d3ba95c1aa3082', 'FIELD_LABEL_UUID', 0, 'Técnico (UE)', NULL, NULL, NULL, '2019-07-31 11:07:50.048', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('b9954d57960f7142f2ffc992093dd95f', 'es', '24bb2984999c116018d3ba95c1aa3082', 'FIELD_DEFINITION_UUID', 0, '<p>El elemento se rige las Guías Técnicas INSPIRE</p>', NULL, NULL, NULL, '2019-07-31 11:07:50.048', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('4f880ab0108e593197b1d10bf310a781', 'hr', '24bb2984999c116018d3ba95c1aa3082', 'FIELD_LABEL_UUID', 0, 'Tehnički (EU)', NULL, NULL, NULL, '2019-07-31 11:07:50.048', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('754e7c3e91dc60f25736dd8ad29033f3', 'hr', '24bb2984999c116018d3ba95c1aa3082', 'FIELD_DEFINITION_UUID', 0, '<p>Stavkom se upravlja na razini Tehničkih naputaka INSPIRE-a</p>', NULL, NULL, NULL, '2019-07-31 11:07:50.063', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('40c9d2c3bad430b9509e7a511a7cd37b', 'ro', '24bb2984999c116018d3ba95c1aa3082', 'FIELD_LABEL_UUID', 0, 'Tehnic (EU)', NULL, NULL, NULL, '2019-07-31 11:07:50.063', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('f6bfb1b041ca41bf92bae2581898465d', 'ro', '24bb2984999c116018d3ba95c1aa3082', 'FIELD_DEFINITION_UUID', 0, '<p>Elementul este reglementat la nivelul Ghidului Tehnic INSPIRE.</p>', NULL, NULL, NULL, '2019-07-31 11:07:50.063', NULL);"
            + "INSERT INTO public.reg_localization VALUES ('d3b65ef302fdf442030063604b5786fd', 'en', 'e7462cf53d17f16c4c0f8e48d40055a7', 'FIELD_LABEL_UUID', 0, 'Good Pranctice (EU)', NULL, NULL, NULL, '2021-04-15 11:05:04.812', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('31fa9b5a822ffec25e86a77775403a7d', 'en', 'e7462cf53d17f16c4c0f8e48d40055a7', 'FIELD_DEFINITION_UUID', 0, '<p>The item is governed at the level of INSPIRE Good Practice.</p>', NULL, NULL, NULL, '2019-07-31 11:05:04.828', NULL);\n";

    private final String QUERY_EXTENSIBILITY
            = "INSERT INTO public.reg_localization VALUES ('e3bdb192afe7e9c9f1d77270d87adf7d', 'en', 'a2290d202bbd591377234ce2eee350df', 'FIELD_LABEL_UUID', 0, 'Extensibility', NULL, NULL, NULL, '2019-07-30 10:34:07.401', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('3ebfd81f0dd3109bf86fa60f994e28af', 'bg', 'a2290d202bbd591377234ce2eee350df', 'FIELD_LABEL_UUID', 0, 'Разширяемост', NULL, NULL, NULL, '2019-07-30 10:56:01.617', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('667e53d2cfaf9b103490a6f3b339e6d5', 'bg', 'a2290d202bbd591377234ce2eee350df', 'FIELD_CONTENTSUMMARY_UUID', 0, '', NULL, NULL, NULL, '2019-07-30 10:56:01.62', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('8b890ec36809112032dfeb80adccf0f7', 'cs', 'a2290d202bbd591377234ce2eee350df', 'FIELD_LABEL_UUID', 0, 'Rozšiřitelnost', NULL, NULL, NULL, '2019-07-30 10:56:01.623', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('93e824ca8a54b549b47b117410535b60', 'cs', 'a2290d202bbd591377234ce2eee350df', 'FIELD_CONTENTSUMMARY_UUID', 0, '', NULL, NULL, NULL, '2019-07-30 10:56:01.627', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('297fdd354aff9b34f79d035b83546641', 'da', 'a2290d202bbd591377234ce2eee350df', 'FIELD_LABEL_UUID', 0, 'Udvidelsesmuligheder', NULL, NULL, NULL, '2019-07-30 10:56:01.63', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('5670c7b9594df418ddc5bcd69348b652', 'da', 'a2290d202bbd591377234ce2eee350df', 'FIELD_CONTENTSUMMARY_UUID', 0, '', NULL, NULL, NULL, '2019-07-30 10:56:01.633', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('38cef7ba2b15c14a46ab3912540abf0c', 'de', 'a2290d202bbd591377234ce2eee350df', 'FIELD_LABEL_UUID', 0, 'Erweiterbarkeit', NULL, NULL, NULL, '2019-07-30 10:56:01.637', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('6b218568ba0b3368d67ad45ec60e069c', 'de', 'a2290d202bbd591377234ce2eee350df', 'FIELD_CONTENTSUMMARY_UUID', 0, '', NULL, NULL, NULL, '2019-07-30 10:56:01.64', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('ee4c4817ff90a8435a873141153bc63d', 'et', 'a2290d202bbd591377234ce2eee350df', 'FIELD_LABEL_UUID', 0, 'Laiendatavus', NULL, NULL, NULL, '2019-07-30 10:56:01.644', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('8de15d517f7cf68d431da443a973caed', 'et', 'a2290d202bbd591377234ce2eee350df', 'FIELD_CONTENTSUMMARY_UUID', 0, '', NULL, NULL, NULL, '2019-07-30 10:56:01.647', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('2061f9f779758b540642718daed3dea2', 'el', 'a2290d202bbd591377234ce2eee350df', 'FIELD_LABEL_UUID', 0, 'Επεκτασιμότητα', NULL, NULL, NULL, '2019-07-30 10:56:01.65', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('bf48404b8d9abc4ddeaed0c9c242d8eb', 'el', 'a2290d202bbd591377234ce2eee350df', 'FIELD_CONTENTSUMMARY_UUID', 0, '', NULL, NULL, NULL, '2019-07-30 10:56:01.653', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('137680360b3cb05c9ef2236aafb99be1', 'es', 'a2290d202bbd591377234ce2eee350df', 'FIELD_LABEL_UUID', 0, 'Extensibilidad', NULL, NULL, NULL, '2019-07-30 10:56:01.658', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('5134d03b0bcffebc448591300eaf5462', 'es', 'a2290d202bbd591377234ce2eee350df', 'FIELD_CONTENTSUMMARY_UUID', 0, '', NULL, NULL, NULL, '2019-07-30 10:56:01.663', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('52c98d148187b1fd6712c7036431edfd', 'fr', 'a2290d202bbd591377234ce2eee350df', 'FIELD_LABEL_UUID', 0, 'Extensibilité', NULL, NULL, NULL, '2019-07-30 10:56:01.667', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('dfd72fd55b61fe679d61366a49d7ab37', 'fr', 'a2290d202bbd591377234ce2eee350df', 'FIELD_CONTENTSUMMARY_UUID', 0, '', NULL, NULL, NULL, '2019-07-30 10:56:01.671', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('ea748cb80f69dc729e340ff504b1376b', 'hr', 'a2290d202bbd591377234ce2eee350df', 'FIELD_LABEL_UUID', 0, 'Proširivost', NULL, NULL, NULL, '2019-07-30 10:56:01.675', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('4de8855439e8df8bb955a5e0dc6f9f75', 'hr', 'a2290d202bbd591377234ce2eee350df', 'FIELD_CONTENTSUMMARY_UUID', 0, '', NULL, NULL, NULL, '2019-07-30 10:56:01.678', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('d12b968cfe441ad3f557f2e45fca4784', 'it', 'a2290d202bbd591377234ce2eee350df', 'FIELD_LABEL_UUID', 0, 'Estendibilità', NULL, NULL, NULL, '2019-07-30 10:56:01.682', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('e8321a59a35cbaec6fffe39df9daa11b', 'it', 'a2290d202bbd591377234ce2eee350df', 'FIELD_CONTENTSUMMARY_UUID', 0, '', NULL, NULL, NULL, '2019-07-30 10:56:01.686', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('e7171646bb2e05e8ecb87edf33a50d27', 'lv', 'a2290d202bbd591377234ce2eee350df', 'FIELD_LABEL_UUID', 0, 'Paplašināmība', NULL, NULL, NULL, '2019-07-30 10:56:01.69', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('bb579d5d252e580c1ca877c77996b705', 'lv', 'a2290d202bbd591377234ce2eee350df', 'FIELD_CONTENTSUMMARY_UUID', 0, '', NULL, NULL, NULL, '2019-07-30 10:56:01.693', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('1c21a86ad29829a40757dd03cc9aef83', 'lt', 'a2290d202bbd591377234ce2eee350df', 'FIELD_LABEL_UUID', 0, 'Plečiamumas', NULL, NULL, NULL, '2019-07-30 10:56:01.696', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('357dec0654a04d99b593e4a5166a867a', 'lt', 'a2290d202bbd591377234ce2eee350df', 'FIELD_CONTENTSUMMARY_UUID', 0, '', NULL, NULL, NULL, '2019-07-30 10:56:01.699', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('04c4a19ee9ae9e3bcf6edcdbc83f4eaf', 'hu', 'a2290d202bbd591377234ce2eee350df', 'FIELD_LABEL_UUID', 0, 'Bővíthetőség', NULL, NULL, NULL, '2019-07-30 10:56:01.703', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('4f67fc9d49fed35d734bd370ae908259', 'hu', 'a2290d202bbd591377234ce2eee350df', 'FIELD_CONTENTSUMMARY_UUID', 0, '', NULL, NULL, NULL, '2019-07-30 10:56:01.707', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('2a76915b91c3f67ec29eb2a20be4c851', 'mt', 'a2290d202bbd591377234ce2eee350df', 'FIELD_LABEL_UUID', 0, 'Estensibbiltà', NULL, NULL, NULL, '2019-07-30 10:56:01.711', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('1d9ca98e41cf489a0185083045a0e7bc', 'mt', 'a2290d202bbd591377234ce2eee350df', 'FIELD_CONTENTSUMMARY_UUID', 0, '', NULL, NULL, NULL, '2019-07-30 10:56:01.715', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('6a1d0f380fe5a5017891fbeb77ee9957', 'nl', 'a2290d202bbd591377234ce2eee350df', 'FIELD_LABEL_UUID', 0, 'Uitbreidbaarheid', NULL, NULL, NULL, '2019-07-30 10:56:01.718', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('90c69388a5b4c9f0b2272feabd85b5b7', 'nl', 'a2290d202bbd591377234ce2eee350df', 'FIELD_CONTENTSUMMARY_UUID', 0, '', NULL, NULL, NULL, '2019-07-30 10:56:01.721', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('23834a39e4ab7056d21789d2a4a2fa68', 'fi', 'a2290d202bbd591377234ce2eee350df', 'FIELD_LABEL_UUID', 0, 'Laajennettavuus', NULL, NULL, NULL, '2019-07-30 10:56:01.725', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('6c682c03b35686438999a651eee700bd', 'fi', 'a2290d202bbd591377234ce2eee350df', 'FIELD_CONTENTSUMMARY_UUID', 0, '', NULL, NULL, NULL, '2019-07-30 10:56:01.728', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('573385d80c2bbe3f3558b1969465605f', 'pl', 'a2290d202bbd591377234ce2eee350df', 'FIELD_LABEL_UUID', 0, 'Rozszerzalność', NULL, NULL, NULL, '2019-07-30 10:56:01.732', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('beb03caca0ff77661552a36d6add096b', 'pl', 'a2290d202bbd591377234ce2eee350df', 'FIELD_CONTENTSUMMARY_UUID', 0, '', NULL, NULL, NULL, '2019-07-30 10:56:01.735', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('a000eb19b4dfd649645f1588453172b7', 'pt', 'a2290d202bbd591377234ce2eee350df', 'FIELD_LABEL_UUID', 0, 'Extensibilidade', NULL, NULL, NULL, '2019-07-30 10:56:01.738', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('fc94ad8070e5529e643f076b692226ac', 'pt', 'a2290d202bbd591377234ce2eee350df', 'FIELD_CONTENTSUMMARY_UUID', 0, '', NULL, NULL, NULL, '2019-07-30 10:56:01.742', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('df8f69e3fe4677f793fa644b7ce30685', 'ro', 'a2290d202bbd591377234ce2eee350df', 'FIELD_LABEL_UUID', 0, 'Extensibilitate', NULL, NULL, NULL, '2019-07-30 10:56:01.745', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('26f86a6aa272ce44e1fd5e8476c9aec9', 'ro', 'a2290d202bbd591377234ce2eee350df', 'FIELD_CONTENTSUMMARY_UUID', 0, '', NULL, NULL, NULL, '2019-07-30 10:56:01.749', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('0f908e5e5ed3c578015f27ee9706fe0e', 'sk', 'a2290d202bbd591377234ce2eee350df', 'FIELD_LABEL_UUID', 0, 'Rozšíriteľnosť', NULL, NULL, NULL, '2019-07-30 10:56:01.752', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('0ccae549d758323cb171f3e27aa7dd8a', 'sk', 'a2290d202bbd591377234ce2eee350df', 'FIELD_CONTENTSUMMARY_UUID', 0, '', NULL, NULL, NULL, '2019-07-30 10:56:01.756', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('f64d8d3eaa8f10c63035724db2367eaf', 'sl', 'a2290d202bbd591377234ce2eee350df', 'FIELD_LABEL_UUID', 0, 'Dopolnjevanje', NULL, NULL, NULL, '2019-07-30 10:56:01.759', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('4136380d844979721fc9970367946a27', 'sl', 'a2290d202bbd591377234ce2eee350df', 'FIELD_CONTENTSUMMARY_UUID', 0, '', NULL, NULL, NULL, '2019-07-30 10:56:01.763', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('21a4f04d85b84ef7855436d7a14d865e', 'sv', 'a2290d202bbd591377234ce2eee350df', 'FIELD_LABEL_UUID', 0, 'Utökningsbarhet', NULL, NULL, NULL, '2019-07-30 10:56:01.766', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('1e535d8f874f8ac242c073fbfb51d02b', 'sv', 'a2290d202bbd591377234ce2eee350df', 'FIELD_CONTENTSUMMARY_UUID', 0, '', NULL, NULL, NULL, '2019-07-30 10:56:01.77', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('eb676958fb0c246eb54ef4c53749bb50', 'en', 'aad13ff0ff9fb151cb8410801e4eeb0a', 'FIELD_LABEL_UUID', 0, 'Extensible with values at any level', NULL, NULL, NULL, '2019-07-30 10:45:51.988', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('128b89cd6585c8822da2ac4180faca2e', 'en', 'aad13ff0ff9fb151cb8410801e4eeb0a', 'FIELD_DEFINITION_UUID', 0, '<p>The code list can be extended with additional values at any level, i.e. its allowed values comprise the values specified in this register and additional values at any level defined by data providers.</p>', NULL, NULL, NULL, '2019-07-30 10:45:51.993', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('57a0a3dd49a30c049a9974ba5af5edbd', 'bg', 'aad13ff0ff9fb151cb8410801e4eeb0a', 'FIELD_LABEL_UUID', 0, 'Разширяем със стойности на всяко ниво', NULL, NULL, NULL, '2019-07-30 14:33:39.707', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('e9255ef68df5ff7e7db40deedec1c20b', 'bg', 'aad13ff0ff9fb151cb8410801e4eeb0a', 'FIELD_DEFINITION_UUID', 0, '<p>Списъкът на кодовете може да бъде разширяван с допълнителни стойности от всяко ниво, т.е. допустимите стойности обхващат стойности, посочени в този регистър, и допълнителни стойности от всяко ниво в йерархията, дефинирани от доставчиците на данни.</p>', NULL, NULL, NULL, '2019-07-30 14:33:39.715', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('f30d2d57ed8a14ceb775cda1d3319629', 'cs', 'aad13ff0ff9fb151cb8410801e4eeb0a', 'FIELD_LABEL_UUID', 0, 'Libovolně rozšiřitelný', NULL, NULL, NULL, '2019-07-30 14:33:39.717', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('c1df8811df064cbac6199f629851c938', 'cs', 'aad13ff0ff9fb151cb8410801e4eeb0a', 'FIELD_DEFINITION_UUID', 0, '<p>Číselník může být rozšířen o další libovolné hodnoty, tj. povolené hodnoty zahrnují hodnoty specifikované v registru a dodatečné hodnoty libovolně definované poskytovatelem dat.</p>', NULL, NULL, NULL, '2019-07-30 14:33:39.72', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('1c185abd5f1e28e92f4997bb2b035e16', 'da', 'aad13ff0ff9fb151cb8410801e4eeb0a', 'FIELD_LABEL_UUID', 0, 'Kan udvides med værdier på alle niveauer', NULL, NULL, NULL, '2019-07-30 14:33:39.723', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('15bccf2658334ce80fb7d249c5c4d760', 'da', 'aad13ff0ff9fb151cb8410801e4eeb0a', 'FIELD_DEFINITION_UUID', 0, '<p>Kodelisten kan udvides med værdier på ethvert niveau, dvs. tilladte værdier udgøres af værdier i dette register samt yderligere værdier på alle niveauer.</p>', NULL, NULL, NULL, '2019-07-30 14:33:39.73', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('e882ba334a4d8eca9f22bb48d519e75b', 'de', 'aad13ff0ff9fb151cb8410801e4eeb0a', 'FIELD_LABEL_UUID', 0, 'Erweiterbar um beliebige Werte', NULL, NULL, NULL, '2019-07-30 14:33:39.733', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('570421313624e1b2bbd7cf8644bfe47e', 'de', 'aad13ff0ff9fb151cb8410801e4eeb0a', 'FIELD_DEFINITION_UUID', 0, '<p>Die Codeliste darf um beliebige Werte erweitert werden, d.h. ihre erlaubten Werte umfassen die Werte, die in diesem Register enthalten sind, und zusätzlich beliebige von Datenanbietern definierte Werte.</p>', NULL, NULL, NULL, '2019-07-30 14:33:39.736', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('5d675a8b28e7d98786ccbb6a80121a5b', 'et', 'aad13ff0ff9fb151cb8410801e4eeb0a', 'FIELD_LABEL_UUID', 0, 'Laiendatav väärtustega suvalisel tasemel', NULL, NULL, NULL, '2019-07-30 14:33:39.739', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('242bab4c16a3e14a50d2950dcb6797c1', 'et', 'aad13ff0ff9fb151cb8410801e4eeb0a', 'FIELD_DEFINITION_UUID', 0, '<p>Koodiloendit saab laiendada lisaväärtustega igal tasemel, st selle lubatavad väärtused hõlmavad väärtusi, mis on määratletud selles registris ja andmete tarnijate poolt määratletud lisaväärtused suvalisel tasemel.</p>', NULL, NULL, NULL, '2019-07-30 14:33:39.745', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('40f8951f290c4a5c92f931ab71ad265b', 'el', 'aad13ff0ff9fb151cb8410801e4eeb0a', 'FIELD_LABEL_UUID', 0, 'επεκτάσιμο με τιμές σε οποιοδήποτε επίπεδο', NULL, NULL, NULL, '2019-07-30 14:33:39.749', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('78f76bd95d4c5dd9e5d230368ca13921', 'el', 'aad13ff0ff9fb151cb8410801e4eeb0a', 'FIELD_DEFINITION_UUID', 0, '<p>Αυτός ο κωδικός λίστας μπορεί να επεκταθεί με επιπλέον τιμές σε οποιοδήποτε επίπεδο, οι επιτρεπόμενες τιμές του αποτελούν τις τιμές που ορίζονται από στο καταχωρητή και επιπλέον τιμές σε οποιοδήποτε επίπεδο όπως ορίζονται από παρόχους δεδομένων</p>', NULL, NULL, NULL, '2019-07-30 14:33:39.753', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('f2027e006a93a892da0dd7fca087e6c6', 'es', 'aad13ff0ff9fb151cb8410801e4eeb0a', 'FIELD_LABEL_UUID', 0, 'Extensible con valores a cualquier nivel', NULL, NULL, NULL, '2019-07-30 14:33:39.755', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('eca9600c953c9e8b99cc936291c04a5c', 'es', 'aad13ff0ff9fb151cb8410801e4eeb0a', 'FIELD_DEFINITION_UUID', 0, '<p>Se puede extender la lista de códigos con valores a cualquier nivel; es decir, los valores permitidos son los valores especificados en el registro y cualquier otro definido por el proveedor de datos a cualquier nivel</p>', NULL, NULL, NULL, '2019-07-30 14:33:39.758', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('2b61800ce3d3f534b860e42edda78233', 'fr', 'aad13ff0ff9fb151cb8410801e4eeb0a', 'FIELD_LABEL_UUID', 0, 'Extensible avec des valeurs à tout niveau', NULL, NULL, NULL, '2019-07-30 14:33:39.765', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('0f5c39443a476f8594370b3808c3c266', 'fr', 'aad13ff0ff9fb151cb8410801e4eeb0a', 'FIELD_DEFINITION_UUID', 0, '<p>La liste de codes peut être étendue avec des valeurs à tout niveau: les codes autorisés sont les codes existants et de nouvelles valeurs définies à tout niveau par des fournisseurs de données.</p>', NULL, NULL, NULL, '2019-07-30 14:33:39.768', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('6e8fcaea7e6aa6d567f9a84a4c52ead3', 'hr', 'aad13ff0ff9fb151cb8410801e4eeb0a', 'FIELD_LABEL_UUID', 0, 'Proširivo vrijednostima na svim razinama', NULL, NULL, NULL, '2019-07-30 14:33:39.771', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('1d0b19bfb293f26ed8dc7e262b4a8360', 'hr', 'aad13ff0ff9fb151cb8410801e4eeb0a', 'FIELD_DEFINITION_UUID', 0, '<p>Kodna lista je proširiva dodatnim vrijednostima na svim razinama tj. dozvoljene vrijednosti obuhvaćaju one specificirane u ovom registru kao i dodatne vrijednosti na svim razinama definirane od strane pružatelja podataka</p>', NULL, NULL, NULL, '2019-07-30 14:33:39.773', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('d68410d0e828e1a1c283b4477631b5b7', 'it', 'aad13ff0ff9fb151cb8410801e4eeb0a', 'FIELD_LABEL_UUID', 0, 'Estendibile con valori a qualsiasi livello', NULL, NULL, NULL, '2019-07-30 14:33:39.781', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('08122fd34aa01b3bf3ceafc8f1d2abc6', 'it', 'aad13ff0ff9fb151cb8410801e4eeb0a', 'FIELD_DEFINITION_UUID', 0, '<p>L''elenco dei codici può essere esteso con valori aggiuntivi a qualsiasi livello, vale a dire i suoi valori consentiti comprendono i valori indicati in questo registro e valori aggiuntivi a qualsiasi livello definito dal provider di dati.</p>', NULL, NULL, NULL, '2019-07-30 14:33:39.785', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('b7980e7644e5c453d87cba3d5c91bff7', 'lv', 'aad13ff0ff9fb151cb8410801e4eeb0a', 'FIELD_LABEL_UUID', 0, 'Paplašināms ar jebkura līmeņa vērtībām', NULL, NULL, NULL, '2019-07-30 14:33:39.788', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('48246ee0e2d706544fa489277fcab351', 'lv', 'aad13ff0ff9fb151cb8410801e4eeb0a', 'FIELD_DEFINITION_UUID', 0, '<p>Kodu sarakstu var paplašināt ar jebkura līmeņa papildu vērtībām, t.i., tā atļautās vērtības aptver šajā reģistrā iekļautās vērtības un papildu vērtības, ko jebkurā līmenī ir definējis datu sniedzējs.</p>', NULL, NULL, NULL, '2019-07-30 14:33:39.791', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('5140aa533eedf6dcf134f335a8513753', 'lt', 'aad13ff0ff9fb151cb8410801e4eeb0a', 'FIELD_LABEL_UUID', 0, 'Plečiamas visais lygmenimis', NULL, NULL, NULL, '2019-07-30 14:33:39.795', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('b9cab08c37c9470035fc914dadbe2c54', 'lt', 'aad13ff0ff9fb151cb8410801e4eeb0a', 'FIELD_DEFINITION_UUID', 0, '<p>Kodų sąrašas gali būti papildytas bet kurio lygmens reikšmėmis, t.y., leidžiamos reikšmės apima registre esančias ir duomenų teikėjo nurodytas bet kurio lygmens reikšmes.</p>', NULL, NULL, NULL, '2019-07-30 14:33:39.797', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('3a0697dcc1f4869d3896fc6b555e27a5', 'hu', 'aad13ff0ff9fb151cb8410801e4eeb0a', 'FIELD_LABEL_UUID', 0, 'Az értékek bármely szinten bővíthetők', NULL, NULL, NULL, '2019-07-30 14:33:39.8', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('e99307bf132c85ff1594a4333ee8e66d', 'hu', 'aad13ff0ff9fb151cb8410801e4eeb0a', 'FIELD_DEFINITION_UUID', 0, '<p>A kódlista akármelyik szintjét bármilyen értékekkel lehet bővíteni. A teljes lista magába foglalja az itt meghatározott értékeket és az adatszolgáltató által hozzáadott értékeket.</p>', NULL, NULL, NULL, '2019-07-30 14:33:39.803', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('73c701507e9a5e43c5f648e8f9a12438', 'mt', 'aad13ff0ff9fb151cb8410801e4eeb0a', 'FIELD_LABEL_UUID', 0, 'Estensibbli b''valuri f''kull livell', NULL, NULL, NULL, '2019-07-30 14:33:39.805', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('888a55859421c7861960b1d9d986643e', 'mt', 'aad13ff0ff9fb151cb8410801e4eeb0a', 'FIELD_DEFINITION_UUID', 0, '<p>Il-lista ta'' kodiċi tista'' tiġi estiża b''valuri addizzjonali f''kull livell, jiġifieri l-valuri permessi tagħha jinkludu l-valuri speċifikati fir-reġistru u valuri addizzjonali f''kull livell definiti mill-fornituri tad-dejta</p>', NULL, NULL, NULL, '2019-07-30 14:33:39.808', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('a2f26c4c902dbf9f6461e91af261de09', 'nl', 'aad13ff0ff9fb151cb8410801e4eeb0a', 'FIELD_LABEL_UUID', 0, 'Uitbreidbaar met waarden op elk niveau', NULL, NULL, NULL, '2019-07-30 14:33:39.811', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('8a168f48d0230c95550c4858c7465c8f', 'nl', 'aad13ff0ff9fb151cb8410801e4eeb0a', 'FIELD_DEFINITION_UUID', 0, '<p>De codelijst kan uitgebreid worden met extra additionele waarden op alle niveaus, met andere woorden de toegestane waarden zijn deze gespecifieerd in dit register plus extra additionele waarden op om het even welk niveau gedefinieerd door gegevens beheerders.</p>', NULL, NULL, NULL, '2019-07-30 14:33:39.814', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('2bda61b7b139b90965127c5c097e8ef8', 'pl', 'aad13ff0ff9fb151cb8410801e4eeb0a', 'FIELD_LABEL_UUID', 0, 'Rozszerzalny z wartościami na każdym poziomie', NULL, NULL, NULL, '2019-07-30 14:33:39.816', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('72a9bb81fb3bc1d2b9ae553f87197533', 'pl', 'aad13ff0ff9fb151cb8410801e4eeb0a', 'FIELD_DEFINITION_UUID', 0, '<p>Lista kodów może zostać rozszerzona dodatkowymi wartościami na każdym poziomie, to znaczy, że dozwolone wartości zawierają wartości określone w tym wykazie oraz dodatkowe wartości na każdym poziomie zdefiniowane przez dostawców danych.</p>', NULL, NULL, NULL, '2019-07-30 14:33:39.819', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('2e2ada23dda76a17e65350a92f4ba12c', 'pt', 'aad13ff0ff9fb151cb8410801e4eeb0a', 'FIELD_LABEL_UUID', 0, 'Extensível com valores de qualquer nível', NULL, NULL, NULL, '2019-07-30 14:33:39.822', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('4c6518c40b96aa1e53de1cee83e92983', 'pt', 'aad13ff0ff9fb151cb8410801e4eeb0a', 'FIELD_DEFINITION_UUID', 0, '<p>A lista de códigos pode ser alargada com valores adicionais de qualquer nível, ou seja, são permitidos os valores que compreendem os valores especificados neste registo e os valores adicionais de qualquer nível definido por fornecedores de dados.</p>', NULL, NULL, NULL, '2019-07-30 14:33:39.825', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('52a1b52efe3f8e0dbd54c525d06be051', 'ro', 'aad13ff0ff9fb151cb8410801e4eeb0a', 'FIELD_LABEL_UUID', 0, 'Extensibilă cu valori de la orice nivel', NULL, NULL, NULL, '2019-07-30 14:33:39.828', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('3670c3c02152e1170d8943507cf89f12', 'ro', 'aad13ff0ff9fb151cb8410801e4eeb0a', 'FIELD_DEFINITION_UUID', 0, '<p>Lista de coduri poate fi extinsă cu lavori suplimentare la orice nivel, adică valorile sale permise cuprind valorile specificate în acest registru și valori suplimentare la orice nivel definite de furnizorii de date.</p>', NULL, NULL, NULL, '2019-07-30 14:33:39.83', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('146174cce3c4133cba9a4fd4f8b696ee', 'sk', 'aad13ff0ff9fb151cb8410801e4eeb0a', 'FIELD_LABEL_UUID', 0, 'Rozšíriteľný s hodnotami na akejkoľvek úrovni', NULL, NULL, NULL, '2019-07-30 14:33:39.833', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('b9f4d1a8b0d1c2f438203e9c48afbdb3', 'sk', 'aad13ff0ff9fb151cb8410801e4eeb0a', 'FIELD_DEFINITION_UUID', 0, '<p>Zoznam kódov (číselník) je možné rozšíriť o ďalšie hodnoty na akejkoľvek úrovni, t.j. jeho povolené hodnoty zahŕňajú hodnoty uvedené v tomto registri a ďalšie hodnoty, na akejkoľvek úrovni definovanej poskytovateľom údajov.</p>', NULL, NULL, NULL, '2019-07-30 14:33:39.835', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('a61d766296941f893e7a25d689b90002', 'sl', 'aad13ff0ff9fb151cb8410801e4eeb0a', 'FIELD_LABEL_UUID', 0, 'Dopolnjevanje je mogoče z vrednostmi vseh nivojev', NULL, NULL, NULL, '2019-07-30 14:33:39.838', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('d0a6543333e659d3e392774349d6546f', 'sl', 'aad13ff0ff9fb151cb8410801e4eeb0a', 'FIELD_DEFINITION_UUID', 0, '<p>Dopolnjevanje seznama oznak je mogoče z vrednostmi vseh nivojev, t.j. dovoljene vrednosti so tiste navedene v registru in vrednosti vseh nivojev, ki jih opredeli ponudnik podatkov.</p>', NULL, NULL, NULL, '2019-07-30 14:33:39.841', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('b45e07ccb68cea45934e4d67f36fe0da', 'fi', 'aad13ff0ff9fb151cb8410801e4eeb0a', 'FIELD_LABEL_UUID', 0, 'Laajennettavissa kaikilla tasoilla', NULL, NULL, NULL, '2019-07-30 14:33:39.844', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('cd8f5aaef3c91496600f54148b87fad0', 'fi', 'aad13ff0ff9fb151cb8410801e4eeb0a', 'FIELD_DEFINITION_UUID', 0, '<p>Koodiluetteloa voidaan laajentaa minkä hyvänsä tason koodiarvoilla. Koodiluettelossa siis sallitaan tässä rekisterissä määritellyt arvot ja lisäksi tiedontuottajien määrittelemät minkä hyvänsä tason arvot.</p>', NULL, NULL, NULL, '2019-07-30 14:33:39.847', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('97a7822e83b34c2b1ba455d16b42ba91', 'sv', 'aad13ff0ff9fb151cb8410801e4eeb0a', 'FIELD_LABEL_UUID', 0, 'Utökningbar med värden oavsett nivå', NULL, NULL, NULL, '2019-07-30 14:33:39.85', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('d3635f6f4f33475871820baa93d6ac95', 'sv', 'aad13ff0ff9fb151cb8410801e4eeb0a', 'FIELD_DEFINITION_UUID', 0, '<p>Kodlistan kan utökas med ytterligare värden oavsett nivå, dvs. värden kan vara inom värdena tillåtna i detta register och ytterligare värden oavsett nivå definierade av dataleverantörerna</p>', NULL, NULL, NULL, '2019-07-30 14:33:39.852', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('2882c3b11a46b35ff2dfe442b2dca405', 'en', '31082311814c08a2ae0e5e647669c73f', 'FIELD_LABEL_UUID', 0, 'Not extensible', NULL, NULL, NULL, '2019-07-30 10:46:04.229', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('a9c5b6993903ee53302c1099cdb0cd12', 'en', '31082311814c08a2ae0e5e647669c73f', 'FIELD_DEFINITION_UUID', 0, '<p>The code list cannot be extended, i.e. its allowed values comprise only the values specified in this register.</p>', NULL, NULL, NULL, '2019-07-30 10:46:04.232', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('19f94f75b47b1338106478199ea469e6', 'bg', '31082311814c08a2ae0e5e647669c73f', 'FIELD_LABEL_UUID', 0, 'Неразширяем', NULL, NULL, NULL, '2019-07-30 14:33:47.798', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('8b93daa5b9652fdb13219aced95d0708', 'bg', '31082311814c08a2ae0e5e647669c73f', 'FIELD_DEFINITION_UUID', 0, '<p>Списъкът на кодовете не може да бъде разширяван, т.e. неговите разрешени стойности включват само стойностите, посочени в този регистър.</p>', NULL, NULL, NULL, '2019-07-30 14:33:47.801', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('3c9ac7f2ceecfb7f5933f387a7a5b119', 'cs', '31082311814c08a2ae0e5e647669c73f', 'FIELD_LABEL_UUID', 0, 'Nerozšiřitelný', NULL, NULL, NULL, '2019-07-30 14:33:47.804', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('4501bb57362d11ce25505f50612240db', 'cs', '31082311814c08a2ae0e5e647669c73f', 'FIELD_DEFINITION_UUID', 0, '<p>Číselník nemůže být rozšířen, tj. povolené hodnoty zahrnují pouze hodnoty specifikované v registru.</p>', NULL, NULL, NULL, '2019-07-30 14:33:47.807', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('82eacab03a303f16fd576b56c08cf9c1', 'da', '31082311814c08a2ae0e5e647669c73f', 'FIELD_LABEL_UUID', 0, 'Kan ikke udvides', NULL, NULL, NULL, '2019-07-30 14:33:47.809', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('a0f99e1950c8fe2ecee8d82c775e0521', 'da', '31082311814c08a2ae0e5e647669c73f', 'FIELD_DEFINITION_UUID', 0, '<p>Kodelisten kan ikke udvides, dvs. tilladte værdier udgøres alene af værdier i dette register.</p>', NULL, NULL, NULL, '2019-07-30 14:33:47.812', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('22683dff64920f55a0a9147cb8e615d7', 'de', '31082311814c08a2ae0e5e647669c73f', 'FIELD_LABEL_UUID', 0, 'Nicht erweiterbar', NULL, NULL, NULL, '2019-07-30 14:33:47.814', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('c922cab709e74be3ab4b4ef68d883922', 'de', '31082311814c08a2ae0e5e647669c73f', 'FIELD_DEFINITION_UUID', 0, '<p>Die Codeliste darf nicht erweitert werden, d.h. ihre erlaubten Werte umfassen nur die Werte, die in diesem Register enthalten sind.</p>', NULL, NULL, NULL, '2019-07-30 14:33:47.816', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('bec8bdbaffaddc1ac470d409891081c3', 'et', '31082311814c08a2ae0e5e647669c73f', 'FIELD_LABEL_UUID', 0, 'Mittelaiendatav', NULL, NULL, NULL, '2019-07-30 14:33:47.819', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('23850e32954da7b2b5cd3eb30fbad198', 'et', '31082311814c08a2ae0e5e647669c73f', 'FIELD_DEFINITION_UUID', 0, '<p>Koodiloendit ei saa laiendada, st tema lubatavad väärtused saavad olla ainult sellised, mis on määratud selles registris.</p>', NULL, NULL, NULL, '2019-07-30 14:33:47.822', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('15cd0617e83fa3720e2c60c9d60aa487', 'el', '31082311814c08a2ae0e5e647669c73f', 'FIELD_LABEL_UUID', 0, 'μη επεκτάσιμο', NULL, NULL, NULL, '2019-07-30 14:33:47.824', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('6fd719cf0f7563f720335bd4efd46d12', 'el', '31082311814c08a2ae0e5e647669c73f', 'FIELD_DEFINITION_UUID', 0, '<p>Αυτός ο κωδικός λίστας δεν μπορεί να επεκταθεί, οι επιτρεπόμενες τιμές του αποτελούν τις τιμές που αναγράφονται στον παρόν καταχωρητής</p>', NULL, NULL, NULL, '2019-07-30 14:33:47.827', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('3c7c42d66b04bb72cd184b53c9eea7de', 'es', '31082311814c08a2ae0e5e647669c73f', 'FIELD_LABEL_UUID', 0, 'No extensible', NULL, NULL, NULL, '2019-07-30 14:33:47.829', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('4c1b1a7e7da8d4cec0c1fbf484b459a7', 'es', '31082311814c08a2ae0e5e647669c73f', 'FIELD_DEFINITION_UUID', 0, '<p>La lista de códigos no se puede extender; es decir, los valores permitidos son únicamente los valores especificados en el registro</p>', NULL, NULL, NULL, '2019-07-30 14:33:47.832', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('ddee8f853fd39b4dc2670c950e959d21', 'fr', '31082311814c08a2ae0e5e647669c73f', 'FIELD_LABEL_UUID', 0, 'Non extensible', NULL, NULL, NULL, '2019-07-30 14:33:47.836', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('a230591ad584f96be9d667f6599e3dbf', 'fr', '31082311814c08a2ae0e5e647669c73f', 'FIELD_DEFINITION_UUID', 0, '<p>La liste de codes ne peut pas être étendue: seuls les codes existants sont autorisés.</p>', NULL, NULL, NULL, '2019-07-30 14:33:47.839', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('6f98438795d52f65f2085317586d6870', 'hr', '31082311814c08a2ae0e5e647669c73f', 'FIELD_LABEL_UUID', 0, 'Nije proširivo', NULL, NULL, NULL, '2019-07-30 14:33:47.841', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('96cd193e555c8c52c112c664f1fc7018', 'hr', '31082311814c08a2ae0e5e647669c73f', 'FIELD_DEFINITION_UUID', 0, '<p>Kodna lista nije proširiva tj. dozvoljene su samo vrijednosti već specificirane u registru.</p>', NULL, NULL, NULL, '2019-07-30 14:33:47.844', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('f2868836f1489193892e42fcb177dfde', 'it', '31082311814c08a2ae0e5e647669c73f', 'FIELD_LABEL_UUID', 0, 'Non estendibile', NULL, NULL, NULL, '2019-07-30 14:33:47.846', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('dade25b2d63869c730b58e1448b51217', 'it', '31082311814c08a2ae0e5e647669c73f', 'FIELD_DEFINITION_UUID', 0, '<p>L''elenco di codici non può essere esteso, ossia i suoi valori consentiti comprendono solo i valori indicati in questo registro.</p>', NULL, NULL, NULL, '2019-07-30 14:33:47.849', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('abdfded27d4f3107934f0ded9a003458', 'lv', '31082311814c08a2ae0e5e647669c73f', 'FIELD_LABEL_UUID', 0, 'Nav paplašināms', NULL, NULL, NULL, '2019-07-30 14:33:47.851', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('5c8d065d5e6fb9c031a15fea151e05e4', 'lv', '31082311814c08a2ae0e5e647669c73f', 'FIELD_DEFINITION_UUID', 0, '<p>Kodu sarakstu nevar paplašināt, t.i., atļautās vērtības aptver tikai šajā reģistrā iekļautās vērtības.</p>', NULL, NULL, NULL, '2019-07-30 14:33:47.855', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('f7d740cbda36f5868a49aca833c51a6a', 'lt', '31082311814c08a2ae0e5e647669c73f', 'FIELD_LABEL_UUID', 0, 'Neplečiamas', NULL, NULL, NULL, '2019-07-30 14:33:47.858', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('fe31278bf26f848e4a736a6c9c934e50', 'lt', '31082311814c08a2ae0e5e647669c73f', 'FIELD_DEFINITION_UUID', 0, '<p>Kodų sąrašas negali būti papildytas, t.y. visos leidžiamos reikšmės jau nurodytos šiame registre.</p>', NULL, NULL, NULL, '2019-07-30 14:33:47.86', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('b8cf703ff76e5d8528fdf9c289836c9a', 'hu', '31082311814c08a2ae0e5e647669c73f', 'FIELD_LABEL_UUID', 0, 'Nem bővíthető', NULL, NULL, NULL, '2019-07-30 14:33:47.863', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('e4c1d272ee6c102258f5cc3f0a15b68c', 'hu', '31082311814c08a2ae0e5e647669c73f', 'FIELD_DEFINITION_UUID', 0, '<p>A kódlista nem bővíthető, vagyis a változók csak az itt szereplő értékeket vehetik fel.</p>', NULL, NULL, NULL, '2019-07-30 14:33:47.866', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('ec2414ee59b84370678836290a254f21', 'mt', '31082311814c08a2ae0e5e647669c73f', 'FIELD_LABEL_UUID', 0, 'Mhux estensibbli', NULL, NULL, NULL, '2019-07-30 14:33:47.87', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('f9a210c5002aee7d4ffecaf71de9e7cb', 'mt', '31082311814c08a2ae0e5e647669c73f', 'FIELD_DEFINITION_UUID', 0, '<p>Il-lista tal-kodiċi ma tistax tiġi estiża, jiġifieri l-valuri permessi tagħha jinkludu biss il-valuri speċifikati f''dan ir-reġistru.</p>', NULL, NULL, NULL, '2019-07-30 14:33:47.872', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('fc000090126700c38af8dbe59896f7e2', 'nl', '31082311814c08a2ae0e5e647669c73f', 'FIELD_LABEL_UUID', 0, 'Niet uitbreidbaar', NULL, NULL, NULL, '2019-07-30 14:33:47.875', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('d6f9ccea5406b89d352e22baca1b596b', 'nl', '31082311814c08a2ae0e5e647669c73f', 'FIELD_DEFINITION_UUID', 0, '<p>De codelijst kan niet worden uitgebreid, met andere woorden de toegestane waarden zijn beperkt tot de waarden in dit register.</p>', NULL, NULL, NULL, '2019-07-30 14:33:47.879', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('11954c186c2d318d73b5494c1be2f744', 'pl', '31082311814c08a2ae0e5e647669c73f', 'FIELD_LABEL_UUID', 0, 'Nierozszerzalny', NULL, NULL, NULL, '2019-07-30 14:33:47.881', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('49e692e17b9a4f1e0329dcb2ce3822b0', 'pl', '31082311814c08a2ae0e5e647669c73f', 'FIELD_DEFINITION_UUID', 0, '<p>Lista kodów nie może być rozszerzona, to znaczy, że dopuszczalne wartości zawierają tylko wartości określone w tym wykazie.</p>', NULL, NULL, NULL, '2019-07-30 14:33:47.885', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('b036deb6283f8bd4e4c5f995770e7f3a', 'pt', '31082311814c08a2ae0e5e647669c73f', 'FIELD_LABEL_UUID', 0, 'Não extensível', NULL, NULL, NULL, '2019-07-30 14:33:47.888', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('44742a895e420cf42b062120e643d298', 'pt', '31082311814c08a2ae0e5e647669c73f', 'FIELD_DEFINITION_UUID', 0, '<p>A lista de códigos não pode ser alargada, ou seja, os valores permitidos compreendem apenas os valores especificados deste registo</p>', NULL, NULL, NULL, '2019-07-30 14:33:47.891', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('30fdd3b48e64288d3233a64d4a30fd8d', 'ro', '31082311814c08a2ae0e5e647669c73f', 'FIELD_LABEL_UUID', 0, 'Nu este extensibilă', NULL, NULL, NULL, '2019-07-30 14:33:47.894', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('0b8ad4e5116f97ac18b1887759c4c131', 'ro', '31082311814c08a2ae0e5e647669c73f', 'FIELD_DEFINITION_UUID', 0, '<p>Lista de coduri nu poate fi extinsă, adică valorile sale permise cuprind numai valorile specificate în acest registru.</p>', NULL, NULL, NULL, '2019-07-30 14:33:47.897', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('d67afd0baac8af8d04cd9ebdb8ed6299', 'sk', '31082311814c08a2ae0e5e647669c73f', 'FIELD_LABEL_UUID', 0, 'Nerozšíriteľný', NULL, NULL, NULL, '2019-07-30 14:33:47.901', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('fccf95be67ee3f8ce8a01488d204ffdb', 'sk', '31082311814c08a2ae0e5e647669c73f', 'FIELD_DEFINITION_UUID', 0, '<p>Zoznam kódov (číselník) nemožno rozšíriť, t.j. jeho povolené hodnoty zahŕňajú iba hodnoty uvedené v tomto registri.</p>', NULL, NULL, NULL, '2019-07-30 14:33:47.903', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('1b1fd27cfc55b4d563d26d5366f8d531', 'sl', '31082311814c08a2ae0e5e647669c73f', 'FIELD_LABEL_UUID', 0, 'Ni mogoče dopolnjevati', NULL, NULL, NULL, '2019-07-30 14:33:47.906', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('341eae03dcf0692c458c13128cf0daa9', 'sl', '31082311814c08a2ae0e5e647669c73f', 'FIELD_DEFINITION_UUID', 0, '<p>Seznama oznak ni mogoče dopolnjevati, t.j. dovoljene vrednosti so le tiste navedne v registru.</p>', NULL, NULL, NULL, '2019-07-30 14:33:47.909', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('902ed203157add38a4b9710628409fbd', 'fi', '31082311814c08a2ae0e5e647669c73f', 'FIELD_LABEL_UUID', 0, 'Ei laajennettavissa', NULL, NULL, NULL, '2019-07-30 14:33:47.912', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('7d90a1607cf2a49433be91857c4ee0a6', 'fi', '31082311814c08a2ae0e5e647669c73f', 'FIELD_DEFINITION_UUID', 0, '<p>Koodiluetteloa ei voi laajentaa. Koodiluettelossa siis sallitaan ainoastaan tässä rekisterissä määritellyt arvot.</p>', NULL, NULL, NULL, '2019-07-30 14:33:47.914', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('c576695c073fc4b5e07e402a512c2e4c', 'sv', '31082311814c08a2ae0e5e647669c73f', 'FIELD_LABEL_UUID', 0, 'Ej utökningsbar', NULL, NULL, NULL, '2019-07-30 14:33:47.917', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('9654d18049d837e9158cee79808426e7', 'sv', '31082311814c08a2ae0e5e647669c73f', 'FIELD_DEFINITION_UUID', 0, '<p>Kodlistan kan inte utökas, dvs. dess tillåtna värden inkluderar bara de värden som är specificierade i denna lista.</p>', NULL, NULL, NULL, '2019-07-30 14:33:47.92', NULL);";

    private final String QUERY_EXTENSIBILITY_1
            = "INSERT INTO public.reg_localization VALUES ('dadcaab8a416afde92ec52e1b10efbd8', 'en', '088f861f19d356685c0da1916fbb260a', 'FIELD_LABEL_UUID', 0, 'Empty code list', NULL, NULL, NULL, '2019-07-30 10:45:49.895', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('3c3b087f514ccd5b1f83a6d3fe5b7770', 'en', '088f861f19d356685c0da1916fbb260a', 'FIELD_DEFINITION_UUID', 0, '<p>No values are specified for this code list in this register, i.e. its allowed values comprise any values defined by data providers.</p>', NULL, NULL, NULL, '2019-07-30 10:45:49.9', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('40ef168c0b003cdedc758193c71cb716', 'bg', '088f861f19d356685c0da1916fbb260a', 'FIELD_LABEL_UUID', 0, 'Празен списък на кодове', NULL, NULL, NULL, '2019-07-30 14:34:10', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('b35647f0e49c3509094db5f73e2b65b9', 'bg', '088f861f19d356685c0da1916fbb260a', 'FIELD_DEFINITION_UUID', 0, '<p>За този списък на кодове в този регистър не са посочени никакви стойности, т.e. неговите допустими стойности обхващат всякакви стойности, дефинирани от доставчиците на данни</p>', NULL, NULL, NULL, '2019-07-30 14:34:10.002', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('b91cd7a0d21079abe0d2482537b2e74e', 'cs', '088f861f19d356685c0da1916fbb260a', 'FIELD_LABEL_UUID', 0, 'Prázdný číselník', NULL, NULL, NULL, '2019-07-30 14:34:10.005', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('fd8307fdd622ca56db9f0ac0d75071a6', 'cs', '088f861f19d356685c0da1916fbb260a', 'FIELD_DEFINITION_UUID', 0, '<p>Pro tento číselník nebyly definovány žádné hodnoty, tj. povolené hodnoty budou zahrnovat hodnoty definované poskytovatelem dat.</p>', NULL, NULL, NULL, '2019-07-30 14:34:10.007', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('64fc41f43cc95f286987fedd73ebe76d', 'da', '088f861f19d356685c0da1916fbb260a', 'FIELD_LABEL_UUID', 0, 'Tom kodeliste', NULL, NULL, NULL, '2019-07-30 14:34:10.01', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('e3ddf1dce47834f49224a0af159e5b08', 'da', '088f861f19d356685c0da1916fbb260a', 'FIELD_DEFINITION_UUID', 0, '<p>Ingen værdier er specificeret for denne kodeliste i dette register, dvs. tilladte værdier udgøres af alle værdier defineret af dataleverandører.</p>', NULL, NULL, NULL, '2019-07-30 14:34:10.012', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('a3519a25500797957895c1026dd03d19', 'de', '088f861f19d356685c0da1916fbb260a', 'FIELD_LABEL_UUID', 0, 'Leere Codeliste', NULL, NULL, NULL, '2019-07-30 14:34:10.014', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('63505bb8fa7fc6a46c000086ce0f6422', 'de', '088f861f19d356685c0da1916fbb260a', 'FIELD_DEFINITION_UUID', 0, '<p>Für die Codeliste sind in diesem Register keine Werte spezifiziert, d.h. ihre erlaubten Werte umfassen beliebige von Datenanbietern definierte Werte.</p>', NULL, NULL, NULL, '2019-07-30 14:34:10.016', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('1b570544e95cd96bc083c22e7214f857', 'et', '088f861f19d356685c0da1916fbb260a', 'FIELD_LABEL_UUID', 0, 'Tühi koodiloend', NULL, NULL, NULL, '2019-07-30 14:34:10.019', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('68da0415cce66f85f4009bbfa230ab95', 'et', '088f861f19d356685c0da1916fbb260a', 'FIELD_DEFINITION_UUID', 0, '<p>Selle registri koodiloendis ei ole määratletud väärtusi, st selle lubatud väärtused hõlmavad andmete tarnijate poolt määratletud suvalisi väärtusi.</p>', NULL, NULL, NULL, '2019-07-30 14:34:10.022', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('c597d7be889c439e4ad5a794adc51556', 'el', '088f861f19d356685c0da1916fbb260a', 'FIELD_LABEL_UUID', 0, 'οποιοδήποτε (κενός κωδικός λίστας)', NULL, NULL, NULL, '2019-07-30 14:34:10.024', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('3c49ad6817a227bd6ea5461881fa3c15', 'el', '088f861f19d356685c0da1916fbb260a', 'FIELD_DEFINITION_UUID', 0, '<p>Δεν ορίζονται τιμές για αυτόν τον κωδικό λίστας σε αυτό το καταχωρητή, οι επιτρεπόμενες τιμές αποτελούνται από τιμές όπως ορίζονται από παρόχους δεδομένων</p>', NULL, NULL, NULL, '2019-07-30 14:34:10.027', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('9dc7d303fed4d3685a244baac946f237', 'es', '088f861f19d356685c0da1916fbb260a', 'FIELD_LABEL_UUID', 0, 'Lista de códigos vacía', NULL, NULL, NULL, '2019-07-30 14:34:10.03', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('a8347b39bb1eab879af12ded0030f00c', 'es', '088f861f19d356685c0da1916fbb260a', 'FIELD_DEFINITION_UUID', 0, '<p>No se ha especificado ningún valor para esta lista de códigos en el registro; es decir. los valores permitidos incluyen cualquier valor especificado por el proveedor de datos</p>', NULL, NULL, NULL, '2019-07-30 14:34:10.032', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('ea8a942fed6c9cb6c931255679746ba0', 'fr', '088f861f19d356685c0da1916fbb260a', 'FIELD_LABEL_UUID', 0, 'Liste de codes vide', NULL, NULL, NULL, '2019-07-30 14:34:10.034', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('a01e8a742a45e967f28970c5e1dd9814', 'fr', '088f861f19d356685c0da1916fbb260a', 'FIELD_DEFINITION_UUID', 0, '<p>Aucune valeur n''est définie dans ce registre: toute valeur définie par un fournisseur de données est autorisé.</p>', NULL, NULL, NULL, '2019-07-30 14:34:10.037', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('add0a2c95445547a9a487b5acb969b92', 'hr', '088f861f19d356685c0da1916fbb260a', 'FIELD_LABEL_UUID', 0, 'Prazna kodna lista', NULL, NULL, NULL, '2019-07-30 14:34:10.04', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('d5d8a341f5bbd1c150e8c82eb944b1d6', 'hr', '088f861f19d356685c0da1916fbb260a', 'FIELD_DEFINITION_UUID', 0, '<p>Vrijednosti za ovu kodnu listu nisu specificirane u registru tj. dozvoljene su bilo koje vrijednosti definirane od strane pružatelja podataka</p>', NULL, NULL, NULL, '2019-07-30 14:34:10.043', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('81e5af7ea5e5a354022aeb566a8d932f', 'it', '088f861f19d356685c0da1916fbb260a', 'FIELD_LABEL_UUID', 0, 'Lista dei codici vuota', NULL, NULL, NULL, '2019-07-30 14:34:10.046', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('47cd056fe283daf04e4b0e13c57e1ffb', 'it', '088f861f19d356685c0da1916fbb260a', 'FIELD_DEFINITION_UUID', 0, '<p>Non sono specificati valori per tale lista di codici in questo registro, ossia i suoi valori consentiti comprendono i valori definiti dal provider di dati.</p>', NULL, NULL, NULL, '2019-07-30 14:34:10.048', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('646460c83fdeabfe86fdf4b03177333a', 'lv', '088f861f19d356685c0da1916fbb260a', 'FIELD_LABEL_UUID', 0, 'Tukšs kodu saraksts', NULL, NULL, NULL, '2019-07-30 14:34:10.051', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('ccc1366aec34f497c7671ccbcbd83cdc', 'lv', '088f861f19d356685c0da1916fbb260a', 'FIELD_DEFINITION_UUID', 0, '<p>Šim kodu sarakstam reģistrā nav norādīta neviena vērtība, proti, atļautās vērtības aptver jebkuru datu sniedzēja definēto vērtību.</p>', NULL, NULL, NULL, '2019-07-30 14:34:10.054', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('f30e1ef9550587784ab60f0ff1871fce', 'lt', '088f861f19d356685c0da1916fbb260a', 'FIELD_LABEL_UUID', 0, 'Tuščias kodų sąrašas', NULL, NULL, NULL, '2019-07-30 14:34:10.056', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('0cbac4b932c77eadbb13047c3919f4b0', 'lt', '088f861f19d356685c0da1916fbb260a', 'FIELD_DEFINITION_UUID', 0, '<p>Kodų sąrašas šiame registre neturi apibrėžtų reikšmių; leidžiamos bet kokios duomenų teikėjo nurodytos reikšmės.</p>', NULL, NULL, NULL, '2019-07-30 14:34:10.059', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('39614b4fd78b73003e42a0d68c373970', 'hu', '088f861f19d356685c0da1916fbb260a', 'FIELD_LABEL_UUID', 0, 'Üres kódlista', NULL, NULL, NULL, '2019-07-30 14:34:10.061', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('5d214a6a93bbd01be7e85d22057e318f', 'hu', '088f861f19d356685c0da1916fbb260a', 'FIELD_DEFINITION_UUID', 0, '<p>Ez a nyilvántartás nem határoz meg értékeket. Az adatszolgáltatók bármilyen értékeket hozzárendelhetnek.</p>', NULL, NULL, NULL, '2019-07-30 14:34:10.064', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('7a0ddb6ed156cf3b0fd2a2a80fae3979', 'mt', '088f861f19d356685c0da1916fbb260a', 'FIELD_LABEL_UUID', 0, 'Lista tal-kodiċi vojta', NULL, NULL, NULL, '2019-07-30 14:34:10.066', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('6023b071c443e5c5ad606ae5aef3c1bc', 'mt', '088f861f19d356685c0da1916fbb260a', 'FIELD_DEFINITION_UUID', 0, '<p>L-ebda valuri m'' huma speċifikati għal din il-lista ta'' kodiċi f''dan ir-reġistru, jiġifieri l-valuri permessi tagħha jinkludu kull valur definit mill-fornituri tad-dejta</p>', NULL, NULL, NULL, '2019-07-30 14:34:10.069', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('a9a08fcfeaaed52d9a3677812637e135', 'nl', '088f861f19d356685c0da1916fbb260a', 'FIELD_LABEL_UUID', 0, 'Lege codelijst', NULL, NULL, NULL, '2019-07-30 14:34:10.072', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('459201ac1b9dde97dde900f3b7919ce3', 'nl', '088f861f19d356685c0da1916fbb260a', 'FIELD_DEFINITION_UUID', 0, '<p>Er zijn geen waarden gespecifieerd voor de codelijst in dit register,met andere woorden alle waarden gedefinieerd door gegevensbeheerders zijn toegestaan.</p>', NULL, NULL, NULL, '2019-07-30 14:34:10.075', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('cc507eda3145c3d5dadb824a8f40ae37', 'pl', '088f861f19d356685c0da1916fbb260a', 'FIELD_LABEL_UUID', 0, 'Pusta lista kodów', NULL, NULL, NULL, '2019-07-30 14:34:10.077', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('54ed512665fee90b556a5825835a1a6b', 'pl', '088f861f19d356685c0da1916fbb260a', 'FIELD_DEFINITION_UUID', 0, '<p>Nie określono wartości dla tej listy kodów w tym rejestrze, to znaczy, że dozwolone wartości zawierają każde wartości zdefiniowane przez dostawców danych.</p>', NULL, NULL, NULL, '2019-07-30 14:34:10.08', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('c0a3c9f7f3586f93406de9335d77c06f', 'pt', '088f861f19d356685c0da1916fbb260a', 'FIELD_LABEL_UUID', 0, 'Lista de códigos vazia', NULL, NULL, NULL, '2019-07-30 14:34:10.083', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('694c2ab7f2bfd1cad1d528b198fa9f23', 'pt', '088f861f19d356685c0da1916fbb260a', 'FIELD_DEFINITION_UUID', 0, '<p>Não há valores especificados para esta lista de códigos deste registo, ou seja, os valores permitidos compreendem todos os valores definidos por provedores de dados</p>', NULL, NULL, NULL, '2019-07-30 14:34:10.084', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('e88cc8388e9d0a4832d9eec4c26c0c0f', 'ro', '088f861f19d356685c0da1916fbb260a', 'FIELD_LABEL_UUID', 0, 'Listă de coduri fără elemente', NULL, NULL, NULL, '2019-07-30 14:34:10.087', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('11349cd60e6ddf48ad408b7ab6696252', 'ro', '088f861f19d356685c0da1916fbb260a', 'FIELD_DEFINITION_UUID', 0, '<p>Nicio valoare nu este specificată pentru această listă de coduri în acest registru, adică cuprinde orice valoare definită de furnizorii de date.</p>', NULL, NULL, NULL, '2019-07-30 14:34:10.09', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('638ea9485081607dc41d6363cb9490bb', 'sk', '088f861f19d356685c0da1916fbb260a', 'FIELD_LABEL_UUID', 0, 'Prázdny zoznam kódov', NULL, NULL, NULL, '2019-07-30 14:34:10.093', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('a53294b44596a68c39f66110b2a0d254', 'sk', '088f861f19d356685c0da1916fbb260a', 'FIELD_DEFINITION_UUID', 0, '<p>Pre tento zoznam kódov (číselník) nie sústanovené žiadne hodnoty, t.j. jeho povolené hodnoty zohľadňujú akékoľvek hodnoty definované poskytovateˇmi údajov.</p>', NULL, NULL, NULL, '2019-07-30 14:34:10.096', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('728f1066e1a3350f025d04b2d92a9ef5', 'sl', '088f861f19d356685c0da1916fbb260a', 'FIELD_LABEL_UUID', 0, 'Prazen seznam oznak', NULL, NULL, NULL, '2019-07-30 14:34:10.098', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('45137c0ffad5f2d1b8cf41533fdb6158', 'sl', '088f861f19d356685c0da1916fbb260a', 'FIELD_DEFINITION_UUID', 0, '<p>Vrednost za pričujoči seznam oznak tega registre ni opredeljena, t.j. dovoljene vrednosti so vse tiste, ki jih opredeli ponudnik podatkov.</p>', NULL, NULL, NULL, '2019-07-30 14:34:10.101', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('53a3dc3922a900aadc603c2824e97062', 'fi', '088f861f19d356685c0da1916fbb260a', 'FIELD_LABEL_UUID', 0, 'Tyhjä koodiluettelo', NULL, NULL, NULL, '2019-07-30 14:34:10.104', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('bdbd12dac004efe165461174638c49e1', 'fi', '088f861f19d356685c0da1916fbb260a', 'FIELD_DEFINITION_UUID', 0, '<p>Tälle koodiluettelolle ei ole määritelty koodiarvoja tässä rekisterissä, joten luettelo muodostuu tiedontuottajien määrittelemistä koodiarvoista.</p>', NULL, NULL, NULL, '2019-07-30 14:34:10.107', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('f6a96672bfaa26561777fd2c11dee25d', 'sv', '088f861f19d356685c0da1916fbb260a', 'FIELD_LABEL_UUID', 0, 'Tom kodlista', NULL, NULL, NULL, '2019-07-30 14:34:10.109', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('381dcc1ad650297b4fc65a79fb0de084', 'sv', '088f861f19d356685c0da1916fbb260a', 'FIELD_DEFINITION_UUID', 0, '<p>Inga värden är specificierade för denna kodlista i detta register. dvs. tillåtna värden är alla värden definierade av dataleverantörerna</p>', NULL, NULL, NULL, '2019-07-30 14:34:10.113', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('c764fa226b511ca3692e5ee24e13874b', 'en', '91e00e7f8d73c54fa2f80c67d3d9d5b0', 'FIELD_LABEL_UUID', 0, 'Extensible with narrower values', NULL, NULL, NULL, '2019-07-30 10:41:43.884', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('86f3b5adcd0d541ed51afb7baec63c77', 'en', '91e00e7f8d73c54fa2f80c67d3d9d5b0', 'FIELD_DEFINITION_UUID', 0, '<p>The code list can only be extended with narrower values, i.e. its allowed values comprise the values specified in this register and narrower values defined by data providers.</p>', NULL, NULL, NULL, '2019-07-30 10:41:43.902', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('5cd387613b918de85f78c16941d6739e', 'bg', '91e00e7f8d73c54fa2f80c67d3d9d5b0', 'FIELD_LABEL_UUID', 0, 'Списък на кодове, позволяващ разширяване със стойности от по-долно ниво в йерархията', NULL, NULL, NULL, '2019-07-30 14:35:08.182', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('08678abd2653c801272966b87f6eb503', 'bg', '91e00e7f8d73c54fa2f80c67d3d9d5b0', 'FIELD_DEFINITION_UUID', 0, '<p>Списъкът на кодовете може да бъде разширяван със стойности от по-долно ниво в йерархията, т.е. допустимите стойности обхващат стойности, посочени в този регистър и стойности от по-долно ниво в йерархията, дефинирани от доставчиците на данни.</p>', NULL, NULL, NULL, '2019-07-30 14:35:08.185', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('23ca5c9c9f9349b1d97f0ae817ce5d5d', 'cs', '91e00e7f8d73c54fa2f80c67d3d9d5b0', 'FIELD_LABEL_UUID', 0, 'Rozšiřitelný s použitím užšího oboru hodnot', NULL, NULL, NULL, '2019-07-30 14:35:08.188', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('e44aef47a1bf6c1507a29be560cd5b86', 'cs', '91e00e7f8d73c54fa2f80c67d3d9d5b0', 'FIELD_DEFINITION_UUID', 0, '<p>Číselník může být rozšířen pouze o užší obor hodnot, tj. povolené hodnoty zahrnují hodnoty specifikované v registru a užší obor hodnot definovaný poskytovatelem dat.</p>', NULL, NULL, NULL, '2019-07-30 14:35:08.19', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('b715ef1e3ea25b5d5f3e60af32afee23', 'da', '91e00e7f8d73c54fa2f80c67d3d9d5b0', 'FIELD_LABEL_UUID', 0, 'Kan udvides med underliggende værdier', NULL, NULL, NULL, '2019-07-30 14:35:08.193', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('c9d7d330de270e5f4eebfaad98752852', 'da', '91e00e7f8d73c54fa2f80c67d3d9d5b0', 'FIELD_DEFINITION_UUID', 0, '<p>Kodelisten kan kun udvides med underliggende værdier, dvs. tilladte værdier udgøres af værdier i dette register samt undeliggende værdier defineret af dataleverandører.</p>', NULL, NULL, NULL, '2019-07-30 14:35:08.195', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('71a9a76e480888c0151406532337ebbc', 'de', '91e00e7f8d73c54fa2f80c67d3d9d5b0', 'FIELD_LABEL_UUID', 0, 'Erweiterbar um spezifischere Werte', NULL, NULL, NULL, '2019-07-30 14:35:08.198', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('37ae3acebdb3ef166021364567fee50d', 'de', '91e00e7f8d73c54fa2f80c67d3d9d5b0', 'FIELD_DEFINITION_UUID', 0, '<p>Die Codeliste darf nur um spezifischere Werte erweitert werden, d.h. ihre erlaubten Werte umfassen die Werte, die in diesem Register enthalten sind, und zusätzlich spezifischere von Datenanbietern definierte Werte.</p>', NULL, NULL, NULL, '2019-07-30 14:35:08.2', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('1122177a1a888f5d04f7da416de27eea', 'et', '91e00e7f8d73c54fa2f80c67d3d9d5b0', 'FIELD_LABEL_UUID', 0, 'Laiendatav rohkem piiratud väärtustega', NULL, NULL, NULL, '2019-07-30 14:35:08.202', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('cb793cdf897eb96130d6412b0f41581d', 'et', '91e00e7f8d73c54fa2f80c67d3d9d5b0', 'FIELD_DEFINITION_UUID', 0, '<p>Koodiloendit võib täiendada ainult rohkem piiratud väärtusega, st selle lubatud väärtused hõlmavad selles registris määratud väärtusi ja andmete tarnijate poolt kitsendavalt defineeritud väärtusi.</p>', NULL, NULL, NULL, '2019-07-30 14:35:08.205', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('2b181ce585dec877dfcc8e3fea29b9b2', 'el', '91e00e7f8d73c54fa2f80c67d3d9d5b0', 'FIELD_LABEL_UUID', 0, 'επεκτάσιμο με μικρότερες τιμές', NULL, NULL, NULL, '2019-07-30 14:35:08.207', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('b5289490decaf8cae742203ecff32f56', 'el', '91e00e7f8d73c54fa2f80c67d3d9d5b0', 'FIELD_DEFINITION_UUID', 0, '<p>Αυτός ο κωδικός λίστας μπορεί να επεκταθεί μόνο με στενότερες τιμές, οι επιτρεπόμενες τιμές αποτελούν τιμές που αναγράφονται καταχωρητής και στενότερες τιμές όπως ορίζονται από παρόχους δεδομένων</p>', NULL, NULL, NULL, '2019-07-30 14:35:08.21', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('064db048e3917c147c1bc76281ad1117', 'es', '91e00e7f8d73c54fa2f80c67d3d9d5b0', 'FIELD_LABEL_UUID', 0, 'Extensible con valores más específicos', NULL, NULL, NULL, '2019-07-30 14:35:08.212', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('540d7144ae14ca65faeb21da556f348c', 'es', '91e00e7f8d73c54fa2f80c67d3d9d5b0', 'FIELD_DEFINITION_UUID', 0, '<p>Se puede extender la lista de códigos sólo con valores más específicos; es decir, los valores permitidos son los valores especificados en el registro y otros más específicos definidos por el proveedor de datos</p>', NULL, NULL, NULL, '2019-07-30 14:35:08.214', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('f6330f03c6d07127e6d9a2ec432fa832', 'fr', '91e00e7f8d73c54fa2f80c67d3d9d5b0', 'FIELD_LABEL_UUID', 0, 'Extensible avec des codes spécifiques', NULL, NULL, NULL, '2019-07-30 14:35:08.217', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('926dcb2dc8cbeee9fb94b3fb083ef46c', 'fr', '91e00e7f8d73c54fa2f80c67d3d9d5b0', 'FIELD_DEFINITION_UUID', 0, '<p>La liste de codes ne peut être étendue qu''avec des codes spécifiques: les codes autorisés sont les codes existants et de nouveaux codes spécifiques définis par des fournisseurs de données.</p>', NULL, NULL, NULL, '2019-07-30 14:35:08.219', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('ad90068e710791963b723a716ed0a722', 'hr', '91e00e7f8d73c54fa2f80c67d3d9d5b0', 'FIELD_LABEL_UUID', 0, 'Proširivo užim skupom vrijednosti', NULL, NULL, NULL, '2019-07-30 14:35:08.222', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('c4b48d461d1a5ccad9eb3477939c1a5a', 'hr', '91e00e7f8d73c54fa2f80c67d3d9d5b0', 'FIELD_DEFINITION_UUID', 0, '<p>Kodna lista je proširiva samo užim skupom vrijednosti specificiranim u ovom registru ili definiranim od strane pružatelja podataka.</p>', NULL, NULL, NULL, '2019-07-30 14:35:08.224', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('7e6e0e4074f4375470e9cb213d750024', 'it', '91e00e7f8d73c54fa2f80c67d3d9d5b0', 'FIELD_LABEL_UUID', 0, 'Estendibile con valori ristretti', NULL, NULL, NULL, '2019-07-30 14:35:08.227', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('bd086cc3cd4c5e79f6e74e56216fe3b6', 'it', '91e00e7f8d73c54fa2f80c67d3d9d5b0', 'FIELD_DEFINITION_UUID', 0, '<p>L''elenco dei codici può essere esteso solo con valori più stretti, cioè i suoi valori consentiti comprendono i valori indicati in questo registro e valori più stretti definiti dal provider di dati.</p>', NULL, NULL, NULL, '2019-07-30 14:35:08.231', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('7c786947045885937581227595386598', 'lv', '91e00e7f8d73c54fa2f80c67d3d9d5b0', 'FIELD_LABEL_UUID', 0, 'Paplašināms ar ierobežotām vērtībām', NULL, NULL, NULL, '2019-07-30 14:35:08.233', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('2e33cddb1e10423103bfe23509d32b50', 'lv', '91e00e7f8d73c54fa2f80c67d3d9d5b0', 'FIELD_DEFINITION_UUID', 0, '<p>Kodu sarakstu var paplašināt tikai ar ierobežotām vērtībām, t.i., tā atļautās vērtības aptver šajā reģistrā iekļautās vērtības un ierobežotas datu sniedzēja definētas vērtības.</p>', NULL, NULL, NULL, '2019-07-30 14:35:08.236', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('b5747e085e993fdf20ffffeba43bed62', 'lt', '91e00e7f8d73c54fa2f80c67d3d9d5b0', 'FIELD_LABEL_UUID', 0, 'Plečiamas konkretesnėmis reikšmėmis', NULL, NULL, NULL, '2019-07-30 14:35:08.238', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('1be1d12115d948f5af6ee0708d7a89bd', 'lt', '91e00e7f8d73c54fa2f80c67d3d9d5b0', 'FIELD_DEFINITION_UUID', 0, '<p>Kodų sąrašas gali būti papildytas tik žemesnio lygmens reikšmėmis, t.y., leidžiamos reikšmės apima registre esančias ir duomenų teikėjo nurodytas reikšmes, jei jos yra konkretesnės už registre esančias reikšmes (jas patikslina).</p>', NULL, NULL, NULL, '2019-07-30 14:35:08.241', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('2ee5cafab646da8412f18ecc567a6025', 'hu', '91e00e7f8d73c54fa2f80c67d3d9d5b0', 'FIELD_LABEL_UUID', 0, 'Az értékek szűkítése elfogadott', NULL, NULL, NULL, '2019-07-30 14:35:08.244', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('6a11db7c1224bdd03e98220cc4964113', 'hu', '91e00e7f8d73c54fa2f80c67d3d9d5b0', 'FIELD_DEFINITION_UUID', 0, '<p>A kódlistához az adatszolgáltató olyan értékeket tehet hozzá, melyek szűkítik az eredeti értékek fogalmát.</p>', NULL, NULL, NULL, '2019-07-30 14:35:08.247', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('2d621e6367a6d9f0a6ee1a8ad4c37e79', 'mt', '91e00e7f8d73c54fa2f80c67d3d9d5b0', 'FIELD_LABEL_UUID', 0, 'Estensibbli b''valuri idjaq', NULL, NULL, NULL, '2019-07-30 14:35:08.249', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('c2d1fe890a89fc26be68264d3bef4120', 'mt', '91e00e7f8d73c54fa2f80c67d3d9d5b0', 'FIELD_DEFINITION_UUID', 0, '<p>Il-lista tal-koriċi tista'' tiġi estiża biss b''valuri idjaq, jiġifieri l-valuri permessi tagħha jinkludu l-valuri speċifikati f''dan ir-reġistru u valuri idjaq definiti mill-fornituri tad-dejta.</p>', NULL, NULL, NULL, '2019-07-30 14:35:08.252', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('61b8e0e60e20cca39b39682327496cdc', 'nl', '91e00e7f8d73c54fa2f80c67d3d9d5b0', 'FIELD_LABEL_UUID', 0, 'Uitbreidbaar met meer beperkende waarden', NULL, NULL, NULL, '2019-07-30 14:35:08.255', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('e2338ce9bbe7533c2e2a8ab6c75ab379', 'nl', '91e00e7f8d73c54fa2f80c67d3d9d5b0', 'FIELD_DEFINITION_UUID', 0, '<p>De codelijst kan enkel worden uitgebreid met nauwere waarden, met andere woorden de toegstane waarden zijn deze gespecifieerd in dit register plus de nauwere waarden gedefinieerd door gegevens beheerders.</p>', NULL, NULL, NULL, '2019-07-30 14:35:08.257', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('e523c811f21f276725fec878996644ed', 'pl', '91e00e7f8d73c54fa2f80c67d3d9d5b0', 'FIELD_LABEL_UUID', 0, 'Rozszerzalny z węższymi wartościami', NULL, NULL, NULL, '2019-07-30 14:35:08.26', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('c572a70e1c259f2d4df4def140c26681', 'pl', '91e00e7f8d73c54fa2f80c67d3d9d5b0', 'FIELD_DEFINITION_UUID', 0, '<p>Lista kodów może zostać jedynie rozszerzona z węższymi wartościami, to znaczy, że dozwolone wartości zawierają wartości określone w tym wykazie oraz zawężone wartości zdefiniowane przez dostawców danych.</p>', NULL, NULL, NULL, '2019-07-30 14:35:08.262', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('5efb291a5bd9fbb386197d17c2c666f1', 'pt', '91e00e7f8d73c54fa2f80c67d3d9d5b0', 'FIELD_LABEL_UUID', 0, 'Extensível com valores mais restritos', NULL, NULL, NULL, '2019-07-30 14:35:08.265', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('7adfa713c49549882e129a507df1d15c', 'pt', '91e00e7f8d73c54fa2f80c67d3d9d5b0', 'FIELD_DEFINITION_UUID', 0, '<p>A lista de códigos só pode ser alargada com valores mais restritos, ou seja, os valores permitidos compreendem os valores especificados neste registo e os valores mais estreitos definidos nos provedores de dados.</p>', NULL, NULL, NULL, '2019-07-30 14:35:08.267', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('46402f09ef9a7ee15147fc5dc0e512bf', 'ro', '91e00e7f8d73c54fa2f80c67d3d9d5b0', 'FIELD_LABEL_UUID', 0, 'Extensibilă cu valori mai restrânse', NULL, NULL, NULL, '2019-07-30 14:35:08.27', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('a688f9d76f1a225697d4f47f93f1980a', 'ro', '91e00e7f8d73c54fa2f80c67d3d9d5b0', 'FIELD_DEFINITION_UUID', 0, '<p>Lista de coduri pot fi extinsă doar cu valori mai restrânse, adică valorile sale permise cuprind valorile specificate în registru și valori înguste definite de furnizorii de date.</p>', NULL, NULL, NULL, '2019-07-30 14:35:08.273', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('d80593eb4c75bdd73dad0dc5caf47796', 'sk', '91e00e7f8d73c54fa2f80c67d3d9d5b0', 'FIELD_LABEL_UUID', 0, 'Rozšíriteľný s užšími (podrobnejšími) hodnotami', NULL, NULL, NULL, '2019-07-30 14:35:08.275', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('3677c233d6b05ad32b9a2358182121fe', 'sk', '91e00e7f8d73c54fa2f80c67d3d9d5b0', 'FIELD_DEFINITION_UUID', 0, '<p>Zoznam kódov (číselník) možno rozšíriť iba s hodnotami užšieho významu, t.j. jeho povolené hodnoty zahŕňajú hodnoty uvedené v tomto registri ako aj užšie hodnoty zadefinované poskytovateľmi údajov</p>', NULL, NULL, NULL, '2019-07-30 14:35:08.278', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('5e6b48e52f4afbf1e05a2915c42cc982', 'sl', '91e00e7f8d73c54fa2f80c67d3d9d5b0', 'FIELD_LABEL_UUID', 0, 'Dopolnjevanje je mogoče z omejenimi vrednostmi', NULL, NULL, NULL, '2019-07-30 14:35:08.281', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('4ff7f568e415ea9b36bf7bf44e1fadf4', 'sl', '91e00e7f8d73c54fa2f80c67d3d9d5b0', 'FIELD_DEFINITION_UUID', 0, '<p>Dopolnjevanje seznama oznak je mogoče le z omejenimi vrednostmi, t.j. dovoljene vrednosti so tiste navedene v registru in vrednosti, ki so bolj omejene od navedenih in ki jih opredeli ponudnik podatkov.</p>', NULL, NULL, NULL, '2019-07-30 14:35:08.283', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('71a00da518b8054c63ccf262d7077960', 'fi', '91e00e7f8d73c54fa2f80c67d3d9d5b0', 'FIELD_LABEL_UUID', 0, 'Laajennettavissa yksityiskohtaisemmilla koodiarvoilla', NULL, NULL, NULL, '2019-07-30 14:35:08.286', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('a7888e278954567a39dfe44bdb624e13', 'fi', '91e00e7f8d73c54fa2f80c67d3d9d5b0', 'FIELD_DEFINITION_UUID', 0, '<p>Koodiluetteloa voidaan laajentaa ainoastaan yksityiskohtaisemmilla koodiarvoilla. Koodiluettelossa siis sallitaan tässä rekisterissä määritellyt koodiarvot ja niitä yksityiskohtaisemmat tiedontuottajien määrittelemät koodiarvot.</p>', NULL, NULL, NULL, '2019-07-30 14:35:08.288', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('982d4f69a6096487493ac852f0b95790', 'sv', '91e00e7f8d73c54fa2f80c67d3d9d5b0', 'FIELD_LABEL_UUID', 0, 'Utökningbar inom befintliga värdedomäner (specialisering)', NULL, NULL, NULL, '2019-07-30 14:35:08.29', NULL);\n"
            + "INSERT INTO public.reg_localization VALUES ('5dae549e0c70666cd3b9bc0c4ca03057', 'sv', '91e00e7f8d73c54fa2f80c67d3d9d5b0', 'FIELD_DEFINITION_UUID', 0, '<p>Kodlistan kan bara utökas inom befintliga värdedomäner, dvs. värden måste vara inom tillåtna värden i detta register och inom de av dataleverantörerna definierade värdedomänerna.</p>', NULL, NULL, NULL, '2019-07-30 14:35:08.293', NULL);";

    public MigrateExtensiblityAndGovernanceLevel(MigrationManager migrationManager) {
        this.migrationManager = migrationManager;
        this.entityManagerRe3gistry2 = migrationManager.getEntityManagerRe3gistry2();
        this.entityManagerRe3gistry2Migration = migrationManager.getEntityManagerRe3gistry2Migration();
        this.logger = migrationManager.getLogger();
    }

    public void startMigrationExtensiblityAndGovernanceLevel() throws Exception {
        ArrayList<RegItem> itemsToIndexSOLR = new ArrayList<>();
        int DATAPROCEDUREORDER = migrationManager.getProcedureorder();
        boolean commit = true;
        /**
         * migrate Register item class
         */
        try {
            String USERMIGRATION_UUID = migrationManager.getMigrationUser().getUuid();

            Query queryRegistry = entityManagerRe3gistry2Migration.createNamedQuery("Registry.findAll", Registry.class);
            Registry oldRegistry = ((Registry) queryRegistry.getResultList().get(0));

            RegItemclasstypeManager regItemclasstypeManager = new RegItemclasstypeManager(entityManagerRe3gistry2);
            RegItemclasstype regItemclasstypeRegistry = regItemclasstypeManager.getByLocalid("registry");
            String REGISTRY_ITEMCLASS_UUID = RegItemclassUuidHelper.getUuid(oldRegistry.getUriname(), null, regItemclasstypeRegistry);

            RegItemclassManager regItemclassManager = new RegItemclassManager(entityManagerRe3gistry2);
            RegItemclass registryItemClass = regItemclassManager.get(REGISTRY_ITEMCLASS_UUID);
            RegItemManager regItemManager = new RegItemManager(entityManagerRe3gistry2);
            RegItem newRegistry = regItemManager.getByLocalidAndRegItemClass(oldRegistry.getUriname(), regItemclassManager.get(REGISTRY_ITEMCLASS_UUID));

            String REGISTRY_UUID = newRegistry.getUuid();
            String BASE_URI_REGISTRY = registryItemClass.getBaseuri() + "/" + newRegistry.getLocalid();

            /**
             * get fields
             */
            RegFieldManager regFieldManager = new RegFieldManager(entityManagerRe3gistry2);
            String FIELD_LABEL_UUID = regFieldManager.getByLocalid(BaseConstants.KEY_FIELD_MANDATORY_LABEL_LOCALID).getUuid();
            String FIELD_CONTENTSUMMARY_UUID = regFieldManager.getByLocalid(BaseConstants.KEY_FIELD_MANDATORY_CONTENTSUMMARY_LOCALID).getUuid();

            RegInstallationHandler regInstallationHandler = new RegInstallationHandler(entityManagerRe3gistry2);
            RegField regFieldDefinition;
            try {
                regFieldDefinition = regFieldManager.getByLocalid(BaseConstants.KEY_FIELD_DEFINITION_LOCALID);
            } catch (Exception ex) {
                regFieldDefinition = regInstallationHandler.createRegFielAndFieldLocalizationdByName(BaseConstants.KEY_FIELD_DEFINITION_LOCALID, BaseConstants.KEY_FIELDTYPE_LONGTEXT_UUID, null, commit);
            }
            String FIELD_DEFINITION_UUID = regFieldDefinition.getUuid();

            String FIELD_REGISTRYMANAGER_UUID = regFieldManager.getByLocalid(BaseConstants.KEY_FIELD_MANDATORY_REGISTRYMANAGER).getUuid();
            String FIELD_REGISTERMANAGER_UUID = regFieldManager.getByLocalid(BaseConstants.KEY_FIELD_MANDATORY_REGISTERMANAGER).getUuid();
            String FIELD_CONTENTBODY_UUID = regFieldManager.getByLocalid(BaseConstants.KEY_FIELD_MANDATORY_CONTROLBODY).getUuid();
            String FIELD_SUBMITTINGORGANIZATION_UUID = regFieldManager.getByLocalid(BaseConstants.KEY_FIELD_MANDATORY_SUBMITTINGORGANIZATIONS).getUuid();
            String FIELD_REGISTEROWNER_UUID = regFieldManager.getByLocalid(BaseConstants.KEY_FIELD_MANDATORY_REGISTEROWNER).getUuid();

            RegField regFieldStatus;
            try {
                regFieldStatus = regFieldManager.getByLocalid(BaseConstants.KEY_FIELD_MANDATORY_STATUS_LOCALID);
            } catch (Exception ex) {
                regFieldStatus = regInstallationHandler.createRegFielAndFieldLocalizationdByName(BaseConstants.KEY_FIELD_MANDATORY_STATUS_LOCALID, BaseConstants.KEY_FIELDTYPE_STATUS_UUID, null, commit);
            }
            String FIELD_STATUS_UUID = regFieldStatus.getUuid();

            RegGroupManager regGroupManager = new RegGroupManager(entityManagerRe3gistry2);

            String GROUP_REGISTERMANAGER_UUID = ((RegGroup) regGroupManager.getByLocalid(BaseConstants.KEY_ROLE_REGISTERMANAGER)).getUuid();
            String GROUP_REGISTEROWNER_UUID = ((RegGroup) regGroupManager.getByLocalid(BaseConstants.KEY_ROLE_REGISTEROWNER)).getUuid();
            String GROUP_CONTENTBODY_UUID = ((RegGroup) regGroupManager.getByLocalid(BaseConstants.KEY_ROLE_CONTROLBODY)).getUuid();
            String GROUP_SUBMITTINGORGANIZATION_UUID = ((RegGroup) regGroupManager.getByLocalid(BaseConstants.KEY_ROLE_SUBMITTINGORGANIZATION)).getUuid();

            try {
                if (!entityManagerRe3gistry2.getTransaction().isActive()) {
                    entityManagerRe3gistry2.getTransaction().begin();
                }
                entityManagerRe3gistry2.createNativeQuery(QUERY_ITEMCLASS_1
                        .replace("BASE_URI_REGISTRY", BASE_URI_REGISTRY)
                        .replace("DATAPROCEDUREORDER", Integer.toString(DATAPROCEDUREORDER))
                        .replace("REGISTRY_ITEMCLASS_UUID", REGISTRY_ITEMCLASS_UUID))
                        .executeUpdate();
                DATAPROCEDUREORDER++;
                entityManagerRe3gistry2.createNativeQuery(QUERY_ITEMCLASS_2
                        .replace("BASE_URI_REGISTRY", BASE_URI_REGISTRY)
                        .replace("DATAPROCEDUREORDER", Integer.toString(DATAPROCEDUREORDER))
                        .replace("REGISTRY_ITEMCLASS_UUID", REGISTRY_ITEMCLASS_UUID))
                        .executeUpdate();
                DATAPROCEDUREORDER++;
                entityManagerRe3gistry2.createNativeQuery(QUERY_ITEMCLASS_3
                        .replace("BASE_URI_REGISTRY", BASE_URI_REGISTRY)
                        .replace("DATAPROCEDUREORDER", Integer.toString(DATAPROCEDUREORDER))
                        .replace("REGISTRY_ITEMCLASS_UUID", REGISTRY_ITEMCLASS_UUID))
                        .executeUpdate();
                DATAPROCEDUREORDER++;
                entityManagerRe3gistry2.createNativeQuery(QUERY_ITEMCLASS_4
                        .replace("BASE_URI_REGISTRY", BASE_URI_REGISTRY)
                        .replace("DATAPROCEDUREORDER", Integer.toString(DATAPROCEDUREORDER))
                        .replace("REGISTRY_ITEMCLASS_UUID", REGISTRY_ITEMCLASS_UUID))
                        .executeUpdate();

                entityManagerRe3gistry2.createNativeQuery(QUERY_FIELDMAPPING
                        .replace("FIELD_CONTENTBODY_UUID", FIELD_CONTENTBODY_UUID)
                        .replace("FIELD_REGISTERMANAGER_UUID", FIELD_REGISTERMANAGER_UUID)
                        .replace("FIELD_SUBMITTINGORGANIZATION_UUID", FIELD_SUBMITTINGORGANIZATION_UUID)
                        .replace("FIELD_CONTENTSUMMARY_UUID", FIELD_CONTENTSUMMARY_UUID)
                        .replace("FIELD_LABEL_UUID", FIELD_LABEL_UUID)
                        .replace("FIELD_STATUS_UUID", FIELD_STATUS_UUID)
                        .replace("FIELD_REGISTEROWNER_UUID", FIELD_REGISTEROWNER_UUID)
                        .replace("FIELD_DEFINITION_UUID", FIELD_DEFINITION_UUID))
                        .executeUpdate();

                entityManagerRe3gistry2.createNativeQuery(QUERY_ITEM
                        .replace("USERMIGRATION_UUID", USERMIGRATION_UUID))
                        .executeUpdate();

                entityManagerRe3gistry2.createNativeQuery(QUERY_ITEM_GROUP_ROLE
                        .replace("GROUP_REGISTEROWNER_UUID", GROUP_REGISTEROWNER_UUID)
                        .replace("GROUP_REGISTERMANAGER_UUID", GROUP_REGISTERMANAGER_UUID)
                        .replace("GROUP_CONTENTBODY_UUID", GROUP_CONTENTBODY_UUID)
                        .replace("GROUP_SUBMITTINGORGANIZATION_UUID", GROUP_SUBMITTINGORGANIZATION_UUID))
                        .executeUpdate();

                entityManagerRe3gistry2.createNativeQuery(QUERY_RELATION
                        .replace("REGISTRY_UUID", REGISTRY_UUID))
                        .executeUpdate();

                entityManagerRe3gistry2.createNativeQuery(QUERY_GOVERNANCELEVEL
                        .replace("FIELD_LABEL_UUID", FIELD_LABEL_UUID)
                        .replace("FIELD_CONTENTSUMMARY_UUID", FIELD_CONTENTSUMMARY_UUID)
                        .replace("FIELD_DEFINITION_UUID", FIELD_DEFINITION_UUID))
                        .executeUpdate();

                entityManagerRe3gistry2.createNativeQuery(QUERY_EXTENSIBILITY
                        .replace("FIELD_LABEL_UUID", FIELD_LABEL_UUID)
                        .replace("FIELD_CONTENTSUMMARY_UUID", FIELD_CONTENTSUMMARY_UUID)
                        .replace("FIELD_DEFINITION_UUID", FIELD_DEFINITION_UUID))
                        .executeUpdate();
                entityManagerRe3gistry2.createNativeQuery(QUERY_EXTENSIBILITY_1
                        .replace("FIELD_LABEL_UUID", FIELD_LABEL_UUID)
                        .replace("FIELD_CONTENTSUMMARY_UUID", FIELD_CONTENTSUMMARY_UUID)
                        .replace("FIELD_DEFINITION_UUID", FIELD_DEFINITION_UUID))
                        .executeUpdate();

                if (commit) {
                    if (!entityManagerRe3gistry2.getTransaction().isActive()) {
                        entityManagerRe3gistry2.getTransaction().begin();
                    }
                    entityManagerRe3gistry2.getTransaction().commit();
                }
            } catch (Exception ex) {
                logger.error(ex.getMessage());
                throw new Exception(ex.getMessage());
            }

            for (String itemUuid : itemsUUID) {
                itemsToIndexSOLR.add(regItemManager.get(itemUuid));
            }

            itemsToIndexSOLR.forEach((regItem) -> {
                try {
                    SolrHandler.indexSingleItem(regItem);
                } catch (Exception e) {
                    logger.error("@ MigrateRegister.MigrateExtensiblityAndGovernanceLevel: Solr indexing error.", e);
                }
            });

        } catch (Exception exe) {
            logger.error(exe.getMessage());
            throw new Exception(exe.getMessage());
        }

    }

}
