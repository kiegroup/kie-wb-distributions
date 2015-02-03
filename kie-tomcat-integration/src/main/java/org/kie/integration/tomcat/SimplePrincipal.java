package org.kie.integration.tomcat;

import java.security.Principal;

public class SimplePrincipal implements Principal {

    private String name;

    public SimplePrincipal() {

    }

    public SimplePrincipal(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
