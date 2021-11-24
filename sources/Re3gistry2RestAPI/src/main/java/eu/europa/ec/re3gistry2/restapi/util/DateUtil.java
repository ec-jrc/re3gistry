/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.europa.ec.re3gistry2.restapi.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DateUtil {

    private static final Logger LOG = LogManager.getLogger(DateUtil.class.getName());
    
    private static DateFormat dateFormatIso8601 = new SimpleDateFormat("yyyy-MM-dd");

    public DateUtil() {
    }

    /**
     * Formats the given date according to ISO 8601.
     */
    public static String convertDate(Date date) {
      return dateFormatIso8601.format(date);
   }
}
