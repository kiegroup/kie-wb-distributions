package org.kie.smoke.wb.selenium.util;

import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FileUtils;
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

    private final WebDriver driver;
    private final File screenshotDir;

    public ScreenshotOnFailure(WebDriver driver) {
        this.driver = driver;
        this.screenshotDir = initScreenshotDir();
    }

    @Override
    protected void failed(Throwable e, Description description) {
        String testClassName = description.getTestClass().getSimpleName();
        String testMethodName = description.getMethodName();
        String filename = testClassName + "_" + testMethodName;
        takeScreenshot(filename);
        savePageHtmlSource(filename);
    }

    private void takeScreenshot(String filename) {
        File tmpScreenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        File targetScreenshot = new File(screenshotDir, filename + ".png");
        try {
            FileUtils.copyFile(tmpScreenshot, targetScreenshot);
            System.out.println("Screenshot on failure taken: " + targetScreenshot);
        } catch (IOException ex) {
            System.err.print("Failed to take a screenshot on failed test " + filename + " " + ex.getMessage());
        }
    }

    private void savePageHtmlSource(String filename) {
        String pageSource = driver.getPageSource();
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
}
