package org.kie.smoke.wb.base;

import static org.kie.smoke.wb.util.TestConstants.*;

import java.net.URL;

import javax.ws.rs.core.MediaType;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.kie.internal.runtime.conf.RuntimeStrategy;
import org.kie.smoke.wb.base.methods.JmsSmokeIntegrationTestMethods;
import org.kie.smoke.wb.base.methods.RestSmokeIntegrationTestMethods;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractKieWbSmokeIntegrationTest {
    
    protected static final Logger logger = LoggerFactory.getLogger(AbstractKieWbSmokeIntegrationTest.class);
    
    private final RestSmokeIntegrationTestMethods restTests;
    private final JmsSmokeIntegrationTestMethods jmsTests;

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
    
//    @Test
//    public void setupDeployment() throws Exception {
//        printTestName();
//        restTests.setupDeploymentForTests(deploymentUrl, MARY_USER, MARY_PASSWORD);
//        Thread.sleep(5000);
//    }
//
//    @Test
//    public void testUrlsGetDeployments() throws Exception {
//        printTestName();
//        restTests.urlsGetDeployments(deploymentUrl, MARY_USER, MARY_PASSWORD);
//    }
//
//    @Test
//    public void testRestHistoryLogs() throws Exception {
//        printTestName();
//        restTests.urlsHistoryLogs(deploymentUrl, MARY_USER, MARY_PASSWORD);
//    }
//
//    @Test
//    public void testRestRemoteApiHumanTaskProcess() throws Exception {
//        printTestName();
//        restTests.remoteApiHumanTaskProcess(deploymentUrl, MARY_USER, MARY_PASSWORD);
//    }
//
//    @Test
//    public void testRestRemoteApiExtraJaxbClasses() throws Exception {
//        printTestName();
//        restTests.remoteApiExtraJaxbClasses(deploymentUrl, MARY_USER, MARY_PASSWORD);
//    }
//
//    @Test
//    public void testRestRemoteApiRuleTaskProcess() throws Exception {
//        printTestName();
//        restTests.remoteApiRuleTaskProcess(deploymentUrl, MARY_USER, MARY_PASSWORD);
//    }
//
//    @Test
//    public void testRestUrlsGroupAssignmentProcess() throws Exception {
//        printTestName();
//        restTests.urlsGroupAssignmentTest(deploymentUrl);
//    }
//
    // JMS ------------------------------------------------------------------------------------------------------------------------
    
//    @Test
//    public void testJmsRemoteApiStartProcessInstanceInitiator() throws Exception {
//        Assume.assumeTrue(jmsQueuesAvailable());
//        printTestName();
//        jmsTests.remoteApiInitiatorIdentityTest(MARY_USER, MARY_PASSWORD);
//    }
//
//    @Test
//    public void testJmsRemoteApiHumanTaskGroupId() throws Exception {
//        Assume.assumeTrue(jmsQueuesAvailable());
//        printTestName();
//
//        jmsTests.remoteApiHumanTaskGroupIdTest(deploymentUrl);
//    }
    
}
