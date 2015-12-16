/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.kie.smoke.wb.rest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.kie.smoke.wb.util.RestUtil.get;
import static org.kie.smoke.wb.util.RestUtil.post;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.MediaType;

import org.apache.commons.net.util.Base64;
import org.jbpm.kie.services.impl.KModuleDeploymentUnit;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.audit.AuditService;
import org.kie.api.runtime.manager.audit.ProcessInstanceLog;
import org.kie.api.runtime.manager.audit.VariableInstanceLog;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.task.TaskService;
import org.kie.api.task.model.Status;
import org.kie.api.task.model.Task;
import org.kie.api.task.model.TaskSummary;
import org.kie.internal.runtime.conf.RuntimeStrategy;
import org.kie.remote.client.api.RemoteRuntimeEngineFactory;
import org.kie.remote.client.jaxb.ClientJaxbSerializationProvider;
import org.kie.remote.client.jaxb.JaxbTaskSummaryListResponse;
import org.kie.services.client.serialization.JaxbSerializationProvider;
import org.kie.services.client.serialization.jaxb.impl.audit.AbstractJaxbHistoryObject;
import org.kie.services.client.serialization.jaxb.impl.audit.JaxbHistoryLogList;
import org.kie.services.client.serialization.jaxb.impl.audit.JaxbProcessInstanceLog;
import org.kie.services.client.serialization.jaxb.impl.audit.JaxbVariableInstanceLog;
import org.kie.services.client.serialization.jaxb.impl.deploy.JaxbDeploymentUnit;
import org.kie.services.client.serialization.jaxb.impl.deploy.JaxbDeploymentUnitList;
import org.kie.services.client.serialization.jaxb.impl.process.JaxbProcessDefinition;
import org.kie.services.client.serialization.jaxb.impl.process.JaxbProcessInstanceResponse;
import org.kie.smoke.wb.AbstractWorkbenchIntegrationTest;
import org.kie.smoke.wb.category.KieWbSmoke;
import org.kie.smoke.wb.util.TestConstants;
import org.kie.tests.MyType;
import org.kie.tests.Person;
import org.kie.tests.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Category(KieWbSmoke.class)
@SuppressWarnings("unchecked")
public class KieRemoteRestSmokeIntegrationTest extends AbstractWorkbenchIntegrationTest {

    private static final Logger logger = LoggerFactory.getLogger(KieRemoteRestSmokeIntegrationTest.class);

    private static final String taskUserId = "salaboy";

    private final String deploymentId;
    private final KModuleDeploymentUnit deploymentUnit;

    private String mediaType;

    public KieRemoteRestSmokeIntegrationTest() {
        this.deploymentId = TestConstants.KJAR_DEPLOYMENT_ID;
        this.mediaType = MediaType.APPLICATION_XML; // TODO run the tests with both XML and JSON?
        this.deploymentUnit = new KModuleDeploymentUnit(TestConstants.GROUP_ID, TestConstants.ARTIFACT_ID, TestConstants.VERSION);
        assertEquals("Returned deployment unit identifier is incorrect!", deploymentId, deploymentUnit.getIdentifier());
    }

    /**
     * Helper methods
     */

    private RuntimeEngine getRemoteRuntime(URL deploymentUrl, String user, String password) {
        // @formatter:off
        return RemoteRuntimeEngineFactory.newRestBuilder()
                .addDeploymentId(deploymentId)
                .addUrl(deploymentUrl)
                .addUserName(user)
                .addPassword(password)
                .addExtraJaxbClasses(MyType.class, Person.class, Request.class)
                .build();
        // @formatter:on
    }

    /**
     * Clone, build and deploy the test deployment unit.
     */
    @BeforeClass
    public static void setupDeployment() throws Exception {
        deployJbpmPlayGroundIntegrationTests(deploymentUrl, RuntimeStrategy.SINGLETON);
    }

    @AfterClass
    public static void waitForTxOnServer() throws InterruptedException {
        long sleep = 1;
        logger.info("Waiting " + sleep + " secs for tx's on server to close.");
        Thread.sleep(sleep * 1000);
    }

