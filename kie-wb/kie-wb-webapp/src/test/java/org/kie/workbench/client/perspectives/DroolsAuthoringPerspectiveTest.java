/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.kie.workbench.client.perspectives;

import java.util.ArrayList;
import java.util.HashMap;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.services.shared.preferences.ApplicationPreferences;
import org.kie.workbench.common.widgets.client.handlers.NewResourcesMenu;
import org.kie.workbench.common.widgets.client.menu.RepositoryMenu;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.uberfire.workbench.model.menu.MenuItem;
import org.uberfire.workbench.model.menu.Menus;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class DroolsAuthoringPerspectiveTest {

    @Mock
    NewResourcesMenu newResourcesMenu;

    @Mock
    RepositoryMenu repositoryMenu;

    @InjectMocks
    DroolsAuthoringPerspective perspective;

    private HashMap<String, String> preferences;

    @Before
    public void setUp() throws Exception {

        preferences = new HashMap<>();

        ApplicationPreferences.setUp(preferences);

        final ArrayList<MenuItem> items = new ArrayList<>();
        items.add(0,
                  mock(MenuItem.class));

        when(repositoryMenu.getMenuItems()).thenReturn(items);
        when(newResourcesMenu.getMenuItems()).thenReturn(items);
    }

    @Test
    public void inboxDisabledNotSet() throws Exception {

        Menus menus = perspective.getMenus();

        assertEquals("explore",
                     menus.getItems().get(0).getCaption());
        assertEquals(5,
                     menus.getItems().size());
    }

    @Test
    public void inboxSetButNotDisabled() throws Exception {

        preferences.put("org.guvnor.inbox.disabled",
                        "false");

        Menus menus = perspective.getMenus();

        assertEquals("explore",
                     menus.getItems().get(0).getCaption());
        assertEquals(5,
                     menus.getItems().size());
    }

    @Test
    public void inboxIsDisabled() throws Exception {

        preferences.put("org.guvnor.inbox.disabled",
                        "true");

        Menus menus = perspective.getMenus();

        assertEquals("newItem",
                     menus.getItems().get(0).getCaption());

        assertEquals(4,
                     menus.getItems().size());
    }
}