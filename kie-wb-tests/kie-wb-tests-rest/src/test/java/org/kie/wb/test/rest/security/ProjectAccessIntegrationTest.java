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

package org.kie.wb.test.rest.security;

import org.guvnor.rest.client.ProjectRequest;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.wb.test.rest.AccessRestTestBase;
import org.kie.wb.test.rest.User;

@RunWith(Parameterized.class)
public class ProjectAccessIntegrationTest extends AccessRestTestBase {

    private static final String ORG_UNIT = "projectAccessTestOrgUnit";

    public ProjectAccessIntegrationTest(User user) {
        super(user);
    }

    @BeforeClass
    public static void createRepository() {
        createOrganizationalUnit(ORG_UNIT);
    }

    @Test
    public void testCreateProject() {
        String name = "createProjectWith" + user.getUserName();

        ProjectRequest project = new ProjectRequest();
        project.setName(name);
        project.setGroupId("groupId");
        project.setVersion("1.0.0");

        assertOperation(() -> roleClient.createProject(ORG_UNIT, project));
    }

    @Test
    public void testDeleteProject() {
        String name = "deleteProjectWith" + user.getUserName();
        createProject(name);

        assertOperation(() -> roleClient.deleteProject(name));
    }

    @Test
    public void testGetProjects() {
        assertOperation(() -> roleClient.getProjects(ORG_UNIT));
    }

    @Test
    public void testCompileProject() {
        String name = "compileProjectWith" + user.getUserName();
        createProject(name);

        assertOperation(() -> roleClient.compileProject(name));
    }

    @Test
    public void testTestProject() {
        String name = "testProjectWith" + user.getUserName();
        createProject(name);

        assertOperation(() -> roleClient.testProject(name));
    }

    @Test
    public void testInstallProject() {
        String name = "installProjectWith" + user.getUserName() + Math.random();
        createProject(name);

        assertOperation(() -> roleClient.installProject(ORG_UNIT, name));
    }

    @Test
    public void testDeployProject() {
        String name = "deployProjectWith" + user.getUserName() + Math.random();
        createProject(name);

        assertOperation(() -> roleClient.deployProject(name));
    }

    private void createProject(String name) {
        ProjectRequest project = new ProjectRequest();
        project.setName(name);
        client.createProject(ORG_UNIT, project);
    }
}
