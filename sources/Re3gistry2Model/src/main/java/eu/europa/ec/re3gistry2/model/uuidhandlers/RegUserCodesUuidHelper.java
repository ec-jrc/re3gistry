/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package eu.europa.ec.re3gistry2.model.uuidhandlers;

import eu.europa.ec.re3gistry2.model.RegUserCodes;
import eu.europa.ec.re3gistry2.model.utility.UuidHelper;

/**
 *
 * @author oriol
 */
public class RegUserCodesUuidHelper {
    private RegUserCodesUuidHelper(){
    }
    
    public static String getUuid(String user, String action) throws Exception{

        return UuidHelper.createUuid(action+user, RegUserCodes.class);
    } 
}
