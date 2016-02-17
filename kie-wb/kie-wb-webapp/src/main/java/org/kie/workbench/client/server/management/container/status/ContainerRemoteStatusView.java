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

package org.kie.workbench.client.server.management.container.status;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Composite;
import org.gwtbootstrap3.client.ui.html.Div;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.client.server.management.container.status.card.ContainerCardPresenter;
import org.kie.workbench.client.server.management.container.status.empty.ContainerStatusEmptyPresenter;

import static org.uberfire.commons.validation.PortablePreconditions.*;

@Dependent
@Templated
public class ContainerRemoteStatusView extends Composite
        implements ContainerRemoteStatusPresenter.View {

    @DataField("container")
    Element container = DOM.createDiv();

    @Inject
    @DataField("card-container")
    Div cardContainer;

    @Override
    public void addCard( final ContainerCardPresenter.View containerCardView ) {
        if (cardContainer.getParent() == null
                || !cardContainer.getParent().getElement().equals( container )){
            container.insertFirst( cardContainer.getElement() );
        }
        cardContainer.add( checkNotNull( "containerCardView", containerCardView ) );
    }

    @Override
    public void setEmpty( final ContainerStatusEmptyPresenter.View widget ) {
        cardContainer.clear();
        container.removeAllChildren();
        container.insertFirst( widget.asWidget().getElement() );
    }

}
