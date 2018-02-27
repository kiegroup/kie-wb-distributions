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

import org.guvnor.rest.client.Space;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.wb.test.rest.AccessRestTestBase;
import org.kie.wb.test.rest.User;

@RunWith(Parameterized.class)
public class SpaceAccessIntegrationTest extends AccessRestTestBase {

    public SpaceAccessIntegrationTest(User user) {
        super(user);
    }

    @Test
    public void testCreateSpace() {
        Space orgUnit = new Space();
        orgUnit.setName("createSpaceWith" + user.getUserName());
        orgUnit.setOwner(USER_ID);

        assertOperation(() -> roleClient.createSpace(orgUnit));
    }

    @Test
    public void testDeleteSpace() {
        String name = "deleteSpaceWith" + user.getUserName();
        createSpace(name);

        assertOperation(() -> roleClient.deleteSpace(name));
    }

    @Test
    public void testGetSpace() {
        String name = "getSpaceWith" + user.getUserName();
        createSpace(name);

        assertOperation(() -> roleClient.getSpace(name));
    }

    @Test
    public void testGetSpaces() {
        assertOperation(roleClient::getSpaces);
    }

}
