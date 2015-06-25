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

package org.kie.remote.client.documentation.jms;

import static org.kie.services.client.serialization.SerializationConstants.DEPLOYMENT_ID_PROPERTY_NAME;
import static org.kie.services.client.serialization.SerializationConstants.SERIALIZATION_TYPE_PROPERTY_NAME;
import static org.kie.services.shared.ServicesVersion.VERSION;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.kie.api.command.Command;  // <1>
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.task.model.TaskSummary;
import org.kie.remote.client.api.RemoteRuntimeEngineFactory;  // <1>
import org.kie.remote.client.api.exception.MissingRequiredInfoException;
import org.kie.remote.client.api.exception.RemoteApiException;
import org.kie.remote.client.api.exception.RemoteCommunicationException;
import org.kie.remote.client.jaxb.ClientJaxbSerializationProvider;
import org.kie.remote.client.jaxb.JaxbCommandsRequest;
import org.kie.remote.client.jaxb.JaxbCommandsResponse;
import org.kie.remote.jaxb.gen.AuditCommand; // <1>
import org.kie.remote.jaxb.gen.GetTaskAssignedAsPotentialOwnerCommand;
import org.kie.remote.jaxb.gen.StartProcessCommand;
import org.kie.remote.jaxb.gen.TaskCommand;
import org.kie.services.client.serialization.JaxbSerializationProvider;
import org.kie.services.client.serialization.SerializationException;
import org.kie.services.client.serialization.SerializationProvider;
import org.kie.services.client.serialization.jaxb.impl.JaxbCommandResponse;
import org.kie.services.client.serialization.jaxb.rest.JaxbExceptionResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SendJmsExample {

    protected static final Logger logger = LoggerFactory.getLogger(SendJmsExample.class);

    public void sendCommandsViaJms( String user, String password, String connectionUser, String connectionPassword,
            String deploymentId, String processId, String hostName ) {

        /**
         * JMS setup
         */
        // Get JNDI context from server
        InitialContext context = RemoteRuntimeEngineFactory.getRemoteJbossInitialContext(hostName, connectionUser, connectionPassword);

        // Create JMS connection
        ConnectionFactory connectionFactory;
        try {
            connectionFactory = (ConnectionFactory) context.lookup("jms/RemoteConnectionFactory");
        } catch( NamingException ne ) {
            throw new RuntimeException("Unable to lookup JMS connection factory.", ne);
        }

        // Setup queues
        Queue sessionQueue, taskQueue, sendQueue, responseQueue;
        try {
            sendQueue = sessionQueue = (Queue) context.lookup("jms/queue/KIE.SESSION");
            taskQueue = (Queue) context.lookup("jms/queue/KIE.TASK");
            responseQueue = (Queue) context.lookup("jms/queue/KIE.RESPONSE");
        } catch( NamingException ne ) {
            throw new RuntimeException("Unable to lookup send or response queue", ne);
        }
        
        /**
         * Command preparation
         */
        StartProcessCommand startProcCmd = new StartProcessCommand();
        startProcCmd.setProcessId(processId);
      
        /**
         * Send command via JMS and receive response
         */
        SerializationProvider serializationProvider = ClientJaxbSerializationProvider.newInstance();
        ProcessInstance procInst = (ProcessInstance) sendJmsCommand(startProcCmd,
                connectionUser, connectionPassword, 
                user, password, deploymentId, null, 
                connectionFactory, sendQueue, responseQueue, 
                serializationProvider, Collections.EMPTY_SET, JaxbSerializationProvider.JMS_SERIALIZATION_TYPE, 
                5 * 1000);
        
        /**
         * Command preparation
         */
        GetTaskAssignedAsPotentialOwnerCommand gtaapoCmd = new GetTaskAssignedAsPotentialOwnerCommand();
        gtaapoCmd.setUserId(user);

        // Send command request
        Long processInstanceId = null; // needed if you're doing an operation on a PER_PROCESS_INSTANCE deployment

        /**
         * Send command via JMS and receive response
         */
        @SuppressWarnings("unchecked")
        List<TaskSummary> taskSumList = (List<TaskSummary>) sendJmsCommand(gtaapoCmd,
                connectionUser, connectionPassword, 
                user, password, deploymentId, processInstanceId, 
                connectionFactory, sendQueue, responseQueue, 
                serializationProvider, Collections.EMPTY_SET, JaxbSerializationProvider.JMS_SERIALIZATION_TYPE, 
                5 * 1000);
        
        long taskId = taskSumList.get(0).getId(); 
    }

    // @formatter:off
    public static Object sendJmsCommand( Command command, 
            String connUser, String connPassword, 
            String userName, String password, String deploymentId, Long processInstanceId, 
            ConnectionFactory factory, Queue sendQueue, Queue responseQueue,
            SerializationProvider serializationProvider, Set<Class<?>> extraJaxbClasses, int serializationType,
            long timeoutInMillisecs ) {
    // @formatter:on

        if( deploymentId == null && !(command instanceof TaskCommand || command instanceof AuditCommand) ) {
            throw new MissingRequiredInfoException("A deployment id is required when sending commands involving the KieSession.");
        }
        JaxbCommandsRequest req; // <2>
        if( command instanceof AuditCommand ) {
            req = new JaxbCommandsRequest(command);
        } else {
            req = new JaxbCommandsRequest(deploymentId, command);
        }

        req.setProcessInstanceId(processInstanceId);
        req.setUser(userName);
        req.setVersion(VERSION);

        Connection connection = null;
        Session session = null;
        JaxbCommandsResponse cmdResponse = null;
        String corrId = UUID.randomUUID().toString();
        String selector = "JMSCorrelationID = '" + corrId + "'";
        try {

            // setup
            MessageProducer producer;
            MessageConsumer consumer;
            try {
                if( password != null ) {
                    connection = factory.createConnection(connUser, connPassword);
                } else {
                    connection = factory.createConnection();
                }
                session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

                producer = session.createProducer(sendQueue);
                consumer = session.createConsumer(responseQueue, selector);

                connection.start();
            } catch( JMSException jmse ) {
                throw new RemoteCommunicationException("Unable to setup a JMS connection.", jmse);
            }

            // Create msg
            TextMessage textMsg; // <3>
            try {

                // serialize request
                String xmlStr = serializationProvider.serialize(req); // <4>
                textMsg = session.createTextMessage(xmlStr);

                // set properties
                // 1. corr id
                textMsg.setJMSCorrelationID(corrId);
                // 2. serialization info
                textMsg.setIntProperty(SERIALIZATION_TYPE_PROPERTY_NAME, serializationType);
                if( extraJaxbClasses != null && !extraJaxbClasses.isEmpty() ) {
                    if( deploymentId == null ) {
                        throw new MissingRequiredInfoException(
                                "Deserialization of parameter classes requires a deployment id, which has not been configured.");
                    }
                    textMsg.setStringProperty(DEPLOYMENT_ID_PROPERTY_NAME, deploymentId);
                }
                // 3. user/pass for task operations
                boolean isTaskCommand = (command instanceof TaskCommand);
                if( isTaskCommand ) {
                    if( userName == null ) {
                        throw new RemoteCommunicationException(
                                "A user name is required when sending task operation requests via JMS");
                    }
                    if( password == null ) {
                        throw new RemoteCommunicationException(
                                "A password is required when sending task operation requests via JMS");
                    }
                    textMsg.setStringProperty("username", userName);
                    textMsg.setStringProperty("password", password);
                }
                // 4. process instance id
            } catch( JMSException jmse ) {
                throw new RemoteCommunicationException("Unable to create and fill a JMS message.", jmse);
            } catch( SerializationException se ) {
                throw new RemoteCommunicationException("Unable to deserialze JMS message.", se.getCause());
            }

            // send
            try {
                producer.send(textMsg);
            } catch( JMSException jmse ) {
                throw new RemoteCommunicationException("Unable to send a JMS message.", jmse);
            }

            // receive
            Message response;
            try {
                response = consumer.receive(timeoutInMillisecs);
            } catch( JMSException jmse ) {
                throw new RemoteCommunicationException("Unable to receive or retrieve the JMS response.", jmse);
            }

            if( response == null ) {
                logger.warn("Response is empty");
                return null;
            }
            // extract response
            assert response != null: "Response is empty.";
            try {
                String xmlStr = ((TextMessage) response).getText();
                cmdResponse = (JaxbCommandsResponse) serializationProvider.deserialize(xmlStr); // <4>
            } catch( JMSException jmse ) {
                throw new RemoteCommunicationException("Unable to extract " + JaxbCommandsResponse.class.getSimpleName()
                        + " instance from JMS response.", jmse);
            } catch( SerializationException se ) {
                throw new RemoteCommunicationException("Unable to extract " + JaxbCommandsResponse.class.getSimpleName()
                        + " instance from JMS response.", se.getCause());
            }
            assert cmdResponse != null: "Jaxb Cmd Response was null!";
        } finally {
            if( connection != null ) {
                try {
                    connection.close();
                    if( session != null ) {
                        session.close();
                    }
                } catch( JMSException jmse ) {
                    logger.warn("Unable to close connection or session!", jmse);
                }
            }
        }

        String version = cmdResponse.getVersion();
        if( version == null ) {
            version = "pre-6.0.3";
        }
        if( !version.equals(VERSION) ) {
            logger.info("Response received from server version [{}] while client is version [{}]! This may cause problems.",
                    version, VERSION);
        }

        List<JaxbCommandResponse<?>> responses = cmdResponse.getResponses();
        if( responses.size() > 0 ) {
            JaxbCommandResponse<?> response = responses.get(0);
            if( response instanceof JaxbExceptionResponse ) {
                JaxbExceptionResponse exceptionResponse = (JaxbExceptionResponse) response;
                throw new RemoteApiException(exceptionResponse.getMessage());
            } else {
                return response.getResult();
            }
        } else {
            assert responses.size() == 0: "There should only be 1 response, not " + responses.size() + ", returned by a command!";
            return null;
        }
    }
}
