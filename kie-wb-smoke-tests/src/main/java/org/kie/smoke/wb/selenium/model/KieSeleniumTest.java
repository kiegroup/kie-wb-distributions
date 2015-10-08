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
package org.kie.smoke.wb.selenium.model;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.kie.smoke.wb.selenium.util.PageObjectFactory;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

public class KieSeleniumTest {

    protected static WebDriver driver;
    protected static PageObjectFactory pof;

    @BeforeClass
    public static void startWebDriver() {
        //TODO - logic to choose WebDriver implementation based on configuration
        //TODO - InternetExplorerDriver & ChromeDriver require additional binary file to run
        driver = new FirefoxDriver();
        pof = new PageObjectFactory(driver);
    }

    @AfterClass
    public static void stopWebDriver() {
        driver.close();
    }
}
