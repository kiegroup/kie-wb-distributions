/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import org.kie.workbench.common.screens.home.client.widgets.home.HomeImagesHelper;
import org.kie.workbench.common.screens.home.model.HomeModel;
import org.kie.workbench.common.screens.home.model.Section;
import org.kie.workbench.drools.client.resources.i18n.HomePageProductConstants;
import org.uberfire.client.mvp.PlaceManager;

/**
 * Producer method for the Home Page content
 */
@ApplicationScoped
public class HomeProducer {

    private HomePageProductConstants homeConstants = HomePageProductConstants.INSTANCE;

    private HomeModel model;

    @Inject
    private PlaceManager placeManager;

    @PostConstruct
    public void init() {
        final String url = GWT.getModuleBaseURL();
        model = new HomeModel( homeConstants.home_title(),
                               homeConstants.home_subtitle() );

        final Section s1 = new Section( homeConstants.authoring_header(),
                                        homeConstants.authoring_paragraph(),
                                        url + HomeImagesHelper.Images.Authoring.getLocalisedImageUrl() );

        final Section s2 = new Section( homeConstants.analyze_header(),
                                        homeConstants.analyze_paragraph(),
                                        url + HomeImagesHelper.Images.Analyze.getLocalisedImageUrl() );

        final Section s3 = new Section( homeConstants.deploy_header(),
                                        homeConstants.deploy_paragraph(),
                                        url + HomeImagesHelper.Images.Deploy.getLocalisedImageUrl() );

        model.addSection( s1 );
        model.addSection( s2 );
        model.addSection( s3 );
    }

    @Produces
    public HomeModel getModel() {
        return model;
    }

}