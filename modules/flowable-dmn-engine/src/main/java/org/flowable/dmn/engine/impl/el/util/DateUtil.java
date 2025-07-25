/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.flowable.dmn.engine.impl.el.util;

import java.time.ZoneId;
import java.util.Date;

import org.flowable.common.engine.impl.joda.JodaDeprecationLogger;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * @author Yvo Swillens
 */
public class DateUtil {

    public static Date toDate(Object dateObject) {
        if (dateObject == null) {
            throw new IllegalArgumentException("date object cannot be empty");
        }

        if (dateObject instanceof Date) {
            return (Date) dateObject;
        } else if (dateObject instanceof LocalDate) {
            JodaDeprecationLogger.LOGGER.warn("Using Joda-Time LocalDate has been deprecated and will be removed in a future version.");
            return ((LocalDate) dateObject).toDate();
        } else if (dateObject instanceof java.time.LocalDate) {
            return Date.from(((java.time.LocalDate) dateObject).atStartOfDay()
                    .atZone(ZoneId.systemDefault())
                    .toInstant());
        } else {
            DateTimeFormatter dtf = DateTimeFormat.forPattern("yyyy-MM-dd");
            LocalDate dateTime = dtf.parseLocalDate((String) dateObject);
            return dateTime.toDate();
        }
    }

    public static Date addDate(Object startDate, Object years, Object months, Object days) {
        LocalDate currentDate = new LocalDate(startDate);
        
        currentDate = currentDate.plusYears(intValue(years));
        currentDate = currentDate.plusMonths(intValue(months));
        currentDate = currentDate.plusDays(intValue(days));

        return currentDate.toDate();
    }

    public static Date subtractDate(Object startDate, Object years, Object months, Object days) {
        LocalDate currentDate = new LocalDate(startDate);

        currentDate = currentDate.minusYears(intValue(years));
        currentDate = currentDate.minusMonths(intValue(months));
        currentDate = currentDate.minusDays(intValue(days));

        return currentDate.toDate();
    }

    public static Date now() {
        return new LocalDate().toDate();
    }
    
    protected static Integer intValue(Object value) {
        Integer intValue = null;
        if (value instanceof Integer) {
            intValue = (Integer) value;
        } else {
            intValue = Integer.valueOf(value.toString());
        }
        
        return intValue;
    }
}
