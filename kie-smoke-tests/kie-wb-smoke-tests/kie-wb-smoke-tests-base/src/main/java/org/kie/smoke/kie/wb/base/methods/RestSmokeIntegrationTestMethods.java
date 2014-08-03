/*
 * JBoss, Home of Professional Open Source
 * 
 * Copyright 2012, Red Hat Middleware LLC, and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.smoke.kie.wb.base.methods;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.kie.smoke.kie.wb.base.util.TestConstants.ARTIFACT_ID;
import static org.kie.smoke.kie.wb.base.util.TestConstants.GROUP_ASSSIGNMENT_PROCESS_ID;
import static org.kie.smoke.kie.wb.base.util.TestConstants.GROUP_ID;
import static org.kie.smoke.kie.wb.base.util.TestConstants.HUMAN_TASK_PROCESS_ID;
import static org.kie.smoke.kie.wb.base.util.TestConstants.JOHN_PASSWORD;
import static org.kie.smoke.kie.wb.base.util.TestConstants.JOHN_USER;
import static org.kie.smoke.kie.wb.base.util.TestConstants.MARY_PASSWORD;
import static org.kie.smoke.kie.wb.base.util.TestConstants.MARY_USER;
import static org.kie.smoke.kie.wb.base.util.TestConstants.OBJECT_VARIABLE_PROCESS_ID;
import static org.kie.smoke.kie.wb.base.util.TestConstants.RULE_TASK_PROCESS_ID;
import static org.kie.smoke.kie.wb.base.util.TestConstants.SCRIPT_TASK_VAR_PROCESS_ID;
import static org.kie.smoke.kie.wb.base.util.TestConstants.VERSION;
import static org.kie.smoke.tests.util.RestUtil.get;
import static org.kie.smoke.tests.util.RestUtil.post;

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
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jbpm.kie.services.impl.KModuleDeploymentUnit;
import org.jbpm.process.audit.AuditLogService;
import org.jbpm.process.audit.ProcessInstanceLog;
import org.jbpm.process.audit.VariableInstanceLog;
import org.jbpm.process.audit.event.AuditEvent;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.task.TaskService;
import org.kie.api.task.model.Status;
import org.kie.api.task.model.Task;
import org.kie.api.task.model.TaskSummary;
import org.kie.internal.runtime.conf.RuntimeStrategy;
import org.kie.services.client.api.RemoteRestRuntimeEngineFactory;
import org.kie.services.client.api.RestRequestHelper;
import org.kie.services.client.api.command.RemoteRuntimeEngine;
import org.kie.services.client.serialization.JaxbSerializationProvider;
import org.kie.services.client.serialization.JsonSerializationProvider;
import org.kie.services.client.serialization.jaxb.impl.audit.AbstractJaxbHistoryObject;
import org.kie.services.client.serialization.jaxb.impl.audit.JaxbHistoryLogList;
import org.kie.services.client.serialization.jaxb.impl.audit.JaxbProcessInstanceLog;
import org.kie.services.client.serialization.jaxb.impl.audit.JaxbVariableInstanceLog;
import org.kie.services.client.serialization.jaxb.impl.deploy.JaxbDeploymentUnit;
import org.kie.services.client.serialization.jaxb.impl.deploy.JaxbDeploymentUnit.JaxbDeploymentStatus;
import org.kie.services.client.serialization.jaxb.impl.deploy.JaxbDeploymentUnitList;
import org.kie.services.client.serialization.jaxb.impl.process.JaxbProcessDefinition;
import org.kie.services.client.serialization.jaxb.impl.process.JaxbProcessInstanceResponse;
import org.kie.services.client.serialization.jaxb.impl.task.JaxbTaskSummaryListResponse;
import org.kie.tests.MyType;
import org.kie.tests.Person;
import org.kie.tests.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RestSmokeIntegrationTestMethods extends AbstractSmokeIntegrationTestMethods {

    private static Logger logger = LoggerFactory.getLogger(RestSmokeIntegrationTestMethods.class);

    private static final String taskUserId = "salaboy";

    private final String deploymentId;
    private final KModuleDeploymentUnit deploymentUnit;
    private RuntimeStrategy strategy = RuntimeStrategy.SINGLETON;

    private MediaType mediaType;
    private int timeout;
    private static final int DEFAULT_TIMEOUT = 10;

    private RestSmokeIntegrationTestMethods(String deploymentId, MediaType mediaType, int timeout, RuntimeStrategy strategy) {
        if (mediaType == null) {
            mediaType = MediaType.APPLICATION_XML_TYPE;
        }
        if (strategy != null) {
            this.strategy = strategy;
        }

        this.deploymentId = deploymentId;
        this.deploymentUnit = new KModuleDeploymentUnit(GROUP_ID, ARTIFACT_ID, VERSION);
        assertEquals("Deployment unit information", deploymentId, deploymentUnit.getIdentifier());
        this.mediaType = mediaType;
        this.timeout = timeout;
    }

    public static Builder newBuilderInstance() {
        return new Builder();
    }

    public static class Builder {

        private String deploymentId = null;
        private RuntimeStrategy strategy = RuntimeStrategy.SINGLETON;
        private MediaType mediaType = MediaType.APPLICATION_XML_TYPE;
        private int timeout = DEFAULT_TIMEOUT;

        private Builder() {
            // default constructor
        }

        public Builder setDeploymentId(String deploymentId) {
            this.deploymentId = deploymentId;
            return this;
        }

        public Builder setStrategy(RuntimeStrategy strategy) {
            this.strategy = strategy;
            return this;
        }

        public Builder setMediaType(MediaType mediaType) {
            this.mediaType = mediaType;
            return this;
        }

        public Builder setTimeout(int timeout) {
            this.timeout = timeout;
            return this;
        }

        public RestSmokeIntegrationTestMethods build() {
            if (this.deploymentId == null) {
                throw new IllegalStateException("The deployment id must be set to create the test methods instance!");
            }
            return new RestSmokeIntegrationTestMethods(deploymentId, mediaType, timeout, strategy);
        }
    }

    private JaxbSerializationProvider jaxbSerializationProvider = new JaxbSerializationProvider();
    {
        jaxbSerializationProvider.addJaxbClasses(MyType.class);
    }
    private JsonSerializationProvider jsonSerializationProvider = new JsonSerializationProvider();

    /**
     * Helper methods
     */

    private RestRequestHelper getRestRequestHelper(URL deploymentUrl, String user, String password) {
        return RestRequestHelper.newInstance(deploymentUrl, user, password, timeout, mediaType);
    }

    private RemoteRuntimeEngine getRemoteRuntime(URL deploymentUrl, String user, String password) {
        return RemoteRestRuntimeEngineFactory.newBuilder().addDeploymentId(deploymentId).addUrl(deploymentUrl).addUserName(user)
                .addPassword(password).build();
    }

    /**
     * Test methods
     */

    /**
     * Clone, build and deploy the test deployment unit.
     * 
     * @param deploymentUrl The url where the kie-wb instance is located
     * @param user The user name of a user with permissions to deploy a deployment and run tests
     * @param password The password for the given user
     * @param mediaType The {@link MediaType} to use when sending REST calls.
     * @throws Exception if anything goes wrong
     */
    public void setupDeploymentForTests(URL deploymentUrl, String user, String password) throws Exception {
        RestRepositoryDeploymentUtil deployUtil = new RestRepositoryDeploymentUtil(deploymentUrl, user, password, this.strategy);
        deployUtil.setSleepSeconds(5);
        deployUtil.setTotalTries(6);

        String repoUrl = "https://github.com/droolsjbpm/jbpm-playground.git";
        String repositoryName = "playground";
        String project = "integration-tests";
        String deploymentId = "org.test:kjar:1.0";
        String orgUnit = "integTestUser";
        deployUtil.createAndDeployRepository(repoUrl, repositoryName, project, deploymentId, orgUnit, user);

    }

    public void urlsGetDeployments(URL deploymentUrl, String user, String password) throws Exception {
        // test with normal RestRequestHelper
        RestRequestHelper requestHelper = getRestRequestHelper(deploymentUrl, user, password);

        ClientRequest restRequest = requestHelper.createRequest("deployment/");
        JaxbDeploymentUnitList depList = get(restRequest, mediaType, JaxbDeploymentUnitList.class);
        assertNotNull("Null answer!", depList);
        assertNotNull("Null deployment list!", depList.getDeploymentUnitList());
        assertTrue("Empty deployment list!", depList.getDeploymentUnitList().size() > 0);

        String deploymentId = depList.getDeploymentUnitList().get(0).getIdentifier();
        restRequest = requestHelper.createRequest("deployment/" + deploymentId);
        JaxbDeploymentUnit dep = get(restRequest, mediaType, JaxbDeploymentUnit.class);
        assertNotNull("Null answer!", dep);
        assertNotNull("Null deployment list!", dep);
        assertEquals("Empty status!", JaxbDeploymentStatus.DEPLOYED, dep.getStatus());

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

        JaxbSerializationProvider jaxbSerializer = new JaxbSerializationProvider();
        String xmlStrObj = getConnectionContent(connection.getContent());
        depList = (JaxbDeploymentUnitList) jaxbSerializer.deserialize(xmlStrObj);

        assertNotNull("Null answer!", depList);
        assertNotNull("Null deployment list!", depList.getDeploymentUnitList());
        assertTrue("Empty deployment list!", depList.getDeploymentUnitList().size() > 0);
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

    /**
     * Test Java Remote API for starting processes and managing tasks
     * 
     * @param deploymentUrl
     * @param user
     * @param password
     * @throws Exception
     */
    public void remoteApiHumanTaskProcess(URL deploymentUrl, String user, String password) throws Exception {
        // create REST request
        RemoteRuntimeEngine engine = getRemoteRuntime(deploymentUrl, user, password);
        KieSession ksession = engine.getKieSession();
        ProcessInstance processInstance = ksession.startProcess(HUMAN_TASK_PROCESS_ID);
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

    public void urlsHistoryLogs(URL deploymentUrl, String user, String password) throws Exception {
        RestRequestHelper helper = getRestRequestHelper(deploymentUrl, user, password);

        {
            // Start process
            ClientRequest restRequest = helper.createRequest(
                    "runtime/" + deploymentId + "/process/" + SCRIPT_TASK_VAR_PROCESS_ID + "/start?map_x=initVal");
            logger.debug(">> " + restRequest.getUri());
            JaxbProcessInstanceResponse processInstance = post(restRequest, mediaType, JaxbProcessInstanceResponse.class);
            long scriptTaskVarProcInstId = processInstance.getId();

            // instances/
            restRequest = helper.createRequest("history/instances");
            JaxbHistoryLogList historyLogList = get(restRequest, mediaType, JaxbHistoryLogList.class);
            List<AuditEvent> auditEventList = historyLogList.getResult();

            assertFalse( "Empty list of audit events.", auditEventList.isEmpty() );
            for (AuditEvent event : auditEventList) {
                assertTrue("ProcessInstanceLog", event instanceof ProcessInstanceLog);
                ProcessInstanceLog procLog = (ProcessInstanceLog) event;
                Object[][] out = { { procLog.getDuration(), "duration" }, { procLog.getEnd(), "end date" },
                        { procLog.getExternalId(), "externalId" }, { procLog.getId(), "id" },
                        { procLog.getIdentity(), "identity" }, { procLog.getOutcome(), "outcome" },
                        { procLog.getParentProcessInstanceId(), "parent proc id" }, { procLog.getProcessId(), "process id" },
                        { procLog.getProcessInstanceId(), "process instance id" }, { procLog.getProcessName(), "process name" },
                        { procLog.getProcessVersion(), "process version" }, { procLog.getStart(), "start date" },
                        { procLog.getStatus(), "status" } };
                for (int i = 0; i < out.length; ++i) {
                    // System.out.println(out[i][1] + ": " + out[i][0]);
                }
            }

            // instance/{procInstId}
            ProcessInstanceLog origProcInstLog = ((ProcessInstanceLog) auditEventList.get(0));
            long procInstId = origProcInstLog.getProcessInstanceId();
            restRequest = helper.createRequest("history/instance/" + origProcInstLog.getProcessInstanceId());
            JaxbProcessInstanceLog procInstLog = get(restRequest, mediaType, JaxbProcessInstanceLog.class);
            assertNotNull( "Null process instance log!", procInstLog );
            assertEquals( "Log process instance id", 
                    procInstId, procInstLog.getProcessInstanceId().longValue());
            assertEquals( "Process instance status",
                    origProcInstLog.getStatus(), procInstLog.getStatus());

            // TODO: instance/{procInstId}/child

            // instance/{procInstId}/node
            restRequest = helper.createRequest("history/instance/" + procInstId + "/node" );
            historyLogList = get(restRequest, mediaType, JaxbHistoryLogList.class);
            assertNotNull( "Null process instance log!", historyLogList );
            auditEventList = historyLogList.getResult();
            assertTrue("Empty audit event list!", auditEventList != null && ! auditEventList.isEmpty() );

            // TODO: instance/{procInstId}/variable

            // TODO: instance/{procInstId}/node/{nodeId}

            // instance/{procInstId}/variable/{variable}
            restRequest = helper.createRequest("history/instance/" + scriptTaskVarProcInstId + "/variable/x");
            logger.debug(">> [runtime]" + restRequest.getUri());
            historyLogList = get(restRequest, mediaType, JaxbHistoryLogList.class);
            List<AbstractJaxbHistoryObject> historyVarLogList = historyLogList.getHistoryLogList();

            for (int i = 0; i < historyVarLogList.size(); ++i) {
                JaxbVariableInstanceLog varLog = (JaxbVariableInstanceLog) historyVarLogList.get(i);
                JaxbVariableInstanceLog historyVarLog = (JaxbVariableInstanceLog) historyVarLogList.get(i);
                assertEquals(historyVarLog.getValue(), varLog.getValue());
                assertEquals("Incorrect variable id", "x", varLog.getVariableId());
                assertEquals("Incorrect process id", SCRIPT_TASK_VAR_PROCESS_ID, varLog.getProcessId());
                assertEquals("Incorrect process instance id", scriptTaskVarProcInstId, varLog.getProcessInstanceId().longValue());
            }
        }
        
        // process/{procDefId}
        {
            ClientRequest restRequest = helper.createRequest(
                    "runtime/" + deploymentId + "/process/" + OBJECT_VARIABLE_PROCESS_ID );
            JaxbProcessDefinition procDef = get(restRequest, mediaType, JaxbProcessDefinition.class);
            assertNotNull( "Empty process definition!", procDef);
            assertEquals( "Process definition id", OBJECT_VARIABLE_PROCESS_ID, procDef.getId());
        }

        { 
            String varId = "myobject";
            String varVal = "10";
            ClientRequest restRequest = helper.createRequest(
                    "runtime/" + deploymentId + "/process/" + OBJECT_VARIABLE_PROCESS_ID + "/start?map_" + varId + "=" + varVal);
            JaxbProcessInstanceResponse procInstResp = post(restRequest, mediaType, JaxbProcessInstanceResponse.class);
            long objVarProcInstId = procInstResp.getResult().getId();

            // variable/{varId}

            restRequest = helper.createRequest("history/variable/" + varId);
            JaxbHistoryLogList jhll = get(restRequest, mediaType, JaxbHistoryLogList.class);
            List<VariableInstanceLog> viLogs = new ArrayList<VariableInstanceLog>();
            if (jhll != null) {
                List<AuditEvent> history = jhll.getResult();
                for (AuditEvent ae : history) {
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
            assertEquals("Process instance id", vil.getProcessInstanceId(), objVarProcInstId);
            assertEquals("Variable id", vil.getVariableId(), "myobject");
            assertEquals("Variable value", vil.getValue(), varVal);

            // TODO: variable/{varId}/{value}
            
            // history/variable/{varId}/instances

            restRequest = helper.createRequest("history/variable/" + varId + "/instances");
            jhll = get(restRequest, mediaType, JaxbHistoryLogList.class);

            assertNotNull("Empty ProcesInstanceLog list", jhll);
            List<ProcessInstanceLog> piLogs = new ArrayList<ProcessInstanceLog>();
            if (jhll != null) {
                List<AuditEvent> history = jhll.getResult();
                for (AuditEvent ae : history) {
                    piLogs.add((ProcessInstanceLog) ae);
                }
            }
            assertNotNull("Empty ProcesInstanceLog list", piLogs);
            assertEquals("ProcessInstanceLog list size", piLogs.size(), 1);
            ProcessInstanceLog pi = piLogs.get(0);
            assertNotNull(pi);
            assertEquals(objVarProcInstId, pi.getId());

            // TODO: history/variable/{varId}/value/{val}/instances
        }
    }

    public void urlsGroupAssignmentTest(URL deploymentUrl) throws Exception {
        RestRequestHelper maryReqHelper = RestRequestHelper.newInstance(deploymentUrl, MARY_USER, MARY_PASSWORD);
        RestRequestHelper johnReqHelper = RestRequestHelper.newInstance(deploymentUrl, JOHN_USER, JOHN_PASSWORD);

        ClientRequest restRequest = maryReqHelper.createRequest(
                "runtime/" + deploymentId + "/process/" + GROUP_ASSSIGNMENT_PROCESS_ID + "/start");
        JaxbProcessInstanceResponse procInstResp = post(restRequest, mediaType, JaxbProcessInstanceResponse.class);
        assertEquals(ProcessInstance.STATE_ACTIVE, procInstResp.getState());
        long procInstId = procInstResp.getId();

        // assert the task
        TaskSummary taskSummary = getTaskSummary(maryReqHelper, procInstId, Status.Ready);
        long taskId = taskSummary.getId();
        assertNull(taskSummary.getActualOwner());
        assertNull(taskSummary.getPotentialOwners());
        assertEquals("Task 1", taskSummary.getName());

        // complete 'Task 1' as mary
        restRequest = maryReqHelper.createRequest("task/" + taskId + "/claim");
        ClientResponse<?> responseObj = post(restRequest, mediaType);
        responseObj.releaseConnection();

        restRequest = maryReqHelper.createRequest("task/" + taskId + "/start");
        responseObj = post(restRequest, mediaType);
        responseObj.releaseConnection();
        restRequest = maryReqHelper.createRequest("task/" + taskId + "/complete");
        responseObj = post(restRequest, mediaType);
        responseObj.releaseConnection();

        // now make sure that the next task has been assigned to the
        // correct person. it should be mary.
        taskSummary = getTaskSummary(maryReqHelper, procInstId, Status.Reserved);
        assertEquals("Task 2", taskSummary.getName());
        assertEquals(MARY_USER, taskSummary.getActualOwner().getId());
        taskId = taskSummary.getId();

        // complete 'Task 2' as john
        restRequest = maryReqHelper.createRequest("task/" + taskId + "/release");
        responseObj = post(restRequest, mediaType);
        responseObj.releaseConnection();
        restRequest = johnReqHelper.createRequest("task/" + taskId + "/start");
        responseObj = post(restRequest, mediaType);
        responseObj.releaseConnection();
        restRequest = johnReqHelper.createRequest("task/" + taskId + "/complete");
        responseObj = post(restRequest, mediaType);
        responseObj.releaseConnection();

        // now make sure that the next task has been assigned to the
        // correct person. it should be john.
        taskSummary = getTaskSummary(johnReqHelper, procInstId, Status.Reserved);
        assertEquals("Task 3", taskSummary.getName());
        assertEquals(JOHN_USER, taskSummary.getActualOwner().getId());
        taskId = taskSummary.getId();

        // complete 'Task 3' as john
        restRequest = johnReqHelper.createRequest("task/" + taskId + "/start");
        responseObj = post(restRequest, mediaType);
        responseObj.releaseConnection();
        restRequest = johnReqHelper.createRequest("task/" + taskId + "/complete");
        responseObj = post(restRequest, mediaType);
        responseObj.releaseConnection();

        // assert process finished
        restRequest = maryReqHelper.createRequest("history/instance/" + procInstId);
        JaxbProcessInstanceLog jaxbProcInstLog = get(restRequest, mediaType, JaxbProcessInstanceLog.class);
        ProcessInstanceLog procInstLog = jaxbProcInstLog.getResult();
        assertEquals("Process instance has not completed!", ProcessInstance.STATE_COMPLETED, procInstLog.getStatus().intValue());
    }

    private TaskSummary getTaskSummary(RestRequestHelper requestHelper, long processInstanceId, Status status) throws Exception {
        ClientRequest restRequest = requestHelper.createRequest(
                "task/query?processInstanceId=" + processInstanceId + "&status=" + status.toString());
        JaxbTaskSummaryListResponse taskSumListResp = get(restRequest, mediaType, JaxbTaskSummaryListResponse.class);
        List<TaskSummary> taskSumList = taskSumListResp.getResult();
        assertEquals(1, taskSumList.size());
        return taskSumList.get(0);
    }

    public void remoteApiExtraJaxbClasses(URL deploymentUrl, String user, String password) throws Exception {
            
        // Remote API setup
        RemoteRuntimeEngine engine = getRemoteRuntime(deploymentUrl, user, password);
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
        testParamSerialization(engine, new Float[] { 39.391f });
    }

    private void testParamSerialization(RemoteRuntimeEngine engine, Object param) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("myobject", param);
        KieSession ksession = engine.getKieSession();
        ProcessInstance procInst = ksession.startProcess(OBJECT_VARIABLE_PROCESS_ID, parameters);
        assertNotNull("No process instance returned!", procInst);
        long procInstId = procInst.getId();

        /**
         * Check that MyType was correctly deserialized on server side
         */
        List<VariableInstanceLog> varLogList = engine.getAuditLogService().findVariableInstancesByName("type", false);
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
        varLogList = engine.getAuditLogService().findVariableInstances(procInstId, "type");
        assertNotNull("No variable log list retrieved!", varLogList);
        assertTrue("Variable log list is empty!", varLogList.size() > 0);
    }

    public void remoteApiRuleTaskProcess(URL deploymentUrl, String user, String password) {
        // Remote API setup
        RemoteRuntimeEngine runtimeEngine = getRemoteRuntime(deploymentUrl, user, password);

        KieSession ksession = runtimeEngine.getKieSession();
        AuditLogService auditLogService = runtimeEngine.getAuditLogService();

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
        ProcessInstance pi = ksession.startProcess(RULE_TASK_PROCESS_ID, params);
        assertNotNull("No Process instance returned!", pi);
        ksession.fireAllRules();

        // Check
        // assertEquals("Poor customer", ((Request)ksession.getObject(factHandle)).getInvalidReason());
        assertNull(ksession.getProcessInstance(pi.getId()));

        List<VariableInstanceLog> varLogs = auditLogService.findVariableInstancesByName("requestReason", false);
        for (VariableInstanceLog varLog : varLogs) {
            if (varLog.getProcessInstanceId() == pi.getId()) {
                assertEquals("Poor customer", varLog.getValue());
            }
        }
    }

}
