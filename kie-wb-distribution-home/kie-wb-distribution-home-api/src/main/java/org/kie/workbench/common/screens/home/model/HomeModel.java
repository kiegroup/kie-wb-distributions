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
