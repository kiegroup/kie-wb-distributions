/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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
package org.kie.wb.selenium.model;

import static org.kie.wb.selenium.model.KieSeleniumTest.driver;
import static org.kie.wb.selenium.util.ByUtil.jquery;

import org.jboss.arquillian.graphene.Graphene;
import org.kie.wb.selenium.model.persps.AbstractPerspective;
import org.kie.wb.selenium.model.persps.AdministrationPerspective;
import org.kie.wb.selenium.model.persps.AppsPerspective;
import org.kie.wb.selenium.model.persps.ArtifactRepositoryPerspective;
import org.kie.wb.selenium.model.persps.ContributorsPerspective;
import org.kie.wb.selenium.model.persps.DataSetsPerspective;
import org.kie.wb.selenium.model.persps.HomePerspective;
import org.kie.wb.selenium.model.persps.JobsPerspective;
import org.kie.wb.selenium.model.persps.PeoplePerspective;
import org.kie.wb.selenium.model.persps.PluginManagementPerspective;
import org.kie.wb.selenium.model.persps.ProcessAndTaskDashboardPerspective;
import org.kie.wb.selenium.model.persps.ProcessDefinitionsPerspective;
import org.kie.wb.selenium.model.persps.ProcessInstancesPerspective;
import org.kie.wb.selenium.model.persps.ProjectAuthoringPerspective;
import org.kie.wb.selenium.model.persps.ProjectLibraryPerspective;
import org.kie.wb.selenium.model.persps.RuleDeploymentsPerspective;
import org.kie.wb.selenium.model.persps.TasksPerspective;
import org.kie.wb.selenium.model.persps.TimelinePerspective;
import org.kie.wb.selenium.model.widgets.DropdownMenu;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PrimaryNavbar {

    private static final Logger LOG = LoggerFactory.getLogger(PrimaryNavbar.class);
    //Contains both the link to expand menu as well as menu item links
    private static final String NAVBAR_MENU = ".navbar-primary > li.dropdown:has(a:contains('%s'))";

    @FindBy(linkText = "Tasks")
    private WebElement tasksLink;

    @FindBy(css = "li[title='Reset all Perspective layouts']+li")
    private DropdownMenu logoutMenu;

    @FindBy(css = ".uf-workbench-layout")
    private WebElement perspectiveRoot;

    public void logout() {
        logoutMenu.selectItem("Log Out");
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

    public ProjectLibraryPerspective projectLibrary() {
        return navigateTo(Persp.PROJECT_LIBRARY);
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

    public RuleDeploymentsPerspective ruleDeployments() {
        return navigateTo(Persp.EXECUTION_SERVERS);
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
        LOG.info("Navigating to {}", p);
        if (p == Persp.TASKS) {
            tasksLink.click(); //Tasks is not a menu, just a link
        } else {
            selectMenuItem(p.getMenu(), p.getName());
        }
        return initPerspective(p);
    }

    private void selectMenuItem(String menuName, String itemName) {
        WebElement menuRoot = driver.findElement(jquery(NAVBAR_MENU, menuName));
        DropdownMenu menu = Graphene.createPageFragment(DropdownMenu.class, menuRoot);
        menu.selectItem(itemName);
    }

    private <T extends AbstractPerspective> T initPerspective(Persp<T> p) {
        T perspective = Graphene.createPageFragment(p.getPerspectivePageObjectClass(), perspectiveRoot);
        perspective.waitForLoaded();
        return perspective;
    }
}
