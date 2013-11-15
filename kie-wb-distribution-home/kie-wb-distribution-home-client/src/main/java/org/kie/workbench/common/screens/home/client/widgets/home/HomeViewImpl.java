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
package org.kie.workbench.common.screens.home.client.widgets.home;

import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.HeadingElement;
import com.google.gwt.dom.client.ParagraphElement;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.*;

import org.kie.workbench.common.screens.home.model.HomeModel;
import org.kie.workbench.common.screens.home.model.Section;

import org.uberfire.security.Identity;
import org.uberfire.security.impl.authz.RuntimeAuthorizationManager;

import java.util.Iterator;

public class HomeViewImpl extends Composite
        implements
        HomePresenter.HomeView{

    interface HomeViewImplBinder
            extends
            UiBinder<Widget, HomeViewImpl> {

    }

    private static HomeViewImplBinder uiBinder = GWT.create( HomeViewImplBinder.class );

    private HomePresenter presenter;


    @Inject
    private RuntimeAuthorizationManager authzManager;

    @Inject
    private Identity identity;

    @UiField
    HTMLPanel titleHtml;

 //   @UiField
 //   HeadingElement title;

 //   @UiField
 //   HTMLPanel subtitle;

    @UiField
    HTMLPanel homeHtml;


    public HomeViewImpl() {
        initWidget( uiBinder.createAndBindUi( this ) );
    }

    @Override
    public void init( final HomePresenter presenter ) {
        this.presenter = presenter;
    }

    @Override
    public void setModel( final HomeModel model ) {
        if ( model == null ) {
            return;
        }
        //Title
        //title.setInnerText( SafeHtmlUtils.htmlEscape( model.getTitle() ) );

        HTML htmlSubtitle = new HTML();
        htmlSubtitle.setHTML("<h1>"+model.getTitle()+"</h1><p>"+model.getSubtitle()+"</p>");
        htmlSubtitle.setStyleName("IntroHome");

        titleHtml.add(htmlSubtitle);
        String htmlLIs="";
        //Add Sections
        int i=1;
        for ( Section section : model.getSections() ) {
            if ( authzManager.authorize( section,
                                         identity ) ) {
                //final SectionWidget sectionWidget = makeSection(section.getHeading(),section.getDescription(),section.getImageUrl(),false);
                //this.columns.add( sectionWidget );
                htmlLIs+="<li>\n" +
                        "    \t<input type=\"radio\" id=\"s"+i+"\" name=\"num\" "+ (i==1? "checked=\"true\"":"") +" />\n" +
                        "\t\t<label for=\"s"+i+"\"><h2>"+section.getHeading()+"</h2><br><p>"+section.getDescription() +"</p></label>\n" +
                        "\t\t<a href=\"javascript:void(0);\">\n" +
                        "\t\t\t<img src=\""+section.getImageUrl()+"\" />\n" +
                        "\t\t</a>\n" +
                        "\t</li>\n";
                i++;
            }
        }
        for(int j=i;j<6;j++){
            htmlLIs+="<li>\n" +
                    "    <input type=\"radio\" \"=\"\" name=\"sn\">\n" +
                    "      <label for=\"sn\"></label>\n" +
                    "</li>\n";

        }
        //htmlContent
        HTML html = new HTML(htmlLIs);
        homeHtml.add(html);


    }


}
