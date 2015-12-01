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

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;

/**
 * Class responsible for instantiating WebDriver instance.
 */
public class WebDriverFactory {

    public static WebDriver create() {
        String browser = System.getProperty("browser");
        if (browser == null || "firefox".equalsIgnoreCase(browser)) {
            return new FirefoxDriver();
        } else if ("ie".equalsIgnoreCase(browser)) {
            // System property "webdriver.ie.driver" specifying path to IEDriverServer.exe
            // is set by failsafe plugin configuration kie-wb-smoke-tests's pom.xml in "ie" profile
            return new InternetExplorerDriver();
        } else {
            throw new IllegalArgumentException("Unrecognized value of property browser='" + browser
                    + "'. The only supported values are 'ie', 'firefox' or null (= defaults to firefox)");
        }
    }
}
