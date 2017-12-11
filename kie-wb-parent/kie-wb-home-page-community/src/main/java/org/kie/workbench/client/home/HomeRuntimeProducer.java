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

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Alternative;
import javax.inject.Inject;

import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.client.resources.i18n.Constants;
import org.kie.workbench.common.screens.home.client.widgets.shortcut.utils.ShortcutHelper;
import org.kie.workbench.common.screens.home.model.HomeShortcut;
import org.kie.workbench.common.screens.home.model.HomeShortcutLink;
import org.kie.workbench.common.screens.home.model.ModelUtils;
import org.uberfire.client.mvp.PlaceManager;

import static org.kie.workbench.common.workbench.client.PerspectiveIds.CONTENT_MANAGEMENT;
import static org.uberfire.workbench.model.ActivityResourceType.PERSPECTIVE;

@Alternative
@ApplicationScoped
public class HomeRuntimeProducer extends AbstractHomeProducer {

    public HomeRuntimeProducer() {
        //CDI proxy
    }

    @Inject
    public HomeRuntimeProducer(final PlaceManager placeManager,
                               final TranslationService translationService,
                               final ShortcutHelper shortcutHelper) {
        super(placeManager,
              translationService,
              shortcutHelper);
    }

    @Override
    protected HomeShortcut createDesignShortcut() {
        final HomeShortcut design = ModelUtils.makeShortcut("pficon pficon-blueprint",
                                                            translationService.format(Constants.Design),
                                                            translationService.format(Constants.DesignRuntimeDescription),
                                                            () -> placeManager.goTo(CONTENT_MANAGEMENT),
                                                            CONTENT_MANAGEMENT,
                                                            PERSPECTIVE);
        design.addLink(new HomeShortcutLink(translationService.format(Constants.Pages),
                                            CONTENT_MANAGEMENT));

        return design;
    }
}
