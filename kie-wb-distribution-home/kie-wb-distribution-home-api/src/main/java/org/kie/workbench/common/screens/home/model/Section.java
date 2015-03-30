package org.kie.workbench.common.screens.home.model;

import java.util.ArrayList;
import java.util.Collection;

import org.uberfire.commons.validation.PortablePreconditions;
import org.uberfire.security.authz.RuntimeFeatureResource;

import static java.util.Collections.*;

/**
 * A Section on the Home Page
 */
public class Section implements RuntimeFeatureResource {

    private final String heading;
    private Collection<String> roles = new ArrayList<String>();
    private final String description;
    private final String imageUrl;

    public Section( final String heading,
                    final String description,
                    final String imageUrl ) {
        this.heading = PortablePreconditions.checkNotNull( "heading",
                                                           heading );
        this.description = PortablePreconditions.checkNotNull( "description",
                                                               description );
        this.imageUrl = PortablePreconditions.checkNotNull( "imageUrl",
                                                            imageUrl );
    }

    public String getHeading() {
        return heading;
    }

    public String getDescription() {
        return description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setRoles( Collection<String> roles ) {
        this.roles = PortablePreconditions.checkNotNull( "roles", roles );
    }

    @Override
    public String getSignatureId() {
        return getClass().getName() + "#" + heading;
    }

    @Override
    public Collection<String> getRoles() {
        return roles;
    }

    @Override
    public Collection<String> getTraits() {
        return emptyList();
    }

}
