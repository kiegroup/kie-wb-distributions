package org.kie.server.controller.api.model.runtime.impl;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.server.controller.api.model.runtime.ContainerKey;
import org.kie.server.controller.api.model.runtime.ServerInstanceKey;

@Portable
public class ContainerKeyImpl implements ContainerKey {

    private String containerSpecId;
    private String containerName;
    private ServerInstanceKeyImpl serverInstanceKey;

    public ContainerKeyImpl() {

    }

    public ContainerKeyImpl( final String containerSpecId,
                             final String containerName,
                             final ServerInstanceKeyImpl serverInstanceKey ) {
        this.containerSpecId = containerSpecId;
        this.containerName = containerName;
        this.serverInstanceKey = serverInstanceKey;
    }

    @Override
    public String getContainerSpecId() {
        return containerSpecId;
    }

    @Override
    public String getContainerName() {
        return containerName;
    }

    @Override
    public ServerInstanceKey getServiceInstanceKey() {
        return serverInstanceKey;
    }
}
