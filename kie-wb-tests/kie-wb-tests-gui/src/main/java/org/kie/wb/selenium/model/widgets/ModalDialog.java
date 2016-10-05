/*
 * Copyright 2016 JBoss by Red Hat.
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
package org.kie.wb.selenium.model.widgets;

import static org.kie.wb.selenium.util.ByUtil.jquery;

import org.jboss.arquillian.graphene.Graphene;
import org.jboss.arquillian.graphene.findby.FindByJQuery;
import org.kie.wb.selenium.model.PageObject;
import org.kie.wb.selenium.util.Waits;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class ModalDialog extends PageObject {

    @FindByJQuery("button.close")
    private WebElement closeButton;

    public static <T extends ModalDialog> T newInstance(Class<T> pageFragmentClass, String modalTitle) {
        By modalRootLocator = jquery(".modal-content:has(.modal-header:contains('%s'))", modalTitle);
        WebElement modalRoot = Waits.elementPresent(modalRootLocator, 3);
        return Graphene.createPageFragment(pageFragmentClass, modalRoot);
    }

    public void close() {
        closeButton.click();
    }

    public void ok() {
        clickButton("Ok");
        waitForDisappearance();
    }

    public void finish() {
        clickButton("Finish");
        waitForDisappearance();
    }

    public void next() {
        clickButton("Next");
    }

    public void clickButton(String buttonText) {
        By buttonLoc = jquery(".modal-footer > .btn:contains('%s'):not([disabled])", buttonText);
        WebElement button = Waits.elementPresent(buttonLoc);
        button.click();
    }

    protected void waitForDisappearance() {
        Waits.elementAbsent(By.cssSelector(".modal.in"));
    }
}