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

import org.guvnor.rest.client.AddRepositoryToOrganizationalUnitRequest;
import org.guvnor.rest.client.CompileProjectRequest;
import org.guvnor.rest.client.CreateOrCloneRepositoryRequest;
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
import org.guvnor.rest.client.RemoveRepositoryFromOrganizationalUnitRequest;
import org.guvnor.rest.client.RemoveRepositoryRequest;
import org.guvnor.rest.client.RepositoryRequest;
import org.guvnor.rest.client.RepositoryResponse;
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
     * [GET] /repositories
     */
    Collection<RepositoryResponse> getRepositories();

    /**
     * [GET] /repositories/{repositoryName}
     */
    RepositoryResponse getRepository(String repositoryName);

    /**
     * [POST] /repositories
     */
    CreateOrCloneRepositoryRequest createOrCloneRepository(RepositoryRequest repository);

    /**
     * [POST] /repositories
     */
    CreateOrCloneRepositoryRequest createRepository(String orgUnitName, String repositoryName);

    /**
     * [POST] /repositories
     */
    CreateOrCloneRepositoryRequest cloneRepository(String orgUnitName, String repositoryName, String gitRepositoryUrl);

    /**
     * [DELETE] /repositories/{repositoryName}
     */
    RemoveRepositoryRequest deleteRepository(String repositoryName);

    /**
     * [POST] /repositories/{repositoryName}/projects/
     */
    CreateProjectRequest createProject(String repositoryName, ProjectRequest project);

    /**
     * [POST] /repositories/{repositoryName}/projects/
     */
    CreateProjectRequest createProject(String repositoryName, String projectName, String groupId, String version);

    /**
     * [POST] /repositories/{repositoryName}/projects/
     */
    CreateProjectRequest createProject(String repositoryName, String projectName, String groupId, String version, String description);

    /**
     * [DELETE] /repositories/{repositoryName}/projects/{projectName}
     */
    DeleteProjectRequest deleteProject(String repositoryName, String projectName);

    /**
     * [GET] /repositories/{repositoryName}/projects/
     */
    Collection<ProjectResponse> getProjects(String repositoryName);

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
     * [POST] /organizationalunits/{organizationalUnitName}/repositories/{repositoryName}
     */
    AddRepositoryToOrganizationalUnitRequest addRepositoryToOrganizationalUnit(String orgUnitName,
                                                                               String repositoryName);

    /**
     * [DELETE] /organizationalunits/{organizationalUnitName}/repositories/{repositoryName}
     */
    RemoveRepositoryFromOrganizationalUnitRequest removeRepositoryFromOrganizationalUnit(String orgUnitName, String repositoryName);

    /**
     * [POST] /repositories/{repositoryName}/projects/{projectName}/maven/compile
     */
    CompileProjectRequest compileProject(String repositoryName, String projectName);

    /**
     * [POST] /repositories/{repositoryName}/projects/{projectName}/maven/install
     */
    InstallProjectRequest installProject(String repositoryName, String projectName);

    /**
     * [POST] /repositories/{repositoryName}/projects/{projectName}/maven/test
     */
    TestProjectRequest testProject(String repositoryName, String projectName);

    /**
     * [POST] /repositories/{repositoryName}/projects/{projectName}/maven/deploy
     */
    DeployProjectRequest deployProject(String repositoryName, String projectName);

}
