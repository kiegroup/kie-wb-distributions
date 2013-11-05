/*
 * Copyright 2013 JBoss by Red Hat.
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

import org.guvnor.inbox.client.InboxPresenter;
import org.kie.workbench.client.resources.i18n.AppConstants;
import org.kie.workbench.common.widgets.client.handlers.NewResourcePresenter;
import org.kie.workbench.common.widgets.client.handlers.NewResourcesMenu;
import org.kie.workbench.common.widgets.client.menu.ProjectMenu;
import org.kie.workbench.common.widgets.client.menu.RepositoryMenu;
import org.uberfire.client.annotations.Perspective;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPerspective;
import org.uberfire.client.annotations.WorkbenchToolBar;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.workbench.model.PanelDefinition;
import org.uberfire.workbench.model.PanelType;
import org.uberfire.workbench.model.PerspectiveDefinition;
import org.uberfire.workbench.model.Position;
import org.uberfire.workbench.model.impl.PanelDefinitionImpl;
import org.uberfire.workbench.model.impl.PartDefinitionImpl;
import org.uberfire.workbench.model.impl.PerspectiveDefinitionImpl;
import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.Menus;
import org.uberfire.workbench.model.toolbar.IconType;
import org.uberfire.workbench.model.toolbar.ToolBar;
import org.uberfire.workbench.model.toolbar.impl.DefaultToolBar;
import org.uberfire.workbench.model.toolbar.impl.DefaultToolBarItem;

@ApplicationScoped
@WorkbenchPerspective(identifier = "org.kie.workbench.client.perspectives.DroolsAuthoringPerspective")
public class DroolsAuthoringPerspective {

    private AppConstants constants = AppConstants.INSTANCE;

    @Inject
    private NewResourcePresenter newResourcePresenter;

    @Inject
    private NewResourcesMenu newResourcesMenu;

    @Inject
    private ProjectMenu projectMenu;

    @Inject
    private PlaceManager placeManager;

    @Inject
    private RepositoryMenu repositoryMenu;

    private PerspectiveDefinition perspective;
    private Menus menus;
    private ToolBar toolBar;

    @PostConstruct
    public void init() {
        buildPerspective();
        buildMenuBar();
        buildToolBar();
    }

    @Perspective
    public PerspectiveDefinition getPerspective() {
        return this.perspective;
    }

    @WorkbenchMenu
    public Menus getMenus() {
        return this.menus;
    }

    @WorkbenchToolBar
    public ToolBar getToolBar() {
        return this.toolBar;
    }

    private void buildPerspective() {
        this.perspective = new PerspectiveDefinitionImpl( PanelType.ROOT_LIST );
        this.perspective.setName( constants.Project_Authoring() );

        final PanelDefinition west = new PanelDefinitionImpl( PanelType.SIMPLE );
        west.setWidth( 400 );
        west.setMinWidth( 370 );
        west.addPart( new PartDefinitionImpl( new DefaultPlaceRequest( "org.kie.guvnor.explorer" ) ) );

        this.perspective.getRoot().insertChild( Position.WEST,
                                                west );
    }

    private void buildMenuBar() {
        this.menus = MenuFactory
                .newTopLevelMenu(constants.explore())
                .menus()
                .menu( constants.inboxIncomingChanges() )
                .respondsWith( new Command() {
                    @Override
                    public void execute() {
                        //PlaceRequest p = new PathPlaceRequest("Inbox");
                        //p.addParameter("inboxname", InboxPresenter.INCOMING_ID);
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
                .menu(constants.inboxRecentlyOpened())
                .respondsWith(new Command() {
                    @Override
                    public void execute() {
                        PlaceRequest p = new DefaultPlaceRequest("Inbox");
                        p.addParameter("inboxname", InboxPresenter.RECENT_VIEWED_ID);
                        placeManager.goTo(p);
                    }
                })
                .endMenu()
                .endMenus()
                .endMenu()
                .newTopLevelMenu( constants.newItem() )
                .withItems( newResourcesMenu.getMenuItems() )
                .endMenu()
                .newTopLevelMenu( constants.tools() )
                .withItems( projectMenu.getMenuItems() )
                .endMenu()
                .newTopLevelMenu(AppConstants.INSTANCE.Repository())
                .withItems(repositoryMenu.getMenuItems())
                .endMenu().build();
    }

    private void buildToolBar() {
        this.toolBar = new DefaultToolBar( "guvnor.new.item" );
        final String tooltip = AppConstants.INSTANCE.newItem();
        final Command command = new Command() {
            @Override
            public void execute() {
                newResourcePresenter.show();
            }
        };
        toolBar.addItem( new DefaultToolBarItem( IconType.FILE,
                                                 tooltip,
                                                 command ) );

    }

}
