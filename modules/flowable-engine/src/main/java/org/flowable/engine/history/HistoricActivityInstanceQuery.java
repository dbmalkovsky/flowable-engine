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

package org.flowable.engine.history;

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.flowable.common.engine.api.query.DeleteQuery;
import org.flowable.common.engine.api.query.Query;

/**
 * Programmatic querying for {@link HistoricActivityInstance}s.
 * 
 * @author Tom Baeyens
 * @author Joram Barrez
 * @author Zheng Ji
 */
public interface HistoricActivityInstanceQuery extends Query<HistoricActivityInstanceQuery, HistoricActivityInstance>, DeleteQuery<HistoricActivityInstanceQuery, HistoricActivityInstance> {

    /**
     * Only select historic activity instances with the given id (primary key within history tables).
     */
    HistoricActivityInstanceQuery activityInstanceId(String activityInstanceId);

    /**
     * Only select historic activity instances with the given process instance. {@link org.flowable.engine.runtime.ProcessInstance} ids and {@link HistoricProcessInstance} ids match.
     */
    HistoricActivityInstanceQuery processInstanceId(String processInstanceId);

    /** Only select historic activity instances for the given process definition */
    HistoricActivityInstanceQuery processDefinitionId(String processDefinitionId);

    /** Only select historic activity instances for the given execution */
    HistoricActivityInstanceQuery executionId(String executionId);

    /**
     * Only select historic activity instances for the given activity (id from BPMN 2.0 XML)
     */
    HistoricActivityInstanceQuery activityId(String activityId);

    /**
     * Only select historic activity instances for activities with the given name
     */
    HistoricActivityInstanceQuery activityName(String activityName);

    /**
     * Only select historic activity instances for activities with the given activity type
     */
    HistoricActivityInstanceQuery activityType(String activityType);

    /**
     * Only select historic activity instances whose activity type is in the given set of activity types.
     */
    HistoricActivityInstanceQuery activityTypes(Set<String>  activityTypes);

    /**
     * Only select historic activity instances for userTask activities assigned to the given user
     */
    HistoricActivityInstanceQuery taskAssignee(String userId);

    /**
     * Only select historic activity instances for userTask activities completed by the given user
     */
    HistoricActivityInstanceQuery taskCompletedBy(String userId);

    /** Only select historic activity instances that are finished. */
    HistoricActivityInstanceQuery finished();

    /** Only select historic activity instances that are not finished yet. */
    HistoricActivityInstanceQuery unfinished();

    /** Only select historic activity instances that were started before the given date. */
    HistoricActivityInstanceQuery startedBefore(Date date);

    /** Only select historic activity instances that were started after the given date. */
    HistoricActivityInstanceQuery startedAfter(Date date);

    /** Only select historic activity instances that were started before the given date. */
    HistoricActivityInstanceQuery finishedBefore(Date date);

    /** Only select historic activity instances that were started after the given date. */
    HistoricActivityInstanceQuery finishedAfter(Date date);

    /** Only select historic activity instances with a specific delete reason. */
    HistoricActivityInstanceQuery deleteReason(String deleteReason);

    /** Only select historic activity instances with a delete reason that matches the provided parameter. */
    HistoricActivityInstanceQuery deleteReasonLike(String deleteReasonLike);

    /** Only select historic activity instances that have the given tenant id. */
    HistoricActivityInstanceQuery activityTenantId(String tenantId);

    /** Only select historic activity instances with one of the given tenant ids. */
    HistoricActivityInstanceQuery tenantIdIn(List<String> tenantIds);

    /**
     * Only select historic activity instances with a tenant id like the given one.
     */
    HistoricActivityInstanceQuery activityTenantIdLike(String tenantIdLike);

    /** Only select historic activity instances that do not have a tenant id. */
    HistoricActivityInstanceQuery activityWithoutTenantId();

    // ordering
    // /////////////////////////////////////////////////////////////////
    /** Order by id (needs to be followed by {@link #asc()} or {@link #desc()}). */
    HistoricActivityInstanceQuery orderByHistoricActivityInstanceId();

    /**
     * Order by processInstanceId (needs to be followed by {@link #asc()} or {@link #desc()}).
     */
    HistoricActivityInstanceQuery orderByProcessInstanceId();

    /**
     * Order by executionId (needs to be followed by {@link #asc()} or {@link #desc()}).
     */
    HistoricActivityInstanceQuery orderByExecutionId();

    /**
     * Order by activityId (needs to be followed by {@link #asc()} or {@link #desc()}).
     */
    HistoricActivityInstanceQuery orderByActivityId();

    /**
     * Order by activityName (needs to be followed by {@link #asc()} or {@link #desc()}).
     */
    HistoricActivityInstanceQuery orderByActivityName();

    /**
     * Order by activityType (needs to be followed by {@link #asc()} or {@link #desc()}).
     */
    HistoricActivityInstanceQuery orderByActivityType();

    /**
     * Order by start (needs to be followed by {@link #asc()} or {@link #desc()} ).
     */
    HistoricActivityInstanceQuery orderByHistoricActivityInstanceStartTime();

    /**
     * Order by end (needs to be followed by {@link #asc()} or {@link #desc()}).
     */
    HistoricActivityInstanceQuery orderByHistoricActivityInstanceEndTime();

    /**
     * Order by duration (needs to be followed by {@link #asc()} or {@link #desc()}).
     */
    HistoricActivityInstanceQuery orderByHistoricActivityInstanceDuration();

    /**
     * Order by processDefinitionId (needs to be followed by {@link #asc()} or {@link #desc()}).
     */
    HistoricActivityInstanceQuery orderByProcessDefinitionId();

    /**
     * Order by tenant id (needs to be followed by {@link #asc()} or {@link #desc()}).
     */
    HistoricActivityInstanceQuery orderByTenantId();

}
