package org.kie.workbench.client.server.management.wizard.template;

import javax.enterprise.context.Dependent;

import com.google.gwt.user.client.ui.Composite;
import org.jboss.errai.ui.shared.api.annotations.Templated;

@Dependent
@Templated
public class NewTemplateView extends Composite
        implements NewTemplatePresenter.View {

    private NewTemplatePresenter presenter;

    @Override
    public void init( final NewTemplatePresenter presenter ) {
        this.presenter = presenter;
    }

    @Override
    public String getTitle() {
        return "Template Name and Capabilities";
    }
}
