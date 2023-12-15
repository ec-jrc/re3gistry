/*
 * Copyright 2010,2016 EUROPEAN UNION
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
 * inspire-registry-info@jrc.ec.europa.eu
 *
 * This work was supported by the EU  Interoperability Solutions for
 * European Public Administrations Programme (http://ec.europa.eu/isa)
 * through Action 1.17: Re-usable INSPIRE Reference Platform 
 */
package eu.europa.ec.ror.managers;

import eu.europa.ec.ror.model.Register;
import eu.europa.ec.ror.model.Registry;
import eu.europa.ec.ror.utility.Configuration;
import eu.europa.ec.ror.utility.Constants;
import java.net.ProxySelector;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.SystemDefaultRoutePlanner;
import org.apache.logging.log4j.Logger;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.common.SolrInputDocument;
import org.json.JSONArray;
import org.json.JSONObject;

public class SolrMgr {

    public static void indexDocs(JSONObject registerJsonObject, ResourceBundle localization, Register register) throws Exception {
         Logger log = Configuration.getInstance().getLogger();

        try {
            Properties properties = Configuration.getInstance().getProperties();
            SolrServer server = null;

            // Connection to Solr
            String solrUrl = properties.getProperty("solr.url");
            HttpClient client = HttpClients.custom().setRoutePlanner(new SystemDefaultRoutePlanner(ProxySelector.getDefault())).create().build();
            server = new HttpSolrServer(solrUrl, client);

            // Getting items from registerJsonObject
            JSONArray jsonItems = registerJsonObject.getJSONArray("items");

            if (jsonItems != null && jsonItems.length() > 0) {
                // Creating the list of onjects to be indexed in Solr
                Collection<SolrInputDocument> docs = new ArrayList<>();
                for (int i = 0; i < jsonItems.length(); i++) {
                    JSONObject tmp = jsonItems.getJSONObject(i).getJSONObject("item");

                    SolrInputDocument doc = new SolrInputDocument();
                    doc.addField("id", tmp.getString("itemUri"));
                    doc.addField("label", tmp.getString("itemLabel"));
                    doc.addField("definition", tmp.getString("itemDefinition"));
                    doc.addField("status", tmp.getString("itemStatus"));
                    doc.addField("inscheme", tmp.getString("itemInScheme"));
                    doc.addField("type", Constants.TYPE_ITEM);
                    doc.addField("registryname", register.getRegistry().getLabel());
                    doc.addField("registryuri", register.getRegistry().getUri());
                    doc.addField("registername", register.getLabel());
                    doc.addField("registeruri", register.getUri());

                    docs.add(doc);
                }

                server.add(docs);
                server.commit();
                server.shutdown();
            } else {
                server.shutdown();
            }

        } catch (Exception e) {
            // Getting the erroro message
            String errorMessage = localization.getString("error.solr.indexing").replace("{0}", e.getMessage());

            // Raise the exception
            log.error(errorMessage);
            throw new Exception(errorMessage);
        }
    }

    public static void indexRegistry(Registry registry, ResourceBundle localization) throws Exception {
        Logger log = Configuration.getInstance().getLogger();

        try {
            Properties properties = Configuration.getInstance().getProperties();
            SolrServer server = null;

            // Connection to Solr
            String solrUrl = properties.getProperty("solr.url");
            HttpClient client = HttpClients.custom().setRoutePlanner(new SystemDefaultRoutePlanner(ProxySelector.getDefault())).create().build();
            server = new HttpSolrServer(solrUrl, client);

            if (registry != null) {
                // Indexing Registry in Solr

                SolrInputDocument doc = new SolrInputDocument();
                doc.addField("id", registry.getUri());
                doc.addField("label", registry.getLabel());
                doc.addField("definition", registry.getDescription());
                doc.addField("status", "");
                doc.addField("inscheme", "");
                doc.addField("type", registry.getDescriptor().getDescriptortype());

                server.add(doc);
                server.commit();
                server.shutdown();
            } else {
                server.shutdown();
            }

        } catch (Exception e) {
            // Getting the erroro message
            String errorMessage = localization.getString("error.solr.indexing").replace("{0}", e.getMessage());

            // Raise the exception
            log.error(errorMessage);
            throw new Exception(errorMessage);
        }
    }

