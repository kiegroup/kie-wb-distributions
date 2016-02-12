/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.kie.smoke.wb.category.KieWbSeleniumSmoke;
import org.kie.smoke.wb.selenium.model.EnumerationEditor;
import org.kie.smoke.wb.selenium.model.KieSeleniumTest;
import static org.kie.smoke.wb.selenium.model.KieSeleniumTest.KIE_PASS;
import static org.kie.smoke.wb.selenium.model.KieSeleniumTest.KIE_USER;
import org.kie.smoke.wb.selenium.model.LoginPage;
import org.kie.smoke.wb.selenium.model.PrimaryNavbar;
import org.kie.smoke.wb.selenium.model.persps.ProjectAuthoringPerspective;
import org.kie.smoke.wb.selenium.model.persps.ProjectEditor;

@Category(KieWbSeleniumSmoke.class)
public class CreateAndSaveAssetIntegrationTest extends KieSeleniumTest {

    @Test
    public void createAndSaveEnum() throws InterruptedException {
        ProjectAuthoringPerspective pa = goToAuthoring();

        EnumerationEditor enumEditor = pa.newEnumeration("MyFirstEnum");
        expectNotification("Item successfully created");

        enumEditor.save("I created my first enumeration");
        expectNotification("Item successfully saved");

        ProjectEditor projEditor = pa.openProjectEditor();
        projEditor.buildAndDeploy();
        expectNotification("Build Successful");
    }

    private void expectNotification(String expectedMessage) {
        String notificationMsg = pof.createNotification().getMessage();
        Assert.assertTrue("Notification '" + expectedMessage + "' should be displayed",
                notificationMsg.contains(expectedMessage));
    }

    private ProjectAuthoringPerspective goToAuthoring() {
        LoginPage lp = pof.createLoginPage();
        PrimaryNavbar navbar = lp.loginAs(KIE_USER, KIE_PASS).getNavbar();
        return navbar.projectAuthoring();
    }
}
