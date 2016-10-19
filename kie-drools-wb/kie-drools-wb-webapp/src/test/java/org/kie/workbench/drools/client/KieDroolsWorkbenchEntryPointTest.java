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

import java.util.ArrayList;
import java.util.List;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.guvnor.common.services.shared.config.AppConfigService;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.social.hp.config.SocialConfigurationService;
import org.kie.workbench.common.services.shared.service.PlaceManagerActivityService;
import org.kie.workbench.common.workbench.client.authz.PermissionTreeSetup;
import org.kie.workbench.common.workbench.client.menu.DefaultWorkbenchFeaturesMenusHelper;
import org.kie.workbench.common.workbench.client.admin.DefaultAdminPageHelper;
import org.kie.workbench.drools.client.home.HomeProducer;
import org.kie.workbench.drools.client.resources.i18n.AppConstants;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.uberfire.client.mvp.ActivityBeansCache;
import org.uberfire.client.workbench.Workbench;
import org.uberfire.client.workbench.widgets.menu.WorkbenchMenuBarPresenter;
import org.uberfire.ext.security.management.client.ClientUserSystemManager;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mocks.ConstantsAnswerMock;
import org.uberfire.mocks.IocTestingUtils;
import org.uberfire.mvp.Command;
import org.uberfire.workbench.model.menu.MenuItem;
import org.uberfire.workbench.model.menu.Menus;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class KieDroolsWorkbenchEntryPointTest {

    @Mock
    private AppConfigService appConfigService;
    private CallerMock<AppConfigService> appConfigServiceCallerMock;

    @Mock
    private PlaceManagerActivityService pmas;
    private CallerMock<PlaceManagerActivityService> pmasCallerMock;

    @Mock
    private ActivityBeansCache activityBeansCache;

    @Mock
    private HomeProducer homeProducer;

    @Mock
    private SocialConfigurationService socialConfigurationService;
    private CallerMock<SocialConfigurationService> socialConfigurationServiceCallerMock;

    @Mock
    private DefaultWorkbenchFeaturesMenusHelper menusHelper;

    @Mock
    protected ClientUserSystemManager userSystemManager;

    @Mock
    protected WorkbenchMenuBarPresenter menuBar;

    @Mock
    protected SyncBeanManager iocManager;

    @Mock
    protected Workbench workbench;

    @Mock
    protected PermissionTreeSetup permissionTreeSetup;

    @Mock
    protected DefaultAdminPageHelper adminPageHelper;

    private KieDroolsWorkbenchEntryPoint kieDroolsWorkbenchEntryPoint;

    @Before
    public void setup() {
        doNothing().when( pmas ).initActivities( anyList() );

        doReturn( Boolean.TRUE ).when( socialConfigurationService ).isSocialEnable();
        doAnswer( invocationOnMock -> {
            ( ( Command ) invocationOnMock.getArguments()[0] ).execute();
            return null;
        } ).when( userSystemManager ).waitForInitialization( any( Command.class ) );

        appConfigServiceCallerMock = new CallerMock<>( appConfigService );
        socialConfigurationServiceCallerMock = new CallerMock<>( socialConfigurationService );
        pmasCallerMock = new CallerMock<>( pmas );

        kieDroolsWorkbenchEntryPoint = spy( new KieDroolsWorkbenchEntryPoint( appConfigServiceCallerMock,
                                                                              pmasCallerMock,
                                                                              activityBeansCache,
                                                                              homeProducer,
                                                                              socialConfigurationServiceCallerMock,
                                                                              menusHelper,
                                                                              userSystemManager,
                                                                              menuBar,
                                                                              iocManager,
                                                                              workbench,
                                                                              permissionTreeSetup,
                                                                              adminPageHelper ) );
        mockMenuHelper();
        mockConstants();
        IocTestingUtils.mockIocManager( iocManager );

        doNothing().when( kieDroolsWorkbenchEntryPoint ).hideLoadingPopup();
    }

    @Test
    public void initTest() {
        kieDroolsWorkbenchEntryPoint.init();

        verify( workbench ).addStartupBlocker( KieDroolsWorkbenchEntryPoint.class );
        verify( homeProducer ).init();
    }

    @Test
    public void setupMenuTest() {
        kieDroolsWorkbenchEntryPoint.setupMenu();

        ArgumentCaptor<Menus> menusCaptor = ArgumentCaptor.forClass( Menus.class );
        verify( menuBar ).addMenus( menusCaptor.capture() );

        Menus menus = menusCaptor.getValue();

        assertEquals( 6, menus.getItems().size() );

        assertEquals( kieDroolsWorkbenchEntryPoint.constants.home(), menus.getItems().get( 0 ).getCaption() );
        assertEquals( kieDroolsWorkbenchEntryPoint.constants.authoring(), menus.getItems().get( 1 ).getCaption() );
        assertEquals( kieDroolsWorkbenchEntryPoint.constants.deploy(), menus.getItems().get( 2 ).getCaption() );
        assertEquals( kieDroolsWorkbenchEntryPoint.constants.extensions(), menus.getItems().get( 3 ).getCaption() );
        assertEquals( kieDroolsWorkbenchEntryPoint.constants.Admin(), menus.getItems().get( 4 ).getCaption() );

        verify( menusHelper ).addRolesMenuItems();
        verify( menusHelper ).addWorkbenchViewModeSwitcherMenuItem();
        verify( menusHelper ).addWorkbenchConfigurationMenuItem();
        verify( menusHelper ).addUtilitiesMenuItems();

        verify( workbench ).removeStartupBlocker( KieDroolsWorkbenchEntryPoint.class );
    }

    @Test
    public void getDeploymentViewsTest() {
        List<? extends MenuItem> deploymentMenuItems = kieDroolsWorkbenchEntryPoint.getDeploymentViews();

        assertEquals( 1, deploymentMenuItems.size() );
        assertEquals( kieDroolsWorkbenchEntryPoint.constants.ExecutionServers(), deploymentMenuItems.get( 0 ).getCaption() );
    }

    private void mockMenuHelper() {
        final ArrayList<MenuItem> menuItems = new ArrayList<>();
        menuItems.add( mock( MenuItem.class ) );
        doReturn( menuItems ).when( menusHelper ).getHomeViews( anyBoolean() );
        doReturn( menuItems ).when( menusHelper ).getAuthoringViews();
        doReturn( menuItems ).when( menusHelper ).getExtensionsViews();
    }

    private void mockConstants() {
        kieDroolsWorkbenchEntryPoint.constants = mock( AppConstants.class, new ConstantsAnswerMock() );
    }
}
