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

import eu.europa.ec.ror.managers.RegisterMgr;
import eu.europa.ec.ror.model.Register;
import eu.europa.ec.ror.model.Registry;
import eu.europa.ec.ror.utility.Constants;
import eu.europa.ec.ror.utility.exception.NotFoundException;
import eu.europa.ec.ror.utility.localization.LocalizationMgr;
import java.util.List;
import java.util.HashMap;
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

@Path("registers")
public class Registers {

    /**
     * GET all the registers
     *
     * @param request
     * @return a JSON containing all the registers
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String list(@Context HttpServletRequest request) {

        Locale currentLocale = LocalizationMgr.getCurrentLocale(request);
        ResourceBundle localization = ResourceBundle.getBundle(Constants.LOCALIZATION_PREFIX + "." + Constants.LOCALIZATION_SUFFIX, currentLocale);

        List<Register> list = RegisterMgr.getAllRegisters();

        JSONObject json = new JSONObject();
        if (!list.isEmpty()) {
            JSONArray array = new JSONArray();
            for (Register registers : list) {
                JSONObject registerObj = addJSONObjectFromRegister(registers);
                array.put(registerObj);
            }
            json.put("registers", array);
        } else {
            json.put("code", Response.Status.NOT_FOUND.getStatusCode());
            json.put("message", localization.getString("error.restful.register.notfound.message"));
            json.put("description", localization.getString("error.restful.register.notfound.description"));
            throw new NotFoundException(json.toString(2));
        }

        return json.toString(2);
    }

    /**
     * GET the registers detail by ID
     *
     * @param id
     * @param request
     * @return a JSON of the Register with the id ID
     */
    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public String id(@PathParam("id") String id, @Context HttpServletRequest request) {

        Locale currentLocale = LocalizationMgr.getCurrentLocale(request);
        ResourceBundle localization = ResourceBundle.getBundle(Constants.LOCALIZATION_PREFIX + "." + Constants.LOCALIZATION_SUFFIX, currentLocale);

        JSONObject obj = new JSONObject();

        Register registers = RegisterMgr.getRegisterByID(id);

        if (registers != null) {
            obj = addJSONObjectFromRegister(registers);
        } else {
            obj.put("code", Response.Status.NOT_FOUND.getStatusCode());
            obj.put("message", localization.getString("error.restful.register.notfound.message"));
            obj.put("description", localization.getString("error.restful.register.item.notfound").replace("{0}", id));
            throw new NotFoundException(obj.toString(2));
        }

        return obj.toString(2);
    }

