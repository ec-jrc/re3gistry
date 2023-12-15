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
package eu.europa.ec.ror.restful;

import eu.europa.ec.ror.managers.RelationMgr;
import eu.europa.ec.ror.model.Register;
import eu.europa.ec.ror.model.Registry;
import eu.europa.ec.ror.model.Relation;
import eu.europa.ec.ror.utility.Constants;
import eu.europa.ec.ror.utility.exception.NotFoundException;
import eu.europa.ec.ror.utility.localization.LocalizationMgr;
import java.util.HashMap;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import javax.ejb.Stateless;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

@Stateless
@Path("relations")
public class Relations {

    /**
     * GET the list detail of all the relations
     *
     * @param request
     * @return a JSON of the detail of all the relations
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String list(@Context HttpServletRequest request) {
        
        Locale currentLocale = LocalizationMgr.getCurrentLocale(request);
        ResourceBundle localization = ResourceBundle.getBundle(Constants.LOCALIZATION_PREFIX + "." + Constants.LOCALIZATION_SUFFIX, currentLocale);

        List<Relation> relationList = RelationMgr.getAllRelations();

        JSONObject json = new JSONObject();
        if (!relationList.isEmpty()) {
            JSONArray array = new JSONArray();
            for (Relation relations : relationList) {
                JSONObject relationObj = addJSONObjectFromRelation(relations);

                array.put(relationObj);
            }
            json.put("relations", array);
        } else {
            json.put("code", Response.Status.NOT_FOUND.getStatusCode());
            json.put("message", localization.getString("error.restful.relation.notfound.message"));
            json.put("description", localization.getString("error.restful.relation.notfound.description"));
            throw new NotFoundException(json.toString(2));
        }

        return json.toString(2);
    }

    /**
     * GET the list detail of the relations by relations ID
     *
     * @param id
     * @param request
     * @return a JSON of the detail of the relations by relations ID
     */
    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public String id(@PathParam("id") String id, @Context HttpServletRequest request) {
        
        Locale currentLocale = LocalizationMgr.getCurrentLocale(request);
        ResourceBundle localization = ResourceBundle.getBundle(Constants.LOCALIZATION_PREFIX + "." + Constants.LOCALIZATION_SUFFIX, currentLocale);

        Relation relations = RelationMgr.getRelationByID(id);

        JSONObject obj = new JSONObject();
        if (relations != null) {
            obj = addJSONObjectFromRelation(relations);
        } else {
            obj.put("code", Response.Status.NOT_FOUND.getStatusCode());
            obj.put("message", localization.getString("error.restful.relation.notfound.message"));
            obj.put("description", localization.getString("error.restful.relation.item.notfound").replace("{0}",id));
            throw new NotFoundException(obj.toString(2));
        }

        return obj.toString(2);
    }
    
