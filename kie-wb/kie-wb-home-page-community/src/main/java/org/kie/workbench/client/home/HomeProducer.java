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

package org.kie.workbench.client.home;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.user.client.Window;
import org.jbpm.dashboard.renderer.service.DashboardURLBuilder;
import org.kie.workbench.client.resources.i18n.HomePageCommunityConstants;
import org.kie.workbench.common.screens.home.model.HomeModel;
import org.kie.workbench.common.screens.home.model.ModelUtils;
import org.kie.workbench.common.screens.home.model.SectionEntry;
import org.kie.workbench.common.workbench.client.authz.WorkbenchFeatures;
import org.kie.workbench.common.workbench.client.library.LibraryMonitor;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.ConditionalPlaceRequest;
import org.uberfire.mvp.impl.DefaultPlaceRequest;

import static org.uberfire.workbench.model.ActivityResourceType.*;
import static org.kie.workbench.common.workbench.client.PerspectiveIds.*;

/**
 * Producer method for the Home Page content
 */
@ApplicationScoped
public class HomeProducer {

    private HomePageCommunityConstants constants = HomePageCommunityConstants.INSTANCE;

    private HomeModel model;

    @Inject
    private PlaceManager placeManager;

    @Inject
    protected LibraryMonitor libraryMonitor;

    public void init() {
        final String url = GWT.getModuleBaseURL();
        model = new HomeModel( constants.homeTheKnowledgeLifeCycle() );
        model.addCarouselEntry( ModelUtils.makeCarouselEntry( constants.homeDiscover(),
                                                              constants.homeDiscoverCaption(),
                                                              url + "/images/HandHome.jpg" ) );
        model.addCarouselEntry( ModelUtils.makeCarouselEntry( constants.homeAuthor(),
                                                              constants.homeAuthorCaption(),
                                                              url + "/images/HandHome.jpg" ) );
        model.addCarouselEntry( ModelUtils.makeCarouselEntry( constants.homeDeploy(),
                                                              constants.homeDeployCaption(),
                                                              url + "/images/HandHome.jpg" ) );
        model.addCarouselEntry( ModelUtils.makeCarouselEntry( constants.homeWork(),
                                                              constants.homeWorkCaption(),
                                                              url + "/images/HandHome.jpg" ) );
        model.addCarouselEntry( ModelUtils.makeCarouselEntry( constants.homeImprove(),
                                                              constants.homeImproveCaption(),
                                                              url + "/images/HandHome.jpg" ) );

        final SectionEntry s1 = ModelUtils.makeSectionEntry( constants.Authoring() );

        final PlaceRequest authoringPlaceRequest = getAuthoringPlaceRequest();
        s1.addChild( ModelUtils.makeSectionEntry( constants.Project_Authoring(),
                () -> placeManager.goTo( authoringPlaceRequest ),
                AUTHORING, PERSPECTIVE ) );

        s1.addChild( ModelUtils.makeSectionEntry( constants.Library(),
                                                  () -> placeManager.goTo( LIBRARY ),
                                                  LIBRARY, PERSPECTIVE ) );

        s1.addChild( ModelUtils.makeSectionEntry( constants.Contributors(),
                () -> placeManager.goTo( CONTRIBUTORS ),
                CONTRIBUTORS, PERSPECTIVE ) );

        s1.addChild( ModelUtils.makeSectionEntry( constants.artifactRepository(),
                () -> placeManager.goTo( GUVNOR_M2REPO ),
                GUVNOR_M2REPO , PERSPECTIVE ) );

        s1.addChild( ModelUtils.makeSectionEntry( constants.Administration(),
                () -> placeManager.goTo( ADMINISTRATION ),
                ADMINISTRATION , PERSPECTIVE ) );

        final SectionEntry s2 = ModelUtils.makeSectionEntry( constants.Deploy() );

        s2.addChild( ModelUtils.makeSectionEntry( constants.ExecutionServers(),
                () -> placeManager.goTo( SERVER_MANAGEMENT ),
                SERVER_MANAGEMENT , PERSPECTIVE ) );

        s2.addChild( ModelUtils.makeSectionEntry( constants.Jobs(),
                () -> placeManager.goTo( JOBS ),
                JOBS , PERSPECTIVE ) );

        final SectionEntry s3 = ModelUtils.makeSectionEntry( constants.Process_Management() );

        s3.addChild( ModelUtils.makeSectionEntry( constants.Process_Definitions(),
                () -> placeManager.goTo( PROCESS_DEFINITIONS ),
                PROCESS_DEFINITIONS , PERSPECTIVE ) );

        s3.addChild( ModelUtils.makeSectionEntry( constants.Process_Instances(),
                () -> placeManager.goTo( PROCESS_INSTANCES ),
                PROCESS_INSTANCES , PERSPECTIVE ) );

        final SectionEntry s4 = ModelUtils.makeSectionEntry( constants.Tasks() );

        s4.addChild( ModelUtils.makeSectionEntry( constants.Tasks_List(),
                () -> placeManager.goTo( TASKS ),
                TASKS , PERSPECTIVE ) );

        final SectionEntry s5 = ModelUtils.makeSectionEntry( constants.Dashboards() );

        s5.addChild( ModelUtils.makeSectionEntry( constants.Process_Dashboard(),
                () -> placeManager.goTo( PROCESS_DASHBOARD ),
                PROCESS_DASHBOARD , PERSPECTIVE ) );

        final String dashbuilderURL = DashboardURLBuilder.getDashboardURL( "/dashbuilder/workspace", "showcase", LocaleInfo.getCurrentLocale().getLocaleName() );
        s5.addChild( ModelUtils.makeSectionEntry( constants.Business_Dashboard(),
                () -> Window.open( dashbuilderURL, "_blank", "" ), WorkbenchFeatures.MANAGE_DASHBOARDS ) );

        model.addSection( s1 );
        model.addSection( s2 );
        model.addSection( s3 );
        model.addSection( s4 );
        model.addSection( s5 );
    }

    PlaceRequest getAuthoringPlaceRequest() {
        final DefaultPlaceRequest libraryPlaceRequest = new DefaultPlaceRequest( LIBRARY );
        return new ConditionalPlaceRequest( AUTHORING ).when( p -> libraryMonitor.thereIsAtLeastOneProjectAccessible() ).orElse( libraryPlaceRequest );
    }

    @Produces
    public HomeModel getModel() {
        return model;
    }

}
