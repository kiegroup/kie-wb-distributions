/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.smoke.wb.selenium.util;

import com.google.common.base.Predicate;
import org.kie.smoke.wb.selenium.model.PageObject;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

public class LoadingIndicator extends PageObject {

    public LoadingIndicator(WebDriver driver) {
        super(driver);
    }

    public void disappear(String msg) {
        final By loadingIndicator = By.xpath("//div[@class='gwt-Label'][contains(.,'" + msg + "')]");
        new WebDriverWait(driver, 10).until(new Predicate<WebDriver>() {
            @Override
            public boolean apply(WebDriver driver) {
                return driver.findElements(loadingIndicator).isEmpty();
            }
        });
    }
}
