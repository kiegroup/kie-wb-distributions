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
import org.guvnor.rest.client.CompileProjectRequest;
import org.guvnor.rest.client.DeleteProjectRequest;
import org.guvnor.rest.client.DeployProjectRequest;
import org.guvnor.rest.client.InstallProjectRequest;
import org.guvnor.rest.client.JobStatus;
import org.guvnor.rest.client.ProjectRequest;
import org.guvnor.rest.client.ProjectResponse;
import org.guvnor.rest.client.TestProjectRequest;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.kie.wb.test.rest.RestTestBase;
import org.kie.wb.test.rest.Utils;
import org.kie.wb.test.rest.client.NotSuccessException;
import qa.tools.ikeeper.annotation.Jira;

import static org.assertj.core.api.Assertions.*;

public class ProjectIntegrationTest extends RestTestBase {

    private static final String ORG_UNIT = "projectTestOrgUnit";

    private static final String DESCRIPTION = "Testing project";
    private static final String GROUP_ID = "org.kie.wb.test";
    private static final String VERSION = "1.0.0";
    private static final String SNAPSHOT_VERSION = "1.0.0-SNAPSHOT";

    private Utils utils;

    @BeforeClass
    public static void createRepository() {
        createOrganizationalUnit(ORG_UNIT);
    }

    @Before
    public void setUp() {
        utils = new Utils(ORG_UNIT, client);
    }

    @Test
    @Jira("GUVNOR-2542")
    public void testCreateProjectWithoutName() {
        ProjectRequest project = new ProjectRequest();
        project.setGroupId(GROUP_ID);
        project.setVersion(VERSION);

        try {
            client.createProject(ORG_UNIT, project);
        } catch (NotSuccessException ex) {
            assertThat(ex.getJobResult().getStatus()).isEqualTo(JobStatus.BAD_REQUEST);
            assertThat(ex.getJobResult().getResult()).contains("name");
        }
    }

    @Test
    public void testCreateProjectMinimal() {
        utils.createProject("minimalProject", null, null, null);
    }

    @Test
    public void testCreateProjectWithDescription() {
        utils.createProject("projectWithDescription", DESCRIPTION, null, null);
    }

    @Test
    public void testCreateProjectWithGroupId() {
        utils.createProject("projectWithGroupId", null, GROUP_ID, null);
    }

    @Test
    public void testCreateProjectWithVersion() {
        utils.createProject("projectWithoutVersion", null, null, VERSION);
    }

    @Jira("GUVNOR-2542")
    @Test(expected = NotFoundException.class)
    public void testDeleteNotExistingProject() {
        try {
            client.deleteProject("notExistingProject");
        } catch (NotSuccessException ex) {
            System.out.println(ex.getJobResult().getStatus() + ": " + ex.getJobResult().getResult());
        }
    }

    @Test
    public void testDeleteProject() {
        String name = "projectToBeDeleted";
        utils.createProject(name, null, null, null);

        DeleteProjectRequest request = client.deleteProject(name);
        assertThat(request.getProjectName()).isEqualTo(name);

        ProjectResponse project = utils.getProjectByName(name);
        assertThat(project).isNull();
    }

    @Test
    public void testGetProjects() {
        String name = "oneOfManyProjects";
        utils.createProject(name, null, null, null);

        Collection<ProjectResponse> projects = client.getProjects(ORG_UNIT);
        assertThat(projects).extracting(ProjectResponse::getName).contains(name);
    }

    @Test
    public void testCompileProject() {
        String name = "projectToBeCompiled";
        utils.createProject(name, null, null, null);

        CompileProjectRequest request = client.compileProject(name);
        assertThat(request.getProjectName()).isEqualTo(name);
    }

    @Test
    public void testTestProject() {
        String name = "projectToBeTested";
        utils.createProject(name, null, null, null);

        TestProjectRequest request = client.testProject(name);
        assertThat(request.getProjectName()).isEqualTo(name);
    }

    @Test
    public void testInstallProject() {
        String name = "projectToBeInstalled" + Math.random();
        utils.createProject(name, null, null, null);

        InstallProjectRequest request = client.installProject(ORG_UNIT, name);
        assertThat(request.getOrganizationalUnitName()).isEqualTo(ORG_UNIT);
        assertThat(request.getProjectName()).isEqualTo(name);
    }

    @Test
    public void testInstallProjectTwice() {
        String name = "projectToBeInstalledTwice" + Math.random();
        utils.createProject(name, null, null, VERSION);

        InstallProjectRequest request = client.installProject(ORG_UNIT, name);
        assertThat(request.getOrganizationalUnitName()).isEqualTo(ORG_UNIT);
        assertThat(request.getProjectName()).isEqualTo(name);

        try {
            client.installProject(ORG_UNIT, name);
        } catch (NotSuccessException ex) {
            Assertions.assertThat(ex.getJobResult().getStatus()).isEqualTo(JobStatus.DUPLICATE_RESOURCE);
        }
    }

    @Test
    public void testInstallProjectSnapshotTwice() {
        String name = "projectSnapshotToBeInstalledTwice" + Math.random();
        utils.createProject(name, null, null, SNAPSHOT_VERSION);

        InstallProjectRequest request = client.installProject(ORG_UNIT, name);
        assertThat(request.getOrganizationalUnitName()).isEqualTo(ORG_UNIT);
        assertThat(request.getProjectName()).isEqualTo(name);

        request = client.installProject(ORG_UNIT, name);
        assertThat(request.getOrganizationalUnitName()).isEqualTo(ORG_UNIT);
        assertThat(request.getProjectName()).isEqualTo(name);
    }

    @Test
    public void testDeployProject() {
        String name = "projectToBeDeployed" + Math.random();
        utils.createProject(name, null, null, null);

        DeployProjectRequest request = client.deployProject(name);
        assertThat(request.getProjectName()).isEqualTo(name);
    }

    @Test
    public void testDeployProjectTwice() {
        String name = "projectToBeDeployedTwice" + Math.random();
        utils.createProject(name, null, null, VERSION);

        DeployProjectRequest request = client.deployProject(name);
        assertThat(request.getProjectName()).isEqualTo(name);

        try {
            client.deployProject(name);
        } catch (NotSuccessException ex) {
            Assertions.assertThat(ex.getJobResult().getStatus()).isEqualTo(JobStatus.DUPLICATE_RESOURCE);
        }
    }

    @Test
    public void testDeployProjectSnapshotTwice() {
        String name = "projectSnapshotToBeDeployedTwice" + Math.random();
        utils.createProject(name, null, null, SNAPSHOT_VERSION);

        DeployProjectRequest request = client.deployProject(name);
        assertThat(request.getProjectName()).isEqualTo(name);

        request = client.deployProject(name);
        assertThat(request.getProjectName()).isEqualTo(name);
    }
}
