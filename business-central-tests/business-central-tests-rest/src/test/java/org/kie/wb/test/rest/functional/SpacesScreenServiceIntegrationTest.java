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

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Random;

import org.guvnor.rest.client.Space;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.kie.wb.test.rest.RestTestBase;
import org.kie.wb.test.rest.client.SpacesScreenLibraryPreference;
import org.kie.workbench.common.screens.library.api.SpacesScreenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.*;

public class SpacesScreenServiceIntegrationTest extends RestTestBase {

    private Logger logger = LoggerFactory.getLogger(SpacesScreenServiceIntegrationTest.class);

    private static final String SPACE_NAME = "ASpace_";

    private String spaceName;

    @BeforeClass
    public static void cleanupSpaces() {
        deleteAllSpaces();
    }

    @Before
    public void before() {
        this.spaceName = SPACE_NAME + getRandomString();
        try {
            Collection<Space> spaces = client.getSpaces();
            spaces.forEach(space -> client.deleteSpace(space.getName()));
        } catch (Exception ex) {
            logger.error("Error ignored", ex);
        }
    }

    @After
    public void after() {
        try {
            Collection<Space> spaces = client.getSpaces();
            spaces.forEach(space -> client.deleteSpace(space.getName()));
        } catch (Exception ex) {
            logger.error("Error ignored", ex);
        }
    }

    @Test
    public void testGetSpaces() {
        assertEquals(0,
                     getSpaces().size());
        createSpace(this.spaceName);
        assertEquals(1,
                     getSpaces().size());
    }

    @Test
    public void testGetSpace() {
        createSpace(this.spaceName);
        assertEquals(200,
                     client.spacesScreen_getSpace(this.spaceName).getStatus());
    }

    @Test
    public void testSaveLibraryPreference() {
        createSpace(this.spaceName);
        createNewProject(this.spaceName,
                         "AProject",
                         "com.AProject",
                         "1.0.0");

        assertEquals(200,
                     client.spacesScreen_savePreference(new SpacesScreenLibraryPreference(false,
                                                                                          this.spaceName)).getStatus());
    }

    @Test
    public void testValidGroupId() {
        assertTrue(client.spacesScreen_isValidGroupId("foo.bar"));
    }

    @Test
    public void testPostSpace() {
        final SpacesScreenService.NewSpace newSpace = new SpacesScreenService.NewSpace();
        newSpace.groupId = "foo.bar";
        newSpace.name = SPACE_NAME;

        assertEquals(201,
                     client.spacesScreen_postSpace(newSpace).getStatus());
    }

    private List<LinkedHashMap<String, Object>> getSpaces() {
        List spaces = client.spacesScreen_getSpaces().readEntity(List.class);

        spaces.removeIf(o -> {
            LinkedHashMap<String, Object> space = (LinkedHashMap<String, Object>) o;
            return (Boolean) space.get("deleted");
        });

        return spaces;
    }
}
