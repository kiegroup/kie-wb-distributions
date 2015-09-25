/*
 * Copyright 2012 JBoss Inc
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
package org.kie.workbench.drools.client;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;

import com.google.gwt.animation.client.Animation;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootPanel;
import org.guvnor.common.services.shared.config.AppConfigService;
import org.guvnor.common.services.shared.security.KieWorkbenchACL;
import org.guvnor.common.services.shared.security.KieWorkbenchPolicy;
import org.guvnor.common.services.shared.security.KieWorkbenchSecurityService;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.AfterInitialization;
import org.jboss.errai.ioc.client.api.EntryPoint;
import org.jboss.errai.ioc.client.container.IOCBeanDef;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.jboss.errai.security.shared.api.Role;
import org.jboss.errai.security.shared.api.identity.User;
import org.jboss.errai.security.shared.service.AuthenticationService;
import org.jbpm.console.ng.ga.forms.service.PlaceManagerActivityService;
import org.kie.workbench.common.screens.social.hp.config.SocialConfigurationService;
import org.kie.workbench.common.services.shared.preferences.ApplicationPreferences;
import org.kie.workbench.common.widgets.client.menu.AboutMenuBuilder;
import org.kie.workbench.common.widgets.client.menu.ResetPerspectivesMenuBuilder;
import org.kie.workbench.common.widgets.client.menu.WorkbenchConfigurationMenuBuilder;
import org.kie.workbench.common.widgets.client.resources.RoundedCornersResource;
import org.kie.workbench.drools.client.home.HomeProducer;
import org.kie.workbench.drools.client.resources.i18n.AppConstants;
import org.uberfire.client.menu.CustomSplashHelp;
import org.uberfire.client.mvp.AbstractWorkbenchPerspectiveActivity;
import org.uberfire.client.mvp.ActivityBeansCache;
import org.uberfire.client.mvp.ActivityManager;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.workbench.docks.UberfireDock;
import org.uberfire.client.workbench.docks.UberfireDockPosition;
import org.uberfire.client.workbench.docks.UberfireDocks;
import org.uberfire.client.workbench.widgets.menu.WorkbenchMenuBarPresenter;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.MenuItem;
import org.uberfire.workbench.model.menu.MenuPosition;
import org.uberfire.workbench.model.menu.Menus;

import static org.kie.workbench.drools.client.security.KieWorkbenchFeatures.*;

/**
 * GWT's Entry-point for kie-drools-wb
 */
@EntryPoint
public class KieDroolsWorkbenchEntryPoint {

    private AppConstants constants = AppConstants.INSTANCE;

    @Inject
    private Caller<AppConfigService> appConfigService;

    @Inject
    private WorkbenchMenuBarPresenter menubar;

    @Inject
    private PlaceManager placeManager;

    @Inject
    private SyncBeanManager iocManager;

    @Inject
    private ActivityManager activityManager;

    @Inject
    private User identity;

    @Inject
    private KieWorkbenchACL kieACL;

    @Inject
    private HomeProducer homeProducer;

    @Inject
    private Caller<KieWorkbenchSecurityService> kieSecurityService;

    @Inject
    private Caller<PlaceManagerActivityService> pmas;

    @Inject
    private ActivityBeansCache activityBeansCache;

    @Inject
    private Caller<AuthenticationService> authService;

    @Inject
    private Caller<SocialConfigurationService> socialConfigurationService;

    @AfterInitialization
    public void startApp() {
        kieSecurityService.call( new RemoteCallback<String>() {
            public void callback( final String str ) {
                KieWorkbenchPolicy policy = new KieWorkbenchPolicy( str );
                kieACL.activatePolicy( policy );
                loadPreferences();
                loadStyles();
                setupMenu();
                hideLoadingPopup();
                homeProducer.init();
            }
        } ).loadPolicy();
        List<String> allActivities = activityBeansCache.getActivitiesById();
        pmas.call( new RemoteCallback<Void>() {

            @Override
            public void callback( Void response ) {

            }
        } ).initActivities( allActivities );
    }

    private void loadPreferences() {
        appConfigService.call( new RemoteCallback<Map<String, String>>() {
            @Override
            public void callback( final Map<String, String> response ) {
                ApplicationPreferences.setUp( response );
            }
        } ).loadPreferences();
    }

    private void loadStyles() {
        //Ensure CSS has been loaded
        //ShowcaseResources.INSTANCE.showcaseCss().ensureInjected();
        RoundedCornersResource.INSTANCE.roundCornersCss().ensureInjected();
    }

