/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.drools.client.navigation;

import java.util.List;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.dashbuilder.navigation.NavGroup;
import org.dashbuilder.navigation.NavItem;
import org.dashbuilder.navigation.NavTree;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;
import static org.kie.workbench.drools.client.navigation.NavTreeDefinitions.*;

@RunWith(GwtMockitoTestRunner.class)
public class NavTreeDefinitionsTest {

    private NavTreeDefinitions navTreeDefinitions;

    @Before
    public void setUp() {
        navTreeDefinitions = new NavTreeDefinitions();
    }

    @Test
    public void buildDefaultNavTreeTest() {
        final NavTree navTree = navTreeDefinitions.buildDefaultNavTree();

        final List<NavItem> rootItems = navTree.getRootItems();
        assertEquals(1, rootItems.size());

        final NavGroup workbenchItem = (NavGroup) rootItems.get(0);
        assertEquals(GROUP_WORKBENCH, workbenchItem.getId());
        assertEquals(false, workbenchItem.isModifiable());
        final List<NavItem> workbenchChildren = workbenchItem.getChildren();
        assertEquals(2, workbenchChildren.size());

        final NavGroup designItem = (NavGroup) workbenchChildren.get(0);
        assertEquals(GROUP_DESIGN, designItem.getId());
        assertEquals(true, designItem.isModifiable());
        final List<NavItem> designChildren = designItem.getChildren();
        assertEquals(1, designChildren.size());
        assertEquals(ENTRY_PROJECTS, designChildren.get(0).getId());
        assertEquals(true, designChildren.get(0).isModifiable());

        final NavGroup deployItem = (NavGroup) workbenchChildren.get(1);
        assertEquals(GROUP_DEPLOY, deployItem.getId());
        assertEquals(true, deployItem.isModifiable());
        final List<NavItem> deployChildren = deployItem.getChildren();
        assertEquals(2, deployChildren.size());
        assertEquals(ENTRY_PROVISIONING, deployChildren.get(0).getId());
        assertEquals(true, deployChildren.get(0).isModifiable());
        assertEquals(ENTRY_EXECUTION_SERVERS, deployChildren.get(1).getId());
        assertEquals(true, deployChildren.get(1).isModifiable());
    }
}
