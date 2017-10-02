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

package org.kie.workbench.drools.client;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.dashbuilder.client.cms.screen.explorer.ContentExplorerScreen;
import org.dashbuilder.client.navigation.NavigationManager;
import org.dashbuilder.client.navigation.impl.NavigationManagerImpl;
import org.dashbuilder.client.navigation.widget.NavTreeEditor;
import org.dashbuilder.navigation.NavGroup;
import org.dashbuilder.navigation.NavItem;
import org.dashbuilder.navigation.NavTree;
import org.dashbuilder.navigation.service.NavigationServices;
import org.guvnor.common.services.shared.config.AppConfigService;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.workbench.client.admin.DefaultAdminPageHelper;
import org.kie.workbench.common.workbench.client.authz.PermissionTreeSetup;
import org.kie.workbench.common.workbench.client.menu.DefaultWorkbenchFeaturesMenusHelper;
import org.kie.workbench.drools.client.navigation.NavTreeDefinitions;
import org.kie.workbench.drools.client.resources.i18n.NavigationConstants;
import org.mockito.Mock;
import org.uberfire.client.authz.PerspectiveTreeProvider;
import org.uberfire.client.mvp.ActivityBeansCache;
import org.uberfire.client.workbench.Workbench;
import org.uberfire.client.workbench.widgets.menu.megamenu.WorkbenchMegaMenuPresenter;
import org.uberfire.ext.security.management.client.ClientUserSystemManager;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mvp.Command;
import org.uberfire.workbench.model.menu.MenuFactory;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class KieDroolsWorkbenchEntryPointTest {

    @Mock
    private AppConfigService appConfigService;

    @Mock
    private ActivityBeansCache activityBeansCache;

    @Mock
    private DefaultWorkbenchFeaturesMenusHelper menusHelper;

    @Mock
    protected ClientUserSystemManager userSystemManager;

    @Mock
    protected WorkbenchMegaMenuPresenter menuBar;

    @Mock
    protected Workbench workbench;

    @Mock
    protected PermissionTreeSetup permissionTreeSetup;

    @Mock
    private PerspectiveTreeProvider perspectiveTreeProvider;

    @Mock
    private SyncBeanManager syncBeanManager;

    @Mock
    private DefaultAdminPageHelper adminPageHelper;

    private NavTreeDefinitions navTreeDefinitions;

    private NavigationManager navigationManager;

    @Mock
    protected ContentExplorerScreen contentExplorerScreen;

    @Mock
    protected NavigationConstants navigationConstants;

    @Mock
    protected NavigationServices navigationServices;

    @Mock
    protected NavTreeEditor navTreeEditor;

    private KieDroolsWorkbenchEntryPoint kieWorkbenchEntryPoint;

    @Before
    public void setup() {
        navTreeDefinitions = new NavTreeDefinitions();
        navigationManager = new NavigationManagerImpl(new CallerMock<>(navigationServices),
                                                      null,
                                                      null,
                                                      null);

        doAnswer(invocationOnMock -> {
            ((Command) invocationOnMock.getArguments()[0]).execute();
            return null;
        }).when(userSystemManager).waitForInitialization(any(Command.class));

        doReturn(mock(MenuFactory.TopLevelMenusBuilder.class)).when(menusHelper).buildMenusFromNavTree(any());

        CallerMock<AppConfigService> appConfigServiceCallerMock = new CallerMock<>(appConfigService);

        kieWorkbenchEntryPoint = spy(new KieDroolsWorkbenchEntryPoint(appConfigServiceCallerMock,
                                                                      activityBeansCache,
                                                                      menusHelper,
                                                                      userSystemManager,
                                                                      menuBar,
                                                                      workbench,
                                                                      permissionTreeSetup,
                                                                      adminPageHelper,
                                                                      navTreeDefinitions,
                                                                      navigationManager,
                                                                      contentExplorerScreen));

        doNothing().when(kieWorkbenchEntryPoint).hideLoadingPopup();

        navTreeEditor = spy(new NavTreeEditor(mock(NavTreeEditor.View.class), syncBeanManager, perspectiveTreeProvider));
        when(contentExplorerScreen.getNavTreeEditor()).thenReturn(navTreeEditor);
    }

    @Test
    public void initTest() {
        kieWorkbenchEntryPoint.init();

        verify(workbench).addStartupBlocker(KieDroolsWorkbenchEntryPoint.class);
        verify(permissionTreeSetup).configureTree();
        verify(navTreeEditor).setMaxLevels(NavTreeDefinitions.GROUP_WORKBENCH, 2);
        verify(navTreeEditor).setNewDividerEnabled(NavTreeDefinitions.GROUP_WORKBENCH, false);
        verify(navTreeEditor).setNewPerspectiveEnabled(NavTreeDefinitions.GROUP_WORKBENCH, false);
        verify(navTreeEditor).setOnlyRuntimePerspectives(NavTreeDefinitions.GROUP_WORKBENCH, false);
        verify(navTreeEditor).setPerspectiveContextEnabled(NavTreeDefinitions.GROUP_WORKBENCH, false);    }

    @Test
    public void setupMenuTest() {
        kieWorkbenchEntryPoint.setupMenu();

        verify(menuBar).addMenus(any());
        verify(menusHelper).addUtilitiesMenuItems();

        verify(workbench).removeStartupBlocker(KieDroolsWorkbenchEntryPoint.class);
    }

    @Test
    public void defaultNavTreeTest() {
        kieWorkbenchEntryPoint.setupMenu();

        NavTree navTree = navigationManager.getNavTree();

        NavGroup workbench = (NavGroup) navTree.getItemById(NavTreeDefinitions.GROUP_WORKBENCH);

        NavGroup design = (NavGroup) navTree.getItemById(NavTreeDefinitions.GROUP_DESIGN);
        NavItem projects = navTree.getItemById(NavTreeDefinitions.ENTRY_PROJECTS);
        NavItem dashboard = navTree.getItemById(NavTreeDefinitions.ENTRY_DASHBOARDS);

        NavGroup devops = (NavGroup) navTree.getItemById(NavTreeDefinitions.GROUP_DEVOPS);
        NavItem execServers = navTree.getItemById(NavTreeDefinitions.ENTRY_EXECUTION_SERVERS);

        assertNotNull(workbench);
        assertNotNull(design);
        assertNotNull(devops);
        assertEquals(design.getParent(),
                     workbench);
        assertEquals(devops.getParent(),
                     workbench);

        assertNotNull(projects);
        assertNotNull(dashboard);
        assertEquals(projects.getParent(),
                     design);
        assertEquals(dashboard.getParent(),
                     design);

        assertNotNull(execServers);
        assertEquals(execServers.getParent(),
                     devops);
    }
}
