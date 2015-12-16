/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.kie.smoke.arq.wb.deploy.KieWbWarDeploy;
import org.kie.smoke.wb.AbstractWorkbenchIntegrationTest;
import org.kie.smoke.wb.category.KieDroolsWbSmoke;
import org.kie.smoke.wb.category.KieWbSmoke;
import org.kie.smoke.wb.rest.GuvnorRestSmokeIntegrationTest;

import java.net.URL;

@RunAsClient
@RunWith(Arquillian.class)
public class GuvnorRestArquillianIntegrationTest {

    @Deployment(testable = false, name = "test-war")
    public static Archive<?> createWar() {
        return KieWbWarDeploy.createTestWar();
    }

    @ArquillianResource
    URL deploymentUrl;
    // Test methods ---------------------------------------------------------------------------------------------------------------

    private GuvnorRestSmokeIntegrationTest mainTest = new GuvnorRestSmokeIntegrationTest();

    @BeforeClass
    public static void waitForServer() throws Exception {
        // the server needs time to process the deployment
        Thread.sleep(3 * 1000);
    }

    @Before
    public void setupDeploymentUrl() {
        mainTest.deploymentUrl = this. deploymentUrl;
    }

    @Test
    public void testManipulatingRepositoriesAndProjects() throws Exception {
        mainTest.testManipulatingRepositoriesAndProjects();
    }

    @Test
    public void testMavenOperations() throws Exception {
        mainTest.testMavenOperations();
    }

    @Test
    public void testManipulatingOUs() throws Exception {
        mainTest.testManipulatingOUs();
    }
}
