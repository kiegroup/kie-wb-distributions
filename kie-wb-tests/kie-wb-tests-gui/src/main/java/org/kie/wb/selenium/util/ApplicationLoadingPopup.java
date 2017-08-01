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
package org.kie.wb.selenium.util;

import com.google.common.base.Predicate;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * The dialog shown after the user logs in and the application is loading,
 * or after the user opens a standalone perspective.
 */
public class ApplicationLoadingPopup {

    private static final By loadingDivLocator = By.id("loading");

    public static void waitForDisappearance(int timeoutInSeconds) {
        try {
            waitUntilLoadingPopupBecomesHidden(timeoutInSeconds);
        } catch (TimeoutException ex) {
            throw new RuntimeException("The application loading popup did not disappear within " + timeoutInSeconds + " seconds.");
        }
    }

    private static void waitUntilLoadingPopupBecomesHidden(int timeout) {
        WebDriver driver = GrapheneUtil.getDriver();
        new WebDriverWait(driver, timeout).until((Predicate<WebDriver>) driver1 -> {
            WebElement loadingDiv = driver1.findElement(loadingDivLocator);
            // This element has no style attribute while it is displayed.
            // After the loading finishes, the style attribute is created and set to "visibility: hidden".
            String loadingDivStyle = loadingDiv.getAttribute("style");
            return loadingDivStyle != null & loadingDivStyle.contains("hidden");
        });
    }
}
