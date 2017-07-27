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
import org.kie.workbench.common.screens.home.model.ModelUtils;
import org.uberfire.client.mvp.PlaceManager;

import static org.kie.workbench.common.workbench.client.PerspectiveIds.BUSINESS_DASHBOARDS;
import static org.kie.workbench.common.workbench.client.PerspectiveIds.LIBRARY;
import static org.kie.workbench.common.workbench.client.PerspectiveIds.PROCESS_INSTANCES;
import static org.kie.workbench.common.workbench.client.PerspectiveIds.SERVER_MANAGEMENT;
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

        model.addShortcut(ModelUtils.makeShortcut("pficon-blueprint",
                                                  translationService.format(Constants.Design),
                                                  translationService.format(Constants.DesignDescription),
                                                  () -> placeManager.goTo(LIBRARY),
                                                  LIBRARY,
                                                  PERSPECTIVE));
        model.addShortcut(ModelUtils.makeShortcut("pficon-build",
                                                  translationService.format(Constants.DevOps),
                                                  translationService.format(Constants.DevOpsDescription),
                                                  () -> placeManager.goTo(SERVER_MANAGEMENT),
                                                  SERVER_MANAGEMENT,
                                                  PERSPECTIVE));
        model.addShortcut(ModelUtils.makeShortcut("fa-briefcase",
                                                  translationService.format(Constants.Manage),
                                                  translationService.format(Constants.ManageDescription),
                                                  () -> placeManager.goTo(PROCESS_INSTANCES),
                                                  PROCESS_INSTANCES,
                                                  PERSPECTIVE));
        model.addShortcut(ModelUtils.makeShortcut("pficon-trend-up",
                                                  translationService.format(Constants.Track),
                                                  translationService.format(Constants.TrackDescription),
                                                  () -> placeManager.goTo(BUSINESS_DASHBOARDS),
                                                  BUSINESS_DASHBOARDS,
                                                  PERSPECTIVE));

        return model;
    }
}
