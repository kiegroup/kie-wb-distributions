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

public class ServerManagementTest {

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