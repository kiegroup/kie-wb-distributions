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

package org.kie.smoke.wb.ws;

import static org.kie.smoke.wb.base.test.methods.KieRemoteWebserviceTestMethods.doTestStartSimpleProcess;
import static org.kie.smoke.wb.deploy.KieWbWarDeploy.createTestWar;

import java.net.URL;
import java.util.concurrent.atomic.AtomicBoolean;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.kie.internal.runtime.conf.RuntimeStrategy;
import org.kie.smoke.wb.base.category.KieWbSmoke;
import org.kie.smoke.wb.base.test.AbstractWorkbenchIntegrationTest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RunAsClient
@RunWith(Arquillian.class)
@Category(KieWbSmoke.class)
public class KieRemoteWebServiceArquillianIntegrationTest extends AbstractWorkbenchIntegrationTest {

    @Deployment(testable = false, name = "test-war")
    public static Archive<?> createWar() {
        return createTestWar();
    }

    @ArquillianResource
    URL deploymentUrl;

    private static final Logger logger = LoggerFactory.getLogger(KieRemoteWebServiceArquillianIntegrationTest.class);

    /**
     * Clone, build and deploy the test deployment unit.
     */
    @BeforeClass
    public static void setupDeployment() throws Exception {
        if( jbpmPlaygroundDeploymentDone.compareAndSet(false, true) ) {
            Thread.sleep(3 * 1000);
            deployJbpmPlayGroundIntegrationTests(RuntimeStrategy.SINGLETON);
        }
    }

    @AfterClass
    public static void waitForTxOnServer() throws InterruptedException {
        long sleep = 1;
        logger.info("Waiting " + sleep + " secs for tx's on server to close.");
        Thread.sleep(sleep * 1000);
    }

    @Test
    public void testStartSimpleProcess() throws Exception {
        doTestStartSimpleProcess(deploymentUrl);
    }


}