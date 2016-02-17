package org.kie.server.controller.api.model.spec.impl;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.server.controller.api.model.MergeMode;
import org.kie.server.controller.api.model.RuntimeStrategy;
import org.kie.server.controller.api.model.spec.ProcessConfig;

@Portable
public class ProcessConfigImpl implements ProcessConfig {

    private RuntimeStrategy runtimeStrategy;
    private String kBase;
    private String kSession;
    private MergeMode mergeMode;

    public ProcessConfigImpl() {
    }

    public ProcessConfigImpl( final RuntimeStrategy runtimeStrategy,
                              final String kBase,
                              final String kSession,
                              final MergeMode mergeMode ) {
        this.runtimeStrategy = runtimeStrategy;
        this.kBase = kBase;
        this.kSession = kSession;
        this.mergeMode = mergeMode;
    }

    @Override
    public RuntimeStrategy getRuntimeStrategy() {
        return runtimeStrategy;
    }

    @Override
    public String getKBase() {
        return kBase;
    }

    @Override
    public String getKSession() {
        return kSession;
    }

    @Override
    public MergeMode getMergeMode() {
        return mergeMode;
    }
}
