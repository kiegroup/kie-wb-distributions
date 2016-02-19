package org.kie.workbench.client.server.management.wizard;

import java.util.ArrayList;
import java.util.List;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.Widget;
import org.kie.workbench.client.server.management.wizard.template.NewTemplatePresenter;
import org.uberfire.client.callbacks.Callback;
import org.uberfire.ext.widgets.core.client.wizards.AbstractWizard;
import org.uberfire.ext.widgets.core.client.wizards.WizardPage;

@Dependent
public class NewServerTemplateWizard extends AbstractWizard {

    private final ArrayList<WizardPage> pages = new ArrayList<WizardPage>();
    private final NewTemplatePresenter newTemplatePresenter;

    @Inject
    public NewServerTemplateWizard( final NewTemplatePresenter newTemplatePresenter ) {
        this.newTemplatePresenter = newTemplatePresenter;
        pages.add( this.newTemplatePresenter );
    }

    @Override
    public List<WizardPage> getPages() {
        return pages;
    }

    @Override
    public Widget getPageWidget( final int pageNumber ) {
        return pages.get( pageNumber ).asWidget();
    }

    @Override
    public String getTitle() {
        return "New Server Template";
    }

    @Override
    public int getPreferredHeight() {
        return 550;
    }

    @Override
    public int getPreferredWidth() {
        return 600;
    }

    @Override
    public void isComplete( final Callback<Boolean> callback ) {
        callback.callback( true );

        //only when all pages are complete we can say the wizard is complete.
        for ( WizardPage page : this.pages ) {
            page.isComplete( new Callback<Boolean>() {
                @Override
                public void callback( final Boolean result ) {
                    if ( Boolean.FALSE.equals( result ) ) {
                        callback.callback( false );
                    }
                }
            } );
        }
    }
}
