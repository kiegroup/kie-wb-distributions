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

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import org.kie.workbench.common.screens.home.model.HomeModel;
import org.kie.workbench.common.screens.home.model.ModelUtils;
import org.kie.workbench.common.screens.home.model.SectionEntry;
import org.kie.workbench.drools.client.resources.i18n.HomePageCommunityConstants;
import org.uberfire.client.mvp.PlaceManager;

import static org.kie.workbench.common.workbench.client.PerspectiveIds.ADMINISTRATION;
import static org.kie.workbench.common.workbench.client.PerspectiveIds.CONTRIBUTORS;
import static org.kie.workbench.common.workbench.client.PerspectiveIds.GUVNOR_M2REPO;
import static org.kie.workbench.common.workbench.client.PerspectiveIds.LIBRARY;
import static org.kie.workbench.common.workbench.client.PerspectiveIds.SERVER_MANAGEMENT;
import static org.uberfire.workbench.model.ActivityResourceType.PERSPECTIVE;

/**
 * Producer method for the Home Page content
 */
@Dependent
public class HomeProducer {

    @Produces
    @ApplicationScoped
    public HomeModel getModel(PlaceManager placeManager) {
        final HomePageCommunityConstants constants = HomePageCommunityConstants.INSTANCE;
        final String url = GWT.getModuleBaseURL();
        final HomeModel model = new HomeModel( constants.homeTheKnowledgeLifeCycle() );
        model.addCarouselEntry( ModelUtils.makeCarouselEntry( constants.homeAuthor(),
                                                              constants.homeAuthorCaption(),
                                                              url + "/images/HandHome.jpg" ) );
        model.addCarouselEntry( ModelUtils.makeCarouselEntry( constants.homeDeploy(),
                                                              constants.homeDeployCaption(),
                                                              url + "/images/HandHome.jpg" ) );

        final SectionEntry s1 = ModelUtils.makeSectionEntry( constants.authoring() );

        s1.addChild( ModelUtils.makeSectionEntry( constants.project_authoring(),
                () -> placeManager.goTo( LIBRARY ),
                LIBRARY, PERSPECTIVE ) );

        s1.addChild( ModelUtils.makeSectionEntry( constants.contributors(),
                () -> placeManager.goTo( CONTRIBUTORS ),
                CONTRIBUTORS, PERSPECTIVE ) );

        s1.addChild( ModelUtils.makeSectionEntry( constants.artifactRepository(),
                () -> placeManager.goTo( GUVNOR_M2REPO ),
                GUVNOR_M2REPO, PERSPECTIVE ) );

        s1.addChild( ModelUtils.makeSectionEntry( constants.administration(),
                () -> placeManager.goTo( ADMINISTRATION ),
                ADMINISTRATION, PERSPECTIVE ) );

        final SectionEntry s2 = ModelUtils.makeSectionEntry( constants.deploy() );

        s2.addChild( ModelUtils.makeSectionEntry( constants.executionServers(),
                () -> placeManager.goTo( SERVER_MANAGEMENT ),
                SERVER_MANAGEMENT, PERSPECTIVE ) );


        model.addSection( s1 );
        model.addSection( s2 );

        return model;
    }

}
