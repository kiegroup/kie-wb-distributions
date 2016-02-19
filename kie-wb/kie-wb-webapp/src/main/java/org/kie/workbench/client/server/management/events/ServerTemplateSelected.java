package org.kie.workbench.client.server.management.events;

import org.kie.server.controller.api.model.spec.ServerTemplateKey;

/**
 * TODO: update me
 */
public class ServerTemplateSelected {
    private final ServerTemplateKey serverTemplateKey;

    public ServerTemplateSelected( final ServerTemplateKey serverTemplateKey ) {
        this.serverTemplateKey = serverTemplateKey;
    }

    public ServerTemplateKey getServerTemplateKey() {
        return serverTemplateKey;
    }
}
