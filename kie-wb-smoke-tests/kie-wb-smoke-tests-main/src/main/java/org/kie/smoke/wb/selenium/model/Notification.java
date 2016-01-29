/*
 * Copyright 2016 JBoss by Red Hat.
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

import org.kie.smoke.wb.selenium.util.Waits;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class Notification extends PageObject {

    @FindBy(css = "button[data-dismiss='alert']")
    private WebElement closeButton;

    public Notification(WebDriver driver) {
        super(driver);
    }

    public String getMessage() {
        WebElement popup = Waits.elementPresent(driver, By.cssSelector(".popupMiddle .alert"), 10);
        String msg = popup.getText();
        closeButton.click();
        return msg;
    }
}
