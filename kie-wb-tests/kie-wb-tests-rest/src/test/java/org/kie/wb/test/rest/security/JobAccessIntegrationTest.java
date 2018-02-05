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

import org.guvnor.rest.client.JobRequest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.wb.test.rest.AccessRestTestBase;
import org.kie.wb.test.rest.User;

@RunWith(Parameterized.class)
public class JobAccessIntegrationTest extends AccessRestTestBase {

    public JobAccessIntegrationTest(User user) {
        super(user);
    }

    @Test
    public void testGetJob() {
        JobRequest jobRequest = createSpace("getSpaceWith" + user.getUserName());

        assertOperation(() -> roleClient.getJob(jobRequest.getJobId()));
    }

    @Test
    public void testDeleteJob() {
        JobRequest jobRequest = createSpace("deleteSpaceWith" + user.getUserName());

        assertOperation(() -> roleClient.deleteJob(jobRequest.getJobId()));
    }

}
