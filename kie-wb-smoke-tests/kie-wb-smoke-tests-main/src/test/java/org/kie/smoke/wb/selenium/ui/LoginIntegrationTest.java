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
package org.kie.smoke.wb.selenium.ui;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.kie.smoke.wb.category.KieWbSeleniumSmoke;
import org.kie.smoke.wb.selenium.model.LoginPage;
import org.kie.smoke.wb.selenium.model.HomePerspective;
import org.kie.smoke.wb.selenium.model.KieSeleniumTest;

@Category(KieWbSeleniumSmoke.class)
public class LoginIntegrationTest extends KieSeleniumTest {

    //Credentials based on from src/test/filtered-resources/eap-wildfly-shared/config/application-users.properties
    public final static String KIE_PASS = "mary123@";
    public final static String KIE_USER = "mary";

    @Test
    public void loginAndLogout() throws Exception {
        LoginPage login = pof.createLoginPage();

        HomePerspective home = login.loginAs(KIE_USER, KIE_PASS);
        assertTrue(home.isDisplayed());

        login = home.logout();
        assertTrue(login.isDisplayed());
    }
}
