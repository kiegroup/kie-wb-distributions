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

package org.kie.workbench.common.screens.home.model;

import org.uberfire.commons.validation.PortablePreconditions;
import org.uberfire.security.Resource;
import org.uberfire.security.ResourceAction;
import org.uberfire.security.ResourceRef;
import org.uberfire.workbench.model.ActivityResourceType;

/**
 * A Section on the Home Page
 */
public class Section {

    private final String heading;
    private final String description;
    private final String imageUrl;
    private String permission = null;
    private Resource resource = null;
    private ResourceAction resourceAction = null;

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

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    public Resource getResource() {
        return resource;
    }

    public void setResource(Resource resource) {
        this.resource = resource;
    }

    public void setPerspectiveId(String perspectiveId) {
        this.resource = new ResourceRef(perspectiveId, ActivityResourceType.PERSPECTIVE );
    }

    public ResourceAction getResourceAction() {
        return resourceAction;
    }

    public void setResourceAction(ResourceAction resourceAction) {
        this.resourceAction = resourceAction;
    }
}
