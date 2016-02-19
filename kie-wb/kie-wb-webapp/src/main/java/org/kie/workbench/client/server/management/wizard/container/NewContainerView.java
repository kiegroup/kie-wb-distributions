package org.kie.workbench.client.server.management.wizard.container;

import javax.enterprise.context.Dependent;

import com.google.gwt.user.client.ui.Composite;
import org.jboss.errai.ui.shared.api.annotations.Templated;

@Dependent
@Templated
public class NewContainerView extends Composite
        implements NewContainerPresenter.View {

    private NewContainerPresenter presenter;

    @Override
    public void init( final NewContainerPresenter presenter ) {
        this.presenter = presenter;
    }

    @Override
    public String getTitle() {
        return "Template Name and Capabilities";
    }
}
