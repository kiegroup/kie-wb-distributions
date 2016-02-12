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
package org.kie.smoke.wb.selenium.model.persps;

import org.kie.smoke.wb.selenium.model.PageObject;
import org.kie.smoke.wb.selenium.util.Waits;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class ProjectEditor extends PageObject {

    private static final By PROJ_EDITOR_TITLE = By.cssSelector(".uf-listbar-panel-header-title-text[title^=Project]");

    public ProjectEditor(WebDriver driver) {
        super(driver);
        //Wait for Project Editor to be opened
        Waits.elementPresent(driver, PROJ_EDITOR_TITLE, 5);
    }

    public void buildAndDeploy() {
        driver.findElement(By.xpath("//button[contains(.,'Build')]")).click();
        Waits.elementVisible(driver, By.linkText("Build & Deploy"), 1).click();
    }
}
