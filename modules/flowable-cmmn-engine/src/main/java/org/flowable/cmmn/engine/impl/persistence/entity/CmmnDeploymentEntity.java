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

package org.flowable.cmmn.engine.impl.persistence.entity;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.flowable.cmmn.api.repository.CmmnDeployment;
import org.flowable.cmmn.engine.impl.persistence.entity.deploy.CaseDefinitionCacheEntry;
import org.flowable.common.engine.api.repository.EngineResource;
import org.flowable.common.engine.impl.persistence.entity.Entity;

/**
 * @author Tijs Rademakers
 * @author Joram Barrez
 */
public interface CmmnDeploymentEntity extends CmmnDeployment, Entity {

    void addResource(CmmnResourceEntity resource);

    void addDeployedArtifact(Object deployedArtifact);

    <T> List<T> getDeployedArtifacts(Class<T> clazz);
    
    void addCaseDefinitionCacheEntry(String caseDefinitionId, CaseDefinitionCacheEntry caseDefinitionCacheEntry);

    CaseDefinitionCacheEntry getCaseDefinitionCacheEntry(String caseDefinitionId);

    void setName(String name);

    void setCategory(String category);
    
    void setKey(String key);

    void setTenantId(String tenantId);

    void setParentDeploymentId(String parentDeploymentId);

    void setResources(Map<String, EngineResource> resources);

    void setDeploymentTime(Date deploymentTime);

    @Override
    boolean isNew();

    void setNew(boolean isNew);
}