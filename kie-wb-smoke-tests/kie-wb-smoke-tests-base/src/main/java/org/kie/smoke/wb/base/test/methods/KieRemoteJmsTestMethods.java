package org.kie.smoke.wb.base.test.methods;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.kie.smoke.wb.base.util.TestConstants.EVALUTAION_PROCESS_ID;
import static org.kie.smoke.wb.base.util.TestConstants.HUMAN_TASK_PROCESS_ID;
import static org.kie.smoke.wb.base.util.TestConstants.JOHN_PASSWORD;
import static org.kie.smoke.wb.base.util.TestConstants.JOHN_USER;
import static org.kie.smoke.wb.base.util.TestConstants.KRIS_PASSWORD;
import static org.kie.smoke.wb.base.util.TestConstants.KRIS_USER;
import static org.kie.smoke.wb.base.util.TestConstants.MARY_PASSWORD;
import static org.kie.smoke.wb.base.util.TestConstants.MARY_USER;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jms.Queue;
import javax.naming.InitialContext;

import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.audit.ProcessInstanceLog;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.task.TaskService;
import org.kie.api.task.model.TaskSummary;
import org.kie.remote.client.api.RemoteJmsRuntimeEngineBuilder;
import org.kie.remote.client.api.RemoteJmsRuntimeEngineFactory;
import org.kie.services.client.api.command.RemoteRuntimeEngine;
import org.kie.smoke.wb.base.util.TestConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KieRemoteJmsTestMethods {

    private static final Logger logger = LoggerFactory.getLogger(KieRemoteJmsTestMethods.class);

    private static final String KSESSION_QUEUE_NAME = "jms/queue/KIE.SESSION";
    private static final String RESPONSE_QUEUE_NAME = "jms/queue/KIE.RESPONSE";
    private static final String TASK_QUEUE_NAME = "jms/queue/KIE.TASK";

    // Helper methods ------------------------------------------------------------------------------------------------------------

    /**
     * Test to make sure that JMS commands received by the server are associated with an identity
     * on the server side
     *
     * @param user     The username to use
     * @param password The password associated with the given user name
     */
    public static void remoteApiInitiatorIdentityTest(String deploymentId, InitialContext remoteInitialContext, String user, String password) {
        // setup
        RemoteJmsRuntimeEngineFactory remoteSessionFactory
                = RemoteJmsRuntimeEngineFactory.newBuilder()
                .addDeploymentId(deploymentId)
                .addRemoteInitialContext(remoteInitialContext)
                .addUserName(user)
                .addPassword(password)
                .buildFactory();

        RemoteRuntimeEngine runtimeEngine = remoteSessionFactory.newRuntimeEngine();

        KieSession ksession = runtimeEngine.getKieSession();
        ProcessInstance procInst = ksession.startProcess(HUMAN_TASK_PROCESS_ID);
        assertNotNull("Null process instance!", procInst);
        long procId = procInst.getId();

        List<ProcessInstanceLog> procLogs = (List<ProcessInstanceLog>) runtimeEngine.getAuditService().findActiveProcessInstances(HUMAN_TASK_PROCESS_ID);
        boolean procLogFound = false;
        for (ProcessInstanceLog log : procLogs) {
            if (log == null) {
                continue;
            }
            if (log.getProcessInstanceId() == procId) {
                procLogFound = true;
                assertNotEquals("The identity should not be unknown!", "unknown", log.getIdentity());
            }
        }
        assertTrue("Process instance log could not be found.", procLogFound);
    }



    protected static TaskSummary findTaskSummary(Long procInstId, List<TaskSummary> taskSumList) {
        for (TaskSummary task : taskSumList) {
            if (procInstId.equals(task.getProcessInstanceId())) {
                return task;
            }
        }
        fail("Unable to find task summary for process instance " + procInstId);
        return null;
    }

    public static void remoteApiHumanTaskGroupIdTest(URL deploymentUrl, String deploymentId, InitialContext remoteInitialContext) {
        RemoteJmsRuntimeEngineBuilder jreBuilder
                = RemoteJmsRuntimeEngineFactory.newBuilder()
                .addDeploymentId(deploymentId)
                .useSsl(true)
                .addHostName("localhost")
                .addJmsConnectorPort(5446)
                .addKeystoreLocation(TestConstants.CLIENT_KEY_TRUSTSTORE_LOCATION)
                .addKeystorePassword(TestConstants.CLIENT_KEYSTORE_PASSWORD)
                .useKeystoreAsTruststore();

        try {
            jreBuilder
                    .addTaskServiceQueue((Queue) remoteInitialContext.lookup(TASK_QUEUE_NAME))
                    .addKieSessionQueue((Queue) remoteInitialContext.lookup(KSESSION_QUEUE_NAME))
                    .addResponseQueue((Queue) remoteInitialContext.lookup(RESPONSE_QUEUE_NAME));
        } catch (Exception e) {
            String msg = "Unable to lookup queue instances: " + e.getMessage();
            logger.error(msg, e);
            fail(msg);
        }

        RuntimeEngine krisRuntimeEngine = jreBuilder
                .addUserName(KRIS_USER)
                .addPassword(KRIS_PASSWORD)
                .build();

        RuntimeEngine maryRuntimeEngine = jreBuilder
                .addUserName(MARY_USER)
                .addPassword(MARY_PASSWORD)
                .build();

        RuntimeEngine johnRuntimeEngine = jreBuilder
                .addUserName(JOHN_USER)
                .addPassword(JOHN_PASSWORD)
                .build();

        runHumanTaskGroupIdTest(krisRuntimeEngine, johnRuntimeEngine, maryRuntimeEngine);
    }

    public static void runHumanTaskGroupIdTest(RuntimeEngine krisRuntimeEngine, RuntimeEngine johnRuntimeEngine, RuntimeEngine maryRuntimeEngine) {

        KieSession ksession = krisRuntimeEngine.getKieSession();

        // start a new process instance
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("employee", "krisv");
        params.put("reason", "Yearly performance evaluation");
        ProcessInstance processInstance = ksession.startProcess(EVALUTAION_PROCESS_ID, params);
        assertNotNull("Null process instance!", processInstance);
        long procInstId = processInstance.getId();

        // complete Self Evaluation
        {
            String user = "krisv";
            TaskService taskService = krisRuntimeEngine.getTaskService();
            List<TaskSummary> tasks = taskService.getTasksAssignedAsPotentialOwner(user, "en-UK");
            TaskSummary task = findTaskSummary(procInstId, tasks);
            assertNotNull("Unable to find " + user + "'s task", task);
            taskService.start(task.getId(), user);
            Map<String, Object> results = new HashMap<String, Object>();
            results.put("performance", "exceeding");
            taskService.complete(task.getId(), user, results);
        }

        // john from HR
        {
            String user = "john";
            TaskService taskService = johnRuntimeEngine.getTaskService();
            List<TaskSummary> tasks = taskService.getTasksAssignedAsPotentialOwner(user, "en-UK");
            TaskSummary task = findTaskSummary(procInstId, tasks);
            assertNotNull("Unable to find " + user + "'s task", task);
            taskService.start(task.getId(), user);
            Map<String, Object> results = new HashMap<String, Object>();
            results.put("performance", "acceptable");
            taskService.complete(task.getId(), user, results);
        }

        // mary from PM
        {
            String user = "mary";
            TaskService taskService = maryRuntimeEngine.getTaskService();
            List<TaskSummary> tasks = taskService.getTasksAssignedAsPotentialOwner(user, "en-UK");
            TaskSummary task = findTaskSummary(procInstId, tasks);
            assertNotNull("Unable to find " + user + "'s task", task);
            taskService.start(task.getId(), user);
            Map<String, Object> results = new HashMap<String, Object>();
            results.put("performance", "outstanding");
            taskService.complete(task.getId(), user, results);
        }

        ProcessInstanceLog procInstLog = maryRuntimeEngine.getAuditService().findProcessInstance(procInstId);
    }

}
