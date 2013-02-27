/*
 * Copyright 2012 JBoss Inc
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
package org.kie.home.client;

import com.github.gwtbootstrap.client.ui.base.IconAnchor;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.user.client.Window;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.uberfire.client.workbench.widgets.events.NotificationEvent;



import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.SuggestBox;
import java.util.ArrayList;
import java.util.List;

import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.uberfire.security.Identity;
import org.uberfire.security.Role;

import org.kie.home.client.i18n.Constants;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.shared.mvp.PlaceRequest;
import org.uberfire.shared.mvp.impl.DefaultPlaceRequest;

@Dependent
@Templated(value = "HomeViewImpl.html")
public class HomeViewImpl extends Composite
        implements
        HomePresenter.HomeView {

  private HomePresenter presenter;
  @Inject
  private PlaceManager placeManager;
  @Inject
  public Identity identity;
  

  @DataField
  public Image carouselImg0;
  @DataField
  public Image carouselImg1;
  @DataField
  public Image carouselImg2;
  @DataField
  public Image carouselImg3;
  @DataField
  public Image carouselImg4;
  @DataField
  public Image carouselImg5;
  
  
  @Inject
  @DataField
  public IconAnchor discoverLabel;
  @Inject
  @DataField
  public IconAnchor designLabel;
  @Inject
  @DataField
  public IconAnchor deployLabel;
  @Inject
  @DataField
  public IconAnchor workLabel;
  @Inject
  @DataField
  public IconAnchor monitorLabel;
  @Inject
  @DataField
  public IconAnchor improveLabel;
  @Inject
  @DataField
  public IconAnchor modelProcessAnchor;
  
  @Inject
  @DataField
  public IconAnchor workTaskListAnchor;
  
  @Inject
  @DataField
  public IconAnchor workProcessRuntimeAnchor;
//  @Inject
//  @DataField
//  public IconAnchor deployIdentityAnchor;
  @Inject
  @DataField
  public IconAnchor monitorBAMAnchor;
  
//  @Inject
//  @DataField
//  public IconAnchor deployJobsAnchor;
  
  @Inject
  private Event<NotificationEvent> notification;
  private Constants constants = GWT.create(Constants.class);

  public HomeViewImpl() {

    
    carouselImg5 = new Image();
    carouselImg4 = new Image();
    carouselImg3 = new Image();
    carouselImg2 = new Image();
    carouselImg1 = new Image();
    carouselImg0 = new Image();
    
  }

  @Override
  public void init(final HomePresenter presenter) {
    this.presenter = presenter;
    String url = GWT.getHostPageBaseURL();

    
    
    carouselImg5.setUrl(url + "images/mountain.jpg");
    carouselImg4.setUrl(url + "images/mountain.jpg");
    carouselImg3.setUrl(url + "images/mountain.jpg");
    carouselImg2.setUrl(url + "images/mountain.jpg");
    carouselImg1.setUrl(url + "images/mountain.jpg");
    carouselImg0.setUrl(url + "images/mountain.jpg");
    

    
   

    modelProcessAnchor.addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        PlaceRequest placeRequestImpl = new DefaultPlaceRequest("Authoring");
        placeManager.goTo(placeRequestImpl);
      }
    });

    workTaskListAnchor.addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        PlaceRequest placeRequestImpl = new DefaultPlaceRequest("Tasks List");
        placeManager.goTo(placeRequestImpl);
      }
    });
    
    workProcessRuntimeAnchor.addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        PlaceRequest placeRequestImpl = new DefaultPlaceRequest("Process Definitions");
        placeManager.goTo(placeRequestImpl);
      }
    });
    
//    deployIdentityAnchor.addClickHandler(new ClickHandler() {
//      @Override
//      public void onClick(ClickEvent event) {
//        PlaceRequest placeRequestImpl = new DefaultPlaceRequest("Users and Groups");
//        placeManager.goTo(placeRequestImpl);
//      }
//    });
//    
//    deployJobsAnchor.addClickHandler(new ClickHandler() {
//      @Override
//      public void onClick(ClickEvent event) {
//        PlaceRequest placeRequestImpl = new DefaultPlaceRequest("Requests List");
//        placeManager.goTo(placeRequestImpl);
//      }
//    });
    
    monitorBAMAnchor.addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        Window.open("http://localhost:8080/bam-app/", "_blank", "");
      }
    });

  }

  public void displayNotification(String text) {
    notification.fire(new NotificationEvent(text));
  }
}
