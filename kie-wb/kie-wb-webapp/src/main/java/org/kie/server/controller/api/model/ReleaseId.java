package org.kie.server.controller.api.model;

import org.guvnor.common.services.project.model.GAV;
import org.jboss.errai.common.client.api.annotations.Portable;

/**
 * TODO: update me
 */
@Portable
public class ReleaseId extends GAV {

    public ReleaseId() {
    }

    public ReleaseId( final String gavString ) {
        super( gavString );
    }

    public ReleaseId( final String groupId,
                      final String artifactId,
                      final String version ) {
        super( groupId, artifactId, version );
    }
}
