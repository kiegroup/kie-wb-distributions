package org.kie.server.controller.api.model.runtime.impl;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.server.controller.api.model.runtime.ServerInstanceKey;

@Portable
public class ServerInstanceKeyImpl implements ServerInstanceKey {

    private String serverTemplateId;
    private String serverName;
    private String serverInstanceId;
    private String url;

    public ServerInstanceKeyImpl() {
    }

    public ServerInstanceKeyImpl( final String serverTemplateId,
                                  final String serverName,
                                  final String serverInstanceId,
                                  final String url ) {
        this.serverTemplateId = serverTemplateId;
        this.serverName = serverName;
        this.serverInstanceId = serverInstanceId;
        this.url = url;
    }

    @Override
    public String getServerTemplateId() {
        return serverTemplateId;
    }

    @Override
    public String getServerName() {
        return serverName;
    }

    @Override
    public String getServerInstanceId() {
        return serverInstanceId;
    }

    @Override
    public String getUrl() {
        return url;
    }
}
