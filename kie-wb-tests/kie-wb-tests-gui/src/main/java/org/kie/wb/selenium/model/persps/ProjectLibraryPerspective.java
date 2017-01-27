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
package org.kie.wb.selenium.model.persps;

import org.kie.wb.selenium.util.Waits;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class ProjectLibraryPerspective extends AbstractPerspective {

    private static final String OU_REPO_BREADCRUMB_XPATH_SELECTOR = "//a[text()='myteam (myrepo)']";
    private static final String MORTGAGES_BUTTON_XPATH_SELECTOR = "//button[text()='mortgages']";

    private static final By MORTGAGES_IMPORT_BUTTON = By.xpath(MORTGAGES_BUTTON_XPATH_SELECTOR);
    private static final By OU_REPO_BREADCRUMB = By.xpath(OU_REPO_BREADCRUMB_XPATH_SELECTOR);

    @FindBy(xpath = MORTGAGES_BUTTON_XPATH_SELECTOR)
    private WebElement exampleButton;

    @Override
    public void waitForLoaded() {
        Waits.isElementPresent( OU_REPO_BREADCRUMB );
    }

    @Override
    public boolean isDisplayed() {
        return Waits.isElementPresent( OU_REPO_BREADCRUMB );
    }

    public void importMortgagesProject() {
        if ( Waits.isElementPresent( MORTGAGES_IMPORT_BUTTON, 20 ) ) {
            exampleButton.click();
        }
    }
}
