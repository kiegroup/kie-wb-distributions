package org.kie.workbench.client.server.management.container.status.card;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.Composite;
import org.gwtbootstrap3.client.ui.html.Div;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.client.server.management.widget.card.CardPresenter;

import static org.uberfire.commons.validation.PortablePreconditions.*;

@Templated
@Dependent
public class ContainerCardView extends Composite
        implements ContainerCardPresenter.View {

    @Inject
    @DataField("container")
    Div container;

    @Override
    public void setCard( final CardPresenter.View cardView ) {
        container.add( checkNotNull( "cardView", cardView ) );
    }
}
