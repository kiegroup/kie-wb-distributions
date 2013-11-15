package org.kie.workbench.drools.client.home;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import org.kie.workbench.common.screens.home.client.resources.i18n.HomeConstants;
import org.kie.workbench.common.screens.home.model.HomeModel;

import org.kie.workbench.common.screens.home.model.Section;
import org.kie.workbench.drools.client.resources.i18n.AppConstants;
import org.uberfire.client.mvp.PlaceManager;

/**
 * Producer method for the Home Page content
 */
@ApplicationScoped
public class HomeProducer {

    private AppConstants constants = AppConstants.INSTANCE;
    private HomeConstants homeConstants = HomeConstants.INSTANCE;

    private HomeModel model;

    @Inject
    private PlaceManager placeManager;

    @PostConstruct
    public void init() {
        final String url = GWT.getModuleBaseURL();

        model = new HomeModel( homeConstants.home_title(),homeConstants.home_subtitle());

        final Section s1 = new Section( homeConstants.authoring_header(),
                homeConstants.authoring_paragraph(),
                url + homeConstants.authoring_image());

        final Section s2 = new Section( homeConstants.analyze_header(),
                homeConstants.analyze_paragraph(),
                url + homeConstants.analyze_image());

        final Section s3 = new Section( homeConstants.deploy_header(),
                homeConstants.deploy_paragraph(),
                url + homeConstants.deploy_image());


        model.addSection(s1);
        model.addSection(s2);
        model.addSection(s3);
    }

    @Produces
    public HomeModel getModel() {
        return model;
    }

}
