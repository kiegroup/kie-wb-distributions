package org.kie.workbench.client.server.management.widget.card.body.notification;

/**
 * TODO: update me
 */
public enum NotificationType {
    OK( "pficon-ok" ),
    WARNING( "pficon-warning-triangle-o" ),
    ERROR( "pficon-error-circle-o" );

    private final String styleName;

    NotificationType( final String styleName ) {
        this.styleName = styleName;
    }

    public String getStyleName() {
        return styleName;
    }
}