    /**
     * GET the registers detail by query
     *
     * @param query
     * @return a JSON of the Register with the query
     */
    @GET
    @Path("q/{query : .+}")
    @Produces(MediaType.APPLICATION_JSON)
    public String query(@PathParam("query") String query, @Context HttpServletRequest request) {

        Locale currentLocale = LocalizationMgr.getCurrentLocale(request);
        ResourceBundle localization = ResourceBundle.getBundle(Constants.LOCALIZATION_PREFIX + "." + Constants.LOCALIZATION_SUFFIX, currentLocale);

        JSONObject json = new JSONObject();
        HashMap<Register, String> map = new HashMap<>();

        String[] queryList = query.split("&");
        for (String queryByQuery : queryList) {
            String[] smallQueryList = queryByQuery.split("=");

            if (smallQueryList.length == 1) {
                json.put("code", Response.Status.BAD_REQUEST.getStatusCode());
                json.put("message", localization.getString("error.restful.register.notfound.message"));
                json.put("description", localization.getString("error.restful.register.query.notfound") + RegisterMgr.ID + ", " + RegisterMgr.URI + ", " + RegisterMgr.REGISTRY_ID + ", " + RegisterMgr.REGISTRY_URI);
                throw new NotFoundException(json.toString(2));
            }

            String variable = smallQueryList[0];
            String value = smallQueryList[1];

            if (variable.equals(RegisterMgr.REGISTRY_ID)) {
                List<Register> managers = RegisterMgr.getAllRegistersByRegistryID(value);
                for (Register registry : managers) {
                    if (!map.containsKey(registry)) {
                        map.put(registry, "");
                    }
                }
            } else if (variable.equals(RegisterMgr.REGISTRY_URI)) {
                List<Register> contactpoint = RegisterMgr.getAllRegistersByRegistryUri(value);
                for (Register registry : contactpoint) {
                    if (!map.containsKey(registry)) {
                        map.put(registry, "");
                    }
                }
            } else if (variable.equals(RegisterMgr.URI)) {
                Register registerFromURI = RegisterMgr.getRegisterByUri(value);
                if (registerFromURI != null && !map.containsKey(registerFromURI)) {
                    map.put(registerFromURI, "");
                }
            } else if (variable.equals(RegisterMgr.ID)) {
                Register registryFromID = RegisterMgr.getRegisterByID(value);
                if (registryFromID != null && !map.containsKey(registryFromID)) {
                    map.put(registryFromID, "");
                }
            } else {
                json.put("code", Response.Status.BAD_REQUEST.getStatusCode());
                json.put("message", localization.getString("error.restful.register.badrequest.message"));
                json.put("description", localization.getString("error.restful.register.badrequest.description") + RegisterMgr.ID + ", " + RegisterMgr.URI + ", " + RegisterMgr.REGISTRY_ID + ", " + RegisterMgr.REGISTRY_URI);
                throw new NotFoundException(json.toString(2));
            }
        }

        JSONArray array = new JSONArray();
        if (!map.isEmpty()) {
            for (Map.Entry<Register, String> entry : map.entrySet()) {
                Register registers = entry.getKey();

                JSONObject registerObj = addJSONObjectFromRegister(registers);
                array.put(registerObj);
            }
            json.put("registers", array);
        } else {
            json.put("code", Response.Status.NOT_FOUND.getStatusCode());
            json.put("message", localization.getString("error.restful.register.notfound.message"));
            json.put("description", localization.getString("error.restful.register.query.notfound") + RegisterMgr.ID + ", " + RegisterMgr.URI + ", " + RegisterMgr.REGISTRY_ID + ", " + RegisterMgr.REGISTRY_URI);
            throw new NotFoundException(json.toString(2));
        }

        return json.toString(2);
    }

    private JSONObject addJSONObjectFromRegister(Register registers) throws JSONException {
        JSONObject obj = new JSONObject();

        Registry registry = registers.getRegistry();
        JSONArray registryArray = new JSONArray();
        JSONObject registryobj = new JSONObject();

        registryobj.put("id", registry.getUuid());
        registryobj.put("uri", registry.getUri());
        registryobj.put("label", registry.getLabel());
        registryobj.put("description", registry.getDescription());

        JSONObject pubr = new JSONObject();
        pubr.put("label", registry.getPublishername());
        pubr.put("email", registry.getPublisheremail());
        pubr.put("uri", registry.getPublisheruri());
        pubr.put("homepage", registry.getPublisherhomepage());

        registryobj.put("publisher", pubr);

        registryobj.put("updatefrequency", registry.getUpdatefrequency());

        registryArray.put(registryobj);

        obj.put("id", registers.getUuid());
        obj.put("uri", registers.getUri());
        obj.put("registry", registryobj);
        obj.put("label", registers.getLabel());
        obj.put("description", registers.getDescription());

        JSONObject pub = new JSONObject();
        pub.put("label", registers.getPublishername());
        pub.put("email", registers.getPublisheremail());
        pub.put("uri", registers.getPublisheruri());
        pub.put("homepage", registers.getPublisherhomepage());

        obj.put("publisher", pub);

        obj.put("updatefrequency", registers.getUpdatefrequency());

        JSONObject registerObj = new JSONObject();
        registerObj.put("register", obj);

        return registerObj;
    }

}
