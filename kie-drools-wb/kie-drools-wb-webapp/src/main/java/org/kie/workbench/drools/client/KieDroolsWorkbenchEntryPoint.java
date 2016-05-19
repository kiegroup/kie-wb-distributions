/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.drools.client;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.guvnor.common.services.shared.config.AppConfigService;
import org.guvnor.common.services.shared.security.KieWorkbenchACL;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.EntryPoint;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.jbpm.console.ng.ht.client.perspectives.DroolsTasksListPerspective;
import org.kie.workbench.common.screens.search.client.menu.SearchMenuBuilder;
import org.kie.workbench.common.screens.social.hp.config.SocialConfigurationService;
import org.kie.workbench.common.services.shared.security.KieWorkbenchSecurityService;
import org.kie.workbench.common.services.shared.service.PlaceManagerActivityService;
import org.kie.workbench.common.workbench.client.entrypoint.DefaultWorkbenchEntryPoint;
import org.kie.workbench.common.workbench.client.menu.DefaultWorkbenchFeaturesMenusHelper;
import org.kie.workbench.drools.client.home.HomeProducer;
import org.kie.workbench.drools.client.resources.i18n.AppConstants;
import org.uberfire.client.mvp.ActivityBeansCache;
import org.uberfire.client.workbench.Workbench;
import org.uberfire.client.workbench.widgets.menu.WorkbenchMenuBarPresenter;
import org.uberfire.ext.security.management.client.ClientUserSystemManager;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.MenuItem;
import org.uberfire.workbench.model.menu.Menus;

import static org.kie.workbench.common.workbench.client.menu.KieWorkbenchFeatures.*;

@EntryPoint
public class KieDroolsWorkbenchEntryPoint extends DefaultWorkbenchEntryPoint {

    protected AppConstants constants = AppConstants.INSTANCE;

    protected HomeProducer homeProducer;

    protected Caller<SocialConfigurationService> socialConfigurationService;

    protected DefaultWorkbenchFeaturesMenusHelper menusHelper;

    protected ClientUserSystemManager userSystemManager;

    protected WorkbenchMenuBarPresenter menuBar;

    protected SyncBeanManager iocManager;

    protected Workbench workbench;

    @Inject
    public KieDroolsWorkbenchEntryPoint( final Caller<AppConfigService> appConfigService,
                                         final Caller<KieWorkbenchSecurityService> kieSecurityService,
                                         final Caller<PlaceManagerActivityService> pmas,
                                         final KieWorkbenchACL kieACL,
                                         final ActivityBeansCache activityBeansCache,
                                         final HomeProducer homeProducer,
                                         final Caller<SocialConfigurationService> socialConfigurationService,
                                         final DefaultWorkbenchFeaturesMenusHelper menusHelper,
                                         final ClientUserSystemManager userSystemManager,
                                         final WorkbenchMenuBarPresenter menuBar,
                                         final SyncBeanManager iocManager,
                                         final Workbench workbench ) {
        super( appConfigService, kieSecurityService, pmas, kieACL, activityBeansCache );
        this.homeProducer = homeProducer;
        this.socialConfigurationService = socialConfigurationService;
        this.menusHelper = menusHelper;
        this.userSystemManager = userSystemManager;
        this.menuBar = menuBar;
        this.iocManager = iocManager;
        this.workbench = workbench;

        addCustomSecurityLoadedCallback( policy -> homeProducer.init() );
    }

    @PostConstruct
    public void init() {
        workbench.addStartupBlocker( KieDroolsWorkbenchEntryPoint.class );
    }

    @Override
    protected void setupMenu() {
        // Social services.
        socialConfigurationService.call( new RemoteCallback<Boolean>() {
            public void callback( final Boolean socialEnabled ) {

                // Wait for user management services to be initialized, if any.
                userSystemManager.waitForInitialization( () -> {

                    final boolean isUserSystemManagerActive = userSystemManager.isActive();

                    final Menus menus =
                            MenuFactory.newTopLevelMenu( constants.home() ).withItems( menusHelper.getHomeViews( socialEnabled, isUserSystemManagerActive ) ).endMenu()
                                    .newTopLevelMenu( constants.authoring() ).withRoles( kieACL.getGrantedRoles( G_AUTHORING ) ).withItems( menusHelper.getAuthoringViews() ).endMenu()
                                    .newTopLevelMenu( constants.deploy() ).withRoles( kieACL.getGrantedRoles( G_AUTHORING ) ).withItems( getDeploymentViews() ).endMenu()
                                    .newTopLevelMenu( constants.tasks() ).place( getTasksView() ).endMenu()
                                    .newTopLevelMenu( constants.extensions() ).withRoles( kieACL.getGrantedRoles( F_EXTENSIONS ) ).withItems( menusHelper.getExtensionsViews() ).endMenu()
                                    .newTopLevelCustomMenu( iocManager.lookupBean( SearchMenuBuilder.class ).getInstance() ).endMenu()
                                    .build();

                    menuBar.addMenus( menus );

                    menusHelper.addRolesMenuItems();
                    menusHelper.addWorkbenchViewModeSwitcherMenuItem();
                    menusHelper.addWorkbenchConfigurationMenuItem();
                    menusHelper.addUtilitiesMenuItems();

                    workbench.removeStartupBlocker( KieDroolsWorkbenchEntryPoint.class );
                } );

            }
        } ).isSocialEnable();
    }

    protected PlaceRequest getTasksView() {
        return new DefaultPlaceRequest( DroolsTasksListPerspective.PERSPECTIVE_ID );
    }

    protected List<MenuItem> getDeploymentViews() {
        final List<MenuItem> result = new ArrayList<>( 1 );

        result.add( MenuFactory.newSimpleItem( constants.ruleDeployments() ).withRoles( kieACL.getGrantedRoles( F_MANAGEMENT ) ).place( new DefaultPlaceRequest( "ServerManagementPerspective" ) ).endMenu().build().getItems().get( 0 ) );

        return result;
    }
}
