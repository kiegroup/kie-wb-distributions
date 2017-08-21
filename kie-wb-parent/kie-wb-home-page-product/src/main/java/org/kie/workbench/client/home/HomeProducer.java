/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.kie.workbench.client.home;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.client.resources.i18n.Constants;
import org.kie.workbench.common.screens.home.model.HomeModel;
import org.kie.workbench.common.screens.home.model.HomeModelProvider;
import org.kie.workbench.common.screens.home.model.HomeShortcut;
import org.kie.workbench.common.screens.home.model.HomeShortcutLink;
import org.kie.workbench.common.screens.home.model.ModelUtils;
import org.uberfire.client.mvp.PlaceManager;

import static org.kie.workbench.common.workbench.client.PerspectiveIds.*;
import static org.uberfire.workbench.model.ActivityResourceType.PERSPECTIVE;

@ApplicationScoped
public class HomeProducer implements HomeModelProvider {

    @Inject
    private PlaceManager placeManager;

    @Inject
    private TranslationService translationService;

    public HomeModel get() {
        final HomeModel model = new HomeModel(translationService.format(Constants.Heading),
                                              translationService.format(Constants.SubHeading),
                                              "images/product_home_bg.svg");

        final HomeShortcut design = ModelUtils.makeShortcut("pficon-blueprint",
                                                            translationService.format(Constants.Design),
                                                            translationService.format(Constants.DesignDescription),
                                                            () -> placeManager.goTo(LIBRARY),
                                                            LIBRARY,
                                                            PERSPECTIVE);
        design.addLink(new HomeShortcutLink(translationService.format(Constants.Projects),
                                            LIBRARY));
        design.addLink(new HomeShortcutLink(translationService.format(Constants.Dashboards),
                                            BUSINESS_DASHBOARDS));

        final HomeShortcut devOps = ModelUtils.makeShortcut("fa-gears",
                                                            translationService.format(Constants.DevOps),
                                                            translationService.format(Constants.DevOpsDescription),
                                                            () -> placeManager.goTo(SERVER_MANAGEMENT),
                                                            SERVER_MANAGEMENT,
                                                            PERSPECTIVE);
        devOps.addLink(new HomeShortcutLink(translationService.format(Constants.Deployments),
                                            DEPLOYMENTS));
        devOps.addLink(new HomeShortcutLink(translationService.format(Constants.Servers),
                                            SERVER_MANAGEMENT));

        final HomeShortcut manage = ModelUtils.makeShortcut("fa-briefcase",
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

        final HomeShortcut track = ModelUtils.makeShortcut("pficon-trend-up",
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

        model.addShortcut(design);
        model.addShortcut(devOps);
        model.addShortcut(manage);
        model.addShortcut(track);

        return model;
    }
}
