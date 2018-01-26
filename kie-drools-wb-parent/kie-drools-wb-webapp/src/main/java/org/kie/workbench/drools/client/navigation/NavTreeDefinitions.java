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

package org.kie.workbench.drools.client.navigation;

import javax.enterprise.context.ApplicationScoped;

import org.dashbuilder.navigation.NavTree;
import org.dashbuilder.navigation.impl.NavTreeBuilder;
import org.kie.workbench.drools.client.resources.i18n.NavigationConstants;

import static org.dashbuilder.navigation.workbench.NavWorkbenchCtx.perspective;
import static org.kie.workbench.common.workbench.client.PerspectiveIds.DEPLOYMENTS;
import static org.kie.workbench.common.workbench.client.PerspectiveIds.LIBRARY;
import static org.kie.workbench.common.workbench.client.PerspectiveIds.SERVER_MANAGEMENT;

/**
 * Navigation tree definitions such as the workbench menu bar
 */
@ApplicationScoped
public class NavTreeDefinitions {

    public static final String GROUP_WORKBENCH = "wb_group";

    public static final String GROUP_DESIGN = "wb_group_design";
    public static final String ENTRY_PROJECTS = "wb_entry_projects";

    public static final String GROUP_DEPLOY = "wb_group_deploy";
    public static final String ENTRY_DEPLOYMENTS = "wb_entry_deployments";
    public static final String ENTRY_EXECUTION_SERVERS = "wb_entry_execution_servers";

    private NavigationConstants i18n = NavigationConstants.INSTANCE;

    public NavTree buildDefaultNavTree() {
        return new NavTreeBuilder()
                .group(GROUP_WORKBENCH,
                        i18n.navTreeWorkbenchName(),
                        i18n.navTreeWorkbenchDescr(),
                        false)
                .group(GROUP_DESIGN,
                       i18n.navTreeDesignName(),
                       i18n.navTreeDesignDescr(),
                       true)
                .item(ENTRY_PROJECTS,
                      i18n.navTreeProjectsName(),
                      i18n.navTreeProjectsDescr(),
                      true,
                      perspective(LIBRARY))
                .endGroup()
                .group(GROUP_DEPLOY,
                       i18n.navTreeDeployName(),
                       i18n.navTreeDeployDescr(),
                       true)
                .item(ENTRY_DEPLOYMENTS,
                      i18n.navTreeDeploymentsName(),
                      i18n.navTreeDeploymentsDescr(),
                      true,
                      perspective(DEPLOYMENTS))
                .item(ENTRY_EXECUTION_SERVERS,
                      i18n.navTreeExecutionServersName(),
                      i18n.navTreeExecutionServersDescr(),
                      true,
                      perspective(SERVER_MANAGEMENT))
                .endGroup()
                .endGroup()
                .endGroup()
                .build();
    }
}
