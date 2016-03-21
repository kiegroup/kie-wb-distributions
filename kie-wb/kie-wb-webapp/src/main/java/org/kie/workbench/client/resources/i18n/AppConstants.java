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
public interface AppConstants
        extends
        Messages {

    AppConstants INSTANCE = GWT.create( AppConstants.class );

    String Process_Dashboard();

    String Business_Dashboard();

    String Project_Authoring();

    String Administration();

    String Contributors();

    String Timeline();

    String People();

    String userManagement();
    
    String groupManagement();

    String Authoring();

    String Process_Management();

    String Tasks();

    String artifactRepository();

    String LogOut();

    String Home();

    String Home_Page();

    String Process_Definitions();

    String Process_Instances();

    String Deploy();

    String Rule_Deployments();

    String Process_Deployments();

    String newItem();

    String Search();

    String Tasks_List();

    String Dashboards();

    String find();

    String PlugIns();

    String Extensions();

    String missingDefaultPerspective();

    String explore();

    String repositories();

    String listRepositories();

    String cloneRepository();

    String newRepository();

    String inboxIncomingChanges();

    String inboxRecentlyEdited();

    String inboxRecentlyOpened();

    String tools();

    String User();

    String Role();

    String Jobs();

    String MenuOrganizationalUnits();

    String MenuManageOrganizationalUnits();

    String Repository();

    String Upload();

    String Refresh();

    String Asset_Management();

    String Apps();

    String DataSets();

    String logoBannerError();

    String assetSearch();

    String Examples();

    String Messages();

}
