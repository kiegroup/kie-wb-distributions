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

package org.kie.wb.test.rest.functional;

import java.util.Collection;

import javax.ws.rs.NotFoundException;

import org.assertj.core.api.Assertions;
import org.assertj.core.api.SoftAssertions;
import org.guvnor.rest.client.CompileProjectRequest;
import org.guvnor.rest.client.CreateProjectRequest;
import org.guvnor.rest.client.DeleteProjectRequest;
import org.guvnor.rest.client.DeployProjectRequest;
import org.guvnor.rest.client.InstallProjectRequest;
import org.guvnor.rest.client.JobStatus;
import org.guvnor.rest.client.ProjectRequest;
import org.guvnor.rest.client.ProjectResponse;
import org.guvnor.rest.client.TestProjectRequest;
import org.junit.BeforeClass;
import org.junit.Test;
import org.kie.wb.test.rest.RestTestBase;
import org.kie.wb.test.rest.client.NotSuccessException;
import qa.tools.ikeeper.annotation.Jira;

import static org.assertj.core.api.Assertions.*;

public class ProjectIntegrationTest extends RestTestBase {

    private static final String ORG_UNIT = "projectTestOrgUnit";
    private static final String REPOSITORY = "projectTestRepository";

    private static final String DESCRIPTION = "Testing project";
    private static final String GROUP_ID = "org.kie.wb.test";
    private static final String VERSION = "1.0.0";
    private static final String SNAPSHOT_VERSION = "1.0.0-SNAPSHOT";

    private static final String DEFAULT_VERSION = "1.0";

    @BeforeClass
    public static void createRepository() {
        createOrganizationalUnit(ORG_UNIT);
        createNewRepository(ORG_UNIT, REPOSITORY);
    }

    @Test
    @Jira("GUVNOR-2542")
    public void testCreateProjectWithoutName() {
        ProjectRequest project = new ProjectRequest();
        project.setGroupId(GROUP_ID);
        project.setVersion(VERSION);

        try {
            client.createProject(REPOSITORY, project);
        } catch (NotSuccessException ex) {
            assertThat(ex.getJobResult().getStatus()).isEqualTo(JobStatus.BAD_REQUEST);
            assertThat(ex.getJobResult().getResult()).contains("name");
        }
    }

    @Test
    public void testCreateProjectMinimal() {
        createProject("minimalProject", null, null, null);
    }

    @Test
    public void testCreateProjectWithDescription() {
        createProject("projectWithDescription", DESCRIPTION, null, null);
    }

    @Test
    public void testCreateProjectWithGroupId() {
        createProject("projectWithGroupId", null, GROUP_ID, null);
    }

    @Test
    public void testCreateProjectWithVersion() {
        createProject("projectWithoutVersion", null, null, VERSION);
    }

    @Jira("GUVNOR-2542")
    @Test(expected = NotFoundException.class)
    public void testDeleteNotExistingProject() {
        try {
            client.deleteProject(REPOSITORY, "notExistingProject");
        } catch (NotSuccessException ex) {
            System.out.println(ex.getJobResult().getStatus() + ": " + ex.getJobResult().getResult());
        }
    }

    @Test
    public void testDeleteProject() {
        String name = "projectToBeDeleted";
        createProject(name, null, null, null);

        DeleteProjectRequest request = client.deleteProject(REPOSITORY, name);
        assertThat(request.getRepositoryName()).isEqualTo(REPOSITORY);
        assertThat(request.getProjectName()).isEqualTo(name);

        ProjectResponse project = getProjectByName(name);
        assertThat(project).isNull();
    }

    @Test
    public void testGetProjects() {
        String name = "oneOfManyProjects";
        createProject(name, null, null, null);

        Collection<ProjectResponse> projects = client.getProjects(REPOSITORY);
        assertThat(projects).extracting(ProjectResponse::getName).contains(name);
    }

    @Test
    public void testCompileProject() {
        String name = "projectToBeCompiled";
        createProject(name, null, null, null);

        CompileProjectRequest request = client.compileProject(REPOSITORY, name);
        assertThat(request.getRepositoryName()).isEqualTo(REPOSITORY);
        assertThat(request.getProjectName()).isEqualTo(name);
    }

    @Test
    public void testTestProject() {
        String name = "projectToBeTested";
        createProject(name, null, null, null);

        TestProjectRequest request = client.testProject(REPOSITORY, name);
        assertThat(request.getRepositoryName()).isEqualTo(REPOSITORY);
        assertThat(request.getProjectName()).isEqualTo(name);
    }

