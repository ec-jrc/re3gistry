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

import eu.europa.ec.ror.managers.util.EntityManagerCustom;
import eu.europa.ec.ror.model.Descriptor;
import eu.europa.ec.ror.model.Organization;
import eu.europa.ec.ror.model.Register;
import eu.europa.ec.ror.model.Registry;
import eu.europa.ec.ror.model.User;
import eu.europa.ec.ror.utility.Configuration;
import eu.europa.ec.ror.utility.Constants;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

public class DescriptorMgr {

    /* Common DB operations */
    public static List<Descriptor> getAllDescriptors() {
        try {
            EntityManagerCustom emc = new EntityManagerCustom();
            EntityManager em = emc.getEntityManager();

            Query q = em.createNamedQuery("Descriptor.findAll");
            List<Descriptor> list = (List<Descriptor>) q.getResultList();

            return list;
        } catch (Exception ex) {
            return null;
        }
    }

    public static Descriptor getDescriptorByUrl(String url) {
        try {
            EntityManagerCustom emc = new EntityManagerCustom();
            EntityManager em = emc.getEntityManager();

            Query q = em.createNamedQuery("Descriptor.findByUrl");
            q.setParameter("url", url);
            Descriptor list = (Descriptor) q.getSingleResult();

            return list;
        } catch (Exception ex) {
            return null;
        }
    }

    public static Descriptor getDescriptorByID(String id) {
        try {
            EntityManagerCustom emc = new EntityManagerCustom();
            EntityManager em = emc.getEntityManager();

            Query q = em.createNamedQuery("Descriptor.findByUuid");
            q.setParameter("uuid", id);
            Descriptor list = (Descriptor) q.getSingleResult();

            return list;
        } catch (Exception ex) {
            return null;
        }
    }

    public static List<Descriptor> getDescriptorByOrganization(Organization organization) {
        try {
            EntityManagerCustom emc = new EntityManagerCustom();
            EntityManager em = emc.getEntityManager();

            Query q = em.createNamedQuery("Descriptor.findByOrganization");
            q.setParameter("organization", organization);
            List<Descriptor> list = (List<Descriptor>) q.getResultList();

            return list;
        } catch (Exception ex) {
            return null;
        }
    }

    public static List<Descriptor> getDescriptorByType(String type) {
        try {
            EntityManagerCustom emc = new EntityManagerCustom();
            EntityManager em = emc.getEntityManager();

            Query q = em.createNamedQuery("Descriptor.findByDescriptortype");
            q.setParameter("descriptortype", type);
            List<Descriptor> list = (List<Descriptor>) q.getResultList();

            return list;
        } catch (Exception ex) {
            return null;
        }
    }

    public static List<Descriptor> getDescriptorByParentDescriptor(Descriptor descriptor) {
        try {
            EntityManagerCustom emc = new EntityManagerCustom();
            EntityManager em = emc.getEntityManager();

            Query q = em.createNamedQuery("Descriptor.findByParentDescriptor");
            q.setParameter("descriptor", descriptor);
            List<Descriptor> list = (List<Descriptor>) q.getResultList();

            return list;
        } catch (Exception ex) {
            return null;
        }
    }

    public static boolean insertDescriptor(Descriptor r) throws Exception {
        EntityManagerCustom emc = new EntityManagerCustom();
        EntityManager em = emc.getEntityManager();

        emc.beginTransaction(em);

        //Checking parameters
        if (r == null) {
            throw new Exception("The Descriptor passed by parameter is null.");
        }

        if (r.getDbcreationdate() == null) {
            r.setDbcreationdate(new Date());
        }

        //Checking the DB managers
        if (em == null) {
            throw new Exception("The persistence layer is not initialized: entyty manager is null.");
        }

        //Saving the object
        em.persist(r);

        emc.commitAndCloseTransaction(em);

        return true;

    }

    public static boolean updateDescriptor(Descriptor r) throws Exception {
        EntityManagerCustom emc = new EntityManagerCustom();
        EntityManager em = emc.getEntityManager();

        emc.beginTransaction(em);

        //Checking parameters
        if (r == null) {
            throw new Exception("The Descriptor passed by parameter is null.");
        }

        //Checking the DB managers
        if (em == null) {
            throw new Exception("The persistence layer is not initialized: entyty manager is null.");
        }

        //Saving the object
        em.merge(r);

        emc.commitAndCloseTransaction(em);

        return true;

    }

