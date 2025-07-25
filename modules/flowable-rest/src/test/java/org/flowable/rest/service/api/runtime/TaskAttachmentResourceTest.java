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

package org.flowable.rest.service.api.runtime;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.flowable.engine.task.Attachment;
import org.flowable.rest.service.BaseSpringRestTestCase;
import org.flowable.rest.service.HttpMultipartHelper;
import org.flowable.rest.service.api.RestUrls;
import org.flowable.task.api.Task;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import net.javacrumbs.jsonunit.core.Option;

/**
 * @author Frederik Heremans
 */
public class TaskAttachmentResourceTest extends BaseSpringRestTestCase {

    /**
     * Test getting all attachments for a task. GET runtime/tasks/{taskId}/attachments
     */
    @Test
    public void testGetAttachments() throws Exception {
        try {
            Task task = taskService.newTask();
            taskService.saveTask(task);

            // Create URL-attachment
            Attachment urlAttachment = taskService
                    .createAttachment("simpleType", task.getId(), null, "Simple attachment", "Simple attachment description", "http://activiti.org");
            taskService.saveAttachment(urlAttachment);

            // Create Binary-attachment
            Attachment binaryAttachment = taskService
                    .createAttachment("binaryType", task.getId(), null, "Binary attachment", "Binary attachment description", new ByteArrayInputStream(
                            "This is binary content".getBytes()));
            taskService.saveAttachment(binaryAttachment);

            CloseableHttpResponse response = executeRequest(
                    new HttpGet(SERVER_URL_PREFIX + RestUrls.createRelativeResourceUrl(RestUrls.URL_TASK_ATTACHMENT_COLLECTION, task.getId())),
                    HttpStatus.SC_OK);
            JsonNode responseNode = objectMapper.readTree(response.getEntity().getContent());
            closeResponse(response);
            assertThat(responseNode.isArray()).isTrue();
            assertThat(responseNode).hasSize(2);

        } finally {
            // Clean adhoc-tasks even if test fails
            List<Task> tasks = taskService.createTaskQuery().list();
            for (Task task : tasks) {
                taskService.deleteTask(task.getId(), true);
            }
        }
    }

    /**
     * Test getting all attachments for a task. GET runtime/tasks/{taskId}/attachments
     */
    @Test
    public void testGetAttachmentsUnexistingTask() throws Exception {
        closeResponse(
                executeRequest(new HttpGet(SERVER_URL_PREFIX + RestUrls.createRelativeResourceUrl(RestUrls.URL_TASK_ATTACHMENT_COLLECTION, "unexistingtask")),
                        HttpStatus.SC_NOT_FOUND));
    }

