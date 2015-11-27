package org.kie.smoke.wb.selenium.model;

/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import static org.kie.smoke.wb.selenium.util.ByUtil.xpath;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class PrimaryNavbar extends PageObject {

    private static final String NAV_MENU //Contains both the link to expand menu as well as menu item links
            = "//nav//li[@class='dropdown'][a[contains(text(),'%s')]]";
    private static final By LOGOUT_MENU // Unlike other navbar menus this has no stable link text
            = By.cssSelector("li[title='Reset all Perspective layouts']+li");

    public PrimaryNavbar(WebDriver driver) {
        super(driver);
    }

    private WebElement openMenu(String menuTitle) {
        WebElement menu = driver.findElement(xpath(NAV_MENU, menuTitle));
        menu.findElement(By.tagName("a")).click();
        return menu;
    }

    private void selectItem(WebElement openedMenu, String itemText) {
        WebElement menuItem = openedMenu.findElement(By.linkText(itemText));
        menuItem.click();
    }

    public void logout() {
        WebElement menu = driver.findElement(LOGOUT_MENU);
        menu.findElement(By.tagName("a")).click();
        selectItem(menu, "Log Out");
    }

    public void projectAuthoring() {
        WebElement menu = openMenu("Authoring");
        selectItem(menu, "Project Authoring");
    }

    //TODO all items to navigate to other perspectivesS
}
