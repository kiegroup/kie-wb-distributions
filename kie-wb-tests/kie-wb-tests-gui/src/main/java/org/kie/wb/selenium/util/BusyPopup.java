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

import org.jboss.arquillian.graphene.Graphene;
import org.kie.wb.selenium.model.PageObject;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BusyPopup extends PageObject {

    private static final String GLASS_CSS_CLASS = "gwt-PopupPanelGlass";
    private static final Logger LOG = LoggerFactory.getLogger(BusyPopup.class);

    public static void waitForDisappearance() {
        By glass = By.className(GLASS_CSS_CLASS);
        Graphene.waitModel().until().element(glass).is().not().present();
        Waits.pause(500);
    }

    /*
     * In Chrome and Firefox click operation might fail if there is gwt-PopupPanelGlass in the way.
     * This method ignores click failures caused by this particular exception and retries the click fixed number of times or until the glass disappears
     */
    public static void retryClickUntilPopupDisappears(WebElement element) {
        int triesRemaining = 100;
        boolean firstTime = true;
        while (triesRemaining > 0) {
            try {
                element.click();
                return;
            } catch (WebDriverException e) {
                if (isGlassException(e)) {
                    triesRemaining--;
                    if (firstTime) {
                        firstTime = false;
                        String firstLineOfExceptionMessage = e.getMessage().split("\n")[0];
                        LOG.warn("Clicking element {} failed: {}",
                                 element,
                                 firstLineOfExceptionMessage);
                    } else {
                        LOG.debug("Retrying click, {} tries remaining.",
                                  triesRemaining);
                    }
                    Waits.pause(100);
                } else {
                    throw e;
                }
            }
        }
    }

    private static boolean isGlassException(WebDriverException e) {
        // Be careful when changing this condition; the exact exception message might differ on FF vs. Chrome.
        return e.getMessage().contains("Other element would receive the click")
                && e.getMessage().contains(GLASS_CSS_CLASS);
    }
}
