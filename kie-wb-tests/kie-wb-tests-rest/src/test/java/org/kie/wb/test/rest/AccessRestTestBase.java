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

package org.kie.wb.test.rest;

import java.util.Arrays;
import java.util.Collection;

import org.guvnor.rest.client.JobRequest;
import org.guvnor.rest.client.JobStatus;
import org.junit.runners.Parameterized;
import org.kie.wb.test.rest.client.RestWorkbenchClient;
import org.kie.wb.test.rest.client.WorkbenchClient;

import static org.assertj.core.api.Assertions.*;

public abstract class AccessRestTestBase extends RestTestBase {

    protected final WorkbenchClient roleClient;
    protected final User user;

    @Parameterized.Parameters(name = "user: {0}")
    public static Collection<Object[]> remoteController() {
        return Arrays.asList(new Object[][]{{User.NO_REST}, {User.REST_PROJECT}});
    }

    public AccessRestTestBase(User user) {
        this.user = user;

        roleClient = RestWorkbenchClient.createWorkbenchClient(URL, user.getUserName(), user.getPassword());
    }

    private void assertOperationDoesNotFail(Operation operation) {
        Object result = operation.execute();
        if (result instanceof JobRequest) {
            JobRequest request = (JobRequest) result;
            assertThat(request.getStatus()).isIn(JobStatus.ACCEPTED, JobStatus.APPROVED, JobStatus.SUCCESS);
        }
    }

    private void assertOperationFails(Operation operation) {
        try {
            operation.execute();
            fail("Operation should have failed");
        } catch (Exception ex) {
            assertThat(ex).hasMessageContaining("Forbidden");
        }
    }

    protected void assertOperation(Operation operation) {
        if (user.isAuthorized()) {
            assertOperationDoesNotFail(operation);
        } else {
            assertOperationFails(operation);
        }
    }

    @FunctionalInterface
    protected interface Operation {
        Object execute();
    }

}
