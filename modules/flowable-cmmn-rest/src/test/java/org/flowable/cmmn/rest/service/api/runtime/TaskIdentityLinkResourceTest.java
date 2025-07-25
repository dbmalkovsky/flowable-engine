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

package org.flowable.cmmn.rest.service.api.runtime;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.flowable.cmmn.api.runtime.CaseInstance;
import org.flowable.cmmn.engine.test.CmmnDeployment;
import org.flowable.cmmn.rest.service.BaseSpringRestTestCase;
import org.flowable.cmmn.rest.service.api.CmmnRestUrls;
import org.flowable.task.api.Task;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import net.javacrumbs.jsonunit.core.Option;

/**
 * Test for all REST-operations related to a identity links on a Task resource.
 *
 * @author Tijs Rademakers
 */
public class TaskIdentityLinkResourceTest extends BaseSpringRestTestCase {

    /**
     * Test getting all identity links. GET cmmn-runtime/tasks/{taskId}/identitylinks
     */
    @Test
    @CmmnDeployment(resources = { "org/flowable/cmmn/rest/service/api/repository/oneHumanTaskCase.cmmn" })
    public void testGetIdentityLinks() throws Exception {
        // Test candidate user/groups links + manual added identityLink
        CaseInstance caseInstance = runtimeService.createCaseInstanceBuilder().caseDefinitionKey("oneHumanTaskCase").start();
        Task task = taskService.createTaskQuery().caseInstanceId(caseInstance.getId()).singleResult();
        taskService.addUserIdentityLink(task.getId(), "john", "customType");

        assertThat(taskService.getIdentityLinksForTask(task.getId())).hasSize(2);

        // Execute the request
        HttpGet httpGet = new HttpGet(SERVER_URL_PREFIX + CmmnRestUrls.createRelativeResourceUrl(CmmnRestUrls.URL_TASK_IDENTITYLINKS_COLLECTION, task.getId()));
        CloseableHttpResponse response = executeRequest(httpGet, HttpStatus.SC_OK);
        JsonNode responseNode = objectMapper.readTree(response.getEntity().getContent());
        closeResponse(response);
        assertThat(responseNode).isNotNull();
        assertThat(responseNode.isArray()).isTrue();
        assertThatJson(responseNode)
                .when(Option.IGNORING_EXTRA_FIELDS, Option.IGNORING_ARRAY_ORDER)
                .isEqualTo("["
                        + " {"
                        + " user: 'john',"
                        + " type: 'customType',"
                        + " group: null,"
                        + " url: '" + SERVER_URL_PREFIX + CmmnRestUrls
                        .createRelativeResourceUrl(CmmnRestUrls.URL_TASK_IDENTITYLINK, task.getId(), "users", "john", "customType") + "'"
                        + " },"
                        + " {"
                        + " user: 'johnDoe',"
                        + " type: 'assignee',"
                        + " group: null,"
                        + " url: '" + SERVER_URL_PREFIX + CmmnRestUrls
                        .createRelativeResourceUrl(CmmnRestUrls.URL_TASK_IDENTITYLINK, task.getId(), "users", "johnDoe", "assignee") + "'"
                        + " }"
                        + "]");
    }

