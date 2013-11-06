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
package org.kie.workbench.client.security;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class KieWorkbenchFeatures {

    // Top level menu entries plus Home menu sections

    public static final String F_PROJECT_AUTHORING = "wb_project_authoring";
    public static final String F_ARTIFACT_REPO = "wb_artifact_repository";
    public static final String F_ADMINISTRATION = "wb_administration";
    public static final String F_PROCESS_DEFINITIONS = "wb_process_definitions";
    public static final String F_PROCESS_INSTANCES = "wb_process_instances";
    public static final String F_DEPLOYMENTS = "wb_deployments";
    public static final String F_JOBS = "wb_jobs";
    public static final String F_TASKS = "wb_tasks";
    public static final String F_PROCESS_DASHBOARD = "wb_process_dashboard";
    public static final String F_DASHBOARD_BUILDER = "wb_dashboard_builder";
    public static final String F_SEARCH = "wb_search";

    public static final String G_AUTHORING = "wb_authoring";
    public static final String G_DEPLOY = "wb_deploy";
    public static final String G_PROCESS_MANAGEMENT = "wb_process_management";
    public static final String G_TASKS = "wb_task_management";
    public static final String G_DASHBOARDS = "wb_dashboards";
}
