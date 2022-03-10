/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.europa.ec.re3gistry2.javaapi.cache;

import eu.europa.ec.re3gistry2.base.utility.BaseConstants;
import eu.europa.ec.re3gistry2.base.utility.Configuration;
import java.io.File;
import java.io.IOException;

public class CacheHelper {

    public CacheHelper() {
    }

    public static boolean checkCacheCompleteRunning() {
        boolean running = false;
        try {
            String systemCacheallPath = Configuration.getPathHelperFiles() + File.separator + BaseConstants.KEY_FILE_NAME_CACHEALL_COMPLETE_RUNNING;
            File systemInstalledFile = new File(systemCacheallPath);
            if (systemInstalledFile.exists() && !systemInstalledFile.isDirectory()) {
                running = true;
            }

        } catch (Exception e) {
        }
        return running;
    }

    public static void createCacheCompleteRunningFile() throws Exception {
        String systemCacheallPath = Configuration.getPathHelperFiles() + File.separator + BaseConstants.KEY_FILE_NAME_CACHEALL_COMPLETE_RUNNING;
        File systemInstalledFile = new File(systemCacheallPath);

        systemInstalledFile.getParentFile().mkdirs();
        try {

            boolean success = systemInstalledFile.createNewFile();

            if (!success) {
                //throw new IOException();
            }

        } catch (IOException ex) {
        }
    }

    public static void deleteCacheCompleteRunningFile() {
        String systemCacheallPath = Configuration.getPathHelperFiles() + File.separator + BaseConstants.KEY_FILE_NAME_CACHEALL_COMPLETE_RUNNING;

        try {
            File file = new File(systemCacheallPath);

            boolean success = file.delete();

            if (!success) {
                //throw new IOException();
            }
        } catch (Exception ex) {
        }
    }

}
