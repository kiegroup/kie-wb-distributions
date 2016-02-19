package org.kie.server.controller.api.events;

import org.guvnor.common.services.project.model.GAV;
import org.kie.server.controller.api.model.spec.ContainerSpecKey;
import org.kie.server.controller.api.model.spec.RuleConfig;

/**
 * TODO: update me
 */
public interface RuleConfigUpdate {

    ContainerSpecKey getContainerSpecKey();

    RuleConfig getRuleConfig();

    GAV getReleasedId();

}
