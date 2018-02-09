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

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.kie.wb.selenium.util.GrapheneUtil.getDriver;

/**
 * JUnit Rule for taking screenshots/saving page HTML source of browser content
 * on failed selenium test.
 */
public class ScreenshotOnFailure extends TestWatcher {

    private static final Logger LOG = LoggerFactory.getLogger(ScreenshotOnFailure.class);
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
            LOG.info("Screenshot on failure taken: {}", targetScreenshot);
        } catch (IOException ex) {
            LOG.info("Failed to take a screenshot on failed test {} : {}", filename, ex.getMessage());
        }
    }

    private void savePageHtmlSource(String filename) {
        String pageSource = GrapheneUtil.getDriver().getPageSource();
        File targetFile = new File(screenshotDir, filename + ".html");
        try {
            FileUtils.writeStringToFile(targetFile, pageSource, "UTF-8");
            LOG.info("Saved page HTML source on failure: {}", targetFile);
        } catch (IOException ex) {
            LOG.info("Failed to save page HTML source ", ex);
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
}
