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
package org.kie.client;

import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.SuggestBox.DefaultSuggestionDisplay;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import org.jboss.errai.ioc.client.api.AfterInitialization;
import org.jboss.errai.ioc.client.api.EntryPoint;
import org.jboss.errai.ioc.client.container.IOCBeanDef;
import org.jboss.errai.ioc.client.container.IOCBeanManager;
import org.jbpm.console.ng.ht.client.resources.ShowcaseResources;
import org.uberfire.client.mvp.AbstractWorkbenchPerspectiveActivity;
import org.uberfire.client.mvp.ActivityManager;
import org.uberfire.client.mvp.Command;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.workbench.widgets.menu.MenuBar;
import org.uberfire.client.workbench.widgets.menu.MenuItemCommand;
import org.uberfire.client.workbench.widgets.menu.MenuItemSubMenu;
import org.uberfire.client.workbench.widgets.menu.impl.DefaultMenuItemCommand;
import org.uberfire.client.workbench.widgets.menu.impl.DefaultMenuItemSubMenu;
import org.uberfire.client.workbench.widgets.menu.impl.DefaultMenuBar;
import org.uberfire.client.workbench.widgets.menu.WorkbenchMenuBarPresenter;
import org.uberfire.shared.mvp.PlaceRequest;
import org.uberfire.shared.mvp.impl.DefaultPlaceRequest;

import com.google.gwt.animation.client.Animation;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.RootPanel;

/**
 *
 */
@EntryPoint
public class ShowcaseEntryPoint {

    @Inject
    private PlaceManager placeManager;
    @Inject
    private WorkbenchMenuBarPresenter menubar;
    @Inject
    private ActivityManager activityManager;
    @Inject
    private IOCBeanManager iocManager;
    private String[] menuItems = new String[]{
        "Tasks",
        "Process Runtime", 
        "Users and Groups",
        "Jobs" 
    };
    
    private SuggestBox actionText;
    private TextBox textSuggestBox ;
    DefaultSuggestionDisplay suggestionDisplay ;
    Map<String, String> actions = new HashMap<String, String>();
    
    @AfterInitialization
    public void startApp() {
        loadStyles();
        setupMenu();
        hideLoadingPopup();
        registerDoAction();
    }

    private void loadStyles() {
        //Ensure CSS has been loaded
        ShowcaseResources.INSTANCE.showcaseCss().ensureInjected();
        //RoundedCornersResource.INSTANCE.roundCornersCss().ensureInjected();
    }

    private void registerDoAction() {
        actions.put("Show me my pending Tasks", "Personal Tasks");
        actions.put("Show me my Inbox", "Inbox Perspective");
        actions.put("I want to start a new Process", "Process Runtime Perspective");
        actions.put("I want to design a new Process Model", "Process Designer Perspective");
        actions.put("I want to create a Task", "Quick New Task");
        actions.put("Show me all the pending tasks in my Group", "Group Tasks");
        actions.put("Logout", "Logout");
        
        KeyPressHandler keyPressHandler = new KeyPressHandler() {
            public void onKeyPress(KeyPressEvent event) {

                if (event.getUnicodeCharCode() == 160 && event.isAltKeyDown()) {
                    final DialogBox dialogBox = createDialogBox();
                    dialogBox.setGlassEnabled(true);
                    dialogBox.setAnimationEnabled(true);
                    dialogBox.center();
                    dialogBox.show();
                    actionText.setFocus(true);        
                }
                if(event.isControlKeyDown() && event.getUnicodeCharCode() == 116){
                    PlaceRequest placeRequestImpl = new DefaultPlaceRequest("Quick New Task");
                    placeManager.goTo(placeRequestImpl);
                }
                
                if(event.isControlKeyDown() && event.getUnicodeCharCode() == 104){
                    PlaceRequest placeRequestImpl = new DefaultPlaceRequest("Home Perspective");
                    placeManager.goTo(placeRequestImpl);
                }
                
                
            }
        };
        
        
        RootPanel.get().addDomHandler(keyPressHandler, KeyPressEvent.getType());
    }

    private void setupMenu() {
        //Places sub-menu
        final DefaultMenuBar placesMenuBar = new DefaultMenuBar();
        final DefaultMenuItemSubMenu placesMenu = new DefaultMenuItemSubMenu("Views",
                placesMenuBar);
        //Home
        final AbstractWorkbenchPerspectiveActivity defaultPerspective = getDefaultPerspectiveActivity();
        if (defaultPerspective != null) {
            menubar.addWorkbenchItem(new DefaultMenuItemCommand("Home",
                    new Command() {
                        @Override
                        public void execute() {
                            placeManager.goTo(new DefaultPlaceRequest(defaultPerspective.getIdentifier()));
                        }
                    }));
        }

       


        //Add places
        Arrays.sort(menuItems);
        for (final String menuItem : menuItems) {
            final MenuItemCommand item = new DefaultMenuItemCommand(menuItem,
                    new Command() {
                        @Override
                        public void execute() {
                            placeManager.goTo(new DefaultPlaceRequest(menuItem));
                        }
                    });
            placesMenuBar.addItem(item);
        }
        //Add Logout
        final MenuItemCommand logoutItem = new DefaultMenuItemCommand("Logout", new Command() {
            @Override
            public void execute() {
                redirect("uf_logout");
            }
        });
        menubar.addWorkbenchItem(placesMenu);
        
        //TODO:  Add Settings HERE
        
        menubar.addWorkbenchItem(logoutItem);
    }

