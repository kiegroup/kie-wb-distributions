/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

import org.junit.runner.RunWith;
import org.kie.workbench.client.resources.i18n.Constants;
import org.kie.workbench.common.screens.home.model.HomeModel;
import org.kie.workbench.common.workbench.client.PerspectiveIds;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(MockitoJUnitRunner.class)
public class HomeRuntimeProducerTest extends HomeProducerTest {

    @Override
    protected AbstractHomeProducer createHomeProducer() {
        return new HomeRuntimeProducer(placeManager,
                translationService,
                shortcutHelper);
    }

    @Override
    protected void assertHomeModel(final HomeModel model,
            final String devOpsDescription) {
        assertNotNull(model);

        assertEquals(Constants.Heading,
                model.getWelcome());
        assertEquals(Constants.SubHeading,
                model.getDescription());

        assertEquals(4,
                model.getShortcuts().size());
        assertEquals(Constants.Design,
                model.getShortcuts().get(0).getHeading());
        assertEquals(Constants.DesignRuntimeDescription,
                model.getShortcuts().get(0).getSubHeading());
        assertEquals(Constants.DevOps,
                model.getShortcuts().get(1).getHeading());
        assertEquals(devOpsDescription,
                model.getShortcuts().get(1).getSubHeading());
    }

    @Override
    protected void assertDesign(final HomeModel model) {
        assertEquals(1,
                model.getShortcuts().get(0).getLinks().size());
        assertEquals(Constants.Pages,
                model.getShortcuts().get(0).getLinks().get(0).getLabel());
        assertEquals(PerspectiveIds.BUSINESS_DASHBOARDS,
                model.getShortcuts().get(0).getLinks().get(0).getPerspectiveIdentifier());
    }
}
