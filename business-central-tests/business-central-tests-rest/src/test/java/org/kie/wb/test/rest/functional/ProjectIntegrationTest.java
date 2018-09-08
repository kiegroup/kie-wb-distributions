/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.wb.test.rest.functional;

import java.util.Collection;

import javax.ws.rs.NotFoundException;

import org.assertj.core.api.Assertions;
import org.assertj.core.api.SoftAssertions;
import org.guvnor.rest.client.CompileProjectRequest;
import org.guvnor.rest.client.CreateProjectJobRequest;
import org.guvnor.rest.client.CreateProjectRequest;
import org.guvnor.rest.client.DeleteProjectRequest;
import org.guvnor.rest.client.DeployProjectRequest;
import org.guvnor.rest.client.InstallProjectRequest;
import org.guvnor.rest.client.JobStatus;
import org.guvnor.rest.client.ProjectResponse;
import org.guvnor.rest.client.TestProjectRequest;
import org.junit.BeforeClass;
import org.junit.Test;
import org.kie.wb.test.rest.RestTestBase;
import org.kie.wb.test.rest.client.NotSuccessException;
import qa.tools.ikeeper.annotation.Jira;

import static org.assertj.core.api.Assertions.assertThat;

public class ProjectIntegrationTest extends RestTestBase {

    private static final String SPACE = "projectTestSpace";
    private static final String PROJECT = "projectTestProject";

    private static final String DESCRIPTION = "Testing project";
    private static final String GROUP_ID = "org.kie.wb.test";
    private static final String VERSION = "1.0.0";
    private static final String SNAPSHOT_VERSION = "1.0.0-SNAPSHOT";

    private static final String DEFAULT_VERSION = "1.0";

    @BeforeClass
    public static void createRepository() {
        createSpace(SPACE);
    }

    @Test
    @Jira("GUVNOR-2542")
    public void testCreateProjectWithoutName() {
        CreateProjectRequest createProjectRequest = new CreateProjectRequest();
        createProjectRequest.setGroupId(GROUP_ID);
        createProjectRequest.setVersion(VERSION);

        try {
            client.createProject(SPACE, createProjectRequest);
        } catch (NotSuccessException ex) {
            assertThat(ex.getJobResult().getStatus()).isEqualTo(JobStatus.BAD_REQUEST);
            assertThat(ex.getJobResult().getResult()).contains("name");
        }
    }

    @Test
    public void testCreateProjectMinimal() {
        createProject("minimalProject", null, GROUP_ID, VERSION);
    }

    @Test
    public void testCreateProjectWithDescription() {
        createProject("projectWithDescription", DESCRIPTION, GROUP_ID, VERSION);
    }

    @Test
    public void testCreateProjectWithGroupId() {
        createProject("projectWithGroupId", null, GROUP_ID, VERSION);
    }

    @Test
    public void testCreateProjectWithVersion() {
        createProject("projectWithoutVersion", null, GROUP_ID, VERSION);
    }

    @Jira("GUVNOR-2542")
    @Test(expected = NotFoundException.class)
    public void testDeleteNotExistingProject() {
        try {
            client.deleteProject(PROJECT, "notExistingProject");
        } catch (NotSuccessException ex) {
            System.out.println(ex.getJobResult().getStatus() + ": " + ex.getJobResult().getResult());
        }
    }

    @Test
    public void testDeleteProject() {
        String name = "projectToBeDeleted";
        createProject(name, null, GROUP_ID, VERSION);

        DeleteProjectRequest request = client.deleteProject(SPACE, name);
        assertThat(request.getSpaceName()).isEqualTo(SPACE);
        assertThat(request.getProjectName()).isEqualTo(name);

        ProjectResponse project = getProjectByName(name);
        assertThat(project).isNull();
    }

    @Test
    public void testGetProjects() {
        String name = "oneOfManyProjects";
        createProject(name, null, GROUP_ID, VERSION);

        Collection<ProjectResponse> projects = client.getProjects(SPACE);



        System.out.println("EEEEEEEEEEEEE "+projects.iterator().next().toString());
        assertThat(projects).extracting(ProjectResponse::getName).contains(name);
    }

    @Test
    public void testCompileProject() {
        String name = "projectToBeCompiled";
        createProject(name, null, GROUP_ID, VERSION);

        CompileProjectRequest request = client.compileProject(SPACE, name);
        assertThat(request.getSpaceName()).isEqualTo(SPACE);
        assertThat(request.getProjectName()).isEqualTo(name);
    }

    @Test
    public void testTestProject() {
        String name = "projectToBeTested";
        createProject(name, null, GROUP_ID, VERSION);

        TestProjectRequest request = client.testProject(SPACE, name);
        assertThat(request.getSpaceName()).isEqualTo(SPACE);
        assertThat(request.getProjectName()).isEqualTo(name);
    }

    @Test
    public void testInstallProject() {
        String name = "projectToBeInstalled" + Math.random();
        createProject(name, null, GROUP_ID, VERSION);

        InstallProjectRequest request = client.installProject(SPACE, name);
        assertThat(request.getSpaceName()).isEqualTo(SPACE);
        assertThat(request.getProjectName()).isEqualTo(name);
    }

