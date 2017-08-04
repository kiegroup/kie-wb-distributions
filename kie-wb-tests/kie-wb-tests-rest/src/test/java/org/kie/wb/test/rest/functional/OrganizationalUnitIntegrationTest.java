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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.ws.rs.NotFoundException;

import org.assertj.core.api.Assertions;
import org.assertj.core.api.SoftAssertions;
import org.guvnor.rest.client.CreateOrganizationalUnitRequest;
import org.guvnor.rest.client.JobStatus;
import org.guvnor.rest.client.OrganizationalUnit;
import org.guvnor.rest.client.ProjectRequest;
import org.guvnor.rest.client.ProjectResponse;
import org.guvnor.rest.client.UpdateOrganizationalUnit;
import org.junit.Test;
import org.kie.wb.test.rest.RestTestBase;
import org.kie.wb.test.rest.User;
import org.kie.wb.test.rest.client.NotSuccessException;
import qa.tools.ikeeper.annotation.Jira;

public class OrganizationalUnitIntegrationTest extends RestTestBase {

    private static final String DESCRIPTION = "Testing organizational unit";
    private static final String GROUP_ID = "org.kie.wb.test";
    private static final String OWNER = USER_ID;

    private static final String DESCRIPTION2 = "Modified testing organizational unit";
    private static final String GROUP_ID2 = "org.kie.wb.test.other";
    private static final String OWNER2 = User.NO_REST.getUserName();

    @Test
    public void testCreateEmptyName() {
        OrganizationalUnit orgUnit = new OrganizationalUnit();
        orgUnit.setOwner(OWNER);

        try {
            client.createOrganizationalUnit(orgUnit);
            Assertions.fail("NotSuccessException should have been thrown");
        } catch (NotSuccessException ex) {
            Assertions.assertThat(ex.getJobResult().getStatus()).isEqualTo(JobStatus.BAD_REQUEST);
            Assertions.assertThat(ex.getJobResult().getResult()).contains("name");
        }
    }

    @Test
    public void testCreateEmptyOwner() {
        OrganizationalUnit orgUnit = new OrganizationalUnit();
        orgUnit.setName("emptyOwnerOrgUnit");

        try {
            client.createOrganizationalUnit(orgUnit);
            Assertions.fail("NotSuccessException should have been thrown");
        } catch (NotSuccessException ex) {
            Assertions.assertThat(ex.getJobResult().getStatus()).isEqualTo(JobStatus.BAD_REQUEST);
            Assertions.assertThat(ex.getJobResult().getResult()).contains("owner");
        }
    }

    @Test
    public void testCreateMinimal() {
        OrganizationalUnit orgUnit = new OrganizationalUnit();
        orgUnit.setName("minimalOrgUnit");
        orgUnit.setOwner(OWNER);

        testCreate(orgUnit);
    }

    @Test
    @Jira("GUVNOR-2542")
    public void testCreateWithDescription() {
        OrganizationalUnit orgUnit = new OrganizationalUnit();
        orgUnit.setName("orgUnitWithDescription");
        orgUnit.setOwner(OWNER);
        orgUnit.setDescription(DESCRIPTION);

        testCreate(orgUnit);
    }

    @Test
    public void testCreateWithGroupId() {
        OrganizationalUnit orgUnit = new OrganizationalUnit();
        orgUnit.setName("orgUnitWithGroupId");
        orgUnit.setOwner(OWNER);
        orgUnit.setDefaultGroupId(GROUP_ID);

        testCreate(orgUnit);
    }

    @Test
    public void testCreateWithProjects() {
        final String originOrgUnitName = "originRepositoryOrgUnit";
        prepareOrganizationalUnit(originOrgUnitName);

        final String projectName1 = "orgUnitProject1";
        ProjectRequest projectRequest = new ProjectRequest();
        projectRequest.setName(projectName1);
        projectRequest.setGroupId("groupId");
        projectRequest.setVersion("1.0.0");
        client.createProject(originOrgUnitName, projectRequest);

        final String projectName2 = "orgUnitProject2";
        ProjectRequest project2 = new ProjectRequest();
        project2.setName(projectName2);
        project2.setGroupId("groupId");
        project2.setVersion("2.0.0");
        client.createProject(originOrgUnitName, project2);

        List<String> projects = new ArrayList<>();
        projects.add(projectName1);
        projects.add(projectName2);

        OrganizationalUnit orgUnit = new OrganizationalUnit();
        orgUnit.setName("orgUnitWithRepositories");
        orgUnit.setOwner(OWNER);
        orgUnit.setProjects(projects);

        testCreate(orgUnit);
    }