    /**
     * Test getting all identity links. POST cmmn-runtime/tasks/{taskId}/identitylinks
     */
    @Test
    public void testCreateIdentityLink() throws Exception {
        try {
            Task task = taskService.newTask();
            taskService.saveTask(task);

            // Add user link
            ObjectNode requestNode = objectMapper.createObjectNode();
            requestNode.put("user", "kermit");
            requestNode.put("type", "myType");

            HttpPost httpPost = new HttpPost(
                    SERVER_URL_PREFIX + CmmnRestUrls.createRelativeResourceUrl(CmmnRestUrls.URL_TASK_IDENTITYLINKS_COLLECTION, task.getId()));
            httpPost.setEntity(new StringEntity(requestNode.toString()));
            CloseableHttpResponse response = executeRequest(httpPost, HttpStatus.SC_CREATED);
            JsonNode responseNode = objectMapper.readTree(response.getEntity().getContent());
            closeResponse(response);
            assertThat(responseNode).isNotNull();
            assertThatJson(responseNode)
                    .when(Option.IGNORING_EXTRA_FIELDS)
                    .isEqualTo("{"
                            + "  user: 'kermit',"
                            + "  type: 'myType',"
                            + "  group: null,"
                            + "  url: '" + SERVER_URL_PREFIX + CmmnRestUrls
                            .createRelativeResourceUrl(CmmnRestUrls.URL_TASK_IDENTITYLINK, task.getId(), CmmnRestUrls.SEGMENT_IDENTITYLINKS_FAMILY_USERS,
                                    "kermit", "myType") + "'"
                            + "}");

            // Add group link
            requestNode = objectMapper.createObjectNode();
            requestNode.put("group", "sales");
            requestNode.put("type", "myType");

            httpPost.setEntity(new StringEntity(requestNode.toString()));
            response = executeRequest(httpPost, HttpStatus.SC_CREATED);
            responseNode = objectMapper.readTree(response.getEntity().getContent());
            closeResponse(response);
            assertThat(responseNode).isNotNull();
            assertThatJson(responseNode)
                    .when(Option.IGNORING_EXTRA_FIELDS)
                    .isEqualTo("{"
                            + "  user: null,"
                            + "  type: 'myType',"
                            + "  group: 'sales',"
                            + "  url: '" + SERVER_URL_PREFIX + CmmnRestUrls
                            .createRelativeResourceUrl(CmmnRestUrls.URL_TASK_IDENTITYLINK, task.getId(), CmmnRestUrls.SEGMENT_IDENTITYLINKS_FAMILY_GROUPS,
                                    "sales", "myType") + "'"
                            + "}");

            // Test with unexisting task
            httpPost = new HttpPost(
                    SERVER_URL_PREFIX + CmmnRestUrls.createRelativeResourceUrl(CmmnRestUrls.URL_TASK_IDENTITYLINKS_COLLECTION, "unexistingtask"));
            httpPost.setEntity(new StringEntity(requestNode.toString()));
            closeResponse(executeRequest(httpPost, HttpStatus.SC_NOT_FOUND));

            // Test with no user/group task
            requestNode = objectMapper.createObjectNode();
            requestNode.put("type", "myType");
            httpPost = new HttpPost(SERVER_URL_PREFIX + CmmnRestUrls.createRelativeResourceUrl(CmmnRestUrls.URL_TASK_IDENTITYLINKS_COLLECTION, task.getId()));
            httpPost.setEntity(new StringEntity(requestNode.toString()));
            closeResponse(executeRequest(httpPost, HttpStatus.SC_BAD_REQUEST));

            // Test with no user/group task
            requestNode = objectMapper.createObjectNode();
            requestNode.put("type", "myType");
            requestNode.put("user", "kermit");
            requestNode.put("group", "sales");

            httpPost.setEntity(new StringEntity(requestNode.toString()));
            closeResponse(executeRequest(httpPost, HttpStatus.SC_BAD_REQUEST));

            // Test with no type
            requestNode = objectMapper.createObjectNode();
            requestNode.put("group", "sales");

            httpPost.setEntity(new StringEntity(requestNode.toString()));
            closeResponse(executeRequest(httpPost, HttpStatus.SC_BAD_REQUEST));

        } finally {
            // Clean adhoc-tasks even if test fails
            List<Task> tasks = taskService.createTaskQuery().list();
            for (Task task : tasks) {
                taskService.deleteTask(task.getId(), true);
            }
        }
    }

