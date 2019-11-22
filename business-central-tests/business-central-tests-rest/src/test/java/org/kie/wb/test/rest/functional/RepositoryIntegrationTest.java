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

import org.assertj.core.api.Assertions;
import org.assertj.core.api.SoftAssertions;
import org.guvnor.rest.client.CloneProjectJobRequest;
import org.guvnor.rest.client.CloneProjectRequest;
import org.guvnor.rest.client.JobStatus;
import org.guvnor.rest.client.ProjectResponse;
import org.junit.BeforeClass;
import org.junit.Test;
import org.kie.wb.test.rest.RestTestBase;
import org.kie.wb.test.rest.client.NotSuccessException;
import qa.tools.ikeeper.annotation.Jira;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.wb.test.rest.functional.Utils.getProjectNames;

public class RepositoryIntegrationTest extends RestTestBase {

    private static final String SPACE = "testSpace";

    @BeforeClass
    public static void createSpace() {
        createSpace(SPACE);
    }

    @Test
    @Jira("GUVNOR-2542")
    public void testCloneRepositoryNotExistingUrl() {
        final CloneProjectRequest cloneProjectRequest = new CloneProjectRequest();
        cloneProjectRequest.setName("clonedRepoWithNotExistingUrl");
        cloneProjectRequest.setGitURL(getLocalGitRepositoryUrl() + "xyz");

        try {
            client.cloneRepository(SPACE, cloneProjectRequest);
            Assertions.fail("Operation should fail because of not valid Git URL");
        } catch (NotSuccessException ex) {
            assertThat(ex.getJobResult().getStatus()).isEqualTo(JobStatus.BAD_REQUEST);
        }
    }

    @Test
    public void testCloneRepositoryLocalFileSystem() {
        final CloneProjectRequest cloneProjectRequest = new CloneProjectRequest();
        cloneProjectRequest.setName("clonedRemoteRepo");
        cloneProjectRequest.setGitURL(getLocalGitRepositoryUrl());

        final CloneProjectJobRequest request = client.cloneRepository(SPACE, cloneProjectRequest);
        assertThat(request).isNotNull();
        assertRepositoryRequest(request.getCloneProjectRequest(), cloneProjectRequest);

        Collection<ProjectResponse> projects = client.getSpace(SPACE).getProjects();

        Assertions.assertThat(getProjectNames(projects)).contains(cloneProjectRequest.getName());
    }

    @Test
    public void testCloneRepositoryInternal() {
        final String originalRepo = "repoToBeCloned";
        createNewProject(SPACE, originalRepo, "org.team", "1.1.0");

        final CloneProjectRequest cloneProjectRequest = new CloneProjectRequest();
        cloneProjectRequest.setName("clonedInternalRepo");
        cloneProjectRequest.setGitURL("git://localhost:" + GIT_PORT + "/" + SPACE + "/" + originalRepo);

        final CloneProjectJobRequest request = client.cloneRepository(SPACE, cloneProjectRequest);
        assertThat(request).isNotNull();
        assertRepositoryRequest(request.getCloneProjectRequest(), cloneProjectRequest);

        Collection<ProjectResponse> projects = client.getSpace(SPACE).getProjects();

        Assertions.assertThat(getProjectNames(projects)).contains("clonedRemoteRepo", "repoToBeCloned", "repoToBeCloned");
    }

    private void assertRepositoryRequest(CloneProjectRequest actual,
                                         CloneProjectRequest expected) {
        assertThat(actual).isNotNull();

        SoftAssertions assertions = new SoftAssertions();
        assertions.assertThat(actual.getName()).isEqualTo(expected.getName());
        assertions.assertThat(actual.getDescription()).isEqualTo(expected.getDescription());
        assertions.assertThat(actual.getUserName()).isEqualTo(expected.getUserName());
        assertions.assertThat(actual.getPassword()).isEqualTo(expected.getPassword());
        assertions.assertThat(actual.getGitURL()).isEqualTo(expected.getGitURL());
        assertions.assertAll();
    }
}