    private void setupMenu() {
        socialConfigurationService.call( new RemoteCallback<Boolean>() {
            public void callback( final Boolean socialEnabled ) {

                final AbstractWorkbenchPerspectiveActivity defaultPerspective = getDefaultPerspectiveActivity();

                final Menus menus =
                        MenuFactory.newTopLevelMenu( constants.home() ).withItems( getHomeViews( socialEnabled ) ).endMenu()
                                .newTopLevelMenu( constants.authoring() ).withRoles( kieACL.getGrantedRoles( G_AUTHORING ) ).withItems( getAuthoringViews() ).endMenu()
                                .newTopLevelMenu( constants.deploy() ).withRoles( kieACL.getGrantedRoles( G_AUTHORING ) ).withItems( getDeploymentViews() ).endMenu()
                                .newTopLevelMenu( constants.tasks() ).withItems( getTasksViews() ).endMenu()
                                .newTopLevelMenu( constants.extensions() ).withRoles( kieACL.getGrantedRoles( F_EXTENSIONS ) ).withItems( getExtensionsViews() ).endMenu()
                                .newTopLevelMenu( constants.find() ).withRoles( kieACL.getGrantedRoles( F_SEARCH ) ).position( MenuPosition.RIGHT ).respondsWith( new Command() {

                            @Override
                            public void execute() {
                                placeManager.goTo( "FindForm" );
                            }
                        } )
                                .endMenu()
                                .newTopLevelMenu( constants.User() + ": " + identity.getIdentifier() )
                                .position( MenuPosition.RIGHT )
                                .withItems( getRoles() )
                                .endMenu()
                                .newTopLevelCustomMenu( iocManager.lookupBean( WorkbenchConfigurationMenuBuilder.class ).getInstance() )
                                .endMenu()
                                .newTopLevelCustomMenu( iocManager.lookupBean( CustomSplashHelp.class ).getInstance() )
                                .endMenu()
                                .newTopLevelCustomMenu( iocManager.lookupBean( AboutMenuBuilder.class ).getInstance() )
                                .endMenu()
                                .newTopLevelCustomMenu( iocManager.lookupBean( ResetPerspectivesMenuBuilder.class ).getInstance() )
                                .endMenu()
                                .build();

                menubar.addMenus( menus );
            }
        } ).isSocialEnable();
    }

    private List<? extends MenuItem> getRoles() {
        final List<MenuItem> result = new ArrayList<MenuItem>( identity.getRoles().size() );
        for ( final Role role : identity.getRoles() ) {
            if ( !role.getName().equals( "IS_REMEMBER_ME" ) ) {
                result.add( MenuFactory.newSimpleItem( constants.Role() + ": " + role.getName() ).endMenu().build().getItems().get( 0 ) );
            }
        }
        result.add( MenuFactory.newSimpleItem( constants.LogOut() ).respondsWith( new LogoutCommand() ).endMenu().build().getItems().get( 0 ) );

        return result;
    }

    private List<? extends MenuItem> getHomeViews( Boolean socialEnabled ) {
        final AbstractWorkbenchPerspectiveActivity defaultPerspective = getDefaultPerspectiveActivity();
        final List<MenuItem> result = new ArrayList<MenuItem>( 1 );

        result.add( MenuFactory.newSimpleItem( constants.homePage() ).respondsWith( new Command() {
            @Override
            public void execute() {
                if ( defaultPerspective != null ) {
                    placeManager.goTo( new DefaultPlaceRequest( defaultPerspective.getIdentifier() ) );
                } else {
                    Window.alert( constants.missingDefaultPerspective() );
                }
            }
        } ).endMenu().build().getItems().get( 0 ) );
        if ( socialEnabled ) {
            result.add( MenuFactory.newSimpleItem( constants.timeline() ).respondsWith( new Command() {
                @Override
                public void execute() {
                    placeManager.goTo( new DefaultPlaceRequest( "SocialHomePagePerspective" ) );
                }
            } ).endMenu().build().getItems().get( 0 ) );

            result.add( MenuFactory.newSimpleItem( constants.people() ).respondsWith( new Command() {
                @Override
                public void execute() {
                    placeManager.goTo( new DefaultPlaceRequest( "UserHomePagePerspective" ) );
                }
            } ).endMenu().build().getItems().get( 0 ) );
        }
        return result;
    }

    private List<MenuItem> getAuthoringViews() {
        final List<MenuItem> result = new ArrayList<MenuItem>( 4 );

        result.add( MenuFactory.newSimpleItem( constants.project_authoring() ).withRoles( kieACL.getGrantedRoles( F_PROJECT_AUTHORING ) ).respondsWith( new Command() {
            @Override
            public void execute() {
                placeManager.goTo( new DefaultPlaceRequest( "AuthoringPerspective" ) );
            }
        } ).endMenu().build().getItems().get( 0 ) );

        result.add( MenuFactory.newSimpleItem( constants.contributors() ).withRoles( kieACL.getGrantedRoles( F_CONTRIBUTORS ) ).respondsWith( new Command() {
            @Override
            public void execute() {
                placeManager.goTo( new DefaultPlaceRequest( "ContributorsPerspective" ) );
            }
        } ).endMenu().build().getItems().get( 0 ) );

        result.add( MenuFactory.newSimpleItem( constants.artifactRepository() ).withRoles( kieACL.getGrantedRoles( F_ARTIFACT_REPO ) ).respondsWith( new Command() {
            @Override
            public void execute() {
                placeManager.goTo( new DefaultPlaceRequest( "org.guvnor.m2repo.client.perspectives.GuvnorM2RepoPerspective" ) );
            }
        } ).endMenu().build().getItems().get( 0 ) );

        result.add( MenuFactory.newSimpleItem( constants.administration() ).withRoles( kieACL.getGrantedRoles( F_ADMINISTRATION ) ).respondsWith( new Command() {
            @Override
            public void execute() {
                placeManager.goTo( new DefaultPlaceRequest( "AdministrationPerspective" ) );
            }
        } ).endMenu().build().getItems().get( 0 ) );

        return result;
    }

