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
package org.kie.wb.selenium.model.persps;

import org.jboss.arquillian.graphene.Graphene;
import org.jboss.arquillian.graphene.page.Page;
import org.kie.wb.selenium.model.PageObject;
import org.kie.wb.selenium.model.PrimaryNavbar;
import org.kie.wb.selenium.model.widgets.Panel;
import org.kie.wb.selenium.util.BusyPopup;
import org.kie.wb.selenium.util.Waits;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import static org.kie.wb.selenium.util.ByUtil.jquery;

public abstract class AbstractPerspective extends PageObject {

    @Page
    private PrimaryNavbar navbar;
    @FindBy(css = "input[type='submit']")
    private WebElement loginAgainButton;

    public PrimaryNavbar getNavbar() {
        return navbar;
    }

    public void logout() {
        navbar.logout();
        // Click 'Login again' to get back to login page
        Graphene.waitModel().until().element(loginAgainButton).is().present();
        loginAgainButton.click();
    }

    public abstract boolean isDisplayed();

    /**
     * Waiting for the perspective to be fully loaded. No-op by default.
     */
    public void waitForLoaded() {
    }

    public <T extends Panel> T createPanel(Class<T> panelClass, String title) {
        By panelLoc = jquery(".uf-listbar-panel:contains('%s')", title);
        WebElement panelRoot = Waits.elementPresent(panelLoc, 3);
        return Graphene.createPageFragment(panelClass, panelRoot);
    }

    public void click(By locatorOfThingToClick) {
        WebElement thingToClick = Waits.elementPresent(locatorOfThingToClick);
        BusyPopup.retryClickUntilPopupDisappears(thingToClick);
    }
}
