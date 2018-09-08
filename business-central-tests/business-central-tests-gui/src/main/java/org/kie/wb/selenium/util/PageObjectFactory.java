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

import org.kie.wb.selenium.model.LoginPage;
import org.kie.wb.selenium.model.PrimaryNavbar;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;

/**
 * Utility for creating page objects without having to explicitly manipulate
 * WebDriver.
 */
public class PageObjectFactory {

    private final WebDriver driver;

    public PageObjectFactory(WebDriver driver) {
        this.driver = driver;
    }

    public LoginPage createLoginPage() {
        return createPageObject(LoginPage.class);
    }

    public PrimaryNavbar createNavBar() {
        return createPageObject(PrimaryNavbar.class);
    }

    private <T> T createPageObject(Class<T> pageObjectClass) {
        return PageFactory.initElements(driver, pageObjectClass);
    }
}
