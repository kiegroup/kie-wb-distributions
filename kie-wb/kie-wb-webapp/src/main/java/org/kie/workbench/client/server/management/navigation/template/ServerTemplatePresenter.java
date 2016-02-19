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

package org.kie.workbench.client.server.management.navigation.template;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.kie.server.controller.api.model.runtime.ServerInstanceKey;
import org.kie.server.controller.api.model.spec.Capability;
import org.kie.server.controller.api.model.spec.ContainerSpec;
import org.kie.server.controller.api.model.spec.ServerTemplate;
import org.kie.server.controller.api.service.SpecManagementService;
import org.kie.workbench.client.server.management.events.AddNewContainer;
import org.kie.workbench.client.server.management.events.ContainerSpecSelected;
import org.kie.workbench.client.server.management.events.ServerInstanceSelected;
import org.kie.workbench.client.server.management.events.ServerTemplateListRefresh;
import org.kie.workbench.client.server.management.navigation.template.copy.CopyPopupPresenter;
import org.uberfire.client.mvp.UberView;
import org.uberfire.ext.widgets.common.client.common.popups.YesNoCancelPopup;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.ParameterizedCommand;
import org.uberfire.workbench.events.NotificationEvent;

@ApplicationScoped
public class ServerTemplatePresenter {

    public interface View extends UberView<ServerTemplatePresenter> {

        void clear();

        void setTemplate( final String id,
                          final String name );

        void select( final String serverTemplateId,
                     final String id );

        void addContainer( final String serverTemplateId,
                           final String containerSpecId,
                           final String containerName,
                           final Command onSelect );

        void addServerInstance( final String serverTemplateId,
                                final String serverInstanceId,
                                final String serverName,
                                final Command onSelect );

        void setRulesCapability( boolean value );

        void setProcessCapability( boolean value );

        void setPlanningCapability( final boolean value );

        void confirmRemove( Command command );
    }

    private final View view;
    private final CopyPopupPresenter copyPresenter;
    private final Caller<SpecManagementService> specManagementService;
    private final Event<NotificationEvent> notification;

    private final Event<AddNewContainer> addNewContainerEvent;
    private final Event<ContainerSpecSelected> containerSpecSelectedEvent;
    private final Event<ServerInstanceSelected> serverInstanceSelectedEvent;
    private final Event<ServerTemplateListRefresh> serverTemplateListRefreshEvent;

    private String templateId;

    @Inject
    public ServerTemplatePresenter( final View view,
                                    final CopyPopupPresenter copyPresenter,
                                    final Caller<SpecManagementService> specManagementService,
                                    final Event<NotificationEvent> notification,
                                    final Event<AddNewContainer> addNewContainerEvent,
                                    final Event<ContainerSpecSelected> containerSpecSelectedEvent,
                                    final Event<ServerInstanceSelected> serverInstanceSelectedEvent,
                                    final Event<ServerTemplateListRefresh> serverTemplateListRefreshEvent ) {
        this.view = view;
        this.copyPresenter = copyPresenter;
        this.specManagementService = specManagementService;
        this.notification = notification;
        this.addNewContainerEvent = addNewContainerEvent;
        this.containerSpecSelectedEvent = containerSpecSelectedEvent;
        this.serverInstanceSelectedEvent = serverInstanceSelectedEvent;
        this.serverTemplateListRefreshEvent = serverTemplateListRefreshEvent;
    }

    @PostConstruct
    public void init() {
        view.init( this );
    }

    public View getView() {
        return view;
    }

    public void setup( final ServerTemplate serverTemplate,
                       final ContainerSpec firstContainerSpec ) {
        view.clear();
        this.templateId = serverTemplate.getId();
        view.setTemplate( serverTemplate.getId(), serverTemplate.getName() );

        view.setProcessCapability( serverTemplate.getCapabilities().contains( Capability.PROCESS ) );

        view.setRulesCapability( serverTemplate.getCapabilities().contains( Capability.RULE ) );

        view.setPlanningCapability( serverTemplate.getCapabilities().contains( Capability.PLANNING ) );

        if ( firstContainerSpec != null ) {
            addContainer( firstContainerSpec );
            for ( final ContainerSpec containerSpec : serverTemplate.getContainersSpec() ) {
                if ( !containerSpec.getId().equals( firstContainerSpec.getId() ) ) {
                    addContainer( containerSpec );
                }
            }
            containerSpecSelectedEvent.fire( new ContainerSpecSelected( firstContainerSpec ) );
        }

        for ( final ServerInstanceKey serverInstanceKey : serverTemplate.getServerIntanceKeys() ) {
            addServerInstance( serverInstanceKey );
        }
    }

    private void addContainer( final ContainerSpec containerSpec ) {
        view.addContainer( containerSpec.getServerTemplateKey().getId(),
                           containerSpec.getId(),
                           containerSpec.getContainerName(),
                           new Command() {
                               @Override
                               public void execute() {
                                   containerSpecSelectedEvent.fire( new ContainerSpecSelected( containerSpec ) );
                               }
                           } );
    }

    private void addServerInstance( final ServerInstanceKey serverInstanceKey ) {
        view.addServerInstance( serverInstanceKey.getServerTemplateId(),
                                serverInstanceKey.getServerInstanceId(),
                                serverInstanceKey.getServerName(),
                                new Command() {
                                    @Override
                                    public void execute() {
                                        serverInstanceSelectedEvent.fire( new ServerInstanceSelected( serverInstanceKey ) );
                                    }
                                } );
    }

    public void onContainerSelect( @Observes final ContainerSpecSelected containerSpecSelected ) {
        view.select( containerSpecSelected.getContainerSpecKey().getServerTemplateKey().getId(),
                     containerSpecSelected.getContainerSpecKey().getId() );
    }

    public void onServerInstanceSelect( @Observes final ServerInstanceSelected serverInstanceSelected ) {
        view.select( serverInstanceSelected.getServerInstanceKey().getServerTemplateId(),
                     serverInstanceSelected.getServerInstanceKey().getServerInstanceId() );
    }

    public void addNewContainer() {
        addNewContainerEvent.fire( new AddNewContainer( templateId ) );
    }

    public void copyTemplate() {
        copyPresenter.copy( new ParameterizedCommand<String>() {
            @Override
            public void execute( final String value ) {
                specManagementService.call( new RemoteCallback<Void>() {
                    @Override
                    public void callback( final Void aVoid ) {
                        copyPresenter.hide();
                    }
                }, new ErrorCallback<Object>() {
                    @Override
                    public boolean error( final Object o,
                                          final Throwable throwable ) {
                        copyPresenter.errorDuringProcessing( "Invalid name." );
                        return false;
                    }
                } ).copyServerTemplate( templateId, value, value );
            }
        } );
    }

    public void removeTemplate() {
        view.confirmRemove( new Command() {
            @Override
            public void execute() {
                specManagementService.call( new RemoteCallback<Void>() {
                    @Override
                    public void callback( final Void aVoid ) {
                        serverTemplateListRefreshEvent.fire( new ServerTemplateListRefresh() );
                    }
                }, new ErrorCallback<Object>() {
                    @Override
                    public boolean error( final Object o,
                                          final Throwable throwable ) {
                        notification.fire( new NotificationEvent( "Error trying to remove template." ) );
                        serverTemplateListRefreshEvent.fire( new ServerTemplateListRefresh() );
                        return false;
                    }
                } ).deleteServerTemplate( templateId );
            }
        } );
    }

}
