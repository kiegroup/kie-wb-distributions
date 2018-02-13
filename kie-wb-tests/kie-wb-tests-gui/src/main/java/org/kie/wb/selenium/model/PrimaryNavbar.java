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

import org.jboss.arquillian.graphene.Graphene;
import org.kie.wb.selenium.model.persps.AbstractPerspective;
import org.kie.wb.selenium.model.persps.AdminPagePerspective;
import org.kie.wb.selenium.model.persps.ProjectLibraryPerspective;
import org.kie.wb.selenium.model.widgets.DropdownMenu;
import org.kie.wb.selenium.util.BusyPopup;
import org.kie.wb.selenium.util.Waits;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.kie.wb.selenium.model.KieSeleniumTest.driver;
import static org.kie.wb.selenium.util.ByUtil.jquery;

public class PrimaryNavbar {

    private static final Logger LOG = LoggerFactory.getLogger(PrimaryNavbar.class);
    //Contains both the link to expand menu as well as menu item links
    private static final String NAVBAR_MENU = ".navbar li.dropdown:has(a#mega-menu-dropdown)";
    private static final String INFO_ALERT = "div.info-alert:not(.uf-cms-nav-alert-panel)";

    @FindBy(css = "#mega-menu > nav")
    private WebElement navbar;

    @FindBy(css = "li[title='Reset all Perspective layouts']+li")
    private DropdownMenu logoutMenu;

    @FindBy(css = ".uf-workbench-layout")
    private WebElement perspectiveRoot;

    @FindBy(css = ".navbar-right a[title=Admin]")
    private WebElement adminLink;

    public void logout() {
        logoutMenu.selectItem("Log Out");
    }

    public ProjectLibraryPerspective projects() {
        return navigateTo(Persp.PROJECTS);
    }

    public AdminPagePerspective admin() {
        adminLink.click();
        return initPerspective(Persp.ADMIN);
    }

    public <T extends AbstractPerspective> T navigateTo(Persp<T> p) {
        LOG.info("Navigating to {}", p);
        selectMenuItem(p.getMenu(), p.getName());
        return initPerspective(p);
    }

    private void selectMenuItem(String menuName, String itemName) {
        Waits.elementAbsent(jquery(INFO_ALERT));
        if ("N/A".equals(menuName)) {
            final By itemLink = jquery("a[title='%s']",
                                       itemName);
            Waits.elementClickable(itemLink);
            BusyPopup.retryClickUntilPopupDisappears(navbar.findElement(itemLink));
        } else {
            WebElement menuRoot = driver.findElement(jquery(NAVBAR_MENU));
            DropdownMenu menu = Graphene.createPageFragment(DropdownMenu.class, menuRoot);
            menu.selectItem(itemName);
        }
    }

    public <T extends AbstractPerspective> T initPerspective(Persp<T> p) {
        T perspective = Graphene.createPageFragment(p.getPerspectivePageObjectClass(), perspectiveRoot);
        perspective.waitForLoaded();
        return perspective;
    }
}
