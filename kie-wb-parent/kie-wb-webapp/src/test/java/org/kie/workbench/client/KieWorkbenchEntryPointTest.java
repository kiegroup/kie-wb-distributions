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

package org.kie.workbench.client;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.dashbuilder.client.navigation.NavigationManager;
import org.dashbuilder.client.navigation.impl.NavigationManagerImpl;
import org.dashbuilder.navigation.NavGroup;
import org.dashbuilder.navigation.NavItem;
import org.dashbuilder.navigation.NavTree;
import org.dashbuilder.navigation.service.NavigationServices;
import org.guvnor.common.services.shared.config.AppConfigService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.client.home.HomeProducer;
import org.kie.workbench.client.navigation.NavTreeDefinitions;
import org.kie.workbench.client.resources.i18n.NavigationConstants;
import org.kie.workbench.common.screens.search.client.menu.SearchMenuBuilder;
import org.kie.workbench.common.screens.social.hp.config.SocialConfigurationService;
import org.kie.workbench.common.services.shared.service.PlaceManagerActivityService;
import org.kie.workbench.common.workbench.client.admin.DefaultAdminPageHelper;
import org.kie.workbench.common.workbench.client.authz.PermissionTreeSetup;
import org.kie.workbench.common.workbench.client.menu.DefaultWorkbenchFeaturesMenusHelper;
import org.mockito.Mock;
import org.uberfire.client.mvp.ActivityBeansCache;
import org.uberfire.client.workbench.Workbench;
import org.uberfire.client.workbench.widgets.menu.WorkbenchMenuBarPresenter;
import org.uberfire.ext.security.management.client.ClientUserSystemManager;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mvp.Command;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class KieWorkbenchEntryPointTest {

    @Mock
    private AppConfigService appConfigService;

    @Mock
    private HomeProducer homeProducer;

    @Mock
    private SocialConfigurationService socialConfigurationService;

    @Mock
    private DefaultWorkbenchFeaturesMenusHelper menusHelper;

    @Mock
    protected ClientUserSystemManager userSystemManager;

    @Mock
    protected WorkbenchMenuBarPresenter menuBar;

    @Mock
    protected Workbench workbench;

    @Mock
    protected PermissionTreeSetup permissionTreeSetup;

    @Mock
    private PlaceManagerActivityService pmas;

    @Mock
    private ActivityBeansCache activityBeansCache;

    @Mock
    private DefaultAdminPageHelper adminPageHelper;

    @Mock
    protected NavigationConstants navigationConstants;

    @Mock
    protected NavigationServices navigationServices;

    @Mock
    protected SearchMenuBuilder searchMenuBuilder;

    private KieWorkbenchEntryPoint kieWorkbenchEntryPoint;

    private NavTreeDefinitions navTreeDefinitions;

    private NavigationManager navigationManager;

    @Before
    public void setup() {
        navTreeDefinitions = new NavTreeDefinitions();
        navigationManager = new NavigationManagerImpl(new CallerMock<>(navigationServices),
                                                      null,
                                                      null,
                                                      null);

        doNothing().when(pmas).initActivities(anyList());
        doReturn(Boolean.TRUE).when(socialConfigurationService).isSocialEnable();
        doAnswer(invocationOnMock -> {
            ((Command) invocationOnMock.getArguments()[0]).execute();
            return null;
        }).when(userSystemManager).waitForInitialization(any(Command.class));

        CallerMock<AppConfigService> appConfigServiceCallerMock = new CallerMock<>(appConfigService);
        CallerMock<SocialConfigurationService> socialConfigurationServiceCallerMock = new CallerMock<>(socialConfigurationService);
        CallerMock<PlaceManagerActivityService> pmasCallerMock = new CallerMock<>(pmas);

        kieWorkbenchEntryPoint = spy(new KieWorkbenchEntryPoint(appConfigServiceCallerMock,
                                                                pmasCallerMock,
                                                                activityBeansCache,
                                                                homeProducer,
                                                                socialConfigurationServiceCallerMock,
                                                                menusHelper,
                                                                userSystemManager,
                                                                menuBar,
                                                                workbench,
                                                                permissionTreeSetup,
                                                                adminPageHelper,
                                                                navTreeDefinitions,
                                                                navigationManager,
                                                                searchMenuBuilder));

        doNothing().when(kieWorkbenchEntryPoint).hideLoadingPopup();
    }

    @Test
    public void initTest() {
        kieWorkbenchEntryPoint.init();

        verify(workbench).addStartupBlocker(KieWorkbenchEntryPoint.class);
        verify(homeProducer).init();
        verify(permissionTreeSetup).configureTree();
    }

    @Test
    public void setupMenuTest() {
        kieWorkbenchEntryPoint.setupMenu();

        verify(menuBar).addMenus(any());
        verify(menusHelper).addRolesMenuItems();
        verify(menusHelper).addWorkbenchViewModeSwitcherMenuItem();
        verify(menusHelper).addWorkbenchConfigurationMenuItem();
        verify(menusHelper).addUtilitiesMenuItems();

        verify(workbench).removeStartupBlocker(KieWorkbenchEntryPoint.class);
    }

    @Test
    public void defaultNavTreeTest() {
        kieWorkbenchEntryPoint.setupMenu();

        NavTree navTree = navigationManager.getNavTree();

        NavGroup workbench = (NavGroup) navTree.getItemById(NavTreeDefinitions.GROUP_WORKBENCH);

        NavGroup home = (NavGroup) navTree.getItemById(NavTreeDefinitions.GROUP_HOME);
        NavItem homePage = navTree.getItemById(NavTreeDefinitions.ENTRY_HOME);
        NavItem preferences = navTree.getItemById(NavTreeDefinitions.ENTRY_PREFERENCES);
        NavItem timeline = navTree.getItemById(NavTreeDefinitions.ENTRY_TIMELINE);
        NavItem people = navTree.getItemById(NavTreeDefinitions.ENTRY_PEOPLE);

        NavGroup authoring = (NavGroup) navTree.getItemById(NavTreeDefinitions.GROUP_AUTHORING);
        NavItem library = navTree.getItemById(NavTreeDefinitions.ENTRY_LIBRARY);
        NavItem contributors = navTree.getItemById(NavTreeDefinitions.ENTRY_CONTRIBUTORS);
        NavItem artifacts = navTree.getItemById(NavTreeDefinitions.ENTRY_ARTIFACTS);
        NavItem administration = navTree.getItemById(NavTreeDefinitions.ENTRY_ADMINISTRATION);

        NavGroup deploy = (NavGroup) navTree.getItemById(NavTreeDefinitions.GROUP_DEPLOY);
        NavItem execServers = navTree.getItemById(NavTreeDefinitions.ENTRY_EXECUTION_SERVERS);
        NavItem jobs = navTree.getItemById(NavTreeDefinitions.ENTRY_JOBS);

        NavGroup processMgmt = (NavGroup) navTree.getItemById(NavTreeDefinitions.GROUP_PROCESS_MANAGEMENT);
        NavItem processDef = navTree.getItemById(NavTreeDefinitions.ENTRY_PROCESS_DEFINITIONS);
        NavItem processInst = navTree.getItemById(NavTreeDefinitions.ENTRY_PROCESS_INSTANCES);

        NavItem tasks = navTree.getItemById(NavTreeDefinitions.ENTRY_TASKS);
        NavItem taskAdmin = navTree.getItemById(NavTreeDefinitions.ENTRY_TASK_ADMIN);

        NavGroup dashboards = (NavGroup) navTree.getItemById(NavTreeDefinitions.GROUP_DASHBOARDS);
        NavItem businessDashboards = navTree.getItemById(NavTreeDefinitions.ENTRY_PROCESS_DASHBOARD);
        NavItem processDashboard = navTree.getItemById(NavTreeDefinitions.ENTRY_BUSINESS_DASHBOARDS);

        NavGroup extensions = (NavGroup) navTree.getItemById(NavTreeDefinitions.GROUP_EXTENSIONS);
        NavItem pluginMgmt = navTree.getItemById(NavTreeDefinitions.ENTRY_PLUGIN_MANAGEMENT);
        NavItem apps = navTree.getItemById(NavTreeDefinitions.ENTRY_APPS);
        NavItem datasets = navTree.getItemById(NavTreeDefinitions.ENTRY_DATASETS);
        NavItem datasources = navTree.getItemById(NavTreeDefinitions.ENTRY_DATA_SOURCES);

        assertNotNull(workbench);
        assertEquals(home.getParent(),
                     workbench);
        assertEquals(authoring.getParent(),
                     workbench);
        assertEquals(deploy.getParent(),
                     workbench);
        assertEquals(tasks.getParent(),
                     workbench);
        assertEquals(dashboards.getParent(),
                     workbench);
        assertEquals(extensions.getParent(),
                     workbench);

        assertNotNull(home);
        assertNotNull(homePage);
        assertNotNull(preferences);
        assertNotNull(timeline);
        assertNotNull(people);
        assertEquals(homePage.getParent(),
                     home);
        assertEquals(preferences.getParent(),
                     home);
        assertEquals(timeline.getParent(),
                     home);
        assertEquals(people.getParent(),
                     home);

        assertNotNull(library);
        assertNotNull(authoring);
        assertNotNull(contributors);
        assertNotNull(artifacts);
        assertNotNull(administration);
        assertEquals(contributors.getParent(),
                     authoring);
        assertEquals(artifacts.getParent(),
                     authoring);
        assertEquals(administration.getParent(),
                     authoring);
        assertEquals(library.getParent(),
                     authoring);

        assertNotNull(deploy);
        assertNotNull(execServers);
        assertNotNull(jobs);
        assertEquals(execServers.getParent(),
                     deploy);
        assertEquals(jobs.getParent(),
                     deploy);

        assertNotNull(processMgmt);
        assertNotNull(processDef);
        assertNotNull(processInst);
        assertNotNull(taskAdmin);
        assertEquals(processDef.getParent(), processMgmt);
        assertEquals(processInst.getParent(), processMgmt);
        assertEquals(taskAdmin.getParent(), processMgmt);

        assertNotNull(tasks);
        assertEquals(tasks.getParent(),
                     workbench);

        assertNotNull(dashboards);
        assertNotNull(businessDashboards);
        assertNotNull(processDashboard);
        assertEquals(businessDashboards.getParent(),
                     dashboards);
        assertEquals(processDashboard.getParent(),
                     dashboards);

        assertNotNull(extensions);
        assertNotNull(pluginMgmt);
        assertNotNull(apps);
        assertNotNull(datasets);
        assertNotNull(datasources);
        assertEquals(pluginMgmt.getParent(),
                     extensions);
        assertEquals(apps.getParent(),
                     extensions);
        assertEquals(datasets.getParent(),
                     extensions);
        assertEquals(datasources.getParent(),
                     extensions);
    }
}
