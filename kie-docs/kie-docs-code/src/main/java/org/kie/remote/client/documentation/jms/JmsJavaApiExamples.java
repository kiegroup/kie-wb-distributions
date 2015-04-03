package org.kie.remote.client.documentation.jms;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jms.ConnectionFactory;
import javax.jms.Queue;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.task.TaskService;
import org.kie.api.task.model.Task;
import org.kie.api.task.model.TaskSummary;
import org.kie.remote.client.api.RemoteRuntimeEngineFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JmsJavaApiExamples {

    /**
     * 1: the names of these objects may be different depending on your application server
     * 2: Here we add the connection factory and queue instances
     * 3: if we are not using SSL, then we have to explictly do that. SSL is necessary 
     * for security reasons (plain-text password in message) when doing task operations
     * via the remote Java API. 
     */
    // @formatter:off
    // tag::connFactoryJmsBuilderExample[]
    
    private static final String CONNECTION_FACTORY_NAME = "jms/RemoteConnectionFactory"; // <1>
    private static final String KSESSION_QUEUE_NAME = "jms/queue/KIE.SESSION";
    private static final String TASK_QUEUE_NAME = "jms/queue/KIE.TASK";
    private static final String RESPONSE_QUEUE_NAME = "jms/queue/KIE.RESPONSE"; 

    public void startProcessViaJmsRemoteJavaAPI(String hostname, int jmsConnPort, 
            String deploymentId, String user, String password, 
            String processId) throws NamingException {

        InitialContext remoteInitialContext = getRemoteInitialContext();

        String queueName = KSESSION_QUEUE_NAME;
        Queue sessionQueue = (Queue) remoteInitialContext.lookup(queueName);
        queueName = TASK_QUEUE_NAME;
        Queue taskQueue = (Queue) remoteInitialContext.lookup(queueName);
        queueName = RESPONSE_QUEUE_NAME;
        Queue responseQueue = (Queue) remoteInitialContext.lookup(queueName);

        String connFactoryName = CONNECTION_FACTORY_NAME;
        ConnectionFactory connFact = (ConnectionFactory) remoteInitialContext.lookup(connFactoryName);

        RuntimeEngine engine = RemoteRuntimeEngineFactory.newJmsBuilder()
                .addDeploymentId(deploymentId)
                .addConnectionFactory(connFact) // <2> 
                .addKieSessionQueue(sessionQueue)
                .addTaskServiceQueue(taskQueue)
                .addResponseQueue(responseQueue)
                .addUserName(user)
                .addPassword(password)
                .addHostName(hostname)
                .addJmsConnectorPort(jmsConnPort)
                .disableTaskSecurity() // <3>
                .build();

        // Create KieSession instances and use them
        KieSession ksession = engine.getKieSession();
        
        // Each operation on a KieSession, TaskService or AuditLogService (client) instance 
        // sends a request for the operation to the server side and waits for the response
        // If something goes wrong on the server side, the client will throw an exception. 
        ProcessInstance processInstance = ksession.startProcess(processId);
        long procId = processInstance.getId();
    }
    // end::connFactoryJmsBuilderExample[]
   
    // tag::initContextJmsBuilderExample[]
    public void startProcessViaJmsRemoteJavaAPIInitialContext(String hostname, int jmsConnPort, 
            String deploymentId, String user, String password,
            String processId) {

        // See your application server documentation for how to initialize
        // a remote InitialContext instance for your server instance
        InitialContext remoteInitialContext = getRemoteInitialContext();

        RuntimeEngine engine = RemoteRuntimeEngineFactory.newJmsBuilder()
                .addDeploymentId(deploymentId)
                .addRemoteInitialContext(remoteInitialContext)
                .addUserName(user)
                .addPassword(password)
                .build();

        // Create KieSession instances and use them
        KieSession ksession = engine.getKieSession();
        
        // Each operation on a KieSession, TaskService or AuditLogService (client) instance 
        // sends a request for the operation to the server side and waits for the response
        // If something goes wrong on the server side, the client will throw an exception. 
        ProcessInstance processInstance = ksession.startProcess(processId);
        long procId = processInstance.getId();
    }
    // end::initContextJmsBuilderExample[]
    // @formatter:on
    
    // @formatter:off
    // tag::jbossHostNameJmsBuilderExample[]
    public void startProcessViaJmsRemoteJavaAPI(String hostNameOrIpAdress, 
            String deploymentId, String user, String password, 
            String processId) {
       
        // this requires that you also have the following dependencies
        // - org.jboss.as:jboss-naming artifact appropriate to the EAP version you're using
        RuntimeEngine engine = RemoteRuntimeEngineFactory.newJmsBuilder()
                .addJbossServerHostName(hostNameOrIpAdress)
                .addDeploymentId(deploymentId)
                .addUserName(user)
                .addPassword(password)
                .build();
    }
    // end::jbossHostNameJmsBuilderExample[]
    // @formatter:on

    private static InitialContext getRemoteInitialContext() { 
       return null; 
    }
   
    private static Logger logger = LoggerFactory.getLogger(JmsJavaApiExamples.class);
    
    // @formatter:off
    // tag::sslJmsBuilderExample[]
    public void startProcessAndHandleTaskViaJmsRemoteJavaAPISsl(String hostNameOrIpAdress, int jmsSslConnectorPort,
            String deploymentId, String user, String password, 
            String keyTrustStoreLocation, String keyTrustStorePassword,
            String processId) {

        InitialContext remoteInitialContext = getRemoteInitialContext();
        
        RuntimeEngine engine = RemoteRuntimeEngineFactory.newJmsBuilder()
                .addDeploymentId(deploymentId)
                .addRemoteInitialContext(remoteInitialContext)
                .addUserName(user)
                .addPassword(password)
                .addHostName(hostNameOrIpAdress)
                .addJmsConnectorPort(jmsSslConnectorPort)
                .useKeystoreAsTruststore()
                .addKeystoreLocation(keyTrustStoreLocation)
                .addKeystorePassword(keyTrustStorePassword)
                .build();

        // create JMS request
        KieSession ksession = engine.getKieSession();
        ProcessInstance processInstance = ksession.startProcess(processId);
        long procInstId = processInstance.getId();
        logger.debug("Started process instance: " + procInstId );

        TaskService taskService = engine.getTaskService();
        List<TaskSummary> taskSumList 
            = taskService.getTasksAssignedAsPotentialOwner(user, "en-UK");
        TaskSummary taskSum = null;
        for( TaskSummary taskSumElem : taskSumList ) { 
            if( taskSumElem.getProcessInstanceId().equals(procInstId) ) {
                taskSum = taskSumElem;
            }
        }
        long taskId = taskSum.getId();
        logger.debug("Found task " + taskId);
        
        // get other info from task if you want to
        Task task = taskService.getTaskById(taskId);
        logger.debug("Retrieved task " + taskId );
        
        taskService.start(taskId, user);
        
        Map<String, Object> resultData = new HashMap<String, Object>();
        // insert results for task in resultData
        taskService.complete(taskId, user, resultData);
        logger.debug("Completed task " + taskId );
    }
    // end::sslJmsBuilderExample[]
    // @formatter:on
    
}
