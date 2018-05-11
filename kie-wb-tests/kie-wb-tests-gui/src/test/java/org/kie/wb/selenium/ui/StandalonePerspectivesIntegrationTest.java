/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.wb.selenium.ui;

import org.assertj.core.api.Assertions;
import org.guvnor.rest.client.CloneProjectRequest;
import org.guvnor.rest.client.Space;
import org.jboss.arquillian.graphene.findby.ByJQuery;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.kie.wb.selenium.model.KieSeleniumTest;
import org.kie.wb.selenium.util.Waits;
import org.kie.wb.test.rest.client.RestWorkbenchClient;
import org.kie.wb.test.rest.client.WorkbenchClient;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriverException;

import static org.assertj.core.api.Assertions.assertThat;

public class StandalonePerspectivesIntegrationTest extends KieSeleniumTest {

    private final static String // Project setup
            PROJECT_URL = "https://github.com/Rikkola/single-project-test-repository.git",
            SPACE_NAME = "Standalone_space",
            PROJECT_NAME = "Standalone_project";

    private final static String // Assemble URL to standalone perspective
            BASE_URL = System.getProperty("kie.wb.url"),
            APP_CONTEXT = System.getProperty("app.name") + ".jsp",
            STANDALONE_PARAMETER = "?standalone",
            HEADER_PARAMETER = "&header=AppNavBar",
            PROJECT_ASSET_PATH = "&path=default://master@" + SPACE_NAME + "/" + PROJECT_NAME + "/src/main/java/mortgages/mortgages/Applicant.java",
            COMPLETE_URL = BASE_URL + "/" + APP_CONTEXT + STANDALONE_PARAMETER + PROJECT_ASSET_PATH;

    private static final By
            DATA_MODELER_EDITOR_TITLE = ByJQuery.selector("[title='Applicant.java - Data Objects']"),
            WORKBENCH_HEADER = By.id("workbenchHeaderPanel");

    @BeforeClass
    public static void cloneTestingProject() {
        WorkbenchClient workbenchClient = RestWorkbenchClient
                // Rest credentials from cargo config in kie-wb-tests/pom.xml
                .createWorkbenchClient(BASE_URL, "restAll", "restAll1234;");

        // Create space
        final Space space = new Space();
        space.setName(SPACE_NAME);
        space.setOwner("donald@duck.gov");
        space.setDefaultGroupId("gov.duck");
        workbenchClient.createSpace(space);

        // Clone project
        CloneProjectRequest cloneReq = new CloneProjectRequest();
        cloneReq.setGitURL(PROJECT_URL);
        cloneReq.setName(PROJECT_NAME);
        workbenchClient.cloneRepository(SPACE_NAME, cloneReq);
    }

    @Before
    public void setUp() {
        login.get();
        if (login.isDisplayed()) {
            login.loginDefaultUser();
        }
    }

    @Test
    public void testAuthoringPerspectiveWithoutHeader() {
        driver.get(COMPLETE_URL);
        verifyPerspectiveIsLoaded();
        verifyPresenceOfHeader(false);
    }

    @Test
    public void testAuthoringPerspectiveWithHeader() {
        driver.get(COMPLETE_URL + HEADER_PARAMETER);
        verifyPerspectiveIsLoaded();
        verifyPresenceOfHeader(true);
    }

    private void verifyPerspectiveIsLoaded() {
        try {
            Waits.elementPresent(DATA_MODELER_EDITOR_TITLE, 15);
        } catch (WebDriverException exception) {
            Assertions.fail("The standalone perspective could not be loaded.", exception);
        }
    }

    private void verifyPresenceOfHeader(boolean headerIncluded) {
        int
                actualHeaderCount = driver.findElements(WORKBENCH_HEADER).size(),
                expectedHeaderCount = headerIncluded ? 1 : 0;

        String headerAssertionMessage = String.format("There should %s be AppNavBar on the page.", headerIncluded ? "" : "NOT");

        assertThat(actualHeaderCount)
                .as(headerAssertionMessage)
                .isEqualTo(expectedHeaderCount);
    }
}
