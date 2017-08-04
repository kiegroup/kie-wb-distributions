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

import org.guvnor.rest.client.OrganizationalUnit;
import org.guvnor.rest.client.UpdateOrganizationalUnit;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.wb.test.rest.AccessRestTestBase;
import org.kie.wb.test.rest.User;
import org.kie.wb.test.rest.Utils;

@RunWith(Parameterized.class)
public class OrganizationalUnitAccessIntegrationTest extends AccessRestTestBase {

    private Utils utils;

    public OrganizationalUnitAccessIntegrationTest(User user) {
        super(user);
    }

    @Test
    public void testCreateOrganizationalUnit() {
        OrganizationalUnit orgUnit = new OrganizationalUnit();
        orgUnit.setName("createOrgUnitWith" + user.getUserName());
        orgUnit.setOwner(USER_ID);

        assertOperation(() -> roleClient.createOrganizationalUnit(orgUnit));
    }

    @Test
    public void testUpdateOrganizationalUnit() {
        String name = "updateOrgUnitWith" + user.getUserName();
        createOrganizationalUnit(name);

        UpdateOrganizationalUnit updateOrgUnit = new UpdateOrganizationalUnit();
        updateOrgUnit.setOwner(user.getUserName());

        assertOperation(() -> roleClient.updateOrganizationalUnit(name, updateOrgUnit));
    }

    @Test
    public void testDeleteOrganizationalUnit() {
        String name = "deleteOrgUnitWith" + user.getUserName();
        createOrganizationalUnit(name);

        assertOperation(() -> roleClient.deleteOrganizationalUnit(name));
    }

    @Test
    public void testGetOrganizationalUnit() {
        String name = "getOrgUnitWith" + user.getUserName();
        createOrganizationalUnit(name);

        assertOperation(() -> roleClient.getOrganizationalUnit(name));
    }

    @Test
    public void testGetOrganizationalUnits() {
        assertOperation(roleClient::getOrganizationalUnits);
    }

    @Test
    public void testAddRepositoryToOrganizationalUnit() {

        String originOrgUnitName = "originAddRepoOrgUnitWith" + user.getUserName();

        Utils utils = new Utils(originOrgUnitName, client);

        createOrganizationalUnit(originOrgUnitName);

        String projectName = "addToOrgUnitRepoWith" + user.getUserName();
        utils.createProject(originOrgUnitName, projectName, "groupId", "1.0.0");

        String orgUnitName = "addRepoOrgUnitWith" + user.getUserName();
        createOrganizationalUnit(orgUnitName);

        assertOperation(() -> roleClient.addProjectToOrganizationalUnit(orgUnitName, projectName));
    }

    @Test
    public void testRemoveRepositoryFromOrganizationalUnit() {
        String orgUnitName = "originRemoveFromOrgUnitWith" + user.getUserName();
        createOrganizationalUnit(orgUnitName);

        Utils utils = new Utils(orgUnitName, client);

        String projectName = "removeFromOrgUnitRepoWith" + user.getUserName();
        utils.createProject(orgUnitName, projectName, "groupId","1.0.0");

        assertOperation(() -> roleClient.removeProjectFromOrganizationalUnit(orgUnitName, projectName));
    }
}
