/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.workbench.client.navigation;

import javax.enterprise.context.ApplicationScoped;

import org.dashbuilder.navigation.NavTree;
import org.dashbuilder.navigation.impl.NavTreeBuilder;
import org.kie.workbench.client.resources.i18n.NavigationConstants;

import static org.dashbuilder.navigation.workbench.NavWorkbenchCtx.*;
import static org.kie.workbench.common.workbench.client.PerspectiveIds.*;

/**
 * Navigation tree definitions such as the workbench menu bar
 */
@ApplicationScoped
public class NavTreeDefinitions {

    public static final String GROUP_ROOT = "root";
    public static final String GROUP_WORKBENCH = "wb_group";

    public static final String GROUP_HOME = "wb_group_home";
    public static final String ENTRY_HOME = "wb_entry_home";
    public static final String ENTRY_PREFERENCES = "wb_entry_preferences";
    public static final String ENTRY_TIMELINE = "wb_entry_timeline";
    public static final String ENTRY_PEOPLE = "wb_entry_people";

    public static final String GROUP_AUTHORING = "wb_group_authoring";
    public static final String ENTRY_PROJECT_AUTHORING = "wb_entry_project_authoring";
    public static final String ENTRY_CONTRIBUTORS = "wb_entry_contributors";
    public static final String ENTRY_ARTIFACTS = "wb_entry_artifacts";
    public static final String ENTRY_ADMINISTRATION = "wb_entry_administration";
    public static final String ENTRY_LIBRARY = "wb_entry_library";

    public static final String GROUP_DEPLOY = "wb_group_deploy";
    public static final String ENTRY_EXECUTION_SERVERS = "wb_entry_execution_servers";
    public static final String ENTRY_JOBS = "wb_entry_jobs";

    public static final String GROUP_PROCESS_MANAGEMENT = "wb_group_process_management";
    public static final String ENTRY_PROCESS_DEFINITIONS = "wb_entry_process_definitions";
    public static final String ENTRY_PROCESS_INSTANCES = "wb_entry_process_instances";

    public static final String ENTRY_TASKS = "wb_entry_tasks";

    public static final String GROUP_DASHBOARDS = "wb_group_dashboards";
    public static final String ENTRY_PROCESS_DASHBOARD = "wb_entry_process_dashboard";
    public static final String ENTRY_BUSINESS_DASHBOARDS = "wb_entry_business_dashboards";

    public static final String GROUP_EXTENSIONS = "wb_group_extensions";
    public static final String ENTRY_PLUGIN_MANAGEMENT = "wb_entry_plugin_management";
    public static final String ENTRY_APPS = "wb_entry_apps";
    public static final String ENTRY_DATASETS = "wb_entry_datasets";
    public static final String ENTRY_DATA_SOURCES = "wb_entry_data_sources";

    private NavigationConstants i18n = NavigationConstants.INSTANCE;

