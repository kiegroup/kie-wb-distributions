/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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
package org.kie.smoke.wb.selenium.util;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class Waits {

    private static final int DEFAULT_TIMEOUT = 15;

    public static WebElement elementVisible(WebDriver driver, By locator, int timeoutSeconds) {
        return new WebDriverWait(driver, timeoutSeconds)
                .until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    public static WebElement elementPresent(WebDriver driver, By locator, int timeoutSeconds) {
        WebElement elementPresent = new WebDriverWait(driver, timeoutSeconds)
                .until(ExpectedConditions.presenceOfElementLocated(locator));
        return elementPresent;
    }

    public static WebElement elementPresent(WebDriver driver, By locator) {
        return elementPresent(driver, locator, DEFAULT_TIMEOUT);
    }

    public static WebElement elementClickable(WebDriver driver, By locator) {
        WebElement clickableElement = new WebDriverWait(driver, DEFAULT_TIMEOUT)
                .until(ExpectedConditions.elementToBeClickable(locator));
        return clickableElement;
    }

    public static boolean isElementPresent(WebDriver driver, By locator) {
        try {
            elementPresent(driver, locator);
            return true;
        } catch (NoSuchElementException nse) {
            return false;
        }
    }

    public static void pause(int miliseconds) {
        try {
            Thread.sleep(miliseconds);
        } catch (InterruptedException ex) {
            System.err.println("Pause interrupted");
        }
    }
}
