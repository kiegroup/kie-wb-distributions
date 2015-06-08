package org.kie.page.objects;

import org.kie.base.KIESeleniumTest;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class KIEWorkbench {

    private final WebDriver driver;
    private KIEMenu kieMenu;

    public KIEWorkbench( WebDriver driver ) {

        this.driver = driver;
        kieMenu = new KIEMenu( driver );
    }

    public ServerManagement accessServerManagementMenu() {

        kieMenu.accessMenuItem( KIEMenu.SERVER_MANAGEMENT );

        return new ServerManagement( driver );
    }

    public boolean isDisplayed(){
        KIESeleniumTest.generateWait( driver, By.className( "container" ) );
        return true;
    }
}
