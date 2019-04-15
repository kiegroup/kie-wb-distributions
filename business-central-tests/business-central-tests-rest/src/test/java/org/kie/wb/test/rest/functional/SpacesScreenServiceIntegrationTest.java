/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.wb.test.rest.functional;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.kie.wb.test.rest.RestTestBase;
import org.kie.wb.test.rest.client.SpacesScreenLibraryPreference;
import org.kie.workbench.common.screens.library.api.SpacesScreenService;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SpacesScreenServiceIntegrationTest extends RestTestBase {

    @Before
    public void before() {
        deleteAllSpaces();
    }

    @Test
    public void testGetSpaces() {
        assertEquals(0, client.spacesScreen_getSpaces().readEntity(List.class).size());
        createSpace("ASpace");
        assertEquals(1, client.spacesScreen_getSpaces().readEntity(List.class).size());
    }

    @Test
    public void testGetSpace() {
        createSpace("ASpace");
        assertEquals(200, client.spacesScreen_getSpace("ASpace").getStatus());
    }

    @Test
    public void testSaveLibraryPreference() {
        createSpace("ASpace");
        createNewProject("ASpace", "AProject", "com.AProject", "1.0.0");

        assertEquals(200, client.spacesScreen_savePreference(new SpacesScreenLibraryPreference(false, "AProject")).getStatus());
    }

    @Test
    public void testValidGroupId() {
        assertTrue(client.spacesScreen_isValidGroupId("foo.bar"));
    }

    @Test
    public void testPostSpace() {
        final SpacesScreenService.NewSpace newSpace = new SpacesScreenService.NewSpace();
        newSpace.groupId = "foo.bar";
        newSpace.name = "ASpace";

        assertEquals(201, client.spacesScreen_postSpace(newSpace).getStatus());
    }
}