    @Test
    public void testUrlsGetDeployments() throws Exception {
        // test with normal RestRequestHelper
        String user = TestConstants.MARY_USER;
        String password = TestConstants.MARY_PASSWORD;

        JaxbDeploymentUnitList depList = get(deploymentUrl, "rest/deployment/", mediaType,
                200, user, password,
                JaxbDeploymentUnitList.class);

        assertNotNull("Null answer!", depList);
        assertNotNull("Null deployment list!", depList.getDeploymentUnitList());
        assertTrue("Empty deployment list!", depList.getDeploymentUnitList().size() > 0);

        String deploymentId = depList.getDeploymentUnitList().get(0).getIdentifier();
        JaxbDeploymentUnit dep = get(deploymentUrl, "rest/deployment/" + deploymentId, mediaType,
                200, user, password,
                JaxbDeploymentUnit.class);

        assertNotNull("Null answer!", dep);
        assertNotNull("Null deployment list!", dep);
        assertEquals("Empty status!", JaxbDeploymentUnit.JaxbDeploymentStatus.DEPLOYED, dep.getStatus());

        // test with HttpURLConnection
        URL url = new URL(deploymentUrl, deploymentUrl.getPath() + "rest/deployment/");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        String authString = user + ":" + password;
        byte[] authEncBytes = Base64.encodeBase64(authString.getBytes());
        String authStringEnc = new String(authEncBytes);
        connection.setRequestProperty("Authorization", "Basic " + authStringEnc);
        connection.setRequestMethod("GET");

        logger.debug(">> [GET] " + url.toExternalForm());
        connection.connect();
        int respCode = connection.getResponseCode();
        if (200 != respCode) {
            logger.warn(connection.getContent().toString());
        }
        assertEquals(200, respCode);

        JaxbSerializationProvider jaxbSerializer = ClientJaxbSerializationProvider.newInstance();
        String xmlStrObj = getConnectionContent(connection.getContent());
        depList = (JaxbDeploymentUnitList) jaxbSerializer.deserialize(xmlStrObj);

        assertNotNull("Null answer!", depList);
        assertNotNull("Null deployment list!", depList.getDeploymentUnitList());
        assertTrue("Empty deployment list!", depList.getDeploymentUnitList().size() > 0);
    }

