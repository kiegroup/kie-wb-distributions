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
package org.kie.workbench.client.perspectives;

import java.util.List;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.guvnor.structure.repositories.Repository;
import org.guvnor.structure.security.RepositoryAction;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.uberfire.mvp.Command;
import org.uberfire.security.authz.ResourceActionRef;
import org.uberfire.workbench.model.menu.MenuItemCommand;
import org.uberfire.workbench.model.menu.Menus;
import org.uberfire.workbench.model.menu.impl.BaseMenuVisitor;

import static org.junit.Assert.*;

@RunWith(GwtMockitoTestRunner.class)
public class AdministrationPerspectiveTest {

    private AdministrationPerspective perspective;

    @Before
    public void setUp() {
        perspective = new AdministrationPerspective();
        perspective.init();
    }

    @Test
    public void testCreateRepoMenus() {
        Menus menus = perspective.getMenus();
        menus.accept(new BaseMenuVisitor() {

            public void visit(MenuItemCommand menuItemCommand) {

                Command command = menuItemCommand.getCommand();
                List<ResourceActionRef> actionRefList = menuItemCommand.getResourceActions();

                if (command != null) {
                    if (command.equals(perspective.getNewRepoCommand())) {
                        assertEquals(actionRefList.size(), 1);
                        ResourceActionRef resourceRef = actionRefList.get(0);
                        assertEquals(resourceRef.getResource().getResourceType(), Repository.RESOURCE_TYPE);
                        assertEquals(resourceRef.getAction(), RepositoryAction.CREATE);
                    }
                    else if (command.equals(perspective.getCloneRepoCommand())) {
                        assertEquals(actionRefList.size(), 1);
                        ResourceActionRef resourceRef = actionRefList.get(0);
                        assertEquals(resourceRef.getResource().getResourceType(), Repository.RESOURCE_TYPE);
                        assertEquals(resourceRef.getAction(), RepositoryAction.CREATE);
                    }
                }
            }
        });
    }
}
