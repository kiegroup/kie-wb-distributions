/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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
package org.kie.smoke.wb.selenium.model.persps;

import org.jboss.arquillian.graphene.page.Page;
import org.kie.smoke.wb.selenium.util.BusyPopup;
import org.kie.smoke.wb.selenium.util.Waits;
import org.openqa.selenium.By;

public class TasksPerspective extends AbstractPerspective {

    private static final By ACTIVE_FILTER_TITLE = By.xpath("//h4[contains(text(),'Filter Active')]");
    @Page
    private BusyPopup loadingIndicator;

    @Override
    public boolean isDisplayed() {
        return Waits.isElementPresent(driver, ACTIVE_FILTER_TITLE);
    }

    @Override
    public void waitForLoaded() {
        loadingIndicator.waitForDisappearance();
    }
}