    @Test
    public void testInstallProject() {
        String name = "projectToBeInstalled" + Math.random();
        createProject(name, null, null, null);

        InstallProjectRequest request = client.installProject(REPOSITORY, name);
        assertThat(request.getRepositoryName()).isEqualTo(REPOSITORY);
        assertThat(request.getProjectName()).isEqualTo(name);
    }

    @Test
    public void testInstallProjectTwice() {
        String name = "projectToBeInstalledTwice" + Math.random();
        createProject(name, null, null, VERSION);

        InstallProjectRequest request = client.installProject(REPOSITORY, name);
        assertThat(request.getRepositoryName()).isEqualTo(REPOSITORY);
        assertThat(request.getProjectName()).isEqualTo(name);

        try {
            client.installProject(REPOSITORY, name);
        } catch (NotSuccessException ex) {
            Assertions.assertThat(ex.getJobResult().getStatus()).isEqualTo(JobStatus.DUPLICATE_RESOURCE);
        }
    }

    @Test
    public void testInstallProjectSnapshotTwice() {
        String name = "projectSnapshotToBeInstalledTwice" + Math.random();
        createProject(name, null, null, SNAPSHOT_VERSION);

        InstallProjectRequest request = client.installProject(REPOSITORY, name);
        assertThat(request.getRepositoryName()).isEqualTo(REPOSITORY);
        assertThat(request.getProjectName()).isEqualTo(name);

        request = client.installProject(REPOSITORY, name);
        assertThat(request.getRepositoryName()).isEqualTo(REPOSITORY);
        assertThat(request.getProjectName()).isEqualTo(name);
    }

    @Test
    public void testDeployProject() {
        String name = "projectToBeDeployed" + Math.random();
        createProject(name, null, null, null);

        DeployProjectRequest request = client.deployProject(REPOSITORY, name);
        assertThat(request.getRepositoryName()).isEqualTo(REPOSITORY);
        assertThat(request.getProjectName()).isEqualTo(name);
    }

    @Test
    public void testDeployProjectTwice() {
        String name = "projectToBeDeployedTwice" + Math.random();
        createProject(name, null, null, VERSION);

        DeployProjectRequest request = client.deployProject(REPOSITORY, name);
        assertThat(request.getRepositoryName()).isEqualTo(REPOSITORY);
        assertThat(request.getProjectName()).isEqualTo(name);

        try {
            client.deployProject(REPOSITORY, name);
        } catch (NotSuccessException ex) {
            Assertions.assertThat(ex.getJobResult().getStatus()).isEqualTo(JobStatus.DUPLICATE_RESOURCE);
        }
    }

    @Test
    public void testDeployProjectSnapshotTwice() {
        String name = "projectSnapshotToBeDeployedTwice" + Math.random();
        createProject(name, null, null, SNAPSHOT_VERSION);

        DeployProjectRequest request = client.deployProject(REPOSITORY, name);
        assertThat(request.getRepositoryName()).isEqualTo(REPOSITORY);
        assertThat(request.getProjectName()).isEqualTo(name);

        request = client.deployProject(REPOSITORY, name);
        assertThat(request.getRepositoryName()).isEqualTo(REPOSITORY);
        assertThat(request.getProjectName()).isEqualTo(name);
    }

    private void createProject(String name, String description, String groupId, String version) {
        ProjectRequest project = new ProjectRequest();
        project.setName(name);
        project.setDescription(description);
        project.setGroupId(groupId);
        project.setVersion(version);

        CreateProjectRequest request = client.createProject(REPOSITORY, project);
        assertCreateProjectRequest(request, project);
        assertProjectExists(project);
    }

    private void assertCreateProjectRequest(CreateProjectRequest actual, ProjectRequest expected) {
        SoftAssertions assertions = new SoftAssertions();
        assertions.assertThat(actual.getRepositoryName()).isEqualTo(REPOSITORY);
        assertions.assertThat(actual.getProjectName()).isEqualTo(expected.getName());
        assertions.assertThat(actual.getDescription()).isEqualTo(expected.getDescription());
        assertions.assertThat(actual.getProjectGroupId()).isEqualTo(expected.getGroupId());
        assertions.assertThat(actual.getProjectVersion()).isEqualTo(expected.getVersion());
        assertions.assertAll();
    }

    private void assertProjectExists(ProjectRequest projectRequest) {
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
        Collection<ProjectResponse> projects = client.getProjects(REPOSITORY);
        return projects.parallelStream()
                .filter(projectResponse -> projectResponse.getName().equals(name))
                .findAny().orElse(null);
    }

}
