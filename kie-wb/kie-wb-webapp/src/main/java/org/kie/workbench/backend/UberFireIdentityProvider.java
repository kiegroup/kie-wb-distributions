package org.kie.workbench.backend;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;

import org.jbpm.kie.services.api.IdentityProvider;
import org.uberfire.security.Identity;
import org.uberfire.security.Role;

@SessionScoped
public class UberFireIdentityProvider implements IdentityProvider, Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private Identity identity;
    
    @Override
    public String getName() {
        return identity.getName();
    }

    @Override
    public List<String> getRoles() {
        List<String> roles = new ArrayList<String>();
        
        List<Role> ufRoles = identity.getRoles();
        for (Role role : ufRoles) {
            roles.add(role.getName());
        }
        
        return roles;
    }

}
