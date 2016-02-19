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

package org.kie.workbench.client.server.management.remote;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.kie.workbench.client.server.management.events.ServerInstanceSelected;

@ApplicationScoped
public class RemotePresenter {

    public interface View extends IsWidget {

    }

    private final View view;

    @Inject
    public RemotePresenter( final View view ) {
        this.view = view;
    }

    @PostConstruct
    public void init() {
    }

    public View getView() {
        return view;
    }

    public void onSelect( @Observes final ServerInstanceSelected serverInstanceSelected ) {

    }
}
