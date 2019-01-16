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
package org.kie.bc.client;

import javax.annotation.PostConstruct;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.dashbuilder.client.cms.screen.explorer.NavigationExplorerScreen;
import org.dashbuilder.client.navigation.NavigationManager;
import org.dashbuilder.client.navigation.event.NavTreeChangedEvent;
import org.dashbuilder.navigation.NavTree;
import org.guvnor.common.services.shared.config.AppConfigService;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.ioc.client.api.EntryPoint;
import org.kie.bc.client.navigation.NavTreeDefinitions;
import org.kie.workbench.common.workbench.client.admin.DefaultAdminPageHelper;
import org.kie.workbench.common.workbench.client.authz.PermissionTreeSetup;
import org.kie.workbench.common.workbench.client.entrypoint.DefaultWorkbenchEntryPoint;
import org.kie.workbench.common.workbench.client.error.DefaultWorkbenchErrorCallback;
import org.kie.workbench.common.workbench.client.menu.DefaultWorkbenchFeaturesMenusHelper;
import org.uberfire.client.mvp.ActivityBeansCache;
import org.uberfire.client.workbench.Workbench;
import org.uberfire.client.workbench.widgets.menu.megamenu.WorkbenchMegaMenuPresenter;
import org.uberfire.ext.security.management.client.ClientUserSystemManager;
import org.uberfire.ext.security.management.client.widgets.management.events.SaveGroupEvent;
import org.uberfire.ext.security.management.client.widgets.management.events.SaveRoleEvent;
import org.uberfire.jsbridge.client.AppFormerJsBridge;
import org.uberfire.workbench.model.menu.Menus;

import static org.uberfire.workbench.model.menu.MenuFactory.MenuBuilder;
import static org.uberfire.workbench.model.menu.MenuFactory.TopLevelMenusBuilder;

@EntryPoint
public class KieWorkbenchEntryPoint extends DefaultWorkbenchEntryPoint {

    protected DefaultWorkbenchFeaturesMenusHelper menusHelper;

    protected ClientUserSystemManager userSystemManager;

    protected WorkbenchMegaMenuPresenter menuBar;

    protected Workbench workbench;

    protected PermissionTreeSetup permissionTreeSetup;

    protected DefaultAdminPageHelper adminPageHelper;

    protected NavTreeDefinitions navTreeDefinitions;

    protected NavigationManager navigationManager;

    protected NavigationExplorerScreen navigationExplorerScreen;

    protected AppFormerJsBridge appFormerJsBridge;

    @Inject
    public KieWorkbenchEntryPoint(final Caller<AppConfigService> appConfigService,
                                  final ActivityBeansCache activityBeansCache,
                                  final DefaultWorkbenchFeaturesMenusHelper menusHelper,
                                  final ClientUserSystemManager userSystemManager,
                                  final WorkbenchMegaMenuPresenter menuBar,
                                  final Workbench workbench,
                                  final PermissionTreeSetup permissionTreeSetup,
                                  final DefaultAdminPageHelper adminPageHelper,
                                  final NavTreeDefinitions navTreeDefinitions,
                                  final NavigationManager navigationManager,
                                  final NavigationExplorerScreen navigationExplorerScreen,
                                  final DefaultWorkbenchErrorCallback defaultWorkbenchErrorCallback,
                                  final AppFormerJsBridge appFormerJsBridge) {
        super(appConfigService,
              activityBeansCache,
              defaultWorkbenchErrorCallback);
        this.menusHelper = menusHelper;
        this.userSystemManager = userSystemManager;
        this.menuBar = menuBar;
        this.workbench = workbench;
        this.permissionTreeSetup = permissionTreeSetup;
        this.adminPageHelper = adminPageHelper;
        this.navTreeDefinitions = navTreeDefinitions;
        this.navigationManager = navigationManager;
        this.navigationExplorerScreen = navigationExplorerScreen;
        this.appFormerJsBridge = appFormerJsBridge;
    }

    @PostConstruct
    public void init() {
        appFormerJsBridge.init("org.kie.bc.KIEWebapp");

        workbench.addStartupBlocker(KieWorkbenchEntryPoint.class);

        // Due to a limitation in the Menus API the number of levels in the workbench's menu bar
        // navigation tree node must be limited to 2 (see https://issues.jboss.org/browse/GUVNOR-2992)
        navigationExplorerScreen.getNavTreeEditor().setMaxLevels(NavTreeDefinitions.GROUP_WORKBENCH,
                                                              2);
    }

    @Override
    protected void initializeWorkbench() {
        permissionTreeSetup.configureTree();
        super.initializeWorkbench();
    }
    @Override
    public void setupMenu() {
        navigationManager.init(() -> {

            // Set the default nav tree
            NavTree navTree = navTreeDefinitions.buildDefaultNavTree();
            navigationManager.setDefaultNavTree(navTree);

            // Initialize the  menu bar
            initMenuBar();

            workbench.removeStartupBlocker(KieWorkbenchEntryPoint.class);
        });
    }

    @Override
    protected void setupAdminPage() {
        adminPageHelper.setup(false,
                              true,
                              false);
    }

    protected void initMenuBar() {
        refreshMenuBar();
    }

    protected void refreshMenuBar() {

        // Turn the workbench nav tree into a Menus instance that is passed as input to the workbench's menu bar
        NavTree workbenchNavTree = navigationManager.getNavTree().getItemAsTree(NavTreeDefinitions.GROUP_WORKBENCH);
        TopLevelMenusBuilder<MenuBuilder> builder = menusHelper.buildMenusFromNavTree(workbenchNavTree);

        Menus menus = builder.build();

        // Refresh the menu bar
        menuBar.clear();
        menuBar.addMenus(menus);
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
