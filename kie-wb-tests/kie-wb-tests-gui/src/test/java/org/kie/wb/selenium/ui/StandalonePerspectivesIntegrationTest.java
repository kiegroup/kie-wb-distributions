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

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.Map;

import org.assertj.core.api.Assertions;
import org.jboss.arquillian.graphene.findby.ByJQuery;
import org.junit.Test;
import org.kie.wb.selenium.model.KieSeleniumTest;
import org.kie.wb.selenium.util.ApplicationLoadingPopup;
import org.kie.wb.selenium.util.BusyPopup;
import org.kie.wb.selenium.util.Waits;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriverException;

public class StandalonePerspectivesIntegrationTest extends KieSeleniumTest {

    private final static String
            BASE_URL = System.getProperty("kie.wb.url"),
            APP_CONTEXT = IS_KIE_WB ? "kie-wb.jsp" : "kie-drools-wb.jsp",
            WORKBENCH_URL = BASE_URL + "/" + APP_CONTEXT;
    private final int PERSPECTIVE_LOADING_TIMEOUT = 30;

    private static final By HEADER = By.id("workbenchHeaderPanel");

    // The following list of perspectives is obtained from Business Central
    // (Extensions -> Plugin Management -> Perspective Plugin).
    private static final Map<String, By> perspectiveToElement = new HashMap<String, By>() {
        {
            //put("FormDisplayPerspective", ); // How to test this? What does this perspective do?
            //put("StandaloneEditorPerspective", ); // How to test this? I need an additional parameter with a path to some asset.

            put("AdministrationPerspective", By.className("qe-list-bar-header-Repository-Editor"));
            put("AdminPagePerspective", By.id("admin-page-title"));
            put("AppsPerspective", By.cssSelector("[title='Create Directory']"));
            put("Asset Management", By.className("qe-list-bar-header-Repository-Configuration"));
            put("AuthoringPerspective", By.className("fa-refresh"));
            put("AuthoringPerspectiveNoContext", By.cssSelector("[title='Project Explorer']"));
            put("ContentManagerPerspective", By.className("qe-list-bar-header-Content-Manager"));
            put("DashboardPerspective", ByJQuery.selector("span:contains('Processes & Tasks Dashboard')"));
            put("DataSetAuthoringPerspective", By.className("qe-list-bar-header-Data-Set-Authoring-Home"));
            put("DataSourceManagementPerspective", By.className("qe-list-bar-header-DataSource-Explorer"));
            put("ExecutionErrors", By.className("qe-list-bar-header-Execution-Errors"));
            put("GuvnorM2RepoPerspective", By.className("qe-list-bar-header-M2-Repository-Content"));
            put("HomePerspective", ByJQuery.selector("h2:contains('The Knowledge Life Cycle')"));
            put("LibraryPerspective", By.id("welcome"));
            put("PlugInAuthoringPerspective", ByJQuery.selector("h3:contains('Plugin Explorer')"));
            put("PreferencesCentralPerspective", By.className("preferences-editor"));
            put("ProcessDefinitions", By.className("qe-list-bar-header-Process-Definitions"));
            put("ProcessInstances", By.className("qe-list-bar-header-Process-Instances"));
            put("Requests", By.className("qe-list-bar-header-Requests-List"));
            put("SecurityManagementPerspective", By.className("qe-list-bar-header-Security-management"));
            put("ServerManagementPerspective", By.className("fa-refresh"));
            put("SocialHomePagePerspective", By.className("qe-list-bar-header-Recent-Assets"));
            put("TaskAdmin", By.className("qe-list-bar-header-Task-Administration-List"));
            put("Tasks", By.className("qe-list-bar-header-Tasks-List"));
            put("UserHomePagePerspective", By.className("fa-home"));
            put("WiresGridsDemoPerspective", By.className("qe-list-bar-header-Grids"));
            put("WiresTreesPerspective", By.className("qe-list-bar-header-Palette"));
        }
    };

    @Test
    public void loadStandalonePerspectives() {
        login.getLoginPage();
        login.loginDefaultUser();

        for (Map.Entry<String, By> entry : perspectiveToElement.entrySet()) {
            String perspectiveId = entry.getKey();
            By elementInPerspective = entry.getValue();

            loadPerspectiveAndCheckLoadingTime(perspectiveId, true);
            verifyPerspectiveIsLoaded(perspectiveId, elementInPerspective);
            verifyPresenceOfHeader(true);

            loadPerspectiveAndCheckLoadingTime(perspectiveId, false);
            verifyPerspectiveIsLoaded(perspectiveId, elementInPerspective);
            verifyPresenceOfHeader(false);
        }
    }

    private void loadPerspectiveAndCheckLoadingTime(String perspectiveID, boolean headerIncluded) {
        //long loadingStart = System.currentTimeMillis();

        String standalonePerspectiveURL = getStandalonePerspectiveURL(perspectiveID, headerIncluded);
        driver.get(standalonePerspectiveURL);

        ApplicationLoadingPopup.waitForDisappearance(PERSPECTIVE_LOADING_TIMEOUT);
        BusyPopup.waitForDisappearance();

        //double perspectiveLoadingTime = (System.currentTimeMillis() - loadingStart) / (double) 1000;

        //TODO: What is the acceptable loading time? Do we want to test this?
        //assertThat(perspectiveLoadingTime)
        //        .as("It should take less than " + PERSPECTIVE_LOADING_TIMEOUT + " seconds to load the perspective " + perspectiveID + ".")
        //        .isLessThan(PERSPECTIVE_LOADING_TIMEOUT);
    }

    private void verifyPerspectiveIsLoaded(String perspectiveID, By elementInPerspective) {
        try {
            Waits.elementPresent(elementInPerspective, 10);
        } catch (WebDriverException exception) {
            Assertions.fail("The perspective with ID " + perspectiveID + " could not be loaded.", exception);
        }
    }

    private void verifyPresenceOfHeader(boolean headerIncluded) {
        int actualHeaderElementCount = driver.findElements(HEADER).size(),
                expectedHeaderElementCount = headerIncluded ? 1 : 0;

        String headerAssertionMessage = String.format("There should %s be AppNavBar on the page.", headerIncluded ? "" : "NOT");

        assertThat(actualHeaderElementCount)
                .as(headerAssertionMessage)
                .isEqualTo(expectedHeaderElementCount);
    }

    private String getStandalonePerspectiveURL(String perspectiveID, boolean headerIncluded) {
        String headerParameter = headerIncluded ? "&header=AppNavBar" : "";
        return WORKBENCH_URL + "?standalone=true&perspective=" + perspectiveID + headerParameter;
    }
}
