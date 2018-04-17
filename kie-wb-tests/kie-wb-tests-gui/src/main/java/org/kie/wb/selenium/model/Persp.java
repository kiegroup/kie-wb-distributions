/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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
package org.kie.wb.selenium.model;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.kie.wb.selenium.model.persps.AbstractPerspective;
import org.kie.wb.selenium.model.persps.AdminPagePerspective;
import org.kie.wb.selenium.model.persps.ArtifactRepositoryPerspective;
import org.kie.wb.selenium.model.persps.ContentManagerPerspective;
import org.kie.wb.selenium.model.persps.ExecutionErrorsPerspective;
import org.kie.wb.selenium.model.persps.ExecutionServersPerspective;
import org.kie.wb.selenium.model.persps.HomePerspective;
import org.kie.wb.selenium.model.persps.JobsPerspective;
import org.kie.wb.selenium.model.persps.ProcessDashboardPerspective;
import org.kie.wb.selenium.model.persps.ProcessDefinitionsPerspective;
import org.kie.wb.selenium.model.persps.ProcessInstancesPerspective;
import org.kie.wb.selenium.model.persps.ProjectLibraryPerspective;
import org.kie.wb.selenium.model.persps.ProvisioningManagementPerspective;
import org.kie.wb.selenium.model.persps.TaskDashboardPerspective;
import org.kie.wb.selenium.model.persps.TasksPerspective;
import org.kie.wb.selenium.model.persps.TaskAdminPerspective;

import static org.kie.wb.selenium.model.KieWbDistribution.KIE_DROOLS_WB;
import static org.kie.wb.selenium.model.KieWbDistribution.KIE_WB;
import static org.kie.wb.selenium.model.KieWbDistribution.KIE_WB_MONITORING;

public class Persp<T extends AbstractPerspective> {

    public static final Persp<HomePerspective> HOME
            = new Persp<>("N/A",
                          "Home",
                          HomePerspective.class);

    public static final Persp<AdminPagePerspective> ADMIN
            = new Persp<>("N/A",
                          "Admin",
                          AdminPagePerspective.class);

    public static final Persp<ArtifactRepositoryPerspective> ARTIFACTS
            = new Persp<>("N/A",
                          "Artifacts",
                          ArtifactRepositoryPerspective.class);

    public static final Persp<ProjectLibraryPerspective> PROJECTS
            = new Persp<>("Design",
                          "Projects",
                          ProjectLibraryPerspective.class,
                          KIE_DROOLS_WB,
                          KIE_WB);
    public static final Persp<ContentManagerPerspective> PAGES
            = new Persp<>("Design",
                          "Pages",
                          ContentManagerPerspective.class,
                          KIE_WB);

    public static final Persp<ProvisioningManagementPerspective> PROVISIONING
            = new Persp<>("Deploy",
                          "Provisioning",
                          ProvisioningManagementPerspective.class);

    public static final Persp<ExecutionServersPerspective> EXECUTION_SERVERS
            = new Persp<>("Deploy",
                          "Execution Servers",
                          ExecutionServersPerspective.class);

    public static final Persp<ProcessDefinitionsPerspective> PROCESS_DEFINITIONS
            = new Persp<>("Manage",
                          "Process Definitions",
                          ProcessDefinitionsPerspective.class,
                          KIE_WB,
                          KIE_WB_MONITORING);
    public static final Persp<ProcessInstancesPerspective> PROCESS_INSTANCES
            = new Persp<>("Manage",
                          "Process Instances",
                          ProcessInstancesPerspective.class,
                          KIE_WB,
                          KIE_WB_MONITORING);

    public static final Persp<TaskAdminPerspective> TASKS
            = new Persp<>("Manage",
                          "Tasks",
                          TaskAdminPerspective.class,
                          KIE_WB,
                          KIE_WB_MONITORING);

    public static final Persp<JobsPerspective> JOBS
            = new Persp<>("Manage",
                          "Jobs",
                          JobsPerspective.class,
                          KIE_WB,
                          KIE_WB_MONITORING);

    public static final Persp<ExecutionErrorsPerspective> EXECUTION_ERRORS
            = new Persp<>("Manage",
                          "Execution Errors",
                          ExecutionErrorsPerspective.class,
                          KIE_WB,
                          KIE_WB_MONITORING);

    public static final Persp<TasksPerspective> TASK_INBOX
            = new Persp<>("Track",
                          "Task Inbox",
                          TasksPerspective.class,
                          KIE_WB,
                          KIE_WB_MONITORING);

    public static final Persp<ProcessDashboardPerspective> PROCESS_REPORTS
            = new Persp<>("Track",
                          "Process Reports",
                          ProcessDashboardPerspective.class,
                          KIE_WB,
                          KIE_WB_MONITORING);

    public static final Persp<TaskDashboardPerspective> TASK_REPORTS
            = new Persp<>("Track",
                          "Task Reports",
                          TaskDashboardPerspective.class,
                          KIE_WB,
                          KIE_WB_MONITORING);

    private static final List<Persp<? extends AbstractPerspective>> ALL_PERSPECTIVES = Collections.unmodifiableList(Arrays.asList(
            ADMIN,
            HOME,
            PROJECTS,
            PAGES,
            PROVISIONING,
            EXECUTION_SERVERS,
            PROCESS_DEFINITIONS,
            PROCESS_INSTANCES,
            TASKS,
            JOBS,
            EXECUTION_ERRORS,
            TASK_INBOX,
            PROCESS_REPORTS,
            TASK_REPORTS
    ));
    private final String parentMenu;
    private final String menuItem;
    private final Class<T> perspPageObjectClass;
    private final List<KieWbDistribution> kieWbDistributions;

    private Persp(String parentMenu,
                  String menuItem,
                  Class<T> perspPageObjectClass) {
        this(parentMenu,
             menuItem,
             perspPageObjectClass,
             KIE_DROOLS_WB,
             KIE_WB,
             KIE_WB_MONITORING);
    }

    /**
     * @param parentMenu Name of the top level menu in which the menu item to
     * access the perspective is present
     * @param menuItem Name of the item in the menu to navigate to the
     * perspective
     * @param perspPageObjectClass Selenium Page Object class representing given
     * perspective
     * @param distributions Distributions where perspective is present
     */
    private Persp(String parentMenu,
                  String menuItem,
                  Class<T> perspPageObjectClass,
                  KieWbDistribution... distributions) {
        this.parentMenu = parentMenu;
        this.menuItem = menuItem;
        this.perspPageObjectClass = perspPageObjectClass;
        this.kieWbDistributions = Arrays.asList(distributions);
    }

    public static List<Persp<? extends AbstractPerspective>> getAllPerspectives(final KieWbDistribution distribution) {
        return ALL_PERSPECTIVES.stream().filter(perspective -> perspective.getKieWbDistributions().contains(distribution)).collect(Collectors.toList());
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

    public List<KieWbDistribution> getKieWbDistributions() {
        return kieWbDistributions;
    }

    /**
     * @return String used to identify parametrized testcases.
     */
    @Override
    public String toString() {
        return getName().replace(' ',
                                 '_');
    }
}
