package org.kie.server.controller.api.model.spec.impl;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.server.controller.api.model.spec.RuleConfig;
import org.kie.server.controller.api.model.spec.ScannerStatus;

@Portable
public class RuleConfigImpl implements RuleConfig {

    private Long pollInterval;
    private ScannerStatus scannerStatus;

    public RuleConfigImpl() {

    }

    public RuleConfigImpl( final Long pollInterval,
                           final ScannerStatus scannerStatus ) {
        this.pollInterval = pollInterval;
        this.scannerStatus = scannerStatus;
    }

    @Override
    public Long getPollInterval() {
        return pollInterval;
    }

    @Override
    public ScannerStatus getScannerStatus() {
        return scannerStatus;
    }
}
