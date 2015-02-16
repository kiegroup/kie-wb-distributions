package org.kie.smoke.wb.ws;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.kie.smoke.wb.util.TestConstants.ARTIFACT_ID;
import static org.kie.smoke.wb.util.TestConstants.GROUP_ID;
import static org.kie.smoke.wb.util.TestConstants.KJAR_DEPLOYMENT_ID;
import static org.kie.smoke.wb.util.TestConstants.MARY_PASSWORD;
import static org.kie.smoke.wb.util.TestConstants.MARY_USER;
import static org.kie.smoke.wb.util.TestConstants.VERSION;

import java.net.URL;
import java.util.Arrays;

import javax.ws.rs.core.MediaType;

import org.jbpm.kie.services.impl.KModuleDeploymentUnit;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
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
import org.kie.smoke.wb.AbstractWorkbenchIntegrationTest;
import org.kie.tests.MyType;
import org.kie.tests.Person;
import org.kie.tests.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KieRemoteWebServiceSmokeIntegrationTest extends AbstractWorkbenchIntegrationTest {

    private static final Logger logger = LoggerFactory.getLogger(KieRemoteWebServiceSmokeIntegrationTest.class);

    private final String deploymentId;
    private final KModuleDeploymentUnit deploymentUnit;
    private static final RuntimeStrategy strategy = RuntimeStrategy.SINGLETON;

    private static final String SCRIPT_TASK_PROCESS_ID = "org.test.kjar.scripttask";
    
    private MediaType mediaType;
    private int timeout;
    private static final int DEFAULT_TIMEOUT = 10;

    public KieRemoteWebServiceSmokeIntegrationTest() {
        this.deploymentId = KJAR_DEPLOYMENT_ID;
        this.mediaType = MediaType.APPLICATION_XML_TYPE; // TODO run the tests with both XML and JSON?
        this.timeout = 1000;
        this.deploymentUnit = new KModuleDeploymentUnit(GROUP_ID, ARTIFACT_ID, VERSION);
        assertEquals("Returned deployment unit identifier is incorrect!", deploymentId, deploymentUnit.getIdentifier());
    }

    private static JaxbSerializationProvider jaxbSerializationProvider;

    {
        Class<?>[] extraClasses = {MyType.class, Person.class, Request.class};
        jaxbSerializationProvider = ClientJaxbSerializationProvider.newInstance(Arrays.asList(extraClasses));

    }

    private JsonSerializationProvider jsonSerializationProvider = new JsonSerializationProvider();

    /**
     * Helper methods
     */

    private CommandWebService createCommandServiceClient(URL deploymentUrl, String deploymentId, String user, String pwd) throws Exception {
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

    /**
     * Clone, build and deploy the test deployment unit.
     */
    @BeforeClass
    public static void setupDeployment() throws Exception {
        deployJbpmPlayGroundIntegrationTests(RuntimeStrategy.SINGLETON);
    }

    @AfterClass
    public static void waitForTxOnServer() throws InterruptedException {
        long sleep = 1;
        logger.info("Waiting " + sleep + " secs for tx's on server to close.");
        Thread.sleep(sleep * 1000);
    }

    @Test
    public void startSimpleProcess() throws Exception {
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