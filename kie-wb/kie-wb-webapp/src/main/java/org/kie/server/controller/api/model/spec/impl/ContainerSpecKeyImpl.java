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

import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.server.controller.api.model.spec.ContainerSpecKey;
import org.kie.server.controller.api.model.spec.ServerTemplateKey;

@Portable
public class ContainerSpecKeyImpl implements ContainerSpecKey {

    private String id;
    private String containerName;
    private ServerTemplateKey serverTemplateKey;

    public ContainerSpecKeyImpl() {

    }

    public ContainerSpecKeyImpl( final String id,
                                 final String containerName,
                                 final ServerTemplateKey serverTemplateKey ) {
        this.id = id;
        this.containerName = containerName;
        this.serverTemplateKey = serverTemplateKey;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getContainerName() {
        return containerName;
    }

    @Override
    public ServerTemplateKey getServerTemplateKey() {
        return serverTemplateKey;
    }
}