    private List<MenuItem> getDeploymentViews() {
        final List<MenuItem> result = new ArrayList<MenuItem>( 1 );

        result.add( MenuFactory.newSimpleItem( constants.ruleDeployments() ).withRoles( kieACL.getGrantedRoles( F_MANAGEMENT ) ).respondsWith( new Command() {
            @Override
            public void execute() {
                placeManager.goTo( new DefaultPlaceRequest( "ServerManagementPerspective" ) );
            }
        } ).endMenu().build().getItems().get( 0 ) );

        return result;
    }

    private List<? extends MenuItem> getTasksViews() {
        final List<MenuItem> result = new ArrayList<MenuItem>( 2 );

        result.add( MenuFactory.newSimpleItem( constants.Tasks_List() ).respondsWith( new Command() {
            @Override
            public void execute() {
                placeManager.goTo( new DefaultPlaceRequest( "DataSet Tasks" ) );
            }
        } ).endMenu().build().getItems().get( 0 ) );

        return result;
    }

    private List<? extends MenuItem> getExtensionsViews() {
        final List<MenuItem> result = new ArrayList<MenuItem>( 2 );
        result.add( MenuFactory.newSimpleItem( constants.plugins() ).withRoles( kieACL.getGrantedRoles( F_PLUGIN_MANAGEMENT ) ).respondsWith( new Command() {
            @Override
            public void execute() {
                placeManager.goTo( new DefaultPlaceRequest( "PlugInAuthoringPerspective" ) );
            }
        } ).endMenu().build().getItems().get( 0 ) );
        result.add( MenuFactory.newSimpleItem( constants.Apps() ).withRoles( kieACL.getGrantedRoles( F_APPS ) ).respondsWith( new Command() {
            @Override
            public void execute() {
                placeManager.goTo( new DefaultPlaceRequest( "AppsPerspective" ) );
            }

        } ).endMenu().build().getItems().get( 0 ) );
        result.add( MenuFactory.newSimpleItem( constants.DataSets() ).withRoles( kieACL.getGrantedRoles( F_DATASETS ) ).respondsWith( new Command() {
            @Override
            public void execute() {
                placeManager.goTo( new DefaultPlaceRequest( "DataSetAuthoringPerspective" ) );
            }

        } ).endMenu().build().getItems().get( 0 ) );
        return result;
    }

    private AbstractWorkbenchPerspectiveActivity getDefaultPerspectiveActivity() {
        AbstractWorkbenchPerspectiveActivity defaultPerspective = null;
        final Collection<IOCBeanDef<AbstractWorkbenchPerspectiveActivity>> perspectives = iocManager.lookupBeans( AbstractWorkbenchPerspectiveActivity.class );
        final Iterator<IOCBeanDef<AbstractWorkbenchPerspectiveActivity>> perspectivesIterator = perspectives.iterator();
        outer_loop:
        while ( perspectivesIterator.hasNext() ) {
            final IOCBeanDef<AbstractWorkbenchPerspectiveActivity> perspective = perspectivesIterator.next();
            final AbstractWorkbenchPerspectiveActivity instance = perspective.getInstance();
            if ( instance.isDefault() ) {
                defaultPerspective = instance;
                break outer_loop;
            } else {
                iocManager.destroyBean( instance );
            }
        }
        return defaultPerspective;
    }

    //Fade out the "Loading application" pop-up
    private void hideLoadingPopup() {
        final Element e = RootPanel.get( "loading" ).getElement();

        new Animation() {

            @Override
            protected void onUpdate( double progress ) {
                e.getStyle().setOpacity( 1.0 - progress );
            }

            @Override
            protected void onComplete() {
                e.getStyle().setVisibility( Style.Visibility.HIDDEN );
            }
        }.run( 500 );
    }

    private class LogoutCommand implements Command {

        @Override
        public void execute() {
            authService.call( new RemoteCallback<Void>() {
                @Override
                public void callback( Void response ) {
                    final String location = GWT.getModuleBaseURL().replaceFirst( "/" + GWT.getModuleName() + "/", "/logout.jsp" );
                    redirect( location );
                }
            } ).logout();
        }
    }

    public static native void redirect( String url )/*-{
        $wnd.location = url;
    }-*/;

}