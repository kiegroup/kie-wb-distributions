package org.kie.workbench.client.server.management.events;

public class AddNewContainer {

    private final String templateId;

    public AddNewContainer( final String templateId ) {
        this.templateId = templateId;
    }

    public String getTemplateId() {
        return templateId;
    }
}
