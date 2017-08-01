/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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
package org.kie.wb.selenium.model;

import org.jboss.arquillian.graphene.page.Page;
import org.kie.wb.selenium.model.persps.HomePerspective;
import org.kie.wb.selenium.util.Waits;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class LoginPage extends PageObject {

    @FindBy(name = "j_username")
    private WebElement usernameInput;
    @FindBy(name = "j_password")
    private WebElement passwordInput;
    @FindBy(className = "button")
    private WebElement loginButton;

    public final static String BASE_URL = System.getProperty("kie.wb.url");

    // Credentials based on Cargo Maven plugin config in ../kie-wb-tests/pom.xml
    public final static String KIE_PASS = "admin1234;";
    public final static String KIE_USER = "admin";

    @Page
    private HomePerspective home;

    public HomePerspective loginDefaultUser() {
        return loginAs(KIE_USER, KIE_PASS);
    }

    public HomePerspective loginAs(String username, String password) {
        submitCredentials(username, password);
        home.waitForLoaded();
        return home;
    }

    public void getLoginPage(){
        driver.get(BASE_URL);
    }

    private void submitCredentials(String username, String password) {
        usernameInput.sendKeys(username);
        passwordInput.sendKeys(password);
        loginButton.submit();
    }

    public boolean isDisplayed() {
        return Waits.isElementPresent(By.name("j_username"));
    }
}
