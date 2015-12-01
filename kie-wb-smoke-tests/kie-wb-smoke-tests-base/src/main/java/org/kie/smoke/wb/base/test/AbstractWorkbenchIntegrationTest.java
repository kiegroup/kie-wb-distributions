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

package org.kie.smoke.wb.base.test;

import org.junit.Rule;
import org.junit.rules.TestRule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.kie.internal.runtime.conf.RuntimeStrategy;
import org.kie.smoke.wb.base.util.RestRepositoryDeploymentUtil;
import org.kie.smoke.wb.base.util.TestConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.kie.smoke.wb.base.util.TestConstants.MARY_PASSWORD;
import static org.kie.smoke.wb.base.util.TestConstants.MARY_USER;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;

abstract public class AbstractWorkbenchIntegrationTest {

    protected static Logger logger = LoggerFactory.getLogger(AbstractWorkbenchIntegrationTest.class);

    protected static final AtomicBoolean jbpmPlaygroundDeploymentDone = new AtomicBoolean(false);

    protected static final URL deploymentUrl;

    static {
        try {
            String property = System.getProperty("deployable.base.uri");
            if( property == null ) {
                Properties testProps = new Properties();
                try {
                    testProps.load(TestConstants.class.getResourceAsStream("/test.properties"));
                } catch (Exception e) {
                    throw new RuntimeException("Unable to initialize projectVersion property: " + e.getMessage(), e);
                }
                property = testProps.getProperty("deployable.base.uri");
            }
            deploymentUrl = new URL(property);
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
       deployJbpmPlayGroundIntegrationTests(deploymentUrl, strategy);
    }

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
    }
}
