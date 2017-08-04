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
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;

import org.guvnor.rest.client.AddProjectToOrganizationalUnitRequest;
import org.guvnor.rest.client.CloneRepositoryRequest;
import org.guvnor.rest.client.CompileProjectRequest;
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
import org.guvnor.rest.client.RemoveProjectFromOrganizationalUnitRequest;
import org.guvnor.rest.client.RepositoryRequest;
import org.guvnor.rest.client.TestProjectRequest;
import org.guvnor.rest.client.UpdateOrganizationalUnit;
import org.guvnor.rest.client.UpdateOrganizationalUnitRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RestWorkbenchClient implements WorkbenchClient {

    private static final Logger log = LoggerFactory.getLogger(RestWorkbenchClient.class);

    private static final int DEFAULT_JOB_TIMEOUT_SECONDS = 10;
    private static final int DEFAULT_PROJECT_JOB_TIMEOUT_SECONDS = 60;
    private static final int DEFAULT_CLONE_REPO_TIMEOUT_SECONDS = 60;
    private final int jobTimeoutSeconds;
    private final int projectJobTimeoutSeconds;
    private final int cloneRepoTimeoutSeconds;

    private static final MediaType MEDIA_TYPE = MediaType.APPLICATION_JSON_TYPE;

    private final boolean async;

    private final WebTarget target;

    private RestWorkbenchClient(String appUrl, String userId, String password, boolean async,
                                int jobTimeoutSeconds, int projectJobTimeoutSeconds, int cloneRepoTimeoutSeconds) {
        this.async = async;

        Client client = ClientBuilder.newClient().register(new Authenticator(userId, password));
        target = client.target(appUrl).path("rest");

        this.jobTimeoutSeconds = jobTimeoutSeconds;
        this.projectJobTimeoutSeconds = projectJobTimeoutSeconds;
        this.cloneRepoTimeoutSeconds = cloneRepoTimeoutSeconds;
    }

    private RestWorkbenchClient(String appUrl, String userId, String password, boolean async) {
        this(appUrl, userId, password, async,
             DEFAULT_JOB_TIMEOUT_SECONDS, DEFAULT_PROJECT_JOB_TIMEOUT_SECONDS, DEFAULT_CLONE_REPO_TIMEOUT_SECONDS
        );
    }

    /**
     * Creates KIE Workbench REST client which will execute each operation asynchronously. The status of the operation
     * can be checked by retrieving the details about the job with ID provided in the request.
     */
    public static WorkbenchClient createAsyncWorkbenchClient(String appUrl, String userId, String password) {
        return new RestWorkbenchClient(appUrl, userId, password, true);
    }

    /**
     * Creates KIE Workbench REST client which will wait for successful completion of each operation.
     */
    public static WorkbenchClient createWorkbenchClient(String appUrl, String userId, String password) {
        return new RestWorkbenchClient(appUrl, userId, password, false);
    }

    /**
     * Creates KIE Workbench REST client which will wait for successful completion of each operation using specified timeouts.
     */
    public static WorkbenchClient createWorkbenchClient(String appUrl, String userId, String password,
                                                        int jobTimeoutSeconds, int projectJobTimeoutSeconds, int cloneRepoTimeoutSeconds) {
        return new RestWorkbenchClient(appUrl, userId, password, false
                , jobTimeoutSeconds, projectJobTimeoutSeconds, cloneRepoTimeoutSeconds);
    }

    @Override
    public JobResult getJob(String jobId) {
        log.info("Getting job '{}'", jobId);

        return target.path("jobs/{jobId}")
                .resolveTemplate("jobId", jobId)
                .request().get(JobResult.class);
    }

    @Override
    public JobResult deleteJob(String jobId) {
        log.info("Deleting job '{}'", jobId);

        return target.path("jobs/{jobId}")
                .resolveTemplate("jobId", jobId)
                .request().delete(JobResult.class);
    }

    @Override
    public Collection<ProjectResponse> getProjects() {
        log.info("Getting all projects");

        return target.path("projects").request().get(new GenericType<Collection<ProjectResponse>>() {
        });
    }

    @Override
    public ProjectResponse getProject(String projectName) {
        log.info("Getting project '{}'", projectName);

        return target.path("projects/{projectName}")
                .resolveTemplate("projectName", projectName)
                .request().get(ProjectResponse.class);
    }

    @Override
    public CloneRepositoryRequest cloneRepository(String orgUnitName, String repositoryName, String gitRepositoryUrl) {
        RepositoryRequest repository = new RepositoryRequest();
        repository.setName(repositoryName);
        repository.setOrganizationalUnitName(orgUnitName);
        repository.setGitURL(gitRepositoryUrl);
        repository.setRequestType("clone");
        return cloneRepository(repository);
    }

    @Override
    public CloneRepositoryRequest cloneRepository(RepositoryRequest repository) {
        log.info("Creating new repository '{}'", repository.getName());

        CloneRepositoryRequest request = target.path("repositories").request()
                .post(createEntity(repository), CloneRepositoryRequest.class);

        if (request.getRepository().getRequestType().equals("clone")) {
            return waitUntilJobFinished(request, cloneRepoTimeoutSeconds);
        } else {
            return waitUntilJobFinished(request);
        }
    }

    @Override
    public DeleteProjectRequest deleteProject(String projectName) {
        log.info("Deleting project '{}'", projectName);

        DeleteProjectRequest request = target.path("projects/{projectName}")
                .resolveTemplate("projectName", projectName)
                .request().delete(DeleteProjectRequest.class);

        return waitUntilJobFinished(request);
    }

    @Override
    public CreateProjectRequest createProject(String repositoryName, String projectName, String groupId, String version) {
        return createProject(repositoryName, projectName, groupId, version, null);
    }

    @Override
    public CreateProjectRequest createProject(String repositoryName, String projectName, String groupId, String version, String description) {
        ProjectRequest project = new ProjectRequest();
        project.setGroupId(groupId);
        project.setName(projectName);
        project.setVersion(version);
        project.setDescription(description);
        return createProject(repositoryName, project);
    }

    @Override
    public CreateProjectRequest createProject(String organizationalUnitName, ProjectRequest project) {
        log.info("Creating project '{}' in repository '{}'", project.getName(), organizationalUnitName);

        CreateProjectRequest request = target.path("organizationalUnits/{organizationalUnitName}/projects")
                .resolveTemplate("organizationalUnitName", organizationalUnitName)
                .request().post(createEntity(project), CreateProjectRequest.class);

        return waitUntilJobFinished(request, projectJobTimeoutSeconds);
    }

    @Override
    public Collection<ProjectResponse> getProjects(String repositoryName) {
        log.info("Retrieving all projects from repository '{}'", repositoryName);

        return target.path("repositories/{repositoryName}/projects")
                .resolveTemplate("repositoryName", repositoryName)
                .request().get(new GenericType<Collection<ProjectResponse>>() {
                });
    }

    @Override
    public Collection<OrganizationalUnit> getOrganizationalUnits() {
        log.info("Getting all organizational units");

        return target.path("organizationalunits").request().get(new GenericType<Collection<OrganizationalUnit>>() {
        });
    }

    @Override
    public CreateOrganizationalUnitRequest createOrganizationalUnit(String orgUnitName, String owner) {
        return createOrganizationalUnit(orgUnitName, owner, null);
    }

    @Override
    public CreateOrganizationalUnitRequest createOrganizationalUnit(String orgUnitName, String owner, String description) {
        return createOrganizationalUnit(orgUnitName, owner, description, null);
    }

    @Override
    public CreateOrganizationalUnitRequest createOrganizationalUnit(String orgUnitName, String owner, String description, String groupId) {
        OrganizationalUnit orgUnit = new OrganizationalUnit();
        orgUnit.setName(orgUnitName);
        orgUnit.setOwner(owner);
        orgUnit.setDescription(description);
        orgUnit.setDefaultGroupId(groupId);
        return createOrganizationalUnit(orgUnit);
    }

    @Override
    public CreateOrganizationalUnitRequest createOrganizationalUnit(OrganizationalUnit orgUnit) {
        log.info("Creating organizational unit '{}' ", orgUnit.getName());

        CreateOrganizationalUnitRequest request = target.path("organizationalunits").request()
                .post(createEntity(orgUnit), CreateOrganizationalUnitRequest.class);

        return waitUntilJobFinished(request);
    }

    @Override
    public OrganizationalUnit getOrganizationalUnit(String orgUnitName) {
        log.info("Getting organizational unit '{}'", orgUnitName);

        return target.path("organizationalunits/{orgUnitName}")
                .resolveTemplate("orgUnitName", orgUnitName)
                .request().get(OrganizationalUnit.class);
    }

    @Override
    public UpdateOrganizationalUnitRequest updateOrganizationalUnit(String name, UpdateOrganizationalUnit orgUnit) {
        log.info("Updating organizational unit '{}'", name);

        UpdateOrganizationalUnitRequest request = target.path("organizationalunits/{orgUnitName}")
                .resolveTemplate("orgUnitName", name)
                .request().post(createEntity(orgUnit), UpdateOrganizationalUnitRequest.class);

        return waitUntilJobFinished(request);
    }

    @Override
    public RemoveOrganizationalUnitRequest deleteOrganizationalUnit(String name) {
        log.info("Deleting organizational unit '{}'", name);

        RemoveOrganizationalUnitRequest request = target.path("organizationalunits/{orgUnitName}")
                .resolveTemplate("orgUnitName", name)
                .request().delete(RemoveOrganizationalUnitRequest.class);

        return waitUntilJobFinished(request);
    }

    @Override
    public AddProjectToOrganizationalUnitRequest addProjectToOrganizationalUnit(String orgUnitName, String projectName) {
        log.info("Adding projct '{}' to organizational unit '{}'", projectName, orgUnitName);

        AddProjectToOrganizationalUnitRequest request = target.path("organizationalunits/{orgUnitName}/projects/{projectName}")
                .resolveTemplate("orgUnitName", orgUnitName)
                .resolveTemplate("projectName", projectName)
                .request().post(createEntity(""), AddProjectToOrganizationalUnitRequest.class);

        return waitUntilJobFinished(request);
    }

    @Override
    public RemoveProjectFromOrganizationalUnitRequest removeProjectFromOrganizationalUnit(String orgUnitName, String projectName) {
        log.info("Removing project '{}' from organizational unit '{}'", projectName, orgUnitName);

        RemoveProjectFromOrganizationalUnitRequest request = target.path("organizationalunits/{orgUnitName}/project/{projectName}")
                .resolveTemplate("orgUnitName", orgUnitName)
                .resolveTemplate("projectName", projectName)
                .request().delete(RemoveProjectFromOrganizationalUnitRequest.class);

        return waitUntilJobFinished(request);
    }

    private <T extends JobRequest> T postMavenRequest(String repositoryName, String projectName, String phase, Class<T> requestType) {
        return target.path("repositories/{repositoryName}/projects/{projectName}/maven/{phase}")
                .resolveTemplate("repositoryName", repositoryName)
                .resolveTemplate("projectName", projectName)
                .resolveTemplate("phase", phase)
                .request().post(createEntity(""), requestType);
    }

    private <T extends JobRequest> T postMavenRequest(String projectName, String phase, Class<T> requestType) {
        return target.path("projects/{projectName}/maven/{phase}")
                .resolveTemplate("projectName", projectName)
                .resolveTemplate("phase", phase)
                .request().post(createEntity(""), requestType);
    }

    @Override
    public CompileProjectRequest compileProject(String projectName) {
        log.info("Compiling project '{}' ", projectName);

        CompileProjectRequest request = postMavenRequest(projectName, "compile", CompileProjectRequest.class);

        return waitUntilJobFinished(request, projectJobTimeoutSeconds);
    }

    @Override
    public InstallProjectRequest installProject(String repositoryName, String projectName) {
        log.info("Installing project '{}' from repository '{}'", projectName, repositoryName);

        InstallProjectRequest request = postMavenRequest(repositoryName, projectName, "install", InstallProjectRequest.class);

        return waitUntilJobFinished(request, projectJobTimeoutSeconds);
    }

    @Override
    public TestProjectRequest testProject(String projectName) {
        log.info("Testing project '{}' ", projectName);

        TestProjectRequest request = postMavenRequest(projectName, "test", TestProjectRequest.class);

        return waitUntilJobFinished(request, projectJobTimeoutSeconds);
    }

    @Override
    public DeployProjectRequest deployProject(String projectName) {
        log.info("Deploying project '{}' ", projectName);

        DeployProjectRequest request = postMavenRequest(projectName, "deploy", DeployProjectRequest.class);

        return waitUntilJobFinished(request, projectJobTimeoutSeconds);
    }

    private <T extends JobRequest> T waitUntilJobFinished(T request) {
        return waitUntilJobFinished(request, jobTimeoutSeconds);
    }

    private <T extends JobRequest> T waitUntilJobFinished(T request, int seconds) {
        if (async) {
            return request;
        }

        JobResult jobResult;
        int totalSecondsWaited = 0;
        while ((jobResult = getJob(request.getJobId())).getStatus() != JobStatus.SUCCESS && seconds-- > 0) {
            switch (jobResult.getStatus()) {
                case ACCEPTED:
                case APPROVED:
                    sleepForSecond();
                    totalSecondsWaited++;
                    break;
                case SUCCESS:
                    log.info("  It took {} seconds to complete the job", totalSecondsWaited);
                    return request;
                default:
                    log.warn("  Timeout waiting {} seconds for job to succeed", totalSecondsWaited);
                    throw new NotSuccessException(jobResult);
            }
        }

        if (jobResult.getStatus() != JobStatus.SUCCESS) {
            log.warn("  Timeout waiting {} seconds for job to succeed", totalSecondsWaited);
            throw new NotSuccessException(jobResult);
        }

        return request;
    }

    private void sleepForSecond() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
            // continue
        }
    }

    private <T> Entity<T> createEntity(T body) {
        return Entity.entity(body, MEDIA_TYPE);
    }
}
