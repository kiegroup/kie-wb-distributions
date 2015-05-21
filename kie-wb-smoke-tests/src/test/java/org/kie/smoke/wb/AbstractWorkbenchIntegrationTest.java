package org.kie.smoke.wb;

import static org.kie.smoke.wb.util.TestConstants.MARY_PASSWORD;
import static org.kie.smoke.wb.util.TestConstants.MARY_USER;

import org.junit.Rule;
import org.junit.rules.TestRule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.kie.internal.runtime.conf.RuntimeStrategy;
import org.kie.smoke.wb.util.RestRepositoryDeploymentUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URL;

abstract public class AbstractWorkbenchIntegrationTest {
    
    private static Logger logger = LoggerFactory.getLogger(AbstractWorkbenchIntegrationTest.class);

    protected static final URL deploymentUrl;

    static {
        try {
            deploymentUrl = new URL(System.getProperty("deployable.base.uri"));
            logger.info("deployable.base.uri=" + deploymentUrl.toExternalForm());
        } catch (MalformedURLException e) {
            throw new RuntimeException("Malformed deployment URL '" + System.getProperty("deployable.base.uri") + "'!", e);
        }
    }

    @Rule
    public TestRule watcher = new TestWatcher() {
       protected void starting(Description description) {
          System.out.println("Starting test: " + description.getMethodName());
       }
    };

    protected static void deployJbpmPlayGroundIntegrationTests(RuntimeStrategy strategy) { 
        int sleepSecs = 5;
        RestRepositoryDeploymentUtil deployUtil = new RestRepositoryDeploymentUtil(deploymentUrl, MARY_USER, MARY_PASSWORD, sleepSecs, strategy);
        deployUtil.setTotalTries(6);

        String repoUrl = "https://github.com/droolsjbpm/jbpm-playground.git";
        String repositoryName = "playground";
        String project = "integration-tests";
        String deploymentId = "org.test:kjar:1.0";
        String orgUnitName = "integTestUser";
        deployUtil.createRepositoryAndDeployProject(repoUrl, repositoryName, project, deploymentId, orgUnitName, MARY_USER);
        
        // Extra wait.. 
        try { 
            Thread.sleep(5000); // TODO don't use hardcoded wait, but rather polling
        } catch( Exception e ) { 
            // no op
        }
    }
}
