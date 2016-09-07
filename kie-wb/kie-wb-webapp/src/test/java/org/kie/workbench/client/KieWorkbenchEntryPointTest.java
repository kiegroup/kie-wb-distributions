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

package org.kie.workbench.client;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.security.shared.service.AuthenticationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.uberfire.client.mvp.PerspectiveManager;
import org.uberfire.mvp.Command;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class KieWorkbenchEntryPointTest {

    @Mock
    private PerspectiveManager perspectiveManager;

    @Mock
    private Caller<AuthenticationService> authService;

    @Mock
    private AuthenticationService authServiceImpl;

    @Spy
    @InjectMocks
    private KieWorkbenchEntryPoint entryPoint;

    @Before
    public void setup() {
        //@InjectMocks is only setting the first Caller<..> which is not AuthenticationService so set it manually.
        entryPoint.authService = authService;
    }

    @Test
    public void logoutCommandTest() {
        final KieWorkbenchEntryPoint.LogoutCommand logoutCommand = spy( entryPoint.new LogoutCommand() );

        logoutCommand.execute();

        verify( perspectiveManager ).savePerspectiveState( any( Command.class ) );
    }

    @Test
    @SuppressWarnings("unchecked")
    public void logoutCommandRedirectIncludesLocaleTest() throws Throwable {
        final KieWorkbenchEntryPoint.LogoutCommand logoutCommand = spy( entryPoint.new LogoutCommand() {

            @Override
            void doRedirect( final String url ) {
                //Do nothing
            }

            @Override
            String getGWTModuleBaseURL() {
                return "/gwtModule/";
            }

            @Override
            String getGWTModuleName() {
                return "gwtModule";
            }

            @Override
            String getLocale() {
                return "en_GB";
            }
        } );

        when( authService.call( any( RemoteCallback.class ) ) ).thenAnswer( new Answer<AuthenticationService>() {
            @Override
            public AuthenticationService answer( InvocationOnMock invocation ) throws Throwable {
                ( (RemoteCallback) invocation.getArguments()[ 0 ] ).callback( null );
                return authServiceImpl;
            }
        } );

        logoutCommand.execute();

        final ArgumentCaptor<Command> postSaveStateCommandCaptor = ArgumentCaptor.forClass( Command.class );
        final ArgumentCaptor<String> redirectURLCaptor = ArgumentCaptor.forClass( String.class );

        verify( perspectiveManager ).savePerspectiveState( postSaveStateCommandCaptor.capture() );

        final Command postSaveStateCommand = postSaveStateCommandCaptor.getValue();
        postSaveStateCommand.execute();

        verify( logoutCommand ).getRedirectURL();
        verify( logoutCommand ).doRedirect( redirectURLCaptor.capture() );
        verify( authServiceImpl ).logout();

        final String redirectURL = redirectURLCaptor.getValue();
        assertTrue( redirectURL.contains( "/logout.jsp?locale=en_GB" ) );
    }

}