    @Test
    public void testRestHistoryLogs() throws Exception {
        //restTests.urlsHistoryLogs(deploymentUrl, MARY_USER, MARY_PASSWORD);
        String user = TestConstants.MARY_USER;
        String password = TestConstants.MARY_PASSWORD;
        {
            // Start process
            JaxbProcessInstanceResponse processInstance = post(deploymentUrl,
                    "rest/runtime/" + deploymentId + "/process/" + TestConstants.SCRIPT_TASK_VAR_PROCESS_ID + "/start?map_x=initVal",
                    mediaType,
                    200, user, password,
                    JaxbProcessInstanceResponse.class);
            long scriptTaskVarProcInstId = processInstance.getId();

            // instances/
            JaxbHistoryLogList historyLogList = get(deploymentUrl, "rest/history/instances", mediaType,
                    200, user, password,
                    JaxbHistoryLogList.class);
            List<Object> auditEventList = historyLogList.getResult();

            assertFalse("Empty list of audit events.", auditEventList.isEmpty());
            for (Object event : auditEventList) {
                assertTrue("ProcessInstanceLog", event instanceof ProcessInstanceLog);
                ProcessInstanceLog procLog = (ProcessInstanceLog) event;
                // @formatter:off
                Object[][] out = {
                        {procLog.getDuration(), "duration"},
                        {procLog.getEnd(), "end date"},
                        {procLog.getIdentity(), "identity"},
                        {procLog.getOutcome(), "outcome"},
                        {procLog.getParentProcessInstanceId(), "parent proc id"},
                        {procLog.getProcessId(), "process id"},
                        {procLog.getProcessInstanceId(), "process instance id"},
                        {procLog.getProcessName(), "process name"},
                        {procLog.getProcessVersion(), "process version"},
                        {procLog.getStart(), "start date"},
                        {procLog.getStatus(), "status"}};
                // @formatter:on
                for (int i = 0; i < out.length; ++i) {
                    // System.out.println(out[i][1] + ": " + out[i][0]);
                }
            }

            // instance/{procInstId}
            ProcessInstanceLog origProcInstLog = ((ProcessInstanceLog) auditEventList.get(0));
            long procInstId = origProcInstLog.getProcessInstanceId();
            JaxbProcessInstanceLog procInstLog = get(deploymentUrl, "rest/history/instance/" + origProcInstLog.getProcessInstanceId(),
                    mediaType,
                    200, user, password,
                    JaxbProcessInstanceLog.class);
            assertNotNull("Null process instance log!", procInstLog);
            assertEquals("Log process instance id",
                    procInstId, procInstLog.getProcessInstanceId().longValue());
            assertEquals("Process instance status",
                    origProcInstLog.getStatus(), procInstLog.getStatus());

            // TODO: instance/{procInstId}/child

            // instance/{procInstId}/node
            historyLogList = get(deploymentUrl, "rest/history/instance/" + procInstId + "/node", mediaType,
                    200, user, password,
                    JaxbHistoryLogList.class);
            assertNotNull("Null process instance log!", historyLogList);
            auditEventList = historyLogList.getResult();
            assertTrue("Empty audit event list!", auditEventList != null && !auditEventList.isEmpty());

            // TODO: instance/{procInstId}/variable

            // TODO: instance/{procInstId}/node/{nodeId}

            // instance/{procInstId}/variable/{variable}
            historyLogList = get(deploymentUrl, "rest/history/instance/" + scriptTaskVarProcInstId + "/variable/x", mediaType,
                    200, user, password,
                    JaxbHistoryLogList.class);
            List<AbstractJaxbHistoryObject> historyVarLogList = historyLogList.getHistoryLogList();

            for (int i = 0; i < historyVarLogList.size(); ++i) {
                JaxbVariableInstanceLog varLog = (JaxbVariableInstanceLog) historyVarLogList.get(i);
                JaxbVariableInstanceLog historyVarLog = (JaxbVariableInstanceLog) historyVarLogList.get(i);
                assertEquals(historyVarLog.getValue(), varLog.getValue());
                assertEquals("Incorrect variable id", "x", varLog.getVariableId());
                Assert.assertEquals("Incorrect process id", TestConstants.SCRIPT_TASK_VAR_PROCESS_ID, varLog.getProcessId());
                assertEquals("Incorrect process instance id", scriptTaskVarProcInstId, varLog.getProcessInstanceId().longValue());
            }
        }

        // process/{procDefId}
        {
            JaxbProcessDefinition procDef = get(deploymentUrl,
                    "rest/runtime/" + deploymentId + "/process/" + TestConstants.OBJECT_VARIABLE_PROCESS_ID,
                    mediaType,
                    200, user, password,
                    JaxbProcessDefinition.class);
            assertNotNull("Empty process definition!", procDef);
            Assert.assertEquals("Process definition id", TestConstants.OBJECT_VARIABLE_PROCESS_ID, procDef.getId());
        }

        {
            String varId = "myobject";
            String varVal = "10";
            JaxbProcessInstanceResponse procInstResp = post(deploymentUrl,
                    "rest/runtime/" + deploymentId + "/process/" + TestConstants.OBJECT_VARIABLE_PROCESS_ID + "/start?map_" + varId + "=" + varVal,
                    mediaType,
                    200, user, password,
                    JaxbProcessInstanceResponse.class);
            long objVarProcInstId = procInstResp.getResult().getId();

            // variable/{varId}
            JaxbHistoryLogList jhll = get(deploymentUrl, "rest/history/variable/" + varId, mediaType,
                    200, user, password,
                    JaxbHistoryLogList.class);
            List<VariableInstanceLog> viLogs = new ArrayList<VariableInstanceLog>();
            if (jhll != null) {
                List<Object> history = jhll.getResult();
                for (Object ae : history) {
                    VariableInstanceLog viLog = (VariableInstanceLog) ae;
                    if (viLog.getProcessInstanceId() == objVarProcInstId) {
                        viLogs.add(viLog);
                    }
                }
            }

            assertNotNull("Empty VariableInstanceLog list.", viLogs);
            assertEquals("VariableInstanceLog list size", 1, viLogs.size());
            VariableInstanceLog vil = viLogs.get(0);
            assertNotNull("Empty VariableInstanceLog instance.", vil);
            assertEquals("Process instance id", vil.getProcessInstanceId().longValue(), objVarProcInstId);
            assertEquals("Variable id", vil.getVariableId(), "myobject");
            assertEquals("Variable value", vil.getValue(), varVal);

            // TODO: variable/{varId}/{value}

            // history/variable/{varId}/instances
            jhll = get(deploymentUrl, "rest/history/variable/" + varId + "/instances", mediaType,
                    200, user, password,
                    JaxbHistoryLogList.class);

            assertNotNull("Empty ProcesInstanceLog list", jhll);
            List<ProcessInstanceLog> piLogs = new ArrayList<ProcessInstanceLog>();
            if (jhll != null) {
                List<Object> history = jhll.getResult();
                for (Object ae : history) {
                    piLogs.add((ProcessInstanceLog) ae);
                }
            }
            assertNotNull("Empty ProcesInstanceLog list", piLogs);
            assertEquals("ProcessInstanceLog list size", piLogs.size(), 1);
            ProcessInstanceLog pi = piLogs.get(0);
            assertNotNull(pi);

            // TODO: history/variable/{varId}/value/{val}/instances
        }
    }

