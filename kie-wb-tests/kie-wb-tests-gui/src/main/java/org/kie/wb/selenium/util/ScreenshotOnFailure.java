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
package org.kie.wb.selenium.util;

import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FileUtils;
import org.jboss.arquillian.drone.api.annotation.Default;
import org.jboss.arquillian.graphene.context.GrapheneContext;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

/**
 * JUnit Rule for taking screenshots/saving page HTML source of browser content
 * on failed selenium test.
 */
public class ScreenshotOnFailure extends TestWatcher {

    private final File screenshotDir = initScreenshotDir();

    @Override
    protected void failed(Throwable e, Description description) {
        String testClassName = description.getTestClass().getSimpleName();
        String testMethodName = description.getMethodName();
        String filename = testClassName + "_" + testMethodName;
        takeScreenshot(filename);
        savePageHtmlSource(filename);
    }

    private void takeScreenshot(String filename) {
        File tmpScreenshot = ((TakesScreenshot) getDriver()).getScreenshotAs(OutputType.FILE);
        File targetScreenshot = new File(screenshotDir, filename + ".png");
        try {
            FileUtils.copyFile(tmpScreenshot, targetScreenshot);
            System.out.println("Screenshot on failure taken: " + targetScreenshot);
        } catch (IOException ex) {
            System.err.print("Failed to take a screenshot on failed test " + filename + " " + ex.getMessage());
        }
    }

    private void savePageHtmlSource(String filename) {
        String pageSource = getDriver().getPageSource();
        File targetFile = new File(screenshotDir, filename + ".html");
        try {
            FileUtils.writeStringToFile(targetFile, pageSource, "UTF-8");
            System.out.println("Saved page HTML source on failure: " + targetFile);
        } catch (IOException ex) {
            System.err.println("Failed to save page HTML source " + ex.getMessage());
        }
    }

    private File initScreenshotDir() {
        String dir = System.getProperty("selenium.screenshots.dir");
        if (dir == null) {
            throw new IllegalStateException("Property selenium.screenshots.dir "
                    + "(where screenshot taken by WebDriver will be put) must be defined: " + dir);
        }
        File scd = new File(dir);
        if (!scd.exists()) {
            boolean mkdirSuccess = scd.mkdir();
            if (!mkdirSuccess) {
                throw new IllegalStateException("Creation of screenshots dir failed " + scd);
            }
        }
        if (!scd.canWrite()) {
            throw new IllegalStateException("The screenshotDir must be writable" + scd);
        }
        return scd;
    }

    private WebDriver getDriver() {
        return GrapheneContext.getContextFor(Default.class).getWebDriver(TakesScreenshot.class);
    }
}