    @Test
    public void testInstallProjectTwice() {
        String name = "projectToBeInstalledTwice" + Math.random();
        createProject(name, null, GROUP_ID, VERSION);

        InstallProjectRequest request = client.installProject(SPACE, name);
        assertThat(request.getSpaceName()).isEqualTo(SPACE);
        assertThat(request.getProjectName()).isEqualTo(name);

        try {
            client.installProject(SPACE, name);
        } catch (NotSuccessException ex) {
            Assertions.assertThat(ex.getJobResult().getStatus()).isEqualTo(JobStatus.DUPLICATE_RESOURCE);
        }
    }

    @Test
    public void testInstallProjectSnapshotTwice() {
        String name = "projectSnapshotToBeInstalledTwice" + Math.random();
        createProject(name, null, GROUP_ID, SNAPSHOT_VERSION);

        InstallProjectRequest request = client.installProject(SPACE, name);
        assertThat(request.getSpaceName()).isEqualTo(SPACE);
        assertThat(request.getProjectName()).isEqualTo(name);

        request = client.installProject(SPACE, name);
        assertThat(request.getSpaceName()).isEqualTo(SPACE);
        assertThat(request.getProjectName()).isEqualTo(name);
    }

    @Test
    public void testDeployProject() {
        String name = "projectToBeDeployed" + Math.random();
        createProject(name, null, GROUP_ID, VERSION);

        DeployProjectRequest request = client.deployProject(SPACE, name);
        assertThat(request.getSpaceName()).isEqualTo(SPACE);
        assertThat(request.getProjectName()).isEqualTo(name);
    }

    @Test
    public void testDeployProjectTwice() {
        String name = "projectToBeDeployedTwice" + Math.random();
        createProject(name, null, GROUP_ID, VERSION);

        DeployProjectRequest request = client.deployProject(SPACE, name);
        assertThat(request.getSpaceName()).isEqualTo(SPACE);
        assertThat(request.getProjectName()).isEqualTo(name);

        try {
            client.deployProject(SPACE, name);
        } catch (NotSuccessException ex) {
            Assertions.assertThat(ex.getJobResult().getStatus()).isEqualTo(JobStatus.DUPLICATE_RESOURCE);
        }
    }

    @Test
    public void testDeployProjectSnapshotTwice() {
        String name = "projectSnapshotToBeDeployedTwice" + Math.random();
        createProject(name, null, GROUP_ID, SNAPSHOT_VERSION);

        DeployProjectRequest request = client.deployProject(SPACE, name);
        assertThat(request.getSpaceName()).isEqualTo(SPACE);
        assertThat(request.getProjectName()).isEqualTo(name);

        request = client.deployProject(SPACE, name);
        assertThat(request.getSpaceName()).isEqualTo(SPACE);
        assertThat(request.getProjectName()).isEqualTo(name);
    }

    private void createProject(String name, String description, String groupId, String version) {
        final CreateProjectRequest createProjectRequest = new CreateProjectRequest();
        createProjectRequest.setGroupId(groupId);
        createProjectRequest.setVersion(version);
        createProjectRequest.setName(name);
        createProjectRequest.setDescription(description);

        final CreateProjectJobRequest request = client.createProject(SPACE, createProjectRequest);
        assertCreateProjectRequest(request, createProjectRequest);
        assertProjectExists(createProjectRequest);
    }

    private void assertCreateProjectRequest(CreateProjectJobRequest actual, CreateProjectRequest expected) {
        SoftAssertions assertions = new SoftAssertions();
        assertions.assertThat(actual.getSpaceName()).isEqualTo(SPACE);
        assertions.assertThat(actual.getProjectName()).isEqualTo(expected.getName());
        assertions.assertThat(actual.getDescription()).isEqualTo(expected.getDescription());
        assertions.assertThat(actual.getProjectGroupId()).isEqualTo(expected.getGroupId());
        assertions.assertThat(actual.getProjectVersion()).isEqualTo(expected.getVersion());
        assertions.assertAll();
    }

    private void assertProjectExists(CreateProjectRequest projectRequest) {
        ProjectResponse projectResponse = getProjectByName(projectRequest.getName());

        SoftAssertions assertions = new SoftAssertions();
        assertions.assertThat(projectResponse.getName()).isEqualTo(projectRequest.getName());
        assertions.assertThat(projectResponse.getDescription()).isEqualTo(projectRequest.getDescription());
        assertions.assertThat(projectResponse.getGroupId())
                .isEqualTo(projectRequest.getGroupId() == null ? projectRequest.getName() : projectRequest.getGroupId());
        assertions.assertThat(projectResponse.getVersion())
                .isEqualTo(projectRequest.getVersion() == null ? DEFAULT_VERSION : projectRequest.getVersion());
        assertions.assertAll();
    }

    private ProjectResponse getProjectByName(String name) {
        Collection<ProjectResponse> projects = client.getProjects(SPACE);

        return projects.parallelStream()
                .filter(projectResponse -> projectResponse.getName().equals(name))
                .findAny().orElse(null);
    }
}