    @Test(expected = NotFoundException.class)
    public void testUpdateNotExisting() {
        UpdateOrganizationalUnit updateOrgUnit = new UpdateOrganizationalUnit();
        updateOrgUnit.setOwner(OWNER2);
        client.updateOrganizationalUnit("notExistingOrgUnit", updateOrgUnit);
    }

    @Test
    @Jira("GUVNOR-2542")
    public void testUpdateName() {
        String oldName = "nameToBeChangedOrgUnit";
        String newName = "nameChangedOrgUnit";
        prepareOrganizationalUnit(oldName);

        UpdateOrganizationalUnit updateOrgUnit = new UpdateOrganizationalUnit();
        updateOrgUnit.setName(newName);
        client.updateOrganizationalUnit(oldName, updateOrgUnit);

        OrganizationalUnit orgUnit = client.getOrganizationalUnit(newName);
        Assertions.assertThat(orgUnit).isNotNull();
    }

    @Test
    public void testUpdateOwner() {
        String name = "ownerChangeOrgUnit";
        prepareOrganizationalUnit(name);

        UpdateOrganizationalUnit updateOrgUnit = new UpdateOrganizationalUnit();
        updateOrgUnit.setOwner(OWNER2);
        client.updateOrganizationalUnit(name, updateOrgUnit);

        OrganizationalUnit orgUnit = client.getOrganizationalUnit(name);
        Assertions.assertThat(orgUnit.getOwner()).isEqualTo(OWNER2);
    }

    @Test
    @Jira("GUVNOR-2542")
    public void testUpdateDescription() {
        String name = "descriptionChangeOrgUnit";
        prepareOrganizationalUnit(name);

        UpdateOrganizationalUnit updateOrgUnit = new UpdateOrganizationalUnit();
        updateOrgUnit.setDescription(DESCRIPTION2);
        client.updateOrganizationalUnit(name, updateOrgUnit);

        OrganizationalUnit orgUnit = client.getOrganizationalUnit(name);
        Assertions.assertThat(orgUnit.getDescription()).isEqualTo(DESCRIPTION2);
    }

    @Test
    public void testUpdateGroupId() {
        String name = "groupIdChangeOrgUnit";
        prepareOrganizationalUnit(name);

        UpdateOrganizationalUnit updateOrgUnit = new UpdateOrganizationalUnit();
        updateOrgUnit.setDefaultGroupId(GROUP_ID2);
        client.updateOrganizationalUnit(name, updateOrgUnit);

        OrganizationalUnit orgUnit = client.getOrganizationalUnit(name);
        Assertions.assertThat(orgUnit.getDefaultGroupId()).isEqualTo(GROUP_ID2);
    }

    @Test(expected = NotFoundException.class)
    public void testDeleteNotExisting() {
        client.deleteOrganizationalUnit("notExistingOrgUnit");
    }

    @Test
    public void testDeleteExisting() {
        String name = "orgUnitToBeDeleted";
        prepareOrganizationalUnit(name);

        client.deleteOrganizationalUnit(name);

        try {
            client.getOrganizationalUnit(name);
            Assertions.fail("Organizational unit should have been deleted");
        } catch (NotFoundException ex) {
            // expected
        }
    }

    @Test(expected = NotFoundException.class)
    public void testGetNotExisting() {
        client.getOrganizationalUnit("notExistingOrgUnit");
    }

    @Test
    public void testGetExisting() {
        String name = "getExistingOrgUnit";
        prepareOrganizationalUnit(name);

        OrganizationalUnit orgUnit = client.getOrganizationalUnit(name);
        Assertions.assertThat(orgUnit.getName()).isEqualTo(name);
    }

    @Test
    public void testGetAll() {
        String name = "oneOfManyOrgUnit";
        prepareOrganizationalUnit(name);

        Collection<OrganizationalUnit> orgUnits = client.getOrganizationalUnits();
        for (OrganizationalUnit orgUnit : orgUnits) {
            System.out.println(orgUnit);
        }
        Assertions.assertThat(orgUnits).extracting(OrganizationalUnit::getName).contains(name);
    }

