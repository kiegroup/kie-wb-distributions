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

import java.util.Map;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.server.controller.api.model.KieContainerStatus;
import org.kie.server.controller.api.model.ReleaseId;
import org.kie.server.controller.api.model.spec.Capability;
import org.kie.server.controller.api.model.spec.ContainerConfig;
import org.kie.server.controller.api.model.spec.ContainerSpec;
import org.kie.server.controller.api.model.spec.ServerTemplateKey;

import static org.uberfire.commons.validation.PortablePreconditions.*;

@Portable
public class ContainerSpecImpl extends ContainerSpecKeyImpl implements ContainerSpec {

    private ReleaseId releasedId;
    private KieContainerStatus status;
    private Map<Capability, ContainerConfig> configs;

    public ContainerSpecImpl() {
    }

    public ContainerSpecImpl( final String id,
                              final String containerName,
                              final ServerTemplateKey serverTemplateKey,
                              final ReleaseId releasedId,
                              final KieContainerStatus status,
                              final Map<Capability, ContainerConfig> configs ) {
        super( id, containerName, serverTemplateKey );
        this.releasedId = checkNotNull( "releasedId", releasedId );
        this.status = status;
        this.configs = configs;
    }

    @Override
    public ReleaseId getReleasedId() {
        return releasedId;
    }

    @Override
    public Map<Capability, ContainerConfig> getConfigs() {
        return configs;
    }

    @Override
    public KieContainerStatus getStatus() {
        return status;
    }
}