    public NavTree buildDefaultNavTree(boolean socialEnabled) {
        NavTreeBuilder builder = new NavTreeBuilder()
            .group(GROUP_ROOT, i18n.navTreeRootName(), i18n.navTreeRootDescr(), false)
                .group(GROUP_WORKBENCH, i18n.navTreeWorkbenchName(), i18n.navTreeWorkbenchDescr(), false)
                    .group(GROUP_HOME, i18n.navTreeHomeGroupName(), i18n.navTreeHomeGroupDescr(), true)
                        .item(ENTRY_HOME, i18n.navTreeHomeName(), i18n.navTreeHomeDescr(), true, perspective(HOME))
                        .item(ENTRY_PREFERENCES, i18n.navTreePreferencesName(), i18n.navTreePreferencesDescr(), true, perspective(ADMIN));

        if (socialEnabled) {
            builder.item(ENTRY_TIMELINE, i18n.navTreeTimelineName(), i18n.navTreeTimelineDescr(), true, perspective(SOCIAL_HOME));
            builder.item(ENTRY_PEOPLE, i18n.navTreePeopleName(), i18n.navTreePeopleDescr(), true, perspective(SOCIAL_USER_HOME));
        }

        return builder.endGroup()
            .group(GROUP_AUTHORING, i18n.navTreeAuthoringName(), i18n.navTreeAuthoringDescr(), true)
                .item(ENTRY_PROJECT_AUTHORING, i18n.navTreeProjectAuthoringName(), i18n.navTreeProjectAuthoringDescr(), true, perspective(AUTHORING))
                .item(ENTRY_CONTRIBUTORS, i18n.navTreeContributorsName(), i18n.navTreeContributorsDescr(), true, perspective(CONTRIBUTORS))
                .item(ENTRY_ARTIFACTS, i18n.navTreeArtifactsName(), i18n.navTreeArtifactsDescr(), true, perspective(GUVNOR_M2REPO))
                .item(ENTRY_ADMINISTRATION, i18n.navTreeAdministrationName(), i18n.navTreeAdministrationDescr(), true, perspective(ADMINISTRATION))
                .item(ENTRY_LIBRARY, i18n.navTreeLibraryName(), i18n.navTreeLibraryDescr(), true, perspective(LIBRARY))
                .endGroup()
            .group(GROUP_DEPLOY, i18n.navTreeDeployName(), i18n.navTreeDeployDescr(), true)
                .item(ENTRY_EXECUTION_SERVERS, i18n.navTreeExecutionServersName(), i18n.navTreeExecutionServersDescr(), true, perspective(SERVER_MANAGEMENT))
                .item(ENTRY_JOBS, i18n.navTreeJobsName(), i18n.navTreeJobsDescr(), true, perspective(JOBS))
                .endGroup()
            .group(GROUP_PROCESS_MANAGEMENT, i18n.navTreeProcessManagementName(), i18n.navTreeProcessManagementDescr(), true)
                .item(ENTRY_PROCESS_DEFINITIONS, i18n.navTreeProcessDefinitionsName(), i18n.navTreeProcessDefinitionsDescr(), true, perspective(PROCESS_DEFINITIONS))
                .item(ENTRY_PROCESS_INSTANCES, i18n.navTreeProcessInstancesName(), i18n.navTreeProcessInstancesDescr(), true, perspective(PROCESS_INSTANCES))
                .endGroup()
            .item(ENTRY_TASKS, i18n.navTreeTasksName(), i18n.navTreeTasksDescr(), true, perspective(TASKS))
            .group(GROUP_DASHBOARDS, i18n.navTreeDashboardsName(), i18n.navTreeDashboardsDescr(), true)
                .item(ENTRY_PROCESS_DASHBOARD, i18n.navTreeProcessDashboardName(), i18n.navTreeProcessDashboardDescr(), true, perspective(PROCESS_DASHBOARD))
                .item(ENTRY_BUSINESS_DASHBOARDS, i18n.navTreeBusinessDashboardsName(), i18n.navTreeBusinessDashboardsDescr(), true, perspective(BUSINESS_DASHBOARDS))
                .endGroup()
            .group(GROUP_EXTENSIONS, i18n.navTreeExtensionsName(), i18n.navTreeExtensionsDescr(), true)
                .item(ENTRY_PLUGIN_MANAGEMENT, i18n.navTreePluginManagementName(), i18n.navTreePluginManagementDescr(), true, perspective(PLUGIN_AUTHORING))
                .item(ENTRY_APPS, i18n.navTreeAppsName(), i18n.navTreeAppsDescr(), true, perspective(APPS))
                .item(ENTRY_DATASETS, i18n.navTreeDatasetsName(), i18n.navTreeDatasetsDescr(), true, perspective(DATASET_AUTHORING))
                .item(ENTRY_DATA_SOURCES, i18n.navTreeDatasourcesName(), i18n.navTreeDatasourcesDescr(), true, perspective(DATASOURCE_MANAGEMENT))
                .endGroup()
        .build();
    }
}