    @Test
    public void testRestRemoteApiHumanTaskProcess() throws Exception {
        String user = TestConstants.SALA_USER;
        String password = TestConstants.SALA_PASSWORD;
        // create REST request
        RuntimeEngine engine = getRemoteRuntime(deploymentUrl, user, password);
        KieSession ksession = engine.getKieSession();
        ProcessInstance processInstance = ksession.startProcess(TestConstants.HUMAN_TASK_PROCESS_ID);
        assertNotNull("Null ProcessInstance!", processInstance);
        long procInstId = processInstance.getId();

        logger.debug("Started process instance: " + processInstance + " " + procInstId);

        TaskService taskService = engine.getTaskService();
        List<TaskSummary> tasks = taskService.getTasksAssignedAsPotentialOwner(taskUserId, "en-UK");
        long taskId = findTaskId(procInstId, tasks);

        logger.debug("Found task " + taskId);
        Task task = taskService.getTaskById(taskId);
        logger.debug("Got task " + taskId + ": " + task);
        taskService.start(taskId, taskUserId);
        taskService.complete(taskId, taskUserId, null);

        logger.debug("Now expecting failure");
        try {
            taskService.complete(taskId, taskUserId, null);
            fail("Should not be able to complete task " + taskId + " a second time.");
        } catch (Throwable t) {
            logger.info("The above exception was an expected part of the test.");
            // do nothing
        }

        List<Status> statuses = new ArrayList<Status>();
        statuses.add(Status.Reserved);
        List<TaskSummary> taskIds = taskService.getTasksByStatusByProcessInstanceId(procInstId, statuses, "en-UK");
        assertEquals("Expected 2 tasks.", 2, taskIds.size());
    }

    protected long findTaskId(Long procInstId, List<TaskSummary> taskSumList) {
        long taskId = -1;
        TaskSummary task = findTaskSummary(procInstId, taskSumList);
        if (task != null) {
            taskId = task.getId();
        }
        assertNotEquals("Could not determine taskId!", -1, taskId);
        return taskId;
    }

    protected TaskSummary findTaskSummary(Long procInstId, List<TaskSummary> taskSumList) {
        for (TaskSummary task : taskSumList) {
            if (procInstId.equals(task.getProcessInstanceId())) {
                return task;
            }
        }
        fail("Unable to find task summary for process instance " + procInstId);
        return null;
    }

