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

package org.kie.smoke.wb;

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

import static org.kie.smoke.wb.util.TestConstants.MARY_PASSWORD;
import static org.kie.smoke.wb.util.TestConstants.MARY_USER;

abstract public class AbstractWorkbenchIntegrationTest {

    private static Logger logger = LoggerFactory.getLogger(AbstractWorkbenchIntegrationTest.class);

    public static URL deploymentUrl;

    static {
        String baseUriPropValue = System.getProperty("deployable.base.uri");
        if (baseUriPropValue == null ) {
            // assume the URL will be set for every test
            logger.info("No deployable base URI specified. Assuming it will be set for each test individually.");
        } else {
            try {
                deploymentUrl = new URL(baseUriPropValue);
                logger.info("deployable.base.uri=" + deploymentUrl.toExternalForm());
            } catch (MalformedURLException e) {
                throw new RuntimeException("Malformed deployment URL '" + baseUriPropValue + "'!", e);
            }
        }
    }

    @Rule
    public TestRule watcher = new TestWatcher() {
        protected void starting(Description description) {
            System.out.println("Starting test: " + description.getMethodName());
        }
    };

    protected static void deployJbpmPlayGroundIntegrationTests(URL deploymentUrl, RuntimeStrategy strategy) {
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
        } catch (Exception e) {
            // no op
        }
    }

}
