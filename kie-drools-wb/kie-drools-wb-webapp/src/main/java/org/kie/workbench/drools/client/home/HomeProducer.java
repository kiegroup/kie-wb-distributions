package org.kie.workbench.drools.client.home;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import org.kie.workbench.common.screens.home.model.HomeModel;
import org.kie.workbench.common.screens.home.model.ModelUtils;
import org.kie.workbench.common.screens.home.model.Section;
import org.kie.workbench.common.screens.home.model.SectionEntry;
import org.kie.workbench.common.services.security.KieWorkbenchACL;
import org.kie.workbench.drools.client.resources.i18n.AppConstants;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.mvp.Command;

import static org.kie.workbench.drools.client.security.KieWorkbenchFeatures.*;

/**
 * Producer method for the Home Page content
 */
@ApplicationScoped
public class HomeProducer {

    private AppConstants constants = AppConstants.INSTANCE;

    private HomeModel model;

    @Inject
    private PlaceManager placeManager;

    @Inject
    private KieWorkbenchACL kieACL;

    public void init() {
        final String url = GWT.getModuleBaseURL();
        model = new HomeModel( constants.homeTheKnowledgeLifeCycle() );
        model.addCarouselEntry( ModelUtils.makeCarouselEntry( constants.homeAuthor(),
                                                              constants.homeAuthorCaption(),
                                                              url + "/images/HandHome.jpg" ) );
        model.addCarouselEntry( ModelUtils.makeCarouselEntry( constants.homeDeploy(),
                                                              constants.homeDeployCaption(),
                                                              url + "/images/HandHome.jpg" ) );

        final Section s1 = new Section( constants.authoring() );
        final SectionEntry s1_a = ModelUtils.makeSectionEntry( constants.project_authoring(),
                                                               new Command() {

                                                                   @Override
                                                                   public void execute() {
                                                                       placeManager.goTo( "AuthoringPerspective" );
                                                                   }
                                                               } );

        final SectionEntry s1_b = ModelUtils.makeSectionEntry( constants.contributors(),
                                                                new Command() {

                                                                    @Override
                                                                    public void execute() {
                                                                        placeManager.goTo( "ContributorsPerspective" );
                                                                    }
                                                                } );

        final SectionEntry s1_c = ModelUtils.makeSectionEntry( constants.Asset_Management(),
                new Command() {

                    @Override
                    public void execute() {
                        placeManager.goTo( "Asset Management" );
                    }
                } );

        final SectionEntry s1_d = ModelUtils.makeSectionEntry( constants.artifactRepository(),
                new Command() {

                    @Override
                    public void execute() {
                        placeManager.goTo( "org.guvnor.m2repo.client.perspectives.GuvnorM2RepoPerspective" );
                    }
                } );

        final SectionEntry s1_e = ModelUtils.makeSectionEntry( constants.administration(),
                                                               new Command() {

                                                                   @Override
                                                                   public void execute() {
                                                                       placeManager.goTo( "org.kie.workbench.drools.client.perspectives.AdministrationPerspective" );
                                                                   }
                                                               } );

        final Section s2 = new Section( constants.deployment() );
        final SectionEntry s2_a = ModelUtils.makeSectionEntry( constants.ruleDeployments(),
                                                               new Command() {

                                                                   @Override
                                                                   public void execute() {
                                                                       placeManager.goTo( "ServerManagementPerspective" );
                                                                   }
                                                               } );

        final Section s3 = new Section( constants.tasks() );
        final SectionEntry s3_a = ModelUtils.makeSectionEntry( constants.Tasks_List(),
                                                                new Command() {

                                                                    @Override
                                                                    public void execute() {
                                                                        placeManager.goTo( "Tasks" );
                                                                    }
                                                                } );

        s1.setRoles( kieACL.getGrantedRoles( G_AUTHORING ) );
        s1_a.setRoles( kieACL.getGrantedRoles( F_PROJECT_AUTHORING ) );
        s1_b.setRoles( kieACL.getGrantedRoles( F_CONTRIBUTORS ) );
        // TODO s1_c.setRoles( kieACL.getGrantedRoles(  ) );
        s1_d.setRoles( kieACL.getGrantedRoles( F_ARTIFACT_REPO ) );
        s1_e.setRoles( kieACL.getGrantedRoles( F_ADMINISTRATION ) );

        s2.setRoles( kieACL.getGrantedRoles( G_AUTHORING ) );
        s2_a.setRoles( kieACL.getGrantedRoles( F_MANAGEMENT ) );

        s1.addEntry( s1_a );
        s1.addEntry( s1_b );
        s1.addEntry( s1_c );
        s1.addEntry( s1_d );
        s1.addEntry( s1_e );

        s2.addEntry( s2_a );

        s3.addEntry( s3_a );

        model.addSection( s1 );
        model.addSection( s2 );
        model.addSection( s3 );
    }

    @Produces
    public HomeModel getModel() {
        return model;
    }

}
