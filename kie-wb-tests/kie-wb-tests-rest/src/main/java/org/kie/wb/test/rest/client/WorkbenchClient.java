/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.wb.test.rest.client;

import java.util.Collection;

import org.guvnor.rest.client.AddProjectToOrganizationalUnitRequest;
import org.guvnor.rest.client.CloneRepositoryRequest;
import org.guvnor.rest.client.CompileProjectRequest;
import org.guvnor.rest.client.CreateOrganizationalUnitRequest;
import org.guvnor.rest.client.CreateProjectRequest;
import org.guvnor.rest.client.DeleteProjectRequest;
import org.guvnor.rest.client.DeployProjectRequest;
import org.guvnor.rest.client.InstallProjectRequest;
import org.guvnor.rest.client.JobResult;
import org.guvnor.rest.client.OrganizationalUnit;
import org.guvnor.rest.client.ProjectRequest;
import org.guvnor.rest.client.ProjectResponse;
import org.guvnor.rest.client.RemoveOrganizationalUnitRequest;
import org.guvnor.rest.client.RemoveProjectFromOrganizationalUnitRequest;
import org.guvnor.rest.client.RepositoryRequest;
import org.guvnor.rest.client.TestProjectRequest;
import org.guvnor.rest.client.UpdateOrganizationalUnit;
import org.guvnor.rest.client.UpdateOrganizationalUnitRequest;

public interface WorkbenchClient {

    /**
     * [GET] /jobs/{jobID}
     */
    JobResult getJob(String jobId);

    /**
     * [DELETE] /jobs/{jobID}
     */
    JobResult deleteJob(String jobId);

    /**
     * [POST] /repositories
     */
    CloneRepositoryRequest cloneRepository(String orgUnitName, String repositoryName, String gitRepositoryUrl);

    /**
     * [POST] /repositories
     */
    CloneRepositoryRequest cloneRepository(RepositoryRequest repository);

    /**
     * [GET] /projects
     */
    Collection<ProjectResponse> getProjects();

    /**
     * [GET] /projects/{projectName}
     */
    ProjectResponse getProject(String projectName);

    /**
     * [POST] /organizationalunits/{organizationalUnitName}/projects/
     */
    CreateProjectRequest createProject(String organizationalUnitName, ProjectRequest project);

    /**
     * [POST] /organizationalunits/{organizationalUnitName}/projects/
     */
    CreateProjectRequest createProject(String organizationalUnitName, String projectName, String groupId, String version);

    /**
     * [POST] /organizationalunits/{organizationalUnitName}/projects/
     */
    CreateProjectRequest createProject(String organizationalUnitName, String projectName, String groupId, String version, String description);

    /**
     * [DELETE] /projects/{projectName}
     */
    DeleteProjectRequest deleteProject(String projectName);

    /**
     * [GET] /organizationalunits/{organizationalUnitName}/projects/
     */
    Collection<ProjectResponse> getProjects(String organizationalUnitName);

    /**
     * [GET] /organizationalunits
     */
    Collection<OrganizationalUnit> getOrganizationalUnits();

    /**
     * [POST] /organizationalunits
     */
    CreateOrganizationalUnitRequest createOrganizationalUnit(OrganizationalUnit organizationalUnit);

    /**
     * [POST] /organizationalunits
     */
    CreateOrganizationalUnitRequest createOrganizationalUnit(String orgUnitName, String owner);

    /**
     * [POST] /organizationalunits
     */
    CreateOrganizationalUnitRequest createOrganizationalUnit(String orgUnitName, String owner, String description);

    /**
     * [POST] /organizationalunits
     */
    CreateOrganizationalUnitRequest createOrganizationalUnit(String orgUnitName, String owner, String description, String groupId);

    /**
     * [GET] /organizationalunits/{orgUnitName}
     */
    OrganizationalUnit getOrganizationalUnit(String orgUnitName);

    /**
     * [POST] /organizationalunits/{orgUnitName}
     */
    UpdateOrganizationalUnitRequest updateOrganizationalUnit(String name, UpdateOrganizationalUnit organizationalUnit);

    /**
     * [DELETE] /organizationalunits/{organizationalUnitName}
     */
    RemoveOrganizationalUnitRequest deleteOrganizationalUnit(String orgUnitName);

    /**
     * [POST] /organizationalunits/{organizationalUnitName}/projects/{projectName}
     */
    AddProjectToOrganizationalUnitRequest addProjectToOrganizationalUnit(String orgUnitName,
                                                                            String projectName);

    /**
     * [DELETE] /organizationalunits/{organizationalUnitName}/projects/{projectName}
     */
    RemoveProjectFromOrganizationalUnitRequest removeProjectFromOrganizationalUnit(String orgUnitName, String projectName);

    /**
     * [POST] /projects/{projectName}/maven/compile
     */
    CompileProjectRequest compileProject(String projectName);

    /**
     * [POST] /organizationalunits/{organizationalUnitName}/projects/{projectName}/maven/install
     */
    InstallProjectRequest installProject(String organizationalUnitName, String projectName);

    /**
     * [POST] /projects/{projectName}/maven/test
     */
    TestProjectRequest testProject(String projectName);

    /**
     * [POST] /projects/{projectName}/maven/deploy
     */
    DeployProjectRequest deployProject(String projectName);
}
