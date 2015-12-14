/*
 * Copyright 2015 JBoss by Red Hat.
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
package org.kie.smoke.wb.selenium.ui;

import java.util.ArrayList;
import java.util.List;
import org.junit.AfterClass;
import static org.junit.Assert.assertTrue;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.kie.smoke.wb.category.KieWbSeleniumSmoke;
import org.kie.smoke.wb.selenium.model.KieSeleniumTest;
import org.kie.smoke.wb.selenium.model.LoginPage;
import org.kie.smoke.wb.selenium.model.Persp;
import org.kie.smoke.wb.selenium.model.PrimaryNavbar;
import org.kie.smoke.wb.selenium.model.persps.AbstractPerspective;

/**
 * Login and verify each perspective can be navigated to and loads some content.
 */
@RunWith(Parameterized.class)
@Category(KieWbSeleniumSmoke.class)
public class LoadAllPerspectivesIntegrationTest extends KieSeleniumTest {

    private static PrimaryNavbar navbar;
    private final Persp<?> persp;

    public LoadAllPerspectivesIntegrationTest(Persp<?> persp) {
        this.persp = persp;
    }

    @Parameters(name = "{0}")
    public static List<Object[]> perspectives() {
        List<Object[]> params = new ArrayList<Object[]>();
        for (Persp<?> p : Persp.getAllPerspectives()) {
            params.add(new Object[]{p});
        }
        return params;
    }

    @BeforeClass
    public static void login() {
        LoginPage lp = pof.createLoginPage();
        navbar = lp.loginAs(KIE_USER, KIE_PASS).getNavbar();
    }

    @AfterClass
    public static void logout() {
        navbar.logout();
    }

    @Test
    public void checkPerspectiveLoaded() {
        AbstractPerspective perspective = navbar.navigateTo(persp);
        assertTrue("Perspective " + persp.getName() + " should be loaded", perspective.isDisplayed());
    }
}