    @Test
    public void testRestRemoteApiExtraJaxbClasses() throws Exception {
        String user = TestConstants.MARY_USER;
        String password = TestConstants.MARY_PASSWORD;

        // Remote API setup
        RuntimeEngine engine = getRemoteRuntime(deploymentUrl, user, password);
        // test

        /**
         * MyType
         */
        testParamSerialization(engine, new MyType("variable", 29));

        /**
         * Float
         */
        testParamSerialization(engine, new Float(23.01));

        /**
         * Float []
         */
        testParamSerialization(engine, new Float[]{39.391f});
    }

    private void testParamSerialization(RuntimeEngine engine, Object param) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("myobject", param);
        KieSession ksession = engine.getKieSession();
        ProcessInstance procInst = ksession.startProcess(TestConstants.OBJECT_VARIABLE_PROCESS_ID, parameters);
        assertNotNull("No process instance returned!", procInst);
        long procInstId = procInst.getId();

        /**
         * Check that MyType was correctly deserialized on server side
         */
        List<VariableInstanceLog> varLogList = (List<VariableInstanceLog>) engine.getAuditService().findVariableInstancesByName("type", false);
        VariableInstanceLog thisProcInstVarLog = null;
        for (VariableInstanceLog varLog : varLogList) {
            if (varLog.getProcessInstanceId() == procInstId) {
                thisProcInstVarLog = varLog;
            }
        }
        assertNotNull("No VariableInstanceLog found!", thisProcInstVarLog);
        assertEquals("type", thisProcInstVarLog.getVariableId());
        assertEquals("De/serialization of Kjar type did not work.", param.getClass().getName(), thisProcInstVarLog.getValue());

