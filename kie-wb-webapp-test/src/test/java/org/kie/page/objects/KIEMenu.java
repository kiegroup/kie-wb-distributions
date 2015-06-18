package org.kie.page.objects;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class KIEMenu {

    public static final String SERVER_MANAGEMENT = "Rule Deployments";

    private final WebDriver driver;

    public KIEMenu( WebDriver driver ) {
        this.driver = driver;
    }

    public void accessMenuItem( String targetMenuLabel ) {
        List<WebElement> allMenus = driver.findElements( By.className( "dropdown-menu" ) );
        for ( WebElement menu : allMenus ) {
            List<WebElement> menuElements = findAllMenuElements( menu );
            for ( WebElement menuItem : menuElements ) {
                if ( isTheTargetMenu( targetMenuLabel, menuItem ) ) {
                    clickMenu( menuItem );
                }
            }
        }
    }

    private List<WebElement> findAllMenuElements( WebElement menu ) {
        return menu.findElements( By.tagName( "a" ) );
    }

    private void clickMenu( WebElement menuItem ) {
        JavascriptExecutor executor = (JavascriptExecutor) driver;
        executor.executeScript( "arguments[0].click();", menuItem );
    }

    private boolean isTheTargetMenu( String menuItemLabel,
                                     WebElement menuItem ) {
        String innerHTML = menuItem.getAttribute( "innerHTML" );
        return innerHTML.contains( menuItemLabel );
    }
}
