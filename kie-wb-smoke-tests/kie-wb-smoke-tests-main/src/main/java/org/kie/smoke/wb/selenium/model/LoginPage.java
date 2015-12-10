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
package org.kie.smoke.wb.selenium.model;

import org.kie.smoke.wb.selenium.model.persps.HomePerspective;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class LoginPage extends PageObject {

    @FindBy(name = "j_username")
    private WebElement usernameInput;
    @FindBy(name = "j_password")
    private WebElement passwordInput;
    @FindBy(className = "button")
    private WebElement loginButton;
    public final static String BASE_URL = System.getProperty("deployable.base.uri");

    public LoginPage(WebDriver driver) {
        super(driver);
    }

    public HomePerspective loginAs(String username, String password) {
        driver.get(BASE_URL);
        submitCredentials(username, password);
        HomePerspective hp = new HomePerspective(driver);
        hp.waitForLoaded();
        return hp;
    }

    private void submitCredentials(String username, String password) {
        usernameInput.sendKeys(username);
        passwordInput.sendKeys(password);
        loginButton.submit();
    }

    public boolean isDisplayed() {
        return usernameInput.isDisplayed();
    }
}