    /**
     * Test getting a single attachments for a task. GET runtime/tasks/{taskId}/attachments/{attachmentId}
     */
    @Test
    public void testGetAttachment() throws Exception {
        try {
            Task task = taskService.newTask();
            taskService.saveTask(task);

            // Create URL-attachment
            Attachment urlAttachment = taskService
                    .createAttachment("simpleType", task.getId(), null, "Simple attachment", "Simple attachment description", "http://activiti.org");
            taskService.saveAttachment(urlAttachment);

            // Create Binary-attachment
            Attachment binaryAttachment = taskService
                    .createAttachment("binaryType", task.getId(), null, "Binary attachment", "Binary attachment description", new ByteArrayInputStream(
                            "This is binary content".getBytes()));
            taskService.saveAttachment(binaryAttachment);

            // Get external url attachment
            CloseableHttpResponse response = executeRequest(
                    new HttpGet(SERVER_URL_PREFIX + RestUrls.createRelativeResourceUrl(RestUrls.URL_TASK_ATTACHMENT, task.getId(), urlAttachment.getId())),
                    HttpStatus.SC_OK);
            JsonNode responseNode = objectMapper.readTree(response.getEntity().getContent());
            closeResponse(response);
            assertThatJson(responseNode)
                    .when(Option.IGNORING_EXTRA_FIELDS)
                    .isEqualTo("{"
                            + "id : '" + urlAttachment.getId() + "',"
                            + "type: 'simpleType',"
                            + "name: 'Simple attachment',"
                            + "description: 'Simple attachment description',"
                            + "externalUrl: 'http://activiti.org',"
                            + "url: '" + SERVER_URL_PREFIX + RestUrls
                            .createRelativeResourceUrl(RestUrls.URL_TASK_ATTACHMENT, task.getId(), urlAttachment.getId()) + "',"
                            + "taskUrl: '" + SERVER_URL_PREFIX + RestUrls.createRelativeResourceUrl(RestUrls.URL_TASK, task.getId()) + "',"
                            + "contentUrl: null,"
                            + "processInstanceUrl: null,"
                            + "time: '${json-unit.any-string}'"
                            + "}");

            // Get binary attachment
            response = executeRequest(
                    new HttpGet(SERVER_URL_PREFIX + RestUrls.createRelativeResourceUrl(RestUrls.URL_TASK_ATTACHMENT, task.getId(), binaryAttachment.getId())),
                    HttpStatus.SC_OK);
            responseNode = objectMapper.readTree(response.getEntity().getContent());
            closeResponse(response);
            assertThatJson(responseNode)
                    .when(Option.IGNORING_EXTRA_FIELDS)
                    .isEqualTo("{"
                            + "id : '" + binaryAttachment.getId() + "',"
                            + "type: 'binaryType',"
                            + "name: 'Binary attachment',"
                            + "description: 'Binary attachment description',"
                            + "externalUrl: null,"
                            + "url: '" + SERVER_URL_PREFIX + RestUrls
                            .createRelativeResourceUrl(RestUrls.URL_TASK_ATTACHMENT, task.getId(), binaryAttachment.getId()) + "',"
                            + "taskUrl: '" + SERVER_URL_PREFIX + RestUrls.createRelativeResourceUrl(RestUrls.URL_TASK, task.getId()) + "',"
                            + "contentUrl: '" + SERVER_URL_PREFIX + RestUrls
                            .createRelativeResourceUrl(RestUrls.URL_TASK_ATTACHMENT_DATA, task.getId(), binaryAttachment.getId()) + "',"
                            + "processInstanceUrl: null,"
                            + "time: '${json-unit.any-string}'"
                            + "}");

        } finally {
            // Clean adhoc-tasks even if test fails
            List<Task> tasks = taskService.createTaskQuery().list();
            for (Task task : tasks) {
                taskService.deleteTask(task.getId(), true);
            }
        }
    }

    /**
     * Test getting a single attachments for a task, using unexisting task and unexisting attachment. GET runtime/tasks/{taskId}/attachments/{attachmentId}
     */
    @Test
    public void testGetAttachmentUnexistingTaskAndAttachment() throws Exception {
        try {
            Task task = taskService.newTask();
            taskService.saveTask(task);

            // Create URL-attachment
            Attachment urlAttachment = taskService
                    .createAttachment("simpleType", task.getId(), null, "Simple attachment", "Simple attachment description", "http://activiti.org");
            taskService.saveAttachment(urlAttachment);

            // Get attachment for unexisting task
            closeResponse(executeRequest(
                    new HttpGet(SERVER_URL_PREFIX + RestUrls.createRelativeResourceUrl(RestUrls.URL_TASK_ATTACHMENT, "unexistingtask", urlAttachment.getId())),
                    HttpStatus.SC_NOT_FOUND));

            // Get attachment for task attachment
            closeResponse(executeRequest(
                    new HttpGet(SERVER_URL_PREFIX + RestUrls.createRelativeResourceUrl(RestUrls.URL_TASK_ATTACHMENT, task.getId(), "unexistingattachment")),
                    HttpStatus.SC_NOT_FOUND));

        } finally {
            // Clean adhoc-tasks even if test fails
            List<Task> tasks = taskService.createTaskQuery().list();
            for (Task task : tasks) {
                taskService.deleteTask(task.getId(), true);
            }
        }
    }

