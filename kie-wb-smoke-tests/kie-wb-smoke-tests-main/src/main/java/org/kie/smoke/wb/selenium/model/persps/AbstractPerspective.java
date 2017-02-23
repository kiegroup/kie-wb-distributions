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
package org.kie.smoke.wb.selenium.model.persps;

import org.kie.smoke.wb.selenium.model.LoginPage;
import org.kie.smoke.wb.selenium.model.PageObject;
import org.kie.smoke.wb.selenium.model.PrimaryNavbar;
import org.kie.smoke.wb.selenium.util.PageObjectFactory;
import org.kie.smoke.wb.selenium.util.Waits;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;

public abstract class AbstractPerspective extends PageObject {

    private final PrimaryNavbar navbar;

    public AbstractPerspective(WebDriver driver) {
        super(driver);
        navbar = PageFactory.initElements(driver, PrimaryNavbar.class);
    }

    public PrimaryNavbar getNavbar() {
        return navbar;
    }

    /**
     * Waiting for the perspective to be fully loaded. No-op by default.
     */
    public void waitForLoaded() {
    }

    public LoginPage logout() {
        navbar.logout();
        // Click 'Login again' to get back to login page
        WebElement loginAgainButton = Waits.elementClickable(driver, By.cssSelector("input[type='submit'],button"));
        loginAgainButton.click();
        return new PageObjectFactory(driver).createLoginPage();
    }

    public abstract boolean isDisplayed();
}
