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

package org.kie.config.cli;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Alternative;

import org.jboss.errai.security.shared.api.Group;
import org.jboss.errai.security.shared.api.Role;
import org.jboss.errai.security.shared.api.identity.User;

/**
 * An alternative Identity used from the CLI to ensure the user has ADMIN permissions
 */
@Alternative
@ApplicationScoped
public class CliIdentity implements User {

    private static final long serialVersionUID = -9178650167557721039L;

    private Set<Role> roles = new HashSet<Role>();

    @PostConstruct
    public void setup() {
        roles.add( EnvironmentProvider.ADMIN_ROLE );
    }

    @Override
    public String getIdentifier() {
        return System.getProperty( "logged.user", System.getProperty( "user.name" ) );
    }

    @Override
    public Set<Role> getRoles() {
        return roles;
    }

    @Override
    public Set<Group> getGroups() {
        return Collections.emptySet();
    }

    @Override
    public Map<String, String> getProperties() {
        return Collections.emptyMap();
    }

    @Override
    public void setProperty( String s,
                             String s2 ) {

    }

    @Override
    public void removeProperty( String name ) {
    }

    @Override
    public String getProperty( String s ) {
        return null;
    }
}
