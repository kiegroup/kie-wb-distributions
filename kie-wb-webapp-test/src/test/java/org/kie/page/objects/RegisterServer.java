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

import org.kie.base.KIESeleniumTest;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class RegisterServer {

    private WebElement cancel;
    private WebDriver driver;

    public RegisterServer( WebDriver driver ) {
        this.driver = driver;
        generateActions( driver );
    }

    private void generateActions( WebDriver driver ) {
        WebElement modal = driver.findElement( By.className( "modal-footer" ) );
        for ( WebElement modalButton : modal.findElements( By.className( "btn" ) ) ) {
            String innerHTML = modalButton.getAttribute( "innerHTML" );
            if ( innerHTML.contains( "Cancel" ) ) {
                cancel = modalButton;
            }
        }
    }

    public boolean isDisplayed() {
        try{
            driver.findElement( By.className( "modal-footer" ) );
            return true;
        }
        catch ( Exception e ){
            return false;
        }
    }

    public void cancel() {
        KIESeleniumTest.generateWait( driver, By.className( "modal-footer" ) );
        cancel.click();
    }
}
