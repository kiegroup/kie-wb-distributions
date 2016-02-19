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

import java.util.HashMap;
import java.util.Map;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Composite;
import org.gwtbootstrap3.client.ui.Anchor;
import org.gwtbootstrap3.client.ui.CheckBox;
import org.gwtbootstrap3.client.ui.LinkedGroup;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.client.server.management.widget.CustomGroupItem;
import org.uberfire.ext.widgets.common.client.common.popups.YesNoCancelPopup;
import org.uberfire.mvp.Command;

import static org.uberfire.commons.validation.PortablePreconditions.*;

@Dependent
@Templated
public class ServerTemplateView extends Composite
        implements ServerTemplatePresenter.View {

    private ServerTemplatePresenter presenter;

    @DataField("current-server-template-name")
    Element serverTemplate = DOM.createElement( "strong" );

    @Inject
    @DataField("rule-capability-checkbox")
    CheckBox ruleEnabled;

    @Inject
    @DataField("process-capability-checkbox")
    CheckBox processEnabled;

    @Inject
    @DataField("planning-capability-checkbox")
    CheckBox planningEnabled;

    @Inject
    @DataField("add-new-container")
    Anchor addNewContainer;

    @Inject
    @DataField("copy-current-server-template")
    Anchor copyTemplate;

    @Inject
    @DataField("remove-current-server-template")
    Anchor removeTemplate;

    @Inject
    @DataField("container-list-group")
    LinkedGroup containersListGroup;

    @Inject
    @DataField("remote-server-list-group")
    LinkedGroup remoteServersListGroup;

    private Map<String, CustomGroupItem> groupItems = new HashMap<String, CustomGroupItem>();

    private CustomGroupItem selected = null;
    private String templateId;

    @Override
    public void init( final ServerTemplatePresenter presenter ) {
        this.presenter = presenter;
        ruleEnabled.setText( "Rule" );
        ruleEnabled.setEnabled( false );
        processEnabled.setText( "Process" );
        processEnabled.setEnabled( false );
        planningEnabled.setText( "Planning" );
        planningEnabled.setEnabled( false );
    }

    @Override
    public void clear() {
        remoteServersListGroup.clear();
        containersListGroup.clear();
        groupItems.clear();
        selected = null;
        templateId = null;
        serverTemplate.setInnerText( "" );
        processEnabled.setValue( false );
        ruleEnabled.setValue( false );
        planningEnabled.setValue( false );
    }

    @Override
    public void setTemplate( final String templateId,
                             final String templateName ) {
        this.templateId = templateId;
        serverTemplate.setInnerText( templateName );
        groupItems.clear();
        containersListGroup.clear();
        remoteServersListGroup.clear();
    }

    @Override
    public void select( final String serverTemplateId,
                        final String id ) {
        checkNotEmpty( "serverTemplateId", serverTemplateId );
        checkNotEmpty( "id", id );

        if ( selected != null ) {
            selected.setActive( false );
            selected.removeStyleName( "active" );
        }

        if ( !serverTemplateId.equals( this.templateId ) ) {
            return;
        }

        selected = groupItems.get( id );
        selected.setActive( true );
    }

    @Override
    public void addContainer( final String serverTemplateId,
                              final String containerSpecId,
                              final String containerName,
                              final Command onSelect ) {
        if ( !serverTemplateId.equals( this.templateId ) ) {
            return;
        }

        final CustomGroupItem groupItem = new CustomGroupItem( containerName,
                                                               IconType.FOLDER_O,
                                                               onSelect );

        groupItems.put( containerSpecId, groupItem );

        containersListGroup.add( groupItem );
    }

    @Override
    public void addServerInstance( final String serverTemplateId,
                                   final String serverInstanceId,
                                   final String serverName,
                                   final Command onSelect ) {
        if ( !serverTemplateId.equals( this.templateId ) ) {
            return;
        }

        final CustomGroupItem groupItem = new CustomGroupItem( serverName,
                                                               IconType.SERVER,
                                                               onSelect );

        groupItems.put( serverInstanceId, groupItem );

        remoteServersListGroup.add( groupItem );
    }

    @Override
    public void setRulesCapability( final boolean value ) {
        ruleEnabled.setValue( value );
    }

    @Override
    public void setProcessCapability( final boolean value ) {
        processEnabled.setValue( value );
    }

    @Override
    public void setPlanningCapability( final boolean value ) {
        planningEnabled.setValue( value );
    }

    @Override
    public void confirmRemove( final Command command ) {
        YesNoCancelPopup.newYesNoCancelPopup( "Template Remove",
                                              "Are you sure you want remove current template?",
                                              command,
                                              new Command() {
                                                  @Override
                                                  public void execute() {
                                                  }
                                              }, null ).show();
    }

    @EventHandler("add-new-container")
    public void addNewContainer( final ClickEvent event ) {
        presenter.addNewContainer();
    }

    @EventHandler("copy-current-server-template")
    public void copyTemplate( final ClickEvent event ) {
        presenter.copyTemplate();
    }

    @EventHandler("remove-current-server-template")
    public void removeTemplate( final ClickEvent event ) {
        presenter.removeTemplate();
    }

}
