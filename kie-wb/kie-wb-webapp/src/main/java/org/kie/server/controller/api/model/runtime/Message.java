package org.kie.server.controller.api.model.runtime;

import java.util.Collection;

public interface Message {

    Severity getSeverity();

    Collection<String> getMessages();

}
