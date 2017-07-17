/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

import org.junit.Test;
import org.kie.wb.selenium.model.KieSeleniumTest;
import org.kie.wb.selenium.model.Persp;
import org.kie.wb.selenium.model.persps.AbstractPerspective;
import org.kie.wb.selenium.model.persps.HomePerspective;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.*;

/**
 * Login and verify each perspective can be navigated to and loads some content.
 */
public class LoadAllPerspectivesIntegrationTest extends KieSeleniumTest {

    private static final Logger LOG = LoggerFactory.getLogger(LoadAllPerspectivesIntegrationTest.class);

    @Test
    public void allPerspectivesCanBeLoaded() {
        login.getLoginPage();
        HomePerspective home = login.loginDefaultUser();

        for (Persp<?> p : Persp.getAllPerspectives()) {
            if (perspectiveInCurrentWebApp(p)) {
                LOG.info("Checking perspective '" + p.getName() + "..");
                AbstractPerspective perspective = home.getNavbar().navigateTo(p);
                LOG.info("Navigated to perspective '" + p.getName() + "..");
                assertTrue("Perspective " + p.getName() + " should be loaded", perspective.isDisplayed());
            }
        }

        home.logout();
    }

    private boolean perspectiveInCurrentWebApp(Persp persp) {
        return IS_KIE_WB || !persp.isKieWbOnly();
    }

}
