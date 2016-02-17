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
import javax.inject.Inject;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Composite;
import org.gwtbootstrap3.client.ui.AnchorListItem;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.DropDownMenu;
import org.gwtbootstrap3.client.ui.TextBox;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;

@Dependent
@Templated
public class ContainerProcessConfigView extends Composite
        implements ContainerProcessConfigPresenter.View {

    private ContainerProcessConfigPresenter presenter;

    @DataField("container-config-runtime-strategy-label")
    Element runtimeStrategyLabel = DOM.createLabel();

    @Inject
    @DataField("container-config-runtime-strategy-button")
    Button runtimeStrategy;

    @Inject
    @DataField("container-config-runtime-strategy-dropdown-menu")
    DropDownMenu runtimeStrategyDropdown;

    @DataField("container-config-kbase-name-form")
    Element kbaseForm = DOM.createDiv();

    @DataField("container-config-kbase-name-label")
    Element kbaseLabel = DOM.createLabel();

    @Inject
    @DataField("container-config-kbase-name-textbox")
    TextBox kbase;

    @DataField("container-config-ksession-name-form")
    Element ksessionForm = DOM.createDiv();

    @DataField("container-config-ksession-name-label")
    Element ksessionLabel = DOM.createLabel();

    @Inject
    @DataField("container-config-ksession-name-textbox")
    TextBox ksession;

    @DataField("container-config-merge-mode-label")
    Element mergeModeLabel = DOM.createLabel();

    @Inject
    @DataField("container-config-merge-mode-button")
    Button mergeMode;

    @Inject
    @DataField("container-config-merge-mode-dropdown-menu")
    DropDownMenu mergeModeDropdown;

    @Inject
    @DataField("container-config-save-button")
    Button save;

    @Inject
    @DataField("container-config-cancel-button")
    Button cancel;

    @PostConstruct
    public void init() {
        final String[] runtimeStrategies = { "Singleton", "Per Request", "Per Process Instance" };

        runtimeStrategy.setText( runtimeStrategies[ 0 ] );
        for ( final String strategy : runtimeStrategies ) {
            runtimeStrategyDropdown.add( new AnchorListItem( strategy ) {{
                addClickHandler( new ClickHandler() {
                    @Override
                    public void onClick( final ClickEvent event ) {
                        runtimeStrategy.setText( strategy );
                    }
                } );
            }} );
        }

        final String[] mergeModes = { "Merge Collections", "Keep All", "Override All", "Override Empty" };

        mergeMode.setText( mergeModes[ 0 ] );
        for ( final String merge : mergeModes ) {
            mergeModeDropdown.add( new AnchorListItem() {{
                setText( merge );
                addClickHandler( new ClickHandler() {
                    @Override
                    public void onClick( final ClickEvent event ) {
                        mergeMode.setText( merge );
                    }
                } );
            }} );
        }
    }

    @Override
    public void init( final ContainerProcessConfigPresenter presenter ) {
        this.presenter = presenter;
    }

    @Override
    public void setContent( final String runtimeStrategy,
                            final String kbase,
                            final String ksession,
                            final String mergeMode ) {
        this.runtimeStrategy.setText( runtimeStrategy );
        this.kbase.setText( kbase );
        this.ksession.setText( ksession );
        this.mergeMode.setText( mergeMode );

        this.runtimeStrategy.setEnabled( true );
        this.kbase.setEnabled( true );
        this.ksession.setEnabled( true );
        this.mergeMode.setEnabled( true );
        enableActions();
    }

    @Override
    public String getRuntimeStrategy() {
        return runtimeStrategy.getText();
    }

    @Override
    public void disable() {
        runtimeStrategy.setEnabled( false );
        kbase.setEnabled( false );
        ksession.setEnabled( false );
        mergeMode.setEnabled( false );
        disableActions();
    }

    @Override
    public void disableActions() {
        save.setEnabled( false );
        cancel.setEnabled( false );
    }

    @Override
    public void enableActions() {
        this.save.setEnabled( true );
        this.cancel.setEnabled( true );
    }

    @Override
    public String getKBase() {
        return kbase.getText();
    }

    @Override
    public String getKSession() {
        return ksession.getText();
    }

    @Override
    public String getMergeMode() {
        return mergeMode.getText();
    }

    @EventHandler("container-config-save-button")
    public void onSave( final ClickEvent event ) {
        presenter.save();
    }

    @EventHandler("container-config-cancel-button")
    public void onCancel( final ClickEvent event ) {
        presenter.cancel();
    }

}
