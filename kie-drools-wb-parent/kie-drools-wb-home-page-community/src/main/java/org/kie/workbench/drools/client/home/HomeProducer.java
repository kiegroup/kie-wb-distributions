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

package org.kie.workbench.drools.client.home;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.screens.home.model.HomeModel;
import org.kie.workbench.common.screens.home.model.HomeModelProvider;
import org.kie.workbench.common.screens.home.model.HomeShortcut;
import org.kie.workbench.common.screens.home.model.HomeShortcutLink;
import org.kie.workbench.common.screens.home.model.ModelUtils;
import org.kie.workbench.drools.client.resources.i18n.Constants;
import org.uberfire.client.mvp.PlaceManager;

import static org.kie.workbench.common.workbench.client.PerspectiveIds.BUSINESS_DASHBOARDS;
import static org.kie.workbench.common.workbench.client.PerspectiveIds.DEPLOYMENTS;
import static org.kie.workbench.common.workbench.client.PerspectiveIds.LIBRARY;
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
                                              "images/community_home_bg.jpg");

        final HomeShortcut design = ModelUtils.makeShortcut("pficon pficon-blueprint",
                                                            translationService.format(Constants.Design),
                                                            translationService.format(Constants.DesignDescription),
                                                            () -> placeManager.goTo(LIBRARY),
                                                            LIBRARY,
                                                            PERSPECTIVE);
        design.addLink(new HomeShortcutLink(translationService.format(Constants.Projects),
                                            LIBRARY));
        design.addLink(new HomeShortcutLink(translationService.format(Constants.Dashboards),
                                            BUSINESS_DASHBOARDS));

        final HomeShortcut devOps = ModelUtils.makeShortcut("fa fa-gears",
                                                            translationService.format(Constants.DevOps),
                                                            translationService.format(Constants.DevOpsDescription),
                                                            () -> placeManager.goTo(SERVER_MANAGEMENT),
                                                            SERVER_MANAGEMENT,
                                                            PERSPECTIVE);
        devOps.addLink(new HomeShortcutLink(translationService.format(Constants.Deployments),
                                            DEPLOYMENTS));
        devOps.addLink(new HomeShortcutLink(translationService.format(Constants.Servers),
                                            SERVER_MANAGEMENT));

        model.addShortcut(design);
        model.addShortcut(devOps);

        return model;
    }
}
