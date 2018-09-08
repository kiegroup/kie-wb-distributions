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

import org.assertj.core.api.Assertions;
import org.guvnor.rest.client.CloneProjectRequest;
import org.guvnor.rest.client.JobRequest;
import org.guvnor.rest.client.JobResult;
import org.guvnor.rest.client.JobStatus;
import org.junit.BeforeClass;
import org.junit.Test;
import org.kie.wb.test.rest.RestTestBase;
import org.kie.wb.test.rest.client.RestWorkbenchClient;
import org.kie.wb.test.rest.client.WorkbenchClient;

public class JobIntegrationTest extends RestTestBase {

    private static final String SPACE_NAME = "jobTestSpace";

    private static WorkbenchClient asyncClient;

    @BeforeClass
    public static void setUp() {
        deleteAllSpaces();

        createSpace(SPACE_NAME);

        asyncClient = RestWorkbenchClient.createAsyncWorkbenchClient(URL, USER_ID, PASSWORD);
    }

    private JobRequest cloneRepositoryAsync(String name) {
        CloneProjectRequest cloneProjectRequest = new CloneProjectRequest();
        cloneProjectRequest.setName(name);
        cloneProjectRequest.setGitURL(getLocalGitRepositoryUrl());

        return asyncClient.cloneRepository(SPACE_NAME, cloneProjectRequest);
    }

    @Test
    public void testGet() {
        JobRequest jobRequest = createSpace("getSpace");

        JobResult jobResult = client.getJob(jobRequest.getJobId());
        Assertions.assertThat(jobResult.getStatus()).isNotEqualTo(JobStatus.GONE);
    }

    @Test
    public void testDelete() {
        JobRequest jobRequest = cloneRepositoryAsync("deleteJobRepository");

        JobResult jobResult = client.getJob(jobRequest.getJobId());
        Assertions.assertThat(jobResult.getStatus()).isNotEqualTo(JobStatus.GONE);

        jobResult = client.deleteJob(jobRequest.getJobId());
        Assertions.assertThat(jobResult.getStatus()).isEqualTo(JobStatus.GONE);

        jobResult = client.getJob(jobRequest.getJobId());
        Assertions.assertThat(jobResult.getStatus()).isEqualTo(JobStatus.GONE);
    }

    @Test
    public void testDeleteNotExisting() {
        JobResult jobResult = client.deleteJob("notExistingJob");
        Assertions.assertThat(jobResult.getStatus()).isEqualTo(JobStatus.GONE);
    }

    @Test
    public void testGetNotExisting() {
        JobResult jobResult = client.getJob("notExistingJob");
        Assertions.assertThat(jobResult.getStatus()).isEqualTo(JobStatus.GONE);
    }
}
