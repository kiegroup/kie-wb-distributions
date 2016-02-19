package org.kie.workbench.client.server.management.widget.card;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.IsWidget;
import org.gwtbootstrap3.client.ui.html.Div;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;

@Templated
@Dependent
public class CardView extends Composite
        implements CardPresenter.View {

    private CardPresenter presenter;

    @Inject
    @DataField("card")
    Div card;

    @Override
    public void init( final CardPresenter presenter ) {
        this.presenter = presenter;
    }

    @Override
    public void add( final IsWidget view ) {
        card.add( view );
    }
}
