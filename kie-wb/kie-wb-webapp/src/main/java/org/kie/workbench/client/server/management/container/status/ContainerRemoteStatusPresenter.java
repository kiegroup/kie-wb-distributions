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

import java.util.Collection;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.jboss.errai.ioc.client.container.IOC;
import org.kie.server.controller.api.model.runtime.Container;
import org.kie.server.controller.api.model.spec.ContainerSpecKey;
import org.kie.workbench.client.server.management.container.status.card.ContainerCardPresenter;
import org.kie.workbench.client.server.management.container.status.empty.ContainerStatusEmptyPresenter;

@Dependent
public class ContainerRemoteStatusPresenter {

    public interface View extends IsWidget {

        void addCard( final ContainerCardPresenter.View containerCardView );

        void setEmpty( final ContainerStatusEmptyPresenter.View widget );
    }

    private final View view;

    private ContainerStatusEmptyPresenter emptyPresenter = null;

    @Inject
    public ContainerRemoteStatusPresenter( final View view ) {
        this.view = view;
    }

    @PostConstruct
    public void init() {
    }

    public View getView() {
        return view;
    }

    public void setup( final ContainerSpecKey containerSpecKey,
                       final Collection<Container> containers ) {
        if ( containers == null || containers.isEmpty() ) {
            ContainerStatusEmptyPresenter emptyPresenter = getEmpty();
            emptyPresenter.setup( containerSpecKey );
            this.view.setEmpty( emptyPresenter.getView() );
        } else {
            for ( Container container : containers ) {
                final ContainerCardPresenter cardPresenter = newCard();
                cardPresenter.setup( container.getServiceInstanceKey(), container );
                view.addCard( cardPresenter.getView() );
            }
        }
    }

    ContainerCardPresenter newCard() {
        return IOC.getBeanManager().lookupBean( ContainerCardPresenter.class ).getInstance();
    }

    ContainerStatusEmptyPresenter getEmpty() {
        if ( emptyPresenter == null ) {
            emptyPresenter = IOC.getBeanManager().lookupBean( ContainerStatusEmptyPresenter.class ).getInstance();
        }
        return emptyPresenter;
    }

}
