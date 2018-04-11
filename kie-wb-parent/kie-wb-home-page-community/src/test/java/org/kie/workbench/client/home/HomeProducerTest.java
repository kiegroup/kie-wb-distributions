/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.client.home;

import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.client.resources.i18n.Constants;
import org.kie.workbench.common.screens.home.client.widgets.shortcut.utils.ShortcutHelper;
import org.kie.workbench.common.screens.home.model.HomeModel;
import org.kie.workbench.common.workbench.client.PerspectiveIds;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.client.mvp.PlaceManager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class HomeProducerTest {

    @Mock
    private PlaceManager placeManager;

    @Mock
    private TranslationService translationService;

    @Mock
    private ShortcutHelper shortcutHelper;

    private HomeProducer producer;

    @Before
    public void setup() {
        producer = new HomeProducer(placeManager,
                                    translationService,
                                    shortcutHelper);
        doAnswer((InvocationOnMock invocation) -> invocation.getArguments()[0]).when(translationService).format(anyString());
    }

    @Test
    public void checkSetupWithProvisioningGranted() {
        when(shortcutHelper.authorize(PerspectiveIds.PROVISIONING)).thenReturn(true);

        final HomeModel model = producer.get();

        assertHomeModel(model,
                        Constants.DeployDescription2);
        assertDesign(model);
        assertManage(model);
        assertTrack(model);

        assertEquals(2,
                     model.getShortcuts().get(1).getLinks().size());
        assertEquals(Constants.Provisioning,
                     model.getShortcuts().get(1).getLinks().get(0).getLabel());
        assertEquals(PerspectiveIds.PROVISIONING,
                     model.getShortcuts().get(1).getLinks().get(0).getPerspectiveIdentifier());
        assertEquals(Constants.Servers,
                     model.getShortcuts().get(1).getLinks().get(1).getLabel());
        assertEquals(PerspectiveIds.SERVER_MANAGEMENT,
                     model.getShortcuts().get(1).getLinks().get(1).getPerspectiveIdentifier());
    }

    @Test
    public void checkSetupWithProvisioningDenied() {
        when(shortcutHelper.authorize(PerspectiveIds.PROVISIONING)).thenReturn(false);

        final HomeModel model = producer.get();

        assertNotNull(model);

        assertHomeModel(model,
                        Constants.DeployDescription1);
        assertDesign(model);
        assertManage(model);
        assertTrack(model);

        assertEquals(1,
                     model.getShortcuts().get(1).getLinks().size());
        assertEquals(Constants.Servers,
                     model.getShortcuts().get(1).getLinks().get(0).getLabel());
        assertEquals(PerspectiveIds.SERVER_MANAGEMENT,
                     model.getShortcuts().get(1).getLinks().get(0).getPerspectiveIdentifier());
    }

    protected void assertHomeModel(final HomeModel model,
                                   final String deployDescription) {
        assertNotNull(model);

        assertEquals(Constants.Heading,
                     model.getWelcome());
        assertEquals(Constants.SubHeading,
                     model.getDescription());

        assertEquals(4,
                     model.getShortcuts().size());
        assertEquals(Constants.Design,
                     model.getShortcuts().get(0).getHeading());
        assertEquals(Constants.DesignDescription,
                     model.getShortcuts().get(0).getSubHeading());
        assertEquals(Constants.Deploy,
                     model.getShortcuts().get(1).getHeading());
        assertEquals(deployDescription,
                     model.getShortcuts().get(1).getSubHeading());
    }

    private void assertDesign(final HomeModel model) {
        assertEquals(2,
                     model.getShortcuts().get(0).getLinks().size());
        assertEquals(Constants.Projects,
                     model.getShortcuts().get(0).getLinks().get(0).getLabel());
        assertEquals(PerspectiveIds.LIBRARY,
                     model.getShortcuts().get(0).getLinks().get(0).getPerspectiveIdentifier());
        assertEquals(Constants.Pages,
                     model.getShortcuts().get(0).getLinks().get(1).getLabel());
        assertEquals(PerspectiveIds.CONTENT_MANAGEMENT,
                     model.getShortcuts().get(0).getLinks().get(1).getPerspectiveIdentifier());
    }

    private void assertManage(final HomeModel model) {
        assertEquals(5,
                     model.getShortcuts().get(2).getLinks().size());
        assertEquals(Constants.ProcessDefinitions,
                     model.getShortcuts().get(2).getLinks().get(0).getLabel());
        assertEquals(PerspectiveIds.PROCESS_DEFINITIONS,
                     model.getShortcuts().get(2).getLinks().get(0).getPerspectiveIdentifier());
        assertEquals(Constants.ProcessInstances,
                     model.getShortcuts().get(2).getLinks().get(1).getLabel());
        assertEquals(PerspectiveIds.PROCESS_INSTANCES,
                     model.getShortcuts().get(2).getLinks().get(1).getPerspectiveIdentifier());
        assertEquals(Constants.Tasks,
                     model.getShortcuts().get(2).getLinks().get(2).getLabel());
        assertEquals(PerspectiveIds.TASKS_ADMIN,
                     model.getShortcuts().get(2).getLinks().get(2).getPerspectiveIdentifier());
        assertEquals(Constants.Jobs,
                     model.getShortcuts().get(2).getLinks().get(3).getLabel());
        assertEquals(PerspectiveIds.JOBS,
                     model.getShortcuts().get(2).getLinks().get(3).getPerspectiveIdentifier());
        assertEquals(Constants.ExecutionErrors,
                     model.getShortcuts().get(2).getLinks().get(4).getLabel());
        assertEquals(PerspectiveIds.EXECUTION_ERRORS,
                     model.getShortcuts().get(2).getLinks().get(4).getPerspectiveIdentifier());
    }

    private void assertTrack(final HomeModel model) {
        assertEquals(3,
                     model.getShortcuts().get(3).getLinks().size());
        assertEquals(Constants.TaskInbox,
                     model.getShortcuts().get(3).getLinks().get(0).getLabel());
        assertEquals(PerspectiveIds.TASKS,
                     model.getShortcuts().get(3).getLinks().get(0).getPerspectiveIdentifier());
        assertEquals(Constants.ProcessReports,
                     model.getShortcuts().get(3).getLinks().get(1).getLabel());
        assertEquals(PerspectiveIds.PROCESS_DASHBOARD,
                     model.getShortcuts().get(3).getLinks().get(1).getPerspectiveIdentifier());
        assertEquals(Constants.TaskReports,
                     model.getShortcuts().get(3).getLinks().get(2).getLabel());
        assertEquals(PerspectiveIds.TASK_DASHBOARD,
                     model.getShortcuts().get(3).getLinks().get(2).getPerspectiveIdentifier());
    }
}
