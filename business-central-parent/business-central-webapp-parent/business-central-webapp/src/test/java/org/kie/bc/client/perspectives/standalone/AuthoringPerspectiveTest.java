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

import javax.enterprise.event.Event;

import org.guvnor.common.services.project.client.security.ProjectController;
import org.guvnor.common.services.project.context.WorkspaceProjectContextChangeEvent;
import org.guvnor.common.services.project.model.WorkspaceProject;
import org.guvnor.common.services.project.service.WorkspaceProjectService;
import org.jboss.errai.common.client.api.Caller;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.VFSService;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.promise.Promises;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.promise.SyncPromises;
import org.uberfire.workbench.events.NotificationEvent;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class AuthoringPerspectiveTest {

    @Mock
    private PlaceManager placeManager;

    @Mock
    private VFSService vfsServices;
    private Caller<VFSService> vfsServicesCaller;

    @Mock
    private EventSourceMock<WorkspaceProjectContextChangeEvent> workspaceProjectContextChangeEvent;

    @Mock
    private WorkspaceProjectService workspaceProjectService;
    private Caller<WorkspaceProjectService> workspaceProjectServiceCaller;

    @Mock
    private ProjectController projectController;

    private Promises promises;

    @Mock
    private Event<NotificationEvent> notificationEvent;

    private AuthoringPerspective authoringPerspective;

    @Before
    public void setup() {
        vfsServicesCaller = new CallerMock<>(vfsServices);
        workspaceProjectServiceCaller = new CallerMock<>(workspaceProjectService);
        promises = new SyncPromises();
        authoringPerspective = spy(new AuthoringPerspective(placeManager,
                                                            vfsServicesCaller,
                                                            workspaceProjectContextChangeEvent,
                                                            workspaceProjectServiceCaller,
                                                            projectController,
                                                            promises,
                                                            notificationEvent));
    }

    @Test
    public void onOpenTest() {
        final Path path = mock(Path.class);
        final WorkspaceProject workspaceProject = mock(WorkspaceProject.class);
        authoringPerspective.projectPathString = "git://master@MySpace/MyProject/src/main/java/com/myspace/myproject/myasset.java";

        doReturn(promises.resolve(true)).when(projectController).canReadBranch(workspaceProject);
        doReturn(true).when(vfsServices).isRegularFile(authoringPerspective.projectPathString);
        doReturn(path).when(vfsServices).get(authoringPerspective.projectPathString);
        doReturn(workspaceProject).when(workspaceProjectService).resolveProject(path);

        authoringPerspective.onOpen();

        verify(placeManager).closeAllPlaces();
        verify(workspaceProjectContextChangeEvent).fire(new WorkspaceProjectContextChangeEvent(workspaceProject));
        verify(placeManager).goTo(same(path));
    }

    @Test
    public void onOpenWithoutPermissionTest() {
        final Path path = mock(Path.class);
        authoringPerspective.projectPathString = "git://master@MySpace/MyProject/src/main/java/com/myspace/myproject/myasset.java";

        doReturn(promises.resolve(false)).when(projectController).canReadBranch(any());
        doReturn(true).when(vfsServices).isRegularFile(authoringPerspective.projectPathString);
        doReturn(path).when(vfsServices).get(authoringPerspective.projectPathString);
        doReturn("AssetAccessDenied").when(authoringPerspective).getAssetAccessDeniedMessage();

        authoringPerspective.onOpen();

        verify(placeManager).closeAllPlaces();
        verify(notificationEvent).fire(new NotificationEvent("AssetAccessDenied",
                                                             NotificationEvent.NotificationType.ERROR));
        verify(workspaceProjectContextChangeEvent, never()).fire(any());
        verify(placeManager, never()).goTo(any(PlaceRequest.class));
    }
}
