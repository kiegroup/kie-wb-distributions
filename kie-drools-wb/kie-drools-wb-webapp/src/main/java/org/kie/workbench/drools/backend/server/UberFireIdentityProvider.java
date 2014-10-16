package org.kie.workbench.drools.backend.server;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.jboss.errai.security.shared.api.Role;
import org.jboss.errai.security.shared.api.identity.User;
import org.jbpm.kie.services.api.IdentityProvider;

@SessionScoped
public class UberFireIdentityProvider implements IdentityProvider, Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private User identity;
    @Inject
    private HttpServletRequest request;

    @Override
    public String getName() {
        try {

            return identity.getIdentifier();
        } catch (Exception e) {
            if (request != null && request.getUserPrincipal() != null) {
                return request.getUserPrincipal().getName();
            }
            return "unknown";
        }
    }

    @Override
    public List<String> getRoles() {
        List<String> roles = new ArrayList<String>();

        final Set<Role> ufRoles = identity.getRoles();
        for (Role role : ufRoles) {
            roles.add(role.getName());
        }

        return roles;
    }

    @Override
    public boolean hasRole(String role) {
        if (request != null) {
            return request.isUserInRole(role);
        }
        return false;
    }

}
