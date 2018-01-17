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
import org.kie.workbench.common.screens.home.client.widgets.shortcut.utils.ShortcutHelper;
import org.kie.workbench.common.screens.home.model.HomeModel;
import org.kie.workbench.common.screens.home.model.HomeModelProvider;
import org.kie.workbench.common.screens.home.model.HomeShortcut;
import org.kie.workbench.common.screens.home.model.HomeShortcutLink;
import org.kie.workbench.common.screens.home.model.ModelUtils;
import org.kie.workbench.drools.client.resources.i18n.Constants;
import org.uberfire.client.mvp.PlaceManager;

import static org.kie.workbench.common.workbench.client.PerspectiveIds.DEPLOYMENTS;
import static org.kie.workbench.common.workbench.client.PerspectiveIds.LIBRARY;
import static org.kie.workbench.common.workbench.client.PerspectiveIds.SERVER_MANAGEMENT;
import static org.uberfire.workbench.model.ActivityResourceType.PERSPECTIVE;

@ApplicationScoped
public class HomeProducer implements HomeModelProvider {

    private PlaceManager placeManager;
    private TranslationService translationService;
    private ShortcutHelper shortcutHelper;

    public HomeProducer() {
        //CDI proxy
    }

    @Inject
    public HomeProducer(final PlaceManager placeManager,
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

        return model;
    }

    private HomeShortcut createDesignShortcut() {
        final HomeShortcut design = ModelUtils.makeShortcut("pficon pficon-blueprint",
                                                            translationService.format(Constants.Design),
                                                            translationService.format(Constants.DesignDescription),
                                                            () -> placeManager.goTo(LIBRARY),
                                                            LIBRARY,
                                                            PERSPECTIVE);
        design.addLink(new HomeShortcutLink(translationService.format(Constants.Projects),
                                            LIBRARY));
        return design;
    }

    private HomeShortcut createDevOpsShortcut() {
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
}
