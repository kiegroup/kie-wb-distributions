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

import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;

import org.assertj.core.api.Assertions;
import org.assertj.core.api.SoftAssertions;
import org.guvnor.rest.client.CloneRepositoryRequest;
import org.guvnor.rest.client.JobStatus;
import org.guvnor.rest.client.ProjectResponse;
import org.guvnor.rest.client.RepositoryRequest;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.kie.wb.test.rest.RestTestBase;
import org.kie.wb.test.rest.Utils;
import org.kie.wb.test.rest.client.NotSuccessException;
import qa.tools.ikeeper.annotation.Jira;

import static org.assertj.core.api.Assertions.*;

public class RepositoryIntegrationTest extends RestTestBase {

    private static final String ORG_UNIT = "repoTestOrgUnit";
    private Utils utils;

    @BeforeClass
    public static void createOrganizationalUnit() {
        createOrganizationalUnit(ORG_UNIT);
    }

    @Before
    public void setUp() {
        utils = new Utils(ORG_UNIT, client);
    }

    @Test(expected = BadRequestException.class)
    public void testCloneRepositoryEmptyRequestType() {
        RepositoryRequest repository = new RepositoryRequest();
        repository.setName("emptyRequestTypeRepo");
        repository.setOrganizationalUnitName(ORG_UNIT);

        client.cloneRepository(repository);
    }

    private void testCloneRepository(RepositoryRequest repository) {
        CloneRepositoryRequest request = client.cloneRepository(repository);
        assertThat(request).isNotNull();
        assertRepositoryRequest(request.getRepository(), repository);

        ProjectResponse response = client.getProject(repository.getName());
        assertRepositoryResponse(response, repository);
    }

    @Test
    public void testCreateRepositoryMinimal() {
        RepositoryRequest repository = new RepositoryRequest();
        repository.setName("minimalRepo");
        repository.setOrganizationalUnitName(ORG_UNIT);
        repository.setRequestType("new");

        testCloneRepository(repository);
    }

    @Test
    @Jira("GUVNOR-2542")
    public void testCreateRepositoryWithDescription() {
        RepositoryRequest repository = new RepositoryRequest();
        repository.setName("repoWithDescription");
        repository.setOrganizationalUnitName(ORG_UNIT);
        repository.setRequestType("new");
        repository.setDescription("Some kind of description");

        testCloneRepository(repository);
    }

    @Test
    public void testCreateRepositoryWithGitUrl() {
        RepositoryRequest repository = new RepositoryRequest();
        repository.setName("createdRepoWithGitUrl");
        repository.setOrganizationalUnitName(ORG_UNIT);
        repository.setRequestType("new");
        repository.setGitURL(getLocalGitRepositoryUrl());

        testCloneRepository(repository);
    }

    @Test
    @Jira("GUVNOR-2542")
    public void testCloneRepositoryEmptyUrl() {
        RepositoryRequest repository = new RepositoryRequest();
        repository.setName("clonedRepoWithEmptyUrl");
        repository.setOrganizationalUnitName(ORG_UNIT);
        repository.setRequestType("clone");

        try {
            client.cloneRepository(repository);
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
        repository.setGitURL(getLocalGitRepositoryUrl() + "xyz");

        try {
            client.cloneRepository(repository);
            Assertions.fail("Operation should fail because of not valid Git URL");
        } catch (NotSuccessException ex) {
            assertThat(ex.getJobResult().getStatus()).isEqualTo(JobStatus.BAD_REQUEST);
        }
    }

    @Test
    public void testCloneRepositoryLocalFileSystem() {
        RepositoryRequest repository = new RepositoryRequest();
        repository.setName("clonedRemoteRepo");
        repository.setOrganizationalUnitName(ORG_UNIT);
        repository.setRequestType("clone");
        repository.setGitURL(getLocalGitRepositoryUrl());

        testCloneRepository(repository);
    }

    @Test
    public void testCloneRepositoryInternal() {
        String originalProject = "repoToBeCloned";
        utils.createProject(ORG_UNIT, originalProject, "groupId", "1.0.0");

        RepositoryRequest repository = new RepositoryRequest();
        repository.setName("clonedInternalRepo");
        repository.setOrganizationalUnitName(ORG_UNIT);
        repository.setRequestType("clone");
        repository.setGitURL("git://localhost:9418/" + originalProject);

        testCloneRepository(repository);
    }

    @Jira("GUVNOR-2542")
    @Test(expected = NotFoundException.class)
    public void testDeleteRepositoryNotExisting() {
        client.deleteProject("notExistingRepo");
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

    private void assertRepositoryResponse(ProjectResponse actual, RepositoryRequest expected) {
        assertThat(actual).isNotNull();

        SoftAssertions assertions = new SoftAssertions();
        assertions.assertThat(actual.getName()).isEqualTo(expected.getName());
        assertions.assertThat(actual.getDescription()).isEqualTo(expected.getDescription());
        assertions.assertAll();
    }
}