    @Test
    public void testAddRepositoryToNotExistingOrganizationalUnit() {
        String orgUnitName = "addToNotExistingOrgUnit";
        prepareOrganizationalUnit(orgUnitName);

        ProjectResponse project = new ProjectResponse();
        project.setName("addToNotExistingOrgUnitRepo");
        project.setGroupId("groupId");
        project.setVersion("1.0.0");

        try {
            client.createProject(orgUnitName, project);
            Assertions.fail("The operation should have failed because organizational unit does not exist");
        } catch (NotFoundException ex) {
            // expected
        }
    }

    @Test
    @Jira("GUVNOR-2542")
    public void testAddRepositoryToOrganizationalUnitAlreadyAdded() {
        String orgUnitName = "alreadyAddedRepoOrgUnit";
        prepareOrganizationalUnit(orgUnitName);

        String projectName = "alreadyAddedProject";
        ProjectRequest project = new ProjectRequest();
        project.setName(projectName);
        project.setGroupId("groupId");
        project.setVersion("1.0.0");

        client.createProject(orgUnitName, project);

        try {
            client.createProject(orgUnitName, project);
            Assertions.fail("The operation should not have succeeded");
        } catch (NotSuccessException ex) {
            Assertions.assertThat(ex.getJobResult().getStatus()).isEqualTo(JobStatus.BAD_REQUEST);
        }
    }

    private OrganizationalUnit prepareOrganizationalUnit(String name) {
        OrganizationalUnit orgUnit = new OrganizationalUnit();
        orgUnit.setName(name);
        orgUnit.setOwner(OWNER);
        orgUnit.setDescription(DESCRIPTION);
        orgUnit.setDefaultGroupId(GROUP_ID);

        client.createOrganizationalUnit(orgUnit);

        OrganizationalUnit storedOrgUnit = client.getOrganizationalUnit(name);
        Assertions.assertThat(storedOrgUnit).isNotNull();

        return orgUnit;
    }

    private void assertCreateOrganizationalUnitRequest(CreateOrganizationalUnitRequest request,
                                                       OrganizationalUnit orgUnit) {
        SoftAssertions assertions = new SoftAssertions();
        assertions.assertThat(request.getOrganizationalUnitName()).isEqualTo(orgUnit.getName());
        assertions.assertThat(request.getDescription()).isEqualTo(orgUnit.getDescription());
        assertions.assertThat(request.getOwner()).isEqualTo(orgUnit.getOwner());
        assertions.assertThat(request.getDefaultGroupId()).isEqualTo(orgUnit.getDefaultGroupId());
        if (orgUnit.getProjects() != null) {
            assertions.assertThat(request.getProjects())
                    .containsOnly(orgUnit.getProjects().toArray(new String[]{}));
        }
        assertions.assertAll();
    }

    private void assertOrganizationalUnit(OrganizationalUnit actual, OrganizationalUnit expected) {
        if (expected.getDefaultGroupId() == null) {
            expected.setDefaultGroupId(expected.getName());
        }

        SoftAssertions assertions = new SoftAssertions();
        assertions.assertThat(actual.getName()).isEqualTo(expected.getName());
        assertions.assertThat(actual.getDescription()).isEqualTo(expected.getDescription());
        assertions.assertThat(actual.getOwner()).isEqualTo(expected.getOwner());
        assertions.assertThat(actual.getDefaultGroupId()).isEqualTo(expected.getDefaultGroupId());
        if (expected.getProjects() != null) {
            assertions.assertThat(actual.getProjects())
                    .containsOnly(expected.getProjects().toArray(new String[]{}));
        }
        assertions.assertAll();
    }

    private void testCreate(OrganizationalUnit organizationalUnit) {
        CreateOrganizationalUnitRequest request = client.createOrganizationalUnit(organizationalUnit);
        assertCreateOrganizationalUnitRequest(request, organizationalUnit);

        OrganizationalUnit orgUnit = client.getOrganizationalUnit(organizationalUnit.getName());
        assertOrganizationalUnit(orgUnit, organizationalUnit);
    }
}
