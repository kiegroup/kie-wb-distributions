package org.kie.workbench.client.server.management.widget.card.body;

import java.util.ArrayList;
import java.util.Collection;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.jboss.errai.ioc.client.container.IOC;
import org.kie.server.controller.api.model.runtime.Message;
import org.kie.workbench.client.server.management.widget.card.body.notification.NotificationPresenter;
import org.uberfire.client.mvp.UberView;

import static org.uberfire.commons.validation.PortablePreconditions.*;

@Dependent
public class BodyPresenter {

    public interface View extends UberView<BodyPresenter> {

        void addNotification( IsWidget view );
    }

    private final View view;

    private final Collection<NotificationPresenter> presenters = new ArrayList<NotificationPresenter>();

    @Inject
    public BodyPresenter( final View view ) {
        this.view = view;
    }

    @PostConstruct
    public void init() {
        view.init( this );
    }

    public View getView() {
        return view;
    }

    public void setMessages( final Collection<Message> messages ) {
        checkNotNull( "messages", messages );

        if ( messages.isEmpty() ) {
            view.addNotification( setupNotification( true ).getView() );
        } else {
            for ( final Message message : messages ) {
                view.addNotification( setupNotification( message ).getView() );
            }
        }
    }

    NotificationPresenter setupNotification( final Message message ) {
        final NotificationPresenter presenter = setupNotification( false );
        presenter.setup( message );
        return presenter;
    }

    NotificationPresenter setupNotification( boolean init ) {
        final NotificationPresenter presenter = newNotification();
        if ( init ) {
            presenter.setupOk();
        }
        presenters.add( presenter );
        return presenter;
    }

    NotificationPresenter newNotification() {
        return IOC.getBeanManager().lookupBean( NotificationPresenter.class ).getInstance();
    }

}
