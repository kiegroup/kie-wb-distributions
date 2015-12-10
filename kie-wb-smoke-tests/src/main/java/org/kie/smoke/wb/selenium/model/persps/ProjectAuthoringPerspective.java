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
package org.kie.smoke.wb.selenium.model.persps;

import org.kie.smoke.wb.selenium.util.Waits;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class ProjectAuthoringPerspective extends AbstractPerspective {

    private static final By PROJECT_EXPLORER_TITLE = By.xpath("//h3[contains(text(),'Project Explorer')]");
    //Project explorer bradcrumb toggle, whose presence indicates that PEX content has been loaded
    private static final By PEX_BREADCRUMB_TOGGLE = By.cssSelector(".fa-chevron-down");

    public ProjectAuthoringPerspective(WebDriver driver) {
        super(driver);
    }

    @Override
    public void waitForLoaded() {
        Waits.elementPresent(driver, PEX_BREADCRUMB_TOGGLE, 10);
    }

    @Override
    public boolean isDisplayed() {
        return Waits.isElementPresent(driver, PROJECT_EXPLORER_TITLE);
    }
}
