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
import org.jboss.arquillian.graphene.findby.ByJQuery;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.kie.wb.selenium.model.KieSeleniumTest;
import org.kie.wb.selenium.util.Waits;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriverException;

import static org.assertj.core.api.Assertions.assertThat;

@Ignore("RHBA-503 - Standalone perspectives are broken")
public class StandalonePerspectivesIntegrationTest extends KieSeleniumTest {

    private final static String
            BASE_URL = System.getProperty("kie.wb.url"),
            APP_CONTEXT = System.getProperty("app.name") + ".jsp",
            STANDALONE_PARAMETER = "?standalone=true",
            PERSPECTIVE_ID = "&perspective=AuthoringPerspective",
            HEADER_PARAMETER = "&header=AppNavBar",
            COMPLETE_URL = BASE_URL + "/" + APP_CONTEXT + STANDALONE_PARAMETER + PERSPECTIVE_ID;

    private static final By
            EXPLORER_HEADER = ByJQuery.selector("h3:contains('Project Explorer')"),
            WORKBENCH_HEADER = By.id("workbenchHeaderPanel");

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
            Waits.elementPresent(EXPLORER_HEADER, 10);
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
