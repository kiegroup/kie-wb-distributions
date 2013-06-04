package org.kie.workbench.client.home;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import com.google.gwt.user.client.Window;
import org.kie.workbench.common.screens.home.client.model.HomeModel;
import org.kie.workbench.common.screens.home.client.model.ModelUtils;
import org.kie.workbench.common.screens.home.client.model.Section;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.mvp.Command;

/**
 * Producer method for the Home Page content
 */
@ApplicationScoped
public class HomeProducer {

    private HomeModel model = new HomeModel();

    @Inject
    private PlaceManager placeManager;

    @PostConstruct
    public void init() {
        model.addCarouselEntry( ModelUtils.makeCarouselEntry( "Discover",
                                                              "The Business Knowledge to drive your company",
                                                              "/images/flowers.jpg" ) );
        model.addCarouselEntry( ModelUtils.makeCarouselEntry( "Author",
                                                              "Formalize your Business Knowledge",
                                                              "/images/flowers.jpg" ) );
        model.addCarouselEntry( ModelUtils.makeCarouselEntry( "Deploy",
                                                              "Learn how to configure your environment",
                                                              "/images/flowers.jpg" ) );
        model.addCarouselEntry( ModelUtils.makeCarouselEntry( "Work",
                                                              "Reduce the paper work",
                                                              "/images/flowers.jpg" ) );
        model.addCarouselEntry( ModelUtils.makeCarouselEntry( "Improve",
                                                              "Your Business Performance",
                                                              "/images/flowers.jpg" ) );
        final Section s1 = new Section( "Discover and Author:" );
        s1.addEntry( ModelUtils.makeSectionEntry( "Author",
                                                  new Command() {

                                                      @Override
                                                      public void execute() {
                                                          placeManager.goTo( "org.drools.workbench.client.perspectives.AuthoringPerspective" );
                                                      }
                                                  } ) );
        model.addSection( s1 );

        final Section s2 = new Section( "Deploy:" );
        s2.addEntry( ModelUtils.makeSectionEntry( "Manage and Deploy Your Assets",
                                                  new Command() {

                                                      @Override
                                                      public void execute() {
                                                          placeManager.goTo( "org.drools.workbench.client.perspectives.AdministrationPerspective" );
                                                      }
                                                  } ) );
        s2.addEntry( ModelUtils.makeSectionEntry( "Assets Repository",
                                                  new Command() {

                                                      @Override
                                                      public void execute() {
                                                          placeManager.goTo( "org.guvnor.m2repo.client.perspectives.GuvnorM2RepoPerspective" );
                                                      }
                                                  } ) );
        model.addSection( s2 );

        final Section s3 = new Section( "Work:" );
        s3.addEntry( ModelUtils.makeSectionEntry( "Tasks List",
                                                  new Command() {

                                                      @Override
                                                      public void execute() {
                                                          placeManager.goTo( "Tasks List" );
                                                      }
                                                  } ) );
        s3.addEntry( ModelUtils.makeSectionEntry( "Process Management",
                                                  new Command() {

                                                      @Override
                                                      public void execute() {
                                                          placeManager.goTo( "Process Definitions" );
                                                      }
                                                  } ) );
        model.addSection( s3 );

        final Section s4 = new Section( "Monitor:" );
        s4.addEntry( ModelUtils.makeSectionEntry( "Business Activity Monitoring",
                                                  new Command() {

                                                      @Override
                                                      public void execute() {
                                                          Window.open( "http://localhost:8080/bam-app/",
                                                                       "_blank",
                                                                       "" );
                                                      }
                                                  } ) );
        model.addSection( s4 );
    }

    @Produces
    public HomeModel getModel() {
        return model;
    }

}
