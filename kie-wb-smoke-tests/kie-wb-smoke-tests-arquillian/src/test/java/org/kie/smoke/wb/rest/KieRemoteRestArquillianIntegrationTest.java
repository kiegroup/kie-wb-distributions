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

package org.kie.smoke.wb.rest;

import static org.junit.Assert.assertEquals;
import static org.kie.smoke.wb.base.test.methods.KieRemoteRestTestMethods.deploymentId;
import static org.kie.smoke.wb.base.test.methods.KieRemoteRestTestMethods.deploymentUnit;
import static org.kie.smoke.wb.base.test.methods.KieRemoteRestTestMethods.doRestHistoryLogs;
import static org.kie.smoke.wb.base.test.methods.KieRemoteRestTestMethods.doRestRemoteApiExtraJaxbClasses;
import static org.kie.smoke.wb.base.test.methods.KieRemoteRestTestMethods.doRestRemoteApiHumanTaskProcess;
import static org.kie.smoke.wb.base.test.methods.KieRemoteRestTestMethods.doRestRemoteApiRuleTaskProcess;
import static org.kie.smoke.wb.base.test.methods.KieRemoteRestTestMethods.doRestUrlsGroupAssignmentProcess;
import static org.kie.smoke.wb.base.test.methods.KieRemoteRestTestMethods.doUrlsGetDeployments;
import static org.kie.smoke.wb.deploy.KieWbWarDeploy.createTestWar;

import java.net.URL;

import javax.ws.rs.core.MediaType;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.kie.internal.runtime.conf.RuntimeStrategy;
import org.kie.smoke.wb.base.category.KieWbSmoke;
import org.kie.smoke.wb.base.test.AbstractWorkbenchIntegrationTest;

@RunAsClient
@RunWith(Arquillian.class)
@Category(KieWbSmoke.class)
public class KieRemoteRestArquillianIntegrationTest extends AbstractWorkbenchIntegrationTest {

    @Deployment(testable = false, name = "test-war")
    public static Archive<?> createWar() {
        return createTestWar();
    }

    @ArquillianResource
    URL deploymentUrl;

    private String mediaType = MediaType.APPLICATION_XML;

    public KieRemoteRestArquillianIntegrationTest() {
        this.mediaType = MediaType.APPLICATION_XML; // TODO run the tests with both XML and JSON?

        assertEquals("Returned deployment unit identifier is incorrect!", deploymentId, deploymentUnit.getIdentifier());
    }

    /**
     * Clone, build and deploy the test deployment unit.
     */
    @Before
    public void setupDeployment() throws Exception {
        if( jbpmPlaygroundDeploymentDone.compareAndSet(false, true) ) {
            Thread.sleep(3 * 1000);
            deployJbpmPlayGroundIntegrationTests(deploymentUrl, RuntimeStrategy.SINGLETON);
        }
    }

    @Test
    public void testUrlsGetDeployments() throws Exception {
        doUrlsGetDeployments(deploymentUrl, mediaType);
    }

    @Test
    public void testRestHistoryLogs() throws Exception {
       doRestHistoryLogs(deploymentUrl, mediaType);
    }

    @Test
    public void testRestRemoteApiHumanTaskProcess() throws Exception {
        doRestRemoteApiHumanTaskProcess(deploymentUrl, mediaType);
    }

    @Test
    public void testRestRemoteApiExtraJaxbClasses() throws Exception {
       doRestRemoteApiExtraJaxbClasses(deploymentUrl, mediaType);
    }

    @Test
    public void testRestRemoteApiRuleTaskProcess() throws Exception {
       doRestRemoteApiRuleTaskProcess(deploymentUrl, mediaType);
    }

    @Test
    public void testRestUrlsGroupAssignmentProcess() throws Exception {
        doRestUrlsGroupAssignmentProcess(deploymentUrl, mediaType);
    }

}
