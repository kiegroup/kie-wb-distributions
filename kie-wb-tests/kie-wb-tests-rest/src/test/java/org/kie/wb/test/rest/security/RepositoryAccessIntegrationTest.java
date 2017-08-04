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

import org.guvnor.rest.client.RepositoryRequest;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.wb.test.rest.AccessRestTestBase;
import org.kie.wb.test.rest.User;
import org.kie.wb.test.rest.Utils;

@RunWith(Parameterized.class)
public class RepositoryAccessIntegrationTest extends AccessRestTestBase {

    private static final String ORG_UNIT = "repositoryAccessTestOrgUnit";

    public RepositoryAccessIntegrationTest(User user) {
        super(user);
    }

    @BeforeClass
    public static void createOrganizationalUnit() {
        createOrganizationalUnit(ORG_UNIT);
    }

    @Test
    public void testCreateRepository() {
        RepositoryRequest repository = new RepositoryRequest();
        repository.setName("createRepositoryWith" + user.getUserName());
        repository.setOrganizationalUnitName(ORG_UNIT);
        repository.setRequestType("new");

        assertOperation(() -> roleClient.cloneRepository(repository));
    }

    @Test
    public void testCloneRepository() {
        RepositoryRequest repository = new RepositoryRequest();
        repository.setName("cloneRepositoryWith" + user.getUserName());
        repository.setOrganizationalUnitName(ORG_UNIT);
        repository.setRequestType("clone");
        repository.setGitURL(getLocalGitRepositoryUrl());

        assertOperation(() -> roleClient.cloneRepository(repository));
    }

    @Test
    public void testDeleteRepository() {

        Utils utils = new Utils(ORG_UNIT, client);
        String name = "deleteRepositoryWith" + user.getUserName();
        utils.createProject(ORG_UNIT, name, "groupId", "1.0.0");

        assertOperation(() -> roleClient.deleteProject(name));
    }

    @Test
    public void testGetRepository() {

        Utils utils = new Utils(ORG_UNIT, client);
        String name = "getRepositoryWith" + user.getUserName();
        utils.createProject(ORG_UNIT, name, "groupId", "1.0.0");

        assertOperation(() -> roleClient.getProject(name));
    }

    @Test
    public void testGetRepositories() {
        assertOperation(roleClient::getProjects);
    }
}
