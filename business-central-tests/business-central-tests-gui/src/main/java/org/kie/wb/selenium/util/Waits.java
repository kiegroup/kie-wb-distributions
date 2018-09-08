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

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class Waits {

    private static final int DEFAULT_TIMEOUT = 15;

    public static WebElement elementVisible(By locator, int timeoutSeconds) {
        return new WebDriverWait(GrapheneUtil.getDriver(), timeoutSeconds)
                .until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    public static WebElement elementPresent(By locator, int timeoutSeconds) {
        return new WebDriverWait(GrapheneUtil.getDriver(), timeoutSeconds)
                .until(ExpectedConditions.presenceOfElementLocated(locator));
    }

    public static WebElement elementPresent(By locator) {
        return elementPresent(locator, DEFAULT_TIMEOUT);
    }

    public static WebElement elementPresent(WebElement element) {
        return new WebDriverWait(GrapheneUtil.getDriver(), DEFAULT_TIMEOUT)
                .until(ExpectedConditions.visibilityOf(element));
    }

    public static void elementAbsent(By locator) {
        new WebDriverWait(GrapheneUtil.getDriver(), DEFAULT_TIMEOUT)
                .until(ExpectedConditions.numberOfElementsToBe(locator, 0));
    }

    public static WebElement elementClickable(By locator) {
        return new WebDriverWait(GrapheneUtil.getDriver(), DEFAULT_TIMEOUT)
                .until(ExpectedConditions.elementToBeClickable(locator));
    }

    public static boolean isElementPresent(By locator, int timeoutSeconds) {
        try {
            elementPresent(locator, timeoutSeconds);
            return true;
        } catch (NoSuchElementException | TimeoutException nse) {
            return false;
        }
    }

    public static boolean isElementPresent(By locator) {
        return isElementPresent(locator, DEFAULT_TIMEOUT);
    }

    public static void pause(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException ex) {
            System.err.println("Pause interrupted");
        }
    }
}