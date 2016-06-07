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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import javax.ws.rs.core.MediaType;

import org.guvnor.rest.client.AddRepositoryToOrganizationalUnitRequest;
import org.guvnor.rest.client.CompileProjectRequest;
import org.guvnor.rest.client.CreateOrCloneRepositoryRequest;
import org.guvnor.rest.client.CreateOrganizationalUnitRequest;
import org.guvnor.rest.client.CreateProjectRequest;
import org.guvnor.rest.client.DeleteProjectRequest;
import org.guvnor.rest.client.DeployProjectRequest;
import org.guvnor.rest.client.InstallProjectRequest;
import org.guvnor.rest.client.JobRequest;
import org.guvnor.rest.client.JobResult;
import org.guvnor.rest.client.JobStatus;
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
import org.kie.wb.test.rest.util.HttpRequestFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RestWorkbenchClient implements WorkbenchClient {

    private static final Logger log = LoggerFactory.getLogger(RestWorkbenchClient.class);

    private static final int JOB_TIMEOUT_SECONDS = 10;
    private static final int PROJECT_JOB_TIMEOUT_SECONDS = 30;
    private static final int CLONE_REPO_TIMEOUT_SECONDS = 60;

    private static final MediaType DEFAULT_CONTENT_TYPE = MediaType.APPLICATION_JSON_TYPE;

    private final HttpRequestFactory http;

    private final boolean async;

    private RestWorkbenchClient(String appUrl, String userId, String password, boolean async) {
        http = new HttpRequestFactory(appUrl + "/rest/", userId, password, DEFAULT_CONTENT_TYPE);
        this.async = async;
    }

    /**
     * Creates KIE Workbench REST client which will execute each operation asynchronously. The status of the operation
     * can be checked by retrieving the details about the job with ID provided in the request.
     */
    public static WorkbenchClient createAsyncWorkbenchClient(String appUrl, String userId, String password) {
        return new RestWorkbenchClient(appUrl, userId, password, true);
    }

    /**
     * Creates KIE Workbench REST client which will wait for completion of each operation.
     */
    public static WorkbenchClient createWorkbenchClient(String appUrl, String userId, String password) {
        return new RestWorkbenchClient(appUrl, userId, password, false);
    }

    @Override
    public JobResult getJob(String jobId) {
        log.info("Getting job '{}'", jobId);

        return http.request("jobs/" + jobId, JobResult.class).get();
    }

    @Override
    public JobResult deleteJob(String jobId) {
        log.info("Deleting job '{}'", jobId);

        return http.request("jobs/" + jobId, JobResult.class).delete();
    }

    @Override
    public Collection<RepositoryResponse> getRepositories() {
        log.info("Getting all repositories");

        Collection<Map<String, String>> result = http.request("repositories", Collection.class).get();

        Collection<RepositoryResponse> repositories = new ArrayList<>();
        for (Map<String, String> map : result) {
            RepositoryResponse repository = new RepositoryResponse();
            repository.setName(map.get("name"));
            repository.setDescription(map.get("description"));
            repository.setUserName(map.get("userName"));
            repository.setPassword(map.get("password"));
            repository.setRequestType(map.get("requestType"));
            repository.setGitURL(map.get("gitURL"));
            repositories.add(repository);
        }

        return repositories;
    }

    @Override
    public RepositoryResponse getRepository(String repositoryName) {
        log.info("Getting repository '{}'", repositoryName);

        return http.request("repositories/" + repositoryName, RepositoryResponse.class).get();
    }

    @Override
    public CreateOrCloneRepositoryRequest createOrCloneRepository(RepositoryRequest repository) {
        log.info("Creating new repository '{}'", repository.getName());

        CreateOrCloneRepositoryRequest request = http.request("repositories", CreateOrCloneRepositoryRequest.class)
                .body(repository).post();

        if (request.getRepository().getRequestType().equals("clone")) {
            return waitUntilJobFinished(request, CLONE_REPO_TIMEOUT_SECONDS);
        } else {
            return waitUntilJobFinished(request);
        }
    }

    @Override
    public RemoveRepositoryRequest deleteRepository(String repositoryName) {
        log.info("Deleting repository '{}'", repositoryName);

        RemoveRepositoryRequest request = http.request("repositories/" + repositoryName, RemoveRepositoryRequest.class)
                .delete();

        return waitUntilJobFinished(request);
    }

    @Override
    public CreateProjectRequest createProject(String repositoryName, ProjectRequest project) {
        log.info("Creating project '{}' in repository '{}'", project.getName(), repositoryName);

        CreateProjectRequest request = http.request("repositories/" + repositoryName + "/projects",
                CreateProjectRequest.class).body(project).post();

        return waitUntilJobFinished(request, PROJECT_JOB_TIMEOUT_SECONDS);
    }

    @Override
    public DeleteProjectRequest deleteProject(String repositoryName, String projectName) {
        log.info("Removing project '{}' from repository '{}'", projectName, repositoryName);

        DeleteProjectRequest request = http.request("repositories/" + repositoryName + "/projects/" + projectName,
                DeleteProjectRequest.class).delete();

        return waitUntilJobFinished(request, PROJECT_JOB_TIMEOUT_SECONDS);
    }

    @Override
    public Collection<ProjectResponse> getProjects(String repositoryName) {
        log.info("Retrieving all projects from repository '{}'", repositoryName);

        Collection<Map<String, String>> result = http.request("repositories/" + repositoryName + "/projects",
                Collection.class).get();

        Collection<ProjectResponse> projects = new ArrayList<>();
        for (Map<String, String> map : result) {
            ProjectResponse project = new ProjectResponse();
            project.setName(map.get("name"));
            project.setDescription(map.get("description"));
            project.setGroupId(map.get("groupId"));
            project.setVersion(map.get("version"));
            projects.add(project);
        }
        return projects;
    }

    @Override
    public Collection<OrganizationalUnit> getOrganizationalUnits() {
        log.info("Getting all organizational units");

        Collection<Map<String, Object>> result = http.request("organizationalunits", Collection.class).get();

        Collection<OrganizationalUnit> orgUnits = new ArrayList<>();
        for (Map<String, Object> map : result) {
            OrganizationalUnit orgUnit = new OrganizationalUnit();
            orgUnit.setName((String) map.get("name"));
            orgUnit.setDescription((String) map.get("description"));
            orgUnit.setOwner((String) map.get("owner"));
            orgUnit.setDefaultGroupId((String) map.get("defaultGroupId"));
            orgUnit.setRepositories((List<String>) map.get("repositories"));
            orgUnits.add(orgUnit);
        }
        return orgUnits;
    }

    @Override
    public CreateOrganizationalUnitRequest createOrganizationalUnit(OrganizationalUnit orgUnit) {
        log.info("Creating organizational unit '{}' ", orgUnit.getName());

        CreateOrganizationalUnitRequest request = http.request("organizationalunits",
                CreateOrganizationalUnitRequest.class).body(orgUnit).post();

        return waitUntilJobFinished(request);
    }

    @Override
    public OrganizationalUnit getOrganizationalUnit(String name) {
        log.info("Getting organizational unit '{}'", name);

        return http.request("organizationalunits/" + name, OrganizationalUnit.class).get();
    }

    @Override
    public UpdateOrganizationalUnitRequest updateOrganizationalUnit(String name, UpdateOrganizationalUnit orgUnit) {
        log.info("Updating organizational unit '{}'", name);

        UpdateOrganizationalUnitRequest request = http.request("organizationalunits/" + name,
                UpdateOrganizationalUnitRequest.class).body(orgUnit).post();

        return waitUntilJobFinished(request);
    }

    @Override
    public RemoveOrganizationalUnitRequest deleteOrganizationalUnit(String name) {
        log.info("Deleting organizational unit '{}'", name);

        RemoveOrganizationalUnitRequest request = http.request("organizationalunits/" + name,
                RemoveOrganizationalUnitRequest.class).delete();

        return waitUntilJobFinished(request);
    }

    @Override
    public AddRepositoryToOrganizationalUnitRequest addRepositoryToOrganizationalUnit(String orgUnitName,
                                                                                      String repositoryName) {
        log.info("Adding repository '{}' to organizational unit '{}'", repositoryName, orgUnitName);

        AddRepositoryToOrganizationalUnitRequest request = http.request("organizationalunits/" + orgUnitName +
                "/repositories/" + repositoryName, AddRepositoryToOrganizationalUnitRequest.class).post();

        return waitUntilJobFinished(request);
    }

    @Override
    public RemoveRepositoryFromOrganizationalUnitRequest removeRepositoryFromOrganizationalUnit(String orgUnitName,
                                                                                                String repositoryName) {
        log.info("Removing repository '{}' from organizational unit '{}'", repositoryName, orgUnitName);

        RemoveRepositoryFromOrganizationalUnitRequest request = http.request("organizationalunits/" + orgUnitName +
                "/repositories/" + repositoryName, RemoveRepositoryFromOrganizationalUnitRequest.class).delete();

        return waitUntilJobFinished(request);
    }

    @Override
    public CompileProjectRequest compileProject(String repositoryName, String projectName) {
        log.info("Compiling project '{}' from repository '{}'", projectName, repositoryName);

        CompileProjectRequest request = http.request("repositories/" + repositoryName + "/projects/" + projectName +
                "/maven/compile", CompileProjectRequest.class).post();

        return waitUntilJobFinished(request, PROJECT_JOB_TIMEOUT_SECONDS);
    }

    @Override
    public InstallProjectRequest installProject(String repositoryName, String projectName) {
        log.info("Installing project '{}' from repository '{}'", projectName, repositoryName);

        InstallProjectRequest request = http.request("repositories/" + repositoryName + "/projects/" + projectName +
                "/maven/install", InstallProjectRequest.class).post();

        return waitUntilJobFinished(request, PROJECT_JOB_TIMEOUT_SECONDS);
    }

    @Override
    public TestProjectRequest testProject(String repositoryName, String projectName) {
        log.info("Testing project '{}' from repository '{}'", projectName, repositoryName);

        TestProjectRequest request = http.request("repositories/" + repositoryName + "/projects/" + projectName +
                "/maven/test", TestProjectRequest.class).post();

        return waitUntilJobFinished(request, PROJECT_JOB_TIMEOUT_SECONDS);
    }

    @Override
    public DeployProjectRequest deployProject(String repositoryName, String projectName) {
        log.info("Deploying project '{}' from repository '{}'", projectName, repositoryName);

        DeployProjectRequest request = http.request("repositories/" + repositoryName + "/projects/" + projectName +
                "/maven/deploy", DeployProjectRequest.class).post();

        return waitUntilJobFinished(request, PROJECT_JOB_TIMEOUT_SECONDS);
    }

    private <T extends JobRequest> T waitUntilJobFinished(T request) {
        return waitUntilJobFinished(request, JOB_TIMEOUT_SECONDS);
    }

    private <T extends JobRequest> T waitUntilJobFinished(T request, int seconds) {
        if (async) {
            return request;
        }

        JobResult jobResult;
        while ((jobResult = getJob(request.getJobId())).getStatus() != JobStatus.SUCCESS && seconds-- > 0) {
            switch (jobResult.getStatus()) {
                case ACCEPTED:
                case APPROVED:
                    sleepForSecond();
                    break;
                case SUCCESS:
                    return request;
                default:
                    throw new NotSuccessException(jobResult);
            }
        }

        if (jobResult.getStatus() != JobStatus.SUCCESS) {
            throw new NotSuccessException(jobResult);
        }

        return request;
    }

    private void sleepForSecond() {
        try {
            Thread.sleep(1000); // TODO use something else
        } catch (InterruptedException ex) {
            // continue
        }
    }

}
