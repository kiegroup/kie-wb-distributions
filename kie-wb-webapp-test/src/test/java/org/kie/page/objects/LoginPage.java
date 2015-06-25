/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.kie.page.objects;

import java.util.concurrent.TimeUnit;

import org.kie.base.KIESeleniumTest;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;

public class LoginPage {

    private final WebDriver driver;
    private final Wait wait;

    public LoginPage( WebDriver driver ) {
        this.driver = driver;

        driver.navigate().to( KIESeleniumTest.KIE_URL );

        wait = new FluentWait( driver ).withTimeout( 30, TimeUnit.SECONDS ).pollingEvery( 2, TimeUnit.SECONDS );
    }

    public KIEWorkbench loginAs( String username,
                                 String password ) {

        executeLogin( username, password );
        waitForHomePageLoad();
        return new KIEWorkbench( driver );
    }

    private void executeLogin( String username,
                               String password ) {
        driver.findElement( By.name( "j_username" ) ).sendKeys( username );
        driver.findElement( By.name( "j_password" ) ).sendKeys( password );
        driver.findElement( By.className( "button" ) ).submit();

    }

    private void waitForHomePageLoad() {
        new KIEWorkbench( driver ).isDisplayed();
    }

}
