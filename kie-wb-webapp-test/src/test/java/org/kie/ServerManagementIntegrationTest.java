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

package org.kie;

import java.lang.Exception;

import org.kie.base.KIESeleniumTest;
import org.kie.page.objects.KIEWorkbench;
import org.kie.page.objects.LoginPage;
import org.kie.page.objects.RegisterServer;
import org.kie.page.objects.ServerManagement;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.WebDriver;

import static org.junit.Assert.*;

public class ServerManagementIntegrationTest {

    private KIESeleniumTest kieSeleniumTest = new KIESeleniumTest();

    private WebDriver driver;

    private KIEWorkbench kie;

    private ServerManagement serverManagement;

    private RegisterServer registerServer;

    @Before
    public void before() {
        driver = kieSeleniumTest.startWebDriver();
    }

    @After
    public void after() {
        kieSeleniumTest.shutdownDriver();
    }

    @Test
    public void showRegisterServerPopup() throws Exception{

        givenALoggedUserAs( KIESeleniumTest.KIE_USER, KIESeleniumTest.KIE_PASS );

        whenIAccessServerManagement();
        andRegisterAServer();
        thenRegisterServerIsDisplayed();

        whenICancelRegister();
        thenRegisterServerIsClosed();

    }

    private void givenALoggedUserAs( String kieUser,
                                     String kiePass ) {
        LoginPage loginPage = new LoginPage( driver );
        kie = loginPage.loginAs( kieUser, kiePass );
    }

    private void whenIAccessServerManagement() {
        serverManagement = kie.accessServerManagementMenu();
    }

    private void andRegisterAServer() {
        registerServer = serverManagement.register();
    }

    private void thenRegisterServerIsDisplayed() {
        assertTrue( registerServer.isDisplayed() );
    }

    private void whenICancelRegister() {
        registerServer.cancel();
    }

    private void thenRegisterServerIsClosed() {
        assertTrue( serverManagement.isDisplayed() );
    }

}