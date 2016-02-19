package org.kie.workbench.client.server.management.widget.card.body.notification;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.kie.server.controller.api.model.runtime.Message;
import org.kie.server.controller.api.model.runtime.Severity;

import static org.uberfire.commons.validation.PortablePreconditions.*;

@Dependent
public class NotificationPresenter {

    public interface View extends IsWidget {

        void setupOk();

        void setup( final NotificationType type,
                    final int size );
    }

    private final View view;

    @Inject
    public NotificationPresenter( final View view ) {
        this.view = view;
    }

    public View getView() {
        return view;
    }

    public void setupOk() {
        view.setupOk();
    }

    public void setup( final Message message ) {
        checkNotNull( "message", message );
        view.setup( toNotificationType( message.getSeverity() ), message.getMessages().size() );
    }

    private NotificationType toNotificationType( final Severity severity ) {
        return checkNotNull( "severity", severity ).equals( Severity.ERROR ) ? NotificationType.ERROR : NotificationType.WARNING;
    }

}
