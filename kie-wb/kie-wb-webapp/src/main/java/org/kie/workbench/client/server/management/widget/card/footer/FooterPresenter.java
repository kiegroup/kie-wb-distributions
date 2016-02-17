package org.kie.workbench.client.server.management.widget.card.footer;

import java.util.ArrayList;
import java.util.Collection;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.kie.workbench.client.server.management.widget.card.body.notification.NotificationPresenter;

@Dependent
public class FooterPresenter {

    public interface View extends IsWidget {

        void setup( final String url,
                    final String version );
    }

    private final View view;

    private final Collection<NotificationPresenter> presenters = new ArrayList<NotificationPresenter>();

    @Inject
    public FooterPresenter( final View view ) {
        this.view = view;
    }

    public View getView() {
        return view;
    }

    public void setup( final String url,
                       final String version ) {
        view.setup( url, version );
    }

}
