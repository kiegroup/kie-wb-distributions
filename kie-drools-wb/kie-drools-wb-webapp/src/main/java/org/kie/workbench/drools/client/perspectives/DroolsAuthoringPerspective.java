/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.kie.workbench.drools.client.perspectives;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.guvnor.inbox.client.InboxPresenter;
import org.kie.workbench.common.widgets.client.handlers.NewResourcePresenter;
import org.kie.workbench.common.widgets.client.handlers.NewResourcesMenu;
import org.kie.workbench.common.widgets.client.menu.RepositoryMenu;
import org.kie.workbench.drools.client.docks.AuthoringWorkbenchDocks;
import org.kie.workbench.drools.client.resources.i18n.AppConstants;
import org.uberfire.client.annotations.Perspective;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPerspective;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.workbench.panels.impl.MultiListWorkbenchPanelPresenter;
import org.uberfire.client.workbench.panels.impl.SimpleWorkbenchPanelPresenter;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.workbench.model.CompassPosition;
import org.uberfire.workbench.model.PanelDefinition;
import org.uberfire.workbench.model.PerspectiveDefinition;
import org.uberfire.workbench.model.impl.PanelDefinitionImpl;
import org.uberfire.workbench.model.impl.PartDefinitionImpl;
import org.uberfire.workbench.model.impl.PerspectiveDefinitionImpl;
import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.Menus;

/**
 * A Perspective for Rule authors
 */
@ApplicationScoped
@WorkbenchPerspective(identifier = "AuthoringPerspective", isTransient = false)
public class DroolsAuthoringPerspective {

    private AppConstants constants = AppConstants.INSTANCE;

    @Inject
    private NewResourcePresenter newResourcePresenter;

    @Inject
    private NewResourcesMenu newResourcesMenu;

    @Inject
    private PlaceManager placeManager;

    @Inject
    private RepositoryMenu repositoryMenu;

    @Inject
    private AuthoringWorkbenchDocks docks;

    @PostConstruct
    public void setup() {
        docks.setup("AuthoringPerspective", new DefaultPlaceRequest( "org.kie.guvnor.explorer" ) );
    }

    @Perspective
    public PerspectiveDefinition getPerspective() {
        final PerspectiveDefinition perspective = new PerspectiveDefinitionImpl( MultiListWorkbenchPanelPresenter.class.getName() );
        perspective.setName( constants.project_authoring() );

        return perspective;
    }

    @WorkbenchMenu
    public Menus getMenus() {
        return MenuFactory
                .newTopLevelMenu( constants.explore() )
                .menus()
                .menu( constants.inboxIncomingChanges() )
                .respondsWith( new Command() {
                    @Override
                    public void execute() {
                        placeManager.goTo( "Inbox" );
                    }
                } )
                .endMenu()
                .menu( constants.inboxRecentlyEdited() )
                .respondsWith( new Command() {
                    @Override
                    public void execute() {
                        PlaceRequest p = new DefaultPlaceRequest( "Inbox" );
                        p.addParameter( "inboxname", InboxPresenter.RECENT_EDITED_ID );
                        placeManager.goTo( p );
                    }
                } )
                .endMenu()
                .menu( constants.inboxRecentlyOpened() )
                .respondsWith( new Command() {
                    @Override
                    public void execute() {
                        PlaceRequest p = new DefaultPlaceRequest( "Inbox" );
                        p.addParameter( "inboxname", InboxPresenter.RECENT_VIEWED_ID );
                        placeManager.goTo( p );
                    }
                } )
                .endMenu()
                .endMenus()
                .endMenu()
                .newTopLevelMenu( constants.newItem() )
                .withItems( newResourcesMenu.getMenuItems() )
                .endMenu()
                .newTopLevelMenu( constants.Repository() )
                .withItems( repositoryMenu.getMenuItems() )
                .endMenu().build();
    }

}
