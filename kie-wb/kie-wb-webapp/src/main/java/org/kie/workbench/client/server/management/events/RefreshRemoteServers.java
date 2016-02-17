package org.kie.workbench.client.server.management.events;

import org.kie.server.controller.api.model.spec.ContainerSpecKey;

public class RefreshRemoteServers {

    private final ContainerSpecKey containerSpecKey;

    public RefreshRemoteServers( final ContainerSpecKey containerSpecKey ) {
        this.containerSpecKey = containerSpecKey;
    }

    public ContainerSpecKey getContainerSpecKey() {
        return containerSpecKey;
    }
}
