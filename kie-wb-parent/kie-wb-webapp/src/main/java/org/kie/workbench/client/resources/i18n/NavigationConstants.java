/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.client.resources.i18n;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.Messages;

/**
 * This uses GWT to provide client side compile time resolving of locales. See:
 * http://code.google.com/docreader/#p=google-web-toolkit-doc-1-5&s=google-web-
 * toolkit-doc-1-5&t=DevGuideInternationalization (for more information).
 * <p/>
 * Each method name matches up with a key in Constants.properties (the
 * properties file can still be used on the server). To use this, use
 * <code>GWT.create(Constants.class)</code>.
 */
public interface NavigationConstants
        extends
        Messages {

    NavigationConstants INSTANCE = GWT.create( NavigationConstants.class );

    String navTreeRootName();

    String navTreeWorkbenchName();

    String navTreeHomeName();

    String navTreeHomeGroupName();

    String navTreePreferencesName();

    String navTreeTimelineName();

    String navTreePeopleName();

    String navTreeAuthoringName();

    String navTreeProjectAuthoringName();

    String navTreeContributorsName();

    String navTreeArtifactsName();

    String navTreeAdministrationName();

    String navTreeLibraryName();

    String navTreeDeployName();

    String navTreeExecutionServersName();

    String navTreeJobsName();

    String navTreeProcessManagementName();

    String navTreeProcessDefinitionsName();

    String navTreeProcessInstancesName();

    String navTreeTasksName();

    String navTreeTaskAdminName();

    String navTreeDashboardsName();

    String navTreeProcessDashboardName();

    String navTreeBusinessDashboardsName();

    String navTreeExtensionsName();

    String navTreePluginManagementName();

    String navTreeAppsName();

    String navTreeDatasetsName();

    String navTreeDatasourcesName();

    String navTreeRootDescr();

    String navTreeWorkbenchDescr();

    String navTreeHomeGroupDescr();

    String navTreeHomeDescr();

    String navTreePreferencesDescr();

    String navTreeTimelineDescr();

    String navTreePeopleDescr();

    String navTreeAuthoringDescr();

    String navTreeProjectAuthoringDescr();

    String navTreeContributorsDescr();

    String navTreeArtifactsDescr();

    String navTreeAdministrationDescr();

    String navTreeLibraryDescr();

    String navTreeDeployDescr();

    String navTreeExecutionServersDescr();

    String navTreeJobsDescr();

    String navTreeProcessManagementDescr();

    String navTreeProcessDefinitionsDescr();

    String navTreeProcessInstancesDescr();

    String navTreeTasksDescr();

    String navTreeTaskAdminDescr();

    String navTreeDashboardsDescr();

    String navTreeProcessDashboardDescr();

    String navTreeBusinessDashboardsDescr();

    String navTreeExtensionsDescr();

    String navTreePluginManagementDescr();

    String navTreeAppsDescr();

    String navTreeDatasetsDescr();

    String navTreeDatasourcesDescr();
}
