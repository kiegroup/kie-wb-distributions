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

package org.kie.workbench.common.screens.home.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.uberfire.commons.validation.PortablePreconditions;

/**
 * Model defining the Home Screen content
 */
public class HomeModel {

    private final String title;
    private final String subtitle;
    private final List<Section> sections = new ArrayList<Section>();


    public HomeModel(final String title, final String subtitle) {
        this.title = PortablePreconditions.checkNotNull("title", title);
        this.subtitle = PortablePreconditions.checkNotNull("subtitle", subtitle);
    }

    public String getTitle() {
        return title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void addSection(final Section section) {
        sections.add(PortablePreconditions.checkNotNull("section",
                section));
    }


    public List<Section> getSections() {
        return Collections.unmodifiableList(sections);
    }

}
