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
import org.junit.Rule;
import org.kie.smoke.wb.selenium.util.PageObjectFactory;
import org.kie.smoke.wb.selenium.util.ScreenshotOnFailure;
import org.openqa.selenium.WebDriver;

public class KieSeleniumTest {

    protected static WebDriver driver;
    protected static PageObjectFactory pof;
    @Rule
    public ScreenshotOnFailure screenshotter = new ScreenshotOnFailure(driver);

    @BeforeClass
    public static void startWebDriver() {
        driver = WebDriverFactory.create();
        pof = new PageObjectFactory(driver);
    }

    @AfterClass
    public static void stopWebDriver() {
        if (driver != null) {
            driver.quit();
        }
    }
}
