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
package org.flowable.engine.configurator.test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.flowable.app.api.AppRepositoryService;
import org.flowable.app.api.repository.AppDefinition;
import org.flowable.app.api.repository.AppDeployment;
import org.flowable.app.engine.AppEngine;
import org.flowable.app.engine.AppEngineConfiguration;
import org.flowable.app.engine.test.FlowableAppExtension;
import org.flowable.cmmn.api.CmmnRepositoryService;
import org.flowable.cmmn.api.repository.CaseDefinition;
import org.flowable.cmmn.api.repository.CmmnDeployment;
import org.flowable.cmmn.engine.CmmnEngineConfiguration;
import org.flowable.common.engine.impl.interceptor.EngineConfigurationConstants;
import org.flowable.engine.ProcessEngineConfiguration;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.ProcessDefinition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * @author Tijs Rademakers
 */
@ExtendWith(FlowableAppExtension.class)
class DeploymentTest {

    protected AppEngineConfiguration appEngineConfiguration;
    protected AppRepositoryService appRepositoryService;

    @BeforeEach
    void setUp(AppEngine appEngine) {
        appEngineConfiguration = appEngine.getAppEngineConfiguration();
        appRepositoryService = appEngine.getAppRepositoryService();
    }

    @Test
    public void testAppDefinitionDeployed() throws Exception {
        String baseResourcePath = "org/flowable/engine/configurator/test/";
        AppDeployment appDeployment = appRepositoryService.createDeployment()
            .addClasspathResource(baseResourcePath + "test.app")
            .addClasspathResource(baseResourcePath + "oneTaskProcess.bpmn20.xml")
            .addClasspathResource(baseResourcePath + "one-human-task-model.cmmn").deploy();
        
        ProcessEngineConfiguration processEngineConfiguration = null;
        Deployment deployment = null;
        CmmnEngineConfiguration cmmnEngineConfiguration = null;
        CmmnDeployment cmmnDeployment = null;
        
        try {
            AppDeployment queryAppDeployment = appRepositoryService.createDeploymentQuery().singleResult();
            assertThat(queryAppDeployment).isNotNull();
            assertThat(queryAppDeployment.getId()).isEqualTo(appDeployment.getId());
            
            AppDefinition appDefinition = appRepositoryService.createAppDefinitionQuery().deploymentId(appDeployment.getId()).singleResult();
            assertThat(appDefinition.getId()).isNotNull();
            assertThat(appDefinition.getDeploymentId()).isNotNull();
            assertThat(appDefinition.getKey()).isEqualTo("testApp");
            assertThat(appDefinition.getName()).isEqualTo("Test app");
            assertThat(appDefinition.getVersion()).isEqualTo(1);
            
            processEngineConfiguration = (ProcessEngineConfiguration) appEngineConfiguration.getEngineConfigurations()
                            .get(EngineConfigurationConstants.KEY_PROCESS_ENGINE_CONFIG);
            RepositoryService repositoryService = processEngineConfiguration.getRepositoryService();
            deployment = repositoryService.createDeploymentQuery().parentDeploymentId(appDeployment.getId()).singleResult();
            assertThat(deployment).isNotNull();
            assertThat(deployment.getParentDeploymentId()).isEqualTo(appDeployment.getId());
            ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().deploymentId(deployment.getId()).singleResult();
            assertThat(processDefinition).isNotNull();
            assertThat(processDefinition.getKey()).isEqualTo("oneTask");
            
            cmmnEngineConfiguration = (CmmnEngineConfiguration) appEngineConfiguration.getEngineConfigurations()
                            .get(EngineConfigurationConstants.KEY_CMMN_ENGINE_CONFIG);
            CmmnRepositoryService cmmnRepositoryService = cmmnEngineConfiguration.getCmmnRepositoryService();
            cmmnDeployment = cmmnRepositoryService.createDeploymentQuery().parentDeploymentId(appDeployment.getId()).singleResult();
            assertThat(cmmnDeployment).isNotNull();
            assertThat(cmmnDeployment.getParentDeploymentId()).isEqualTo(appDeployment.getId());
            CaseDefinition caseDefinition = cmmnRepositoryService.createCaseDefinitionQuery().deploymentId(cmmnDeployment.getId()).singleResult();
            assertThat(caseDefinition).isNotNull();
            assertThat(caseDefinition.getKey()).isEqualTo("oneTaskCase");

            appRepositoryService.deleteDeployment(appDeployment.getId(), true);

            assertThat(appRepositoryService.createAppDefinitionQuery().list())
                    .extracting(AppDefinition::getKey)
                    .isEmpty();

            assertThat(repositoryService.createProcessDefinitionQuery().list())
                    .extracting(ProcessDefinition::getKey)
                    .isEmpty();

            assertThat(cmmnRepositoryService.createCaseDefinitionQuery().list())
                    .extracting(CaseDefinition::getKey)
                    .isEmpty();
            
            
        } finally {
            appDeployment = appRepositoryService.createDeploymentQuery().deploymentId(appDeployment.getId()).singleResult();
            if (appDeployment != null) {
                appRepositoryService.deleteDeployment(appDeployment.getId(), true);
            }
        }
    }
}
