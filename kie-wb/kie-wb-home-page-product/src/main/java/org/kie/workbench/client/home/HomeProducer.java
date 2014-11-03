package org.kie.workbench.client.home;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.LocaleInfo;
import org.jbpm.dashboard.renderer.service.DashboardURLBuilder;
import org.kie.workbench.common.screens.home.client.resources.i18n.HomeConstants;
import org.kie.workbench.common.screens.home.model.HomeModel;
import org.kie.workbench.common.screens.home.model.Section;
import org.kie.workbench.common.services.security.KieWorkbenchACL;
import org.uberfire.client.mvp.PlaceManager;

import static org.kie.workbench.client.security.KieWorkbenchFeatures.*;

/**
 * * Producer method for the Home Page content
 */
@ApplicationScoped
public class HomeProducer {

    private HomeConstants homeConstants = HomeConstants.INSTANCE;

    private HomeModel model;

    @Inject
    private PlaceManager placeManager;

    @Inject
    private KieWorkbenchACL kieACL;

    public void init() {
        final String url = GWT.getModuleBaseURL();
        model = new HomeModel( homeConstants.home_title(), homeConstants.home_subtitle() );

        final Section s1 = new Section( homeConstants.authoring_header(),
                                        homeConstants.authoring_paragraph(),
                                        url + homeConstants.authoring_image() );

        final Section s2 = new Section( homeConstants.deploy_header(),
                                        homeConstants.deploy_paragraph(),
                                        url + homeConstants.deploy_image() );

        final Section s3 = new Section( homeConstants.process_Management_header(),
                                        homeConstants.process_Management_paragraph(),
                                        url + homeConstants.process_Management_image() );

        final Section s4 = new Section( homeConstants.tasks_header(),
                                        homeConstants.tasks_paragraph(),
                                        url + homeConstants.tasks_image() );

        final Section s5 = new Section( homeConstants.dashboards_header(),
                                        homeConstants.dashboards_paragraph(),
                                        url + homeConstants.dashboards_image() );

        final String dashbuilderURL = DashboardURLBuilder.getDashboardURL( "/dashbuilder/workspace", "showcase", LocaleInfo.getCurrentLocale().getLocaleName() );

        s1.setRoles( kieACL.getGrantedRoles( G_AUTHORING ) );

        s2.setRoles( kieACL.getGrantedRoles( G_DEPLOY ) );

        s3.setRoles( kieACL.getGrantedRoles( G_PROCESS_MANAGEMENT ) );

        s4.setRoles( kieACL.getGrantedRoles( G_TASKS ) );

        s5.setRoles( kieACL.getGrantedRoles( G_DASHBOARDS ) );

        model.addSection( s1 );
        model.addSection( s2 );
        model.addSection( s3 );
        model.addSection( s4 );
        model.addSection( s5 );
    }

    @Produces
    public HomeModel getModel() {
        return model;
    }

}
