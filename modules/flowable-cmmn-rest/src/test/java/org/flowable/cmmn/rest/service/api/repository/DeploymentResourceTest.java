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
package org.flowable.cmmn.rest.service.api.repository;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.flowable.cmmn.api.repository.CmmnDeployment;
import org.flowable.cmmn.rest.service.BaseSpringRestTestCase;
import org.flowable.cmmn.rest.service.HttpMultipartHelper;
import org.flowable.cmmn.rest.service.api.CmmnRestUrls;
import org.flowable.common.engine.impl.util.ReflectUtil;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.JsonNode;

import net.javacrumbs.jsonunit.core.Option;

/**
 * Test for all REST-operations related to a single Deployment resource.
 *
 * @author Tijs Rademakers
 */
public class DeploymentResourceTest extends BaseSpringRestTestCase {

    /**
     * Test deploying single cmmn file. POST cmmn-repository/deployments
     */
    @Test
    public void testPostNewDeploymentCmmnFile() throws Exception {
        try {
            // Upload a valid BPMN-file using multipart-data
            HttpPost httpPost = new HttpPost(SERVER_URL_PREFIX + CmmnRestUrls.createRelativeResourceUrl(CmmnRestUrls.URL_DEPLOYMENT_COLLECTION));
            httpPost.setEntity(HttpMultipartHelper.getMultiPartEntity("oneHumanTaskCase.cmmn", "application/xml",
                    ReflectUtil.getResourceAsStream("org/flowable/cmmn/rest/service/api/repository/oneHumanTaskCase.cmmn"), null));
            CloseableHttpResponse response = executeBinaryRequest(httpPost, HttpStatus.SC_CREATED);

            // Check deployment
            JsonNode responseNode = objectMapper.readTree(response.getEntity().getContent());
            closeResponse(response);

            String deploymentId = responseNode.get("id").textValue();

            assertThatJson(responseNode)
                    .when(Option.IGNORING_EXTRA_FIELDS)
                    .isEqualTo("{"
                            + " id: '${json-unit.any-string}',"
                            + " name: 'oneHumanTaskCase',"
                            + " category: null,"
                            + " deploymentTime: '${json-unit.any-string}',"
                            + " tenantId: \"\","
                            + " url: '" + SERVER_URL_PREFIX + CmmnRestUrls
                            .createRelativeResourceUrl(CmmnRestUrls.URL_DEPLOYMENT, deploymentId) + "'"
                            + "}");

            assertThat(repositoryService.createDeploymentQuery().deploymentId(deploymentId).count()).isEqualTo(1);

            // Check if process is actually deployed in the deployment
            List<String> resources = repositoryService.getDeploymentResourceNames(deploymentId);
            assertThat(resources)
                    .containsOnly("oneHumanTaskCase.cmmn");

            assertThat(repositoryService.createCaseDefinitionQuery().deploymentId(deploymentId).count()).isEqualTo(1);

        } finally {
            // Always cleanup any created deployments, even if the test failed
            List<CmmnDeployment> deployments = repositoryService.createDeploymentQuery().list();
            for (CmmnDeployment deployment : deployments) {
                repositoryService.deleteDeployment(deployment.getId(), true);
            }
        }
    }

    /**
     * Test deploying an invalid file. POST repository/deployments
     */
    @Test
    public void testPostNewDeploymentInvalidFile() throws Exception {
        // Upload a valid BPMN-file using multipart-data
        HttpPost httpPost = new HttpPost(SERVER_URL_PREFIX + CmmnRestUrls.createRelativeResourceUrl(CmmnRestUrls.URL_DEPLOYMENT_COLLECTION));
        httpPost.setEntity(HttpMultipartHelper.getMultiPartEntity("oneTaskProcess.invalidfile", "application/xml",
                ReflectUtil.getResourceAsStream("org/flowable/cmmn/rest/service/api/repository/oneHumanTaskCase.cmmn"), null));
        closeResponse(executeBinaryRequest(httpPost, HttpStatus.SC_BAD_REQUEST));
    }

    /**
     * Test getting a single deployment. GET repository/deployments/{deploymentId}
     */
    @Test
    @org.flowable.cmmn.engine.test.CmmnDeployment(resources = { "org/flowable/cmmn/rest/service/api/repository/oneHumanTaskCase.cmmn" })
    public void testGetDeployment() throws Exception {
        CmmnDeployment existingDeployment = repositoryService.createDeploymentQuery().singleResult();

        HttpGet httpGet = new HttpGet(SERVER_URL_PREFIX + CmmnRestUrls.createRelativeResourceUrl(CmmnRestUrls.URL_DEPLOYMENT, existingDeployment.getId()));
        CloseableHttpResponse response = executeRequest(httpGet, HttpStatus.SC_OK);

        JsonNode responseNode = objectMapper.readTree(response.getEntity().getContent());

        closeResponse(response);

        assertThatJson(responseNode)
                .when(Option.IGNORING_EXTRA_FIELDS)
                .isEqualTo("{"
                        + " id: '" + existingDeployment.getId() + "',"
                        + " name: '" + existingDeployment.getName() + "',"
                        + " category: null,"
                        + " tenantId: \"\","
                        + " url: '" + SERVER_URL_PREFIX + CmmnRestUrls.createRelativeResourceUrl(CmmnRestUrls.URL_DEPLOYMENT, deploymentId) + "',"
                        + " deploymentTime: '${json-unit.any-string}'"
                        + "}");
    }

    /**
     * Test getting an unexisting deployment. GET repository/deployments/{deploymentId}
     */
    @Test
    public void testGetUnexistingDeployment() throws Exception {
        HttpGet httpGet = new HttpGet(SERVER_URL_PREFIX + CmmnRestUrls.createRelativeResourceUrl(CmmnRestUrls.URL_DEPLOYMENT, "unexisting"));
        CloseableHttpResponse response = executeRequest(httpGet, HttpStatus.SC_NOT_FOUND);
        closeResponse(response);
    }

    /**
     * Test deleting a single deployment. DELETE repository/deployments/{deploymentId}
     */
    @Test
    @org.flowable.cmmn.engine.test.CmmnDeployment(resources = { "org/flowable/cmmn/rest/service/api/repository/oneHumanTaskCase.cmmn" })
    public void testDeleteDeployment() throws Exception {
        CmmnDeployment existingDeployment = repositoryService.createDeploymentQuery().singleResult();
        assertThat(existingDeployment).isNotNull();

        // Delete the deployment
        HttpDelete httpDelete = new HttpDelete(
                SERVER_URL_PREFIX + CmmnRestUrls.createRelativeResourceUrl(CmmnRestUrls.URL_DEPLOYMENT, existingDeployment.getId()));
        CloseableHttpResponse response = executeRequest(httpDelete, HttpStatus.SC_NO_CONTENT);
        closeResponse(response);

        existingDeployment = repositoryService.createDeploymentQuery().singleResult();
        assertThat(existingDeployment).isNull();
    }

    /**
     * Test deleting an unexisting deployment. DELETE repository/deployments/{deploymentId}
     */
    @Test
    public void testDeleteUnexistingDeployment() throws Exception {
        HttpDelete httpDelete = new HttpDelete(SERVER_URL_PREFIX + CmmnRestUrls.createRelativeResourceUrl(CmmnRestUrls.URL_DEPLOYMENT, "unexisting"));
        CloseableHttpResponse response = executeRequest(httpDelete, HttpStatus.SC_NOT_FOUND);
        closeResponse(response);
    }
}
