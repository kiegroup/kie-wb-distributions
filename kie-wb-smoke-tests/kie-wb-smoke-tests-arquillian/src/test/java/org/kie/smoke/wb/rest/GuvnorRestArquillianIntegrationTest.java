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

import static org.kie.smoke.wb.base.test.methods.GuvnorRestTestMethods.doTestManipulatingOUs;
import static org.kie.smoke.wb.base.test.methods.GuvnorRestTestMethods.doTestMavenOperations;
import static org.kie.smoke.wb.base.util.TestConstants.MARY_PASSWORD;
import static org.kie.smoke.wb.base.util.TestConstants.MARY_USER;

import javax.ws.rs.core.MediaType;

import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.kie.smoke.wb.base.category.KieDroolsWbSmoke;
import org.kie.smoke.wb.base.category.KieWbSmoke;
import org.kie.smoke.wb.base.test.AbstractWorkbenchIntegrationTest;

@RunAsClient
@RunWith(Arquillian.class)
@Category({KieWbSmoke.class, KieDroolsWbSmoke.class})
public class GuvnorRestArquillianIntegrationTest extends AbstractWorkbenchIntegrationTest {

    private final static String mediaType = MediaType.APPLICATION_JSON;
    private final static String user = MARY_USER;
    private final static String password = MARY_PASSWORD;

    // Test methods ---------------------------------------------------------------------------------------------------------------

    @BeforeClass
    public static void waitForServer() throws Exception {
        // the server needs time to process the deployment
        Thread.sleep(3*1000);
    }

    @Test
    public void testManipulatingRepositoriesAndProjects() throws Exception {
        doTestManipulatingOUs(deploymentUrl, mediaType, user, password);
    }

    @Test
    public void testMavenOperations() throws Exception {
       doTestMavenOperations(deploymentUrl, mediaType, user, password);
    }

    @Test
    public void testManipulatingOUs() throws Exception {
       doTestManipulatingOUs(deploymentUrl, mediaType, user, password);
    }
}
