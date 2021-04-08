/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.europa.ec.re3gistry2.restapi.util;

import eu.europa.ec.re3gistry2.restapi.format.RDFFormatter;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DateUtil {

    private static final Logger LOG = LogManager.getLogger(DateUtil.class.getName());

    public DateUtil() {
    }

   public static String convertDate(Date date) {
        try {
            DateFormat f = new SimpleDateFormat("E MMM dd HH:mm:ss zzz yyyy");
            Date d = f.parse(date.toString());
            DateFormat out = new SimpleDateFormat("YYYY-DD-MM");
            return out.format(d);

        } catch (ParseException ex) {
            java.util.logging.Logger.getLogger(RDFFormatter.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
