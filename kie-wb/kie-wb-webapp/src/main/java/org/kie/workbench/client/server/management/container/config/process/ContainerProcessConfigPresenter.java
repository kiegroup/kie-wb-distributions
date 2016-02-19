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

package org.kie.workbench.client.server.management.container.config.process;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.kie.server.controller.api.model.spec.ContainerConfig;
import org.kie.server.controller.api.model.spec.ContainerSpecKey;
import org.kie.server.controller.api.model.spec.ProcessConfig;
import org.kie.server.controller.api.model.spec.impl.ProcessConfigImpl;
import org.kie.server.controller.api.service.SpecManagementService;
import org.kie.workbench.client.server.management.util.ClientMergeMode;
import org.kie.workbench.client.server.management.util.ClientRuntimeStrategy;
import org.uberfire.client.mvp.UberView;
import org.uberfire.workbench.events.NotificationEvent;

import static org.uberfire.commons.validation.PortablePreconditions.*;

@Dependent
public class ContainerProcessConfigPresenter {

    public interface View extends UberView<ContainerProcessConfigPresenter> {

        void setContent( final String runtimeStrategy,
                         final String kbase,
                         final String ksession,
                         final String mergeMode );

        String getKBase();

        String getKSession();

        String getMergeMode();

        String getRuntimeStrategy();

        void disable();

        void disableActions();

        void enableActions();
    }

    private final View view;
    private final Caller<SpecManagementService> specManagementService;
    private final Event<NotificationEvent> notification;
    private ContainerSpecKey containerSpecKey;
    private ProcessConfig processConfig;

    @Inject
    public ContainerProcessConfigPresenter( final View view,
                                            final Caller<SpecManagementService> specManagementService,
                                            final Event<NotificationEvent> notification ) {
        this.view = view;
        this.specManagementService = specManagementService;
        this.notification = notification;
    }

    @PostConstruct
    public void init() {
        this.view.init( this );
    }

    public View getView() {
        return view;
    }

    public void setup( final ContainerSpecKey containerSpecKey,
                       final ProcessConfig processConfig ) {
        this.processConfig = checkNotNull( "processConfig", processConfig );
        this.containerSpecKey = checkNotNull( "containerSpecKey", containerSpecKey );
        setupView( processConfig );
    }

    private void setupView( final ProcessConfig processConfig ) {
        this.view.setContent( ClientRuntimeStrategy.convert( processConfig.getRuntimeStrategy() ).toString(),
                              processConfig.getKBase(),
                              processConfig.getKSession(),
                              ClientMergeMode.convert( processConfig.getMergeMode() ).toString() );

    }

    public void disable() {
        view.disable();
    }

    public void save() {
        view.disableActions();
        specManagementService.call( new RemoteCallback<ContainerConfig>() {
                                        @Override
                                        public void callback( final ContainerConfig containerConfig ) {
                                            if ( !( containerConfig instanceof ProcessConfig ) ) {
                                                notification.fire( new NotificationEvent( "Container has failed to update.", NotificationEvent.NotificationType.ERROR ) );
                                                setupView( processConfig );
                                            } else {
                                                processConfig = (ProcessConfig) containerConfig;
                                                notification.fire( new NotificationEvent( "Container has been updated.", NotificationEvent.NotificationType.SUCCESS ) );
                                                setupView( processConfig );
                                            }
                                        }
                                    },
                                    new ErrorCallback<Object>() {
                                        @Override
                                        public boolean error( final Object o,
                                                              final Throwable throwable ) {
                                            notification.fire( new NotificationEvent( "Container has failed to update.", NotificationEvent.NotificationType.ERROR ) );
                                            setupView( processConfig );
                                            return false;
                                        }
                                    } )
                .updateContainerConfig( containerSpecKey,
                                        buildProcessConfig() );
    }

    public void cancel() {
        setupView( this.processConfig );
    }

    private ProcessConfig buildProcessConfig() {
        return new ProcessConfigImpl( ClientRuntimeStrategy.convert( view.getRuntimeStrategy() ).getRuntimeStrategy(),
                                      view.getKBase(),
                                      view.getKSession(),
                                      ClientMergeMode.convert( view.getMergeMode() ).getMergeMode() );
    }

}
