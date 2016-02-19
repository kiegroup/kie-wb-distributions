/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.kie.server.controller.api.model.spec.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.server.controller.api.model.runtime.ServerInstanceKey;
import org.kie.server.controller.api.model.spec.Capability;
import org.kie.server.controller.api.model.spec.ContainerSpec;
import org.kie.server.controller.api.model.spec.ServerConfig;
import org.kie.server.controller.api.model.spec.ServerTemplate;

import static org.uberfire.commons.validation.PortablePreconditions.*;

@Portable
public class ServerTemplateImpl extends ServerTemplateKeyImpl
        implements ServerTemplate {

    private Map<Capability, ServerConfig> configs = new HashMap<Capability, ServerConfig>();
    private Collection<Capability> capabilities = new ArrayList<Capability>();
    private Map<String, ContainerSpec> containersSpec = new HashMap<String, ContainerSpec>();
    private Collection<ServerInstanceKey> serverIntanceKeys = new ArrayList<ServerInstanceKey>();

    public ServerTemplateImpl() {
    }

    public ServerTemplateImpl( final String id,
                               final String name ) {
        super( id, name );
    }

    public ServerTemplateImpl( final String id,
                               final String name,
                               final Collection<Capability> capabilities,
                               final Map<Capability, ServerConfig> configs,
                               final Collection<ContainerSpec> containersSpec ) {
        super( id, name );
        this.capabilities.addAll( checkNotNull( "capabilities", capabilities ) );
        this.configs.putAll( checkNotNull( "configs", configs ) );
        checkNotNull( "containersSpec", containersSpec );
        for ( ContainerSpec containerSpec : containersSpec ) {
            this.containersSpec.put( containerSpec.getId(), containerSpec );
        }
    }

    public ServerTemplateImpl( final String id,
                               final String name,
                               final Collection<Capability> capabilities,
                               final Map<Capability, ServerConfig> configs,
                               final Collection<ContainerSpec> containersSpec,
                               final Collection<ServerInstanceKey> serverIntanceKeys ) {
        this( id, name, capabilities, configs, containersSpec );
        serverIntanceKeys.addAll( checkNotNull( "serverIntanceKeys", serverIntanceKeys ) );
    }

    @Override
    public Collection<Capability> getCapabilities() {
        return capabilities;
    }

    @Override
    public Map<Capability, ServerConfig> getConfigs() {
        return configs;
    }

    @Override
    public Collection<ContainerSpec> getContainersSpec() {
        return containersSpec.values();
    }

    @Override
    public boolean hasContainerSpec( final String containerSpecId ) {
        return containersSpec.containsKey( checkNotEmpty( "containerSpecId", containerSpecId ) );
    }

    @Override
    public ContainerSpec getContainerSpec( final String containerSpecId ) {
        return containersSpec.get( checkNotEmpty( "containerSpecId", containerSpecId ) );
    }

    @Override
    public void addContainerSpec( final ContainerSpec containerSpec ) {
        checkNotNull( "containerSpec", containerSpec );
        if ( hasContainerSpec( containerSpec.getId() ) ) {
            containersSpec.remove( containerSpec.getId() );
        }
        containersSpec.put( containerSpec.getId(), containerSpec );
    }

    @Override
    public void deleteContainerSpec( final String containerSpecId ) {
        containersSpec.remove( checkNotEmpty( "containerSpecId", containerSpecId ) );
    }

    @Override
    public Collection<ServerInstanceKey> getServerIntanceKeys() {
        return serverIntanceKeys;
    }
}
