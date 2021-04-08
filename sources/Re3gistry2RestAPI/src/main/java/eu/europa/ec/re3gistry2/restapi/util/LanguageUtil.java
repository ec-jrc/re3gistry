/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.europa.ec.re3gistry2.restapi.util;

import eu.europa.ec.re3gistry2.base.utility.PersistenceFactory;
import eu.europa.ec.re3gistry2.crudimplementation.RegLanguagecodeManager;
import eu.europa.ec.re3gistry2.model.RegLanguagecode;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LanguageUtil {

    private static final Logger LOG = LogManager.getLogger(LanguageUtil.class.getName());
    private EntityManagerFactory emf = null;
    private EntityManager em = null;

    public LanguageUtil() {
        try {
            this.emf = PersistenceFactory.getEntityManagerFactory();
            this.em = emf.createEntityManager();
        } catch (Exception e) {
            LOG.error("Unexpected exception occured: cannot load the configuration system", e);
        }
    }

    public List<RegLanguagecode> getLanguageList() throws Exception {
        RegLanguagecodeManager languageManager = new RegLanguagecodeManager(em);
        List<RegLanguagecode> languageList = languageManager.getAll();
        return languageList;
    }
}
