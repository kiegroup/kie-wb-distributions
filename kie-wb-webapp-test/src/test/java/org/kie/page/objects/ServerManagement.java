package org.kie.page.objects;

import java.util.List;

import org.kie.base.KIESeleniumTest;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class ServerManagement {

    private final WebDriver driver;

    private WebElement register;

    public ServerManagement( WebDriver driver ) {
        this.driver = driver;

        generateActions( driver );
    }

    private void generateActions( WebDriver driver ) {
        List<WebElement> actions = driver.findElements( By.tagName( "a" ) );
        for ( WebElement action : actions ) {
            String innerHTML = action.getAttribute( "innerHTML" );
            if ( innerHTML.contains( "Register" ) ) {
                register = action;
            }
        }
    }

    public RegisterServer register() {
        register.click();
        return new RegisterServer(driver);
    }

    public boolean isDisplayed() {
        KIESeleniumTest.generateWait( driver, register );
        return true;
    }
}