    private AbstractWorkbenchPerspectiveActivity getDefaultPerspectiveActivity() {
        AbstractWorkbenchPerspectiveActivity defaultPerspective = null;
        final Collection<IOCBeanDef<AbstractWorkbenchPerspectiveActivity>> perspectives = iocManager.lookupBeans(AbstractWorkbenchPerspectiveActivity.class);
        final Iterator<IOCBeanDef<AbstractWorkbenchPerspectiveActivity>> perspectivesIterator = perspectives.iterator();
        outer_loop:
        while (perspectivesIterator.hasNext()) {
            final IOCBeanDef<AbstractWorkbenchPerspectiveActivity> perspective = perspectivesIterator.next();
            final AbstractWorkbenchPerspectiveActivity instance = perspective.getInstance();
            if (instance.isDefault()) {
                defaultPerspective = instance;
                break outer_loop;
            } else {
                iocManager.destroyBean(instance);
            }
        }
        return defaultPerspective;
    }

    private List<AbstractWorkbenchPerspectiveActivity> getPerspectiveActivities() {

        //Get Perspective Providers
        final Set<AbstractWorkbenchPerspectiveActivity> activities = activityManager.getActivities(AbstractWorkbenchPerspectiveActivity.class);

        //Sort Perspective Providers so they're always in the same sequence!
        List<AbstractWorkbenchPerspectiveActivity> sortedActivities = new ArrayList<AbstractWorkbenchPerspectiveActivity>(activities);
        Collections.sort(sortedActivities,
                new Comparator<AbstractWorkbenchPerspectiveActivity>() {
                    @Override
                    public int compare(AbstractWorkbenchPerspectiveActivity o1,
                            AbstractWorkbenchPerspectiveActivity o2) {
                        return o1.getPerspective().getName().compareTo(o2.getPerspective().getName());
                    }
                });

        return sortedActivities;
    }
    //Fade out the "Loading application" pop-up

    private void hideLoadingPopup() {
        final Element e = RootPanel.get("loading").getElement();

        new Animation() {
            @Override
            protected void onUpdate(double progress) {
                e.getStyle().setOpacity(1.0 - progress);
            }

            @Override
            protected void onComplete() {
                e.getStyle().setVisibility(Style.Visibility.HIDDEN);
            }
        }.run(500);
    }

    public static native void redirect(String url)/*-{
     $wnd.location = url;
     }-*/;

    private DialogBox createDialogBox() {
        // Create a dialog box and set the caption text
        final DialogBox dialogBox = new DialogBox();
        dialogBox.ensureDebugId("cwDialogBox");
        dialogBox.setText("Do Action");

        // Create a table to layout the content
        VerticalPanel dialogContents = new VerticalPanel();
        dialogContents.setSpacing(4);
        dialogBox.setWidget(dialogContents);

        // Add some text to the top of the dialog
        HTML details = new HTML("What do you want to do now?");
        dialogContents.add(details);
        dialogContents.setCellHorizontalAlignment(
                details, HasHorizontalAlignment.ALIGN_CENTER);

        MultiWordSuggestOracle oracle = new MultiWordSuggestOracle();
        String[] words = {"Show me my pending Tasks",
            "I want to start a new Process",
            "I want to design a new Process Model",
            "I want to design a new Form",
            "I want to create a Task",
            "Show me all the pending tasks in my Group",
            "Show me my Inbox", "Logout"
        };
        for (int i = 0; i < words.length; ++i) {
            oracle.add(words[i]);
        }
        // Create the suggest box
        textSuggestBox = new TextBox();
        suggestionDisplay = new DefaultSuggestionDisplay();
        actionText = new SuggestBox(oracle, textSuggestBox, suggestionDisplay);
        
         KeyPressHandler keyPressHandler = new KeyPressHandler() {
            public void onKeyPress(KeyPressEvent event) {
                if (event.getNativeEvent().getKeyCode() == 27) {
                    
                    dialogBox.hide();
                    suggestionDisplay.hideSuggestions();
                    
                }
                if (event.getNativeEvent().getKeyCode() == 13) {
                    
                    doAction(actionText.getText());
                    dialogBox.hide();
                }
            }
        };
        
        actionText.addHandler(keyPressHandler, KeyPressEvent.getType());
        
        dialogContents.add(actionText);
        

        // Return the dialog box
        return dialogBox;
    }
    
    public void doAction(String action) {
        String locatedAction = actions.get(action);
        if(locatedAction == null || locatedAction.equals("")){
            
            return;
        }
        PlaceRequest placeRequestImpl = new DefaultPlaceRequest(locatedAction);
//        placeRequestImpl.addParameter("taskId", Long.toString(task.getId()));

        placeManager.goTo(placeRequestImpl);
    }
}
