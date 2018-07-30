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

package org.kie.wb.test.rest.security;

import org.guvnor.rest.client.CreateProjectRequest;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.wb.test.rest.AccessRestTestBase;
import org.kie.wb.test.rest.User;

@Ignore("See https://issues.jboss.org/browse/DROOLS-2803")
@RunWith(Parameterized.class)
public class ProjectAccessIntegrationTest extends AccessRestTestBase {

    private static final String SPACE_NAME = "projectAccessTestSpace";
    private static final String PROJECT_NAME = "projectAccessTestRepository";

    public ProjectAccessIntegrationTest(User user) {
        super(user);
    }

    @BeforeClass
    public static void createRepository() {
        createSpace(SPACE_NAME);
        createNewProject(SPACE_NAME, PROJECT_NAME, "my.team", "1.0.0");
    }

    @Test
    public void testCreateProject() {
        String name = "createProjectWith" + user.getUserName();

        CreateProjectRequest createProjectRequest = new CreateProjectRequest();
        createProjectRequest.setGroupId("org.myteam");
        createProjectRequest.setVersion("1.0.0");
        createProjectRequest.setName(name);

        assertOperation(() -> roleClient.createProject(SPACE_NAME, createProjectRequest));
    }

    @Test
    public void testDeleteProject() {
        String name = "deleteProjectWith" + user.getUserName();
        createProject(name);

        assertOperation(() -> roleClient.deleteProject(SPACE_NAME, name));
    }

    @Test
    public void testGetProjects() {
        assertOperation(() -> roleClient.getProjects(SPACE_NAME));
    }

    @Test
    public void testCompileProject() {
        String name = "compileProjectWith" + user.getUserName();
        createProject(name);

        assertOperation(() -> roleClient.compileProject(SPACE_NAME, name));
    }

    @Test
    public void testTestProject() {
        String name = "testProjectWith" + user.getUserName();
        createProject(name);

        assertOperation(() -> roleClient.testProject(SPACE_NAME, name));
    }

    @Test
    public void testInstallProject() {
        String name = "installProjectWith" + user.getUserName() + Math.random();
        createProject(name);

        assertOperation(() -> roleClient.installProject(SPACE_NAME, name));
    }

    @Test
    public void testDeployProject() {
        String name = "deployProjectWith" + user.getUserName() + Math.random();
        createProject(name);

        assertOperation(() -> roleClient.deployProject(SPACE_NAME, name));
    }

    private void createProject(String name) {
        CreateProjectRequest createProjectRequest = new CreateProjectRequest();
        createProjectRequest.setGroupId("org.myteam");
        createProjectRequest.setVersion("1.0.0");
        createProjectRequest.setName(name);
        client.createProject(SPACE_NAME, createProjectRequest);
    }
}
