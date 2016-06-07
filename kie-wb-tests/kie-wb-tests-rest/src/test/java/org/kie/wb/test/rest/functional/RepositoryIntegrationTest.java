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

import org.assertj.core.api.Assertions;
import org.assertj.core.api.SoftAssertions;
import org.guvnor.rest.client.CreateOrCloneRepositoryRequest;
import org.guvnor.rest.client.JobStatus;
import org.guvnor.rest.client.OrganizationalUnit;
import org.guvnor.rest.client.RepositoryRequest;
import org.guvnor.rest.client.RepositoryResponse;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.kie.wb.test.rest.RestTestBase;
import org.kie.wb.test.rest.client.NotSuccessException;
import org.kie.wb.test.rest.exception.BadRequestException;
import org.kie.wb.test.rest.exception.NotFoundException;
import qa.tools.ikeeper.annotation.Jira;

import static org.assertj.core.api.Assertions.*;

public class RepositoryIntegrationTest extends RestTestBase {

    private static final String ORG_UNIT = "repoTestOrgUnit";
    private static final String GIT_URL_REMOTE = "https://github.com/droolsjbpm/jbpm-playground.git";

    @BeforeClass
    public static void createOrganizationalUnit() {
        OrganizationalUnit orgUnit = new OrganizationalUnit();
        orgUnit.setName(ORG_UNIT);
        orgUnit.setOwner(USER_ID);

        client.createOrganizationalUnit(orgUnit);

        deleteAllRepositories();
    }

    @AfterClass
    public static void deleteOrganizationalUnit() {
        deleteAllRepositories();

        client.deleteOrganizationalUnit(ORG_UNIT);
    }

    @Test
    public void testCreateRepositoryEmptyName() {
        RepositoryRequest repository = new RepositoryRequest();
        repository.setOrganizationalUnitName(ORG_UNIT);
        repository.setRequestType("new");

        try {
            client.createOrCloneRepository(repository);
            Assertions.fail("Operation should fail because of missing repository name");
        } catch (NotSuccessException ex) {
            assertThat(ex.getJobResult().getStatus()).isEqualTo(JobStatus.BAD_REQUEST);
            assertThat(ex.getJobResult().getResult()).contains("name");
        }
    }

    @Jira("GUVNOR-2542")
    @Test(expected = BadRequestException.class)
    public void testCreateRepositoryEmptyOrganizationalUnit() {
        RepositoryRequest repository = new RepositoryRequest();
        repository.setName("emptyOrgUnitRepo");
        repository.setRequestType("new");

        client.createOrCloneRepository(repository);
    }

    @Test(expected = BadRequestException.class)
    public void testCreateRepositoryEmptyRequestType() {
        RepositoryRequest repository = new RepositoryRequest();
        repository.setName("emptyRequestTypeRepo");
        repository.setOrganizationalUnitName(ORG_UNIT);

        client.createOrCloneRepository(repository);
    }

    private void testCreateOrCloneRepository(RepositoryRequest repository) {
        CreateOrCloneRepositoryRequest request = client.createOrCloneRepository(repository);
        assertThat(request).isNotNull();
        assertRepositoryRequest(request.getRepository(), repository);

        RepositoryResponse response = client.getRepository(repository.getName());
        assertRepositoryResponse(response, repository);
    }

    @Test
    public void testCreateRepositoryMinimal() {
        RepositoryRequest repository = new RepositoryRequest();
        repository.setName("minimalRepo");
        repository.setOrganizationalUnitName(ORG_UNIT);
        repository.setRequestType("new");

        testCreateOrCloneRepository(repository);
    }

    @Test
    @Jira("GUVNOR-2542")
    public void testCreateRepositoryWithDescription() {
        RepositoryRequest repository = new RepositoryRequest();
        repository.setName("repoWithDescription");
        repository.setOrganizationalUnitName(ORG_UNIT);
        repository.setRequestType("new");
        repository.setDescription("Some kind of description");

        testCreateOrCloneRepository(repository);
    }

    @Test
    public void testCreateRepositoryWithGitUrl() {
        RepositoryRequest repository = new RepositoryRequest();
        repository.setName("createdRepoWithGitUrl");
        repository.setOrganizationalUnitName(ORG_UNIT);
        repository.setRequestType("new");
        repository.setGitURL(GIT_URL_REMOTE);

        testCreateOrCloneRepository(repository);
    }

    @Test
    @Jira("GUVNOR-2542")
    public void testCloneRepositoryEmptyUrl() {
        RepositoryRequest repository = new RepositoryRequest();
        repository.setName("clonedRepoWithEmptyUrl");
        repository.setOrganizationalUnitName(ORG_UNIT);
        repository.setRequestType("clone");

        try {
            client.createOrCloneRepository(repository);
            Assertions.fail("Operation should fail because of missing Git URL");
        } catch (NotSuccessException ex) {
            assertThat(ex.getJobResult().getStatus()).isEqualTo(JobStatus.BAD_REQUEST);
        }
    }

