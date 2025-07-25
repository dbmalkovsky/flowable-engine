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
package org.flowable.dmn.rest.service.api.repository;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;

import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.flowable.dmn.api.DmnDecision;
import org.flowable.dmn.engine.test.DmnDeployment;
import org.flowable.dmn.rest.service.api.BaseSpringDmnRestTestCase;
import org.flowable.dmn.rest.service.api.DmnRestUrls;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.JsonNode;

import net.javacrumbs.jsonunit.core.Option;

/**
 * @author Yvo Swillens
 */
public class DecisionTableResourceTest extends BaseSpringDmnRestTestCase {

    @Test
    @DmnDeployment(resources = { "org/flowable/dmn/rest/service/api/repository/simple.dmn" })
    public void testGetDecisionTable() throws Exception {

        DmnDecision definition = dmnRepositoryService.createDecisionQuery().singleResult();

        HttpGet httpGet = new HttpGet(SERVER_URL_PREFIX + DmnRestUrls.createRelativeResourceUrl(DmnRestUrls.URL_DECISION_TABLE, definition.getId()));
        CloseableHttpResponse response = executeRequest(httpGet, HttpStatus.SC_OK);
        JsonNode responseNode = objectMapper.readTree(response.getEntity().getContent());
        closeResponse(response);
        assertThatJson(responseNode)
                .when(Option.IGNORING_EXTRA_FIELDS)
                .isEqualTo("{"
                        + "  id: '" + definition.getId() + "',"
                        + "  url: '" + httpGet.getURI().toString() + "',"
                        + "  category: " + definition.getCategory() + ","
                        + "  name: '" + definition.getName() + "',"
                        + "  key: '" + definition.getKey() + "',"
                        + "  description: " + definition.getDescription() + ","
                        + "  version: " + definition.getVersion() + ","
                        + "  deploymentId: '" + definition.getDeploymentId() + "'"
                        + "  }"
                );
    }

    @Test
    @DmnDeployment(resources = { "org/flowable/dmn/rest/service/api/repository/simple.dmn" })
    public void testGetUnexistingDecisionTable() throws Exception {
        HttpGet httpGet = new HttpGet(SERVER_URL_PREFIX + DmnRestUrls.createRelativeResourceUrl(DmnRestUrls.URL_DECISION_TABLE, "unexisting"));
        CloseableHttpResponse response = executeRequest(httpGet, HttpStatus.SC_NOT_FOUND);
        closeResponse(response);
    }

}
