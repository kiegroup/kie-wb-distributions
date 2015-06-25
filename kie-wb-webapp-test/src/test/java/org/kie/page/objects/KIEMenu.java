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

package org.kie.page.objects;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class KIEMenu {

    public static final String SERVER_MANAGEMENT = "Rule Deployments";

    private final WebDriver driver;

    public KIEMenu( WebDriver driver ) {
        this.driver = driver;
    }

    public void accessMenuItem( String targetMenuLabel ) {
        List<WebElement> allMenus = driver.findElements( By.className( "dropdown-menu" ) );
        for ( WebElement menu : allMenus ) {
            List<WebElement> menuElements = findAllMenuElements( menu );
            for ( WebElement menuItem : menuElements ) {
                if ( isTheTargetMenu( targetMenuLabel, menuItem ) ) {
                    clickMenu( menuItem );
                }
            }
        }
    }

    private List<WebElement> findAllMenuElements( WebElement menu ) {
        return menu.findElements( By.tagName( "a" ) );
    }

    private void clickMenu( WebElement menuItem ) {
        JavascriptExecutor executor = (JavascriptExecutor) driver;
        executor.executeScript( "arguments[0].click();", menuItem );
    }

    private boolean isTheTargetMenu( String menuItemLabel,
                                     WebElement menuItem ) {
        String innerHTML = menuItem.getAttribute( "innerHTML" );
        return innerHTML.contains( menuItemLabel );
    }
}
