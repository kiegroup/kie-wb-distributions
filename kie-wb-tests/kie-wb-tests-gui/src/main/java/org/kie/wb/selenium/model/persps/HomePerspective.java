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
package org.kie.wb.selenium.model.persps;

import java.time.Duration;

import org.kie.wb.selenium.util.Waits;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HomePerspective extends AbstractPerspective {

    private static final Logger LOG = LoggerFactory.getLogger(HomePerspective.class);
    private static final By HOME_CONTENT = By.className("kie-page");
    private static final int DEFAULT_HOME_PERSP_LOADING_TIMEOUT_SECONDS = 15;
    // Troubleshooting webapp loading issues on tomcat / jenkins slaves where the default 15 seconds is not enough
    private static final int HOME_PERSP_LOADING_TIMEOUT_SECONDS = getTimeoutSeconds();

    @Override
    public void waitForLoaded() {
        long start = System.currentTimeMillis();
        Waits.elementPresent(HOME_CONTENT, HOME_PERSP_LOADING_TIMEOUT_SECONDS);
        long homePageLoadingDurationMillis = System.currentTimeMillis() - start;
        Duration loadingDuration = Duration.ofMillis(homePageLoadingDurationMillis);
        LOG.info("It took {} seconds to load home page.", loadingDuration.getSeconds());
    }

    @Override
    public boolean isDisplayed() {
        try {
            Waits.elementPresent(HOME_CONTENT, 2);
            return true;
        } catch (NoSuchElementException nse) {
            return false;
        }
    }

    private static int getTimeoutSeconds() {
        String timeout = System.getProperty("selenium.homepage.loading.timeout.seconds");
        try {
            return Integer.parseInt(timeout);
        } catch (NumberFormatException nfe) {
            return DEFAULT_HOME_PERSP_LOADING_TIMEOUT_SECONDS;
        }
    }
}
