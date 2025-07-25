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
package org.flowable.common.engine.impl.calendar;

import java.time.Duration;
import java.time.Period;
import java.time.ZonedDateTime;
import java.util.Date;

import org.flowable.common.engine.api.FlowableException;
import org.flowable.common.engine.impl.runtime.ClockReader;
import org.flowable.common.engine.impl.util.DateUtil;

public class DueDateBusinessCalendar extends BusinessCalendarImpl {

    public static final String NAME = "dueDate";

    public DueDateBusinessCalendar(ClockReader clockReader) {
        super(clockReader);
    }

    @Override
    public Date resolveDuedate(String duedate, int maxIterations) {
        try {
            // check if due period was specified
            if (duedate.startsWith("P")) {
                ZonedDateTime calculateTime = clockReader.getCurrentTime()
                        .toInstant()
                        .atZone(clockReader.getCurrentTimeZone().toZoneId());
                Period period;
                Duration duration;
                if (duedate.startsWith("PT")) {
                    period = Period.ZERO;
                    duration = Duration.parse(duedate);
                } else {
                    int timeIndex = duedate.indexOf('T');
                    if (timeIndex > 0) {
                        period = Period.parse(duedate.substring(0, timeIndex));
                        duration = Duration.parse("P" + duedate.substring(timeIndex));
                    } else {
                        period = Period.parse(duedate);
                        duration = Duration.ZERO;
                    }
                }

                return Date.from(calculateTime.plus(period).plus(duration).toInstant());
            }

            return DateUtil.parseDate(duedate);

        } catch (Exception e) {
            throw new FlowableException("couldn't resolve duedate: " + e.getMessage(), e);
        }
    }
}
