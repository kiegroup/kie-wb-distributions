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
import org.guvnor.rest.client.RepositoryRequest;
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
    public void testCreateWithRepositories() {
        final String originOrgUnitName = "originRepositoryOrgUnit";
        prepareOrganizationalUnit(originOrgUnitName);

        final String repositoryName1 = "orgUnitRepository1";
        RepositoryRequest repository = new RepositoryRequest();
        repository.setName(repositoryName1);
        repository.setOrganizationalUnitName(originOrgUnitName);
        repository.setRequestType("new");
        client.createOrCloneRepository(repository);

        final String repositoryName2 = "orgUnitRepository2";
        RepositoryRequest repository2 = new RepositoryRequest();
        repository2.setName(repositoryName2);
        repository2.setOrganizationalUnitName(originOrgUnitName);
        repository2.setRequestType("new");
        client.createOrCloneRepository(repository2);

        List<String> repositories = new ArrayList<>();
        repositories.add(repositoryName1);
        repositories.add(repositoryName2);

        OrganizationalUnit orgUnit = new OrganizationalUnit();
        orgUnit.setName("orgUnitWithRepositories");
        orgUnit.setOwner(OWNER);
        orgUnit.setRepositories(repositories);

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

        RepositoryRequest repository = new RepositoryRequest();
        repository.setName("addToNotExistingOrgUnitRepo");
        repository.setOrganizationalUnitName(orgUnitName);
        repository.setRequestType("new");
        client.createOrCloneRepository(repository);

        try {
            client.addRepositoryToOrganizationalUnit("notExistingOrgUnit", repository.getName());
            Assertions.fail("The operation should have failed because organizational unit does not exist");
        } catch (NotFoundException ex) {
            // expected
        }
    }

    @Test
    public void testAddNotExistingRepositoryToOrganizationalUnit() {
        String orgUnitName = "addToNotExistingRepoOrgUnit";
        prepareOrganizationalUnit(orgUnitName);

        try {
            client.addRepositoryToOrganizationalUnit(orgUnitName, "notExistingRepo");
            Assertions.fail("The operation should have failed because repository does not exist");
        } catch (NotFoundException ex) {
            // expected
        }
    }

    @Test
    @Jira("GUVNOR-2542")
    public void testAddRepositoryToOrganizationalUnitAlreadyAdded() {
        String orgUnitName = "alreadyAddedRepoOrgUnit";
        prepareOrganizationalUnit(orgUnitName);

        String repoName = "alreadyAddedRepo";
        RepositoryRequest repository = new RepositoryRequest();
        repository.setName(repoName);
        repository.setOrganizationalUnitName(orgUnitName);
        repository.setRequestType("new");
        client.createOrCloneRepository(repository);

        try {
            client.addRepositoryToOrganizationalUnit(orgUnitName, repoName);
            Assertions.fail("The operation should not have succeeded");
        } catch (NotSuccessException ex) {
            Assertions.assertThat(ex.getJobResult().getStatus()).isEqualTo(JobStatus.BAD_REQUEST);
        }
    }

    @Test
    public void testAddRepositoryToOrganizationalUnit() {
        String originOrgUnitName = "originAddRepoOrgUnit";
        prepareOrganizationalUnit(originOrgUnitName);

        String repoName = "addToOrgUnitRepo";
        RepositoryRequest repository = new RepositoryRequest();
        repository.setName(repoName);
        repository.setOrganizationalUnitName(originOrgUnitName);
        repository.setRequestType("new");
        client.createOrCloneRepository(repository);

        String orgUnitName = "addRepoOrgUnit";
        prepareOrganizationalUnit(orgUnitName);

        client.addRepositoryToOrganizationalUnit(orgUnitName, repoName);

        OrganizationalUnit orgUnit = client.getOrganizationalUnit(orgUnitName);
        Assertions.assertThat(orgUnit.getRepositories()).contains(repoName);
    }

    @Test
    public void testRemoveRepositoryFromNotExistingOrganizationalUnit() {
        String orgUnitName = "removeFromNotExistingOrgUnit";
        prepareOrganizationalUnit(orgUnitName);

        RepositoryRequest repository = new RepositoryRequest();
        repository.setName("removeFromNotExistingOrgUnitRepo");
        repository.setOrganizationalUnitName(orgUnitName);
        repository.setRequestType("new");
        client.createOrCloneRepository(repository);

        try {
            client.removeRepositoryFromOrganizationalUnit("notExistingOrgUnit", repository.getName());
            Assertions.fail("The operation should have failed because organizational unit does not exist");
        } catch (NotFoundException ex) {
            // expected
        }
    }

    @Test
    public void testRemoveNotExistingRepositoryFromOrganizationalUnit() {
        String orgUnitName = "removeFromNotExistingRepoOrgUnit";
        prepareOrganizationalUnit(orgUnitName);

        try {
            client.addRepositoryToOrganizationalUnit(orgUnitName, "notExistingRepo");
            Assertions.fail("The operation should have failed because repository does not exist");
        } catch (NotFoundException ex) {
            // expected
        }
    }

    @Test
    @Jira("GUVNOR-2542")
    public void testRemoveNotAddedRepositoryFromOrganizationalUnit() {
        String originOrgUnitName = "originRemoveFromNotAddedOrgUnit";
        prepareOrganizationalUnit(originOrgUnitName);

        String repoName = "removeFromNotAddedRepo";
        RepositoryRequest repository = new RepositoryRequest();
        repository.setName(repoName);
        repository.setOrganizationalUnitName(originOrgUnitName);
        repository.setRequestType("new");
        client.createOrCloneRepository(repository);

        String orgUnitName = "removeFromNotAddedOrgUnit";
        prepareOrganizationalUnit(orgUnitName);

        try {
            client.removeRepositoryFromOrganizationalUnit(orgUnitName, repoName);
            Assertions.fail("Operation should have failed");
        } catch (NotSuccessException ex) {
            Assertions.assertThat(ex.getJobResult().getStatus()).isEqualTo(JobStatus.BAD_REQUEST);
        }
    }

    @Test
    public void testRemoveRepositoryFromOrganizationalUnit() {
        String orgUnitName = "originRemoveFromOrgUnit";
        prepareOrganizationalUnit(orgUnitName);

        String repoName = "removeFromOrgUnitRepo";
        RepositoryRequest repository = new RepositoryRequest();
        repository.setName(repoName);
        repository.setOrganizationalUnitName(orgUnitName);
        repository.setRequestType("new");
        client.createOrCloneRepository(repository);

        client.removeRepositoryFromOrganizationalUnit(orgUnitName, repoName);

        OrganizationalUnit orgUnit = client.getOrganizationalUnit(orgUnitName);
        Assertions.assertThat(orgUnit.getRepositories()).doesNotContain(repoName);
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
        if (orgUnit.getRepositories() != null) {
            assertions.assertThat(request.getRepositories())
                    .containsOnly(orgUnit.getRepositories().toArray(new String[]{}));
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
        if (expected.getRepositories() != null) {
            assertions.assertThat(actual.getRepositories())
                    .containsOnly(expected.getRepositories().toArray(new String[]{}));
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
