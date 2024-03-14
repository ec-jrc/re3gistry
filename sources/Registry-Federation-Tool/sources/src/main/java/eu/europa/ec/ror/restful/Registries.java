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

import eu.europa.ec.ror.managers.RegistryMgr;
import eu.europa.ec.ror.model.Registry;
import eu.europa.ec.ror.utility.Constants;
import eu.europa.ec.ror.utility.exception.NotFoundException;
import eu.europa.ec.ror.utility.localization.LocalizationMgr;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
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

@Path("registries")
public class Registries {
         
    /**
     * GET all the registries
     *
     * @param request
     * @param req
     * @return a JSON containing all the registries
     */

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String list(@Context HttpServletRequest request) {
        
        Locale currentLocale = LocalizationMgr.getCurrentLocale(request);
        ResourceBundle localization = ResourceBundle.getBundle(Constants.LOCALIZATION_PREFIX + "." + Constants.LOCALIZATION_SUFFIX, currentLocale);
        
        List<Registry> list = RegistryMgr.getAllRegistries();

        JSONObject json = new JSONObject();
        if (list != null && !list.isEmpty()) {

            JSONArray array = new JSONArray();
            for (Registry registries : list) {
                JSONObject registryObj = addRegistryJSON(registries);

                array.put(registryObj);
            }
            json.put("registries", array);
        } else {
            json.put("code", Response.Status.NOT_FOUND.getStatusCode());
            json.put("message", localization.getString("error.restful.registry.notfound.message"));
            json.put("description", localization.getString("error.restful.registry.notfound.description"));
            throw new NotFoundException(json.toString(2));
        }
        return json.toString(2);
    }

    /**
     * GET the registries detail by ID
     *
     * @param id
     * @param request
     * @return a JSON of the Registry with the id ID
     */
    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public String detailByID(@PathParam("id") String id, @Context HttpServletRequest request) {
        
        Locale currentLocale = LocalizationMgr.getCurrentLocale(request);
        ResourceBundle localization = ResourceBundle.getBundle(Constants.LOCALIZATION_PREFIX + "." + Constants.LOCALIZATION_SUFFIX, currentLocale);
        
        Registry registries = RegistryMgr.getRegistryByID(id);

        JSONObject obj = new JSONObject();
        if (registries != null) {
            obj = addRegistryJSON(registries);
        } else {
            obj.put("code", Response.Status.NOT_FOUND.getStatusCode());
            obj.put("message", localization.getString("error.restful.registry.notfound.message"));
            obj.put("description", localization.getString("error.restful.registry.item.notfound").replace("{0}",id));
            throw new NotFoundException(obj.toString(2));
        }
        return obj.toString(2);
    }

    /**
     * GET the registries detail by query
     *
     * @param query
     * @return a JSON of the Registry with the query
     */
    @GET
    @Path("q/{query : .+}")
    @Produces(MediaType.APPLICATION_JSON)
    public String query(@PathParam("query") String query,@Context HttpServletRequest request) {
        
        Locale currentLocale = LocalizationMgr.getCurrentLocale(request);
        ResourceBundle localization = ResourceBundle.getBundle(Constants.LOCALIZATION_PREFIX + "." + Constants.LOCALIZATION_SUFFIX, currentLocale);

        JSONObject json = new JSONObject();
        HashMap<Registry, String> map = new HashMap<>();

        String[] queryList = query.split("&");
        for (String queryByQuery : queryList) {
            String[] smallQueryList = queryByQuery.split("=");

            if (smallQueryList.length == 1) {
                json.put("code", Response.Status.BAD_REQUEST.getStatusCode());
                json.put("message", localization.getString("error.restful.registry.notfound.message"));
                json.put("description", localization.getString("error.restful.registry.query.notfound") + RegistryMgr.ID + ", " + RegistryMgr.URI);
                throw new NotFoundException(json.toString(2));
            }

            String variable = smallQueryList[0];
            String value = smallQueryList[1];

            if (variable.equals(RegistryMgr.URI)) {
                Registry registryFromURI = RegistryMgr.getRegistryByUri(value);
                if (registryFromURI != null && !map.containsKey(registryFromURI)) {
                    map.put(registryFromURI, "");
                }
            } else if (variable.equals(RegistryMgr.ID)) {
                Registry registryFromID = RegistryMgr.getRegistryByID(value);
                if (registryFromID != null && !map.containsKey(registryFromID)) {
                    map.put(registryFromID, "");
                }
            } else {
                json.put("code", Response.Status.BAD_REQUEST.getStatusCode());
                json.put("message", localization.getString("error.restful.registry.badrequest.message"));
                json.put("description",  localization.getString("error.restful.registry.badrequest.description") + RegistryMgr.ID + ", " + RegistryMgr.URI);
                throw new NotFoundException(json.toString(2));
            }
        }

        JSONArray array = new JSONArray();
        if (!map.isEmpty()) {
            for (Map.Entry<Registry, String> entry : map.entrySet()) {
                Registry registries = entry.getKey();

                JSONObject registryObj = addRegistryJSON(registries);

                array.put(registryObj);
            }
            json.put("registries", array);
        } else {
            json = new JSONObject();
            json.put("code", Response.Status.NOT_FOUND.getStatusCode());
            json.put("message", localization.getString("error.restful.registry.notfound.message"));
            json.put("description", localization.getString("error.restful.registry.query.notfound") + RegistryMgr.ID + ", " + RegistryMgr.URI);
            throw new NotFoundException(json.toString(2));
        }

        return json.toString(2);
    }

    private JSONObject addRegistryJSON(Registry registries) throws JSONException {
        JSONObject obj = new JSONObject();

        obj.put("id", registries.getUuid());
        obj.put("uri", registries.getUri());
        obj.put("label", registries.getLabel());
        obj.put("description", registries.getDescription());

        JSONObject pub = new JSONObject();
        pub.put("label", registries.getPublishername());
        pub.put("email", registries.getPublisheremail());
        pub.put("uri", registries.getPublisheruri());
        pub.put("homepage", registries.getPublisherhomepage());

        obj.put("publisher", pub);

        obj.put("updatefrequency", registries.getUpdatefrequency());

        JSONObject registryObj = new JSONObject();
        registryObj.put("registry", obj);

        return registryObj;
    }
}
