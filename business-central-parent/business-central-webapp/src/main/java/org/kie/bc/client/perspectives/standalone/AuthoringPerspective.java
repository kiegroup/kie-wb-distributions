/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.bc.client.perspectives.standalone;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.gwt.user.client.Window;
import org.guvnor.common.services.project.client.security.ProjectController;
import org.guvnor.common.services.project.context.WorkspaceProjectContextChangeEvent;
import org.guvnor.common.services.project.model.WorkspaceProject;
import org.guvnor.common.services.project.service.WorkspaceProjectService;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.kie.bc.client.resources.i18n.AppConstants;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.VFSService;
import org.uberfire.client.annotations.Perspective;
import org.uberfire.client.annotations.WorkbenchPerspective;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.promise.Promises;
import org.uberfire.client.workbench.panels.impl.MultiListWorkbenchPanelPresenter;
import org.uberfire.lifecycle.OnOpen;
import org.uberfire.workbench.events.NotificationEvent;
import org.uberfire.workbench.model.PerspectiveDefinition;
import org.uberfire.workbench.model.impl.PerspectiveDefinitionImpl;

@ApplicationScoped
@WorkbenchPerspective(identifier = AuthoringPerspective.IDENTIFIER)
public class AuthoringPerspective {

    public static final String IDENTIFIER = "AuthoringPerspective";

    private PlaceManager placeManager;

    private Caller<VFSService> vfsServices;

    private Event<WorkspaceProjectContextChangeEvent> workspaceProjectContextChangeEvent;

    private Caller<WorkspaceProjectService> workspaceProjectService;

    private ProjectController projectController;

    private Promises promises;

    private Event<NotificationEvent> notificationEvent;

    String projectPathString;

    @Inject
    public AuthoringPerspective(final PlaceManager placeManager,
                                final Caller<VFSService> vfsServices,
                                final Event<WorkspaceProjectContextChangeEvent> workspaceProjectContextChangeEvent,
                                final Caller<WorkspaceProjectService> workspaceProjectService,
                                final ProjectController projectController,
                                final Promises promises,
                                final Event<NotificationEvent> notificationEvent) {
        this.placeManager = placeManager;
        this.vfsServices = vfsServices;
        this.workspaceProjectContextChangeEvent = workspaceProjectContextChangeEvent;
        this.workspaceProjectService = workspaceProjectService;
        this.projectController = projectController;
        this.promises = promises;
        this.notificationEvent = notificationEvent;
    }

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
            vfsServices.call((RemoteCallback<Boolean>) isRegularFile -> {
                if (isRegularFile) {
                    vfsServices.call((RemoteCallback<Path>) path -> {
                        setWorkspaceContext(path, () -> placeManager.goTo(path));
                    }).get(projectPathString);
                }
            }).isRegularFile(projectPathString);
        }
    }

    private void setWorkspaceContext(final Path assetPath,
                                     final Runnable callback) {
        workspaceProjectService.call((RemoteCallback<WorkspaceProject>) workspaceProject -> {
            projectController.canReadBranch(workspaceProject).then(canReadPath -> {
                if (!canReadPath) {
                    notificationEvent.fire(new NotificationEvent(getAssetAccessDeniedMessage(),
                                                                 NotificationEvent.NotificationType.ERROR));
                    return promises.resolve();
                }

                workspaceProjectContextChangeEvent.fire(new WorkspaceProjectContextChangeEvent(workspaceProject));
                callback.run();
                return promises.resolve();
            });
        }).resolveProject(assetPath);
    }

    String getAssetAccessDeniedMessage() {
        return AppConstants.INSTANCE.AssetAccessDenied();
    }
}
