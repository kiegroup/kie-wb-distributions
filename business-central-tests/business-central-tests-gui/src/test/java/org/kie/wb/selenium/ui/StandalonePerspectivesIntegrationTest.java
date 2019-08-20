/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

    private final static String
            PROJECT_URL = "https://github.com/Rikkola/single-project-test-repository.git",
            SPACE_NAME = "Standalone_space",
            PROJECT_NAME = "Standalone_project";

    private final static String
            BASE_URL = System.getProperty("kie.wb.url"),
            APP_CONTEXT = "kie-wb.jsp",
            STANDALONE_PARAMETER = "?standalone",
            // URL of a standalone perspective:
            PERSPECTIVE_PARAMETER = "&perspective=",
            HEADER_PARAMETER = "&header=UberfireBreadcrumbsContainer",
            PERSPECTIVE_FULL_URL = BASE_URL + "/" + APP_CONTEXT + STANDALONE_PARAMETER + PERSPECTIVE_PARAMETER,
            // URL of a standalone editor:
            PROJECT_ASSET_PATH = "&path=default://master@" + SPACE_NAME + "/" + PROJECT_NAME + "/src/main/java/mortgages/mortgages/Applicant.java",
            EDITOR_FULL_URL = BASE_URL + "/" + APP_CONTEXT + STANDALONE_PARAMETER + PROJECT_ASSET_PATH;

    private static final By
            CONTENT_MANAGEMENT = By.cssSelector("[title='Content Manager']"),
            DATA_OBJECT = By.cssSelector("[title='Applicant.java - Data Objects']"),
            HEADER = By.id("workbenchHeaderPanel"),
            LIBRARY = By.className("toolbar-data-title-kie");

    @BeforeClass
    public static void cloneTestingProject() {
        WorkbenchClient workbenchClient = RestWorkbenchClient
                // REST credentials from Cargo configuration in business-central-tests/pom.xml:
                .createWorkbenchClient(BASE_URL, "restAll", "restAll1234;");

        // Create a space:
        final Space space = new Space();
        space.setName(SPACE_NAME);
        space.setOwner("donald@duck.gov");
        space.setDefaultGroupId("gov.duck");
        workbenchClient.createSpace(space);

        // Clone a project:
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
    public void testLibraryPerspectiveWithoutHeader() {
        String perspective = "LibraryPerspective";
        driver.get(PERSPECTIVE_FULL_URL + perspective);

        verifyPerspectiveIsLoaded(LIBRARY);
        verifyPresenceOfHeader(false);
    }

    @Test
    public void testLibraryPerspectiveWithHeader() {
        String perspective = "LibraryPerspective";
        driver.get(PERSPECTIVE_FULL_URL + perspective + HEADER_PARAMETER);

        verifyPerspectiveIsLoaded(LIBRARY);
        verifyPresenceOfHeader(true);
    }

    @Test
    public void testStandaloneEditorPerspective() {
        driver.get(EDITOR_FULL_URL);

        verifyPerspectiveIsLoaded(DATA_OBJECT);
    }

    @Test
    public void testContentManagerPerspective() {
        String perspective = "ContentManagerPerspective";
        driver.get(PERSPECTIVE_FULL_URL + perspective);

        verifyPerspectiveIsLoaded(CONTENT_MANAGEMENT);
    }

    private void verifyPerspectiveIsLoaded(By verifier) {
        try {
            Waits.elementPresent(verifier, getHomepageLoadingTimeoutSeconds());
        } catch (WebDriverException exception) {
            Assertions.fail("The standalone perspective was not loaded.", exception);
        }
    }

    private void verifyPresenceOfHeader(boolean headerIncluded) {
        int
                actualHeaderCount = driver.findElements(HEADER).size(),
                expectedHeaderCount = headerIncluded ? 1 : 0;

        String headerAssertionMessage = String.format("There should %s be UberfireBreadcrumbsContainer on the page.", headerIncluded ? "" : "NOT");

        assertThat(actualHeaderCount)
                .as(headerAssertionMessage)
                .isEqualTo(expectedHeaderCount);
    }
}
