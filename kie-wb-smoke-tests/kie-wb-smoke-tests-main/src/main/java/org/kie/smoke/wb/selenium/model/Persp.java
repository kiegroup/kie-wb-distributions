/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.smoke.wb.selenium.model;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
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

public class Persp<T extends AbstractPerspective> {

    public static final Persp<HomePerspective> HOME_PAGE
            = new Persp<HomePerspective>("Home", "Home Page", HomePerspective.class);

    public static final Persp<TimelinePerspective> TIMELINE
            = new Persp<TimelinePerspective>("Home", "Timeline", TimelinePerspective.class);

    public static final Persp<PeoplePerspective> PEOPLE
            = new Persp<PeoplePerspective>("Home", "People", PeoplePerspective.class);

    public static final Persp<ProjectAuthoringPerspective> PROJECT_AUTHORING
            = new Persp<ProjectAuthoringPerspective>("Authoring", "Project Authoring", ProjectAuthoringPerspective.class);

    public static final Persp<ContributorsPerspective> CONTRIBUTORS
            = new Persp<ContributorsPerspective>("Authoring", "Contributors", ContributorsPerspective.class);

    public static final Persp<ArtifactRepositoryPerspective> ARTIFACT_REPOSITORY
            = new Persp<ArtifactRepositoryPerspective>("Authoring", "Artifact repository", ArtifactRepositoryPerspective.class);

    public static final Persp<AdministrationPerspective> ADMINISTRATION
            = new Persp<AdministrationPerspective>("Authoring", "Administration", AdministrationPerspective.class);

    public static final Persp<ProcessDeploymentsPerspective> PROCESS_DEPLOYMENTS
            = new Persp<ProcessDeploymentsPerspective>("Deploy", "Process Deployments", ProcessDeploymentsPerspective.class, true);

    public static final Persp<RuleDeploymentsPerspective> RULE_DEPLOYMENTS
            = new Persp<RuleDeploymentsPerspective>("Deploy", "Execution Servers", RuleDeploymentsPerspective.class);

    public static final Persp<JobsPerspective> JOBS
            = new Persp<JobsPerspective>("Deploy", "Jobs", JobsPerspective.class, true);

    public static final Persp<ProcessDefinitionsPerspective> PROCESS_DEFINITIONS
            = new Persp<ProcessDefinitionsPerspective>("Process Management", "Process Definitions", ProcessDefinitionsPerspective.class, true);

    public static final Persp<ProcessInstancesPerspective> PROCESS_INSTANCES
            = new Persp<ProcessInstancesPerspective>("Process Management", "Process Instances", ProcessInstancesPerspective.class, true);

    public static final Persp<TasksPerspective> TASKS
            = new Persp<TasksPerspective>("N/A", "Tasks", TasksPerspective.class);

    public static final Persp<ProcessAndTaskDashboardPerspective> PROCESS_AND_TASK_DASHBOARD
            = new Persp<ProcessAndTaskDashboardPerspective>("Dashboards", "Process & Task Dashboard", ProcessAndTaskDashboardPerspective.class, true);

    public static final Persp<PluginManagementPerspective> PLUGIN_MANAGEMENT
            = new Persp<PluginManagementPerspective>("Extensions", "PlugIn Management", PluginManagementPerspective.class);

    public static final Persp<AppsPerspective> APPS
            = new Persp<AppsPerspective>("Extensions", "Apps", AppsPerspective.class);

    public static final Persp<DataSetsPerspective> DATA_SETS
            = new Persp<DataSetsPerspective>("Extensions", "Data Sets", DataSetsPerspective.class);

    private static final List<Persp<? extends AbstractPerspective>> ALL_PERSPS = Collections.unmodifiableList(Arrays.asList(
            HOME_PAGE, TIMELINE, PEOPLE, PROJECT_AUTHORING, CONTRIBUTORS, ARTIFACT_REPOSITORY, ADMINISTRATION,
            PROCESS_DEPLOYMENTS, RULE_DEPLOYMENTS, JOBS, PROCESS_DEFINITIONS, PROCESS_INSTANCES, TASKS,
            PROCESS_AND_TASK_DASHBOARD, PLUGIN_MANAGEMENT, APPS, DATA_SETS
    ));

    public static List<Persp<? extends AbstractPerspective>> getAllPerspectives() {
        return ALL_PERSPS;
    }

    private final String parentMenu;
    private final String menuItem;
    private final Class<T> perspPageObjectClass;
    private final boolean isKieWbOnly; // = Perspective is present in kie-wb only, not in kie-drools-wb

    private Persp(String parentMenu, String menuItem, Class<T> perspPageObjectClass) {
        this(parentMenu, menuItem, perspPageObjectClass, false);
    }

    /**
     * @param parentMenu Name of the top level menu in which the menu item to
     * access the perspective is present
     * @param menuItem Name of the item in the menu to navigate to the
     * perspective
     * @param perspPageObjectClass Selenium Page Object class representing given
     * perspective
     * @param isKieWbOnly true if the perspective is present only in kie-wb (not
     * in kie-drools-wb), false otherwise
     */
    private Persp(String parentMenu, String menuItem, Class<T> perspPageObjectClass, boolean isKieWbOnly) {
        this.parentMenu = parentMenu;
        this.menuItem = menuItem;
        this.perspPageObjectClass = perspPageObjectClass;
        this.isKieWbOnly = isKieWbOnly;
    }

    public String getMenu() {
        return parentMenu;
    }

    public String getName() {
        return menuItem;
    }

    public Class<T> getPerspectivePageObjectClass() {
        return perspPageObjectClass;
    }

    public boolean isKieWbOnly() {
        return isKieWbOnly;
    }

    /**
     * @return String used to identify parametrized testcases.
     */
    @Override
    public String toString() {
        return getName().replace(' ', '_');
    }
}