    public static boolean deleteDescriptor(Descriptor d) throws Exception {
        EntityManagerCustom emc = new EntityManagerCustom();
        EntityManager em = emc.getEntityManager();

        emc.beginTransaction(em);

        //Checking parameters
        if (d == null) {
            throw new Exception("The Descriptor passed by parameter is null.");
        }

        Descriptor descriptor = em.merge(d);

        em.remove(descriptor);

        em.getEntityManagerFactory().getCache().evictAll();

        emc.commitTransaction(em);

        return true;
    }

    /* Specific operations on Descriptors */
    /**
     * Adds the Descriptor to the DB
     *
     * @param url The URL of the Descriptor
     * @param descriptorType The type of descriptor
     * @param organization The organization from which it belongs
     * @param user The user that insert the Descriptor
     * @param parentDescriptor
     * @param localization The ResourceBundle with the locales
     * @return The Descriptor Object
     * @throws Exception In case of error, it raises the an exception with the
     * localized error message
     */
    public static Descriptor addDescriptor(String url, String descriptorType, Organization organization, User user, Descriptor parentDescriptor, ResourceBundle localization) throws Exception {

//        Logger log = Logger.getLogger(Constants.MODULE_NAME);
        Logger log = Configuration.getInstance().getLogger();

        // Parameter check
        if (url != null && url.length() > 0 && organization != null && user != null) {

            // Checking the URL (if it can resolve the resource)
            checkURL(url, localization, false);
            // Note: if there is a redirect, in the DB the original URL provided
            // by the user is stored

            //Instantiating variables
            Descriptor descriptor;

            // Check if the URL is already available in the DB
            descriptor = DescriptorMgr.getDescriptorByUrl(url);

            // Raise an error if the URL is already available in the DB
            if (descriptor != null) {
                String errorMessage = localization.getString("error.url.available");
                log.error(errorMessage);
                throw new Exception(errorMessage);
            }

            // Descriptor object creation
            descriptor = new Descriptor();
            descriptor.setUuid(descriptor.createUUID(url));
            descriptor.setUrl(url);
            descriptor.setOrganization(organization);
            descriptor.setAddedby(user);
            descriptor.setDescriptortype(descriptorType);

            if (parentDescriptor != null) {
                descriptor.setParentdescriptor(parentDescriptor);
            }

            try {
                //Saving the descriptor to the database
                DescriptorMgr.insertDescriptor(descriptor);
                return descriptor;

            } catch (Exception e) {
                // Raise an error if the insert procedure went wrong
                String errorMessage = localization.getString("error.insert.descriptor");
                log.error(errorMessage);
                throw new Exception(errorMessage);
            }

        } else {
            String errorMessage = localization.getString("error.missing.parameter");
            String additionalInfo = "";
            if (url == null || url.length() <= 0) {
                additionalInfo += "url ";
            }
            if (organization == null) {
                additionalInfo += "organization ";
            }
            if (user == null) {
                additionalInfo += "user ";
            }

            errorMessage = errorMessage.replace("{0}", additionalInfo);

            log.error(errorMessage);
            throw new Exception(errorMessage);
        }
    }

