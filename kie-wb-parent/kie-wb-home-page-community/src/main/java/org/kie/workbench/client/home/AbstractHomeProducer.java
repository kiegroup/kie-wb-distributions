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

package org.kie.workbench.client.home;

import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.client.resources.i18n.Constants;
import org.kie.workbench.common.screens.home.client.widgets.shortcut.utils.ShortcutHelper;
import org.kie.workbench.common.screens.home.model.HomeModel;
import org.kie.workbench.common.screens.home.model.HomeModelProvider;
import org.kie.workbench.common.screens.home.model.HomeShortcut;
import org.kie.workbench.common.screens.home.model.HomeShortcutLink;
import org.kie.workbench.common.screens.home.model.ModelUtils;
import org.uberfire.client.mvp.PlaceManager;

import static org.kie.workbench.common.workbench.client.PerspectiveIds.APPS;
import static org.kie.workbench.common.workbench.client.PerspectiveIds.DEPLOYMENTS;
import static org.kie.workbench.common.workbench.client.PerspectiveIds.EXECUTION_ERRORS;
import static org.kie.workbench.common.workbench.client.PerspectiveIds.JOBS;
import static org.kie.workbench.common.workbench.client.PerspectiveIds.PROCESS_DASHBOARD;
import static org.kie.workbench.common.workbench.client.PerspectiveIds.PROCESS_DEFINITIONS;
import static org.kie.workbench.common.workbench.client.PerspectiveIds.PROCESS_INSTANCES;
import static org.kie.workbench.common.workbench.client.PerspectiveIds.SERVER_MANAGEMENT;
import static org.kie.workbench.common.workbench.client.PerspectiveIds.TASKS;
import static org.kie.workbench.common.workbench.client.PerspectiveIds.TASKS_ADMIN;
import static org.uberfire.workbench.model.ActivityResourceType.PERSPECTIVE;

public abstract class AbstractHomeProducer implements HomeModelProvider {

    protected PlaceManager placeManager;
    protected TranslationService translationService;
    private ShortcutHelper shortcutHelper;

    public AbstractHomeProducer() {
        //CDI proxy
    }

    public AbstractHomeProducer(final PlaceManager placeManager,
                                final TranslationService translationService,
                                final ShortcutHelper shortcutHelper) {
        this.placeManager = placeManager;
        this.translationService = translationService;
        this.shortcutHelper = shortcutHelper;
    }

    public HomeModel get() {
        final HomeModel model = new HomeModel(translationService.format(Constants.Heading),
                                              translationService.format(Constants.SubHeading),
                                              "images/community_home_bg.jpg");

        model.addShortcut(createDesignShortcut());
        model.addShortcut(createDevOpsShortcut());
        model.addShortcut(createManageShortcut());
        model.addShortcut(createTrackShortcut());

        return model;
    }

    protected HomeShortcut createTrackShortcut() {
        final HomeShortcut track = ModelUtils.makeShortcut("pficon pficon-trend-up",
                                                           translationService.format(Constants.Track),
                                                           translationService.format(Constants.TrackDescription),
                                                           () -> placeManager.goTo(APPS),
                                                           APPS,
                                                           PERSPECTIVE);
        track.addLink(new HomeShortcutLink(translationService.format(Constants.Tasks),
                                           TASKS));
        track.addLink(new HomeShortcutLink(translationService.format(Constants.Reports),
                                           PROCESS_DASHBOARD));
        track.addLink(new HomeShortcutLink(translationService.format(Constants.BusinessDashboards),
                                           APPS));
        return track;
    }

    protected HomeShortcut createManageShortcut() {
        final HomeShortcut manage = ModelUtils.makeShortcut("fa fa-briefcase",
                                                            translationService.format(Constants.Manage),
                                                            translationService.format(Constants.ManageDescription),
                                                            () -> placeManager.goTo(PROCESS_INSTANCES),
                                                            PROCESS_INSTANCES,
                                                            PERSPECTIVE);
        manage.addLink(new HomeShortcutLink(translationService.format(Constants.ProcessDefinitions),
                                            PROCESS_DEFINITIONS));
        manage.addLink(new HomeShortcutLink(translationService.format(Constants.ProcessInstances),
                                            PROCESS_INSTANCES));
        manage.addLink(new HomeShortcutLink(translationService.format(Constants.TasksAdmin),
                                            TASKS_ADMIN));
        manage.addLink(new HomeShortcutLink(translationService.format(Constants.Jobs),
                                            JOBS));
        manage.addLink(new HomeShortcutLink(translationService.format(Constants.ExecutionErrors),
                                            EXECUTION_ERRORS));
        return manage;
    }

    protected HomeShortcut createDevOpsShortcut() {
        final HomeShortcutLink deployments = new HomeShortcutLink(translationService.format(Constants.Deployments),
                                                                  DEPLOYMENTS);
        final HomeShortcutLink servers = new HomeShortcutLink(translationService.format(Constants.Servers),
                                                              SERVER_MANAGEMENT);
        final boolean isDeploymentsAuthorized = shortcutHelper.authorize(deployments.getPerspectiveIdentifier());

        final HomeShortcut devOps = ModelUtils.makeShortcut("fa fa-gears",
                                                            translationService.format(Constants.DevOps),
                                                            getDevOpsDescription(isDeploymentsAuthorized),
                                                            () -> placeManager.goTo(SERVER_MANAGEMENT),
                                                            SERVER_MANAGEMENT,
                                                            PERSPECTIVE);
        if (isDeploymentsAuthorized) {
            devOps.addLink(deployments);
        }
        devOps.addLink(servers);

        return devOps;
    }

    private String getDevOpsDescription(final boolean isDeploymentsAuthorized) {
        if (isDeploymentsAuthorized) {
            return translationService.format(Constants.DevOpsDescription2);
        }
        return translationService.format(Constants.DevOpsDescription1);
    }

    protected abstract HomeShortcut createDesignShortcut();
}
