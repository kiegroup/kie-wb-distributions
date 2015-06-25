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
