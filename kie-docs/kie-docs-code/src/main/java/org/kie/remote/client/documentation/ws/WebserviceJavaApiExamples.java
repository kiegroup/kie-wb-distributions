/*
 * Copyright 2015 JBoss Inc
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

package org.kie.remote.client.documentation.ws;

// @formatter:off
// tag::simpleWebserviceBuilderExample[]
import java.net.URL;
import java.util.List;
import java.util.Map;

import org.kie.api.command.Command;
import org.kie.api.task.model.Task;
import org.kie.remote.client.documentation.objects.MyType;
import org.kie.remote.client.jaxb.JaxbCommandsRequest;
import org.kie.remote.client.jaxb.JaxbCommandsResponse;
import org.kie.remote.jaxb.gen.GetTaskCommand; // <1>
import org.kie.remote.jaxb.gen.GetTaskContentCommand;
import org.kie.remote.jaxb.gen.GetTasksByProcessInstanceIdCommand;
import org.kie.remote.jaxb.gen.JaxbStringObjectPairArray;
import org.kie.remote.jaxb.gen.StartProcessCommand;
import org.kie.remote.jaxb.gen.util.JaxbStringObjectPair;
import org.kie.remote.services.ws.command.generated.CommandWebService;
import org.kie.remote.services.ws.command.generated.CommandWebServiceException;
import org.kie.services.client.api.RemoteRuntimeEngineFactory;
import org.kie.services.client.serialization.jaxb.impl.JaxbCommandResponse;
import org.kie.services.client.serialization.jaxb.impl.JaxbLongListResponse;
import org.kie.services.client.serialization.jaxb.impl.process.JaxbProcessInstanceResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebserviceJavaApiExamples {

    private Logger logger = LoggerFactory.getLogger(WebserviceJavaApiExamples.class); 
   
    /**
     * Create the webservice client
     * @param applicationUrl Something like "http://localhost:8080/kie-wb/" 
     *                       or "http://localhost:8080/business-central/"
     * @param user The user doing the webservice requests
     * @param password The user's password
     * @param deploymentId The deployment id that the request will interact with
     * @return A {@link CommandWebService} client instance
     */
    private static CommandWebService createWebserviceClient(URL applicationUrl, 
            String user, String password, String deploymentId) {
        CommandWebService client =
        RemoteRuntimeEngineFactory.newCommandWebServiceClientBuilder()
            .addServerUrl(applicationUrl)
            .addUserName(user)
            .addPassword(password)
            .addDeploymentId(deploymentId)
            .addExtraJaxbClasses(MyType.class) // <2>
            .buildBasicAuthClient();
        
        return client;
    }

    /**
     * Send a webservice request with a single command
     * @param service The webserivce client instance
     * @param cmd The command that we're sending (see the 
     *            'org.kie.remote.jaxb.gen package' in kie-remote-client)
     * @param respClass The class that we expect as a response
     * @param deploymentId The id of the deployment that we will interact with
     * @return the response object
     * @throws CommandWebServiceException if the webservice operation fails
     */
    private static <T> T doWebserviceSingleCommandRequest(CommandWebService service, 
            Command<?> cmd, Class<T> respClass, String deploymentId) 
                    throws CommandWebServiceException { 
        // Send request and get response from the WebService
        JaxbCommandsRequest req = new JaxbCommandsRequest(deploymentId, cmd);
        JaxbCommandsResponse response = service.execute(req);
    
        // Unwrap response
        JaxbCommandResponse<?> cmdResp = response.getResponses().get(0);
        
        return (T) cmdResp;
    }

    /**
     * Start a simple process, and retrieve the task information and content 
     * via the webservice
     * 
     * @param applicationUrl Something like "http://localhost:8080/kie-wb/" 
     *                       or "http://localhost:8080/business-central/"
     * @param user The user doing the webservice requests
     * @param password The user's password
     * @param deploymentId The deployment id that the request will interact with
     * @param processId The id of the process we want to start
     * @throws Exception if something goes wrong
     */
    public static void startSimpleProcess(URL applicationUrl, 
            String user, String password, String deploymentId, String processId) 
                    throws Exception {
        
        CommandWebService commandWebService 
            = createWebserviceClient(applicationUrl, user, password, deploymentId); 
        
        // Create start process command
        StartProcessCommand spc = new StartProcessCommand();
        spc.setProcessId(processId);
        JaxbStringObjectPairArray map = new JaxbStringObjectPairArray();
        JaxbStringObjectPair keyValue = new JaxbStringObjectPair();
        keyValue.setKey("myobject");
        keyValue.setValue(new MyType("variable", 29));
        map.getItems().add(keyValue);
        spc.setParameter(map);
        
        // Do webService request
        JaxbProcessInstanceResponse jpir 
            = doWebserviceSingleCommandRequest(commandWebService, spc, 
                    JaxbProcessInstanceResponse.class, deploymentId);
        long procInstId = ((JaxbProcessInstanceResponse) jpir).getId();
       
        // Create command
        GetTasksByProcessInstanceIdCommand gtbic = new GetTasksByProcessInstanceIdCommand();
        gtbic.setProcessInstanceId(procInstId);
       
        // Do webservice request
        JaxbLongListResponse jllr 
            = doWebserviceSingleCommandRequest(commandWebService, gtbic, 
                    JaxbLongListResponse.class, deploymentId );
        List<Long> taskIds = jllr.getResult();
        long taskId = taskIds.get(0);
       
        // Commands for task and task content 
        GetTaskCommand gtc = new GetTaskCommand();
        gtc.setTaskId(taskId);
        GetTaskContentCommand gtcc = new GetTaskContentCommand();
        gtcc.setTaskId(taskId);
        
        // Do webservice request (with both commands) 
        JaxbCommandsRequest req = new JaxbCommandsRequest(deploymentId, gtc);
        req.getCommands().add(gtcc); // <3>
        JaxbCommandsResponse response = commandWebService.execute(req);
        
        // Get task and content response
        Task task = (Task) response.getResponses().get(0).getResult();
        Map<String, Object> contentMap 
            = (Map<String, Object>) response.getResponses().get(1).getResult();
    }
    
}
// end::simpleWebserviceBuilderExample[]
// @formatter:on
