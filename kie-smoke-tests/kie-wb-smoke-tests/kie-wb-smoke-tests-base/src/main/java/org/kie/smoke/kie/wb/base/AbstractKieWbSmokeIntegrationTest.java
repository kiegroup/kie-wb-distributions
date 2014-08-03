package org.kie.smoke.kie.wb.base;

import static org.kie.smoke.kie.wb.base.util.TestConstants.*;

import java.net.URL;

import javax.ws.rs.core.MediaType;

import org.jboss.arquillian.junit.InSequence;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.junit.AfterClass;
import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.Test;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.internal.runtime.conf.RuntimeStrategy;
import org.kie.services.client.api.RemoteJmsRuntimeEngineFactory;
import org.kie.smoke.kie.wb.base.methods.JmsSmokeIntegrationTestMethods;
import org.kie.smoke.kie.wb.base.methods.RestSmokeIntegrationTestMethods;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractKieWbSmokeIntegrationTest {
    
    protected static final Logger logger = LoggerFactory.getLogger(AbstractKieWbSmokeIntegrationTest.class);
    
    private final RestSmokeIntegrationTestMethods restTests;
    private final JmsSmokeIntegrationTestMethods jmsTests;

    @ArquillianResource
    URL deploymentUrl;
   
    public abstract MediaType getMediaType();
    public abstract boolean jmsQueuesAvailable();
    public abstract RuntimeStrategy getStrategy();
    public abstract int getTimeout();
   
    public AbstractKieWbSmokeIntegrationTest() { 
         restTests = RestSmokeIntegrationTestMethods.newBuilderInstance()
                 .setDeploymentId(KJAR_DEPLOYMENT_ID)
                 .setMediaType(getMediaType())
                 .setStrategy(getStrategy())
                 .setTimeout(getTimeout())
                 .build();
         if( jmsQueuesAvailable() ) { 
             jmsTests = new JmsSmokeIntegrationTestMethods(KJAR_DEPLOYMENT_ID);
         } else { 
             jmsTests = null;
         }
    }

    private final static int SETUP = 0;
    
    private final static int REST_SUCCEEDING = 1;
    
    private final static int JMS_SUCCEEDING = 2;
    
    @BeforeClass
    public static void waitForDeployedKmodulesToLoad() throws InterruptedException {
        long sleep = 2;
        logger.info("Waiting " + sleep + " secs for server to finish starting up.");
        Thread.sleep(sleep*1000);
    }

    @AfterClass
    public static void waitForTxOnServer() throws InterruptedException {
        long sleep = 1;
        logger.info("Waiting " + sleep + " secs for tx's on server to close.");
        Thread.sleep(sleep*1000);
    }

    protected void printTestName() { 
        String testName = Thread.currentThread().getStackTrace()[2].getMethodName();
        System.out.println( "-=> " + testName );
    }
    
    @Test
    @InSequence(SETUP)
    public void setupDeployment() throws Exception {
        printTestName();
        restTests.setupDeploymentForTests(deploymentUrl, MARY_USER, MARY_PASSWORD);
        Thread.sleep(5000);
    }

    @Test
    @InSequence(REST_SUCCEEDING)
    public void testUrlsGetDeployments() throws Exception {
        printTestName();
        restTests.urlsGetDeployments(deploymentUrl, MARY_USER, MARY_PASSWORD);
    }

    @Test
    @InSequence(REST_SUCCEEDING)
    public void testRestHistoryLogs() throws Exception {
        printTestName();
        restTests.urlsHistoryLogs(deploymentUrl, MARY_USER, MARY_PASSWORD);
    }

    @Test
    @InSequence(REST_SUCCEEDING)
    public void testRestRemoteApiHumanTaskProcess() throws Exception {
        printTestName();
        restTests.remoteApiHumanTaskProcess(deploymentUrl, MARY_USER, MARY_PASSWORD);
    }

    @Test
    @InSequence(REST_SUCCEEDING)
    public void testRestRemoteApiExtraJaxbClasses() throws Exception {
        printTestName();
        restTests.remoteApiExtraJaxbClasses(deploymentUrl, MARY_USER, MARY_PASSWORD);
    }

    @Test
    @InSequence(REST_SUCCEEDING)
    public void testRestRemoteApiRuleTaskProcess() throws Exception {
        printTestName();
        restTests.remoteApiRuleTaskProcess(deploymentUrl, MARY_USER, MARY_PASSWORD);
    }

    @Test
    @InSequence(REST_SUCCEEDING)
    public void testRestUrlsGroupAssignmentProcess() throws Exception { 
        printTestName();
        restTests.urlsGroupAssignmentTest(deploymentUrl);
    }
   
    // JMS ------------------------------------------------------------------------------------------------------------------------
    
    @Test
    @InSequence(JMS_SUCCEEDING)
    public void testJmsRemoteApiStartProcessInstanceInitiator() throws Exception { 
        Assume.assumeTrue(jmsQueuesAvailable());
        printTestName();
        jmsTests.remoteApiInitiatorIdentityTest(MARY_USER, MARY_PASSWORD);
    }
    
    @Test
    @InSequence(JMS_SUCCEEDING)
    public void testJmsRemoteApiHumanTaskGroupId() throws Exception { 
        Assume.assumeTrue(jmsQueuesAvailable());
        printTestName();
        
        jmsTests.remoteApiHumanTaskGroupIdTest(deploymentUrl);
    }
    
}