    @Test
    @Jira("GUVNOR-2542")
    public void testCloneRepositoryNotExistingUrl() {
        RepositoryRequest repository = new RepositoryRequest();
        repository.setName("clonedRepoWithNotExistingUrl");
        repository.setOrganizationalUnitName(ORG_UNIT);
        repository.setRequestType("clone");
        repository.setGitURL(GIT_URL_REMOTE + "xyz");

        try {
            client.createOrCloneRepository(repository);
            Assertions.fail("Operation should fail because of not valid Git URL");
        } catch (NotSuccessException ex) {
            assertThat(ex.getJobResult().getStatus()).isEqualTo(JobStatus.BAD_REQUEST);
        }
    }

    @Test
    public void testCloneRepositoryRemote() {
        RepositoryRequest repository = new RepositoryRequest();
        repository.setName("clonedRemoteRepo");
        repository.setOrganizationalUnitName(ORG_UNIT);
        repository.setRequestType("clone");
        repository.setGitURL(GIT_URL_REMOTE);

        testCreateOrCloneRepository(repository);
    }

    private RepositoryRequest prepareRepository(String name) {
        RepositoryRequest repository = new RepositoryRequest();
        repository.setName(name);
        repository.setOrganizationalUnitName(ORG_UNIT);
        repository.setRequestType("new");

        return client.createOrCloneRepository(repository).getRepository();
    }

    @Test
    public void testCloneRepositoryInternal() {
        String originalRepo = "repoToBeCloned";
        prepareRepository(originalRepo);

        RepositoryRequest repository = new RepositoryRequest();
        repository.setName("clonedInternalRepo");
        repository.setOrganizationalUnitName(ORG_UNIT);
        repository.setRequestType("clone");
        repository.setGitURL("git://localhost:9418/" + originalRepo);

        testCreateOrCloneRepository(repository);
    }

    @Test
    public void testDeleteRepository() {
        String name = "repoToBeDeleted";
        prepareRepository(name);

        client.deleteRepository(name);

        try {
            client.getRepository(name);
            Assertions.fail("Repository should have been deleted");
        } catch (NotFoundException ex) {
            // expected, repository has been deleted
        }
    }

    @Jira("GUVNOR-2542")
    @Test(expected = NotFoundException.class)
    public void testDeleteRepositoryNotExisting() {
        client.deleteRepository("notExistingRepo");
    }

    @Test
    public void testGetExistingRepository() {
        String name = "getExistingRepo";
        prepareRepository(name);

        RepositoryResponse repository = client.getRepository(name);
        assertThat(repository.getName()).isEqualTo(name);
    }

    @Test(expected = NotFoundException.class)
    public void testGetNotExistingRepository() {
        client.getRepository("notExistingRepo");
    }

    @Test
    public void testGetRepositories() {
        String name = "oneOfManyRepos";
        prepareRepository(name);

        Collection<RepositoryResponse> repositories = client.getRepositories();
        assertThat(repositories).extracting(RepositoryResponse::getName).contains(name);
    }

    private void assertRepositoryRequest(RepositoryRequest actual, RepositoryRequest expected) {
        assertThat(actual).isNotNull();

        SoftAssertions assertions = new SoftAssertions();
        assertions.assertThat(actual.getName()).isEqualTo(expected.getName());
        assertions.assertThat(actual.getDescription()).isEqualTo(expected.getDescription());
        assertions.assertThat(actual.getOrganizationalUnitName())
                .isEqualTo(expected.getOrganizationalUnitName());
        assertions.assertThat(actual.getUserName()).isEqualTo(expected.getUserName());
        assertions.assertThat(actual.getPassword()).isEqualTo(expected.getPassword());
        assertions.assertThat(actual.getRequestType()).isEqualTo(expected.getRequestType());
        assertions.assertThat(actual.getGitURL()).isEqualTo(expected.getGitURL());
        assertions.assertAll();
    }

    private void assertRepositoryResponse(RepositoryResponse actual, RepositoryRequest expected) {
        assertThat(actual).isNotNull();

        SoftAssertions assertions = new SoftAssertions();
        assertions.assertThat(actual.getName()).isEqualTo(expected.getName());
        assertions.assertThat(actual.getDescription()).isEqualTo(expected.getDescription());
        assertions.assertThat(actual.getUserName()).isEqualTo(expected.getUserName());
        assertions.assertThat(actual.getPassword()).isEqualTo(expected.getPassword());
        assertions.assertThat(actual.getRequestType()).isNull();
        assertions.assertAll();
    }

}
