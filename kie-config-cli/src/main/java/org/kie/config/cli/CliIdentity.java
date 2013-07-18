package org.kie.config.cli;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Alternative;

import org.uberfire.security.Identity;
import org.uberfire.security.Role;

/**
 * An alternative Identity used from the CLI to ensure the user has ADMIN permissions
 */
@Alternative
@ApplicationScoped
public class CliIdentity implements Identity {

    private static final long serialVersionUID = -9178650167557721039L;

    private List<Role> roles = new ArrayList<Role>();

    @PostConstruct
    public void setup() {
        roles.add( EnvironmentProvider.ADMIN_ROLE );
    }

    //@Override
    public String getName() {
        return System.getProperty( "user.name" );
    }

    @Override
    public boolean hasRole( Role role ) {
        return roles.contains( role );
    }

    @Override
    public List<Role> getRoles() {
        return roles;
    }

    @Override
    public Map<String, String> getProperties() {
        return Collections.emptyMap();
    }

    @Override
    public void aggregateProperty(String name,
                                  String value) {
    }

    @Override
    public void removeProperty(String name) {
    }

    @Override
    public String getProperty(String name,
                              String defaultValue) {
        return null;
    }

}
