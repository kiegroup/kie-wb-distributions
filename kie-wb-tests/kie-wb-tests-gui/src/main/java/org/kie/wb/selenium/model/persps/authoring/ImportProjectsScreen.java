/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.wb.selenium.model.persps.authoring;

import org.jboss.arquillian.graphene.Graphene;
import org.kie.wb.selenium.model.PageObject;
import org.kie.wb.selenium.util.BusyPopup;
import org.kie.wb.selenium.util.Waits;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import static org.kie.wb.selenium.util.ByUtil.jquery;

public class ImportProjectsScreen extends PageObject {

    @FindBy(css = ".uf-workbench-layout")
    private WebElement screenRoot;

    @FindBy(css = "button.btn-primary")
    private WebElement okButton;

    public void selectProjects(String... projects) {
        for (String project : projects) {
            By projectCardLocator = jquery(".card-pf-view-select:has(.card-pf-title:contains('%s'))", project);
            WebElement projectCard = Waits.elementPresent(projectCardLocator);
            waitForLoaded();
            projectCard.click();
        }
    }

    public void ok() {
        okButton.click();
    }

    public static ImportProjectsScreen newInstance() {
        By screenRootLocator = By.cssSelector(".uf-workbench-layout");
        WebElement screenRoot = Waits.elementPresent(screenRootLocator);
        return Graphene.createPageFragment(ImportProjectsScreen.class, screenRoot);
    }

    public void waitForLoaded() {
        BusyPopup indicator = PageFactory.initElements(driver, BusyPopup.class);
        indicator.waitForDisappearance();
    }
}