        // Double check for BZ-1085267
        varLogList = (List<VariableInstanceLog>) engine.getAuditService().findVariableInstances(procInstId, "type");
        assertNotNull("No variable log list retrieved!", varLogList);
        assertTrue("Variable log list is empty!", varLogList.size() > 0);
    }

    @Test
    public void testRestRemoteApiRuleTaskProcess() throws Exception {
        // Remote API setup
        RuntimeEngine runtimeEngine = getRemoteRuntime(deploymentUrl, TestConstants.MARY_USER, TestConstants.MARY_PASSWORD);

        KieSession ksession = runtimeEngine.getKieSession();
        AuditService auditService = runtimeEngine.getAuditService();

        // Setup facts
        Person person = new Person("guest", "Dluhoslav Chudobny");
        person.setAge(25); // >= 18
        Request request = new Request("1");
        request.setPersonId("guest");
        request.setAmount(500); // < 1000

        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("request", request);
        params.put("person", person);

        // Start process
        ProcessInstance pi = ksession.startProcess(TestConstants.RULE_TASK_PROCESS_ID, params);
        assertNotNull("No Process instance returned!", pi);
        ksession.fireAllRules();

        // Check
        // assertEquals("Poor customer", ((Request)ksession.getObject(factHandle)).getInvalidReason());
        assertNull(ksession.getProcessInstance(pi.getId()));

        List<VariableInstanceLog> varLogs = (List<VariableInstanceLog>) auditService.findVariableInstancesByName("requestReason", false);
        for (VariableInstanceLog varLog : varLogs) {
            if (varLog.getProcessInstanceId() == pi.getId()) {
                assertEquals("Poor customer", varLog.getValue());
            }
        }
    }

    @Test
    public void testRestUrlsGroupAssignmentProcess() throws Exception {
        JaxbProcessInstanceResponse procInstResp = post(deploymentUrl,
                "rest/runtime/" + deploymentId + "/process/" + TestConstants.GROUP_ASSSIGNMENT_PROCESS_ID + "/start",
                mediaType,
                200, TestConstants.MARY_USER, TestConstants.MARY_PASSWORD,
                JaxbProcessInstanceResponse.class);
        assertEquals(ProcessInstance.STATE_ACTIVE, procInstResp.getState());
        long procInstId = procInstResp.getId();

        // assert the task
        TaskSummary taskSummary = getTaskSummary(procInstId, Status.Ready, TestConstants.MARY_USER, TestConstants.MARY_PASSWORD);
        long taskId = taskSummary.getId();
        assertNull(taskSummary.getActualOwner());
        assertNull(taskSummary.getPotentialOwners());
        assertEquals("Task 1", taskSummary.getName());

        // complete 'Task 1' as mary
        post(deploymentUrl, "rest/task/" + taskId + "/claim", mediaType,
               200, TestConstants.MARY_USER, TestConstants.MARY_PASSWORD);
        post(deploymentUrl, "rest/task/" + taskId + "/start", mediaType,
               200, TestConstants.MARY_USER, TestConstants.MARY_PASSWORD);
        post(deploymentUrl, "rest/task/" + taskId + "/complete", mediaType,
               200, TestConstants.MARY_USER, TestConstants.MARY_PASSWORD);

        // now make sure that the next task has been assigned to the
        // correct person. it should be mary.
        taskSummary = getTaskSummary(procInstId, Status.Reserved, TestConstants.MARY_USER, TestConstants.MARY_PASSWORD);
        assertEquals("Task 2", taskSummary.getName());
        Assert.assertEquals(TestConstants.MARY_USER, taskSummary.getActualOwner().getId());
        taskId = taskSummary.getId();

        // complete 'Task 2' as john
        post(deploymentUrl, "rest/task/" + taskId + "/release", mediaType,
               200, TestConstants.MARY_USER, TestConstants.MARY_PASSWORD);
        post(deploymentUrl, "rest/task/" + taskId + "/start", mediaType,
               200, TestConstants.JOHN_USER, TestConstants.JOHN_PASSWORD);
        post(deploymentUrl, "rest/task/" + taskId + "/complete", mediaType,
               200, TestConstants.JOHN_USER, TestConstants.JOHN_PASSWORD);

        // now make sure that the next task has been assigned to the
        // correct person. it should be john.
        taskSummary = getTaskSummary(procInstId, Status.Reserved, TestConstants.JOHN_USER, TestConstants.JOHN_PASSWORD);
        assertEquals("Task 3", taskSummary.getName());
        Assert.assertEquals(TestConstants.JOHN_USER, taskSummary.getActualOwner().getId());
        taskId = taskSummary.getId();

        // complete 'Task 3' as john
        post(deploymentUrl, "rest/task/" + taskId + "/start", mediaType,
               200, TestConstants.JOHN_USER, TestConstants.JOHN_PASSWORD);
        post(deploymentUrl, "rest/task/" + taskId + "/complete", mediaType,
               200, TestConstants.JOHN_USER, TestConstants.JOHN_PASSWORD);

        // assert process finished
        JaxbProcessInstanceLog jaxbProcInstLog = get(deploymentUrl, "rest/history/instance/" + procInstId, mediaType,
                200, TestConstants.MARY_USER, TestConstants.MARY_PASSWORD,
                JaxbProcessInstanceLog.class);
        ProcessInstanceLog procInstLog = jaxbProcInstLog.getResult();
        assertEquals("Process instance has not completed!", ProcessInstance.STATE_COMPLETED, procInstLog.getStatus().intValue());
    }

    private TaskSummary getTaskSummary(long processInstanceId, Status status, String user, String password) throws Exception {
        JaxbTaskSummaryListResponse taskSumListResp = get(deploymentUrl,
                "rest/task/query?processInstanceId=" + processInstanceId + "&status=" + status.toString(), mediaType,
                200, user, password,
                JaxbTaskSummaryListResponse.class);
        List<TaskSummary> taskSumList = taskSumListResp.getResult();
        assertEquals(1, taskSumList.size());
        return taskSumList.get(0);
    }


    private String getConnectionContent(Object content) throws Exception {
        InputStreamReader in = new InputStreamReader((InputStream) content);
        BufferedReader buff = new BufferedReader(in);
        StringBuffer text = new StringBuffer();
        String line = buff.readLine();
        while (line != null) {
            text.append(line);
            line = buff.readLine();
        }
        return text.toString();
    }

}
