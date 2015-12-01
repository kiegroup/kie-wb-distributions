package org.kie.smoke.wb.base.test.methods;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.kie.smoke.wb.base.util.TestConstants.ARTIFACT_ID;
import static org.kie.smoke.wb.base.util.TestConstants.GROUP_ID;
import static org.kie.smoke.wb.base.util.TestConstants.KJAR_DEPLOYMENT_ID;
import static org.kie.smoke.wb.base.util.TestConstants.MARY_PASSWORD;
import static org.kie.smoke.wb.base.util.TestConstants.MARY_USER;
import static org.kie.smoke.wb.base.util.TestConstants.SCRIPT_TASK_PROCESS_ID;
import static org.kie.smoke.wb.base.util.TestConstants.VERSION;

import java.net.URL;
import java.util.Arrays;

import org.jbpm.kie.services.impl.KModuleDeploymentUnit;
import org.kie.internal.runtime.conf.RuntimeStrategy;
import org.kie.remote.client.api.RemoteRuntimeEngineFactory;
import org.kie.remote.client.jaxb.ClientJaxbSerializationProvider;
import org.kie.remote.client.jaxb.JaxbCommandsRequest;
import org.kie.remote.client.jaxb.JaxbCommandsResponse;
import org.kie.remote.jaxb.gen.JaxbStringObjectPairArray;
import org.kie.remote.jaxb.gen.StartProcessCommand;
import org.kie.remote.jaxb.gen.util.JaxbStringObjectPair;
import org.kie.remote.services.ws.command.generated.CommandWebService;
import org.kie.services.client.serialization.JaxbSerializationProvider;
import org.kie.services.client.serialization.JsonSerializationProvider;
import org.kie.services.client.serialization.jaxb.impl.JaxbCommandResponse;
import org.kie.services.client.serialization.jaxb.impl.process.JaxbProcessInstanceResponse;
import org.kie.tests.MyType;
import org.kie.tests.Person;
import org.kie.tests.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KieRemoteWebserviceTestMethods {

    private static final Logger logger = LoggerFactory.getLogger(KieRemoteWebserviceTestMethods.class);

    private static final String deploymentId = KJAR_DEPLOYMENT_ID;
    private static final KModuleDeploymentUnit deploymentUnit = new KModuleDeploymentUnit(GROUP_ID, ARTIFACT_ID, VERSION);
    static {
        assertEquals("Returned deployment unit identifier is incorrect!", deploymentId, deploymentUnit.getIdentifier());
    }

    /**
     * Helper methods
     */

    private static CommandWebService createCommandServiceClient(URL deploymentUrl, String deploymentId, String user, String pwd) throws Exception {
        CommandWebService client =
        RemoteRuntimeEngineFactory.newCommandWebServiceClientBuilder()
            .addServerUrl(deploymentUrl)
            .addUserName(user)
            .addPassword(pwd)
            .addDeploymentId(KJAR_DEPLOYMENT_ID)
            .addExtraJaxbClasses(MyType.class)
            .buildBasicAuthClient();

        return client;
    }

    public static void doTestStartSimpleProcess(URL deploymentUrl) throws Exception {
        CommandWebService commandWebService = createCommandServiceClient(deploymentUrl, deploymentId, MARY_USER, MARY_PASSWORD);

        logger.info("[Client] Webservice request.");
        // create request object
        StartProcessCommand cmd = new StartProcessCommand();
        cmd.setProcessId(SCRIPT_TASK_PROCESS_ID);
        JaxbStringObjectPairArray map = new JaxbStringObjectPairArray();
        JaxbStringObjectPair keyValue = new JaxbStringObjectPair();
        keyValue.setKey("myobject");
        keyValue.setValue(new MyType("variable", 29));
        map.getItems().add(keyValue);
        cmd.setParameter(map);
        JaxbCommandsRequest req = new JaxbCommandsRequest(KJAR_DEPLOYMENT_ID, cmd);

        // Get a response from the WebService
        final JaxbCommandsResponse response = commandWebService.execute(req);
        assertNotNull( "Null webservice response", response );
        assertFalse( "Empty webservice response", response.getResponses().isEmpty() );

        // check response
        JaxbCommandResponse<?> cmdResp = response.getResponses().get(0);
        assertNotNull( "Null command response", cmdResp );
        if( ! (cmdResp instanceof JaxbProcessInstanceResponse) ) {
            System.out.println( "!!: " + cmdResp.getClass().getSimpleName() );
            assertTrue( "Incorrect cmd response type", cmdResp instanceof JaxbProcessInstanceResponse );
        }

        logger.info("[WebService] response: {} [{}]",
                ((JaxbProcessInstanceResponse) cmdResp).getId(),
                ((JaxbProcessInstanceResponse) cmdResp).getProcessId()
                );
    }

}
