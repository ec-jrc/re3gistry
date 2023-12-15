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

import eu.europa.ec.ror.model.Procedure;
import eu.europa.ec.ror.utility.Configuration;
import eu.europa.ec.ror.utility.Constants;
import java.util.ResourceBundle;
import org.apache.logging.log4j.Logger;

public class HarvestingMgr implements Runnable {

    private Thread thread;
    private Procedure procedure;
    private ResourceBundle localization;
    private final Logger log;

    public HarvestingMgr(Procedure procedure, ResourceBundle localization) {
        this.procedure = procedure;
        this.log = Configuration.getInstance().getLogger();
        this.localization = localization;
    }

    @Override
    public void run() {
        this.log.info("Runnin thread for: " + this.procedure.getDescriptor().getUrl());

        // Checking the descriptor type
        if (this.procedure.getDescriptor().getDescriptortype().equals(Constants.DESCRIPTOR_TYPE_REGISTRY)) {

            //Run the Registry descriptor harvesting
            RegistryMgr.harvestRegistry(this.procedure, this.localization);

        } else if (this.procedure.getDescriptor().getDescriptortype().equals(Constants.DESCRIPTOR_TYPE_REGISTER)) {

            //Run the Register descriptor harvesting
            RegisterMgr.harvestRegister(this.procedure, this.localization);
        }
        
        // Process any eventual procedure in queue 
        ProcedureMgr.processQueue(localization,this.procedure.getStartedby());

        this.log.info("Exiting thread for: " + this.procedure.getDescriptor().getUrl());
    }

    public void start() {
        this.log.info("Starting thread: " + this.procedure.getDescriptor().getUrl());
        if (this.thread == null) {
            this.thread = new Thread(this);
            
            // Start an harvesting thread
            this.thread.start();
        }
    }
}