    /**
     * Test getting the content for a single attachments for a task. GET runtime/tasks/{taskId}/attachments/{attachmentId}/content
     */
    @Test
    public void testGetAttachmentContent() throws Exception {
        try {
            Task task = taskService.newTask();
            taskService.saveTask(task);

            // Create Binary-attachment
            Attachment binaryAttachment = taskService
                    .createAttachment("binaryType", task.getId(), null, "Binary attachment", "Binary attachment description", new ByteArrayInputStream(
                            "This is binary content".getBytes()));
            taskService.saveAttachment(binaryAttachment);

            // Get external url attachment
            CloseableHttpResponse response = executeRequest(new HttpGet(
                            SERVER_URL_PREFIX + RestUrls.createRelativeResourceUrl(RestUrls.URL_TASK_ATTACHMENT_DATA, task.getId(), binaryAttachment.getId())),
                    HttpStatus.SC_OK);

            // Check response body
            try (InputStream contentStream = response.getEntity().getContent()) {
                assertThat(contentStream).hasContent("This is binary content");
            }

            // Check response headers
            assertThat(response.getEntity().getContentType().getValue()).isEqualTo("application/octet-stream");
            closeResponse(response);

        } finally {
            // Clean adhoc-tasks even if test fails
            List<Task> tasks = taskService.createTaskQuery().list();
            for (Task task : tasks) {
                taskService.deleteTask(task.getId(), true);
            }
        }
    }

    /**
     * Test getting the content for a single attachments for a task, with a mime-type set. GET runtime/tasks/{taskId}/attachments/{attachmentId}/content
     */
    @Test
    public void testGetAttachmentContentWithMimeType() throws Exception {
        try {
            Task task = taskService.newTask();
            taskService.saveTask(task);

            // Create Binary-attachment
            Attachment binaryAttachment = taskService
                    .createAttachment("application/xml", task.getId(), null, "Binary attachment", "Binary attachment description", new ByteArrayInputStream(
                            "<p>This is binary content</p>".getBytes()));
            taskService.saveAttachment(binaryAttachment);

            // Get external url attachment
            CloseableHttpResponse response = executeRequest(new HttpGet(
                            SERVER_URL_PREFIX + RestUrls.createRelativeResourceUrl(RestUrls.URL_TASK_ATTACHMENT_DATA, task.getId(), binaryAttachment.getId())),
                    HttpStatus.SC_OK);

            // Check response headers
            assertThat(response.getEntity().getContentType().getValue()).isEqualTo("application/xml");
            closeResponse(response);

        } finally {
            // Clean adhoc-tasks even if test fails
            List<Task> tasks = taskService.createTaskQuery().list();
            for (Task task : tasks) {
                taskService.deleteTask(task.getId(), true);
            }
        }
    }

    /**
     * Test getting the content for a single attachments for a task, for an attachment without content. GET runtime/tasks/{taskId}/attachments/{attachmentId}/content
     */
    @Test
    public void testGetAttachmentContentWithoutContent() throws Exception {
        try {
            Task task = taskService.newTask();
            taskService.saveTask(task);

            // Create URL-attachment
            Attachment urlAttachment = taskService
                    .createAttachment("simpleType", task.getId(), null, "Simple attachment", "Simple attachment description", "http://activiti.org");
            taskService.saveAttachment(urlAttachment);

            // Get attachment content for non-binary attachment
            closeResponse(executeRequest(
                    new HttpGet(SERVER_URL_PREFIX + RestUrls.createRelativeResourceUrl(RestUrls.URL_TASK_ATTACHMENT_DATA, task.getId(), urlAttachment.getId())),
                    HttpStatus.SC_NOT_FOUND));

        } finally {
            // Clean adhoc-tasks even if test fails
            List<Task> tasks = taskService.createTaskQuery().list();
            for (Task task : tasks) {
                taskService.deleteTask(task.getId(), true);
            }
        }
    }

