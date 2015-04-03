package org.kie.remote.client.documentation.rest;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.task.TaskService;
import org.kie.api.task.model.TaskSummary;
import org.kie.remote.client.api.RemoteRuntimeEngineFactory;
import org.kie.remote.client.documentation.objects.MyType;

//TODO: changed, add to documentation
public class RestJavaApiExamples {

    // @formatter:off
    public void simpleRestBuilderExample() throws Exception { 
        String appName = "kie-wb";
        
        // tag::simpleBuilderExample[]
        String deploymentId = "com.burns.reactor:homer:1.0";
        String serverInstanceUrl = "http://localhost:8080/" + appName;

        RuntimeEngine engine = RemoteRuntimeEngineFactory.newRestBuilder() // <1>
            .addDeploymentId(deploymentId) // <2> 
            .addUrl(new URL(serverInstanceUrl))
            .addUserName("homer")
            .addPassword("d0nut5R!!!")
            .build(); // <3>

        KieSession kieSessionClient = engine.getKieSession(); 
        // end::simpleBuilderExample[]
    }
    // @formatter:on

    // @formatter:off
    // tag::simpleRestBuilderExample[]
    public void startProcessAndHandleTaskViaRestRemoteJavaAPI(URL serverRestUrl, String deploymentId, String user, String password) {
        // the serverRestUrl should contain a URL similar to "http://localhost:8080/jbpm-console/"
        
        // Setup the factory class with the necessarry information to communicate with the REST services
        RuntimeEngine engine = RemoteRuntimeEngineFactory.newRestBuilder()
            .addUrl(serverRestUrl)
            .addTimeout(5)
            .addDeploymentId(deploymentId)
            .addUserName(user)
            .addPassword(password)
            // if you're sending custom class parameters, make sure that
            // the remote client instance knows about them! 
            .addExtraJaxbClasses(MyType.class)
            .build();

        // Create KieSession and TaskService instances and use them
        KieSession ksession = engine.getKieSession();
        TaskService taskService = engine.getTaskService();

        // Each operation on a KieSession, TaskService or AuditLogService (client) instance 
        // sends a request for the operation to the server side and waits for the response
        // If something goes wrong on the server side, the client will throw an exception. 
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("paramName", new MyType("name", 23));
        ProcessInstance processInstance 
            = ksession.startProcess("com.burns.reactor.maintenance.cycle", params);
        long procId = processInstance.getId();

        String taskUserId = user;
        taskService = engine.getTaskService();
        List<TaskSummary> tasks = taskService.getTasksAssignedAsPotentialOwner(user, "en-UK");

        long taskId = -1;
        for (TaskSummary task : tasks) {
            if (task.getProcessInstanceId() == procId) {
                taskId = task.getId();
            }
        }

        if (taskId == -1) {
            throw new IllegalStateException("Unable to find task for " + user + 
                    " in process instance " + procId);
        }

        taskService.start(taskId, taskUserId);
      
        // resultData can also just be null
        Map<String, Object> resultData = new HashMap<String, Object>(); 
        taskService.complete(taskId, taskUserId, resultData);
    }
    // end::simpleRestBuilderExample[]
    // @formatter:on
}
