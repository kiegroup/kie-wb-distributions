/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.client.home;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.workbench.client.library.LibraryMonitor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.mvp.impl.ConditionalPlaceRequest;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class HomeProducerTest {

    @Mock
    private PlaceManager placeManager;

    @Mock
    private LibraryMonitor libraryMonitor;

    @InjectMocks
    private HomeProducer homeProducer;

    @Test
    public void authoringRedirectsToLibraryIfThereIsNoProjectAccessibleTest() {
        doReturn( false ).when( libraryMonitor ).thereIsAtLeastOneProjectAccessible();

        final ConditionalPlaceRequest authoringPlaceRequest = (ConditionalPlaceRequest) homeProducer.getAuthoringPlaceRequest();

        assertEquals( "LibraryPerspective", authoringPlaceRequest.resolveConditionalPlaceRequest().getIdentifier() );
    }

    @Test
    public void authoringDoesntRedirectToLibraryIfThereIsAtLeastOneProjectAccessibleTest() {
        doReturn( true ).when( libraryMonitor ).thereIsAtLeastOneProjectAccessible();

        final ConditionalPlaceRequest authoringPlaceRequest = (ConditionalPlaceRequest) homeProducer.getAuthoringPlaceRequest();

        assertEquals( "AuthoringPerspective", authoringPlaceRequest.resolveConditionalPlaceRequest().getIdentifier() );
    }
}
