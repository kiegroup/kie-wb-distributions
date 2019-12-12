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

import javax.ws.rs.core.Response;

import org.guvnor.rest.client.CloneProjectJobRequest;
import org.guvnor.rest.client.AddBranchJobRequest;
import org.guvnor.rest.client.CloneProjectRequest;
import org.guvnor.rest.client.CompileProjectRequest;
import org.guvnor.rest.client.CreateProjectJobRequest;
import org.guvnor.rest.client.CreateProjectRequest;
import org.guvnor.rest.client.DeleteProjectRequest;
import org.guvnor.rest.client.DeployProjectRequest;
import org.guvnor.rest.client.InstallProjectRequest;
import org.guvnor.rest.client.AddBranchRequest;
import org.guvnor.rest.client.RemoveBranchJobRequest;
import org.guvnor.rest.client.JobResult;
import org.guvnor.rest.client.ProjectResponse;
import org.guvnor.rest.client.BranchResponse;
import org.guvnor.rest.client.RemoveSpaceRequest;
import org.guvnor.rest.client.Space;
import org.guvnor.rest.client.SpaceRequest;
import org.guvnor.rest.client.TestProjectRequest;
import org.kie.workbench.common.screens.library.api.SpacesScreenService;

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
     * [GET] /spaces/{spaceName}/project/{projectName}
     */
    ProjectResponse getProject(String spaceName, String projectName);

    /**
     * [POST] /spaces/{spaceName}/git/clone
     */
    CloneProjectJobRequest cloneRepository(String spaceName, CloneProjectRequest cloneProjectRequest);

    /**
     * [POST] /spaces/{spaceName}/projects/
     */
    CreateProjectJobRequest createProject(String spaceName, CreateProjectRequest project);

    /**
     * [POST] /spaces/{spaceName}/projects/
     */
    CreateProjectJobRequest createProject(String spaceName, String projectName, String groupId, String version);

    /**
     * [POST] /spaces/{spaceName}/projects/
     */
    CreateProjectJobRequest createProject(String spaceName, String projectName, String groupId, String version, String description);

    /**
     * [DELETE] /spaces/{spaceName}/projects/{projectName}
     */
    DeleteProjectRequest deleteProject(String spaceName, String projectName);

    /**
     * [GET] /spaces/{spaceName}/projects/
     */
    Collection<ProjectResponse> getProjects(String spaceName);

    /**
     * [GET] /spaces
     */
    Collection<Space> getSpaces();

    /**
     * [GET] /ready
     */
    String isReady();

    /**
     * [GET] /healthy
     */
    String isHealthy();

    /**
     * [POST] /spaces
     */
    SpaceRequest createSpace(Space space);

    /**
     * [POST] /spaces
     */
    SpaceRequest createSpace(String spaceName, String owner);

    /**
     * [POST] /spaces
     */
    SpaceRequest createSpace(String spaceName, String owner, String description);

    /**
     * [POST] /spaces
     */
    SpaceRequest createSpace(String spaceName, String owner, String description, String groupId);

    /**
     * [GET] /spaces/{spaceName}
     */
    Space getSpace(String orgUnitName);

    /**
     * [DELETE] /spaces/{spaceName}
     */
    RemoveSpaceRequest deleteSpace(String orgUnitName);

    /**
     * [POST] /spaces/{spaceName}/projects/{projectName}/maven/compile
     */
    CompileProjectRequest compileProject(String spaceName, String projectName);

    /**
     * [POST] /spaces/{spaceName}/projects/{projectName}/branches/{branchName}/maven/compile
     */
    CompileProjectRequest compileProject(String spaceName, String projectName, String branchName);

    /**
     * [POST] /spaces/{spaceName}/projects/{projectName}/maven/install
     */
    InstallProjectRequest installProject(String spaceName, String projectName);

    /**
     * [POST] /spaces/{spaceName}/projects/{projectName}/branches/{branchName}/maven/install
     */
    InstallProjectRequest installProject(String spaceName, String projectName, String branchName);

    /**
     * [POST] /spaces/{spaceName}/projects/{projectName}/maven/test
     */
    TestProjectRequest testProject(String spaceName, String projectName);

    /**
     * [POST] /spaces/{spaceName}/projects/{projectName}/branches/{branchName}/maven/test
     */
    TestProjectRequest testProject(String spaceName, String projectName, String branchName);

    /**
     * [POST] /spaces/{spaceName}/projects/{projectName}/maven/deploy
     */
    DeployProjectRequest deployProject(String spaceName, String projectName);

    /**
     * [POST] /spaces/{spaceName}/projects/{projectName}/branches/{branchName}/maven/deploy
     */
    DeployProjectRequest deployProject(String spaceName, String projectName, String branchName);

    /**
     * [GET] /spacesScreen/spaces
     */
    Response spacesScreen_getSpaces();

    /**
     * [PUT] /spacesScreen/libraryPreference
     */
    Response spacesScreen_savePreference(SpacesScreenLibraryPreference preference);

    /**
     * [GET] /spacesScreen/space/{name}
     */
    Response spacesScreen_getSpace(String name);

    /**
     * [GET] /spacesScreen/validGroupId
     */
    boolean spacesScreen_isValidGroupId(String groupId);

    /**
     * [POST] /spacesScreen/spaces
     */
    Response spacesScreen_postSpace(SpacesScreenService.NewSpace newSpace);

    /**
     * [GET] /spaces/{spaceName}/projects/{projectName}/branches
     */
    Collection<BranchResponse> getBranches(String spaceName, String projectName);

    /**
     * [POST] /spaces/{spaceName}/projects/{projectName}/branches
     */
    AddBranchJobRequest addBranch(String spaceName, String projectName, AddBranchRequest addBranchRequest);

    /**
     * [DELETE] /spaces/{spaceName}/projects/{projectName}/branches/{branchName}
     */
    RemoveBranchJobRequest removeBranch(String spaceName, String projectName, String branchName);
}
