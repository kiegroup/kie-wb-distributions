package org.kie.workbench.drools.backend.server;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.guvnor.common.services.shared.identity.RequestIdentityProvider;
import org.uberfire.security.Identity;
import org.uberfire.security.Role;

@SessionScoped
public class UberFireIdentityProvider implements RequestIdentityProvider, Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private Identity identity;
    @Inject
    private HttpServletRequest request;
    
    @Override
    public String getName() {
        try {
            return identity.getName();
        } catch (Exception e) {
            if (request != null && request.getUserPrincipal() != null) {
                return request.getUserPrincipal().getName();
            }
            return null;
        }
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
