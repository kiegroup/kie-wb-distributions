package org.kie.workbench.client.server.management.widget.card.body.notification;

import javax.enterprise.context.Dependent;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Composite;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;

import static org.uberfire.commons.validation.PortablePreconditions.*;

@Templated
@Dependent
public class NotificationView extends Composite
        implements NotificationPresenter.View {

    @DataField("size")
    Element size = DOM.createSpan();

    @DataField("icon")
    Element icon = DOM.createSpan();

    @Override
    public void setupOk() {
        icon.addClassName( NotificationType.OK.getStyleName() );
        this.size.getStyle().setVisibility( Style.Visibility.HIDDEN );
    }

    @Override
    public void setup( final NotificationType type,
                       final int size ) {
        icon.addClassName( checkNotNull( "type", type ).getStyleName() );
        if ( type.equals( NotificationType.OK ) ) {
            this.size.getStyle().setVisibility( Style.Visibility.HIDDEN );
        } else {
            this.size.getStyle().setVisibility( Style.Visibility.VISIBLE );
            this.size.setInnerText( String.valueOf( size ) );
        }
    }
}
