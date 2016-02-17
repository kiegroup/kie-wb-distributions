package org.kie.server.controller.api.model;

import java.util.Collection;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.server.controller.api.model.runtime.Container;
import org.kie.server.controller.api.model.spec.ContainerSpec;

@Portable
public class ContainerSpecData {

    private ContainerSpec containerSpec;
    private Collection<Container> containers;

    public ContainerSpecData() {

    }

    public ContainerSpecData( final ContainerSpec containerSpec,
                              final Collection<Container> containers ) {
        this.containerSpec = containerSpec;
        this.containers = containers;
    }

    public ContainerSpec getContainerSpec() {
        return containerSpec;
    }

    public Collection<Container> getContainers() {
        return containers;
    }
}
