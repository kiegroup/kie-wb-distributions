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

package org.kie.base;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class KIESeleniumTest {

    public final static String KIE_PASS = "admin";
    public final static String KIE_URL = "http://localhost:8080/kie-wb";
    public final static String KIE_USER = "admin";

    private WebDriver driver;

    public WebDriver startWebDriver() {
        String phantomBin = System.getProperty( PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY );
        if ( phantomBin == null ) {
            driver = new FirefoxDriver();
        } else {
            DesiredCapabilities caps = new DesiredCapabilities();
            caps.setCapability( "takesScreenshot", true );
            caps.setCapability( PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY,
                                phantomBin );
            driver = new PhantomJSDriver( caps );
        }
        return driver;
    }

    public void shutdownDriver() {
        driver.close();
    }

    public static void generateWait( WebDriver driver,
                                     By element ) {
        WebDriverWait wait = new WebDriverWait( driver, 60 );
        wait.until( ExpectedConditions.elementToBeClickable( element ) );
    }

    public static void generateWait( WebDriver driver,
                                     WebElement element ) {
        WebDriverWait wait = new WebDriverWait( driver, 60 );
        wait.until( ExpectedConditions.elementToBeClickable( element ) );
    }
}
