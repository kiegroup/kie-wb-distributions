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
import javax.ws.rs.core.Response;

import org.guvnor.rest.client.CloneProjectJobRequest;
import org.guvnor.rest.client.CloneProjectRequest;
import org.guvnor.rest.client.CompileProjectRequest;
import org.guvnor.rest.client.CreateProjectJobRequest;
import org.guvnor.rest.client.CreateProjectRequest;
import org.guvnor.rest.client.DeleteProjectRequest;
import org.guvnor.rest.client.DeployProjectRequest;
import org.guvnor.rest.client.InstallProjectRequest;
import org.guvnor.rest.client.JobRequest;
import org.guvnor.rest.client.JobResult;
import org.guvnor.rest.client.JobStatus;
import org.guvnor.rest.client.ProjectResponse;
import org.guvnor.rest.client.RemoveSpaceRequest;
import org.guvnor.rest.client.Space;
import org.guvnor.rest.client.SpaceRequest;
import org.guvnor.rest.client.TestProjectRequest;
import org.kie.workbench.common.screens.library.api.SpacesScreenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RestWorkbenchClient implements WorkbenchClient {

    private static final Logger log = LoggerFactory.getLogger(RestWorkbenchClient.class);

    private static final int DEFAULT_JOB_TIMEOUT_SECONDS = 60; // TODO lower this back to 10s after https://issues.jboss.org/browse/AF-1310 is fixed
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

        final Client client = ClientBuilder.newClient();
        if (userId != null && password != null) {
            client.register(new Authenticator(userId, password));
        }

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
     * Creates Business Central REST client which will execute each operation asynchronously. The status of the operation
     * can be checked by retrieving the details about the job with ID provided in the request.
     */
    public static WorkbenchClient createAsyncWorkbenchClient(String appUrl, String userId, String password) {
        return new RestWorkbenchClient(appUrl, userId, password, true);
    }

    /**
     * Creates Business Central REST client which will wait for successful completion of each operation.
     */
    public static WorkbenchClient createWorkbenchClient(String appUrl, String userId, String password) {
        return new RestWorkbenchClient(appUrl, userId, password, false);
    }

    /**
     * Creates Business Central REST client which will wait for successful completion of each operation using specified timeouts.
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
    public ProjectResponse getProject(String spaceName, String projectName) {
        log.info("Getting project '{}'", spaceName);

        return target.path("spaces/{spaceName}/projects/{projectName}")
                .resolveTemplate("spaceName", spaceName)
                .resolveTemplate("projectName", projectName)
                .request().get(ProjectResponse.class);
    }

    @Override
    public CloneProjectJobRequest cloneRepository(String spaceName,
                                                  CloneProjectRequest cloneProjectRequest) {
        log.info("Cloning a project '{}'", cloneProjectRequest.getName());

        CloneProjectJobRequest request = target.path("/spaces/{spaceName}/git/clone")
                .resolveTemplate("spaceName", spaceName)
                .request()
                .post(createEntity(cloneProjectRequest), CloneProjectJobRequest.class);

        return waitUntilJobFinished(request, cloneRepoTimeoutSeconds);
    }

    @Override
    public CreateProjectJobRequest createProject(String spaceName,
                                                 String projectName,
                                                 String groupId,
                                                 String version) {
        return createProject(spaceName, projectName, groupId, version, null);
    }

    @Override
    public CreateProjectJobRequest createProject(String spaceName, String projectName, String groupId, String version, String description) {
        final CreateProjectRequest createProjectRequest = new CreateProjectRequest();
        createProjectRequest.setGroupId(groupId);
        createProjectRequest.setVersion(version);
        createProjectRequest.setName(projectName);
        createProjectRequest.setDescription(description);

        return createProject(spaceName, createProjectRequest);
    }

    @Override
    public CreateProjectJobRequest createProject(final String spaceName,
                                                 final CreateProjectRequest createProjectRequest) {
        log.info("Creating project '{}' in space '{}'", createProjectRequest.getName(), spaceName);

        CreateProjectJobRequest request = target.path("spaces/{spaceName}/projects")
                .resolveTemplate("spaceName", spaceName)
                .request().post(createEntity(createProjectRequest), CreateProjectJobRequest.class);

        return waitUntilJobFinished(request, projectJobTimeoutSeconds);
    }

    @Override
    public DeleteProjectRequest deleteProject(String spaceName, String projectName) {
        log.info("Removing project '{}' from space '{}'", projectName, spaceName);

        DeleteProjectRequest request = target.path("spaces/{spaceName}/projects/{projectName}")
                .resolveTemplate("spaceName", spaceName)
                .resolveTemplate("projectName", projectName)
                .request().delete(DeleteProjectRequest.class);

        return waitUntilJobFinished(request, projectJobTimeoutSeconds);
    }

    @Override
    public Collection<ProjectResponse> getProjects(String spaceName) {
        log.info("Retrieving all projects from space '{}'", spaceName);

        return target.path("spaces/{spaceName}/projects")
                .resolveTemplate("spaceName", spaceName)
                .request().get(new GenericType<Collection<ProjectResponse>>() {
                });
    }

    @Override
    public Collection<Space> getSpaces() {
        log.info("Getting all spaces");

        return target.path("spaces").request().get(new GenericType<Collection<Space>>() {
        });
    }

    @Override
    public SpaceRequest createSpace(String spaceName, String owner) {
        return createSpace(spaceName, owner, null);
    }

    @Override
    public SpaceRequest createSpace(String spaceName, String owner, String description) {
        return createSpace(spaceName, owner, description, null);
    }

    @Override
    public SpaceRequest createSpace(String spaceName, String owner, String description, String groupId) {
        Space space = new Space();
        space.setName(spaceName);
        space.setOwner(owner);
        space.setDescription(description);
        space.setDefaultGroupId(groupId);
        return createSpace(space);
    }

    @Override
    public SpaceRequest createSpace(Space spaceName) {
        log.info("Creating space '{}' ", spaceName.getName());

        SpaceRequest request = target.path("spaces").request()
                .post(createEntity(spaceName), SpaceRequest.class);

        return waitUntilJobFinished(request);
    }

    @Override
    public Space getSpace(String spaceNameName) {
        log.info("Getting space '{}'", spaceNameName);

        return target.path("spaces/{spaceName}")
                .resolveTemplate("spaceName", spaceNameName)
                .request().get(Space.class);
    }

    @Override
    public String isReady() {
        log.info("Getting readiness status");
        return target.path("ready")
                .request().get(String.class);
    }

    @Override
    public String isHealthy() {
        log.info("Getting health status");
        return target.path("healthy")
                .request().get(String.class);
    }

    @Override
    public org.guvnor.rest.client.RemoveSpaceRequest deleteSpace(String name) {
        log.info("Deleting space '{}'", name);

        RemoveSpaceRequest request = target.path("spaces/{spaceName}")
                .resolveTemplate("spaceName", name)
                .request().delete(RemoveSpaceRequest.class);

        return waitUntilJobFinished(request);
    }

    private <T extends JobRequest> T postMavenRequest(String spaceName, String projectName, String phase, Class<T> requestType) {
        return target.path("spaces/{spaceName}/projects/{projectName}/maven/{phase}")
                .resolveTemplate("spaceName", spaceName)
                .resolveTemplate("projectName", projectName)
                .resolveTemplate("phase", phase)
                .request().post(createEntity(""), requestType);
    }

    @Override
    public CompileProjectRequest compileProject(String spaceName, String projectName) {
        log.info("Compiling project '{}' from space '{}'", projectName, spaceName);

        CompileProjectRequest request = postMavenRequest(spaceName, projectName, "compile", CompileProjectRequest.class);

        return waitUntilJobFinished(request, projectJobTimeoutSeconds);
    }

    @Override
    public InstallProjectRequest installProject(String spaceName, String projectName) {
        log.info("Installing project '{}' from space '{}'", projectName, spaceName);

        InstallProjectRequest request = postMavenRequest(spaceName, projectName, "install", InstallProjectRequest.class);

        return waitUntilJobFinished(request, projectJobTimeoutSeconds);
    }

    @Override
    public TestProjectRequest testProject(String spaceName, String projectName) {
        log.info("Testing project '{}' from space '{}'", projectName, spaceName);

        TestProjectRequest request = postMavenRequest(spaceName, projectName, "test", TestProjectRequest.class);

        return waitUntilJobFinished(request, projectJobTimeoutSeconds);
    }

    @Override
    public DeployProjectRequest deployProject(String spaceName, String projectName) {
        log.info("Deploying project '{}' from space '{}'", projectName, spaceName);

        DeployProjectRequest request = postMavenRequest(spaceName, projectName, "deploy", DeployProjectRequest.class);

        return waitUntilJobFinished(request, projectJobTimeoutSeconds);
    }

    @Override
    public Response spacesScreen_getSpaces() {
        return target.path("spacesScreen/spaces")
                .request()
                .get();
    }

    @Override
    public Response spacesScreen_savePreference(final SpacesScreenLibraryPreference preference) {
        return target.path("spacesScreen/libraryPreference")
                .request()
                .put(createEntity(preference));
    }

    @Override
    public Response spacesScreen_getSpace(final String name) {
        return target.path("spacesScreen/spaces/{name}")
                .resolveTemplate("name", name)
                .request()
                .get();
    }

    @Override
    public boolean spacesScreen_isValidGroupId(final String groupId) {
        return target.path("spacesScreen/spaces/validGroupId")
                .queryParam("groupId", groupId)
                .request()
                .get(Boolean.class);
    }

    @Override
    public Response spacesScreen_postSpace(final SpacesScreenService.NewSpace newSpace) {
        return target.path("spacesScreen/spaces")
                .request()
                .post(createEntity(newSpace));
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
                    throw new ClientRequestTimedOutException(jobResult, totalSecondsWaited);
            }
        }

        if (jobResult.getStatus() != JobStatus.SUCCESS) {
            log.warn("  Timeout waiting {} seconds for job to succeed", totalSecondsWaited);
            throw new ClientRequestTimedOutException(jobResult, totalSecondsWaited);
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
