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

package org.flowable.cmmn.rest.service.api.history.variable;

import java.util.List;

import org.flowable.cmmn.rest.service.api.engine.variable.QueryVariable;
import org.flowable.common.rest.api.PaginateRequest;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

/**
 * @author Tijs Rademakers
 */
public class HistoricVariableInstanceQueryRequest extends PaginateRequest {

    private Boolean excludeTaskVariables;
    private String taskId;
    private String planItemInstanceId;
    private String caseInstanceId;
    private String variableName;
    private String variableNameLike;
    private List<QueryVariable> variables;
    private Boolean excludeLocalVariables;

    public Boolean getExcludeTaskVariables() {
        return excludeTaskVariables;
    }

    public void setExcludeTaskVariables(Boolean excludeTaskVariables) {
        this.excludeTaskVariables = excludeTaskVariables;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getPlanItemInstanceId() {
        return planItemInstanceId;
    }

    public void setPlanItemInstanceId(String planItemInstanceId) {
        this.planItemInstanceId = planItemInstanceId;
    }

    public String getCaseInstanceId() {
        return caseInstanceId;
    }

    public void setCaseInstanceId(String caseInstanceId) {
        this.caseInstanceId = caseInstanceId;
    }

    public String getVariableName() {
        return variableName;
    }

    public void setVariableName(String variableName) {
        this.variableName = variableName;
    }

    public String getVariableNameLike() {
        return variableNameLike;
    }

    public void setVariableNameLike(String variableNameLike) {
        this.variableNameLike = variableNameLike;
    }

    @JsonTypeInfo(use = Id.CLASS, defaultImpl = QueryVariable.class)
    public List<QueryVariable> getVariables() {
        return variables;
    }

    public void setVariables(List<QueryVariable> variables) {
        this.variables = variables;
    }

    public void setExcludeLocalVariables(Boolean excludeLocalVariables) {
        this.excludeLocalVariables = excludeLocalVariables;
    }

    public Boolean getExcludeLocalVariables() {
        return excludeLocalVariables;
    }
}
