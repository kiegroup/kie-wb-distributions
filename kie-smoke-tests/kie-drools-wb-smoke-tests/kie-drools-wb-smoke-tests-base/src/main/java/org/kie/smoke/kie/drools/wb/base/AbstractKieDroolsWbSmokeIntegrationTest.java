package org.kie.smoke.kie.drools.wb.base;

import java.net.URL;

import org.jboss.arquillian.test.api.ArquillianResource;
import org.junit.Test;
import org.kie.smoke.kie.drools.wb.base.methods.KieDroolsWbRestSmokeIntegrationTestMethods;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractKieDroolsWbSmokeIntegrationTest {
    
    protected static final Logger logger = LoggerFactory.getLogger(AbstractKieDroolsWbSmokeIntegrationTest.class);

    private KieDroolsWbRestSmokeIntegrationTestMethods restTests = new KieDroolsWbRestSmokeIntegrationTestMethods();
    
    @ArquillianResource
    URL deploymentUrl;
    
    @Test
    public void manipulatingRepositories() throws Exception {
        restTests.manipulatingRepositories(deploymentUrl);
    }
    
    @Test
    public void mavenOperations() throws Exception {
        restTests.mavenOperations(deploymentUrl);
    }
    
    @Test
    public void manipulatingOUs() throws Exception {
        restTests.manipulatingOUs(deploymentUrl);
    }
}
