/*
 * Copyright Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.workbench.client.perspectives;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.google.gwt.user.client.Window;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.kie.workbench.client.resources.i18n.AppConstants;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.VFSService;
import org.uberfire.client.annotations.Perspective;
import org.uberfire.client.annotations.WorkbenchPerspective;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.workbench.panels.impl.MultiListWorkbenchPanelPresenter;
import org.uberfire.lifecycle.OnOpen;
import org.uberfire.workbench.model.PerspectiveDefinition;
import org.uberfire.workbench.model.impl.PerspectiveDefinitionImpl;

@ApplicationScoped
@WorkbenchPerspective(identifier = "AuthoringPerspective", isTransient = false)
public class DroolsAuthoringPerspective {

    @Inject
    private PlaceManager placeManager;

    @Inject
    private Caller<VFSService> vfsServices;

    private String projectPathString;

    @PostConstruct
    public void init() {
        projectPathString = (((Window.Location.getParameterMap().containsKey("path")) ? Window.Location.getParameterMap().get("path").get(0) : "")).trim();
    }

    @Perspective
    public PerspectiveDefinition getPerspective() {
        final PerspectiveDefinitionImpl perspective = new PerspectiveDefinitionImpl(MultiListWorkbenchPanelPresenter.class.getName());
        perspective.setName(AppConstants.INSTANCE.Project_Authoring());

        return perspective;
    }

    @OnOpen
    public void onOpen() {
        placeManager.closeAllPlaces();

        if (!projectPathString.isEmpty()) {
            vfsServices.call(new RemoteCallback<Boolean>() {
                @Override
                public void callback(Boolean isRegularFile) {
                    if (isRegularFile) {
                        vfsServices.call(new RemoteCallback<Path>() {
                            @Override
                            public void callback(Path path) {
                                placeManager.goTo(path);
                            }
                        }).get(projectPathString);
                    }
                }
            }).isRegularFile(projectPathString);
        }
    }
}
