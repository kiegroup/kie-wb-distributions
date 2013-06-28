package org.kie.workbench.client.perspectives;

import org.uberfire.client.annotations.Perspective;
import org.uberfire.client.annotations.WorkbenchPerspective;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.workbench.model.PartDefinition;
import org.uberfire.workbench.model.PerspectiveDefinition;
import org.uberfire.workbench.model.impl.PartDefinitionImpl;
import org.uberfire.workbench.model.impl.PerspectiveDefinitionImpl;

import javax.enterprise.context.ApplicationScoped;


/**
 * A Perspective to show Dashboard
 */
@ApplicationScoped
@WorkbenchPerspective(identifier = "DashboardPerspective", isDefault = false)
public class DashboardPerspective {


    @Perspective
    public PerspectiveDefinition buildPerspective() {
        final PerspectiveDefinition p = new PerspectiveDefinitionImpl();
        p.setName("Dashboard builder");
        PartDefinition pDef = new PartDefinitionImpl(new DefaultPlaceRequest("DashboardPanel"));
        p.getRoot().addPart(pDef);
        p.setTransient(true);
        return p;
    }

}