package org.kie.smoke.wb.selenium.model;

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
import org.kie.smoke.wb.selenium.model.persps.AbstractPerspective;
import org.kie.smoke.wb.selenium.model.persps.AdministrationPerspective;
import org.kie.smoke.wb.selenium.model.persps.AppsPerspective;
import org.kie.smoke.wb.selenium.model.persps.ArtifactRepositoryPerspective;
import org.kie.smoke.wb.selenium.model.persps.ContributorsPerspective;
import org.kie.smoke.wb.selenium.model.persps.DataSetsPerspective;
import org.kie.smoke.wb.selenium.model.persps.HomePerspective;
import org.kie.smoke.wb.selenium.model.persps.JobsPerspective;
import org.kie.smoke.wb.selenium.model.persps.PeoplePerspective;
import org.kie.smoke.wb.selenium.model.persps.PluginManagementPerspective;
import org.kie.smoke.wb.selenium.model.persps.ProcessAndTaskDashboardPerspective;
import org.kie.smoke.wb.selenium.model.persps.ProcessDefinitionsPerspective;
import org.kie.smoke.wb.selenium.model.persps.ProcessDeploymentsPerspective;
import org.kie.smoke.wb.selenium.model.persps.ProcessInstancesPerspective;
import org.kie.smoke.wb.selenium.model.persps.ProjectAuthoringPerspective;
import org.kie.smoke.wb.selenium.model.persps.RuleDeploymentsPerspective;
import org.kie.smoke.wb.selenium.model.persps.TasksPerspective;
import org.kie.smoke.wb.selenium.model.persps.TimelinePerspective;
import static org.kie.smoke.wb.selenium.util.ByUtil.xpath;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class PrimaryNavbar extends PageObject {

    private static final String NAV_MENU //Contains both the link to expand menu as well as menu item links
            = "//nav//li[contains(@class,'dropdown')][a[contains(text(),'%s')]]";

    @FindBy(css = "li[title='Reset all Perspective layouts']+li")
    private WebElement logoutMenu;

    public PrimaryNavbar(WebDriver driver) {
        super(driver);
    }

    public void logout() {
        //Logou menu has no stable title (just username)
        logoutMenu.findElement(By.tagName("a")).click();
        selectItem(logoutMenu, "Log Out");
    }

    public HomePerspective homePage() {
        return navigateTo(Persp.HOME_PAGE);
    }

    public TimelinePerspective timeline() {
        return navigateTo(Persp.TIMELINE);
    }

    public PeoplePerspective people() {
        return navigateTo(Persp.PEOPLE);
    }

    public ProjectAuthoringPerspective projectAuthoring() {
        return navigateTo(Persp.PROJECT_AUTHORING);
    }

    public ContributorsPerspective contributors() {
        return navigateTo(Persp.CONTRIBUTORS);
    }

    public ArtifactRepositoryPerspective artifactRepository() {
        return navigateTo(Persp.ARTIFACT_REPOSITORY);
    }

    public AdministrationPerspective administration() {
        return navigateTo(Persp.ADMINISTRATION);
    }

    public ProcessDeploymentsPerspective processDeployments() {
        return navigateTo(Persp.PROCESS_DEPLOYMENTS);
    }

    public RuleDeploymentsPerspective ruleDeployments() {
        return navigateTo(Persp.RULE_DEPLOYMENTS);
    }

    public JobsPerspective jobs() {
        return navigateTo(Persp.JOBS);
    }

    public ProcessDefinitionsPerspective processDefinitions() {
        return navigateTo(Persp.PROCESS_DEFINITIONS);
    }

    public ProcessInstancesPerspective processInstances() {
        return navigateTo(Persp.PROCESS_INSTANCES);
    }

    public TasksPerspective tasks() {
        return navigateTo(Persp.TASKS);
    }

    public ProcessAndTaskDashboardPerspective processAndTaskDashboard() {
        return navigateTo(Persp.PROCESS_AND_TASK_DASHBOARD);
    }

    public PluginManagementPerspective pluginManagement() {
        return navigateTo(Persp.PLUGIN_MANAGEMENT);
    }

    public AppsPerspective apps() {
        return navigateTo(Persp.APPS);
    }

    public DataSetsPerspective dataSets() {
        return navigateTo(Persp.DATA_SETS);
    }

    public <T extends AbstractPerspective> T navigateTo(Persp<T> p) {
        if (p == Persp.TASKS) {
            //Tasks is not a menu, just a link
            driver.findElement(By.linkText("Tasks")).click();
        } else {
            WebElement menu = openMenu(p.getMenu());
            selectItem(menu, p.getName());
        }
        T perspective = PageFactory.initElements(driver, p.getPerspectivePageObjectClass());
        perspective.waitForLoaded();
        return perspective;
    }

    private WebElement openMenu(String menuTitle) {
        WebElement menu = driver.findElement(xpath(NAV_MENU, menuTitle));
        menu.findElement(By.tagName("a")).click();
        return menu;
    }

    private void selectItem(WebElement openedMenu, String itemText) {
        WebElement menuItem = openedMenu.findElement(By.linkText(itemText));
        menuItem.click();
    }
}
