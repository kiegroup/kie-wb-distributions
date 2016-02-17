package org.kie.workbench.client.server.management.wizard.template;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.Widget;
import org.uberfire.client.callbacks.Callback;
import org.uberfire.client.mvp.UberView;
import org.uberfire.ext.widgets.core.client.wizards.WizardPage;

@Dependent
public class NewTemplatePresenter implements WizardPage {

    public interface View extends UberView<NewTemplatePresenter> {

        String getTitle();
    }

    private final View view;

    @Inject
    public NewTemplatePresenter( final View view ) {
        this.view = view;
    }

    @PostConstruct
    public void init() {
        this.view.init( this );
    }

    @Override
    public String getTitle() {
        return view.getTitle();
    }

    @Override
    public void isComplete( final Callback<Boolean> callback ) {
        if ( isValid() ) {
            callback.callback( true );
        } else {
            callback.callback( false );
        }
    }

    private boolean isValid() {
        return true;
    }

    @Override
    public void initialise() {

    }

    @Override
    public void prepareView() {

    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }
}
