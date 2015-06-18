package org.kie.base;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class KIESeleniumTest {

    public final static String KIE_PASS = "admin";
    public final static String KIE_URL = "http://localhost:8080/kie-wb";
    public final static String KIE_USER = "admin";

    private WebDriver driver;

    public WebDriver startWebDriver() {
        String phantomBin = System.getProperty( PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY );
        if ( phantomBin == null ) {
            driver = new FirefoxDriver();
        } else {
            DesiredCapabilities caps = new DesiredCapabilities();
            caps.setCapability( "takesScreenshot", true );
            caps.setCapability( PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY,
                                phantomBin );
            driver = new PhantomJSDriver( caps );
        }
        return driver;
    }

    public void shutdownDriver() {
        driver.close();
    }

    public static void generateWait( WebDriver driver,
                                     By element ) {
        WebDriverWait wait = new WebDriverWait( driver, 60 );
        wait.until( ExpectedConditions.elementToBeClickable( element ) );
    }

    public static void generateWait( WebDriver driver,
                                     WebElement element ) {
        WebDriverWait wait = new WebDriverWait( driver, 60 );
        wait.until( ExpectedConditions.elementToBeClickable( element ) );
    }
}