    /**
     * Test creating a single attachments for a task POST runtime/tasks/{taskId}/attachments/{attachmentId}
     */
    @Test
    public void testCreateAttachment() throws Exception {
        try {
            Task task = taskService.newTask();
            taskService.saveTask(task);

            ObjectNode requestNode = objectMapper.createObjectNode();
            requestNode.put("name", "Simple attachment");
            requestNode.put("description", "Simple attachment description");
            requestNode.put("type", "simpleType");
            requestNode.put("externalUrl", "http://activiti.org");

            HttpPost httpPost = new HttpPost(SERVER_URL_PREFIX + RestUrls.createRelativeResourceUrl(RestUrls.URL_TASK_ATTACHMENT_COLLECTION, task.getId()));
            httpPost.setEntity(new StringEntity(requestNode.toString()));
            CloseableHttpResponse response = executeRequest(httpPost, HttpStatus.SC_CREATED);

            // Check if attachment is created
            List<Attachment> attachments = taskService.getTaskAttachments(task.getId());
            assertThat(attachments).hasSize(1);

            Attachment urlAttachment = attachments.get(0);

            JsonNode responseNode = objectMapper.readTree(response.getEntity().getContent());
            closeResponse(response);
            assertThatJson(responseNode)
                    .when(Option.IGNORING_EXTRA_FIELDS)
                    .isEqualTo("{"
                            + "id : '" + urlAttachment.getId() + "',"
                            + "type: 'simpleType',"
                            + "name: 'Simple attachment',"
                            + "description: 'Simple attachment description',"
                            + "externalUrl: 'http://activiti.org',"
                            + "url: '" + SERVER_URL_PREFIX + RestUrls
                            .createRelativeResourceUrl(RestUrls.URL_TASK_ATTACHMENT, task.getId(), urlAttachment.getId()) + "',"
                            + "taskUrl: '" + SERVER_URL_PREFIX + RestUrls.createRelativeResourceUrl(RestUrls.URL_TASK, task.getId()) + "',"
                            + "contentUrl: null,"
                            + "processInstanceUrl: null,"
                            + "time: '${json-unit.any-string}'"
                            + "}");

        } finally {
            // Clean adhoc-tasks even if test fails
            List<Task> tasks = taskService.createTaskQuery().list();
            for (Task task : tasks) {
                taskService.deleteTask(task.getId(), true);
            }
        }
    }

