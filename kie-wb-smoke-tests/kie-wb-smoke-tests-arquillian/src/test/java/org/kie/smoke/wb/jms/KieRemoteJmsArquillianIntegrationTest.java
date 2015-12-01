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

package org.kie.smoke.wb.jms;

import static org.kie.smoke.wb.base.test.methods.KieRemoteJmsTestMethods.remoteApiHumanTaskGroupIdTest;
import static org.kie.smoke.wb.base.test.methods.KieRemoteJmsTestMethods.remoteApiInitiatorIdentityTest;
import static org.kie.smoke.wb.base.util.TestConstants.KJAR_DEPLOYMENT_ID;
import static org.kie.smoke.wb.base.util.TestConstants.MARY_PASSWORD;
import static org.kie.smoke.wb.base.util.TestConstants.MARY_USER;

import javax.naming.InitialContext;

import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.kie.internal.runtime.conf.RuntimeStrategy;
import org.kie.remote.client.api.RemoteRuntimeEngineFactory;
import org.kie.smoke.wb.base.category.JMSSmoke;
import org.kie.smoke.wb.base.category.KieWbSmoke;
import org.kie.smoke.wb.base.test.AbstractWorkbenchIntegrationTest;

@RunAsClient
@RunWith(Arquillian.class)
@Category({KieWbSmoke.class, JMSSmoke.class})
public class KieRemoteJmsArquillianIntegrationTest extends AbstractWorkbenchIntegrationTest {

    private final String deploymentId = KJAR_DEPLOYMENT_ID;
    private final InitialContext remoteInitialContext = RemoteRuntimeEngineFactory.getRemoteJbossInitialContext("localhost", MARY_USER, MARY_PASSWORD);


    /**
     * Clone, build and deploy the test deployment unit.
     */
    @BeforeClass
    public static void setupDeployment() {
        deployJbpmPlayGroundIntegrationTests(RuntimeStrategy.SINGLETON);
    }

    @Ignore("JMS configuration on container side is not yet implemented")
    @Test
    public void testJmsRemoteApiStartProcessInstanceInitiator() throws Exception {
        remoteApiInitiatorIdentityTest(deploymentId, remoteInitialContext, MARY_USER, MARY_PASSWORD);
    }

    @Ignore("JMS configuration on container side is not yet implemented")
    @Test
    public void testJmsRemoteApiHumanTaskGroupId() throws Exception {
        remoteApiHumanTaskGroupIdTest(deploymentUrl, deploymentId, remoteInitialContext);
    }
}
