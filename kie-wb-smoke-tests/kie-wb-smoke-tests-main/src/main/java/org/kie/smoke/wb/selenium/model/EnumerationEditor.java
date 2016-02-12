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
package org.kie.smoke.wb.selenium.model;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class EnumerationEditor extends PageObject {

    @FindBy(xpath = "//div[@class='uf-listbar-panel-header-toolbar']//button[contains(.,'Save')]")
    private WebElement saveButton;

    public EnumerationEditor(WebDriver driver) {
        super(driver);
    }

    public void save(String commitMessage) {
        saveButton.click();

        //TODO wait for modal to appear
        SaveDialog saveModal = PageFactory.initElements(driver, SaveDialog.class);
        saveModal.save(commitMessage);
    }
}
