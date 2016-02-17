package org.kie.workbench.client.server.management.events;

import org.kie.server.controller.api.model.runtime.ServerInstanceKey;

/**
 * TODO: update me
 */
public class ServerInstanceSelected {

    private final ServerInstanceKey serverInstanceKey;

    public ServerInstanceSelected( final ServerInstanceKey serverInstanceKey ) {
        this.serverInstanceKey = serverInstanceKey;
    }

    public ServerInstanceKey getServerInstanceKey() {
        return serverInstanceKey;
    }
}
