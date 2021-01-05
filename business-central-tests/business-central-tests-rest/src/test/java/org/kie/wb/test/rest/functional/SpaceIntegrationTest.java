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
import org.guvnor.rest.client.CreateProjectRequest;
import org.guvnor.rest.client.JobStatus;
import org.guvnor.rest.client.ProjectResponse;
import org.guvnor.rest.client.Space;
import org.guvnor.rest.client.SpaceRequest;
import org.junit.Ignore;
import org.junit.Test;
import org.kie.wb.test.rest.RestTestBase;
import org.kie.wb.test.rest.client.NotSuccessException;
import qa.tools.ikeeper.annotation.Jira;

public class SpaceIntegrationTest extends RestTestBase {

    private static final String DESCRIPTION = "Testing space";
    private static final String GROUP_ID = "org.kie.wb.test";
    private static final String OWNER = USER_ID;

    @Test
    public void testCreateEmptyName() {
        final Space space = new Space();
        space.setOwner(OWNER);

        try {
            client.createSpace(space);
            Assertions.fail("NotSuccessException should have been thrown");
        } catch (NotSuccessException ex) {
            Assertions.assertThat(ex.getJobResult().getStatus()).isEqualTo(JobStatus.BAD_REQUEST);
            Assertions.assertThat(ex.getJobResult().getResult()).contains("name");
        }
    }

    @Test
    public void testCreateEmptyOwner() {
        final Space space = new Space();
        space.setName("emptyOwnerSpace");

        try {
            client.createSpace(space);
            Assertions.fail("NotSuccessException should have been thrown");
        } catch (NotSuccessException ex) {
            Assertions.assertThat(ex.getJobResult().getStatus()).isEqualTo(JobStatus.BAD_REQUEST);
            Assertions.assertThat(ex.getJobResult().getResult()).contains("owner");
        }
    }

    @Test
    public void testCreateMinimal() {
        final Space space = new Space();
        space.setName("minimalSpace");
        space.setOwner(OWNER);

        testCreate(space);
    }

    @Ignore("https://issues.redhat.com/browse/JBPM-9542")
    @Test
    @Jira("GUVNOR-2542")
    public void testCreateWithDescription() {
        final Space space = new Space();
        space.setName("spaceWithDescription");
        space.setOwner(OWNER);
        space.setDescription(DESCRIPTION);

        testCreate(space);
    }

    @Test
    public void testCreateWithGroupId() {
        final Space space = new Space();
        space.setName("spaceWithGroupId");
        space.setOwner(OWNER);
        space.setDefaultGroupId(GROUP_ID);

        testCreate(space);
    }

    @Test
    public void testDeleteNotExisting() {
        try {
            client.deleteSpace("notExistingSpace");
            Assertions.fail("Space should have not been deleted");
        } catch (NotFoundException ex) {
            // Nothing
        }
    }

    @Test
    public void testDeleteExisting() {
        final String name = "spaceToBeDeleted";
        prepareSpace(name);

        client.deleteSpace(name);

        try {
            client.getSpace(name);
            Assertions.fail("Space should have been deleted");
        } catch (NotFoundException ex) {
            // expected
        }
    }

    @Test(expected = NotFoundException.class)
    public void testGetNotExisting() {
        client.getSpace("notExistingSpace");
    }

    @Test
    public void testGetExisting() {
        final String name = "getExistingSpace";
        prepareSpace(name);

        final Space space = client.getSpace(name);
        Assertions.assertThat(space.getName()).isEqualTo(name);
    }

    @Test
    public void testGetAll() {
        final String name = "oneOfManySpace";
        prepareSpace(name);

        final Collection<Space> spaces = client.getSpaces();
        Assertions.assertThat(spaces).extracting(Space::getName).contains(name);
    }

    @Test
    public void testAddProjectToNotExistingSpace() {
        final String spaceName = "notExistingSpace";

        final CreateProjectRequest createProjectRequest = new CreateProjectRequest();
        createProjectRequest.setGroupId("org.myteam");
        createProjectRequest.setVersion("1.0.0");
        createProjectRequest.setName("addToNotExistingSpaceRepo");

        try {
            client.createProject(spaceName, createProjectRequest);
            Assertions.fail("The operation should have failed because space does not exist");
        } catch (NotFoundException ex) {
            // Nothing
        }
    }

    private Space prepareSpace(String name) {
        final Space space = new Space();
        space.setName(name);
        space.setOwner(OWNER);
        space.setDescription(DESCRIPTION);
        space.setDefaultGroupId(GROUP_ID);

        client.createSpace(space);

        final Space storedSpace = client.getSpace(name);
        Assertions.assertThat(storedSpace).isNotNull();

        return space;
    }

    private void assertCreateSpaceRequest(SpaceRequest request,
                                          Space space) {
        final SoftAssertions assertions = new SoftAssertions();
        assertions.assertThat(request.getSpaceName()).isEqualTo(space.getName());
        assertions.assertThat(request.getDescription()).isEqualTo(space.getDescription());
        assertions.assertThat(request.getOwner()).isEqualTo(space.getOwner());
        assertions.assertThat(request.getDefaultGroupId()).isEqualTo(space.getDefaultGroupId());
        assertions.assertAll();
    }

    private void assertSpace(Space actual, Space expected) {
        if (expected.getDefaultGroupId() == null) {
            expected.setDefaultGroupId(expected.getName());
        }

        final SoftAssertions assertions = new SoftAssertions();
        assertions.assertThat(actual.getName()).isEqualTo(expected.getName());
        assertions.assertThat(actual.getDescription()).isEqualTo(expected.getDescription());
        assertions.assertThat(actual.getOwner()).isEqualTo(expected.getOwner());
        assertions.assertThat(actual.getDefaultGroupId()).isEqualTo(expected.getDefaultGroupId());
        if (expected.getProjects() != null) {
            assertions.assertThat(actual.getProjects())
                    .containsOnly(expected.getProjects().toArray(new ProjectResponse[]{}));
        }
        assertions.assertAll();
    }

    private void testCreate(Space space) {
        final SpaceRequest request = client.createSpace(space);
        assertCreateSpaceRequest(request, space);

        final Space clientSpace = client.getSpace(space.getName());
        assertSpace(clientSpace, space);
    }
}
