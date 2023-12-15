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

import eu.europa.ec.ror.utility.Configuration;
import eu.europa.ec.ror.utility.Constants;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;

public class AutomaticHarvestingMgr {

    public static Date calculateNextDate(String frequency) {
        Properties properties = Configuration.getInstance().getProperties();

        String frequencyBaseUri = properties.getProperty("application.frequencycodelist.baseuri");
        int defaultStartHour = Integer.parseInt(properties.getProperty("application.automaticharvesting.startHour","0"));
        int defaultStartMinute= Integer.parseInt(properties.getProperty("application.automaticharvesting.startMinute","1"));
        int defaultStartSecond = Integer.parseInt(properties.getProperty("application.automaticharvesting.startSecond","0"));

        frequency = frequency.replace(frequencyBaseUri, "");

        Date date = null;
        Double daysToAdd;
        switch (frequency) {
            case Constants.FREQUENCIES_ANNUAL:
                daysToAdd = Constants.FREQUENCIES_TIME_ANNUAL;
                break;
            case Constants.FREQUENCIES_ANNUAL_2:
                daysToAdd = Constants.FREQUENCIES_TIME_ANNUAL_2;
                break;
            case Constants.FREQUENCIES_ANNUAL_3:
                daysToAdd = Constants.FREQUENCIES_TIME_ANNUAL_3;
                break;
            case Constants.FREQUENCIES_BIENNIAL:
                daysToAdd = Constants.FREQUENCIES_TIME_BIENNIAL;
                break;
            case Constants.FREQUENCIES_BIMONTHLY:
                daysToAdd = Constants.FREQUENCIES_TIME_BIMONTHLY;
                break;
            case Constants.FREQUENCIES_BIWEEKLY:
                daysToAdd = Constants.FREQUENCIES_TIME_BIWEEKLY;
                break;
            case Constants.FREQUENCIES_CONT:
                daysToAdd = Constants.FREQUENCIES_TIME_CONT;
                break;
            case Constants.FREQUENCIES_DAILY:
                daysToAdd = Constants.FREQUENCIES_TIME_DAILY;
                break;
            case Constants.FREQUENCIES_DAILY_2:
                daysToAdd = Constants.FREQUENCIES_TIME_DAILY_2;
                break;
            case Constants.FREQUENCIES_IRREG:
                daysToAdd = Constants.FREQUENCIES_TIME_IRREG;
                break;
            case Constants.FREQUENCIES_MONTHLY:
                daysToAdd = Constants.FREQUENCIES_TIME_MONTHLY;
                break;
            case Constants.FREQUENCIES_MONTHLY_2:
                daysToAdd = Constants.FREQUENCIES_TIME_MONTHLY_2;
                break;
            case Constants.FREQUENCIES_MONTHLY_3:
                daysToAdd = Constants.FREQUENCIES_TIME_MONTHLY_3;
                break;
            case Constants.FREQUENCIES_NEVER:
                daysToAdd = Constants.FREQUENCIES_TIME_NEVER;
                break;
            case Constants.FREQUENCIES_OTHER:
                daysToAdd = Constants.FREQUENCIES_TIME_OTHER;
                break;
            case Constants.FREQUENCIES_QUARTERLY:
                daysToAdd = Constants.FREQUENCIES_TIME_QUARTERLY;
                break;
            case Constants.FREQUENCIES_TRIENNIAL:
                daysToAdd = Constants.FREQUENCIES_TIME_TRIENNIAL;
                break;
            case Constants.FREQUENCIES_UNKNOWN:
                daysToAdd = Constants.FREQUENCIES_TIME_UNKNOWN;
                break;
            case Constants.FREQUENCIES_UPDATE_CONT:
                daysToAdd = Constants.FREQUENCIES_TIME_UPDATE_CONT;
                break;
            case Constants.FREQUENCIES_WEEKLY:
                daysToAdd = Constants.FREQUENCIES_TIME_WEEKLY;
                break;
            case Constants.FREQUENCIES_WEEKLY_2:
                daysToAdd = Constants.FREQUENCIES_TIME_WEEKLY_2;
                break;
            case Constants.FREQUENCIES_WEEKLY_3:
                daysToAdd = Constants.FREQUENCIES_TIME_WEEKLY_3;
                break;
            default:
                daysToAdd = 0.0;
        }

        if (daysToAdd > 0) {
            Calendar c = Calendar.getInstance();
            c.setTime(new Date());
            if (daysToAdd == 0.5) {
                c.add(Calendar.HOUR_OF_DAY, 12);
            } else {
                c.add(Calendar.DATE, daysToAdd.intValue());
            }
            c.set(Calendar.HOUR_OF_DAY,defaultStartHour);
            c.set(Calendar.MINUTE,defaultStartMinute);
            c.set(Calendar.SECOND,defaultStartSecond);
            
            date = c.getTime();
        }

        return date;
    }
}