     /**
     * GET the relations detail by query
     *
     * @param query
     * @param request
     * @return a JSON of the Relation with the query
     */
    @GET
    @Path("q/{query : .+}")
    @Produces(MediaType.APPLICATION_JSON)
    public String query(@PathParam("query") String query, @Context HttpServletRequest request) {
        
        Locale currentLocale = LocalizationMgr.getCurrentLocale(request);
        ResourceBundle localization = ResourceBundle.getBundle(Constants.LOCALIZATION_PREFIX + "." + Constants.LOCALIZATION_SUFFIX, currentLocale);

        JSONObject json = new JSONObject();
        HashMap<Relation, String> map = new HashMap<>();

        String[] queryList = query.split("&");
        for (String queryByQuery : queryList) {
            String[] smallQueryList = queryByQuery.split("=");

            if (smallQueryList.length == 1) {
                json.put("code", Response.Status.BAD_REQUEST.getStatusCode());
                json.put("message", localization.getString("error.restful.relation.notfound.message"));
                json.put("description", localization.getString("error.restful.relation.query.notfound") + RelationMgr.ID + ", " + RelationMgr.SUBJECT_REGISTER_ID + ", " + RelationMgr.SUBJECT_REGISTER_URI + ", " + RelationMgr.OBJECT_REGISTER_ID + ", " + RelationMgr.OBJECT_REGISTER_URI + ", " + RelationMgr.STATUS + ", " + RelationMgr.PREDICATE);
                throw new NotFoundException(json.toString(2));
            }

            String variable = smallQueryList[0];
            String value = smallQueryList[1];

            if (variable.equals(RelationMgr.OBJECT_REGISTER_ID)) {
                List<Relation> registersForRegistryID = RelationMgr.getAllRelationsByRelatedregisterID(value);
                for (Relation relations : registersForRegistryID) {
                    if (!map.containsKey(relations)) {
                        map.put(relations, "");
                    }
                }
            } else if (variable.equals(RelationMgr.OBJECT_REGISTER_URI)) {
                List<Relation> registersForRegistryURI = RelationMgr.getAllRelationsByRelatedregisterURI(value);
                for (Relation relations : registersForRegistryURI) {
                    if (!map.containsKey(relations)) {
                        map.put(relations, "");
                    }
                }
            } else if (variable.equals(RelationMgr.SUBJECT_REGISTER_ID)) {
                List<Relation> relationsBySourceregisterID = RelationMgr.getAllRelationsBySubjectregisterID(value);
                for (Relation relations : relationsBySourceregisterID) {
                    if (!map.containsKey(relations)) {
                        map.put(relations, "");
                    }
                }
            } else if (variable.equals(RelationMgr.SUBJECT_REGISTER_URI)) {
                List<Relation> relationsBySourceregisterURI = RelationMgr.getAllRelationsBySubjectregisterURI(value);
                for (Relation relations : relationsBySourceregisterURI) {
                    if (!map.containsKey(relations)) {
                        map.put(relations, "");
                    }
                }
            } else if (variable.equals(RelationMgr.ID)) {
                Relation relationFromID = RelationMgr.getRelationByID(value);
                if (relationFromID != null && !map.containsKey(relationFromID)) {
                    map.put(relationFromID, "");
                }
            } else if (variable.equals(RelationMgr.SUBJECT_REGISTER_OR_OBJECT_REGISTER_ID)) {
                List<Relation> relationsBySourceregisterIDorRelatedregisterID = RelationMgr.getAllRelationsBySourceregisterIDorRelatedregisterID(value);
                for (Relation relations : relationsBySourceregisterIDorRelatedregisterID) {
                    if (!map.containsKey(relations)) {
                        map.put(relations, "");
                    }
                }
            } else if (variable.equals(RelationMgr.STATUS)) {
                List<Relation> relationsByStatus = RelationMgr.getAllRelationsByStatus(value);
                for (Relation relations : relationsByStatus) {
                    if (!map.containsKey(relations)) {
                        map.put(relations, "");
                    }
                }
            } else if (variable.equals(RelationMgr.PREDICATE)) {
                List<Relation> relationsByPredicate = RelationMgr.getAllRelationsByPredicate(value);
                for (Relation relations : relationsByPredicate) {
                    if (!map.containsKey(relations)) {
                        map.put(relations, "");
                    }
                }
            } else {
                json.put("code", Response.Status.BAD_REQUEST.getStatusCode());
                json.put("message", localization.getString("error.restful.relation.badrequest.message"));
                json.put("description",  localization.getString("error.restful.relation.badrequest.description") + RelationMgr.ID + ", " + RelationMgr.SUBJECT_REGISTER_ID + ", " + RelationMgr.SUBJECT_REGISTER_URI + ", " + RelationMgr.OBJECT_REGISTER_ID + ", " + RelationMgr.OBJECT_REGISTER_URI + ", " + RelationMgr.STATUS + ", " + RelationMgr.PREDICATE);
                throw new NotFoundException(json.toString(2));
            }
        }

        JSONArray array = new JSONArray();
        if (!map.isEmpty()) {
            for (Map.Entry<Relation, String> entry : map.entrySet()) {
                Relation relations = entry.getKey();

                JSONObject registerObj = addJSONObjectFromRelation(relations);
                array.put(registerObj);
            }
            json.put("relations", array);
        } else {
            json.put("code", Response.Status.NOT_FOUND.getStatusCode());
            json.put("message", localization.getString("error.restful.register.notfound.message"));
            json.put("description", localization.getString("error.restful.register.query.notfound") + RelationMgr.ID + ", " + RelationMgr.SUBJECT_REGISTER_ID + ", " + RelationMgr.SUBJECT_REGISTER_URI + ", " + RelationMgr.OBJECT_REGISTER_ID + ", " + RelationMgr.OBJECT_REGISTER_URI + ", " + RelationMgr.STATUS + ", " + RelationMgr.PREDICATE);
            throw new NotFoundException(json.toString(2));
        }

        return json.toString(2);
    }

    private JSONObject addJSONObjectFromRelation(Relation relations) throws JSONException {
        JSONObject obj = new JSONObject();
        
        // Subject
        Register subjectRegister = relations.getSubjectasset();

        JSONObject subjectRegistryObj = new JSONObject();
        Registry sourceregisterregistry = subjectRegister.getRegistry();
        subjectRegistryObj.put("id", sourceregisterregistry.getUuid());
        subjectRegistryObj.put("uri", sourceregisterregistry.getUri());
        subjectRegistryObj.put("label", sourceregisterregistry.getLabel());

        JSONObject subjectRegisterObj = new JSONObject();
        subjectRegisterObj.put("id", subjectRegister.getUuid());
        subjectRegisterObj.put("uri", subjectRegister.getUri());
        subjectRegisterObj.put("label", subjectRegister.getLabel());
        subjectRegisterObj.put("registry", subjectRegistryObj);
        
        // Object
        Register objectRegister = relations.getObjectasset();

        JSONObject objectRegistryObj = new JSONObject();
        Registry relatedregisterregistry = objectRegister.getRegistry();
        objectRegistryObj.put("id", relatedregisterregistry.getUuid());
        objectRegistryObj.put("uri", relatedregisterregistry.getUri());
        objectRegistryObj.put("label", relatedregisterregistry.getLabel());


        JSONObject objectRegisterObj = new JSONObject();
        objectRegisterObj.put("id", objectRegister.getUuid());
        objectRegisterObj.put("uri", objectRegister.getUri());
        objectRegisterObj.put("label", objectRegister.getLabel());
        objectRegisterObj.put("registry", objectRegistryObj);

        obj.put("id", relations.getUuid());
        obj.put("subjectRegister", subjectRegisterObj);
        obj.put("objectRegister", objectRegisterObj);
        
        obj.put("predicate", relations.getPredicate());
        obj.put("status", relations.getStatus());
        

        JSONObject relationObj = new JSONObject();
        relationObj.put("relation", obj);

        return relationObj;
    }

}
