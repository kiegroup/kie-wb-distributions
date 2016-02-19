package org.kie.workbench.client.server.management.events;

import org.kie.server.controller.api.model.runtime.ServerInstanceKey;

/**
 * TODO: update me
 */
public class RemoteServerSelected {

    private final ServerInstanceKey serverInstanceKey;

    public RemoteServerSelected( final ServerInstanceKey serverInstanceKey ) {
        this.serverInstanceKey = serverInstanceKey;
    }
}
