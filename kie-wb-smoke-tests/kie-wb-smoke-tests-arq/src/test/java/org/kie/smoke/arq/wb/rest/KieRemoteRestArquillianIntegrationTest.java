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

package org.kie.smoke.arq.wb.rest;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.kie.smoke.arq.wb.deploy.KieWbWarDeploy;
import org.kie.smoke.wb.category.KieDroolsWbSmoke;
import org.kie.smoke.wb.category.KieWbSmoke;
import org.kie.smoke.wb.rest.GuvnorRestSmokeIntegrationTest;
import org.kie.smoke.wb.rest.KieRemoteRestSmokeIntegrationTest;

import java.net.URL;

@RunAsClient
@RunWith(Arquillian.class)
public class KieRemoteRestArquillianIntegrationTest {

    @Deployment(testable = false, name = "test-war")
    public static Archive<?> createWar() {
        return KieWbWarDeploy.createTestWar();
    }

    @ArquillianResource
    URL deploymentUrl;
    // Test methods ---------------------------------------------------------------------------------------------------------------

    private KieRemoteRestSmokeIntegrationTest mainTest = new KieRemoteRestSmokeIntegrationTest();

    @BeforeClass
    public static void waitForServer() throws Exception {
        // the server needs time to process the deployment
        Thread.sleep(3 * 1000);
    }

    @Before
    public void setup() throws Exception {
        KieRemoteRestSmokeIntegrationTest.deploymentUrl = this. deploymentUrl;
        // this is likely going to fail for second test; unless the deployment is removed in @After method
        KieRemoteRestSmokeIntegrationTest.setupDeployment();
    }

    @Test
    public void testUrlsGetDeployments() throws Exception {
        mainTest.testUrlsGetDeployments();
    }

    @Test
    public void testRestHistoryLogs() throws Exception {
        mainTest.testRestHistoryLogs();
    }

    @Test
    public void testRestRemoteApiHumanTaskProcess() throws Exception {
        mainTest.testRestRemoteApiHumanTaskProcess();
    }

    @Test
    public void testRestRemoteApiExtraJaxbClasses() throws Exception {
        mainTest.testRestRemoteApiExtraJaxbClasses();
    }

    @Test
    public void testRestRemoteApiRuleTaskProcess() throws Exception {
        mainTest.testRestRemoteApiRuleTaskProcess();
    }

    @Test
    public void testRestUrlsGroupAssignmentProcess() throws Exception {
        mainTest.testRestUrlsGroupAssignmentProcess();
    }

}