    /**
     * Test getting a single identity link for a task. GET runtime/tasks/{taskId}/identitylinks/{family}/{identityId}/{type}
     */
    @Test
    public void testGetSingleIdentityLink() throws Exception {
        try {
            Task task = taskService.newTask();
            taskService.saveTask(task);
            taskService.addUserIdentityLink(task.getId(), "kermit", "myType");

            HttpGet httpGet = new HttpGet(
                    SERVER_URL_PREFIX + CmmnRestUrls.createRelativeResourceUrl(CmmnRestUrls.URL_TASK_IDENTITYLINK, task.getId(), "users", "kermit", "myType"));
            CloseableHttpResponse response = executeRequest(httpGet, HttpStatus.SC_OK);
            JsonNode responseNode = objectMapper.readTree(response.getEntity().getContent());
            closeResponse(response);
            assertThat(responseNode).isNotNull();
            assertThatJson(responseNode)
                    .when(Option.IGNORING_EXTRA_FIELDS)
                    .isEqualTo("{"
                            + "  user: 'kermit',"
                            + "  type: 'myType',"
                            + "  group: null,"
                            + "  url: '" + SERVER_URL_PREFIX + CmmnRestUrls
                            .createRelativeResourceUrl(CmmnRestUrls.URL_TASK_IDENTITYLINK, task.getId(), CmmnRestUrls.SEGMENT_IDENTITYLINKS_FAMILY_USERS,
                                    "kermit", "myType") + "'"
                            + "}");

            // Test with unexisting task
            httpGet = new HttpGet(SERVER_URL_PREFIX + CmmnRestUrls
                    .createRelativeResourceUrl(CmmnRestUrls.URL_TASK_IDENTITYLINK, "unexistingtask", CmmnRestUrls.SEGMENT_IDENTITYLINKS_FAMILY_USERS, "kermit",
                            "myType"));
            closeResponse(executeRequest(httpGet, HttpStatus.SC_NOT_FOUND));

        } finally {
            // Clean adhoc-tasks even if test fails
            List<Task> tasks = taskService.createTaskQuery().list();
            for (Task task : tasks) {
                taskService.deleteTask(task.getId(), true);
            }
        }
    }

    /**
     * Test deleting a single identity link for a task. DELETE runtime/tasks/{taskId}/identitylinks/{family}/{identityId}/{type}
     */
    @Test
    public void testDeleteSingleIdentityLink() throws Exception {
        try {
            Task task = taskService.newTask();
            taskService.saveTask(task);
            taskService.addUserIdentityLink(task.getId(), "kermit", "myType");

            HttpDelete httpDelete = new HttpDelete(
                    SERVER_URL_PREFIX + CmmnRestUrls.createRelativeResourceUrl(CmmnRestUrls.URL_TASK_IDENTITYLINK, task.getId(), "users", "kermit", "myType"));
            closeResponse(executeRequest(httpDelete, HttpStatus.SC_NO_CONTENT));

            // Test with unexisting identitylink
            httpDelete = new HttpDelete(SERVER_URL_PREFIX
                    + CmmnRestUrls
                    .createRelativeResourceUrl(CmmnRestUrls.URL_TASK_IDENTITYLINK, task.getId(), CmmnRestUrls.SEGMENT_IDENTITYLINKS_FAMILY_USERS, "kermit",
                            "unexistingtype"));
            closeResponse(executeRequest(httpDelete, HttpStatus.SC_NOT_FOUND));

            // Test with unexisting task
            httpDelete = new HttpDelete(SERVER_URL_PREFIX
                    + CmmnRestUrls
                    .createRelativeResourceUrl(CmmnRestUrls.URL_TASK_IDENTITYLINK, "unexistingtask", CmmnRestUrls.SEGMENT_IDENTITYLINKS_FAMILY_USERS, "kermit",
                            "myType"));
            closeResponse(executeRequest(httpDelete, HttpStatus.SC_NOT_FOUND));

        } finally {
            // Clean adhoc-tasks even if test fails
            List<Task> tasks = taskService.createTaskQuery().list();
            for (Task task : tasks) {
                taskService.deleteTask(task.getId(), true);
            }
        }
    }
}
