/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ObstructedClickExceptionHandler {

    private static final Logger LOG = LoggerFactory.getLogger(BusyPopup.class);

    /*
     * Clicking an element might fail if there is gwt-PopupPanelGlass or alert in the way.
     * This method ignores click failures caused by WebDriverException's like
     *
     * <pre>
     * Element <button id="deploy" class="btn btn-default" type="button"> is not clickable at point (1213.5,128.60000610351562)
     * because another element <div class="alert alert-dismissable alert-success"> obscures it
     * </pre>
     *
     * and retries clicking until the obstructing element disappears
     *
     *
     * WARNING: the standard selenium way of dealing with these exceptions (waiting for something to become true
     * using WebDriverWait) doesn't work because of the non-predictable nature of these popups/alerts,
     * so it's not clear where such waits should be placed.
     */
    public static void retryClickUntilNotObstructed(WebElement element) {
        int triesRemaining = 100;
        boolean firstTime = true;
        while (triesRemaining > 0) {
            try {
                element.click();
                return;
            } catch (WebDriverException e) {
                if (isClickObstructedByPopupPanelGlass(e) || isClickObstructedByAlert(e)) {
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

    private static boolean isClickObstructedByPopupPanelGlass(WebDriverException e) {
        return e.getMessage().contains("gwt-PopupPanelGlass");
    }

    private static boolean isClickObstructedByAlert(WebDriverException e) {
        return e.getMessage().contains("alert-dismissable");
    }
}
