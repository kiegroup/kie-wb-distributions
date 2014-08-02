/*
 * Copyright 2012 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.kie.workbench.drools.client.security;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class KieWorkbenchFeatures {

    // Top level menu entries plus Home menu sections

    public static final String F_PROJECT_AUTHORING = "wb_project_authoring";
    public static final String F_ARTIFACT_REPO = "wb_artifact_repository";
    public static final String F_ADMINISTRATION = "wb_administration";
    public static final String F_SEARCH = "wb_search";
    public static final String F_MANAGEMENT = "wb_management";

    public static final String G_AUTHORING = "wb_authoring";
    public static final String G_SERVERS = "wb_servers";
}