    /**
     * Test creating a single attachments for a task, using multipart-request to supply content POST runtime/tasks/{taskId}/attachments/{attachmentId}
     */
    @Test
    public void testCreateAttachmentWithContent() throws Exception {

        try {
            Task task = taskService.newTask();
            taskService.saveTask(task);

            InputStream binaryContent = new ByteArrayInputStream("This is binary content".getBytes());

            // Add name, type and scope
            Map<String, String> additionalFields = new HashMap<>();
            additionalFields.put("name", "An attachment");
            additionalFields.put("description", "An attachment description");
            additionalFields.put("type", "myType");

            HttpPost httpPost = new HttpPost(SERVER_URL_PREFIX + RestUrls.createRelativeResourceUrl(RestUrls.URL_TASK_ATTACHMENT_COLLECTION, task.getId()));
            httpPost.setEntity(HttpMultipartHelper.getMultiPartEntity("value", "application/octet-stream", binaryContent, additionalFields));
            CloseableHttpResponse response = executeBinaryRequest(httpPost, HttpStatus.SC_CREATED);

            // Check if attachment is created
            List<Attachment> attachments = taskService.getTaskAttachments(task.getId());
            assertThat(attachments).hasSize(1);

            Attachment binaryAttachment = attachments.get(0);
            try (InputStream contentStream = taskService.getAttachmentContent(binaryAttachment.getId())) {
                assertThat(contentStream).hasContent("This is binary content");
            }

            JsonNode responseNode = objectMapper.readTree(response.getEntity().getContent());
            closeResponse(response);
            assertThatJson(responseNode)
                    .when(Option.IGNORING_EXTRA_FIELDS)
                    .isEqualTo("{"
                            + "id : '" + binaryAttachment.getId() + "',"
                            + "type: 'myType',"
                            + "name: 'An attachment',"
                            + "description: 'An attachment description',"
                            + "externalUrl: null,"
                            + "url: '" + SERVER_URL_PREFIX + RestUrls
                            .createRelativeResourceUrl(RestUrls.URL_TASK_ATTACHMENT, task.getId(), binaryAttachment.getId()) + "',"
                            + "taskUrl: '" + SERVER_URL_PREFIX + RestUrls.createRelativeResourceUrl(RestUrls.URL_TASK, task.getId()) + "',"
                            + "contentUrl: '" + SERVER_URL_PREFIX + RestUrls
                            .createRelativeResourceUrl(RestUrls.URL_TASK_ATTACHMENT_DATA, task.getId(), binaryAttachment.getId()) + "',"
                            + "processInstanceUrl: null,"
                            + "time: '${json-unit.any-string}'"
                            + "}");

        } finally {
            // Clean adhoc-tasks even if test fails
            List<Task> tasks = taskService.createTaskQuery().list();
            for (Task task : tasks) {
                taskService.deleteTask(task.getId(), true);
            }
        }

    }

