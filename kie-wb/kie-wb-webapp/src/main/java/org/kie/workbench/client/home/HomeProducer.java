package org.kie.workbench.client.home;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.user.client.Window;
import org.kie.workbench.client.resources.i18n.AppConstants;
import org.kie.workbench.common.screens.home.model.HomeModel;
import org.kie.workbench.common.screens.home.model.ModelUtils;
import org.kie.workbench.common.screens.home.model.Section;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.mvp.Command;

/**
 * Producer method for the Home Page content
 */
@ApplicationScoped
public class HomeProducer {

    private HomeModel model;

    private AppConstants constants = AppConstants.INSTANCE;

    @Inject
    private PlaceManager placeManager;

    @PostConstruct
    public void init() {
        final String url = GWT.getModuleBaseURL();
        model = new HomeModel( constants.homeTheKnowledgeLifeCycle() );
        model.addCarouselEntry( ModelUtils.makeCarouselEntry( constants.homeDiscover(),
                                                              constants.homeDiscoverCaption(),
                                                              url + "/images/flowers.jpg" ) );
        model.addCarouselEntry( ModelUtils.makeCarouselEntry( constants.homeAuthor(),
                                                              constants.homeAuthorCaption(),
                                                              url + "/images/flowers.jpg" ) );
        model.addCarouselEntry( ModelUtils.makeCarouselEntry( constants.homeDeploy(),
                                                              constants.homeDeployCaption(),
                                                              url + "/images/flowers.jpg" ) );
        model.addCarouselEntry( ModelUtils.makeCarouselEntry( constants.homeWork(),
                                                              constants.homeWorkCaption(),
                                                              url + "/images/flowers.jpg" ) );
        model.addCarouselEntry( ModelUtils.makeCarouselEntry( constants.homeImprove(),
                                                              constants.homeImproveCaption(),
                                                              url + "/images/flowers.jpg" ) );
        final Section s1 = new Section( constants.Authoring() );
        s1.addEntry( ModelUtils.makeSectionEntry( constants.Project_Authoring(),
                                                  new Command() {

                                                      @Override
                                                      public void execute() {
                                                          placeManager.goTo( "org.kie.workbench.client.perspectives.DroolsAuthoringPerspective" );
                                                      }
                                                  } ) );

        s1.addEntry( ModelUtils.makeSectionEntry( constants.Asset_repo(),
                                                  new Command() {

                                                      @Override
                                                      public void execute() {
                                                          placeManager.goTo( "org.guvnor.m2repo.client.perspectives.GuvnorM2RepoPerspective" );
                                                      }
                                                  } ) );

        s1.addEntry( ModelUtils.makeSectionEntry( constants.Administration(),
                                                  new Command() {

                                                      @Override
                                                      public void execute() {
                                                          placeManager.goTo( "org.kie.workbench.client.perspectives.AdministrationPerspective" );
                                                      }
                                                  } ) );
        model.addSection( s1 );

        final Section s2 = new Section( constants.Deploy() );
        s2.addEntry( ModelUtils.makeSectionEntry( constants.Deployments(),
                                                  new Command() {

                                                      @Override
                                                      public void execute() {
                                                          placeManager.goTo( "Deployments" );
                                                      }
                                                  } ) );

        model.addSection( s2 );

        final Section s3 = new Section( constants.Process_Management() );
        s3.addEntry( ModelUtils.makeSectionEntry( constants.Process_Definitions(),
                                                  new Command() {

                                                      @Override
                                                      public void execute() {
                                                          placeManager.goTo( "Process Definitions" );
                                                      }
                                                  } ) );
        s3.addEntry( ModelUtils.makeSectionEntry( constants.Process_Instances(),
                                                  new Command() {

                                                      @Override
                                                      public void execute() {
                                                          placeManager.goTo( "Process Instances" );
                                                      }
                                                  } ) );
        model.addSection( s3 );

        final Section s4 = new Section( constants.Tasks() );
        s4.addEntry( ModelUtils.makeSectionEntry( constants.Tasks_List(),
                                                  new Command() {

                                                      @Override
                                                      public void execute() {
                                                          placeManager.goTo( "Tasks" );
                                                      }
                                                  } ) );
        model.addSection( s4 );

        final Section s5 = new Section( constants.Dashboards() );
        s5.addEntry( ModelUtils.makeSectionEntry( constants.Process_Dashboard(),
                                                  new Command() {

                                                      @Override
                                                      public void execute() {
                                                          placeManager.goTo( "DashboardPerspective" );
                                                      }
                                                  } ) );

        final String dashbuilderURL = DashboardURLBuilder.getDashboardURL("/dashbuilder/workspace", null, LocaleInfo.getCurrentLocale());
        s5.addEntry( ModelUtils.makeSectionEntry( constants.Business_Dashboard(),
                                                  new Command() {
                                                      @Override
                                                      public void execute() {
                                                          Window.open( dashbuilderURL, "_blank", "" );
                                                      }
                                                  } ) );

        model.addSection( s5 );
    }

    @Produces
    public HomeModel getModel() {
        return model;
    }

}
