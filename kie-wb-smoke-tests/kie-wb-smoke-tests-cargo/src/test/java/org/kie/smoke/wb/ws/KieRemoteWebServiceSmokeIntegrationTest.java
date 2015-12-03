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

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.kie.internal.runtime.conf.RuntimeStrategy;
import org.kie.smoke.wb.base.test.AbstractWorkbenchIntegrationTest;

public class KieRemoteWebServiceSmokeIntegrationTest extends AbstractWorkbenchIntegrationTest {

    /**
     * Clone, build and deploy the test deployment unit.
     */
    @BeforeClass
    public static void setupDeployment() {
        deployJbpmPlayGroundIntegrationTests(RuntimeStrategy.SINGLETON);
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