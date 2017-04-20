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
package org.kie.workbench.client;

import javax.annotation.PostConstruct;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.dashbuilder.client.navigation.NavigationManager;
import org.dashbuilder.client.navigation.event.NavTreeChangedEvent;
import org.dashbuilder.navigation.NavTree;
import org.guvnor.common.services.shared.config.AppConfigService;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.ioc.client.api.EntryPoint;
import org.kie.workbench.client.home.HomeProducer;
import org.kie.workbench.client.navigation.NavTreeDefinitions;
import org.kie.workbench.common.screens.social.hp.config.SocialConfigurationService;
import org.kie.workbench.common.services.shared.service.PlaceManagerActivityService;
import org.kie.workbench.common.workbench.client.admin.DefaultAdminPageHelper;
import org.kie.workbench.common.workbench.client.authz.PermissionTreeSetup;
import org.kie.workbench.common.workbench.client.entrypoint.DefaultWorkbenchEntryPoint;
import org.kie.workbench.common.workbench.client.menu.DefaultWorkbenchFeaturesMenusHelper;
import org.uberfire.client.mvp.ActivityBeansCache;
import org.uberfire.client.workbench.Workbench;
import org.uberfire.client.workbench.widgets.menu.WorkbenchMenuBarPresenter;
import org.uberfire.ext.security.management.client.ClientUserSystemManager;
import org.uberfire.ext.security.management.client.widgets.management.events.SaveGroupEvent;
import org.uberfire.ext.security.management.client.widgets.management.events.SaveRoleEvent;

import static org.uberfire.workbench.model.menu.MenuFactory.MenuBuilder;
import static org.uberfire.workbench.model.menu.MenuFactory.TopLevelMenusBuilder;

@EntryPoint
public class KieWorkbenchEntryPoint extends DefaultWorkbenchEntryPoint {

    protected HomeProducer homeProducer;

    protected Caller<SocialConfigurationService> socialConfigurationService;

    protected DefaultWorkbenchFeaturesMenusHelper menusHelper;

    protected ClientUserSystemManager userSystemManager;

    protected WorkbenchMenuBarPresenter menuBar;

    protected Workbench workbench;

    protected PermissionTreeSetup permissionTreeSetup;

    protected DefaultAdminPageHelper adminPageHelper;

    protected NavTreeDefinitions navTreeDefinitions;

    protected NavigationManager navigationManager;

    @Inject
    public KieWorkbenchEntryPoint(final Caller<AppConfigService> appConfigService,
                                  final Caller<PlaceManagerActivityService> pmas,
                                  final ActivityBeansCache activityBeansCache,
                                  final HomeProducer homeProducer,
                                  final Caller<SocialConfigurationService> socialConfigurationService,
                                  final DefaultWorkbenchFeaturesMenusHelper menusHelper,
                                  final ClientUserSystemManager userSystemManager,
                                  final WorkbenchMenuBarPresenter menuBar,
                                  final Workbench workbench,
                                  final PermissionTreeSetup permissionTreeSetup,
                                  final DefaultAdminPageHelper adminPageHelper,
                                  final NavTreeDefinitions navTreeDefinitions,
                                  final NavigationManager navigationManager) {
        super(appConfigService,
              pmas,
              activityBeansCache);
        this.homeProducer = homeProducer;
        this.socialConfigurationService = socialConfigurationService;
        this.menusHelper = menusHelper;
        this.userSystemManager = userSystemManager;
        this.menuBar = menuBar;
        this.workbench = workbench;
        this.permissionTreeSetup = permissionTreeSetup;
        this.adminPageHelper = adminPageHelper;
        this.navTreeDefinitions = navTreeDefinitions;
        this.navigationManager = navigationManager;
    }

    @PostConstruct
    public void init() {
        workbench.addStartupBlocker(KieWorkbenchEntryPoint.class);
        homeProducer.init();
        permissionTreeSetup.configureTree();
    }

    @Override
    public void setupMenu() {
        socialConfigurationService.call((Boolean socialEnabled) -> {
            navigationManager.init(() -> {

                // Set the default nav tree
                NavTree navTree = navTreeDefinitions.buildDefaultNavTree(socialEnabled);
                navigationManager.setDefaultNavTree(navTree);

                // Initialize the  menu bar
                initMenuBar();

                workbench.removeStartupBlocker(KieWorkbenchEntryPoint.class);
            });
        }).isSocialEnable();
    }

    @Override
    protected void setupAdminPage() {
        adminPageHelper.setup();
    }

    protected void initMenuBar() {
        menusHelper.addRolesMenuItems();
        menusHelper.addWorkbenchViewModeSwitcherMenuItem();
        refreshMenuBar();
    }

    protected void refreshMenuBar() {

        // Turn the workbench nav tree into a Menus instance that is passed as input to the workbench's menu bar
        NavTree workbenchNavTree = navigationManager.getNavTree().getItemAsTree(NavTreeDefinitions.GROUP_WORKBENCH);
        TopLevelMenusBuilder<MenuBuilder> builder = menusHelper.buildMenusFromNavTree(workbenchNavTree);

        // Refresh the menu bar
        menuBar.clear();
        if (builder != null) {
            menuBar.addMenus(builder.build());
        }
        menusHelper.addWorkbenchConfigurationMenuItem();
        menusHelper.addUtilitiesMenuItems();
    }

    // Listen to changes in the navigation tree

    public void onNavTreeChanged(@Observes final NavTreeChangedEvent event) {
        refreshMenuBar();
    }

    // Listen to authorization policy changes as it might impact the menu items shown

    public void onAuthzPolicyChanged(@Observes final SaveRoleEvent event) {
        refreshMenuBar();
    }

    public void onAuthzPolicyChanged(@Observes final SaveGroupEvent event) {
        refreshMenuBar();
    }
}
