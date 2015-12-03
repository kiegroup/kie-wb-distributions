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
package org.kie.smoke.wb.base.selenium.model;

import org.kie.smoke.wb.base.selenium.util.PageObjectFactory;
import org.kie.smoke.wb.base.selenium.util.Waits;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;

public class HomePerspective extends PageObject {

    private final PrimaryNavbar navbar;
    private static final By CAROUSEL = By.className("carousel-caption");

    public HomePerspective(WebDriver driver) {
        super(driver);
        navbar = PageFactory.initElements(driver, PrimaryNavbar.class);
    }

    public void waitForLoaded() {
        Waits.elementPresent(driver, CAROUSEL);
    }

    public boolean isDisplayed() {
        try {
            Waits.elementPresent(driver, CAROUSEL, 2);
            return true;
        } catch (NoSuchElementException nse) {
            return false;
        }
    }

    public LoginPage logout() {
        navbar.logout();
        // Click 'Login again' to get back to login page
        WebElement loginAgainButton = Waits.elementClickable(driver, By.cssSelector("input[value='Login again']"));
        loginAgainButton.click();
        return new PageObjectFactory(driver).createLoginPage();
    }
}
