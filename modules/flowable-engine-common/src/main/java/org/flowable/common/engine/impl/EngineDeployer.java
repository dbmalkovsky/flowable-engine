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
package org.flowable.common.engine.impl;

import java.util.Map;

import org.flowable.common.engine.api.repository.EngineDeployment;

/**
 * @author Tijs Rademakers
 */
public interface EngineDeployer {

    int DEFAULT_UNDEPLOY_ORDER = 0;

    void deploy(EngineDeployment deployment, Map<String, Object> deploymentSettings);

    void undeploy(EngineDeployment parentDeployment, boolean cascade);

    default int getUndeployOrder() {
        return DEFAULT_UNDEPLOY_ORDER;
    }

}
