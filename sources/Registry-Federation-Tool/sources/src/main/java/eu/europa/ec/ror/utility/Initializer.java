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
package eu.europa.ec.ror.utility;

import java.io.File;
import javax.servlet.*;
import javax.servlet.http.*;

public class Initializer extends HttpServlet {

    @Override
    public void init() throws ServletException {
        System.out.println("### Initializing the system ...");

        //Getting base configuration path
        String configurationFolder = getServletContext().getRealPath("/" + Constants.WEBINF_FOLDER_NAME) + File.separator + Constants.CLASSES_FOLDER_NAME + File.separator + Constants.CONFIGURATIONS_FOLDER_NAME;
        System.setProperty(Constants.CONFIGURATIONS_FOLDER_NAME, configurationFolder);
        System.out.println("### Configuration folder: " + configurationFolder);

        try {
            //Initializing the configurations
            Configuration.getInstance();
            System.out.println("### The system is now initialized.");

        } catch (Exception e) {
            System.out.println("### Error during system initialization: " + e.getMessage());
        }
    }

}