    /**
     * Checks the URL It tries to connect and checks if the HTTP code is 200.
     *
     * @param inurl The URL to be checked
     * @param localization The ResourceBundle with the locales
     * @param getResource
     * @return
     * @throws Exception In case of error, it raises the an exception with the
     * localized error message
     */
    public static StringBuilder checkURL(String inurl, ResourceBundle localization, boolean getResource) throws Exception {

        Logger log = Configuration.getInstance().getLogger();
        String outUrl = inurl;
        try {
            URL url = new URL(inurl);

            // Getting the content negotiation accept string
            Properties properties = Configuration.getInstance().getProperties();
            String negotiationAccept = properties.getProperty("application.negotiation.content.type", "application/x-ror-rdf+xml");

            HttpURLConnection.setFollowRedirects(true);

            // Trying to get the resource using the Content Negotiation
            HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
            urlConn.setRequestProperty("Accept", negotiationAccept);
            urlConn.connect();

            int status = urlConn.getResponseCode();
            boolean redirect = false;

            // If the HTTP code is different from 200, 
            if (status != HttpURLConnection.HTTP_OK) {

                // If it is a redirect, handling the redirect
                if (status == HttpURLConnection.HTTP_MOVED_TEMP
                        || status == HttpURLConnection.HTTP_MOVED_PERM
                        || status == HttpURLConnection.HTTP_SEE_OTHER) {

                    redirect = true;

                } else {
                    // Trying to get the resource without the Content Negotiation

                    // Disconnecting the currend request
                    urlConn.disconnect();
                    // Preparing the new request
                    urlConn = (HttpURLConnection) url.openConnection();
                    urlConn.connect();

                    status = urlConn.getResponseCode();

                    if (status != HttpURLConnection.HTTP_OK) {

                        // Check if it is a redirect
                        if (status == HttpURLConnection.HTTP_MOVED_TEMP
                                || status == HttpURLConnection.HTTP_MOVED_PERM
                                || status == HttpURLConnection.HTTP_SEE_OTHER) {

                            redirect = true;

                        } else {
                            // If there is still an error, the descriptor check is failed
                            urlConn.disconnect();
                            String errorMessage = localization.getString("error.url.noresolve");
                            log.error(errorMessage);
                            throw new Exception(errorMessage);
                        }
                    }
                }
            }

            if (redirect) {
                // Handling the redirect
                while (status == HttpURLConnection.HTTP_MOVED_TEMP
                        || status == HttpURLConnection.HTTP_MOVED_PERM
                        || status == HttpURLConnection.HTTP_SEE_OTHER) {

                    urlConn.disconnect();

                    // get redirect url from "location" header field
                    String newUrl = urlConn.getHeaderField("Location");

                    // open the new connnection again
                    urlConn = (HttpURLConnection) new URL(newUrl).openConnection();

                    urlConn.connect();

                    outUrl = newUrl;
                    status = urlConn.getResponseCode();

                }
            }

            InputStream is;
            StringBuilder sb = null;
            BufferedReader bufferedReader;
            if (getResource) {
                is = urlConn.getInputStream();
                bufferedReader = new BufferedReader(new InputStreamReader(is, Constants.INPUT_STREAM_DEFAULT_CHARSET));
                int charRead = 0;
                char[] buffer = new char[1024];
                sb = new StringBuilder();
                while ((charRead = bufferedReader.read(buffer)) > 0) {
                    sb.append(buffer, 0, charRead);
                }
                is.close();
            }

            urlConn.disconnect();

            return sb;

        } catch (Exception e) {
            String errorMessage = localization.getString("error.url.noresolve");
            log.error(errorMessage);
            throw new Exception(errorMessage);
        }
    }

    /**
     * Removes a Descriptor from the DB
     *
     * @param id The ID of the Descriptor
     * @param localization
     * @throws Exception In case of error, it raises the an exception with the
     * localized error message
     */
    public static void removeRegistryDescriptor(String id, ResourceBundle localization) throws Exception {
        // Checking the ID
        if (id != null && id.length() > 0) {

            // Getting the descriptor object
            Descriptor descriptor = DescriptorMgr.getDescriptorByID(id);

            if (descriptor != null) {

                Registry registry = RegistryMgr.getRegistryByDescriptor(descriptor);

                //Removing related document from Solr indes
                SolrMgr.removeDocsFromSolr(registry, localization);

                //Removing the Registry descriptor and related          
                DescriptorMgr.deleteDescriptor(descriptor);

            } else {
                throw new Exception(localization.getString("error.delete.descriptor.notavailable"));
            }
        }
    }

    /**
     *
     * @param availableRegisterDescriptors
     * @param registers
     * @param localization
     * @throws Exception Remove the Registers that have been removed from the
     * Registry descriptor (Available in the DB but not available any more in
     * the Registry descriptor).
     */
    public static String checkAndRemoveRegisterDescriptor(List<Descriptor> availableRegisterDescriptors, JSONArray registers, ResourceBundle localization) throws Exception {
        String outstr = "";

        for (Descriptor tmpDescriptor : availableRegisterDescriptors) {
            int found = 0;
            for (int i = 0; i < registers.length(); i++) {
                JSONObject tmp = registers.getJSONObject(i).getJSONObject("register");
                if (tmp != null) {
                    String tmpURL = tmp.getString("registerUrl");
                    found += (tmpURL.equals(tmpDescriptor.getUrl())) ? 1 : 0;
                }
            }

            if (found == 0) {
                // Removing the items related to this register from the Solr index
                Register register = RegisterMgr.getRegisterByDescriptor(tmpDescriptor);
                SolrMgr.removeDocsFromSolr(register, localization);

                // Removing the register descriptor and related items
                DescriptorMgr.deleteDescriptor(tmpDescriptor);

                // Updating the output string (for email info)
                outstr += "<li>" + register.getUri() + "</li>";
            }
        }

        return outstr;
    }
}