    /**
     * Test creating a single attachments for a task, without a name POST runtime/tasks/{taskId}/attachments/{attachmentId}
     */
    @Test
    public void testCreateAttachmentNoName() throws Exception {
        try {
            Task task = taskService.newTask();
            taskService.saveTask(task);

            ObjectNode requestNode = objectMapper.createObjectNode();
            requestNode.put("description", "Simple attachment description");
            requestNode.put("type", "simpleType");
            requestNode.put("externalUrl", "http://activiti.org");

            // Post JSON without name
            HttpPost httpPost = new HttpPost(SERVER_URL_PREFIX + RestUrls.createRelativeResourceUrl(RestUrls.URL_TASK_ATTACHMENT_COLLECTION, task.getId()));
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
     * Test deleting a single attachments for a task DELETE runtime/tasks/{taskId}/attachments/{attachmentId}
     */
    @Test
    public void testDeleteAttachment() throws Exception {
        try {
            Task task = taskService.newTask();
            taskService.saveTask(task);

            // Create URL-attachment
            Attachment urlAttachment = taskService
                    .createAttachment("simpleType", task.getId(), null, "Simple attachment", "Simple attachment description", "http://activiti.org");
            taskService.saveAttachment(urlAttachment);

            // Delete the attachment
            HttpDelete httpDelete = new HttpDelete(
                    SERVER_URL_PREFIX + RestUrls.createRelativeResourceUrl(RestUrls.URL_TASK_ATTACHMENT, task.getId(), urlAttachment.getId()));
            closeResponse(executeBinaryRequest(httpDelete, HttpStatus.SC_NO_CONTENT));

            // Check if attachment is really deleted
            assertThat(taskService.getAttachment(urlAttachment.getId())).isNull();

            // Deleting again should result in 404
            closeResponse(executeBinaryRequest(httpDelete, HttpStatus.SC_NOT_FOUND));

        } finally {
            // Clean adhoc-tasks even if test fails
            List<Task> tasks = taskService.createTaskQuery().list();
            for (Task task : tasks) {
                taskService.deleteTask(task.getId(), true);
            }
        }
    }

    /**
     * Test getting a single attachments for a task. GET runtime/tasks/{taskId}/attachments/{attachmentId}
     */
    @Test
    public void testGetAttachmentForCompletedTask() throws Exception {
        try {
            Task task = taskService.newTask();
            taskService.saveTask(task);

            // Create URL-attachment
            Attachment urlAttachment = taskService
                    .createAttachment("simpleType", task.getId(), null, "Simple attachment", "Simple attachment description", "http://activiti.org");
            taskService.saveAttachment(urlAttachment);

            // Create Binary-attachment
            Attachment binaryAttachment = taskService
                    .createAttachment("binaryType", task.getId(), null, "Binary attachment", "Binary attachment description", new ByteArrayInputStream(
                            "This is binary content".getBytes()));
            taskService.saveAttachment(binaryAttachment);

            taskService.complete(task.getId());

            // Get external url attachment
            CloseableHttpResponse response = executeRequest(
                    new HttpGet(SERVER_URL_PREFIX + RestUrls.createRelativeResourceUrl(RestUrls.URL_TASK_ATTACHMENT, task.getId(), urlAttachment.getId())),
                    HttpStatus.SC_OK);

            JsonNode responseNode = objectMapper.readTree(response.getEntity().getContent());
            closeResponse(response);
            assertThatJson(responseNode)
                    .when(Option.IGNORING_EXTRA_FIELDS)
                    .isEqualTo("{"
                            + "id : '" + urlAttachment.getId() + "',"
                            + "type: 'simpleType',"
                            + "name: 'Simple attachment',"
                            + "description: 'Simple attachment description',"
                            + "externalUrl: 'http://activiti.org',"
                            + "url: '" + SERVER_URL_PREFIX + RestUrls
                            .createRelativeResourceUrl(RestUrls.URL_TASK_ATTACHMENT, task.getId(), urlAttachment.getId()) + "',"
                            + "taskUrl: '" + SERVER_URL_PREFIX + RestUrls.createRelativeResourceUrl(RestUrls.URL_TASK, task.getId()) + "',"
                            + "contentUrl: null,"
                            + "processInstanceUrl: null,"
                            + "time: '${json-unit.any-string}'"
                            + "}");

            // Get binary attachment
            response = executeRequest(
                    new HttpGet(SERVER_URL_PREFIX + RestUrls.createRelativeResourceUrl(RestUrls.URL_TASK_ATTACHMENT, task.getId(), binaryAttachment.getId())),
                    HttpStatus.SC_OK);
            responseNode = objectMapper.readTree(response.getEntity().getContent());
            closeResponse(response);
            assertThatJson(responseNode)
                    .when(Option.IGNORING_EXTRA_FIELDS)
                    .isEqualTo("{"
                            + "id : '" + binaryAttachment.getId() + "',"
                            + "type: 'binaryType',"
                            + "name: 'Binary attachment',"
                            + "description: 'Binary attachment description',"
                            + "externalUrl: null,"
                            + "url: '" + SERVER_URL_PREFIX + RestUrls
                            .createRelativeResourceUrl(RestUrls.URL_TASK_ATTACHMENT, task.getId(), binaryAttachment.getId()) + "',"
                            + "taskUrl: '" + SERVER_URL_PREFIX + RestUrls.createRelativeResourceUrl(RestUrls.URL_TASK, task.getId()) + "',"
                            + "contentUrl: '" + SERVER_URL_PREFIX + RestUrls
                            .createRelativeResourceUrl(RestUrls.URL_TASK_ATTACHMENT_DATA, task.getId(), binaryAttachment.getId()) + "',"
                            + "processInstanceUrl: null"
                            + "}");

        } finally {
            // Clean adhoc-tasks even if test fails
            List<HistoricTaskInstance> tasks = historyService.createHistoricTaskInstanceQuery().list();
            for (HistoricTaskInstance task : tasks) {
                taskService.deleteTask(task.getId(), true);
            }
        }
    }
}