    public static void indexRegister(Register register, ResourceBundle localization) throws Exception {
         Logger log = Configuration.getInstance().getLogger();

        try {
            Properties properties = Configuration.getInstance().getProperties();
            SolrServer server = null;

            // Connection to Solr
            String solrUrl = properties.getProperty("solr.url");
            HttpClient client = HttpClients.custom().setRoutePlanner(new SystemDefaultRoutePlanner(ProxySelector.getDefault())).create().build();
            server = new HttpSolrServer(solrUrl, client);

            if (register != null) {
                // Indexing Registry in Solr

                SolrInputDocument doc = new SolrInputDocument();
                doc.addField("id", register.getUri());
                doc.addField("label", register.getLabel());
                doc.addField("definition", register.getDescription());
                doc.addField("status", "");
                doc.addField("inscheme", "");
                doc.addField("type", register.getDescriptor().getDescriptortype());
                doc.addField("registryname", register.getRegistry().getLabel());
                doc.addField("registryuri", register.getRegistry().getUri());

                server.add(doc);
                server.commit();
                server.shutdown();
            } else {
                server.shutdown();
            }

        } catch (Exception e) {
            // Getting the erroro message
            String errorMessage = localization.getString("error.solr.indexing").replace("{0}", e.getMessage());

            // Raise the exception
            log.error(errorMessage);
            throw new Exception(errorMessage);
        }
    }

    public static void removeDocsFromSolr(Registry registry, ResourceBundle localization) throws Exception {
         Logger log = Configuration.getInstance().getLogger();

        try {
            Properties properties = Configuration.getInstance().getProperties();
            SolrServer server = null;

            // Connection to Solr
            String solrUrl = properties.getProperty("solr.url");
            HttpClient client = HttpClients.custom().setRoutePlanner(new SystemDefaultRoutePlanner(ProxySelector.getDefault())).create().build();
            server = new HttpSolrServer(solrUrl, client);

            // Getting the related registers
            List<Register> registers = RegisterMgr.getAllRegistersByRegistry(registry);

            for (Register r : registers) {

                // Delete the items entry for the current register
                server.deleteByQuery("inscheme:\"" + r.getUri() + "\"");

                // Delete the register entry
                server.deleteById(r.getUri());

                server.commit();
            }

            // Delete the registry entry
            if (registry != null) {
                server.deleteById(registry.getUri());
            }

            server.commit();
            server.shutdown();

        } catch (Exception e) {
            // Getting the erroro message
            String errorMessage = localization.getString("error.solr.deleting").replace("{0}", e.getMessage());

            // Raise the exception
            log.error(errorMessage);
            throw new Exception(errorMessage);
        }

    }

    public static void removeDocsFromSolr(Register register, ResourceBundle localization) throws Exception {
         Logger log = Configuration.getInstance().getLogger();

        try {
            Properties properties = Configuration.getInstance().getProperties();
            SolrServer server = null;

            // Connection to Solr
            String solrUrl = properties.getProperty("solr.url");
            HttpClient client = HttpClients.custom().setRoutePlanner(new SystemDefaultRoutePlanner(ProxySelector.getDefault())).create().build();
            server = new HttpSolrServer(solrUrl, client);

            if (register != null) {
                // Delete related items
                server.deleteByQuery("inscheme:\"" + register.getUri() + "\"");

                // Delete the register entry
                server.deleteById(register.getUri());
            }

            server.commit();
            server.shutdown();

        } catch (Exception e) {
            // Getting the error message
            String errorMessage = localization.getString("error.solr.deleting").replace("{0}", e.getMessage());

            // Raise the exception
            log.error(errorMessage);
            throw new Exception(errorMessage);
        }

    }
}
